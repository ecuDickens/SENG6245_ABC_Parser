package main;

import model.Song;
import parser.AbcParser;
import player.MidiPlayer;

/**
 * Main entry point of your application.
 */
public class Main {

	/**
	 * Plays the input file using Java MIDI API and displays
	 * header information to the standard output stream.
	 * 
	 * <p>Your code <b>should not</b> exit the application abnormally using
	 * System.exit()</p>
	 * 
	 * @param file the name of input abc file
	 */
	public static void play(String file) {
		final AbcParser parser = new AbcParser();
		final Song song = parser.parse(file);
		final MidiPlayer player = new MidiPlayer();
		player.loadSong(song);
		player.play();
	}
}
