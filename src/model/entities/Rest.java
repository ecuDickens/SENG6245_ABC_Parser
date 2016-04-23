package model.entities;

/**
 * A representation of a period of silence.
 */
public class Rest extends MeasureEntity {

    @Override
    public Rest withDurationMultiplier(final Double durationMultiplier) {
        setDurationMultiplier(durationMultiplier);
        return this;
    }
}
