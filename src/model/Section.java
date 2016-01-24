package model;

import java.util.List;
import java.util.Map;

public class Section {
	private Voice voice;
	private int loopIndex;
	private Map<Integer, Integer> sectionIndex;
	private List<Bar> bars;

	public Voice getVoice() {
		return voice;
	}
	public void setVoice(Voice voice) {
		this.voice = voice;
	}
	
	public int getLoopIndex() {
		return loopIndex;
	}
	public void setLoopIndex(int loopIndex) {
		this.loopIndex = loopIndex;
	}
	
	public Map<Integer, Integer> getSectionIndex() {
		return sectionIndex;
	}
	public void setSectionIndex(Map<Integer, Integer> sectionIndex) {
		this.sectionIndex = sectionIndex;
	}
	
	public List<Bar> getBars() {
		return bars;
	}
	public void setBars(List<Bar> bars) {
		this.bars = bars;
	}
}
