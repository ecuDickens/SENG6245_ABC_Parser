package model.entities;

/**
 * The base class that all bar entities extend from.
 */
public class MeasureEntity {

    // The factor to multiply the default note duration for the measure
    // ex. If the default note duration is an eighth note (0.125) and the multiplier is 2 then the actual note duration is a quarter note (0.25).
    private Double durationMultiplier = 1.0;

    public Double getDurationMultiplier() {
        return durationMultiplier;
    }
    public void setDurationMultiplier(Double durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }
}
