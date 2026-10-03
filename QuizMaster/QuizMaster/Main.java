import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        QuizManager manager = new QuizManager();
        Leaderboard leaderboard = new Leaderboard("leaderboard.txt");

        boolean loaded = manager.loadQuestions("questions.csv");
        if (!loaded) {
            return;
        }

        System.out.println("===== Welcome to QuizMaster =====");
        boolean running = true;

        while (running) {
            System.out.println("\n1. Play Quiz");
            System.out.println("2. View Leaderboard");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                playQuiz(sc, manager, leaderboard);
            } else if (choice.equals("2")) {
                leaderboard.display();
            } else if (choice.equals("3")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }

    private static void playQuiz(Scanner sc, QuizManager manager, Leaderboard leaderboard) {
        ArrayList<String> categories = manager.getCategories();

        System.out.println("\nCategories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }

        int pick = 0;
        while (pick < 1 || pick > categories.size()) {
            System.out.print("Select category number: ");
            try {
                pick = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                pick = 0;
            }
        }
        String category = categories.get(pick - 1);

        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            name = "Player";
        }

        Quiz quiz = new Quiz(manager.getQuestionsByCategory(category));
        quiz.start(sc);
        quiz.showResults();

        leaderboard.addScore(name, category, quiz.getScore(), quiz.getTotal());
        System.out.println("Your score has been saved to the leaderboard.");
    }
}
