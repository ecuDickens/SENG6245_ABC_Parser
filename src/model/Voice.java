package model;

import java.util.List;

import model.enums.VoiceType;

public class Voice {
	private Song song;
	private String name;
	private VoiceType type;
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
	
	public VoiceType getType() {
		return type;
	}
	public void setType(VoiceType type) {
		this.type = type;
	}
	
	public List<Section> getSections() {
		return sections;
	}
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
}
