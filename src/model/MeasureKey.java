package model;

import model.entities.Note;
import model.enums.Accidental;
import model.enums.Key;
import model.enums.NoteEnum;

import java.util.List;

import static model.enums.Accidental.FLAT;
import static model.enums.Accidental.SHARP;
import static model.enums.Key.keyToFlats;
import static model.enums.Key.keyToSharps;

/**
 * Provides a method to determine whether a note is sharp or flat.
 */
public class MeasureKey {

    private List<NoteEnum> flats;
    private List<NoteEnum> sharps;

    public MeasureKey(final Key key) {
        this.flats = keyToFlats.get(key);
        this.sharps = keyToSharps.get(key);
    }

    public Accidental getAccidental(final Note note) {
        return null != flats && flats.contains(note.getNoteEnum()) ? FLAT :
               null != sharps && sharps.contains(note.getNoteEnum()) ? SHARP : null;
    }
}
