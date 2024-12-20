/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author sharr
 * this class has all details about the ticket  
 */

public class Ticket {
    
    private String ticketId;
    private Passenger passenger;    // passenger obj
    private TicketStatus status;    // ticket status obj
    private Flight flight;          // flight obj

    // constructor
    public Ticket(Passenger passenger, TicketStatus status, Flight flight) {
        String timeStamp = new SimpleDateFormat("mmss").format(new Date());
        this.ticketId = "T" + flight.getFlightCode()+ "_"+timeStamp;   // simple id
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

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public String toString() {
        return "\n\n===== Ticket Info =====" + "\nTicket ID: " + ticketId + " \nPassenger Name: " + passenger.getName() +
               "\nStatus: " + status + "\nFlight ID: " + flight.getFlightCode() + "\n================";
    }
}
