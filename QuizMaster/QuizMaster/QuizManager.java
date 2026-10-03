import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class QuizManager {
    private ArrayList<Question> allQuestions = new ArrayList<>();

    // Reads every line of the CSV file and creates Question objects
    public boolean loadQuestions(String fileName) {
        try {
            Scanner fileScanner = new Scanner(new File(fileName));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length != 7) {
                    continue;
                }

                String category = parts[0].trim();
                String text = parts[1].trim();
                String[] options = {parts[2].trim(), parts[3].trim(),
                                    parts[4].trim(), parts[5].trim()};
                char correct = parts[6].trim().toUpperCase().charAt(0);

                allQuestions.add(new Question(category, text, options, correct));
            }
            fileScanner.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + fileName + " file not found.");
            return false;
        }
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<>();
        for (Question q : allQuestions) {
            if (!categories.contains(q.getCategory())) {
                categories.add(q.getCategory());
            }
        }
        return categories;
    }

    public ArrayList<Question> getQuestionsByCategory(String category) {
        ArrayList<Question> result = new ArrayList<>();
        for (Question q : allQuestions) {
            if (q.getCategory().equals(category)) {
                result.add(q);
            }
        }
        return result;
    }
}
