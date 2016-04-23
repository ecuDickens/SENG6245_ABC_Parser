package player;

import model.Measure;
import model.MeasureKey;
import model.Song;
import model.Voice;
import model.entities.Chord;
import model.entities.MeasureEntity;
import model.entities.Note;
import model.entities.Rest;
import model.entities.Tuplet;
import model.enums.Accidental;
import model.enums.BarLineEnum;
import model.enums.NoteEnum;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.HashMap;
import java.util.Map;

import static javax.sound.midi.Sequence.PPQ;
import static player.MidiHelper.getPitch;

public class MidiPlayer implements Player {

    private Sequencer sequencer;
    private Track track;

    // Any note durations need to be multiplied by this to get the actual tick duration.
    private static int TICKS_PER_QUARTER_NOTE = 16;

	@Override
	public void play(final Song song) throws MidiUnavailableException, InvalidMidiDataException {
        System.out.println("Loading " + song.getTitle());

        // Always set the sequence to 8 ticks per quarter note, allowing for 32nd note granularity.
        final Sequence sequence = new Sequence(PPQ, TICKS_PER_QUARTER_NOTE);
        track = sequence.createTrack();

        sequencer = MidiSystem.getSequencer();
        sequencer.setSequence(sequence);
        sequencer.open();

        // Iterate through each voice, adding notes to the track.
        boolean setTempo = true; // only need to set the tempo from the first voice.  All other voices should have the same tempo.
        for (Map.Entry<String, Voice> entry : song.getVoices().entrySet()) {
            System.out.println("Adding voice " + entry.getKey() + " to track");
            if (entry.getValue().getMeasures().isEmpty()) {
                continue;
            }

            int tick = 0;
            int endingIndex = 1; // as in, the first time through.  Will match with the alternate ending count when reached.
            Measure currentMeasure = entry.getValue().getMeasures().get(0);
            MeasureKey key = currentMeasure.getKey();
            while(true) {
                if (setTempo && null != currentMeasure.getTempo()) {
                    System.out.println("Setting tempo to " + currentMeasure.getTempo());
                    // sequencer.setTempoInBPM doesn't seem to work for multiple tempo changes.  Have to add an event at the appropriate time tick.
                    // Also, if the default length is not a quarter note, need to scale the tempo to match.
                    addTempoEvent(currentMeasure.getTempo(), currentMeasure.getLastNoteDuration(), tick);
                }
                final Map<NoteEnum, Accidental> overrideTracker = new HashMap<>();

                for (MeasureEntity entity : currentMeasure.getEntities()) {
                    tick += handleEntity(entity, overrideTracker, key, currentMeasure.getLastNoteDuration(), tick);
                }
                if (null == currentMeasure.getNextMeasure()) {
                    break;
                }

                // If we've reached a repeat, go back to the section start and increment the counter.
                if (currentMeasure.getEndLine() == BarLineEnum.REPEAT_END) {
                    if (endingIndex == 1) {
                        currentMeasure = currentMeasure.getSectionStart();
                        System.out.println("Repeating back to measure " + currentMeasure.getIndex());
                        endingIndex++;
                    } else {
                        currentMeasure = currentMeasure.getNextMeasure();
                        endingIndex--;
                    }
                }
                // If we've reached an alternate ending, go to the appropriately numbered one.
                else if (null != currentMeasure.getNextMeasure().getAlternateEnding()) {
                    currentMeasure = currentMeasure.getNextAlternateEnding(endingIndex);
                    System.out.println("Going to alternate ending " + endingIndex + " at measure " + currentMeasure.getIndex());
                } else {
                    currentMeasure = currentMeasure.getNextMeasure();
                }

                if (currentMeasure.getEndLine() == BarLineEnum.SECTION_END) {
                    endingIndex = 1;
                }
            }
            setTempo = false;
        }

        System.out.println("Playing " + song.getTitle());
        sequencer.start();
        while (sequencer.isRunning()) {
            Thread.yield();
        }
        sequencer.close();
        System.out.println("Done");
	}

    private Integer handleEntity(final MeasureEntity entity, final Map<NoteEnum, Accidental> overrideTracker, final MeasureKey key, final Double defaultNoteDuration, final Integer tick) {
        // Rests simply move the song along.
        if (entity instanceof Rest) {
            final Rest rest = (Rest) entity;
            return getDurationTick(rest.getDurationMultiplier(), defaultNoteDuration);
        }
        // Notes add midi events for their duration.
        if (entity instanceof Note) {
            final Note note = (Note) entity;
            final Integer durationTick = getDurationTick(note.getDurationMultiplier(), defaultNoteDuration);
            addNote(getPitch(note, getAccidental(note, overrideTracker, key)), tick, durationTick);
            return durationTick;
        }
        // Chords add note events for each note played at the same tick.
        if (entity instanceof Chord) {
            final Chord chord = (Chord) entity;
            final int duration = getDurationTick(chord.getDurationMultiplier(), defaultNoteDuration);
            for (Note note : chord.getNotes()) {
                // Ensure the notes are all the same duration.
                handleEntity(note.withDurationMultiplier(chord.getDurationMultiplier()), overrideTracker, key, defaultNoteDuration, tick);
            }
            return duration;
        }
        // Tuplets play out their notes evenly over the course of the duration.
        if (entity instanceof Tuplet) {
            final Tuplet tuplet = (Tuplet) entity;
            final int duration = getDurationTick(tuplet.getDurationMultiplier(), defaultNoteDuration);
            final int increment = duration / tuplet.getEntities().size();
            int tupletTick = tick;
            for (MeasureEntity tupletEntity : tuplet.getEntities()) {
                // Ensure each entity doesn't overlap in duration.
                handleEntity(tupletEntity.withDurationMultiplier(tuplet.getDurationMultiplier()/tuplet.getEntities().size()), overrideTracker, key, defaultNoteDuration, tupletTick);
                tupletTick += increment;
            }
            return duration;
        }
        // All other entity types are ignored.
        return 0;
    }

    private Accidental getAccidental(final Note note, final Map<NoteEnum, Accidental> overrideTracker, final MeasureKey key) {
        if (null != note.getAccidentalOverride()) {
            overrideTracker.put(note.getNoteEnum(), note.getAccidentalOverride());
            return note.getAccidentalOverride();
        }
        return overrideTracker.containsKey(note.getNoteEnum()) ? overrideTracker.get(note.getNoteEnum()) : key.getAccidental(note);
    }

    private int getDurationTick(final Double noteMultiplier, final Double defaultNoteDuration) {
        final Double durationTick = defaultNoteDuration * noteMultiplier * TICKS_PER_QUARTER_NOTE * 8; // ex. a quarter note (0.25) * 8 * 8 = 16 ticks.
        return durationTick.intValue();
    }

    private void addNote(int pitch, int startTick, int numTicks) {
        try {
            addMidiEvent(ShortMessage.NOTE_ON, pitch, startTick);
            addMidiEvent(ShortMessage.NOTE_OFF, pitch, startTick + numTicks);
        } catch (InvalidMidiDataException e) {
            String msg = String.format("Cannot add note with the pitch %s at tick %s for duration of %s", pitch, startTick, numTicks);
            throw new RuntimeException(msg, e);
        }
    }

    private void addMidiEvent(final int eventType, final int pitch, final int tick) throws InvalidMidiDataException {
        final ShortMessage msg = new ShortMessage();
        msg.setMessage(eventType, 0, pitch, 100);
        final MidiEvent event = new MidiEvent(msg, tick);
        track.add(event);
    }

    private void addTempoEvent(final int tempo, final double defaultDuration, final int tick) throws InvalidMidiDataException {
        // scale by the note duration divided by the default duration (since tempo is given in eighth note beats per minute).
        final double scaledTempo = (double) tempo * (defaultDuration / 0.125);

        // from http://www.programcreek.com/java-api-examples/index.php?api=javax.sound.midi.MetaMessage
        final int mpq = 60000000 / (int) scaledTempo;
        final MetaMessage tempoMsg = new MetaMessage();
        tempoMsg.setMessage(0x51,new byte[] {
                (byte)(mpq>>16 & 0xff),
                (byte)(mpq>>8 & 0xff),
                (byte)(mpq & 0xff)
        },3);
        track.add(new MidiEvent(tempoMsg, tick));
    }
}