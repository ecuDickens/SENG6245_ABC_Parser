package parser;

import model.Meter;
import model.enums.Key;
import model.enums.NoteEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static model.enums.Key.*;
import static model.enums.NoteEnum.A;
import static model.enums.NoteEnum.B;
import static model.enums.NoteEnum.C;
import static model.enums.NoteEnum.D;
import static model.enums.NoteEnum.E;
import static model.enums.NoteEnum.F;
import static model.enums.NoteEnum.G;

/**
 * Contains helper methods to assist in converting abc notation into entities.
 */
public class AbcHelper {

    // The important characters that comprise the body of an abc file.
    public static final Set<Character> NOTE_CHARS = Stream.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'A', 'B', 'C', 'D', 'E', 'F', 'G').collect(toSet());
    public static final Set<Character> NUM_CHARS = Stream.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0').collect(toSet());
    public static final Character SLASH_CHAR = '/';
    public static final Character SHARP_CHAR = '^';
    public static final Character FLAT_CHAR = '_';
    public static final Character NATURAL_CHAR = '=';
    public static final Set<Character> ACCIDENTAL_CHARS = Stream.of(SHARP_CHAR, FLAT_CHAR, NATURAL_CHAR).collect(toSet());
    public static final Character REST_CHAR = 'z';
    public static final Character OCTAVE_DOWN_CHAR = ',';
    public static final Character OCTAVE_UP_CHAR = '\'';
    public static final Set<Character> OCTAVE_CHARS = Stream.of(OCTAVE_DOWN_CHAR, OCTAVE_UP_CHAR).collect(toSet());
    public static final Character COMMENT_CHAR = '%';
    public static final Character CHORD_OR_ALT_START_CHAR = '[';
    public static final Character CHORD_END_CHAR = ']';
    public static final Character TUPLET_START_CHAR = '(';
    public static final Character REPEAT_CHAR = ':';
    public static final Character BAR_CHAR = '|';
    public static final Character BROKEN_RHYTHM_FORWARD = '<';
    public static final Character BROKEN_RHYTHM_BACK = '>';

    // Characters that denote the start of a new entity.
    public static final Set<Character> NEW_ENTITY_CHARS = Stream.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'A', 'B', 'C', 'D', 'E', 'F', 'G', SHARP_CHAR, FLAT_CHAR, NATURAL_CHAR, CHORD_OR_ALT_START_CHAR, REST_CHAR, TUPLET_START_CHAR, BAR_CHAR, REPEAT_CHAR, BROKEN_RHYTHM_FORWARD, BROKEN_RHYTHM_BACK).collect(toSet());

    public static final Double STANDARD_NOTE_DURATION = 0.125; // Eighth note
    public static final Integer STANDARD_TEMPO = 100;
    public static final Meter STANDARD_METER = new Meter().withBeatsPerMeasure(4).withDuration(4);
    public static final String STANDARD_VOICE = "Default";
    public static final String STANDARD_COMPOSER = "Unknown";

    // The map of key strings to key.
    public static Map<String, Key> stringToKey;
    static {
        stringToKey = new HashMap<>();
        stringToKey.put("C#", C_SHARP_MAJOR);
        stringToKey.put("F#", F_SHARP_MAJOR);
        stringToKey.put("B", B_MAJOR);
        stringToKey.put("E", E_MAJOR);
        stringToKey.put("A", A_MAJOR);
        stringToKey.put("D", D_MAJOR);
        stringToKey.put("G", G_MAJOR);
        stringToKey.put("C", C_MAJOR);
        stringToKey.put("F", F_MAJOR);
        stringToKey.put("Bb", B_FLAT_MAJOR);
        stringToKey.put("Eb", E_FLAT_MAJOR);
        stringToKey.put("Ab", A_FLAT_MAJOR);
        stringToKey.put("Db", D_FLAT_MAJOR);
        stringToKey.put("Gb", G_FLAT_MAJOR);
        stringToKey.put("Cb", C_FLAT_MAJOR);

        stringToKey.put("A#m", A_SHARP_MINOR);
        stringToKey.put("D#m", D_SHARP_MINOR);
        stringToKey.put("G#m", G_SHARP_MINOR);
        stringToKey.put("C#m", C_SHARP_MINOR);
        stringToKey.put("F#m", F_SHARP_MINOR);
        stringToKey.put("Bm", B_MINOR);
        stringToKey.put("Em", E_MINOR);
        stringToKey.put("Am", A_MINOR);
        stringToKey.put("Dm", D_MINOR);
        stringToKey.put("Gm", G_MINOR);
        stringToKey.put("Cm", C_MINOR);
        stringToKey.put("Fm", F_MINOR);
        stringToKey.put("Bbm", B_FLAT_MINOR);
        stringToKey.put("Ebm", E_FLAT_MINOR);
        stringToKey.put("Abm", A_FLAT_MINOR);
    }

    // The map of note characters to note enum.
    public static Map<Character, NoteEnum> characterToNote;
    static {
        characterToNote = new HashMap<>();
        characterToNote.put('A', A);
        characterToNote.put('B', B);
        characterToNote.put('C', C);
        characterToNote.put('D', D);
        characterToNote.put('E', E);
        characterToNote.put('F', F);
        characterToNote.put('G', G);
        characterToNote.put('a', A);
        characterToNote.put('b', B);
        characterToNote.put('c', C);
        characterToNote.put('d', D);
        characterToNote.put('e', E);
        characterToNote.put('f', F);
        characterToNote.put('g', G);
    }

    // Returns the number of complete chord and note characters in the given list.  Notes inside of chords are not counted.
    public static int countNotesAndChords(final List<Character> entityChars) {
        int count = 0;
        boolean withinChord = false;
        for (Character c : entityChars) {
            if (CHORD_OR_ALT_START_CHAR.equals(c)) {
                withinChord = true;
            } else if (CHORD_END_CHAR.equals(c)) {
                count++;
                withinChord = false;
            } else if (characterToNote.containsKey(c) && !withinChord) {
                count++;
            }
        }
        return count;
    }
}
