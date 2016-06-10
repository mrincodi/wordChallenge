package gameintext;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class implements a "word tree" for a dictionary.
 * It makes looking for words extremely fast.
 * Will be used by a main MyWordChallenge class.
 * 
 * Currently, running the main class will get a random 6-letter word from the dictionary
 * and will give all the words that have its letters.
 * 
 * To run:
 * 	mkdir -p bin
 *  javac src/gameintext/WordTree.java -d bin/ # Will complain about  unchecked or unsafe operations.
 *  java -classpath bin gameintext.WordTree
 */

/*
 * TODO:
 * - Javadoc
 * - GUI
 * - Tests
 * - Nice publicity. :)
 */

import java.util.Set;

public abstract class WordTree {
	private static char LEAF='+';
	private static String NO_WORD_FOUND="++";
	private static int MIN_WORD_SIZE_DEFAULT=3;	// Just for the main method.
	private static int GAME_WORD_SIZE_DEFAULT=6;	// Just for the main method.

	//TODO: Clean the Spanish "lemario". Take out words with capital letters.
	//TODO: Also (maybe), consider making 'á' equivalent to 'a' and so forth (although I like
	//		to consider them as different letters).
	//FYI: The lemario was downloaded from here:  http://www.teoruiz.com/lemario
	private static String WORDS="etc/lemario-20101017.txt";
	private static int gameWordSize = GAME_WORD_SIZE_DEFAULT;
	private static int minWordSize = MIN_WORD_SIZE_DEFAULT;
	
	static java.util.HashMap < Character, java.util.HashMap > map = new java.util.HashMap <Character, java.util.HashMap >  ();

	//TODO: Instead of an ArrayList with possible words, i should actually have a list of unique possible sets of characters
	// to play with. gameWordSize-sized words with a lot of anagrams have (very slightly) more chance
	// of being chosen.
	static java.util.ArrayList <String > possibleWordsArray = new java.util.ArrayList <String > ();
	
	static java.util.ArrayList < String > words = new java.util.ArrayList <String > ();
	static java.util.ArrayList < String > clues = new java.util.ArrayList <String > ();
	
	static char [] gameLetters; 
	

	public static void initialize (int wordSize) throws IOException{
		setGameWordSize (wordSize);
		
		//Populate the map.
		addWordsFromTextFile ( WORDS );
	
		// Set some letters to play with:
		gameLetters = getRandomWord().toCharArray();
		randomizeChars();

		//Find out the words that have these letters (why wait?).
		getAllWordsWithLetters ();

		//Wonderful. Finally, populate the "clues" array.
		populateClues ();
	}
	
	private static void populateClues() {
		for (int i = 0; i < words.size(); i++){
			String word = words.get(i);
			String stars = "";
			for (int j = 0; j<word.length(); j++){
				stars += "*"; //TODO: There is a way to make this better, right?
			}
			clues.add(stars);
		}
	}

	public static void initialize (int minWordSize, int wordSize) throws IOException{
		setMinWordSize (minWordSize);
		initialize (wordSize);
	}
	
	private static void setMinWordSize(int theMinWordSize) {
		minWordSize=theMinWordSize;
	}

	public static int getGameWordSize (){
		return gameWordSize;
	}
	
	public static void setGameWordSize (int wordSize){
		gameWordSize = wordSize;
	}
	
	public static char [] getGameletters (){
		return gameLetters;
	}
	
	/**
	 * This method adds the words from a file with a list of words to the map.
	 * @param filename
	 * @throws java.io.IOException
	 */
	public static void addWordsFromTextFile ( String filename ) throws java.io.IOException{
		java.io.BufferedReader br = new java.io.BufferedReader(
				   new java.io.InputStreamReader(
		                      new java.io.FileInputStream(filename), "UTF8"));

		String line = br.readLine();
		while (line != null) {
			addWord(line);
			line = br.readLine();
		}

		br.close();
	}

	/**
	 * Adds a word to the map. Also, if the word has the gameWordSize, add it as a
	 * possible word for us to play with.
	 * 
	 * @param word
	 */
	public static void addWord ( String word ){
		addWord ( word, map);
		if ( word.length() == gameWordSize) {
			possibleWordsArray.add(word);
		}
	}

	public static void addWord ( String word, java.util.HashMap < Character, java.util.HashMap > aMap){
		if ( aMap == null ) return ; //TODO: Actually, throw exception.

		if ( word.length() == 0 ) {
			aMap.put ( LEAF, null);
		}
		else {
			char letter = word.charAt(0);
			String decapitatedArray = word.substring(1, word.length()  ); // Lovely name, I know.
			if ( !aMap.containsKey(letter)){
				aMap.put(letter, new java.util.HashMap < Character, java.util.HashMap > ());
			}

			addWord (decapitatedArray, aMap.get(letter));
		}	
	}

	public java.util.ArrayList < String > getAllWords ( ){
		return getAllWords ( "", map);
	}

	private java.util.ArrayList < String > getAllWords ( String carry, java.util.HashMap < Character, java.util.HashMap > aMap){
		java.util.ArrayList < String > returnArrayList = new java.util.ArrayList < String > ();

		for ( char c : aMap.keySet()){
			if ( c == LEAF ) returnArrayList.add(carry);
			else
				returnArrayList.addAll(getAllWords(carry + c, aMap.get(c)));
		}
		return returnArrayList;
	}

	public static void getAllWordsWithLetters (  ){

		//Just sort the string
		char [] charArray = gameLetters.clone();

		java.util.Arrays.sort(charArray);
		String sortedChars =new String ( charArray );

		java.util.ArrayList < String > allWords = getAllWordsWithLetters ("", sortedChars, map );
		java.util.ArrayList < String > rightWords = new java.util.ArrayList < String > (); 
		
		for ( int i = 0; i < allWords.size(); i++){
			String word = allWords.get(i);
			if ( word.length() >= minWordSize ){
				rightWords.add(word);
			}
		}
		words = rightWords;
	}



	/**
	 * Recursive method to find all words with a series of characters.
	 * Has as precondition that the chars are sorted (or 
	 * that repeated chars are contiguous). 
	 * @param sortedChars
	 * @param aMap
	 * @return
	 */

	private static java.util.ArrayList<String> getAllWordsWithLetters(String carry, String sortedChars,
			java.util.HashMap<Character, java.util.HashMap> aMap) {

		java.util.ArrayList<String> wordList = new java.util.ArrayList<String> ();

		//First of all: is the carried word a word? If so, add id to the word list.

		if ( aMap.containsKey(LEAF)) wordList.add(carry);

		//Then, do the method for the leaves:
		for ( int i = 0; i < sortedChars.length (); i++){
			char thisChar=sortedChars.charAt(i);

			//Did I work on that char already?
			//Is this char even there in the HashMap? Continue if not.

			if ( ! (i > 0 && thisChar == sortedChars.charAt(i-1)) && aMap.containsKey(thisChar)){

				String newCarry = carry + thisChar;

				String otherChars = sortedChars.substring(0, i) + sortedChars.substring(i+1, sortedChars.length ());

				wordList.addAll(getAllWordsWithLetters(newCarry, otherChars, aMap.get(thisChar)));

			}
		}
		return wordList;
	}

	/**
	 * Just get a random word from the array of possible words.
	 * @returnThe random word.
	 */
	public static String getRandomWord (){
		java.util.Random rand = new java.util.Random ();
		int pos = rand.nextInt ( possibleWordsArray.size() );

		return possibleWordsArray.get(pos);
	}

	private String getRandomWord(String carry, int size, java.util.HashMap < Character, java.util.HashMap > aMap) {
		if ( size == 0){
			if ( aMap.containsKey(LEAF)) return carry;
			else return NO_WORD_FOUND;
		}

		//Get the keys of the HashMap and randomize their entry.
		Set<Character> keySet = aMap.keySet();

		String letters = "";
		int i = 0;
		for ( char ch : keySet){
			if ( ch != LEAF) letters = letters + ch;
		}

		while ( letters.length() > 0 ){
			//Get a random number from 1 to the size of the string.
			java.util.Random rand = new java.util.Random ();
			int pos = rand.nextInt ( letters.length() );
			char selectedChar = letters.charAt(pos);

			// The partial word is newCarry:
			String newCarry = carry + selectedChar;

			String possibleNewWord = getRandomWord ( newCarry, size - 1, aMap.get(selectedChar));

			if ( possibleNewWord != NO_WORD_FOUND ) return possibleNewWord;

			//Well, that letter didn't work, so go to the next:
			letters = letters.substring(0,pos) + letters.substring(pos+1, letters.length());
		}
		
		//If totally no word found, return NO_WORD_FOUND.
		return NO_WORD_FOUND;
	}

	public static void main ( String [] args ) throws java.io.IOException{
//		WordTree wt = new WordTree ();
//
//		wt.addWordsFromTextFile(WORDS);
//		java.util.ArrayList <String> words; // = wt.getAllWords();
//
//		String word = wt.getRandomWord();
//		System.out.println (word);
//		String word2 = "";
//
//		//words= wt.getAllWords();
//		words = wt.getAllWordsWithLetters ( word );
//		for ( int i = 0; i < words.size(); i++){
//			word2 = words.get(i);
//			if ( word2.length() >= WordTree.MIN_WORD_SIZE )
//			System.out.println ( word2);
//		}


		
	}
	
	/**
	 * This method just randomizes an array of letters.
	 * @param letters
	 * @return The array, randomized.
	 */
	public static void randomizeChars() {
		
		char [] remainingLetters = gameLetters.clone();
		char [] scrambledLetters = new char [ gameLetters.length ];
		
		int i = 0;
		while ( remainingLetters.length > 0 ){
			int newIndex = new java.util.Random().nextInt(remainingLetters.length);
			char letter = remainingLetters [ newIndex ];
			scrambledLetters [ i++ ] = letter;
			
			char [] newRemainingLetters = new char [ remainingLetters.length - 1 ];
			int indexNewRemainingLetters = 0;
			
			for (int indexRemainingLetters = 0; indexRemainingLetters < remainingLetters.length; indexRemainingLetters++){
				if ( indexRemainingLetters != newIndex){
					newRemainingLetters [ indexNewRemainingLetters ] = remainingLetters [ indexRemainingLetters];
					indexNewRemainingLetters++;
				}
			}
			remainingLetters = newRemainingLetters;
		}
		
		gameLetters = scrambledLetters;
	}
//		// TODO Auto-generated method stub
//		char [] newArray = new char [letters.length];
//		
//		java.util.HashSet <Integer > positions = new java.util.HashSet<Integer>();
//		
//		for ( int i = 0; i < letters.length; i++){
//			positions.add(i);
//		}
//		
//		int randomIndexInSet = 0;
//		int randomIndexInWord = 0;
//		int i = 0;
//		while ( positions.size() > 0){
//			//get random number.
//			java.util.Random r = new java.util.Random ();
//			randomIndexInSet = r.nextInt( positions.size());
//			Integer [] availablePositions = positions.toArray(new Integer [positions.size()]);
//			randomIndexInWord = availablePositions [ randomIndexInSet ];
//			
//			newArray [ i++ ] = letters [ randomIndexInWord ];
//			positions.remove(randomIndexInWord);
//		}
//		
//		
//		return null;
//	}

	public static ArrayList<String> getWords() {
		return words;
	}

	public static ArrayList<String> getClues() {
		return clues;
	}

	public static boolean validateWord(String aWord) {
		// Is aWord inside words?
		//TODO: Enhance. The words are sorted lexicographically, so you don't need to get to the end.
		for ( int i = 0; i < words.size(); i++){
			
			if (aWord.compareTo(words.get(i)) == 0){
				clues.set(i, aWord);
				return true;
			}
		}
		return false;
			
	}

}
