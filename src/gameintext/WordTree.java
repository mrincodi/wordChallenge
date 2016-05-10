package gameintext;

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

public class WordTree {
	private char LEAF='+';
	private String NO_WORD_FOUND="++";
	public static int MIN_WORD_SIZE=3;	// Just for the main method.
	public static int GAME_WORD_SIZE=6;	// Just for the main method.

	//TODO: Clean the Spanish "lemario". Take out words with capital letters.
	//TODO: Also (maybe), consider making 'á' equivalent to 'a' and so forth (although I like
	//		to consider them as different letters).
	//FYI: The lemario was downloaded from here:  http://www.teoruiz.com/lemario
	static String WORDS="etc/lemario-20101017.txt";
	java.util.HashMap < Character, java.util.HashMap > map;

	java.util.ArrayList <String > possibleWordsArray = new java.util.ArrayList <String > ();
	
	public WordTree (){
		map = new java.util.HashMap <Character, java.util.HashMap >  ();
	}

	public void addWordsFromTextFile ( String filename ) throws java.io.IOException{
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

	public void addWords ( String [] words){
		for ( int i = 0; i < words.length; i++ ){
			addWord (  words [ i ]);			
		}
	}

	public void addWord ( String word ){
		addWord ( word, map);
		if ( word.length() == GAME_WORD_SIZE) {
			possibleWordsArray.add(word);
		}
	}

	public void addWord ( String word, java.util.HashMap < Character, java.util.HashMap > aMap){
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

	public java.util.ArrayList < String > getAllWordsWithLetters ( String chars ){

		//Just sort the string
		char [] charArray = chars.toCharArray();
		java.util.Arrays.sort(charArray);
		String sortedChars =new String ( charArray );

		return getAllWordsWithLetters ("", sortedChars, map );
	}



	/**
	 * Recursive method to find all words with a series of characters.
	 * Has as precondition that the chars are sorted (or 
	 * that repeated chars are contiguous). 
	 * @param sortedChars
	 * @param aMap
	 * @return
	 */

	private java.util.ArrayList<String> getAllWordsWithLetters(String carry, String sortedChars,
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

	public String getRandomWord (){

		java.util.Random rand = new java.util.Random ();
		int pos = rand.nextInt ( possibleWordsArray.size() );

		return possibleWordsArray.get(pos);
		//return getRandomWord ("", WordTree.GAME_WORD_SIZE, map);
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
		WordTree wt = new WordTree ();

		wt.addWordsFromTextFile(WORDS);
		java.util.ArrayList <String> words; // = wt.getAllWords();

		String word = wt.getRandomWord();
		System.out.println (word);
		String word2 = "";

		//words= wt.getAllWords();
		words = wt.getAllWordsWithLetters ( word );
		for ( int i = 0; i < words.size(); i++){
			word2 = words.get(i);
			if ( word2.length() >= WordTree.MIN_WORD_SIZE )
			System.out.println ( word2);
		}

	}

}
