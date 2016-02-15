package model;

import java.util.List;

public class Voice {
	// The parent song the voice belongs to.
	private Song song;
	
	// The name of the song.
	private String name;
	
	// This list of sections belonging to this song.
	private List<Section> sections;

	public Song getSong() {
		return song;
	}
	public void setSong(Song song) {
		this.song = song;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Section> getSections() {
		return sections;
	}
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	public Voice withSong(Song song) {
		setSong(song);
		return this;
	}
	public Voice withName(String name) {
		setName(name);
		return this;
	}
	public Voice withType(Song song) {
		setSong(song);
		return this;
	}
	public Voice withSections(List<Section> sections) {
		setSections(sections);
		return this;
	}
}
