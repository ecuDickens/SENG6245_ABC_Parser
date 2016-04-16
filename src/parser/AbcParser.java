package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Meter;
import model.Song;
import model.Voice;

public class AbcParser implements Parser {
	
	@Override
	public Song parse(final String fileName) {
		final Song song = new Song().withVoices(new HashMap<String, Voice>());
		final Map<String, List<String>> voiceNameToTokens = parseHeader(fileName, song);
		handleBody(voiceNameToTokens, song);
        return song;
	}
	
	// Parses the file, extracting all of the header fields into the song object and 
	// returning a map of declared voice names to their associated sections of music.
	private Map<String, List<String>> parseHeader(final String fileName, final Song song) {
		final Map<String, List<String>> voiceNameToToken = new HashMap<String, List<String>>();
		
		BufferedReader bufferedReader = null;
        try {
        	bufferedReader = new BufferedReader(new FileReader(fileName));
            String line = null;
            String currentVoice = "default";            
            while((line = bufferedReader.readLine()) != null) {
            	final List<String> tokens = Arrays.asList(line.split(" "));
            	// Ignore empty lines.
            	if (tokens.isEmpty()) {
            		continue;
            	}
            	
            	// Extract the header token and perform logic based on what type of field it is.
            	final AbcHeaderField field = AbcHeaderField.from(tokens.get(0));
            	switch(field) {
	            	case INDEX:
	        			handleIndex(tokens, song);
	        			break;
            		case TITLE:
            			handleTitle(tokens, song);
            			break;
            		case COMPOSER:
            			handleComposer(tokens, song);
            			break;            		
            		case DURATION:
            			handleDuration(tokens, song);
            			break;
            		case METER:
            			handleMeter(tokens, song);
            			break;
            		case TEMPO:
            			handleTempo(tokens, song);
            			break;            		
            		case VOICE:
            			currentVoice = handleVoice(tokens, song);
            			break;
            		case KEY:
            			handleKey(tokens, song);
            			break;
        			default:
        				// No valid header token found, add this line to the current voice.
        				if (null == voiceNameToToken.get(currentVoice)) {
        					voiceNameToToken.put(currentVoice, tokens);
        				} else {
        					voiceNameToToken.get(currentVoice).addAll(tokens);
        				}
        				break;
            	}
            }          
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        } catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");   
        } finally {
        	if (null != bufferedReader) {
        		try {
					bufferedReader.close();
				} catch (IOException e) {
					System.out.println("Unable to close file '" + fileName + "'"); 
				}
        	}
        }
        
        return voiceNameToToken;
	}
	
	// Validates the index line and adds it to the song.
	private void handleIndex(final List<String> tokens, final Song song) {
		checkArgument(tokens.size() != 2, "Index incorrectly formatted.");
		checkArgument(null != song.getIndex(), "Index cannot be declared more than once.");
		
		song.setIndex(Integer.valueOf(tokens.get(1)));
	}	
	
	// Validates the title line and adds it to the song.
	private void handleTitle(final List<String> tokens, final Song song) {
		checkArgument(null == song.getIndex(), "Index must be specified first.");
		checkArgument(null != song.getTitle(), "Title cannot be declared more than once.");
		
		tokens.remove(0);
		song.setTitle(String.join(" ", tokens));
	}
	
	// Validates the composer line and adds it to the song.
	private void handleComposer(final List<String> tokens, final Song song) {
		checkArgument(null == song.getIndex(), "Index must be specified first.");
		checkArgument(null == song.getTitle(), "Title must be specified second.");
		checkArgument(null != song.getKey(), "Key must be specified last.");
		checkArgument(null != song.getComposer(), "Composer cannot be declared more than once.");
		
		tokens.remove(0);
		song.setComposer(String.join(" ", tokens));
	}
	
	// Validates the duration and adds it to the song.
	private void handleDuration(final List<String> tokens, final Song song) {
		checkArgument(null == song.getIndex(), "Index must be specified first.");
		checkArgument(null == song.getTitle(), "Title must be specified second.");
		checkArgument(null != song.getKey(), "Key must be specified last.");
		checkArgument(null != song.getNoteDuration(), "Duration cannot be declared more than once.");
		checkArgument(tokens.size() != 2, "Duration incorrectly formatted.");
		
		final List<String> durationTokens = Arrays.asList(tokens.get(1).split("/"));
		checkArgument(durationTokens.size() != 2, "Duration incorrectly formatted.");
		song.setNoteDuration(Double.valueOf(tokens.get(0)) / Double.valueOf(tokens.get(1)));
	}
	
	private void handleMeter(final List<String> tokens, final Song song) {
		checkArgument(null == song.getIndex(), "Index must be specified first.");
		checkArgument(null == song.getTitle(), "Title must be specified second.");
		checkArgument(null != song.getKey(), "Key must be specified last.");
		checkArgument(null != song.getMeter(), "Meter cannot be declared more than once.");
		
		final List<String> meterTokens = Arrays.asList(tokens.get(1).split("/"));
		checkArgument(meterTokens.size() != 2, "Meter incorrectly formatted.");
		song.setMeter(new Meter()
				.withBeatsPerMeasure(Integer.valueOf(meterTokens.get(0)))
				.withDuration(Integer.valueOf(meterTokens.get(1))));
	}
	
	private void handleTempo(final List<String> tokens, final Song song) {
		checkArgument(null == song.getIndex(), "Index must be specified first.");
		checkArgument(null == song.getTitle(), "Title must be specified second.");
		checkArgument(null != song.getKey(), "Key must be specified last.");
		checkArgument(null != song.getTempo(), "Tempo cannot be declared more than once.");
		checkArgument(tokens.size() != 2, "Tempo incorrectly formatted.");
		
		song.setTempo(Integer.valueOf(tokens.get(1)));
	}
	
	// Adds the voice to the song if it hasn't already been declared.
	// Returns the voice name so that the voice to token map builds correctly if declared in the body.
	private String handleVoice(final List<String> tokens, final Song song) {
		checkArgument(null == song.getIndex(), "Index must be specified first.");
		checkArgument(null == song.getTitle(), "Title must be specified second.");
		
		tokens.remove(0);
		final String voiceName = String.join(" ", tokens);		
		if (null == song.getVoices().get(voiceName)) {
			song.getVoices().put(voiceName, new Voice().withName(voiceName));
		}
		
		return voiceName;
	}
	
	// Validates the key and adds it to the song.
	private void handleKey(final List<String> tokens, final Song song) {
		checkArgument(null == song.getIndex(), "Index must be specified first.");
		checkArgument(null == song.getTitle(), "Title must be specified second.");
		checkArgument(tokens.size() != 2, "Tempo incorrectly formatted.");
		
	}
	
	// Parses the tokens for sections, bars, and notes.
	private void handleBody(final Map<String, List<String>> voiceNameToTokens, final Song song) {
		
	}
	
	// Throw an illegal argument if the boolean is true (ie. pass in what you don't want to occur).
	private void checkArgument(final Boolean argument, final String errorText) {
		if (argument) {
			throw new IllegalArgumentException(errorText);
		}
	}
}
