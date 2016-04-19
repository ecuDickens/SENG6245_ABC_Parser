package model.entities;

/**
 * A representation of a period of silence.
 */
public class Rest extends MeasureEntity {

    public Rest withDuration(final Double duration) {
        setDurationMultiplier(duration);
        return this;
    }
}
