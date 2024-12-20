/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
/**
 *
 * @author sharr
 * this class contains all flight schedules 
 */
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class FlightTicketSystem {

    private List<Flight> flights = new ArrayList<>(); // store found flights based on user search
    private HashMap<String, Ticket> allBookings = new HashMap<>(); // Storing all tickets by Ticket ID
    private HashMap<String, Passenger> registeredPassenger = new HashMap<>(); //store passenger id details

    public List<Flight> getFlights() {
        return this.flights;
    }

    // load flight data from csv
    public void loadFlightData(String filePath, boolean printData) {

        try {
            Scanner sc = new Scanner(new File(filePath));

            // skip header !!!
            if (sc.hasNextLine()) {
                sc.nextLine();
            }

            while (sc.hasNextLine()) {
                
                String line = sc.nextLine();

                // only print if true
                if (printData == true) {
                    System.out.println(line);
                }
                
                String[] data = line.split(",");   // split by comma

                if (data.length == 3) {
                    String flightId = data[0];
                    String flightDate = data[1];
                    int availableSeats  = Integer.parseInt(data[2]);
    
                    flights.add(new Flight(flightId, flightDate, availableSeats));
    
                }
                
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found ");
            e.printStackTrace();
        }
    
        
    }

    // search for flight by week
    public void searchFlights(String userInput) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inputDate = LocalDate.parse(userInput.trim(), formatter);  // ensure right format

        // find start week and end week by user input
        LocalDate startWeek = inputDate.with(DayOfWeek.MONDAY);
        LocalDate endWeek = startWeek.plusDays(6); // total 7 end sunday

        System.out.println("Searching for flights from " + startWeek + " to " + endWeek);

        // set found flights in a list
        List<String> flightsFound = new ArrayList<>();

        for (Flight flight : flights) {
            String flightDate = flight.getDate().trim();
            LocalDate fLocalDate = LocalDate.parse(flightDate, formatter);

            // debug check if can get flight code or not
            //System.out.println("\nFlight: " + flight.getFlightCode() + "ON " + fLocalDate);

            // check for the week in the range of user input datess ONLY
            if (!fLocalDate.isBefore(startWeek) && !fLocalDate.isAfter(endWeek)) {
                flightsFound.add("Flight ID: " + flight.getFlightCode() + ", Date: " + flight.getDate() + ", Available Seats: " + flight.getAvailableSeats());
            }

        }

        // display
        if (!flightsFound.isEmpty()) {
            System.out.println("Flights for the week of " + userInput);
            for (String flight : flightsFound) {
                System.out.println(flight);
            }
        } else {
            System.out.println("No flights available!! ");
        }


    }

    // book ticket for the user input flight id
    public void bookTicket(String flightId, String passengerName, String passengerId) {
        Flight flight = null;

        // check for passenger id
        if (registeredPassenger.containsKey(passengerId)) {
            System.out.println("Passport ID already registered");
            return;
        }
        
        // find flgiht
        for (Flight userFlight : flights) {
            if (userFlight.getFlightCode().equals(flightId)) { // yes contains
                flight = userFlight;
                break;
            }
        }
        // not found for id
        if (flight == null) {
            System.out.println("No flight found with ID: " + flightId);
            return;
        }

        // book ticket based on user input
        Passenger passenger = new Passenger(passengerName, passengerId);
        Ticket ticket = new Ticket(passenger, TicketStatus.WAITING, flight);    // set default as waiting first
        Ticket bookedTicket = flight.bookTicket(ticket);

        // Store passenger and ticket
        registeredPassenger.put(passengerId, passenger);
        allBookings.put(bookedTicket.getTicketId(), bookedTicket);

        if (bookedTicket.getStatus() == TicketStatus.CONFIRMED) {
            System.out.println("Ticket booked successfully: " + bookedTicket); // get Ticket info print
        } else {
            System.out.println("No seats available. Added to Waiting List " + bookedTicket);
        }
    
    }

    /* 
    // cancel ticket
    public void cancelTicket(String ticketId) {
        
        // check if contains
        for (Flight flight : flights) {
            flight.cancelTicket(ticketId);
        }
        
    }

    // check ticket status 
    public void viewTicketStatus(String ticketId) {
        
        boolean ticketFound = false;

        for (Flight flight: flights) {
            String status = flight.viewStatus(ticketId);
            
            if (!status.equals("Ticket not found")) {
                System.out.println("\n****** Searching Ticket ID: " + ticketId + " ******");
                System.out.println("Status: " + status);
                ticketFound = true;
                break;
            }

        }


        if (!ticketFound) {
            System.out.println("Ticket id " + ticketId + " not found");
        }
        

    }
*/
    // Optional method to check Confirmed Tickets CSV
    private void checkConfirmedTicketsCSV(String ticketId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("ConfirmedTickets.csv"))) {
            String line;
            boolean found = false;
            
            // Skip header
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].trim().equals(ticketId)) {
                    System.out.println("\n===== Historical Ticket Record =====");
                    System.out.println("Ticket ID found in past confirmed tickets");
                    System.out.println("Passenger: " + parts[1]);
                    System.out.println("Flight Code: " + parts[3]);
                    System.out.println("Note: This ticket may have been processed previously");
                    System.out.println("===============================");
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("\nNo historical ticket records found.");
            }
        } catch (IOException e) {
            System.out.println("Error checking confirmed tickets CSV: " + e.getMessage());
        }
    }

}
