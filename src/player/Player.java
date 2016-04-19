package player;

import model.Song;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public interface Player {
    /**
     * Stores the song and converts it into a format the the specified player can use (if needed).
     *
     * @param song the song to load.
     */
	void loadSong(Song song) throws MidiUnavailableException, InvalidMidiDataException;

    /**
     * Plays the loaded song.
     */
	void play() throws MidiUnavailableException;
}
