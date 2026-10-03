# Java Internship Projects Showcase

A comprehensive repository containing three distinct Java applications built with Object-Oriented Design (OOD), file-based persistence, and desktop GUI / CLI interfaces.

---

## Table of Contents

- [Projects Overview](#projects-overview)
- [Project 1: QuizMaster – Quiz Practice Platform](#project-1-quizmaster--quiz-practice-platform)
  - [Features](#features)
  - [Architecture & Design](#architecture--design)
  - [CSV Data Format](#csv-data-format)
  - [Screenshots](#screenshots)
- [Project 2: GrandStay – Hotel Booking Platform](#project-2-grandstay--hotel-booking-platform)
  - [Features](#features-1)
  - [Architecture & Design](#architecture--design-1)
  - [Room Availability Logic](#room-availability-logic)
  - [Default Room Setup](#default-room-setup)
  - [Screenshots](#screenshots-1)
- [Project 3: LibraryDesk – Library Management System](#project-3-librarydesk--library-management-system)
  - [Features](#features-2)
  - [Architecture & Design](#architecture--design-2)
  - [Late Fee System](#late-fee-system)
  - [Screenshots](#screenshots-2)
- [Global Repository Structure](#global-repository-structure)
- [How to Run Any Project](#how-to-run-any-project)
- [Author](#author)

---

## Projects Overview

| Project Name | Interface Type | Primary Technologies | Core Focus & Functionality |
|---|---|---|---|
| **QuizMaster** | Console (CLI) | Java, CSV I/O, ArrayList | Dynamic quiz engine with category filtering, instant feedback, live scoring, and leaderboard persistence. |
| **GrandStay** | Console (CLI) | Java, `java.time.LocalDate`, File I/O | Hotel reservation platform featuring overlapping booking guards, availability search, and bill generation. |
| **LibraryDesk** | Desktop GUI | Java Swing, `LocalDate`, File I/O | Multi-tab library manager with automatic late fee logic, zero-stock safeguards, and settings panel. |

---

## Project 1: QuizMaster – Quiz Practice Platform

QuizMaster is a console-based quiz engine written in Java. Questions are parsed dynamically from a CSV file (`questions.csv`), eliminating hardcoded data. Users select a category, answer multiple-choice questions, view real-time score updates, and review missed questions upon test completion.

### Features

- **Dynamic Data Parsing:** Loads multiple-choice questions directly from `questions.csv`.
- **Category Filtering:** Choice between Java, Science, and General Knowledge (GK) categories.
- **Live Score Tracking:** Displays instant correct/incorrect feedback alongside the current score after every question.
- **Comprehensive Results Review:** End-of-quiz breakdown showing the final percentage score and missed questions with correct answers.
- **Persistent Leaderboard:** High scores saved to `leaderboard.txt` and sorted by percentage score (Top 10 display).
- **Input Guardrails:** Restricts user input strictly to valid choice options (A, B, C, D).

### Architecture & Design

```text
01-quizmaster/
 ├── Main.java         # Application entry point and menu handler
 ├── Question.java     # Question domain entity
 ├── QuizManager.java  # CSV file parser and category filter
 ├── Quiz.java         # Quiz session controller
 ├── Leaderboard.java  # High-score file persistence and sorter
 ├── questions.csv     # Question bank database
 └── leaderboard.txt   # Persistent leaderboard record
```

| Class Name | Primary Responsibility | Key Fields | Key Methods |
|---|---|---|---|
| `Question` | Holds individual question state | `category`, `text`, `options[]`, `correct` | `isCorrect()`, `getCorrectText()` |
| `QuizManager` | Reads the CSV bank and filters by subject | `ArrayList<Question>` | `loadQuestions()`, `getCategories()`, `getQuestionsByCategory()` |
| `Quiz` | Manages the active quiz runtime and scoring | `score`, `questions`, `missed` | `start()`, `showResults()`, `getPercentage()` |
| `Leaderboard` | High-score record-keeping and sorting | `ArrayList<String[]>` | `addScore()`, `display()`, `load()`, `save()` |
| `Main` | Menu controller and interactive CLI | `Scanner` | `main()`, `playQuiz()` |

### CSV Data Format

Questions must follow this comma-separated structure (1 question per line):

```csv
category,question,optionA,optionB,optionC,optionD,correctLetter
```

> **Note:** Avoid inserting internal commas inside individual question or option strings.

### Screenshots

| Interface Section | Screenshot Reference |
|---|---|
| Main Menu & Categories | <img src="01-quizmaster/screenshots/01-menu-category-question.png" alt="Main menu and categories" width="480"> |
| Question Flow & Feedback | <img src="01-quizmaster/screenshots/02-questions-feedback.png" alt="Question flow and feedback" width="480"> |
| Final Results & Review | <img src="01-quizmaster/screenshots/03-final-results.png" alt="Final results and review" width="480"> |
| Complete Quiz Run | <img src="01-quizmaster/screenshots/04-complete-run.png" alt="Complete quiz run" width="640"> |
| Leaderboard | <img src="01-quizmaster/screenshots/05-leaderboard.png" alt="Leaderboard" width="480"> |

---

## Project 2: GrandStay – Hotel Booking Platform

GrandStay is a console-based hotel management application written in Java. It enables guests and administrators to search available rooms by category and date windows, make non-overlapping room reservations, cancel active bookings, and generate itemized total bills.

### Features

- **Availability Query:** Filter rooms by tier (Standard / Deluxe / Suite) across specified date intervals.
- **Overbooking Prevention:** Algorithms prevent double-booking room instances across overlapping date ranges.
- **Reservation Lifecycle:** Instant reservation cancellation with automatic inventory recovery.
- **Billing Calculator:** Automatic total charge calculation (Price Per Night × Nights Stayed).
- **Automatic State Persistence:** Room states and bookings load/save seamlessly to `.txt` storage.
- **Input Validation:** Date parser validation enforcing `yyyy-MM-dd` standards.

### Architecture & Design

```text
02-grandstay/
 ├── Main.java          # CLI runner and input parser
 ├── Hotel.java         # Core domain engine and file persistence
 ├── Room.java          # Room domain entity
 ├── Reservation.java   # Reservation domain entity
 ├── rooms.txt          # Room inventory storage
 └── reservations.txt   # Reservation registry storage
```

| Class Name | Primary Responsibility | Key Fields | Key Methods |
|---|---|---|---|
| `Room` | Holds room details | `number`, `type`, `price` | Getters |
| `Reservation` | Represents an active booking | `id`, `guestName`, `roomNumber`, `checkIn`, `checkOut` | `getNights()`, `toFileLine()` |
| `Hotel` | System manager & file persistence | `ArrayList<Room>`, `ArrayList<Reservation>`, `nextId` | `searchAvailable()`, `bookRoom()`, `cancelReservation()`, `printBill()`, `isRoomFree()` |
| `Main` | Interactive menu driver | `Scanner`, `Hotel` | `main()`, `readDate()`, `readType()` |

### Room Availability Logic

A room is flagged unavailable if a requested booking date interval overlaps with an existing reservation:

```text
NewCheckIn < ExistingCheckOut   AND   NewCheckOut > ExistingCheckIn
```

> **Note:** This logic permits check-out and check-in transactions to occur on the exact same date without conflict.

### Default Room Setup

| Room Numbers | Category | Daily Rate |
|---|---|---|
| 101, 102, 103 | Standard | Rs. 5,000 |
| 201, 202, 203 | Deluxe | Rs. 8,000 |
| 301, 302 | Suite | Rs. 15,000 |

### Screenshots

| Workflow | Screenshot Reference |
|---|---|
| Room Booking & Restart | <img src="02-grandstay/screenshots/01-booking-and-restart.png" alt="Booking and restart" width="560"> |
| Availability Search (overlap guard), Booking & Bill | <img src="02-grandstay/screenshots/02-search-booking-bill.png" alt="Search, booking and bill" width="560"> |
| Bill Total & All Reservations | <img src="02-grandstay/screenshots/03-bill-and-reservations.png" alt="Bill and all reservations" width="560"> |

---

## Project 3: LibraryDesk – Library Management System

LibraryDesk is a Java Swing desktop GUI application designed for managing library inventories, loan transactions, overdue dates, and fee calculations.

### Features

- **Catalog Management:** Add new books or dynamically increment stock for matching ISBN entries.
- **Issuance Workflow:** Flexible checkout system with custom retention durations.
- **Inventory Safety Lock:** Automatic blockade preventing checkout attempts on zero-copy items.
- **Automated Late Fee Engine:** Calculates overdue fines (Overdue Days × Fine Per Day).
- **Loan Tracking Dashboard:** Tabular display showing active loans, due dates, and student IDs.
- **Custom Settings Panel:** Configuration options for fine rates and default loan durations.
- **File Persistence:** Automatic load/save routines across restarts.

### Architecture & Design

```text
03-librarydesk/
 ├── Main.java               # GUI entry launcher
 ├── LibraryGUI.java         # Tabbed Swing interface
 ├── LibraryManagement.java  # Library logic & state persistence
 ├── Book.java               # Book domain model
 ├── IssuedBookRecord.java   # Active loan model
 ├── books.txt               # Persistent book registry
 ├── issued.txt              # Persistent loans registry
 └── settings.txt            # Persistent configuration file
```

| Class Name | Primary Responsibility | Key Fields | Key Methods |
|---|---|---|---|
| `Book` | Represents an individual book entity | `title`, `author`, `isbn`, `availableCopies` | `issueOneCopy()`, `returnOneCopy()`, `addCopies()` |
| `IssuedBookRecord` | Tracks active book loans | `studentId`, `isbn`, `issueDate`, `dueDate` | Getters, `toFileLine()` |
| `LibraryManagement` | Application logic & persistence | `ArrayList<Book>`, `ArrayList<IssuedBookRecord>`, `allowedDays`, `finePerDay` | `addBook()`, `searchBook()`, `issueBook()`, `returnBook()`, `calculateLateFee()` |
| `LibraryGUI` | Swing window and UI tabs | Tabbed panes, dynamic tables, forms | `buildBooksTab()`, `buildIssueTab()`, `refreshTables()` |
| `Main` | Application main runner | None | `main()` |

### Late Fee System

Late fines are computed based on calendar dates upon book return:

```text
DueDate     = IssueDate + DaysToKeep
LateDays    = ReturnDate - DueDate
TotalFine   = LateDays * FinePerDay    (0 if returned on or before DueDate)
```

### Screenshots

| View / Feature | Screenshot Reference |
|---|---|
| Books Catalog Tab | <img src="03-librarydesk/screenshots/01-books-tab.png" alt="Books catalog tab" width="480"> |
| Issue Workflow | <img src="03-librarydesk/screenshots/02-issue-book.png" alt="Issue workflow" width="480"> |
| Active Loans Dashboard | <img src="03-librarydesk/screenshots/03-issued-books.png" alt="Active loans dashboard" width="480"> |
| Late Fee Calculation | <img src="03-librarydesk/screenshots/04-late-fee.png" alt="Late fee calculation" width="480"> |
| Last Copy Indicator | <img src="03-librarydesk/screenshots/05-last-copy-issued.png" alt="Last copy indicator" width="480"> |
| Out Of Stock Warning | <img src="03-librarydesk/screenshots/06-no-copies.png" alt="Out of stock warning" width="480"> |
| Settings Panel | <img src="03-librarydesk/screenshots/07-settings.png" alt="Settings panel" width="480"> |

---

## Global Repository Structure

```text
.
├── 01-quizmaster/
│   ├── Main.java
│   ├── Question.java
│   ├── QuizManager.java
│   ├── Quiz.java
│   ├── Leaderboard.java
│   ├── questions.csv
│   └── screenshots/
├── 02-grandstay/
│   ├── Main.java
│   ├── Hotel.java
│   ├── Room.java
│   ├── Reservation.java
│   └── screenshots/
├── 03-librarydesk/
│   ├── Main.java
│   ├── LibraryGUI.java
│   ├── LibraryManagement.java
│   ├── Book.java
│   ├── IssuedBookRecord.java
│   └── screenshots/
└── README.md
```

---

## How to Run Any Project

### Prerequisites

- **Java Development Kit (JDK):** Version 8 or higher installed on your system.
- Terminal, Command Prompt, or any Java-compatible IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).

### Compilation & Execution Steps

1. Open your terminal and navigate into the target project folder:

   ```bash
   # Example: Navigating to LibraryDesk
   cd 03-librarydesk
   ```

2. Compile all `.java` files in the directory:

   ```bash
   javac *.java
   ```

3. Launch the application:

   ```bash
   java Main
   ```

> **Date Format Input:** When prompted for dates in any console or GUI form, always input them using the `yyyy-MM-dd` format (e.g., `2026-10-05`).

---

## Author

**Siraj Ul Umer**
