// Chumeng Zhang
// 11/09/2023
// CSE 122 
// P2: Absurdle

//This class represents a word-guessing game called Absurdle.
//It gives the impression of picking a secret word, but it actually considers
//the entire list of possible word that math the guesses so far.
//Each time the user guess, Absurdle purnes its intenal list minimally as possible.

import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    // [[ ALL OF MAIN PROVIDED ]]
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // [[ PROVIDED ]]
    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // [[ PROVIDED ]]
    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // [[ PROVIDED ]]
    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    // TODO: Write your code here! 

    //pre:
    //1. 'contents' is not null
    //2. 'wordLength' is a int that is greater than 1
    //post:
    //1. if 'wordLength' is than 1, an IllegalArgumentException will be thrown
    //2. Returns a Set<String> containing only words from 'contents' that have a length equal to 'wordLength'
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if(wordLength < 1){
            throw new IllegalArgumentException();
        }
        Set<String> prunedSet = new TreeSet<>();
        for(String stringInTheContents : contents){
            if(stringInTheContents.length() == wordLength){
                prunedSet.add(stringInTheContents);
            }
        }
        return prunedSet;
    }

    //pre:
    //1. 'guess' has the same length as 'wordLength'
    //2. 'words' is not is not is not empty
    //3. 'wordLength' should be greater than 1
    //post:
    //1. Returns a String that represents the best pattern that corresponds to the largest set of words still remaining
    //2. Updates the 'words' set to only include words that match the best pattern
    //3. An IllegalArgumentException will be thrown if the length of 'guess' is not equal to 'wordLength'
    //4. An IllegalArgumentException will be thrown if 'words' set is empty
    public static String record(String guess, Set<String> words, int wordLength) {
        if(guess.length() != wordLength || words.isEmpty()){
            throw new IllegalArgumentException();
        }
        Map<String, Integer> mapOfPatterns = new TreeMap<>();
        int currentCount;
        for(String targetWord: words){
            String currentPattern = patternFor(targetWord, guess);
            if(mapOfPatterns.getOrDefault(currentPattern, 0) != 0){
                currentCount = mapOfPatterns.get(currentPattern);
            }else{
                currentCount = 0;
            }
            mapOfPatterns.put(currentPattern, currentCount + 1);
        }
        
        int maxCount = 0;
        String mostPattern ="";
        for(String keys : mapOfPatterns.keySet()){
            if(mapOfPatterns.get(keys) > maxCount){
                mostPattern = keys;
                maxCount = mapOfPatterns.get(keys);
            }
        }
        
        Set<String> updatedWords = new TreeSet<>();
       
        for(String target: words){
            String currentPattern1 = patternFor(target, guess);
            if(mostPattern.equals(currentPattern1)){
                updatedWords.add(target);
            }
        }
        words.clear();
        words.addAll(updatedWords);
        return mostPattern;
    }

    //pre:
    //1. 'word' and 'guess' are not null, and both have the same length
    //post:
    //1. Returns a String representing the pattern of matches and mismatches between the characters in word and guess. 
    //   The pattern includes GREEN for correct letters in the correct position, 
    //   YELLOW for correct letters in the wrong position, 
    //   and GRAY for incorrect letters.
    public static String patternFor(String word, String guess) {
        List<String> listOfGuess = new ArrayList<>();
        Map<String, Integer> letterCounter = new TreeMap<>();
        
        for(int i = 0; i < guess.length(); i++){
            String letter = guess.substring(i, i+1);
            listOfGuess.add(letter);
        }
        
        for(int j = 0; j < listOfGuess.size(); j++){
            letterCounter.put(word.substring(j, j+1), 0);
        }

        for(int h = 0; h < listOfGuess.size(); h++){
            int count = letterCounter.get(word.substring(h, h+1)) + 1;
            letterCounter.put(word.substring(h, h+1), count);
        }

        for(int k = 0; k < listOfGuess.size(); k++){
            String current = listOfGuess.get(k);
            if(current.equals(word.substring(k, k+1))){
                setColor(k, GREEN, letterCounter, listOfGuess);
            }
        }
        
        for(int l = 0; l < listOfGuess.size(); l++){
            if(!listOfGuess.get(l).equals(GREEN) && letterCounter.getOrDefault((listOfGuess.get(l)), 0) != 0){
                int updatedCount1 = letterCounter.get(listOfGuess.get(l));
                String currentLetter = listOfGuess.get(l);
                if(word.contains(currentLetter) && updatedCount1 > 0){
                    setColor(l, YELLOW, letterCounter, listOfGuess);
                }
            }
        }
        
        for(int n = 0; n < listOfGuess.size(); n++){
            if(!(listOfGuess.get(n) == GREEN) && !(listOfGuess.get(n) == YELLOW)){
                listOfGuess.set(n, GRAY);
            }
        }
        
        String stringToBeReturned = "";
        for(int m = 0; m < listOfGuess.size(); m++){
            String currentString = listOfGuess.get(m);
            stringToBeReturned += currentString;
        }
        return stringToBeReturned;
    }

    //pre:
    //1. position is valid index for strings in set 'guess'
    //2. 'color' is a String that represents a GREEN, YELLOW or GRAY emoji.
    //3. 'etterCounter' and 'guess' are not null.
    //post:
    //1. Sets the color of the element at the specified position in set 'guess' to the given color
    //2. Update the letter count in 'letterCounter' accordingly   
    public static void setColor(int position, String color, Map<String, Integer> letterCounter, List<String> guess){
        int updatedCount = letterCounter.get(guess.get(position)) - 1;
        letterCounter.put(guess.get(position), updatedCount);
        guess.set(position, color);
    }
}
