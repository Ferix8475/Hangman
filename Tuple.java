/**
 * Class to represent a tuple of (word, frequency) as in the csv file.
 * 
 * @Attributes:
 * String word: the word in the tuple.
 * long frequency: the number of times 'word' shows up in a large dataset of words.
 * 
 * Support getters for these two values, no setters, as it is a tuple and maintains immutability.
 */

public class Tuple implements java.io.Serializable {
    String word;
    long frequency;

    public Tuple(String word, long frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public long getFrequency(){
        return this.frequency;
    }

    public String getWord(){
        return this.word;
    }
}