import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

public class LibraryManagement {
    // All books in the library, and all books currently issued to students
    private ArrayList<Book> listOfBooks = new ArrayList<>();
    private ArrayList<IssuedBookRecord> listOfIssuedBooks = new ArrayList<>();

    // Late fee rules (can be changed from the Settings tab)
    private int allowedDays = 7;
    private double finePerDay = 10.0;

    private String booksFile = "books.txt";
    private String issuedFile = "issued.txt";
    private String settingsFile = "settings.txt";

    public LibraryManagement() {
        loadSettings();
        loadBooks();
        loadIssued();
    }

    public int getAllowedDays() {
        return allowedDays;
    }

    public double getFinePerDay() {
        return finePerDay;
    }

    public ArrayList<Book> getBooks() {
        return listOfBooks;
    }

    public ArrayList<IssuedBookRecord> getIssuedRecords() {
        return listOfIssuedBooks;
    }

    public void setRules(int allowedDays, double finePerDay) {
        this.allowedDays = allowedDays;
        this.finePerDay = finePerDay;
        saveSettings();
    }

    // ---------- Loading from files ----------

    private void loadSettings() {
        File file = new File(settingsFile);
        if (!file.exists()) {
            return;
        }
        try {
            Scanner fileScanner = new Scanner(file);
            if (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(",");
                if (parts.length == 2) {
                    allowedDays = Integer.parseInt(parts[0].trim());
                    finePerDay = Double.parseDouble(parts[1].trim());
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read " + settingsFile);
        }
    }

    private void loadBooks() {
        File file = new File(booksFile);
        if (!file.exists()) {
            return;
        }
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(",");
                if (parts.length != 4) {
                    continue;
                }
                String isbn = parts[0].trim();
                String title = parts[1].trim();
                String author = parts[2].trim();
                int copies = Integer.parseInt(parts[3].trim());
                listOfBooks.add(new Book(title, author, isbn, copies));
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read " + booksFile);
        }
    }

    private void loadIssued() {
        File file = new File(issuedFile);
        if (!file.exists()) {
            return;
        }
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(",");
                if (parts.length != 3 && parts.length != 4) {
                    continue;
                }
                String studentId = parts[0].trim();
                String isbn = parts[1].trim();
                LocalDate issueDate = LocalDate.parse(parts[2].trim());
                LocalDate dueDate = issueDate.plusDays(allowedDays);
                if (parts.length == 4) {
                    dueDate = LocalDate.parse(parts[3].trim());
                }
                listOfIssuedBooks.add(new IssuedBookRecord(studentId, isbn, issueDate, dueDate));
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read " + issuedFile);
        }
    }

    // ---------- Saving to files ----------

    private void saveSettings() {
        try {
            PrintWriter writer = new PrintWriter(settingsFile);
            writer.println(allowedDays + "," + finePerDay);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save " + settingsFile);
        }
    }

    private void saveBooks() {
        try {
            PrintWriter writer = new PrintWriter(booksFile);
            for (Book b : listOfBooks) {
                writer.println(b.getIsbn() + "," + b.getTitle() + ","
                        + b.getAuthor() + "," + b.getAvailableCopies());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save " + booksFile);
        }
    }

    private void saveIssued() {
        try {
            PrintWriter writer = new PrintWriter(issuedFile);
            for (IssuedBookRecord r : listOfIssuedBooks) {
                writer.println(r.toFileLine());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save " + issuedFile);
        }
    }

    // ---------- Library features ----------
    // These methods return a message. A message starting with "Error" means it failed.

    private Book findByIsbn(String isbn) {
        for (Book b : listOfBooks) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {
                return b;
            }
        }
        return null;
    }

    public String getTitleByIsbn(String isbn) {
        Book b = findByIsbn(isbn);
        if (b == null) {
            return isbn;
        }
        return b.getTitle();
    }

    public String addBook(String title, String author, String isbn, int copies) {
        title = title.replace(",", " ");
        author = author.replace(",", " ");
        isbn = isbn.replace(",", " ");

        Book existing = findByIsbn(isbn);
        String message;
        if (existing != null) {
            existing.addCopies(copies);
            message = "This ISBN already exists. Added " + copies + " more copies.";
        } else {
            listOfBooks.add(new Book(title, author, isbn, copies));
            message = "Book added to the library.";
        }
        saveBooks();
        return message;
    }

    // Search by ISBN or title
    public Book searchBook(String pickBook) {
        for (Book book : listOfBooks) {
            if (book.getIsbn().equalsIgnoreCase(pickBook)
                    || book.getTitle().equalsIgnoreCase(pickBook)) {
                return book;
            }
        }
        return null;
    }

    public String issueBook(String pickBook, String studentId, LocalDate issueDate, int daysToKeep) {
        Book bookToIssue = searchBook(pickBook);

        if (bookToIssue == null) {
            return "Error: Book not found in the library.";
        }

        // Requirement: block issuing when no copies are left
        if (bookToIssue.getAvailableCopies() <= 0) {
            return "Error: No copies of this book are available.";
        }

        if (daysToKeep < 1) {
            return "Error: Days to keep must be at least 1.";
        }

        studentId = studentId.replace(",", " ");
        LocalDate dueDate = issueDate.plusDays(daysToKeep);
        bookToIssue.issueOneCopy();
        listOfIssuedBooks.add(new IssuedBookRecord(studentId, bookToIssue.getIsbn(), issueDate, dueDate));

        saveBooks();
        saveIssued();

        return "Book issued to " + studentId + ".\nDays to keep: " + daysToKeep
                + "\nDue date: " + dueDate;
    }

    public String returnBook(String pickBook, String studentId, LocalDate returnDate) {
        Book bookToReturn = searchBook(pickBook);

        if (bookToReturn == null) {
            return "Error: Book does not belong to this library.";
        }

        // Find the record of this student holding this book
        IssuedBookRecord recordToRemove = null;
        for (IssuedBookRecord record : listOfIssuedBooks) {
            if (record.getStudentId().equalsIgnoreCase(studentId)
                    && record.getIsbn().equalsIgnoreCase(bookToReturn.getIsbn())) {
                recordToRemove = record;
                break;
            }
        }

        if (recordToRemove == null) {
            return "Error: No issue record found for this student and book.";
        }

        if (returnDate.isBefore(recordToRemove.getIssueDate())) {
            return "Error: Return date cannot be before the issue date.";
        }

        int daysKept = (int) ChronoUnit.DAYS.between(recordToRemove.getIssueDate(), returnDate);
        int lateDays = (int) ChronoUnit.DAYS.between(recordToRemove.getDueDate(), returnDate);
        double fee = calculateLateFee(lateDays);

        bookToReturn.returnOneCopy();
        listOfIssuedBooks.remove(recordToRemove);
        saveBooks();
        saveIssued();

        String message = "Book returned successfully.\nDays kept: " + daysKept
                + "\nDue date was: " + recordToRemove.getDueDate();
        if (fee > 0) {
            message = message + "\nLate days: " + lateDays + "\nLate fee: Rs. " + fee;
        } else {
            message = message + "\nReturned on time. No late fee.";
        }
        return message;
    }

    // lateDays = number of days after the due date (0 or less means on time)
    public double calculateLateFee(int lateDays) {
        if (lateDays > 0) {
            return lateDays * finePerDay;
        }
        return 0.0;
    }
}
