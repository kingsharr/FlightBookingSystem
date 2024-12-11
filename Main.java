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

    // display selected flight
    public static Flight selectFlight(List<Flight> flights, String flightCode) {
        for (Flight flight : flights) {
            if (flight.getFlightCode().equalsIgnoreCase(flightCode)){
                return flight;
            }
        }
        return null;    // not found
    }

    public static void flightMenu() {
        System.out.println("\n~~\n1.  View All Flights ");
        System.out.println("2.  Search for Flights ");
        System.out.println("3.  Book Flight Ticket");
        System.out.println("4.  Cancel Ticket");
        System.out.println("5.  View Ticket Status");
        System.out.println("6.  Exit ");
        System.out.println("Select an option: ");
    }

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        FlightTicketSystem ticketSystem = new FlightTicketSystem();
        String weekStartDate = null;
        List<Flight> availableFlights = null;
        
        // start system until user select 6
        System.out.println("~~~~~~~~ Welcome To XYZ Flight Ticket System ~~~~~~~~");

        while (true) {

            flightMenu();

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // view csv all flights
                    ticketSystem.loadFlightData("flightds/FlightData.csv", true);
                    break;

                case 2:     
                    // search for flight by week

                    // check if data loaded 
                    if (ticketSystem.getFlights().isEmpty()) {
                        ticketSystem.loadFlightData("flightds/FlightData.csv", false);
                    } 

                    // user input specific date of week to search
                    System.out.println("Enter date of the week (yyyy-MM-dd): ");
                    
                    // check date format accuracyy
                    while (true) {
                        weekStartDate = scanner.nextLine().trim();
                        try {
                            LocalDate.parse(weekStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            // search flight method
                            ticketSystem.searchFlights(weekStartDate);
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid date format!! Try again!!");
                        }
                    }

                    
                    break;
                
                case 3:     
                    // book ticket

                    // check if data loaded 
                    if (ticketSystem.getFlights().isEmpty()) {
                        ticketSystem.loadFlightData("flightds/FlightData.csv", false);
                    } 

                    // User selects a flight
                    System.out.println("\nEnter a flight code to book a ticket:");
                    String flightCode = scanner.nextLine();

                    // select the flight
                    Flight selectedFlight = selectFlight(ticketSystem.getFlights(), flightCode);

                    if (selectedFlight != null) {
                        // User provides passenger details
                        System.out.println("Enter your name: ");
                        String passengerName = scanner.nextLine();
                        System.out.println("Enter your passenger ID: ");
                        String passengerId = scanner.nextLine();

                        // check validation
                        if (passengerName.isEmpty() || passengerId.isEmpty()) {
                            System.out.println("Invalid Input \n Failed to Book Ticket");
                            
                        } else {
                            // pass to method
                            ticketSystem.bookTicket(flightCode, passengerName, passengerId);

                        }
                    } else {
                        System.out.println("Flight ID is invalid!");
                    }
                    
                    
                    break;

                case 4:     // cancel ticket
                    
                    System.out.println("Cancel my ticket");
                    break;
                
                case 5:     // view ticket stats 
                    
                    System.out.println("View Ticket Status");

                    break;

                case 6: 
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

