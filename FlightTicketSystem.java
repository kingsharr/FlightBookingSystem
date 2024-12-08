/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

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

public class FlightTicketSystem {
    
    private HashMap<String, FlightSchedule> schedules;

    public FlightTicketSystem() {
        this.schedules = new HashMap<>();
    }

    public FlightSchedule getSchedule(String week) {
        return schedules.get(week);
    }

    // generate flights based on week user input
    public List<Flight> generateFlightsForWeek(String weekStartDate, int numFlightsPerDay) {
        
        // check if already generated first
        if (schedules.containsKey(weekStartDate)) {
           
            return schedules.get(weekStartDate).listFlights();
            
        } 

        List<Flight> flights = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(weekStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (int i = 0; i < 7; i++) { // 7 days in a week
            LocalDate flightDate = startDate.plusDays(i);
            for (int j = 0; j < numFlightsPerDay; j++) {
                String flightCode = "FL" + (i + 1) + "-" + (j + 1);
                Flight flight = new Flight(flightCode, flightDate.toString(), 2); // Assuming 2 seats per flight
                flights.add(flight);
            }
        }
        FlightSchedule schedule = new FlightSchedule(weekStartDate);
        for (Flight flight : flights) {
            schedule.addFlight(flight);  // add generated flights 
        }
        
        addSchedule(schedule);
        return flights;
    }


    // method to add schedule using week
    public void addSchedule(FlightSchedule schedule) {
        // check if already exists
        if (schedules.containsKey(schedule.getWeek())) {
            System.out.println("Schedule already exists for week: " + schedule.getWeek());
        } else {
            schedules.put(schedule.getWeek(), schedule);

        }
        
    }

    // search for flight by week
    public void searchFlights(String week) {
        FlightSchedule schedule = schedules.get(week);
        if (schedule == null) {
            System.out.println("No schedule found for the week: " + week);
        } else {
            schedule.listFlights();
        }
    }

    // book ticket for the scheduled week
    public void bookTicket(String week, String flightId, String passengerName, String passengerId) {
        FlightSchedule schedule = schedules.get(week);
        // if not found for that week
        if (schedule == null) {
            System.out.println("No schedule found for the week: " + week);
            return;
        }

        // not found for id
        Flight flight = schedule.getFlight(flightId);
        if (flight == null) {
            System.out.println("No flight found with ID: " + flightId);
            return;
        }

        // book ticket based on user input
        Passenger passenger = new Passenger(passengerName, passengerId);
        Ticket ticket = new Ticket(passenger, TicketStatus.WAITING, flight);    // set default as waiting first
        Ticket bookedTicket = flight.bookTicket(ticket);

        if (bookedTicket.getStatus() == TicketStatus.CONFIRMED) {
            System.out.println("Ticket booked successfully: " + bookedTicket);
        } else {
            System.out.println("No seats available. Added to Waiting List");
        }
    
    }

    // cancel ticket
    public void cancelTicket(String week, String flightId, String ticketId) {
        FlightSchedule schedule = schedules.get(week);
        if (schedule == null) {
            System.out.println("No schedule found for the week: " + week);
            return;
        }

        Flight flight = schedule.getFlight(flightId);
        if (flight == null) {
            System.out.println("No flight found with ID: " + flightId);
            return;
        }

        // successfull cancel
        flight.cancelTicket(ticketId);
        System.out.println("Ticket ID " + ticketId + " has been canceled.");
    }

    // check ticket status 
    public void viewTicketStatus(String week, String flightId, Passenger passenger) {
        FlightSchedule schedule = schedules.get(week);
        if (schedule == null) {
            System.out.println("No schedule found for the week: " + week);
            return;
        }

        Flight flight = schedule.getFlight(flightId);
        if (flight == null) {
            System.out.println("No flight found with ID: " + flightId);
            return;
        }

        // successful
        String status = flight.viewStatus(passenger);
        System.out.println("Ticket status for Passenger " + passenger.getName() + ": " + status);
    }

}
