

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// Testing class to test efficiency of model
public class Hangman{
    public static void main(String[] args) {
        String filename = "sample_1000.txt"; 
        double mistakes = 0;
        Solver solve = new Solver("bigmaps.ser");
        Evidence evidence;
        double lines = 0;
        double length = 0;

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNext()) {
                String word = scanner.next();
                evidence = solve.play_game_print(word);
                if (evidence == null){
                    System.out.println("model failed on " + word);
                    mistakes += word.length();
                    continue;
                }
                mistakes += evidence.getIncorrect().size();
                lines += 1;
                if (lines % 100 == 0){
                    System.out.println("Reached " + lines + " lines");
                }
                length += word.length();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(mistakes/lines + " Average Mistakes");
        System.out.println(length/lines + " Average word Length");

    }



}