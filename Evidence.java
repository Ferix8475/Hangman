import java.util.HashSet;
import java.util.Map.Entry;
import java.util.HashMap;
/**
 * Class to represent the current state of Evidence/current game state.
 * 
 * Let WG be the word that is to be guessed, or the goal state
 * 
 * @Attributes:
 * int length: represents the length of the WG, narrows down the search to words of  size WG
 * HashSet correct: The set of correct guesses, unordered
 * HashSet incorrect: The set of incorrect guesses
 * HashMap positions: A mapping of index: character for the current correctly guessed letters.
 * 
 * Supports getters and setters, except a setter for length, as this should remain constant
 * Supports a toString
 */
public class Evidence {
    
    int length;
    HashSet<Character> correct;
    HashSet<Character> incorrect;
    HashMap<Integer, Character> positions;

    public Evidence(int length){
        this.length = length;
        this.correct = new HashSet<>();
        this.incorrect = new HashSet<>();
        this.positions = new HashMap<>();
    }

    public void setIncorrect(char letter){
        this.incorrect.add(letter);
    }

    public void setCorrect(char letter){
        this.correct.add(letter);
    }

    public void setPosition(int position, char letter){
        this.positions.put(position, letter);
    } 

    public HashSet<Character> getCorrect(){
        return this.correct;
    }

    public HashSet<Character> getIncorrect(){
        return this.incorrect;
    }

    public HashMap<Integer, Character> getPositions(){
        return this.positions;
    }

    public int getLength(){
        return this.length;
    }

    public String toString(){

        StringBuilder stringBuilder = new StringBuilder(this.length + 1);
        Character blank = '_';

        for (int idx = 0; idx < this.length; idx++) {
            stringBuilder.append(blank);
        }

        for (int idx = 0; idx < this.length; idx++){
            if (this.positions.containsKey(idx)){
                stringBuilder.setCharAt(idx, this.positions.get(idx));
            } else {
                stringBuilder.setCharAt(idx, blank);
            }
        }
        String positionString = stringBuilder.toString();

        return "Current State: " + positionString + " | Wrong guesses: " + incorrect.toString();
    }

    
}
