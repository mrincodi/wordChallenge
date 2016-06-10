package gameintext;

import java.io.IOException;
import java.util.ArrayList;

public abstract class TextScreen {

	public static void printLetters ( char [] letters ){
		System.out.print("Available letters...... ");
		for ( int i = 0; i < letters.length; i++){
			System.out.print(letters [ i ] + " ");
		}
		System.out.println();
	}

	
	public static void printClues(ArrayList<String> clues) {
		// TODO Split into several columns!
		for ( int i = 0; i < clues.size(); i++){
			System.out.println(clues.get(i));
		}
	}

	public static String getWord() throws IOException {
		System.out.print("Palabra: ");
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		String aWord = in.readLine();
		return aWord;
	}
	
	/**
	 * As a precondition, both arrays are sorted.
	 * This method doesn't check that the list of someWords is accurate.
	 * @param allWords
	 * @param someWords
	 */
	/*
	public static void printSomeWords (java.util.ArrayList <String> allWords, java.util.ArrayList <String> someWords ){
		int j = 0;
		for (int i = 0; i < allWords.size(); i++ ){
			String word = allWords.get(i);
			
			if ( someWords.contains(word)){
				System.out.println(word);
			}
			else {
				System.out.println("Blah!");
			}
				
//			boolean go = true;
//			while ( go ){
//				
//				if (someWords.get(j).compareTo(allWords.get(i))==0){
//					System.out.println(x);
//					
//				}
			}
		}
	}
	*/
	
}
