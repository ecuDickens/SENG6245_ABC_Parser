package model.entities;

/**
 * A representation of the concept of alternate endings (different music to play based on how many times you've looped through the main section).
 */
public class AlternateEnding extends MeasureEntity {

    // Denotes how many times through a repeat to go before this measure gets played.
    private Integer endingIndex;

    public Integer getEndingIndex() {
        return endingIndex;
    }
    public void setEndingIndex(Integer endingIndex) {
        this.endingIndex = endingIndex;
    }

    public AlternateEnding withEndingIndex(final Integer endingIndex) {
        setEndingIndex(endingIndex);
        return this;
    }
}
