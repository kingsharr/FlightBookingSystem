/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 * this is main class for the project
 * includes all user inputs
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    // display selected flights
    public static Flight selectFlight(List<Flight> availableFlight, String flightCode) {
        for (Flight flight : availableFlight) {
            if (flight.getFlightCode().equalsIgnoreCase(flightCode)){
                return flight;
            }
        }
        return null;    // not found
    }

    public static void flightMenu() {
        System.out.println("\n~~\n1.  Search for Flights ");
        System.out.println("2.  Book Flight Ticket");
        System.out.println("3.  Cancel Ticket");
        System.out.println("4.  View Ticket Status");
        System.out.println("5.  Exit ");
        System.out.println("Select an option: ");
    }

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        FlightTicketSystem ticketSystem = new FlightTicketSystem();
        String weekStartDate = null;
        List<Flight> availableFlights = null;
        
        // start system until user select 5
        System.out.println("~~~~~~~~ Welcome To XYZ Flight Ticket System ~~~~~~~~");

        while (true) {

            flightMenu();

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // User inputs the week start date
                    System.out.println("Enter the start date of the week (yyyy-MM-dd): ");
                    
                    // check date format accuracyy
                    while (true) {
                        weekStartDate = scanner.nextLine();

                        try {
                            LocalDate.parse(weekStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid date format!! Try again!!");
                        }
                    }

                    availableFlights = ticketSystem.generateFlightsForWeek(weekStartDate, 3); // 3 flights per day

                    // Display flights to the user
                    if (availableFlights !=null) {
                        System.out.println("\nAvailable Flights for the week starting " + weekStartDate + ":");
                        for (Flight flight : availableFlights) {
                            System.out.println(flight);
                        }
                    } else {
                        System.out.println("No available flights for selected week");
                    }

                    break;
                
                case 2:
                    // book ticket
                    if (availableFlights != null){
                        // User selects a flight
                        System.out.println("\nChoose a flight code to book a ticket:");
                        String flightCode = scanner.nextLine();

                        // select the flight
                        Flight selectedFlight = selectFlight(availableFlights, flightCode);

                        if (selectedFlight != null) {
                            // User provides passenger details
                            System.out.println("Enter your name: ");
                            String passengerName = scanner.nextLine();
                            System.out.println("Enter your passenger ID: ");
                            String passengerId = scanner.nextLine();

                            // pass to method
                            ticketSystem.bookTicket(weekStartDate, flightCode, passengerName, passengerId);

                        } else {
                            System.out.println("Invalid flight code. Please try again.");
                        }
                    }
                    break;

                case 3:
                    // cancel ticket
                    System.out.println("Cancel my ticket");
                    break;
                
                case 4:
                    // view ticket stats 
                    System.out.println("View Ticket Status");

                    break;

                case 5: 
                    //exit
                    System.out.println("Exiting system... \nThank you for using XYZ Flight Booking System");
                    scanner.close();
                    System.exit(0);
                    break;
                    
            
                default:
                    System.out.println("Invalid option. Please try again!! ");
                    break;
            }
        }

    }
}

