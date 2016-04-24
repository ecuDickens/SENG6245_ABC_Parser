# SENG6245_ABC_Parser

This project implements a parser that can read abc file notation and return a data structure representing that song.  
The data structure can then be passed to a player that can read the song and convert it into a midi sequence for playback.

Main Parts:
Parser: The interface defining a single method to parse a file name and return a song.
AbcParser: The implementation of the parser interface for abc file notation.  Loads the file, parsing it line by line.  
  Returns a Song object containing all information from the file.
Song: Represents all pertinant information about a song.  It contains one or more voices, each voice contains one or more measures, 
  and each measure contains one or more measure entities.  
Measure: Measures contain all the information needed to navigate through a song.  This included descriptions of the enclosing bar lines, 
  whether the measure is the start of an alternated ending, links to the previous and next measures, and of course the list of 
  entities.  Players are then able to follow the linked list of measures to properly play repeats and alternate endings.
MeasureEntity: All entities in a measure extend from this allowing for generic storage, processing, and reprocessing.  For example
  tuplets can contain notes and chords, so it simply stores a list of MeasureEntities.  Recursive processing can then be done to parse
  and play back such entities (see toEntity in AbcParser and handleEntity in MidiPlayer).
Player:  The interface defining a single method to load and play a song object.
MidiPlayer: The implementation of the player interface for midi playback.  It knows how to turn a song into a sequence of events
  (note on, note off, and tempo change) and play them back.)
  
Design Notes:
I wanted to seperate each function as cleanly as possible.  As such, none of the model entities or enums know anything about either abc 
notation or how to get a midi pitch.  The goal is to be able to add more parser's and player's that can all interact with each other without 
issue as long as the song object returned by the parser is the same.

Testing:
I wrote one junit test suite that heavily validates how the parser handles notes, chords, tuplets, octaves, and voices.  I also added 
tests that lightly validate and play all of the sample abc files.  It took 763 seconds to run all the tests.

Running:
To play a song, run javac Main.java <absolute_path_to_file>.  This will load the file, parse it into a song, and play it back while printing
status out to the console.

Further work:
If I were to revisit this project, I would add a mock midi player service and expanded the test suite to validate that pitches and
timing information was generated correctly.  I would also implement a simple gui for playing files so that the absolute path didn't have to be
specified by the user.
