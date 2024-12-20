package flightds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicketManager {

    private FlightTicketSystem ticketSystem;
    private Flight flight;

    public TicketManager(FlightTicketSystem ticketSystem) {
        this.ticketSystem = ticketSystem;
        
    }

    public void cancelTicket(String flightId, String passengerId) {
        try {

            Flight flightCancel = null;
            for (Flight flight : ticketSystem.getFlights()) {
                if (flight.getFlightCode().equals(flightId)) {
                    flightCancel = flight;
                    break;
                }
            }

            if (flightCancel == null) {
                System.out.println("Flight not found: " + flightId);
                return;
            }

            // check and remove from Confirmed Tickets csv
            boolean removedFromConfirmed = cancelConfirmedTicket(flightId, passengerId);
            
            // if removed from confirmed, process waiting list
            if (removedFromConfirmed) {
                //update seats
                int availableSeats = flightCancel.getAvailableSeats()+1;
                flightCancel.saveSeatsData(flightId, availableSeats);
                processWaitingList(flightId);   // check in waiting list to add

            } else {
                // not in confirmed check waiting list
                boolean removedFromWaiting = cancelWaitingTicket(flightId, passengerId);
                
                if (removedFromWaiting) {
                   
                    processWaitingList(flightId);   // add the next person in waiting list 
                    System.out.println("Ticket removed from waiting list.");
                } else {
                    System.out.println("No ticket found for Passenger ID " + passengerId + " on Flight " + flightId);
                }
            }
        } catch (IOException e) {
            System.out.println("Error canceling ticket: " + e.getMessage());
        }
    }

    private boolean cancelConfirmedTicket(String flightId, String passengerId) throws IOException {
        List<String> updatedLines = new ArrayList<>();
        boolean ticketRemoved = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("flightds/ConfirmedTickets.csv"))) {
            String line;
            
            // Read header
            String header = reader.readLine();
            updatedLines.add(header);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",\\s*");
                
                // Check if ticket matches flight and passenger
                if (parts.length >=4 && parts[3].equals(flightId) && parts[2].equals(passengerId)) {
                    ticketRemoved = true;
                    System.out.println("Ticket canceled: " + line); 
                    continue;
                } else {
                    //System.out.println("No match " + line);
                    updatedLines.add(line); // maintain other lines 
                }
            }
        }

        // Rewrite the file if a ticket was removed
        if (ticketRemoved) {
            try (FileWriter writer = new FileWriter("flightds/ConfirmedTickets.csv")) {
                for (String updatedLine : updatedLines) {
                    writer.write(updatedLine + "\n");
                }
            }
        }

        return ticketRemoved;
    }

    private boolean cancelWaitingTicket(String flightId, String passengerId) throws IOException {
        List<String> updatedLines = new ArrayList<>();
        boolean ticketRemoved = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("flightds/waitinglist.csv"))) {
            String line;
            
            // Read header
            String header = reader.readLine();
            updatedLines.add(header);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",\\s*");
                
                // Check if ticket matches flight and passenger
                if (parts.length >=5 && parts[3].trim().equals(flightId) && parts[1].trim().equals(passengerId)) {
                    ticketRemoved = true;
                    System.out.println("Ticket removed from waiting list: " + line);
                } else {
                    updatedLines.add(line);
                }
            }
        }

        // Rewrite the file if a ticket was removed
        if (ticketRemoved) {
            try (FileWriter writer = new FileWriter("flightds/waitinglist.csv")) {
                for (String updatedLine : updatedLines) {
                    writer.write(updatedLine + "\n");
                }
            }
        }

        return ticketRemoved;
    }

    private void processWaitingList(String flightId) throws IOException {
        //List<String> waitingTicketsForFlight = new ArrayList<>();
        //List<String> otherWaitingTickets = new ArrayList<>();

        Flight flight = null;
        // find flightd
        for (Flight f : ticketSystem.getFlights()) {
            if (f.getFlightCode().equals(flightId)) {
                flight = f;
                break;
            }
        }

        if (flight == null) {
            System.out.println("Flight not found: " + flightId);
            return;
        }
        
        QueueStore queueStore = new QueueStore(null);
        queueStore.loadFromCSV("flightds/waitinglist.csv");
        
        Queue<Ticket> waitingQueue = queueStore.getQueue();
        
        // dequeue first ticket
        Ticket firstWaitingTicket = queueStore.dequeueRemove(flightId);

        if (firstWaitingTicket != null) {
            try (FileWriter writer = new FileWriter("flightds/ConfirmedTickets.csv", true)) {
                writer.append(firstWaitingTicket.getTicketId()).append(",")
                .append(firstWaitingTicket.getPassenger().getName()).append(",")
                .append(firstWaitingTicket.getPassenger().getPassportNum()).append(",")
                .append(firstWaitingTicket.getFlight().getFlightCode()).append(",")
                .append(firstWaitingTicket.getFlight().getDate()).append("\n");

                System.out.println("First waiting list ticket promoted to confirmed: " + firstWaitingTicket);

                // update seat count
                flight.saveSeatsData(flightId, flight.getAvailableSeats());

            }
        } else {
            System.out.println("No waiting ticket to move for confirmed");
        }
        /* 
        try (BufferedReader reader = new BufferedReader(new FileReader("flightds/waitinglist.csv"))) {
            // Read header
            String header = reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",\\s*");
                
                if (parts.length >=5 && parts[3].trim().equalsIgnoreCase(flightId)) {
                    //waitingTicketsForFlight.add(line);  // match found
                    waitingQueue.enqueue(line);
                } else {
                    otherWaitingTickets.add(line);  // others
                }
            }
        }

        // Process first waiting ticket
        if (!waitingTicketsForFlight.isEmpty()) {
            String firstWaitingTicket = waitingTicketsForFlight.remove(0);
            
            // Add to confirmed tickets
            try (FileWriter confirmedWriter = new FileWriter("flightds/ConfirmedTickets.csv", true)) {
                confirmedWriter.write(firstWaitingTicket + "\n");
                System.out.println("First waiting list ticket promoted to confirmed: " + firstWaitingTicket);

                
            }
        }

        // Rewrite waiting list
        try (FileWriter waitingWriter = new FileWriter("flightds/waitinglist.csv")) {
            // Write header
            waitingWriter.write("TicketID,PassengerName,PassengerID,FlightCode,FlightDate\n");
            
            // Write waiting tickets for other flights
            for (String ticket : otherWaitingTickets) {
                waitingWriter.write(ticket + "\n");
            }
            
            // Write remaining waiting tickets for this flight
            for (String ticket : waitingQueue) {
                waitingWriter.write(ticket + "\n");
            }
        }
        */
    }
/* 
    private void rearrangeWaitingList(String flightId) throws IOException {
        List<String> waitingTicketsForFlight = new ArrayList<>();
        List<String> otherWaitingTickets = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("flightds/waitinglist.csv"))) {
            // Read header
            String header = reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",\\s*");
                
                if (parts[4].trim().equals(flightId)) {
                    waitingTicketsForFlight.add(line);
                } else {
                    otherWaitingTickets.add(line);
                }
            }
        }

        // Rewrite waiting list
        try (FileWriter waitingWriter = new FileWriter("flightds/waitinglist.csv")) {
            // Write header
            waitingWriter.write("TicketID,PassengerID,PassengerName,Status,FlightCode\n");
            
            // Write waiting tickets for other flights
            for (String ticket : otherWaitingTickets) {
                waitingWriter.write(ticket + "\n");
            }
            
            // Write remaining waiting tickets for this flight
            for (String ticket : waitingTicketsForFlight) {
                waitingWriter.write(ticket + "\n");
            }
        }
    }
*/
    public void editTicket(String ticketId, Scanner scanner) {
        try {
            // Check Confirmed Tickets CSV first
            File confirmedFile = new File("flightds/ConfirmedTickets.csv");
            File tempConfirmedFile = new File("flightds/ConfirmedTickets_temp.csv");
            boolean ticketFoundAndEdited = false;

            // Read Confirmed Tickets
            try (BufferedReader reader = new BufferedReader(new FileReader(confirmedFile));
                 FileWriter writer = new FileWriter(tempConfirmedFile)) {
                
                // Write header
                String header = reader.readLine();
                writer.write(header + "\n");

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    
                    // Find the ticket
                    if (parts[0].equals(ticketId)) {
                        // Ticket found, allow editing
                        System.out.println("Current Passenger Name: " + parts[1]);
                        System.out.println("Current Passport ID: " + parts[2]);
                        
                        // Ask for new passenger name
                        System.out.print("Enter new passenger name (press Enter to keep current): ");
                        String newName = scanner.nextLine().trim();
                        if (newName.isEmpty()) {
                            newName = parts[1];
                        }
                        
                        // Ask for new passenger ID
                        System.out.print("Enter new Passport ID (press Enter to keep current): ");
                        String newPassengerId = scanner.nextLine().trim();
                        if (newPassengerId.isEmpty()) {
                            newPassengerId = parts[2];
                        }
                        
                        // Reconstruct the line with updated information
                        String updatedLine = String.format("%s,%s,%s,%s,%s\n", 
                            ticketId, newName, newPassengerId, parts[3], parts[4]);
                        
                        writer.write(updatedLine);
                        ticketFoundAndEdited = true;
                        
                        System.out.println("Ticket information updated successfully!");
                    } else {
                        // Write the original line
                        writer.write(line + "\n");
                    }
                }
            }

            // Check Waiting List CSV
            if (!ticketFoundAndEdited) {
                File waitingFile = new File("flightds/waitinglist.csv");
                File tempWaitingFile = new File("flightds/waitinglist_temp.csv");

                try (BufferedReader reader = new BufferedReader(new FileReader(waitingFile));
                     FileWriter writer = new FileWriter(tempWaitingFile)) {
                    
                    // Write header
                    String header = reader.readLine();
                    writer.write(header + "\n");

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        
                        // Find the ticket
                        if (parts[0].equals(ticketId)) {
                            // Ticket found, allow editing
                            System.out.println("Current Passenger Name: " + parts[2]);
                            System.out.println("Current Passport ID: " + parts[1]);
                            
                            // Ask for new passenger name
                            System.out.print("Enter new passenger name (press Enter to keep current): ");
                            String newName = scanner.nextLine().trim();
                            if (newName.isEmpty()) {
                                newName = parts[2];
                            }
                            
                            // Ask for new passenger ID
                            System.out.print("Enter new passport ID (press Enter to keep current): ");
                            String newPassengerId = scanner.nextLine().trim();
                            if (newPassengerId.isEmpty()) {
                                newPassengerId = parts[1];
                            }
                            
                            // Reconstruct the line with updated information
                            String updatedLine = String.format("%s,%s,%s,%s,%s\n", 
                                ticketId, newPassengerId, newName, parts[3], parts[4]);
                            
                            writer.write(updatedLine);
                            ticketFoundAndEdited = true;
                            
                            System.out.println("Ticket information updated successfully!");
                        } else {
                            // Write the original line
                            writer.write(line + "\n");
                        }
                    }
                }

                // Replace waiting list file
                if (ticketFoundAndEdited) {
                    waitingFile.delete();
                    tempWaitingFile.renameTo(waitingFile);
                } else {
                    tempWaitingFile.delete();
                }
            }

            // Replace confirmed tickets file
            if (ticketFoundAndEdited) {
                confirmedFile.delete();
                tempConfirmedFile.renameTo(confirmedFile);
            } else {
                tempConfirmedFile.delete();
                System.out.println("No ticket found with ID: " + ticketId);
            }

        } catch (IOException e) {
            System.out.println("Error editing ticket: " + e.getMessage());
        }
    }


    // Method to view ticket status by checking CSV files
   public void viewTicketStatus(String flightId, String passengerId) {
        boolean ticketFound = false;
        
        // Check Confirmed Tickets CSV
        try (BufferedReader confirmedReader = new BufferedReader(new FileReader("flightds/ConfirmedTickets.csv"))) {
            String line;
            confirmedReader.readLine(); // skip header
            
            while ((line = confirmedReader.readLine()) != null) {
                String[] parts = line.split(",\\s*");

                if (parts.length >=4 && parts[3].equals(flightId) && parts[2].equals(passengerId)) {
                    System.out.println("Ticket Status: CONFIRMED");
                    System.out.println("Passenger Name: " + parts[1]);
                    System.out.println("Flight Code: " + parts[3]);
                    ticketFound = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Confirmed Tickets: " + e.getMessage());
        }

        // check in waiting list csv
        if (!ticketFound) {
            try (BufferedReader waitingReader = new BufferedReader(new FileReader("flightds/waitinglist.csv"))) {
                String line;
               
                
                while ((line = waitingReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >=5 && parts[4].trim().equals(flightId) && parts[1].trim().equals(passengerId)) {
                        System.out.println("Ticket Status: WAITING");
                        System.out.println("Passenger Name: " + parts[2]);
                        System.out.println("Flight Code: " + parts[4]);
                        ticketFound = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading Waiting List: " + e.getMessage());
            }
        }

        // If ticket not found in either file
        if (!ticketFound) {
            System.out.println("No ticket found for Flight ID: " + flightId + " and Passenger ID: " + passengerId);
        }
    }
}
