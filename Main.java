/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static List<Flight> generateFlightsForWeek(String weekStartDate, int numFlightsPerDay) {
        List<Flight> flights = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(weekStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (int i = 0; i < 7; i++) { // 7 days in a week
            LocalDate flightDate = startDate.plusDays(i);
            for (int j = 0; j < numFlightsPerDay; j++) {
                String flightCode = "FL" + (i + 1) + "-" + (j + 1);
                flights.add(new Flight(flightCode, flightDate.toString(), 2)); // Assuming 2 seats per flight
            }
        }
        return flights;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // User inputs the week start date
        System.out.println("Enter the start date of the week (yyyy-MM-dd): ");
        String weekStartDate = scanner.nextLine();

        // Generate flights for the week
        List<Flight> availableFlights = generateFlightsForWeek(weekStartDate, 3); // 3 flights per day

        // Display flights to the user
        System.out.println("\nAvailable Flights for the week starting " + weekStartDate + ":");
        for (Flight flight : availableFlights) {
            System.out.println(flight);
        }

        // User selects a flight
        System.out.println("\nChoose a flight code to book a ticket:");
        String flightCode = scanner.nextLine();

        Flight selectedFlight = null;
        for (Flight flight : availableFlights) {
            if (flight.getFlightCode().equals(flightCode)) {
                selectedFlight = flight;
                break;
            }
        }

        if (selectedFlight != null) {
            // User provides passenger details
            System.out.println("Enter your name: ");
            String passengerName = scanner.nextLine();
            System.out.println("Enter your passenger ID: ");
            String passengerId = scanner.nextLine();

            Passenger passenger = new Passenger(passengerName, passengerId);
            Ticket ticket = new Ticket(passenger, TicketStatus.WAITING, selectedFlight);
            Ticket bookedTicket = selectedFlight.bookTicket(ticket);

            if (bookedTicket.getStatus() == TicketStatus.CONFIRMED) {
                System.out.println("Ticket booked successfully: " + bookedTicket);
            } else {
                System.out.println("No available seats on this flight. Added to waiting list.");
            }
        } else {
            System.out.println("Invalid flight code. Please try again.");
        }

        scanner.close();
    }
}

