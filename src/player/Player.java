package player;

import model.Song;

public interface Player {
	void loadSong(Song song);
	
	void play();
}
