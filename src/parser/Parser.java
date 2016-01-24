package parser;

import model.Song;

public interface Parser {
	Song parse(String fileName);
}
