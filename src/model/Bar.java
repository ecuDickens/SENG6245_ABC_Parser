package model;

import java.util.List;

import model.enums.BarType;

public class Bar {
	// The parent section.
	private Section section;
	
	// The type of bar (ex.  Section Start, Section Start Repeat, Section End Repeat)
	// Informs what part of the section the bar is and how the sections index should be set up.
	private BarType type;
	
	// The list of notes, rests, tuplets, and chords that can be contained in a bar.
	private List<BarEntity> barContent; 	

	public Section getSection() {
		return section;
	}
	public void setSection(Section section) {
		this.section = section;
	}
	
	public BarType getType() {
		return type;
	}
	public void setType(BarType type) {
		this.type = type;
	}
	
	public List<BarEntity> getBarContent() {
		return barContent;
	}
	public void setBarContent(List<BarEntity> barContent) {
		this.barContent = barContent;
	}
}
