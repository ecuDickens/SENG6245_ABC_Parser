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
import model.enums.NoteEnum;

import javax.sound.midi.InvalidMidiDataException;
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
    private static int TICKS_PER_QUARTER_NOTE = 8;

	@Override
	public void loadSong(final Song song) throws MidiUnavailableException, InvalidMidiDataException {
        // Always set the sequence to 8 ticks per quarter note, allowing for 32nd note granularity.
        final Sequence sequence = new Sequence(PPQ, TICKS_PER_QUARTER_NOTE);
        track = sequence.createTrack();

        sequencer = MidiSystem.getSequencer();
        sequencer.setSequence(sequence);

        // Iterate through each voice, adding notes to the track.
        for (Voice voice : song.getVoices().values()) {
            if (voice.getMeasures().isEmpty()) {
                continue;
            }

            int tick = 0;
            Measure currentMeasure = voice.getMeasures().get(0);
            MeasureKey key = currentMeasure.getKey();
            while(true) {
                if (null != currentMeasure.getTempo()) {
                    sequencer.setTempoInBPM(currentMeasure.getTempo());
                }
                final Map<NoteEnum, Accidental> overrideTracker = new HashMap<>();

                for (MeasureEntity entity : currentMeasure.getEntities()) {
                    tick += handleEntity(entity, overrideTracker, key, currentMeasure.getLastNoteDuration(), tick);
                }
                if (null == currentMeasure.getNextMeasure()) {
                    break;
                }
                currentMeasure = currentMeasure.getNextMeasure();
            }
        }
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
                handleEntity(note, overrideTracker, key, defaultNoteDuration, tick);
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
                handleEntity(tupletEntity, overrideTracker, key, defaultNoteDuration, tupletTick);
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
        final Double actualDuration = defaultNoteDuration * noteMultiplier;
        final Double durationTick = actualDuration * TICKS_PER_QUARTER_NOTE * 4;
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

	@Override
	public void play() throws MidiUnavailableException {
        sequencer.open();
        sequencer.start();
        while (sequencer.isRunning()) {
            Thread.yield();
        }
        sequencer.close();
	}
}