package model;

import java.util.List;

/**
 * A representation of a single musical track in a song.  Iterating over the measures and following the bar types will
 * result in the track being played appropriately.
 */
public class Voice {

	// This list of measures belonging to this voice.
	private List<Measure> measures;
	
	public List<Measure> getMeasures() {
		return measures;
	}
	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}

	public Voice withMeasures(List<Measure> sections) {
		setMeasures(sections);
		return this;
	}
}
