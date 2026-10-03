import java.util.ArrayList;
import java.util.Scanner;

public class Quiz {
    private ArrayList<Question> questions;
    private ArrayList<Question> missed = new ArrayList<>();
    private int score = 0;

    public Quiz(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return questions.size();
    }

    public void start(Scanner sc) {
        int number = 1;

        for (Question q : questions) {
            System.out.println("\nQuestion " + number + " of " + questions.size());
            System.out.println(q.getText());

            String[] options = q.getOptions();
            char label = 'A';
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + label + ") " + options[i]);
                label++;
            }

            char answer = readAnswer(sc);

            if (q.isCorrect(answer)) {
                score++;
                System.out.println("Correct!");
            } else {
                missed.add(q);
                System.out.println("Incorrect. Correct answer: " + q.getCorrectText());
            }

            System.out.println("Current score: " + score + "/" + number);
            number++;
        }
    }

    // Keeps asking until the user types A, B, C or D
    private char readAnswer(Scanner sc) {
        while (true) {
            System.out.print("Your answer (A/B/C/D): ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("A") || input.equals("B") || input.equals("C") || input.equals("D")) {
                return input.charAt(0);
            }
            System.out.println("Invalid input. Type only A, B, C or D.");
        }
    }

    public double getPercentage() {
        return score * 100.0 / questions.size();
    }

    public void showResults() {
        System.out.println("\n========== FINAL RESULTS ==========");
        System.out.println("Score      : " + score + "/" + questions.size());
        System.out.printf("Percentage : %.1f%%\n", getPercentage());

        if (missed.isEmpty()) {
            System.out.println("Perfect score! No missed questions.");
        } else {
            System.out.println("\nMissed questions:");
            for (Question q : missed) {
                System.out.println("- " + q.getText());
                System.out.println("  Correct answer: " + q.getCorrectText());
            }
        }
        System.out.println("===================================");
    }
}
