/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;


/**
 *
 * @author sharr
 * this class has all details about the ticket  
 */

 public class Ticket {
    
    private String ticketId;
    private static int ticketCount = 1000; // start count 1000
    private Passenger passenger;    // passenger obj
    private TicketStatus status;    // ticket status obj
    private Flight flight;          // flight obj

    // constructor
    public Ticket(Passenger passenger, TicketStatus status, Flight flight) {
        this.ticketId = "T" + (ticketCount++);  // simple id
        this.passenger = passenger;
        this.status = status;
        this.flight = flight;
    }

    //getters 
    public String getTicketId() {
        return ticketId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Flight getFlight() {
        return flight;
    }

    //set status
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "\n\n===== Ticket Info =====" + "\nTicket ID: " + ticketId + " \nPassenger Name: " + passenger.getName() +
               "\nStatus: " + status + "\nFlight ID: " + flight.getFlightCode() + "\n================";
    }
}
