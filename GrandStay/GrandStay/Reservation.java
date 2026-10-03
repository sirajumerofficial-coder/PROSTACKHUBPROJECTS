import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private int id;
    private String guestName;
    private int roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public Reservation(int id, String guestName, int roomNumber,
                       LocalDate checkIn, LocalDate checkOut) {
        this.id = id;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getId() {
        return id;
    }

    public String getGuestName() {
        return guestName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getNights() {
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        return (int) days;
    }

    // One line of text to store this reservation in the file
    public String toFileLine() {
        return id + "," + guestName + "," + roomNumber + "," + checkIn + "," + checkOut;
    }
}
