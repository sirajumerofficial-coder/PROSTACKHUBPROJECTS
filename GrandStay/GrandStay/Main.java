import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static Hotel hotel = new Hotel();

    public static void main(String[] args) {
        System.out.println("===== Welcome to GrandStay Hotel =====");
        boolean running = true;

        while (running) {
            System.out.println("\n1. Search available rooms");
            System.out.println("2. Book a room");
            System.out.println("3. Cancel a reservation");
            System.out.println("4. Generate bill");
            System.out.println("5. Show all reservations");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                searchRooms();
            } else if (choice.equals("2")) {
                bookRoom();
            } else if (choice.equals("3")) {
                cancelReservation();
            } else if (choice.equals("4")) {
                showBill();
            } else if (choice.equals("5")) {
                hotel.showAllReservations();
            } else if (choice.equals("6")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }

    // ---------- Input helpers ----------

    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }

    static String readType() {
        while (true) {
            System.out.print("Room type (Standard/Deluxe/Suite): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("standard")) {
                return "Standard";
            } else if (input.equalsIgnoreCase("deluxe")) {
                return "Deluxe";
            } else if (input.equalsIgnoreCase("suite")) {
                return "Suite";
            }
            System.out.println("Invalid room type.");
        }
    }

    static LocalDate readDate(String label) {
        while (true) {
            System.out.print(label + " (yyyy-mm-dd): ");
            try {
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Wrong date format. Example: 2026-10-15");
            }
        }
    }

    // Check-out must be after check-in
    static LocalDate readCheckOut(LocalDate checkIn) {
        LocalDate checkOut = readDate("Check-out date");
        while (!checkOut.isAfter(checkIn)) {
            System.out.println("Check-out must be after check-in.");
            checkOut = readDate("Check-out date");
        }
        return checkOut;
    }

    static void showRooms(ArrayList<Room> list) {
        System.out.println("\nAvailable rooms:");
        for (Room r : list) {
            System.out.println("Room " + r.getNumber() + " | " + r.getType()
                    + " | Rs. " + r.getPrice() + " per night");
        }
    }

    // ---------- Menu actions ----------

    static void searchRooms() {
        String type = readType();
        LocalDate checkIn = readDate("Check-in date");
        LocalDate checkOut = readCheckOut(checkIn);

        ArrayList<Room> freeRooms = hotel.searchAvailable(type, checkIn, checkOut);

        if (freeRooms.isEmpty()) {
            System.out.println("No " + type + " rooms are available for these dates.");
        } else {
            showRooms(freeRooms);
        }
    }

    static void bookRoom() {
        String type = readType();
        LocalDate checkIn = readDate("Check-in date");
        LocalDate checkOut = readCheckOut(checkIn);

        ArrayList<Room> freeRooms = hotel.searchAvailable(type, checkIn, checkOut);

        if (freeRooms.isEmpty()) {
            System.out.println("No " + type + " rooms are available for these dates.");
            return;
        }
        showRooms(freeRooms);

        System.out.print("Enter room number to book: ");
        int roomNumber = readInt();

        System.out.print("Guest name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            name = "Guest";
        }

        Reservation reservation = hotel.bookRoom(name, roomNumber, checkIn, checkOut);

        if (reservation == null) {
            System.out.println("Booking failed. The room number is wrong or the room is not available.");
        } else {
            System.out.println("Booking confirmed! Your reservation ID is " + reservation.getId());
        }
    }

    static void cancelReservation() {
        System.out.print("Enter reservation ID to cancel: ");
        int id = readInt();

        if (hotel.cancelReservation(id)) {
            System.out.println("Reservation cancelled. The room is available again.");
        } else {
            System.out.println("No reservation found with this ID.");
        }
    }

    static void showBill() {
        System.out.print("Enter reservation ID: ");
        int id = readInt();
        hotel.printBill(id);
    }
}
