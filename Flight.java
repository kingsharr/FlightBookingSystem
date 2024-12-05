/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 */
import java.util.ArrayList;
import java.util.List;

public class Flight {
    private String flightCode;
    private String flightDate;
    private int availableSeats;
    private List<Ticket> bookedTickets;

    public Flight(String flightCode, String flightDate, int availableSeats) {
        this.flightCode = flightCode;
        this.flightDate = flightDate;
        this.availableSeats = availableSeats;
        this.bookedTickets = new ArrayList<>();
    }

    public String getFlightCode() {
        return flightCode;
    }

    public Ticket bookTicket(Ticket ticket) {
        if (availableSeats > 0) {
            bookedTickets.add(ticket);
            availableSeats--;
            ticket.setStatus(TicketStatus.CONFIRMED);
            return ticket;
        } else {
            ticket.setStatus(TicketStatus.WAITING);
            return ticket;
        }
    }

    public void cancelTicket(String ticketId) {
        Ticket ticketToCancel = null;
        for (Ticket ticket : bookedTickets) {
            if (ticket.getTicketId().equals(ticketId)) { // Compare Strings
                ticketToCancel = ticket;
                break;
            }
        }

        if (ticketToCancel != null) {
            bookedTickets.remove(ticketToCancel);
            availableSeats++;
            System.out.println("Ticket canceled successfully: " + ticketId);
        } else {
            System.out.println("Ticket not found: " + ticketId);
        }
    }

    public String viewStatus(Passenger passenger) {
        for (Ticket ticket : bookedTickets) {
            if (ticket.getPassenger().equals(passenger)) {
                return ticket.getStatus().toString();
            }
        }
        return "No ticket found for Passenger: " + passenger.getName();
    }

    @Override
    public String toString() {
        return "Flight Code: " + flightCode + ", Date: " + flightDate + ", Available Seats: " + availableSeats;
    }
}
