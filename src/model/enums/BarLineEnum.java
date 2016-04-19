package model.enums;

/**
 * Defines allowable types of bar lines.
 */
public enum BarLineEnum {
    START,          // The start of the voice track.
    STANDARD,       // The standard divider between measures.
    REPEAT_START,   // The start of a repeat section.
    REPEAT_END,     // The end of a repeat section.
    SECTION_END     // The end of a major section of the song.
}
