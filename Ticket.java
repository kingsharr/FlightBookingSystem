/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 */
public class Ticket {
    private String ticketId;
    private Passenger passenger;
    private TicketStatus status;
    private Flight flight;

    public Ticket(Passenger passenger, TicketStatus status, Flight flight) {
        this.ticketId = "T" + System.currentTimeMillis(); // Unique ID
        this.passenger = passenger;
        this.status = status;
        this.flight = flight;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TicketID: " + ticketId + ", Passenger: " + passenger.getName() +
               ", Status: " + status + ", Flight: " + flight.getFlightCode();
    }
}
