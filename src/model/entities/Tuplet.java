package model.entities;

import java.util.List;

/**
 * A representation of a set of entities to play spread out over a specifed duration.
 */
public class Tuplet extends MeasureEntity {

    // The set of entities to play for the tuplet (can be entities or chords), spread evenly over the specified duration.
    private List<MeasureEntity> entities;

    public List<MeasureEntity> getEntities() {
        return entities;
    }
    public void setEntities(List<MeasureEntity> entities) {
        this.entities = entities;
    }

    @Override
    public Tuplet withDurationMultiplier(final Double duration) {
        setDurationMultiplier(duration);
        return this;
    }
    public Tuplet withEntities(final List<MeasureEntity> notes) {
        setEntities(notes);
        return this;
    }
}
