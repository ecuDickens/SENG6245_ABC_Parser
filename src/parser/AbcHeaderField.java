package parser;

import java.util.EnumSet;

/**
 * Enum defining appropriate line headers.
 * 
 * @author Nathan
 */
public enum AbcHeaderField {
	COMPOSER("C:"),
	KEY("K:"),
	DURATION("L:"),
	METER("M:"),
	TEMPO("Q:"),
	TITLE("T:"),
	INDEX("X:"),
	VOICE("V:");	
	
	private final String value;
	
	private AbcHeaderField(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static AbcHeaderField from(final String value) {
		for (AbcHeaderField abcHeader : EnumSet.allOf(AbcHeaderField.class)) {
			if (abcHeader.getValue().equals(value)) {
				return abcHeader;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return value;
	}
}