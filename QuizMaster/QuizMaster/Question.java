public class Question {
    private String category;
    private String text;
    private String[] options;
    private char correct;

    public Question(String category, String text, String[] options, char correct) {
        this.category = category;
        this.text = text;
        this.options = options;
        this.correct = correct;
    }

    public String getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public char getCorrect() {
        return correct;
    }

    public boolean isCorrect(char answer) {
        return answer == correct;
    }

    public String getCorrectText() {
        int index = correct - 'A';
        return correct + ") " + options[index];
    }
}
