/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 * this class manages each flights data and booking + canceling
 */
import java.util.ArrayList;
import java.util.List;

public class Flight {

    private String flightCode; // flightID
    private String flightDate; //date to book flight
    private int availableSeats; // num of seats able to book
    private List<Ticket> bookedTickets; // stores confirmed tickets !!
    private Queue<Ticket> waitingList = new Queue<>();  // waiting list q

    // constructor
    public Flight(String flightCode, String flightDate, int availableSeats) {
        this.flightCode = flightCode;
        this.flightDate = flightDate;
        this.availableSeats = availableSeats;
        this.bookedTickets = new ArrayList<>();
    }

    // getter for flight id
    public String getFlightCode() {
        return flightCode;
    }

    // method to book ticket 
    public Ticket bookTicket(Ticket ticket) {
        if (availableSeats > 0) {
            bookedTickets.add(ticket);  //add passenger to confirmed list
            availableSeats--;   // reduce seat num
            ticket.setStatus(TicketStatus.CONFIRMED);
            return ticket;
        } else {
            ticket.setStatus(TicketStatus.WAITING);

            // no vacany add passenger to waiting list
            waitingList.enqueue(ticket);    

            return ticket;
        }

        
    }

    // method to cancel ticket 
    public void cancelTicket(String ticketId) {
        Ticket ticketToCancel = null;

        // loop through the tickets to find id
        for (int i=0; i< bookedTickets.size(); i++) {
            Ticket ticket = bookedTickets.get(i);

            if (ticket.getTicketId().equals(ticketId)) { // compare strings
                ticketToCancel = ticket;
                bookedTickets.remove(i);   // remove the ticket
                availableSeats++;   // increase seat by 1
                System.out.println("Ticket ID " + ticketId + " has been canceled successfully");
                
                // pass next passenger from waiting list to confirmed
                if (!waitingList.isEmpty()) {   // check if got anyone waiting
                    Ticket nextInLine = waitingList.dequeue();
                    bookedTickets.add(nextInLine);  // add them to booked
                    availableSeats--;

                    nextInLine.setStatus(TicketStatus.CONFIRMED);
                    System.out.println("Passenger in waiting list has been confirmed"); 
                    // TO-DO: include waiting list passenger name here as confirmed
                }

                return;
            }

            
        }

        // if no ticket id found
        if (ticketToCancel == null) {
            System.out.println("Ticket ID " + ticketId + " not found. Try again.");
        }
    }

    // method to view ticket status based on passenger id
    public String viewStatus(Passenger passenger) {
        
        // loop through to find ticket id
        for (int i=0; i<bookedTickets.size(); i++) {
            Ticket ticket = bookedTickets.get(i);

            // show status for passenger
            if (ticket.getPassenger().equals(passenger)) {
                return ticket.getStatus().toString();
            }
        }

        // else 
        return "No ticket found for Passenger: " + passenger.getName();
    }

    @Override
    public String toString() {
        return "Flight Code: " + flightCode + ", Date: " + flightDate + ", Available Seats: " + availableSeats;
    }
}
