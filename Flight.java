/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

import java.io.File;
import java.io.FileWriter;
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

    public List<Ticket> getBookedTickets() {
        return bookedTickets;
    }

    public List<Ticket> getWaitingList() {
        return waitingList.getQueue();
    }

    // method to book ticket 
    public Ticket bookTicket(Ticket ticket) {
        if (availableSeats > 0) {
            bookedTickets.add(ticket);  //add passenger to confirmed list
            availableSeats--;   // reduce seat num
            ticket.setStatus(TicketStatus.CONFIRMED);

            // save to confirmed tickets CSV
            saveConfirmedTicketToCSV(ticket);

            // update to csv
            saveSeatsData(flightCode, availableSeats);

            return ticket;
        } else {
            ticket.setStatus(TicketStatus.WAITING);

            // no vacany add passenger to waiting list
            
            // load waiting list csv 
            //queueStore.loadFromCSV("flightds/waitinglist.csv");
            queueStore.enqueueSave(ticket, "flightds/waitinglist.csv");

            return ticket;
        }

        
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

    // save confirmed bookings
    private void saveConfirmedTicketToCSV(Ticket ticket) {
        try (FileWriter fileWriter = new FileWriter("flightds/ConfirmedTickets.csv", true)) {
            
            File file = new File("flightds/ConfirmedTickets.csv");
            
            if (file.length() == 0) {   // if empty add header
                fileWriter.append("TicketID,PassengerName,PassengerID,FlightCode,FlightDate\n");
            }
            
            // Append ticket details
            fileWriter.append(String.format("%s,%s,%s,%s,%s\n", 
                ticket.getTicketId(), 
                ticket.getPassenger().getName(), 
                ticket.getPassenger().getPassportNum(),
                this.flightCode,
                this.flightDate));
        } catch (IOException e) {
            System.out.println("Error saving confirmed ticket: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Flight Code: " + flightCode + ", Date: " + flightDate + ", Available Seats: " + availableSeats;
    }
}
