package parser;

import model.Measure;
import model.MeasureKey;
import model.Meter;
import model.Song;
import model.Voice;
import model.entities.AlternateEnding;
import model.entities.BarLine;
import model.entities.BrokenRhythm;
import model.entities.Chord;
import model.entities.MeasureEntity;
import model.entities.Note;
import model.entities.Rest;
import model.entities.Tuplet;
import model.enums.Accidental;
import model.enums.BarLineEnum;
import model.enums.Key;
import model.enums.MeasureEntityEnum;
import model.enums.NoteEnum;
import parser.enums.AbcHeader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static model.enums.BarLineEnum.REPEAT_END;
import static model.enums.BarLineEnum.REPEAT_START;
import static model.enums.BarLineEnum.SECTION_END;
import static model.enums.BarLineEnum.STANDARD;
import static model.enums.BarLineEnum.START;
import static model.enums.MeasureEntityEnum.ALTERNATE_ENDING;
import static model.enums.MeasureEntityEnum.BAR;
import static model.enums.MeasureEntityEnum.BROKEN_RHYTHM;
import static model.enums.MeasureEntityEnum.CHORD;
import static model.enums.MeasureEntityEnum.NOTE;
import static model.enums.MeasureEntityEnum.REST;
import static model.enums.MeasureEntityEnum.TUPLET;
import static parser.AbcHelper.*;
import static parser.enums.AbcHeader.INDEX;
import static parser.enums.AbcHeader.TITLE;

public class AbcParser implements Parser {
	
	@Override
	public Song parse(final String fileName) throws IOException {
		final Song song = new Song().withComposer(STANDARD_COMPOSER).withVoices(new HashMap<>());

        System.out.println("Parsing " + fileName);

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));

            // Tracks voice, tempo, meter, and key changes.  Any measures created from a body line will inherit this measure's attributes.
            final Measure tracker = new Measure()
                    .withVoiceName(STANDARD_VOICE)
                    .withNoteDuration(STANDARD_NOTE_DURATION)
                    .withMeter(STANDARD_METER)
                    .withTempo(STANDARD_TEMPO);
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
            System.out.println("Parsed " +song.getVoices().size() + " voices with " +song.getVoices().get(tracker.getVoiceName()).getMeasures().size() + " measures per voice.");
        } catch (FileNotFoundException ex) {
            System.out.println(String.format("File '%s' not found.", fileName));
            throw ex;
        } catch (Exception ex) {
            System.out.println(String.format("Error reading file '%s': %s.", fileName, ex.getMessage()));
            throw ex;
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println("Unable to close file '" + fileName + "'");
                    throw e;
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
        int commentIndex = line.indexOf(COMMENT_CHAR);
        return line.trim().substring(null != header ? 2 : 0, 0 < commentIndex ? commentIndex : line.trim().length()).trim();
    }

    // Validates the header and sets it on the appropriate field on the song or measure tracker.
    private void handleHeader(final AbcHeader header, final String value, final Song song, final Measure tracker) {
        validateHeaderField(value, header, song);
        switch(header) {
            case INDEX:
                song.setIndex(toValidInteger(value, String.format("%s is invalid.", header)));
                System.out.println("Set index to " + song.getIndex());
                break;
            case TITLE:
                // Concatenate multiple title's together.
                if (isNullOrEmpty(song.getTitle())) {
                    song.setTitle(value);
                } else {
                    song.setTitle(String.format("%s %s", song.getTitle(), value));
                }
                System.out.println("Set title to " + song.getTitle());
                break;
            case COMPOSER:
                // Concatenate multiple composer's together.
                if (isNullOrEmpty(song.getComposer()) || STANDARD_COMPOSER.equals(song.getComposer())) {
                    song.setComposer(value);
                } else {
                    song.setComposer(String.format("%s %s", song.getComposer(), value));
                }
                System.out.println("Set composer to " + song.getComposer());
                break;
            case VOICE:
                // Add the voice to the song if it doesn't already exist.
                // Set on the tracker so that any measures created will be assigned to that voice.
                if (!song.getVoices().containsKey(value)) {
                    song.getVoices().put(value, new Voice());
                    System.out.println("Adding voice " + value);
                } else {
                    System.out.println("Adding measures to voice " + value);
                }
                tracker.setVoiceName(value);
                break;
            case DURATION:
                final List<String> durationTokens = Arrays.asList(value.split("/"));
                checkArgument(durationTokens.size() != 2, "Invalid Duration.");
                tracker.setNoteDuration(toValidDouble(durationTokens.get(0), String.format("%s is invalid.", header)) / toValidDouble(durationTokens.get(1), String.format("%s is invalid.", header)));
                System.out.println("Setting duration to " + tracker.getNoteDuration());
                break;
            case METER:
                if ("C".equals(value)) {
                    tracker.setMeter(new Meter().withBeatsPerMeasure(4).withDuration(4));
                } else {
                    final List<String> meterTokens = Arrays.asList(value.split("/"));
                    checkArgument(meterTokens.size() != 2, "Invalid Meter.");
                    tracker.setMeter(new Meter().withBeatsPerMeasure(toValidInteger(meterTokens.get(0), String.format("%s is invalid.", header))).withDuration(toValidInteger(meterTokens.get(1), String.format("%s is invalid.", header))));
                }
                System.out.println("Setting meter to " + tracker.getMeter());
                break;
            case TEMPO:
                tracker.setTempo(toValidInteger(value, String.format("%s is invalid.", header)));
                System.out.println("Setting tempo to " + tracker.getTempo());
                break;
            case KEY:
                final Key parsedKey = AbcHelper.stringToKey.get(value);
                checkArgument(null == parsedKey, "Invalid Key.");
                tracker.setKey(new MeasureKey(parsedKey));
                System.out.println("Setting key to " + parsedKey);
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
                    .withIndex(previous.getIndex() + 1)
                    .withPreviousMeasure(previous)
                    .withTempo(!current.getLastTempo().equals(tracker.getTempo()) ? tracker.getTempo() : null)
                    .withKey(!current.getLastKey().equals(tracker.getKey()) ? tracker.getKey() : null)
                    .withMeter(!current.getLastMeter().equals(tracker.getMeter()) ? tracker.getMeter() : null)
                    .withNoteDuration(!current.getLastNoteDuration().equals(tracker.getNoteDuration()) ? tracker.getNoteDuration() : null);
        } else {
            current.withStartLine(START)
                    .withIndex(1)
                    .withTempo(tracker.getTempo())
                    .withKey(tracker.getKey())
                    .withMeter(tracker.getMeter())
                    .withNoteDuration(tracker.getNoteDuration());
        }

        final List<MeasureEntity> entities = toEntities(line);
        handleBrokenRhythms(entities);
        Integer count = 1;
        for (MeasureEntity entity : entities) {
            // Bar lines signify the end of a measure, set up the next one.
            if (entity instanceof BarLine) {
                final BarLine barLine = (BarLine) entity;
                if (!current.getEntities().isEmpty()) {
                    current.setEndLine(barLine.getType());
                    measures.add(current);
                    if (count++ != entities.size()) {
                        current = new Measure().withIndex(current.getIndex() + 1).withPreviousMeasure(current).withStartLine(barLine.getType()).withEntities(new ArrayList<>());
                        current.getPreviousMeasure().setNextMeasure(current);
                    }
                }
            } else if (entity instanceof AlternateEnding) {
                current.withAlternateEnding((AlternateEnding) entity);
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

    // Parses the characters in the line for a list of matching entities.
    private List<MeasureEntity> toEntities(final String line) {
        final List<MeasureEntity> entities = new ArrayList<>();
        int index = 0;
        char[] lineChars = line.trim().replace(" ", "").toCharArray();
        while (true) {
            // Get the first character of an entity and determine it's type.
            final List<Character> chars = new ArrayList<>();
            chars.add(lineChars[index++]);
            MeasureEntityEnum type = getType(chars.get(0));
            // Read ahead for the rest of the entity characters.
            while (true) {
                if (lineChars.length == index) {
                    break;
                }
                final Character next = lineChars[index];
                // Special case to handle alternate endings.
                if (type == CHORD && NUM_CHARS.contains(next) && chars.size() == 1) {
                    type = ALTERNATE_ENDING;
                    chars.add(next);
                    index++;
                    break;
                }
                if (isNewEntity(next, chars, type)) {
                    break;
                }
                chars.add(next);
                index++;
            }
            // Construct the set of characters into the entity.
            entities.add(toEntity(chars, type));
            if (lineChars.length == index) {
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
        // Broken rhythm's start with '<' or '>'
        if (BROKEN_RHYTHM_BACK.equals(c) || BROKEN_RHYTHM_FORWARD.equals(c)) {
            return BROKEN_RHYTHM;
        }
        return null;
    }


    // Returns true if the next character does not belong to the existing list.
    // Only light validation is done here.  The heavy lifting occurs when trying to convert a list of characters into an entity.
    private boolean isNewEntity(final Character next, final List<Character> chars, final MeasureEntityEnum type) {
        switch(type) {
            case NOTE:
                // Return true if the actual note has been reached and next is the start of a new entity.
                return countNotesAndChords(chars) == 1 && NEW_ENTITY_CHARS.contains(next);
            case REST:
                // Return true if next is the start of a new entity.
                return NEW_ENTITY_CHARS.contains(next);
            case CHORD:
                // Return true if we've reached the ']' part of the chord.
                return countNotesAndChords(chars) == 1;
            case TUPLET:
                // Return true if the stated number of notes/chords have been defined.
                if (chars.size() == 1) {
                    checkArgument(!NUM_CHARS.contains(next), String.format("Invalid characters '%s'", chars.toString() + next));
                    return false;
                }
                final Integer noteCount = toValidInteger(chars.get(1).toString(), String.format("Invalid characters '%s'", chars.toString() + next));
                return countNotesAndChords(chars) == noteCount && NEW_ENTITY_CHARS.contains(next);
            case BAR:
                // Bars can be at most 2 characters consisting of '|', ':', and ']'.
                if (chars.size() == 1) {
                    return !BAR_CHAR.equals(next) && !REPEAT_CHAR.equals(next) && !CHORD_END_CHAR.equals(next);
                }
                checkArgument(BAR_CHAR.equals(next) || REPEAT_CHAR.equals(next) || CHORD_END_CHAR.equals(next), String.format("Invalid character '%s' following characters '%s'", next, chars.get(0) + chars.get(1)));
                return true;
            case BROKEN_RHYTHM:
                if (next == BROKEN_RHYTHM_BACK || next == BROKEN_RHYTHM_FORWARD) {
                    checkArgument(next != chars.get(0), "Invalid broken rhythm.");
                    return false;
                }
                return true;
            default:
                throw new IllegalArgumentException("Unknown type: " +type);
        }
    }

    // Construct the appropriate entity from the set of characters.
    private MeasureEntity toEntity(final List<Character> chars, final MeasureEntityEnum type) {
        switch(type) {
            case NOTE:
                final Note note = new Note()
                        .withAccidental(getAccidental(chars))
                        .withNoteEnum(getNoteEnum(chars));
                final Character noteChar = chars.get(0);
                chars.remove(0);
                if (!chars.isEmpty()) {
                    // The provided examples had these in two different orders, must account for both.
                    if ((SLASH_CHAR.equals(chars.get(0)) || NUM_CHARS.contains(chars.get(0)))) {
                        note.withDurationMultiplier(getDurationMultiplier(chars)).
                             withOctave(getOctave(chars, noteChar));
                    } else {
                        note.withOctave(getOctave(chars, noteChar)).
                             withDurationMultiplier(getDurationMultiplier(chars));
                    }
                } else {
                    note.setOctave(getOctave(chars, noteChar));
                }
                checkArgument(!chars.isEmpty(), "Invalid characters: " +chars.toString());
                return note;
            case REST:
                chars.remove(0); // Remove the 'z'
                final Rest rest = new Rest().withDurationMultiplier(getDurationMultiplier(chars));
                checkArgument(!chars.isEmpty(), "Invalid characters: " +chars.toString());
                return rest;
            case CHORD:
                checkArgument(!CHORD_OR_ALT_START_CHAR.equals(chars.get(0)) || !CHORD_END_CHAR.equals(chars.get(chars.size() - 1)), "Chord is invalid");
                // Strip the brackets and turn the remaining string into a list of note entities.
                chars.remove(0);
                chars.remove(chars.size() - 1);
                final List<MeasureEntity> notes = toEntities(toString(chars));
                checkArgument(notes.stream().anyMatch(n -> !(n instanceof Note)), "Chord is invalid.");
                return new Chord().withDurationMultiplier(notes.get(0).getDurationMultiplier()).withNotes(notes.stream().map(n -> (Note) n).collect(toList()));
            case TUPLET:
                checkArgument(!TUPLET_START_CHAR.equals(chars.get(0)) || !NUM_CHARS.contains(chars.get(1)), "Tuplet is invalid.");
                chars.remove(0);
                final Integer entityCount = toValidInteger(chars.get(0).toString(), "Tuplet is invalid");
                chars.remove(0);
                final List<MeasureEntity> entities = toEntities(toString(chars));
                checkArgument(entities.size() != entityCount, "Tuplet is invalid");
                // A tuplet's duration is either 2 notes over 3 beats, 3 notes over 2 beats, or 4 notes over 3 beats.
                return new Tuplet().withDurationMultiplier(3 == entities.size() ? 2.0 : 3.0).withEntities(entities);
            case BAR:
                final BarLineEnum barLineEnum = chars.size() == 1 ? STANDARD :
                        REPEAT_CHAR.equals(chars.get(0)) && BAR_CHAR.equals(chars.get(1)) ? REPEAT_END :
                        BAR_CHAR.equals(chars.get(0)) && REPEAT_CHAR.equals(chars.get(1)) ? REPEAT_START :
                        BAR_CHAR.equals(chars.get(0)) && CHORD_END_CHAR.equals(chars.get(1)) ? SECTION_END :
                        BAR_CHAR.equals(chars.get(0)) && BAR_CHAR.equals(chars.get(1)) ? SECTION_END : null;
                checkArgument(null == barLineEnum, String.format("%s is invalid.", chars.toString()));
                return new BarLine().withType(barLineEnum);
            case ALTERNATE_ENDING:
                return new AlternateEnding().withEndingIndex(toValidInteger(chars.get(1).toString(), String.format("%s is invalid.", chars.toString())));
            case BROKEN_RHYTHM:
                final Double multiplier = 1.0 / Math.pow(2.0, chars.size()); // 0.5 for one, 0.25 for 2. 0.125 for 3, etc.
                return new BrokenRhythm()
                        .withFirstNoteMultiplier(chars.get(0) == BROKEN_RHYTHM_FORWARD ? multiplier : 2 - multiplier) // forward means the first note is short.
                        .withSecondNoteMultiplier(chars.get(0) == BROKEN_RHYTHM_BACK ? multiplier : 2 - multiplier);
            default:
                throw new IllegalArgumentException("Unknown type: " +type);
        }
    }

    // Notes have the following order, Accidental (opt - 1-2 characters), Note (Required - 1 character), Octave (opt - x characters), Duration (opt - x/x characters)
    // Run through these methods in order with the assumption that the first character in the list should pertain
    private Accidental getAccidental(final List<Character> chars) {
        if (ACCIDENTAL_CHARS.contains(chars.get(0))) {
            if (chars.size() > 1 && ACCIDENTAL_CHARS.contains(chars.get(1))) {
                if (SHARP_CHAR.equals(chars.get(0)) && SHARP_CHAR.equals(chars.get(1))) {
                    chars.remove(0);
                    chars.remove(0);
                    return Accidental.DOUBLE_SHARP;
                }
                if (FLAT_CHAR.equals(chars.get(0)) && FLAT_CHAR.equals(chars.get(1))) {
                    chars.remove(0);
                    chars.remove(0);
                    return Accidental.DOUBLE_FLAT;
                }
                throw new IllegalArgumentException("Invalid accidental");
            }
            if (SHARP_CHAR.equals(chars.get(0))) {
                chars.remove(0);
                return Accidental.SHARP;
            }
            if (FLAT_CHAR.equals(chars.get(0))) {
                chars.remove(0);
                return Accidental.FLAT;
            }
            if (NATURAL_CHAR.equals(chars.get(0))) {
                chars.remove(0);
                return Accidental.NATURAL;
            }
        }
        return null;
    }
    private NoteEnum getNoteEnum(final List<Character> chars) {
        checkArgument(chars.size() == 0 || !characterToNote.containsKey(chars.get(0)), "Invalid note");
        return characterToNote.get(chars.get(0)); // Don't remove the note from the list so that the octave calculation can use it.
    }

    private Double getDurationMultiplier(final List<Character> chars) {
        if (chars.isEmpty() || (!SLASH_CHAR.equals(chars.get(0)) && !NUM_CHARS.contains(chars.get(0)))) {
            return 1.0;
        }
        Double numerator = 1.0;
        if (!SLASH_CHAR.equals(chars.get(0))) {
            numerator = getNumber(chars);
        }

        Double denominator = 1.0;
        if (!chars.isEmpty() && SLASH_CHAR.equals(chars.get(0))) {
            chars.remove(0);
            denominator = chars.isEmpty() ? 2.0 : getNumber(chars);
        }

        return numerator/denominator;
    }

    private Double getNumber(final List<Character> chars) {
        final List<Character> numeratorChars = new ArrayList<>();
        while (true) {
            if (chars.isEmpty()) {
                break;
            }
            final Character next = chars.get(0);
            if (!NUM_CHARS.contains(next)) {
                break;
            }
            numeratorChars.add(next);
            chars.remove(0);
        }
        return toValidDouble(toString(numeratorChars), "Invalid duration.");
    }

    private Integer getOctave(final List<Character> chars, final Character noteChar) {
        final Integer octave = Character.isUpperCase(noteChar) ? 0 : 1;
        if (chars.isEmpty()) {
            return octave;
        }

        Integer upCount = 0;
        Integer downCount = 0;
        while (true) {
            if (chars.isEmpty()) {
                break;
            }
            final Character next = chars.get(0);
            if (!OCTAVE_CHARS.contains(next)) {
                break;
            }
            if (OCTAVE_UP_CHAR.equals(next)) {
                upCount++;
            }
            if (OCTAVE_DOWN_CHAR.equals(next)) {
                downCount++;
            }
            chars.remove(0);
        }
        // Don't allow mixtures of the octave chars or the down octave on lower case notes.
        checkArgument((upCount != 0 && downCount != 0) || (octave == 1 && downCount != 0), "Invalid octave");
        return octave + upCount - downCount;
    }

    // Apply the duration to the entities preceding and following.
    private void handleBrokenRhythms(final List<MeasureEntity> entities) {
        int count = 0;
        for (MeasureEntity entity : entities) {
            if (entity instanceof BrokenRhythm) {
                final BrokenRhythm br = (BrokenRhythm) entity;

                // Must be between two notes or chords.
                checkArgument(count == 0 || count == entities.size() - 1, "Invalid Broken Rhythm");
                final MeasureEntity first = entities.get(count - 1);
                final MeasureEntity second = entities.get(count + 1);
                checkArgument(!(first instanceof Note || first instanceof Chord) || !(second instanceof Note || second instanceof Chord), "Invalid Broken Rhythm");
                first.setDurationMultiplier(first.getDurationMultiplier() * br.getFirstNoteMultiplier());
                second.setDurationMultiplier(second.getDurationMultiplier() * br.getSecondNoteMultiplier());
            }
            count ++;
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

    private String toString(final List<Character> characters) {
        return characters.stream().map(c -> c.toString()).collect(Collectors.joining());
    }
}
