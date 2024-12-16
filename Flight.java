/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private QueueStore queueStore = new QueueStore(waitingList);    // queue list for storage

    // constructor
    public Flight(String flightCode, String flightDate, int availableSeats) {
        this.flightCode = flightCode;
        this.flightDate = flightDate;
        this.availableSeats = availableSeats;
        this.bookedTickets = new ArrayList<>();
        this.queueStore = new QueueStore(waitingList);

    }

    // getter for flight id
    public String getFlightCode() {
        return flightCode;
    }

    // getter for flight date
    public String getDate() {
        return flightDate;
    }

    // getter for flight seats
    public int getAvailableSeats() {
        return availableSeats;
    }

    // method to book ticket 
    public Ticket bookTicket(Ticket ticket) {
        if (availableSeats > 0) {
            bookedTickets.add(ticket);  //add passenger to confirmed list
            availableSeats--;   // reduce seat num
            ticket.setStatus(TicketStatus.CONFIRMED);

            // update to csv
            saveSeatsData(flightCode, availableSeats);

            return ticket;
        } else {
            ticket.setStatus(TicketStatus.WAITING);

            // no vacany add passenger to waiting list
            //waitingList.enqueue(ticket); 
            // load waiting list csv 
            queueStore.loadFromCSV("flightds/waitinglist.csv");
            queueStore.enqueueSave(ticket, "flightds/waitinglist.csv");

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

    // update available seats info realtime
    public void saveSeatsData(String flightCode, int newAvailableSeats) {
        // temp list
        List<String> updatedLine = new ArrayList<>();

        try {
            // get existing data to check code
            List<String> lines = Files.readAllLines(Paths.get("flightds/FlightData.csv"));

            // in each line split for code part only
            for (String line : lines) {
                String[] parts = line.split(",");

                if (parts[0].equals(flightCode)) { //match !!
                    parts[2] = String.valueOf(newAvailableSeats);
                }

                updatedLine.add(String.join(",", parts));
            }

            // write back to csv
            Files.write(Paths.get("flightds/FlightData.csv"), updatedLine);
        } catch(IOException e) {
            System.out.println("Error saving new flight data !!");
        }
        
    }

    @Override
    public String toString() {
        return "Flight Code: " + flightCode + ", Date: " + flightDate + ", Available Seats: " + availableSeats;
    }
}
