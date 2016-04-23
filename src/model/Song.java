package model;

import java.util.Map;

/**
 * A representation of a song.
 */
public class Song {
	// The song id.
	private Integer index;
	
	// The song title.
	private String title;
	
	// The song composer.
	private String composer = "Unknown";
	
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
	
	public Map<String, Voice> getVoices() {
		return voices;
	}
	public void setVoices(Map<String, Voice> voices) {
		this.voices = voices;
	}

    public Song withComposer(final String composer) {
        setComposer(composer);
        return this;
    }
    public Song withVoices(Map<String, Voice> voices) {
		setVoices(voices);
		return this;
	}
}
