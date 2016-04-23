package model.entities;

import java.util.List;

/**
 * A representation of a set of notes to play at the same time.
 */
public class Chord extends MeasureEntity {

    // The set of notes to play at the same time.
    private List<Note> notes;

    public List<Note> getNotes() {
        return notes;
    }
    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public Chord withDurationMultiplier(final Double duration) {
        setDurationMultiplier(duration);
        return this;
    }
    public Chord withNotes(final List<Note> notes) {
        setNotes(notes);
        return this;
    }
}
