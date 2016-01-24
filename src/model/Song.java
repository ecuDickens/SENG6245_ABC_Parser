package model;

import java.util.List;

import model.enums.SongKey;
import model.enums.NoteDuration;

public class Song {
	// The song id.
	private int index;
	
	// The song title.
	private String title;
	
	// The song composer.
	private String composer;
	
	// The default note duration.  Used when determining the 
	private NoteDuration noteDuration;
	private String meter;
	private int tempo;
	private SongKey key;
	private List<Voice> voices;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getComposer() {
		return composer;
	}
	public void setComposer(String composer) {
		this.composer = composer;
	}

	public NoteDuration getNoteDuration() {
		return noteDuration;
	}
	public void setNoteDuration(NoteDuration noteDuration) {
		this.noteDuration = noteDuration;
	}
	
	public String getMeter() {
		return meter;
	}
	public void setMeter(String meter) {
		this.meter = meter;
	}
	
	public int getTempo() {
		return tempo;
	}
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}
	
	public SongKey getKey() {
		return key;
	}
	public void setKey(SongKey key) {
		this.key = key;
	}
	
	public List<Voice> getVoices() {
		return voices;
	}
	public void setVoices(List<Voice> voices) {
		this.voices = voices;
	}
}
