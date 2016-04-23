package test;

import model.Measure;
import model.Meter;
import model.Song;
import model.Voice;
import model.entities.Chord;
import model.entities.MeasureEntity;
import model.entities.Note;
import model.enums.Accidental;
import model.enums.Key;
import model.enums.NoteEnum;
import org.junit.Test;
import parser.AbcHelper;
import parser.AbcParser;
import player.MidiPlayer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Various Junit tests asserting that functionality works correctly.
 */
public class AbcParserTest {

    @Test
    public void testOctaves() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/sample1.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getTitle(), "sample 1");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 1);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 1);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.C_MAJOR);
        assertEquals(measure.getTempo().intValue(), AbcHelper.STANDARD_TEMPO.intValue());
        assertEquals(measure.getNoteDuration(), AbcHelper.STANDARD_NOTE_DURATION);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 4);

        MeasureEntity entity = measure.getEntities().get(0);
        assertTrue(entity instanceof Note);
        assertEquals(((Note) entity).getNoteEnum(), NoteEnum.C);
        assertEquals(((Note) entity).getOctave(), -1);
        assertEquals(entity.getDurationMultiplier(), 2.0);

        entity = measure.getEntities().get(1);
        assertTrue(entity instanceof Note);
        assertEquals(((Note) entity).getNoteEnum(), NoteEnum.C);
        assertEquals(((Note) entity).getOctave(), 0);
        assertEquals(entity.getDurationMultiplier(), 2.0);

        entity = measure.getEntities().get(2);
        assertTrue(entity instanceof Note);
        assertEquals(((Note) entity).getNoteEnum(), NoteEnum.C);
        assertEquals(((Note) entity).getOctave(), 2);
        assertEquals(entity.getDurationMultiplier(), 1.0);

        entity = measure.getEntities().get(3);
        assertTrue(entity instanceof Note);
        assertEquals(((Note) entity).getNoteEnum(), NoteEnum.C);
        assertEquals(((Note) entity).getOctave(), 3);
        assertEquals(entity.getDurationMultiplier(), 1.0);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testChord() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/sample2.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 8);
        assertEquals(song.getTitle(), "Chord");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 1);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 1);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.C_MAJOR);
        assertEquals(measure.getTempo().intValue(), AbcHelper.STANDARD_TEMPO.intValue());
        assertEquals(measure.getNoteDuration(), AbcHelper.STANDARD_NOTE_DURATION);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 1);

        MeasureEntity entity = measure.getEntities().get(0);
        assertTrue(entity instanceof Chord);
        assertEquals(entity.getDurationMultiplier(), 1.0);
        assertTrue(null != ((Chord) entity).getNotes() && !((Chord) entity).getNotes().isEmpty() && ((Chord) entity).getNotes().size() == 2);

        Note child = ((Chord) entity).getNotes().get(0);
        assertEquals(child.getNoteEnum(), NoteEnum.E);
        assertEquals(child.getOctave(), 0);
        assertEquals(child.getDurationMultiplier(), 1.0);

        child = ((Chord) entity).getNotes().get(1);
        assertEquals(child.getNoteEnum(), NoteEnum.C);
        assertEquals(child.getOctave(), 0);
        assertEquals(child.getDurationMultiplier(), 1.0);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testVoices() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/sample3.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getTitle(), "voices");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 3);

        // Assert the song
        final List<Voice> voices = song.getVoices().values().stream().collect(toList());

        // Voice 1
        Voice voice = voices.get(0);
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 1);

        Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.C_MINOR);
        assertEquals(measure.getTempo().intValue(), AbcHelper.STANDARD_TEMPO.intValue());
        assertEquals(measure.getNoteDuration(), AbcHelper.STANDARD_NOTE_DURATION);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 1);

        MeasureEntity entity = measure.getEntities().get(0);
        assertTrue(entity instanceof Note);
        assertEquals(((Note) entity).getNoteEnum(), NoteEnum.C);
        assertEquals(((Note) entity).getOctave(), 0);
        assertEquals(entity.getDurationMultiplier(), 1.0);

        // Voice 2
        voice = voices.get(1);
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 1);
        measure = voice.getMeasures().get(0);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 1);
        entity = measure.getEntities().get(0);
        assertTrue(entity instanceof Note);
        assertEquals(((Note) entity).getNoteEnum(), NoteEnum.E);
        assertEquals(measure.getKey().getAccidental((Note) entity), Accidental.FLAT);
        assertEquals(((Note) entity).getOctave(), 0);
        assertEquals(entity.getDurationMultiplier(), 1.0);

        // Voice 3
        voice = voices.get(2);
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 1);
        measure = voice.getMeasures().get(0);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 1);
        entity = measure.getEntities().get(0);
        assertTrue(entity instanceof Note);
        assertEquals(((Note) entity).getNoteEnum(), NoteEnum.G);
        assertEquals(((Note) entity).getOctave(), 0);
        assertEquals(entity.getDurationMultiplier(), 1.0);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testDuplets() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/sample4.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getTitle(), "Triplets");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 1);

        // Assert the song
        final List<Voice> voices = song.getVoices().values().stream().collect(toList());
        Voice voice = voices.get(0);
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 6);

        Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.C_MAJOR);
        assertEquals(measure.getTempo().intValue(), 120);
        assertEquals(measure.getNoteDuration(), 0.25);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 4);

        measure = voice.getMeasures().get(1);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 4);

        measure = voice.getMeasures().get(2);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 8);

        measure = voice.getMeasures().get(3);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 2);

        measure = voice.getMeasures().get(4);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 2);

        measure = voice.getMeasures().get(5);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 2);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testScale() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/scale.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getComposer(), "Unknown");
        assertEquals(song.getTitle(), "Simple scale");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 1);

        // Assert the song
        Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 3);
        Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.C_MAJOR);
        assertEquals(measure.getTempo().intValue(), 120);
        assertEquals(measure.getNoteDuration(), 0.25);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 4);
        measure = voice.getMeasures().get(1);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 4);
        measure = voice.getMeasures().get(2);
        assertTrue(null != measure.getEntities() && !measure.getEntities().isEmpty() && measure.getEntities().size() == 8);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testPrelude() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/prelude.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 8628);
        assertEquals(song.getComposer(), "Johann Sebastian Bach");
        assertEquals(song.getTitle(), "Prelude BWV 846 no. 1");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 3);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 35);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.C_MAJOR);
        assertEquals(measure.getTempo().intValue(), 280);
        assertEquals(measure.getNoteDuration(), 0.0625);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testPaddy() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/paddy.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getComposer(), "Trad.");
        assertEquals(song.getTitle(), "Paddy O'Rafferty");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 1);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 26);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), new Meter().withBeatsPerMeasure(6).withDuration(8));
        assertEquals(measure.getKey().getKey(), Key.D_MAJOR);
        assertEquals(measure.getTempo().intValue(), 200);
        assertEquals(measure.getNoteDuration(), AbcHelper.STANDARD_NOTE_DURATION);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testNightMusic() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/little_night_music.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getComposer(), "Wolfgang Amadeus Mozart");
        assertEquals(song.getTitle(), "Little Night Music Mvt. 1");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 1);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 55);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.G_MAJOR);
        assertEquals(measure.getTempo().intValue(), 280);
        assertEquals(measure.getNoteDuration(), AbcHelper.STANDARD_NOTE_DURATION);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testInvention() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/invention.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1868);
        assertEquals(song.getComposer(), "Johann Sebastian Bach");
        assertEquals(song.getTitle(), "Invention no. 1");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 2);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 22);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), AbcHelper.STANDARD_METER);
        assertEquals(measure.getKey().getKey(), Key.C_MAJOR);
        assertEquals(measure.getTempo().intValue(), 140);
        assertEquals(measure.getNoteDuration(), AbcHelper.STANDARD_NOTE_DURATION);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testFurElise() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/fur_elise.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getComposer(), "Ludwig van Beethoven");
        assertEquals(song.getTitle(), "Bagatelle No.25 in A, WoO.59");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 2);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 106);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), new Meter().withBeatsPerMeasure(3).withDuration(8));
        assertEquals(measure.getKey().getKey(), Key.A_MINOR);
        assertEquals(measure.getTempo().intValue(), 400);
        assertEquals(measure.getNoteDuration(), 0.0625);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }

    @Test
    public void testDebussy() throws IOException, InvalidMidiDataException, MidiUnavailableException {
        final AbcParser parser = new AbcParser();
        final Song song = parser.parse("src/test/resource/debussy.abc");
        assertTrue(null != song);

        // Assert Header
        assertEquals(song.getIndex().intValue(), 1);
        assertEquals(song.getComposer(), "Claude Debussy");
        assertEquals(song.getTitle(), "Valse Romantique");
        assertTrue(null != song.getVoices() && !song.getVoices().isEmpty() && song.getVoices().size() == 3);

        // Assert the song
        final Voice voice = song.getVoices().values().stream().findFirst().get();
        assertTrue(null != voice.getMeasures() && !voice.getMeasures().isEmpty() && voice.getMeasures().size() == 151);

        final Measure measure = voice.getMeasures().get(0);
        assertEquals(measure.getMeter(), new Meter().withBeatsPerMeasure(3).withDuration(4));
        assertEquals(measure.getKey().getKey(), Key.F_MINOR);
        assertEquals(measure.getTempo().intValue(), 120);
        assertEquals(measure.getNoteDuration(), 0.25);

        final MidiPlayer player = new MidiPlayer();
        player.play(song);
    }
}
