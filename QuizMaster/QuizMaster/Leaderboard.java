import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Leaderboard {
    // Each entry is: {name, category, score, total}
    private ArrayList<String[]> entries = new ArrayList<>();
    private String fileName;

    public Leaderboard(String fileName) {
        this.fileName = fileName;
        load();
    }

    private void load() {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    entries.add(parts);
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not load the leaderboard.");
        }
    }

    public void addScore(String name, String category, int score, int total) {
        name = name.replace(",", " ");
        String[] entry = {name, category, String.valueOf(score), String.valueOf(total)};
        entries.add(entry);
        sortEntries();
        save();
    }

    private double percentOf(String[] entry) {
        int score = Integer.parseInt(entry[2]);
        int total = Integer.parseInt(entry[3]);
        return score * 100.0 / total;
    }

    // Bubble sort: highest percentage first
    private void sortEntries() {
        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = 0; j < entries.size() - 1 - i; j++) {
                if (percentOf(entries.get(j)) < percentOf(entries.get(j + 1))) {
                    String[] temp = entries.get(j);
                    entries.set(j, entries.get(j + 1));
                    entries.set(j + 1, temp);
                }
            }
        }
    }

    private void save() {
        try {
            PrintWriter writer = new PrintWriter(fileName);
            for (String[] entry : entries) {
                writer.println(entry[0] + "," + entry[1] + "," + entry[2] + "," + entry[3]);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save the leaderboard.");
        }
    }

    public void display() {
        System.out.println("\n========== LEADERBOARD (Top 10) ==========");

        if (entries.isEmpty()) {
            System.out.println("No scores yet.");
        } else {
            int limit = Math.min(10, entries.size());
            for (int i = 0; i < limit; i++) {
                String[] e = entries.get(i);
                System.out.printf("%d. %s | %s | %s/%s | %.1f%%\n",
                        i + 1, e[0], e[1], e[2], e[3], percentOf(e));
            }
        }
        System.out.println("==========================================");
    }
}
