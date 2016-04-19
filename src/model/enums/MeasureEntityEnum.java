package model.enums;

/**
 * Defines the allowable types of measure entities.
 */
public enum MeasureEntityEnum {
    NOTE,               // A single note.
    REST,               // A single rest.
    CHORD,              // A set of notes played together.
    TUPLET,             // A set of notes and/or chords played evenly over a specified number of beats.
    BAR,                // A divider between measures.
    ALTERNATE_ENDING    // Specifies whether a measure is the start of an alternate ending.
}
