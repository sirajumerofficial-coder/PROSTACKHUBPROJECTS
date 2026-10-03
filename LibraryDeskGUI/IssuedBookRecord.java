import java.time.LocalDate;

public class IssuedBookRecord {
    private String studentId;
    private String isbn;
    private LocalDate issueDate;
    private LocalDate dueDate;

    public IssuedBookRecord(String studentId, String isbn, LocalDate issueDate, LocalDate dueDate) {
        this.studentId = studentId;
        this.isbn = isbn;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getIsbn() {
        return isbn;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // One line of text to store this record in the file
    public String toFileLine() {
        return studentId + "," + isbn + "," + issueDate + "," + dueDate;
    }
}
