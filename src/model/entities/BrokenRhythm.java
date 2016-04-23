package model.entities;

/**
 * A representation of broken rhythm between two notes.
 * ex. A < between two quarter notes results in the first note duration multiplied by 1.5 and the second by .25.
 *     So the first note becomes a dotted quarter note, the second and eighth note.
 *     A << results in multipliers of 1.75 and 0.25.
 */
public class BrokenRhythm extends MeasureEntity {

    // The multiplier to apply to the first note/chord.
    private Double firstNoteMultiplier;

    // The multiplier to apply to the second note/chord.
    private Double secondNoteMultiplier;

    public Double getFirstNoteMultiplier() {
        return firstNoteMultiplier;
    }
    public void setFirstNoteMultiplier(Double firstNoteMultiplier) {
        this.firstNoteMultiplier = firstNoteMultiplier;
    }

    public Double getSecondNoteMultiplier() {
        return secondNoteMultiplier;
    }
    public void setSecondNoteMultiplier(Double secondNoteMultiplier) {
        this.secondNoteMultiplier = secondNoteMultiplier;
    }

    public BrokenRhythm withFirstNoteMultiplier(final Double firstNoteMultiplier) {
        setFirstNoteMultiplier(firstNoteMultiplier);
        return this;
    }
    public BrokenRhythm withSecondNoteMultiplier(final Double secondNoteMultiplier) {
        setSecondNoteMultiplier(secondNoteMultiplier);
        return this;
    }
}
