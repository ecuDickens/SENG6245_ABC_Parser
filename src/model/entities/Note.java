package model.entities;

import model.enums.Accidental;
import model.enums.NoteEnum;

/**
 * A representation of a single note in the song.
 */
public class Note extends MeasureEntity {

    // The raw note value.
    private NoteEnum noteEnum;

    // The octave of the note.
    private int octave = 0;

    // The accidental override associated with the note.  Any further similar notes in the measure will have the same accidental applied.
    private Accidental accidentalOverride = null;

    public NoteEnum getNoteEnum() {
        return noteEnum;
    }
    public void setNoteEnum(NoteEnum noteEnum) {
        this.noteEnum = noteEnum;
    }

    public int getOctave() {
        return octave;
    }
    public void setOctave(int octave) {
        this.octave = octave;
    }

    public Accidental getAccidentalOverride() {
        return accidentalOverride;
    }
    public void setAccidentalOverride(Accidental accidentalOverride) {
        this.accidentalOverride = accidentalOverride;
    }

    public Note withNoteEnum(final NoteEnum noteEnum) {
        setNoteEnum(noteEnum);
        return this;
    }
    public Note withOctave(final int octave) {
        setOctave(octave);
        return this;
    }
    public Note withDuration(final Double duration) {
        setDurationMultiplier(duration);
        return this;
    }
    public Note withAccidental(final Accidental accidental) {
        setAccidentalOverride(accidental);
        return this;
    }
}
