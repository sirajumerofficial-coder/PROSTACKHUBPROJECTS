public class Book {
    private String title;
    private String author;
    private String isbn;
    private int availableCopies;

    public Book(String title, String author, String isbn, int availableCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.availableCopies = availableCopies;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    // This text is what the dropdown list shows
    public String toString() {
        return isbn + " - " + title + " (" + availableCopies + " available)";
    }

    public void addCopies(int count) {
        availableCopies = availableCopies + count;
    }

    public void issueOneCopy() {
        availableCopies--;
    }

    public void returnOneCopy() {
        availableCopies++;
    }
}
