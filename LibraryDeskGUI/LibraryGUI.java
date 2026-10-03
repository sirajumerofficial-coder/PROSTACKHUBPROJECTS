import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LibraryGUI extends JFrame {
    private LibraryManagement library = new LibraryManagement();

    // Books tab
    private DefaultTableModel booksModel;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField copiesField;

    // Issue / Return tab
    private JComboBox<Book> bookBox;
    private JTextField studentField;
    private JTextField dateField;
    private JTextField keepDaysField;

    // Issued books tab
    private DefaultTableModel issuedModel;

    // Settings tab
    private JTextField daysField;
    private JTextField fineField;

    public LibraryGUI() {
        setTitle("LibraryDesk - Library Management System");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", buildBooksTab());
        tabs.addTab("Issue / Return", buildIssueTab());
        tabs.addTab("Issued Books", buildIssuedTab());
        tabs.addTab("Settings", buildSettingsTab());
        add(tabs);

        refreshTables();
        setVisible(true);
    }

    // ---------- Tab 1: all books + add book form ----------

    private JPanel buildBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ISBN", "Title", "Author", "Copies available"};
        booksModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(booksModel);
        table.setDefaultEditor(Object.class, null);   // table cells cannot be edited

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Title:"));
        titleField = new JTextField();
        form.add(titleField);

        form.add(new JLabel("Author:"));
        authorField = new JTextField();
        form.add(authorField);

        form.add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        form.add(isbnField);

        form.add(new JLabel("Number of copies:"));
        copiesField = new JTextField();
        form.add(copiesField);

        JButton addButton = new JButton("Add Book");
        form.add(new JLabel(""));
        form.add(addButton);
        addButton.addActionListener(e -> addBook());

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Tab 2: issue and return ----------

    private JPanel buildIssueTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));

        form.add(new JLabel("Select book:"));
        bookBox = new JComboBox<>();
        form.add(bookBox);

        form.add(new JLabel("Student ID:"));
        studentField = new JTextField();
        form.add(studentField);

        form.add(new JLabel("Date (yyyy-mm-dd):"));
        dateField = new JTextField(LocalDate.now().toString());
        form.add(dateField);

        form.add(new JLabel("Days to keep (for Issue):"));
        keepDaysField = new JTextField(String.valueOf(library.getAllowedDays()));
        form.add(keepDaysField);

        JButton issueButton = new JButton("Issue Book");
        JButton returnButton = new JButton("Return Book");
        form.add(issueButton);
        form.add(returnButton);

        issueButton.addActionListener(e -> issueBook());
        returnButton.addActionListener(e -> returnBook());

        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    // ---------- Tab 3: who holds which book ----------

    private JPanel buildIssuedTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Student ID", "Book", "Issue date", "Due date"};
        issuedModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(issuedModel);
        table.setDefaultEditor(Object.class, null);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ---------- Tab 4: late fee rules ----------

    private JPanel buildSettingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        form.add(new JLabel("Default days to keep a book:"));
        daysField = new JTextField(String.valueOf(library.getAllowedDays()));
        form.add(daysField);

        form.add(new JLabel("Fine per late day (Rs.):"));
        fineField = new JTextField(String.valueOf(library.getFinePerDay()));
        form.add(fineField);

        JButton saveButton = new JButton("Save Rules");
        form.add(new JLabel(""));
        form.add(saveButton);
        saveButton.addActionListener(e -> saveRules());

        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    // ---------- Button actions ----------

    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();

        int copies = 0;
        try {
            copies = Integer.parseInt(copiesField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Number of copies must be a whole number.");
            return;
        }

        if (title.isEmpty() || isbn.isEmpty() || copies <= 0) {
            showError("Title, ISBN and a positive number of copies are required.");
            return;
        }

        String message = library.addBook(title, author, isbn, copies);
        JOptionPane.showMessageDialog(this, message);

        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        copiesField.setText("");
        refreshTables();
    }

    private void issueBook() {
        Book book = (Book) bookBox.getSelectedItem();
        String student = studentField.getText().trim();

        if (book == null) {
            showError("No book selected. Add a book first.");
            return;
        }
        if (student.isEmpty()) {
            showError("Please enter the student ID.");
            return;
        }

        LocalDate date = readDate();
        if (date == null) {
            return;
        }

        int days = readDays();
        if (days < 1) {
            return;
        }

        String message = library.issueBook(book.getIsbn(), student, date, days);
        showResult(message);
        refreshTables();
    }

    private void returnBook() {
        Book book = (Book) bookBox.getSelectedItem();
        String student = studentField.getText().trim();

        if (book == null) {
            showError("No book selected. Add a book first.");
            return;
        }
        if (student.isEmpty()) {
            showError("Please enter the student ID.");
            return;
        }

        LocalDate date = readDate();
        if (date == null) {
            return;
        }

        String message = library.returnBook(book.getIsbn(), student, date);
        showResult(message);
        refreshTables();
    }

    private void saveRules() {
        try {
            int days = Integer.parseInt(daysField.getText().trim());
            double fine = Double.parseDouble(fineField.getText().trim());

            if (days < 0 || fine < 0) {
                showError("Values cannot be negative.");
                return;
            }
            library.setRules(days, fine);
            keepDaysField.setText(String.valueOf(days));
            JOptionPane.showMessageDialog(this, "Rules updated.");
            refreshTables();
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers.");
        }
    }

    // ---------- Helper methods ----------

    // Returns the date from the date box, or null if the format is wrong
    private LocalDate readDate() {
        String text = dateField.getText().trim();
        if (text.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ex) {
            showError("Wrong date format. Example: 2026-10-05");
            return null;
        }
    }

    // Returns the number of days from the box, or -1 if it is not valid
    private int readDays() {
        try {
            int days = Integer.parseInt(keepDaysField.getText().trim());
            if (days < 1) {
                showError("Days to keep must be at least 1.");
                return -1;
            }
            return days;
        } catch (NumberFormatException ex) {
            showError("Days to keep must be a whole number.");
            return -1;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showResult(String message) {
        if (message.startsWith("Error")) {
            showError(message);
        } else {
            JOptionPane.showMessageDialog(this, message);
        }
    }

    // Reloads both tables from the library data
    private void refreshTables() {
        // Refill the book dropdown and keep the same selection if possible
        int selected = bookBox.getSelectedIndex();
        bookBox.removeAllItems();
        for (Book b : library.getBooks()) {
            bookBox.addItem(b);
        }
        if (selected >= 0 && selected < bookBox.getItemCount()) {
            bookBox.setSelectedIndex(selected);
        }

        booksModel.setRowCount(0);
        for (Book b : library.getBooks()) {
            booksModel.addRow(new Object[]{
                b.getIsbn(), b.getTitle(), b.getAuthor(), b.getAvailableCopies()
            });
        }

        issuedModel.setRowCount(0);
        for (IssuedBookRecord r : library.getIssuedRecords()) {
            String title = library.getTitleByIsbn(r.getIsbn());
            LocalDate due = r.getDueDate();
            issuedModel.addRow(new Object[]{
                r.getStudentId(), title, r.getIssueDate().toString(), due.toString()
            });
        }
    }
}
