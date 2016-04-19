package parser;

import model.Measure;
import model.MeasureKey;
import model.Meter;
import model.Song;
import model.Voice;
import model.entities.AlternateEnding;
import model.entities.BarLine;
import model.entities.Chord;
import model.entities.MeasureEntity;
import model.entities.Note;
import model.entities.Rest;
import model.entities.Tuplet;
import model.enums.BarLineEnum;
import model.enums.Key;
import model.enums.MeasureEntityEnum;
import parser.enums.AbcHeader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.Character.isUpperCase;
import static model.enums.BarLineEnum.REPEAT_END;
import static model.enums.BarLineEnum.REPEAT_START;
import static model.enums.BarLineEnum.SECTION_END;
import static model.enums.BarLineEnum.STANDARD;
import static model.enums.BarLineEnum.START;
import static model.enums.MeasureEntityEnum.ALTERNATE_ENDING;
import static model.enums.MeasureEntityEnum.BAR;
import static model.enums.MeasureEntityEnum.CHORD;
import static model.enums.MeasureEntityEnum.NOTE;
import static model.enums.MeasureEntityEnum.REST;
import static model.enums.MeasureEntityEnum.TUPLET;
import static parser.AbcHelper.ACCIDENTAL_CHARS;
import static parser.AbcHelper.BAR_CHAR;
import static parser.AbcHelper.CHORD_END_CHAR;
import static parser.AbcHelper.CHORD_OR_ALT_START_CHAR;
import static parser.AbcHelper.NOTE_CHARS;
import static parser.AbcHelper.NUM_CHARS;
import static parser.AbcHelper.REPEAT_CHAR;
import static parser.AbcHelper.REST_CHAR;
import static parser.AbcHelper.TUPLET_START_CHAR;
import static parser.AbcHelper.characterToNote;
import static parser.AbcHelper.countNotesAndChords;
import static parser.enums.AbcHeader.INDEX;
import static parser.enums.AbcHeader.TITLE;

public class AbcParser implements Parser {
	
	@Override
	public Song parse(final String fileName) {
		final Song song = new Song().withVoices(new HashMap<>());

        System.out.println("Parsing " + fileName);

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));

            // Tracks voice, tempo, meter, and key changes.  Any measures created from a body line will inherit this measure's attributes.
            final Measure tracker = new Measure().withVoiceName("Default").withNoteDuration(0.5).withMeter(new Meter().withBeatsPerMeasure(4).withDuration(4)).withTempo(100);
            String line;
            while((line = bufferedReader.readLine()) != null) {
                // Skip empty or comment lines.
                if (isNullOrEmpty(line) || line.startsWith("%")) {
                    continue;
                }

                final AbcHeader header = getHeader(line);
                final String value = stripHeaderAndComment(line, header);
                if (null != header) {
                    handleHeader(header, value, song, tracker);
                } else {
                    checkArgument(null == tracker.getKey(), "Key must be declared at least once before defining the body.");
                    handleValue(value, tracker, song);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(String.format("File '%s' not found.", fileName));
        } catch (Exception ex) {
            System.out.println(String.format("Error reading file '%s': %s.", fileName, ex.getMessage()));
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println("Unable to close file '" + fileName + "'");
                }
            }
        }

        return song;
	}

    // Only lines starting with "%s:" will return.  Lines with repeat bars like ":|" will be ignored.
    private AbcHeader getHeader(final String line) {
        if (line.contains(":") && !line.contains("|")) {
            return AbcHeader.from(line.trim().substring(0, 2));
        }
        return null;
    }

    // Form a string from the text after any header and before any comments.
    private String stripHeaderAndComment(final String line, final AbcHeader header) {
        int commentIndex = line.indexOf('%');
        return line.trim().substring(null != header ? 2 : 0, 0 < commentIndex ? commentIndex : line.length());
    }

    // Validates the header and sets it on the appropriate field on the song or measure tracker.
    private void handleHeader(final AbcHeader header, final String value, final Song song, final Measure tracker) {
        validateHeaderField(value, header, song);
        switch(header) {
            case INDEX:
                song.setIndex(toValidInteger(value, String.format("%s is invalid.", header)));
                break;
            case TITLE:
                // Concatenate multiple title's together.
                if (isNullOrEmpty(song.getTitle())) {
                    song.setTitle(value);
                } else {
                    song.setTitle(String.format("%s %s", song.getTitle(), value));
                }
                break;
            case COMPOSER:
                // Concatenate multiple composer's together.
                if (isNullOrEmpty(song.getComposer())) {
                    song.setComposer(value);
                } else {
                    song.setComposer(String.format("%s %s", song.getComposer(), value));
                }
                break;
            case VOICE:
                // Add the voice to the song if it doesn't already exist.
                // Set on the tracker so that any measures created will be assigned to that voice.
                if (!song.getVoices().containsKey(value)) {
                    song.getVoices().put(value, new Voice());
                }
                tracker.setVoiceName(value);
                break;
            case DURATION:
                final List<String> durationTokens = Arrays.asList(value.split("/"));
                checkArgument(durationTokens.size() != 2, "Invalid Duration.");
                tracker.setNoteDuration(toValidDouble(durationTokens.get(0), String.format("%s is invalid.", header)) / toValidDouble(durationTokens.get(1), String.format("%s is invalid.", header)));
                break;
            case METER:
                final List<String> meterTokens = Arrays.asList(value.split("/"));
                checkArgument(meterTokens.size() != 2, "Invalid Meter.");
                tracker.setMeter(new Meter().withBeatsPerMeasure(toValidInteger(meterTokens.get(0), String.format("%s is invalid.", header))).withDuration(toValidInteger(meterTokens.get(1), String.format("%s is invalid.", header))));
                break;
            case TEMPO:
                tracker.setTempo(toValidInteger(value, String.format("%s is invalid.", header)));
                break;
            case KEY:
                final Key parsedKey = AbcHelper.stringToKey.get(value);
                checkArgument(null == parsedKey, "Invalid Key.");
                tracker.setKey(new MeasureKey(parsedKey));
                break;
            default:
                throw new IllegalArgumentException(header.getValue() + " is invalid.");
        }
    }

    private void validateHeaderField(final String headerText, final AbcHeader header, final Song song) {
        checkArgument(isNullOrEmpty(headerText), String.format("%s is invalid.", header));
        if (INDEX == header) {
            checkArgument(null != song.getIndex(), header + " cannot be declared more than once.");
        } else {
            checkArgument(null == song.getIndex(), header + " must be specified first.");
            if (TITLE != header) {
                checkArgument(null == song.getTitle(), header + " must be specified second.");
            }
        }
    }

    // Validate and parse the line into a list of measure entities.
    private void handleValue(final String line, final Measure tracker, final Song song) {
        // handle a default voice.
        if (song.getVoices().isEmpty()) {
            song.getVoices().put("Default", new Voice());
        }
        final Voice voice = song.getVoices().get(tracker.getVoiceName());

        final List<Measure> measures = null == voice.getMeasures() ? new ArrayList<>() : voice.getMeasures();
        Measure current = new Measure().withEntities(new ArrayList<>());
        // Pick up where we left off or start fresh with the settings from the tracker.
        // If any settings change between lines, record them here as well.
        if (!measures.isEmpty()) {
            final Measure previous = measures.get(measures.size()-1);
            previous.setNextMeasure(current);
            current.withStartLine(previous.getEndLine())
                    .withPreviousMeasure(previous)
                    .withTempo(!current.getLastTempo().equals(tracker.getTempo()) ? tracker.getTempo() : null)
                    .withKey(!current.getLastKey().equals(tracker.getKey()) ? tracker.getKey() : null)
                    .withMeter(!current.getLastMeter().equals(tracker.getMeter()) ? tracker.getMeter() : null)
                    .withNoteDuration(!current.getLastNoteDuration().equals(tracker.getNoteDuration()) ? tracker.getNoteDuration() : null);
        } else {
            current.withStartLine(START)
                    .withTempo(tracker.getTempo())
                    .withKey(tracker.getKey())
                    .withMeter(tracker.getMeter())
                    .withNoteDuration(tracker.getNoteDuration());
        }

        for (MeasureEntity entity : toEntities(line)) {
            // Bar lines signify the end of a measure, set up the next one.
            if (entity instanceof BarLine) {
                final BarLine barLine = (BarLine) entity;
                if (!current.getEntities().isEmpty()) {
                    current.setEndLine(barLine.getType());
                    measures.add(current);
                    if (SECTION_END != barLine.getType()) {
                        current = new Measure().withPreviousMeasure(current).withStartLine(barLine.getType()).withEntities(new ArrayList<>());
                        current.getPreviousMeasure().setNextMeasure(current);
                    }
                }
            } else if (entity instanceof AlternateEnding) {
                current.setAlternateEnding((AlternateEnding) entity);
            } else {
                current.getEntities().add(entity);
            }
        }
        // In case the line didn't end in a bar line.
        if (!current.getEntities().isEmpty()) {
            measures.add(current);
        }

        // Merge in the new measures.
        voice.setMeasures(measures);
    }

    // Where all the nasty stuff happens.  Parses the characters in the line for a list of matching entities.
    private List<MeasureEntity> toEntities(final String line) {
        final List<MeasureEntity> entities = new ArrayList<>();
        int index = 0;
        char[] chars = line.trim().replace(" ", "").toCharArray();
        while (true) {
            // Get the first character of an entity and determine it's type.
            final List<Character> entityCharacters = new ArrayList<>();
            entityCharacters.add(chars[index++]);
            MeasureEntityEnum type = getType(entityCharacters.get(0));
            // Read ahead for the rest of the entity characters.
            while (true) {
                if (chars.length == index) {
                    break;
                }
                final Character next = chars[index];
                // Special case to handle alternate endings.
                if (type == CHORD && NUM_CHARS.contains(next)) {
                    type = ALTERNATE_ENDING;
                    entityCharacters.add(next);
                    index++;
                    break;
                }
                if (isNewEntity(next, entityCharacters, type)) {
                    break;
                }
                entityCharacters.add(next);
                index++;
            }
            // Construct the set of characters into the entity.
            entities.add(toEntity(entityCharacters, type));
            if (chars.length == index) {
                break;
            }
        }
        return entities;
    }

    private MeasureEntityEnum getType(final Character c) {
        // Notes can start with the raw note or an accidental (ex. B,,,2  G/2  ^F)
        if (NOTE_CHARS.contains(c) || ACCIDENTAL_CHARS.contains(c)) {
            return NOTE;
        }
        // Chords and alternate endings both start with '[', return CHORD for now and update once we have more characters.
        if (CHORD_OR_ALT_START_CHAR.equals(c)) {
            return CHORD;
        }
        // Rests always start with 'z'.
        if (REST_CHAR.equals(c)) {
            return REST;
        }
        // Tuplets always start with '('.
        if (TUPLET_START_CHAR.equals(c)) {
            return TUPLET;
        }
        // Bars can start with ':' or '|'.
        if (BAR_CHAR.equals(c) || REPEAT_CHAR.equals(c)) {
            return BAR;
        }
        return null;
    }


    // Returns true if the next character does not belong to the existing list.
    // Only light validation is done here.  The heavy lifting occurs when trying to convert a list of characters into an entity.
    private boolean isNewEntity(final Character next, final List<Character> entityCharacters, final MeasureEntityEnum type) {
        switch(type) {
            case NOTE:
                // Notes consist of accidentals (optional), note (mandatory), octaves (optional), and duration (optional).
                return countNotesAndChords(entityCharacters) == 1 && null != getType(next);
            case REST:
                // Rests are 'z' followed by an optional duration.
                return null != getType(next);
            case CHORD:
                // Chords are bookended by '[' and ']', so as long as we haven't gotten to ']' yet it isn't a new entity.
                return countNotesAndChords(entityCharacters) == 1 && null != getType(next);
            case TUPLET:
                // Tuplets end once the stated number of notes/chords have been defined.
                checkArgument(entityCharacters.size() == 1 && !NUM_CHARS.contains(next), String.format("Invalid characters '%s'", entityCharacters.toString() + next));
                final Integer noteCount = toValidInteger(entityCharacters.get(1).toString(), String.format("Invalid characters '%s'", entityCharacters.toString() + next));
                return countNotesAndChords(entityCharacters) == noteCount && null != getType(next);
            case BAR:
                // Bars can be at most 2 characters consisting of '|', ':', and ']'.
                if (entityCharacters.size() == 1) {
                    return !BAR_CHAR.equals(next) && !REPEAT_CHAR.equals(next) && !CHORD_END_CHAR.equals(next);
                }
                checkArgument(BAR_CHAR.equals(next) || REPEAT_CHAR.equals(next) || CHORD_END_CHAR.equals(next), String.format("Invalid character '%s' following characters '%s'", next, entityCharacters.get(0) + entityCharacters.get(1)));
                return true;
            default:
                throw new IllegalArgumentException("Unknown type: " +type);
        }
    }

    // Construct the appropriate entity from the set of characters.
    private MeasureEntity toEntity(final List<Character> entityCharacters, final MeasureEntityEnum type) {
        switch(type) {
            case NOTE:
                final Character noteChar = entityCharacters.get(0);
                return new Note()
                        .withNoteEnum(characterToNote.get(noteChar))
                        .withOctave(isUpperCase(noteChar) ? 0 : 1);
            case REST:
                return new Rest();
            case CHORD:
                final List<Note> notes = new ArrayList<>();
                return new Chord().withDuration(notes.get(0).getDurationMultiplier()).withNotes(notes);
            case TUPLET:
                final List<MeasureEntity> entities = new ArrayList<>();
                return new Tuplet().withDuration(2.0).withEntities(entities);
            case BAR:
                final BarLineEnum barLineEnum = entityCharacters.size() == 1 ? STANDARD :
                        REPEAT_CHAR.equals(entityCharacters.get(0)) && BAR_CHAR.equals(entityCharacters.get(1)) ? REPEAT_END :
                        BAR_CHAR.equals(entityCharacters.get(0)) && REPEAT_CHAR.equals(entityCharacters.get(1)) ? REPEAT_START :
                        BAR_CHAR.equals(entityCharacters.get(0)) && CHORD_END_CHAR.equals(entityCharacters.get(1)) ? SECTION_END :
                        BAR_CHAR.equals(entityCharacters.get(0)) && BAR_CHAR.equals(entityCharacters.get(1)) ? SECTION_END : null;
                checkArgument(null == barLineEnum, String.format("%s is invalid.", entityCharacters.toString()));
                return new BarLine().withType(barLineEnum);
            case ALTERNATE_ENDING:
                return new AlternateEnding().withEndingIndex(toValidInteger(entityCharacters.get(1).toString(), String.format("%s is invalid.", entityCharacters.toString())));
            default:
                throw new IllegalArgumentException("Unknown type: " +type);
        }
    }

    private boolean isNullOrEmpty(final String string) {
        return null == string || "".equals(string);
    }

	// Throw an illegal argument if the boolean is true (ie. pass in what you don't want to occur).
	private void checkArgument(final Boolean argument, final String errorText) {
		if (argument) {
			throw new IllegalArgumentException(errorText);
		}
	}

    private Integer toValidInteger(final String integerString, final String error) {
        try {
            return Integer.valueOf(integerString.trim());
        } catch(Exception e) {
            throw new IllegalArgumentException(error);
        }
    }

    private Double toValidDouble(final String doubleString, final String error) {
        try {
            return Double.valueOf(doubleString.trim());
        } catch(Exception e) {
            throw new IllegalArgumentException(error);
        }
    }
}
