package parser.enums;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Enum defining appropriate line headers.
 */
public enum AbcHeader {
	COMPOSER("C:"),
	KEY("K:"),
	DURATION("L:"),
	METER("M:"),
	TEMPO("Q:"),
	TITLE("T:"),
	INDEX("X:"),
	VOICE("V:");	
	
	private final String value;
    private static EnumSet<AbcHeader> FULL_SET = EnumSet.allOf(AbcHeader.class);
	
	private AbcHeader(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static AbcHeader from(final String value) {
        final Optional<AbcHeader> optional = FULL_SET.stream().filter(field -> field.getValue().equals(value)).findFirst();
        return optional.isPresent() ? optional.get() : null;
	}
	
	@Override
	public String toString() {
		return value;
	}
}