package model.entities;

import model.enums.BarLineEnum;

/**
 * A representation of a bar line that separates measures.
 */
public class BarLine extends MeasureEntity {

    // The bar line type.
    private BarLineEnum type;

    public BarLineEnum getType() {
        return type;
    }
    public void setType(BarLineEnum type) {
        this.type = type;
    }

    public BarLine withType(final BarLineEnum type) {
        setType(type);
        return this;
    }
}
