import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This file takes a raw csv file of words and their relative frequencies, and turns it into a map of their relative probabilities given a set length of the word.
 * 
 * It also can read and return the map that it stores from the main() function with it's static funciton, get_probabilities_map(filename)
 * 
 * Dataset Citation: 
 * Tatman, R. (2017). English Words Frequency Dataset. Kaggle.
 * Retreived from https://www.kaggle.com/datasets/rtatman/english-word-frequency
 * 
*/

public class FileParser {

    // Retrieves the probability maps that are calculated by the driver code. 
    public static Map<Integer, Map<String, Double>> get_probabilities_map(String filename){

        Map<Integer, Map<String, Double>> probabilities_map = null;

        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)){

                probabilities_map =  (Map<Integer, Map<String, Double>>) in.readObject();


        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

        return probabilities_map;
    }


    // Driver code to calculate the probability hashmaps for each word, sorted by lengths of words

    public static void main(String[] args) {

        Map<Integer, ArrayList<Tuple>> wordMap = new HashMap<>();

        String csv = "unigram_freq.csv";
        String saveFile = "bigmaps.ser";
        String line;
        String delimiter = ",";
        
        // Read CSV line by line to get (word, count), categorize this by the length of word in a hashmap. 
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            while ((line = br.readLine()) != null) {
                String[] l = line.split(delimiter);
                
                if (l.length < 2) {
                    System.out.println("Invalid line: " + line);
                    continue;
                }

                String word = l[0];
                long freq = Long.parseLong(l[1]); 

                int key = word.length();
                Tuple value = new Tuple(word, freq);

                wordMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Turn wordmap into probabilitiesMap by calculating P(W = w) for all w, where W represents all the words of the same length
        Map<Integer, Map<String, Double>> probabilitiesMap = new HashMap<>();
        
        for (Integer key : wordMap.keySet()){

            ArrayList<Tuple> words = wordMap.get(key);
            double denominator = 0;
            probabilitiesMap.computeIfAbsent(key, k -> new HashMap<String, Double>());

            for (Tuple word : words){
                denominator += word.getFrequency();
            }

            for (Tuple word : words){
                Double p = (word.getFrequency() / denominator); 
                probabilitiesMap.get(key).put(word.getWord(), p);
            }

        }

        try (FileOutputStream fileOut = new FileOutputStream(saveFile);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(probabilitiesMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
}