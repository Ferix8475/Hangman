
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 
 * Solver Class: Runs simulations of hangman game given a certain word. Create an object based on a serialized map of probabilities. 
 * 
 * @METHODS:
 * 
 * Constructors: 
 * Supports a default constructor which reads from a precomputed map (recommended), or if you would like can also read from
 * a different serialized file. That constructor accepts a String filename as a parameter
 * 
 * public int word_fits_evidence(Evidence evidence, String word): 
 * Checks if a word is valid given the evidence. See Evidence.java for details on the Evidence class. 
 * 
 * public HashMap<String, Double> calculate_posteriors(Evidence evidence, HashMap<String, Double> probabilities):
 * Calculates the posterior probabilities given the current state of evidence and using the probability map probabilities. This is essential in calculating the 
 * predictive probabilities, which will dictate decision making. 
 * 
 * public double calculate_predictive_probabilities(Character letter, Evidence evidence, HashMap<String, Double> probabilities):
 * Calculates the predictive probability for each lower case letter in the alphabet, or the probability that the letter will show up given the current state of evidence
 * 
 * public boolean step(Evidence evidence, HashMap<String, Double> probabilities, String to_guess):
 * Takes a step in the game. Picks the best letter given the current state of evidence, and guesses it. Then it modifies the evidence based on the true values of he word.
 * The best letter is chosen by the highest predictive probability. 
 * 
 * public Evidence play_game_print(String word):
 * Simulates a whole game with the model given the secret word. This prints out the current state of the game step by step. 
 * Returns the final state of evidence, which can be used for analysis. 
 * 
 */ 
public class Solver {

    private final String FILENAME = "bigmaps.ser";
    Map<Integer, Map<String, Double>> probability_maps;

    // Default Solver Constructor, fetches probability maps from precomputed probabilities_map.ser, see FileParser.java
    public Solver(){
        this.probability_maps = FileParser.get_probabilities_map(FILENAME);
    }




    // Default Solver Constructor, fetches probability maps from precomputed probabilities_map.ser, see FileParser.java
    public Solver(String filename){
        this.probability_maps = FileParser.get_probabilities_map(filename);
    }




    // Check if a word works given the evidence, P(W = w|E) = {1, 0} for true and false.
    public int word_fits_evidence(Evidence evidence, String word) {
    
        HashMap<Integer, Character> positions = evidence.getPositions();
        HashSet<Character> correct = evidence.getCorrect();
        HashSet<Character> incorrect = evidence.getIncorrect();
    
        // Check if a letter from `incorrect` set is present in the word
        for (Character letter : incorrect) {
            if (word.contains(String.valueOf(letter))) {
                return 0; 
            }
        }
    
        // Check if the word matches the known correct positions
        for (int idx = 0; idx < word.length(); idx++) {
            if (positions.containsKey(idx)) {
                // If the position is revealed, the word must match the character at that position
                if (positions.get(idx) != word.charAt(idx)) {
                    return 0;
                }
            } else {
                // If the position is not revealed, it shouldn't contain a known correct letter
                if (correct.contains(word.charAt(idx))) {
                    return 0;
                }
            }
        }
    
        return 1; // Word fits
    }



    // Calculates posterior probabilities given the current state of evidence. 
    public HashMap<String, Double> calculate_posteriors(Evidence evidence, HashMap<String, Double> probabilities){

        Set<String> words = probabilities.keySet();
        ArrayList<String> numerator_words = new ArrayList<String>();
        HashMap<String, Double> posteriors = new HashMap<String, Double>();
        double denominator = 0;

        // A word will contribute to the posterior probability's denominator if it fits the current evidence. 
        for (String word : words){
            int fit = word_fits_evidence(evidence, word);
            if (fit == 0){
                continue;
            } else {
                numerator_words.add(word);
                denominator += probabilities.get(word);
            }
        }

        if (denominator == 0){ return posteriors;}
        // Calculate the posterior probabilities and return the map. 
        for (String word : numerator_words){
            posteriors.put(word, probabilities.get(word));
        }

        return posteriors;

    }




    // Calculates the predictive probabilities given the current state of evidence. 
    public double calculate_predictive_probabilities(Character letter, Evidence evidence, HashMap<String, Double> probabilities){
        
        HashMap<String, Double> posteriors = calculate_posteriors(evidence, probabilities);
        if (posteriors.size() == 0){ return 0; }

        CharSequence l = String.valueOf(letter);
        double res = 0; 

        // For each letter, calculate a predictive probability of how likely, if guessed, will show up. 
        for (String word : posteriors.keySet()){
            if (word.contains(l)){
                res += probabilities.get(word);
            }
        }

        return res;

    }


    // A step in the game, wherein the Solver takes a guess of a letter, and modifies the evidence based on the guess. 
    public boolean step(Evidence evidence, HashMap<String, Double> probabilities, String to_guess){


        Character guess = null;
        double best_probability =  0; 
        HashSet<Character> correct = evidence.getCorrect();
        HashSet<Character> incorrect = evidence.getIncorrect();
        
        // Get the best guess possible by picking the best predictive probability of all valid letters
        for (char letter = 'a'; letter <= 'z'; letter++) {
            // Skip letters already guessed incorrectly or correctly
            if (correct.contains(letter) || incorrect.contains(letter)) {
                continue;
            }
    
            double current_probability = calculate_predictive_probabilities(letter, evidence, probabilities);
            
            // Update the guess if this letter has a higher probability
            if (current_probability > best_probability) {
                guess = letter;
                best_probability = current_probability;
            }
        }

        // No guesses have been found, terminate the game
        if (guess == null){ return false;}

        // Modify the evidence with the new guess, see what letters are revealed
        boolean changed = false;
        for (int idx = 0; idx < to_guess.length(); idx++){
            Character current_character = to_guess.charAt(idx);
            if (current_character == guess){
                evidence.setPosition(idx, guess);
                changed = true;
            }
        }
         
        // Add guess to the proper set
        if (changed){
            evidence.setCorrect(guess);
        } else {
            evidence.setIncorrect(guess);
        }

        // Whole step went through without trouble, return true
        return true;
        

    }

    // Simulate a whole game with a given word. 
    public Evidence play_game_print(String word){
        
        // Initialize the evidence
        Evidence evidence = new Evidence(word.length());
        HashMap<String, Double> probability_map = (HashMap<String, Double>) this.probability_maps.get(word.length());

        // Run Game simulation
        while (true){
            boolean end = step(evidence, probability_map, word);
            if (!end) { 
                //System.out.println("Game ended, no more guesses");
                System.out.println(evidence.toString());
                return null;
            }
            if (evidence.getPositions().keySet().size() == word.length()){
                System.out.println(evidence.toString());
                return evidence;
            }
            System.out.println(evidence.toString());

        }

    }


}

