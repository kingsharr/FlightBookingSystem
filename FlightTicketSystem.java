/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 */
import java.util.HashMap;

public class FlightTicketSystem {
    private HashMap<String, FlightSchedule> schedules;

    public FlightTicketSystem() {
        this.schedules = new HashMap<>();
    }

    public void addSchedule(FlightSchedule schedule) {
        schedules.put(schedule.getWeek(), schedule);
    }

    public FlightSchedule getSchedule(String week) {
        return schedules.get(week);
    }

    public void searchFlights(String week) {
        FlightSchedule schedule = schedules.get(week);
        if (schedule == null) {
            System.out.println("No schedule found for the week: " + week);
        } else {
            schedule.listFlights();
        }
    }

    public void bookTicket(String week, String flightId, Passenger passenger) {
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

        Ticket ticket = new Ticket(passenger, TicketStatus.WAITING, flight);
        Ticket bookedTicket = flight.bookTicket(ticket);
        System.out.println("Ticket booked for Passenger: " + passenger.getName() + " | Status: " + bookedTicket.getStatus());
    }

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

        flight.cancelTicket(ticketId);
        System.out.println("Ticket ID " + ticketId + " has been canceled.");
    }

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

        String status = flight.viewStatus(passenger);
        System.out.println("Ticket status for Passenger " + passenger.getName() + ": " + status);
    }
}
