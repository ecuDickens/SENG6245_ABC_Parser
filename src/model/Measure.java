package model;

import model.entities.AlternateEnding;
import model.entities.MeasureEntity;
import model.enums.BarLineEnum;

import java.util.List;

/**
 * A representation of a single measure in the song.  A measure should know everything it needs to:
 *  - 1. Play all the notes at the appropriate pitch and tempo.
 *  - 2. Transition to the next valid measure.
 */
public class Measure {

    // The voice name that this measure belongs to.
    private String voiceName;

    // The default note duration.  Used when determining the actual duration of parsed notes.
    // A value of 1 is defined as a quarter note. Default is an eighth note, or 0.0125.
    private Double noteDuration;

    // Defines what the sum of all note durations in a bar should be. Default is 4/4.
    private Meter meter;

    // Defines the number of default length notes per minute. Default is 100.
    private Integer tempo;

    // Defines which notes are should be flat or sharp.
    private MeasureKey key;

	// Describes the bar line before the measure.
	private BarLineEnum startLine;

    // Describes the bar line after the measure.
    private BarLineEnum endLine;

    // Denotes whether this measure is the start of an alternate ending.
    private AlternateEnding alternateEnding;

    // A link to the measure directly preceding this one.
    private Measure previousMeasure;

    // A link to the measure directly after this one.
    private Measure nextMeasure;

	// The list of notes, rests, tuplets, and chords contained in the measure.
	private List<MeasureEntity> entities;

    public String getVoiceName() {
        return voiceName;
    }
    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public Double getNoteDuration() {
        return noteDuration;
    }
    public void setNoteDuration(Double noteDuration) {
        this.noteDuration = noteDuration;
    }

    public Meter getMeter() {
        return meter;
    }
    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public Integer getTempo() {
        return tempo;
    }
    public void setTempo(Integer tempo) {
        this.tempo = tempo;
    }

    public MeasureKey getKey() {
        return key;
    }
    public void setKey(MeasureKey key) {
        this.key = key;
    }

    public BarLineEnum getStartLine() {
        return startLine;
    }
    public void setStartLine(BarLineEnum startLine) {
        this.startLine = startLine;
    }

    public BarLineEnum getEndLine() {
        return endLine;
    }
    public void setEndLine(BarLineEnum endLine) {
        this.endLine = endLine;
    }

    public AlternateEnding getAlternateEnding() {
        return alternateEnding;
    }
    public void setAlternateEnding(AlternateEnding alternateEnding) {
        this.alternateEnding = alternateEnding;
    }

    public Measure getPreviousMeasure() {
        return previousMeasure;
    }
    public void setPreviousMeasure(Measure previousMeasure) {
        this.previousMeasure = previousMeasure;
    }

    public Measure getNextMeasure() {
        return nextMeasure;
    }
    public void setNextMeasure(Measure nextMeasure) {
        this.nextMeasure = nextMeasure;
    }

    public List<MeasureEntity> getEntities() {
        return entities;
    }
    public void setEntities(List<MeasureEntity> entities) {
        this.entities = entities;
    }

    public Measure withVoiceName(final String voiceName) {
        setVoiceName(voiceName);
        return this;
    }
    public Measure withNoteDuration(final Double noteDuration) {
        setNoteDuration(noteDuration);
        return this;
    }
    public Measure withMeter(final Meter meter) {
        setMeter(meter);
        return this;
    }
    public Measure withTempo(final Integer tempo) {
        setTempo(tempo);
        return this;
    }
    public Measure withKey(final MeasureKey key) {
        setKey(key);
        return this;
    }
    public Measure withStartLine(final BarLineEnum startLine) {
        setStartLine(startLine);
        return this;
    }
    public Measure withEndLine(final BarLineEnum endLine) {
        setEndLine(endLine);
        return this;
    }
    public Measure withAlternateEnding(final AlternateEnding alternateEnding) {
        setAlternateEnding(alternateEnding);
        return this;
    }
    public Measure withPreviousMeasure(final Measure previousMeasure) {
        setPreviousMeasure(previousMeasure);
        return this;
    }
    public Measure withNextMeasure(final Measure nextMeasure) {
        setNextMeasure(nextMeasure);
        return this;
    }
    public Measure withEntities(final List<MeasureEntity> entities) {
        setEntities(entities);
        return this;
    }

    // Follow the chain back to the last time the tempo was set.
    public Integer getLastTempo() {
        if (null != tempo) {
            return tempo;
        }
        if (null == previousMeasure) {
            throw new IllegalArgumentException("Tempo was never set");
        }
        return previousMeasure.getLastTempo();
    }
    // Follow the chain back to the last time the key was set.
    public MeasureKey getLastKey() {
        if (null != key) {
            return key;
        }
        if (null == previousMeasure) {
            throw new IllegalArgumentException("Key was never set");
        }
        return previousMeasure.getLastKey();
    }
    // Follow the chain back to the last time the meter was set.
    public Meter getLastMeter() {
        if (null != meter) {
            return meter;
        }
        if (null == previousMeasure) {
            throw new IllegalArgumentException("Meter was never set");
        }
        return previousMeasure.getLastMeter();
    }
    // Follow the chain back to the last time the Note Duration was set.
    public Double getLastNoteDuration() {
        if (null != noteDuration) {
            return noteDuration;
        }
        if (null == previousMeasure) {
            throw new IllegalArgumentException("Duration was never set");
        }
        return previousMeasure.getLastNoteDuration();
    }
}
