package player;

import model.entities.Note;
import model.enums.Accidental;
import model.enums.NoteEnum;

import java.util.HashMap;
import java.util.Map;

import static model.enums.NoteEnum.A;
import static model.enums.NoteEnum.B;
import static model.enums.NoteEnum.C;
import static model.enums.NoteEnum.D;
import static model.enums.NoteEnum.E;
import static model.enums.NoteEnum.F;
import static model.enums.NoteEnum.G;

/**
 * Provides methods and mappings to convert a song data structure into a list of midi events.
 */
public class MidiHelper {

    public static final int OCTAVE = 12;

    public static final Map<NoteEnum, Integer> noteToPitch;
    static {
        noteToPitch = new HashMap<>();
        noteToPitch.put(C, 0);
        noteToPitch.put(D, 2);
        noteToPitch.put(E, 4);
        noteToPitch.put(F, 5);
        noteToPitch.put(G, 7);
        noteToPitch.put(A, 9);
        noteToPitch.put(B, 11);
    }
    public static final Map<Accidental, Integer> accidentalToPitch;
    static {
        accidentalToPitch = new HashMap<>();
        accidentalToPitch.put(Accidental.DOUBLE_FLAT, -2);
        accidentalToPitch.put(Accidental.FLAT, -1);
        accidentalToPitch.put(Accidental.NATURAL, 0);
        accidentalToPitch.put(Accidental.SHARP, 1);
        accidentalToPitch.put(Accidental.DOUBLE_SHARP, 2);
    }

    public static int getPitch(final Note note, final Accidental accidental) {
        final int accidentalPitch = null != accidental ? accidentalToPitch.get(accidental) : 0;
        return noteToPitch.get(note.getNoteEnum()) + accidentalPitch + OCTAVE * note.getOctave() + 60;
    }
}
