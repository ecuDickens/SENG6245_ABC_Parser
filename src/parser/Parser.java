package parser;

import model.Song;

public interface Parser {
    /**
     * Attempts to parse the specified file according to rules of the chosen processor.
     *
     * @param fileName the name of the file to parse.
     * @return a Song representation of the file.
     */
	Song parse(String fileName);
}
