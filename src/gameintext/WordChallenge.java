package gameintext;

import java.io.IOException;

public class WordChallenge {

	public int MIN_SIZE = 3;
	public int MAX_SIZE=6;
		
	
	/**
	 * Primera aproximación. Sólo texto.
	 * @param args
	 * @throws IOException 
	 */
	public static void main ( String [] args ) throws IOException{
		
		// By default, game starts with 6 characters.
		// TODO: Receive number of letters as a parameter.

		//First: get a random word!
		WordTree.initialize(3, 6);
//		
//		String randomWord=WordTree.getRandomWord();
//		
//		char [] letters = randomWord.toCharArray();
//		
//		letters = WordTree.randomizeChars ( letters); 
//		
		boolean allGuessed = false;

		java.util.ArrayList < String > clues = WordTree.getClues ();
		
		while (!allGuessed){
			
			// Mañana: esto debe establecerse al principio de la clase.
			TextScreen.printLetters(WordTree.getGameletters());
		
			//TextScreen.printSomeWords ();
			
			TextScreen.printClues (WordTree.getClues());
			
			String aWord = TextScreen.getWord ();
			
			WordTree.validateWord(aWord);

			
			//WordTree.randomizeChars ();
		}
	}
}
