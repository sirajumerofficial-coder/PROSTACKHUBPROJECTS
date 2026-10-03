import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Hotel {
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private int nextId = 1;

    private String roomsFile = "rooms.txt";
    private String reservationsFile = "reservations.txt";

    public Hotel() {
        loadRooms();
        loadReservations();
    }

    // ---------- Loading from files ----------

    private void loadRooms() {
        File file = new File(roomsFile);

        // First run: create default rooms and save them
        if (!file.exists()) {
            rooms.add(new Room(101, "Standard", 5000));
            rooms.add(new Room(102, "Standard", 5000));
            rooms.add(new Room(103, "Standard", 5000));
            rooms.add(new Room(201, "Deluxe", 8000));
            rooms.add(new Room(202, "Deluxe", 8000));
            rooms.add(new Room(203, "Deluxe", 8000));
            rooms.add(new Room(301, "Suite", 15000));
            rooms.add(new Room(302, "Suite", 15000));
            saveRooms();
            return;
        }

        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue;
                }
                int number = Integer.parseInt(parts[0].trim());
                String type = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                rooms.add(new Room(number, type, price));
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read " + roomsFile);
        }
    }

    private void loadReservations() {
        File file = new File(reservationsFile);
        if (!file.exists()) {
            return;
        }

        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length != 5) {
                    continue;
                }
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                int roomNumber = Integer.parseInt(parts[2].trim());
                LocalDate checkIn = LocalDate.parse(parts[3].trim());
                LocalDate checkOut = LocalDate.parse(parts[4].trim());

                reservations.add(new Reservation(id, name, roomNumber, checkIn, checkOut));

                // Make sure new IDs continue after the highest old ID
                if (id >= nextId) {
                    nextId = id + 1;
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read " + reservationsFile);
        }
    }

    // ---------- Saving to files ----------

    private void saveRooms() {
        try {
            PrintWriter writer = new PrintWriter(roomsFile);
            for (Room r : rooms) {
                writer.println(r.getNumber() + "," + r.getType() + "," + r.getPrice());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save " + roomsFile);
        }
    }

    private void saveReservations() {
        try {
            PrintWriter writer = new PrintWriter(reservationsFile);
            for (Reservation r : reservations) {
                writer.println(r.toFileLine());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save " + reservationsFile);
        }
    }

    // ---------- Helper methods ----------

    public Room findRoom(int number) {
        for (Room r : rooms) {
            if (r.getNumber() == number) {
                return r;
            }
        }
        return null;
    }

    public Reservation findReservation(int id) {
        for (Reservation r : reservations) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    // A room is free if no existing booking overlaps the new dates
    public boolean isRoomFree(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation r : reservations) {
            if (r.getRoomNumber() == roomNumber) {
                boolean overlap = checkIn.isBefore(r.getCheckOut())
                               && checkOut.isAfter(r.getCheckIn());
                if (overlap) {
                    return false;
                }
            }
        }
        return true;
    }

    // ---------- Main features ----------

    public ArrayList<Room> searchAvailable(String type, LocalDate checkIn, LocalDate checkOut) {
        ArrayList<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (r.getType().equals(type) && isRoomFree(r.getNumber(), checkIn, checkOut)) {
                result.add(r);
            }
        }
        return result;
    }

    public Reservation bookRoom(String guestName, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        if (findRoom(roomNumber) == null) {
            return null;
        }
        if (!isRoomFree(roomNumber, checkIn, checkOut)) {
            return null;
        }

        guestName = guestName.replace(",", " ");
        Reservation reservation = new Reservation(nextId, guestName, roomNumber, checkIn, checkOut);
        nextId++;
        reservations.add(reservation);
        saveReservations();
        return reservation;
    }

    public boolean cancelReservation(int id) {
        Reservation r = findReservation(id);
        if (r == null) {
            return false;
        }
        reservations.remove(r);
        saveReservations();
        return true;
    }

    public void printBill(int id) {
        Reservation r = findReservation(id);
        if (r == null) {
            System.out.println("No reservation found with this ID.");
            return;
        }

        Room room = findRoom(r.getRoomNumber());
        int nights = r.getNights();
        double total = nights * room.getPrice();

        System.out.println("\n========== GRANDSTAY BILL ==========");
        System.out.println("Reservation ID : " + r.getId());
        System.out.println("Guest          : " + r.getGuestName());
        System.out.println("Room           : " + room.getNumber() + " (" + room.getType() + ")");
        System.out.println("Check-in       : " + r.getCheckIn());
        System.out.println("Check-out      : " + r.getCheckOut());
        System.out.println("Nights         : " + nights);
        System.out.println("Price per night: Rs. " + room.getPrice());
        System.out.println("------------------------------------");
        System.out.println("TOTAL          : Rs. " + total);
        System.out.println("====================================");
    }

    public void showAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }

        System.out.println("\nAll reservations:");
        for (Reservation r : reservations) {
            System.out.println("ID " + r.getId() + " | " + r.getGuestName()
                    + " | Room " + r.getRoomNumber()
                    + " | " + r.getCheckIn() + " to " + r.getCheckOut());
        }
    }
}
