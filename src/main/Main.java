package main;

import model.Song;
import parser.AbcParser;
import player.MidiPlayer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;

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
	 * @param args the name of input abc file
	 */
    public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException, IOException {
		final AbcParser parser = new AbcParser();
		final Song song = parser.parse(args[0]);
		final MidiPlayer player = new MidiPlayer();
		player.play(song);
	}
}
