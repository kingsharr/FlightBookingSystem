import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

// Booking class remains the same (no changes needed for date handling)
class Booking {
    private String bookingId;
    private Passenger passenger;
    private Flight flight;
    private String status;

    public Booking(String bookingId, Passenger passenger, Flight flight, String status) {
        this.bookingId = bookingId;
        this.passenger = passenger;
        this.flight = flight;
        this.status = status;
    }

    public String getBookingId(){ 
        return bookingId; 
    }
    public Passenger getPassenger(){ 
        return passenger; 
    }
    public Flight getFlight(){ 
        return flight; 
    }
    public String getStatus(){ 
        return status; 
    }

    // Setter for status
    public void setStatus(String status){ 
        this.status = status; 
    }


    // New method to convert booking to CSV format
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%d,%s", 
            bookingId, 
            passenger.getName(), 
            passenger.getPassportNumber(), 
            status, 
            flight.getFlightId(), 
            flight.getDate());
    }

    // New static method to create Booking from CSV
    public static Booking fromCSV(String csvLine, Map<Integer, Flight> flights) {
        String[] parts = csvLine.split(",");
        if (parts.length != 6) return null;

        String bookingId = parts[0];
        String name = parts[1];
        String passportNumber = parts[2];
        String status = parts[3];
        int flightId = Integer.parseInt(parts[4]);
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try{
            date= dateFormat.parse(parts[5]);
        }catch (ParseException e){
            System.out.println("Error parsing date:"+parts[5]);
            return null;
        }

        Flight flight = flights.get(flightId);
        if (flight == null) return null;

        Passenger passenger = new Passenger(name, passportNumber);
        Booking booking = new Booking(bookingId, passenger, flight, status);
        
        if (status.equals("Confirmed")) {
            flight.getConfirmedBookings().add(booking);
        } else {
            flight.getWaitingList().add(booking);
        }

        return booking;
    }
}
class Passenger {
    private String name;
    private String passportNumber;

    public Passenger(String name, String passportNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }
}

class Flight {
    private int flightId;
    private Date date;
    private int totalSeats;
    private List<Booking> confirmedBookings;
    private Queue<Booking> waitingList;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Flight(int flightId, String dateStr, int totalSeats) {
        this.flightId = flightId;
        try {
            this.date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            this.date = new Date(); // Default to current date if parsing fails
        }
        this.totalSeats = totalSeats;
        this.confirmedBookings = new ArrayList<>();
        this.waitingList = new LinkedList<>();
    }

    public int getFlightId() {
        return flightId;
    }

    public String getDate() {
        return dateFormat.format(date);
    }

    public int getAvailableSeats() {
        return totalSeats - confirmedBookings.size();
    }

    public Booking bookTicket(Passenger passenger, String bookingId) {
        Booking booking;
        if (confirmedBookings.size() < totalSeats) {
            booking = new Booking(bookingId, passenger, this, "Confirmed");
            confirmedBookings.add(booking);
        } else {
            booking = new Booking(bookingId, passenger, this, "Waiting List");
            waitingList.add(booking);
        }
        return booking;
    }
    public Booking findBookingById(String bookingId) {
        // Check confirmed bookings
        for (Booking booking : confirmedBookings) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }

        // Check waiting list
        for (Booking booking : waitingList) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }

        return null;
    }
    public Booking cancelBooking(String bookingId) {
        Booking cancelledBooking = findBookingById(bookingId);
        
        if (cancelledBooking != null) {
            // Remove from confirmed or waiting list
            confirmedBookings.remove(cancelledBooking);
            waitingList.remove(cancelledBooking);

            // Move first waiting list booking to confirmed if available
            if (!waitingList.isEmpty()) {
                Booking nextBooking = waitingList.poll();
                nextBooking.setStatus("Confirmed");
                confirmedBookings.add(nextBooking);
                return nextBooking;
            }
        }

        return null;
    }

    public List<Booking> getConfirmedBookings() {
        return confirmedBookings;
    }

    public Queue<Booking> getWaitingList() {
        return waitingList;
    }

    public void saveBookingsToFile() {
        try (PrintWriter confirmedWriter = new PrintWriter(new FileWriter("confirmed_bookings_" + flightId + ".csv"));
             PrintWriter waitingWriter = new PrintWriter(new FileWriter("waiting_list_" + flightId + ".csv"))) {
            for (Booking booking : confirmedBookings) {
                confirmedWriter.println(booking.toCSV());
            }
            for (Booking booking : waitingList) {
                waitingWriter.println(booking.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings for Flight " + flightId + ": " + e.getMessage());
        }
    }
}

class FlightBookingSystem {
    private Map<Integer, Flight> flights;
    private Scanner scanner;
    private Map<String, Booking> allBookings;
    private int bookingid;
    private static final String FLIGHTS_FILE = "flights.csv";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public FlightBookingSystem() {
        flights = new HashMap<>();
        scanner = new Scanner(System.in);
        allBookings = new HashMap<>();
        bookingid = 1000;

        loadFlights();
    }

    private void loadFlights() {
        File flightsFile = new File(FLIGHTS_FILE);
        if (flightsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(flightsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        int flightId = Integer.parseInt(parts[0]);
                        String dateStr = parts[1];
                        int totalSeats = Integer.parseInt(parts[2]);
                        int availableSeats = Integer.parseInt(parts[3]);

                        Flight flight = new Flight(flightId, dateStr, totalSeats);
                        flights.put(flightId, flight);

                        loadFlightBookings(flight);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading flights file: " + e.getMessage());
                createDefaultFlights();
            }
        } else {
            createDefaultFlights();
        }
    }
private String generateBookingId() {
        return "B" + (++bookingid);
    }
    private void createDefaultFlights() {
        flights.put(101, new Flight(101, "20/11/2024", 10));
        flights.put(102, new Flight(102, "22/11/2024", 5));
        flights.put(103, new Flight(103, "24/11/2024", 5));
        saveFlights();
    }

    private void loadFlightBookings(Flight flight) {
        File confirmedFile = new File("confirmed_bookings_" + flight.getFlightId() + ".csv");
        if (confirmedFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(confirmedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Booking booking = Booking.fromCSV(line, flights);
                    if (booking != null) {
                        allBookings.put(booking.getBookingId(), booking);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading confirmed bookings: " + e.getMessage());
            }
        }

        File waitingFile = new File("waiting_list_" + flight.getFlightId() + ".csv");
        if (waitingFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(waitingFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Booking booking = Booking.fromCSV(line, flights);
                    if (booking != null) {
                        allBookings.put(booking.getBookingId(), booking);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading waiting list: " + e.getMessage());
            }
        }
    }

    private void saveFlights() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FLIGHTS_FILE))) {
            for (Flight flight : flights.values()) {
                writer.printf("%d,%s,%d,%d\n",
                        flight.getFlightId(),
                        flight.getDate(),
                        flight.getConfirmedBookings().size() + flight.getWaitingList().size() + flight.getAvailableSeats(),
                        flight.getAvailableSeats());
                flight.saveBookingsToFile();
            }
        } catch (IOException e) {
            System.out.println("Error saving flights: " + e.getMessage());
        }
    }

    public void run() {
        displayStart();
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    searchFlights();
                    break;
                case 2:
                    bookTicket();
                    break;
                case 3:
                    editTicketInformation();
                    break;
                case 4:
                    viewTicketStatus();
                    break;
                case 5:
                    cancelTicket();
                    break;
                case 6:
                    saveFlights();
                    System.out.println("Exiting the Flight Booking System. Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    public void displayStart(){
    System.out.println("\n======================");
        System.out.println("FLIGHT TICKET BOOKING SYSTEM");
        System.out.println("======================");
        System.out.println("Welcome to the Flight Booking System!");
}
    private void displayMenu() {
        System.out.println();
        System.out.println("Enter your choice:");
        System.out.println("1. Search Flights");
        System.out.println("2. Book a Ticket");
        System.out.println("3. Edit Ticket Information");
        System.out.println("4. View Ticket Status");
        System.out.println("5. Cancel Ticket");
        System.out.println("6. Exit");
        System.out.print("Enter your choice (1-6): ");
    }

    private void searchFlights() {
        System.out.println("------------------------");
        System.out.println("Search Flights (Enter a week range, e.g., 19th November 2024 – 25th November 2024):");
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();

        System.out.println("Flights available from 19th November 2024 – 25th November 2024:");
        for (Flight flight : flights.values()) {
            System.out.printf("Flight ID: %d | Date: %s | Available Seats: %d\n", 
                flight.getFlightId(), flight.getDate(), 
                flight.getAvailableSeats() > 0 ? flight.getAvailableSeats() : 0);
        }
    }

    // Modify booking methods to save flights after changes
    private void bookTicket() {
       System.out.println("------------------------");
        System.out.print("Enter the Flight ID to book a ticket: ");
        int flightId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Flight selectedFlight = flights.get(flightId);
        if (selectedFlight == null) {
            System.out.println("Invalid Flight ID.");
            return;
        }

        System.out.println("------------------------");
        System.out.printf("Booking a ticket for Flight ID: %d\n", flightId);
        System.out.print("Enter passenger name: ");
        String name = scanner.nextLine();
        System.out.print("Enter passport number: ");
        String passportNumber = scanner.nextLine();

        // Generate unique booking ID
        String bookingId = generateBookingId();

        Passenger passenger = new Passenger(name, passportNumber);
        Booking booking = selectedFlight.bookTicket(passenger, bookingId);

        if (booking.getStatus().equals("Confirmed")) {
            System.out.printf("Ticket booked successfully for %s on Flight %d.\n", name, flightId);
            System.out.println("Status: Confirmed");
            System.out.println("Your Booking ID is: " + bookingId);
            // Store booking in central bookings map
            allBookings.put(bookingId, booking);
        } else {
            System.out.printf("Flight %d is fully booked. Adding %s to the waiting list.\n", flightId, name);
            System.out.println("Your Booking ID is: " + bookingId);
            // Store booking in central bookings map
            allBookings.put(bookingId, booking);
        }

        System.out.print("Would you like to make another booking? (y/n): ");
        String continueBooking = scanner.nextLine();
        if (continueBooking.equalsIgnoreCase("y")) {
            bookTicket();
        }
        saveFlights();
    }
     private void viewTicketStatus() {
        System.out.println("------------------------");
        System.out.print("Enter your Booking ID to view ticket status: ");
        String bookingId = scanner.nextLine();

        Booking booking = allBookings.get(bookingId);
        if (booking != null) {
            System.out.println("Ticket Information:");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Passenger Name: " + booking.getPassenger().getName());
            System.out.println("Flight ID: " + booking.getFlight().getFlightId());
            System.out.println("Date: " + booking.getFlight().getDate());
            System.out.println("Status: " + booking.getStatus());
        } else {
            System.out.println("No ticket found for this Booking ID.");
        }
    }

    private void cancelTicket() {
         System.out.println("------------------------");
        System.out.print("Enter your Booking ID to cancel the ticket: ");
        String bookingId = scanner.nextLine();

        Booking booking = allBookings.get(bookingId);
        if (booking != null) {
            Flight flight = booking.getFlight();
            Booking nextBooking = flight.cancelBooking(bookingId);

            System.out.printf("Ticket for %s on Flight %d is canceled.\n", 
                booking.getPassenger().getName(), flight.getFlightId());
            
            if (nextBooking != null) {
                System.out.println("Checking waiting list for Flight " + flight.getFlightId() + "...");
                System.out.println("First person on waiting list: " + nextBooking.getPassenger().getName());
                System.out.println("Adding " + nextBooking.getPassenger().getName() + " to the confirmed list for Flight " + flight.getFlightId() + ".");
                System.out.println("Ticket successfully moved to confirmed for " + nextBooking.getPassenger().getName() + ".");
            }
        } else {
            System.out.println("No ticket found for this Booking ID.");
        }
        saveFlights();
    }

    private void editTicketInformation() {
       System.out.println("------------------------");
        System.out.print("Enter your Booking ID to edit ticket information: ");
        String bookingId = scanner.nextLine();

        Booking booking = allBookings.get(bookingId);
        if (booking != null) {
            System.out.print("Enter new passenger name (press enter to skip): ");
            String newName = scanner.nextLine();
            System.out.print("Enter new passport number (press enter to skip): ");
            String newPassportNumber = scanner.nextLine();

            if (!newName.isEmpty()) {
                booking.getPassenger().setName(newName);
            }
            if (!newPassportNumber.isEmpty()) {
                booking.getPassenger().setPassportNumber(newPassportNumber);
            }

            System.out.println("Ticket information updated successfully.");
        } else {
            System.out.println("No ticket found for this Booking ID.");
        }
                saveFlights();

    }
    }


public class Tester {
    public static void main(String[] args) {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        bookingSystem.run();
    }
}
