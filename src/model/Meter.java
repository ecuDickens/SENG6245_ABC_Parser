package model;

/**
 * A representation of how many beats are in a measure.
 */
public class Meter {
	// The upper part of the meter, specifying how many beats are in each measure.
	private Integer beatsPerMeasure;
	// The lower part of the meter, defining the type of beat (4 = quarter note, 8 = eighth note, etc). 
	private Integer duration;
	
	public Integer getBeatsPerMeasure() {
		return beatsPerMeasure;
	}
	public void setBeatsPerMeasure(Integer beatsPerMeasure) {
		this.beatsPerMeasure = beatsPerMeasure;
	}
	
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Meter withBeatsPerMeasure(Integer beatsPerMeasure) {
		setBeatsPerMeasure(beatsPerMeasure);
		return this;
	}
	public Meter withDuration(Integer duration) {
		setDuration(duration);
		return this;
	}

    @Override
    public String toString() {
        return beatsPerMeasure + "/" + duration;
    }
}
