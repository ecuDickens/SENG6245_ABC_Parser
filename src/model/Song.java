package model;

import java.util.Map;

import model.enums.SongKey;

public class Song {
	// The song id.
	private Integer index;
	
	// The song title.
	private String title;
	
	// The song composer.
	private String composer;
	
	// The default note duration.  Used when determining the actual duration of parsed notes.
	private Double noteDuration;
	
	// Defines what the sum of all note durations in a bar should be.
	private Meter meter;
	
	// Defines the number of default durations notes per minute.
	private Integer tempo;
	
	// Defines which notes are naturally flat or sharp.
	private SongKey key;
	
	// The list of voices for this song mapped by their name.  Defaults to one if none are defined.
	private Map<String, Voice> voices;
	
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
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
	
	public SongKey getKey() {
		return key;
	}
	public void setKey(SongKey key) {
		this.key = key;
	}
	
	public Map<String, Voice> getVoices() {
		return voices;
	}
	public void setVoices(Map<String, Voice> voices) {
		this.voices = voices;
	}
	
	public Song withVoices(Map<String, Voice> voices) {
		setVoices(voices);
		return this;
	}
}
