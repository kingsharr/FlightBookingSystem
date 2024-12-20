// this class is to handle all queue and store waiting list 
// passenger details to csv

package flightds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class QueueStore {
    
    private Queue<Ticket> queue;

    public Queue<Ticket> getQueue() {
        if (queue == null) {
            loadFromCSV("flightds/waitinglist.csv");
        }
        
        return queue;
    }

    public QueueStore(Queue<Ticket> queue) {
        this.queue = queue;
    }

    // enqueue n save to csv
    public void enqueueSave(Ticket ticket, String filepath) {
        queue.enqueue(ticket);
        saveToCSV(filepath);
    }

    // dequeueu n remove frm csv
    public Ticket dequeueRemove(String flightId) {
        
        //temp queue while looping
        Queue<Ticket> tempQueue = new Queue<>();
        Ticket foundTicket = null;
        
        while (!queue.isEmpty()) {
            Ticket ticket = queue.dequeue();
            //System.out.println("Dequeue ticket: " + ticket);

            boolean ticketFound = ticket.getFlight().getFlightCode().equals(flightId);

            if (ticketFound && foundTicket ==null) {
                foundTicket = ticket;
                System.out.println("FOUND ticket ");
                removeFromCSV(ticket, "flightds/waitinglist.csv");
            } else {
                tempQueue.enqueue(ticket);  // add the unmatched ticekts
            }

        }

        queue = tempQueue;

        if (foundTicket == null) {
            System.out.println("NO Ticket FOUND " + flightId);
        }
        
    
        return foundTicket;
        
    }

    // save queueu to csv file
    private void saveToCSV(String filepath) {
        try (FileWriter fileWriter = new FileWriter(filepath, true)) {
            //fileWriter.write("TicketID,PassengerName,PassengerID,FlightCode,FlightDate\n");
            
            for (Ticket ticket : queue.getQueue()) {
                fileWriter.append(ticket.getTicketId()).append(",")
                    .append(ticket.getPassenger().getName()).append(",")
                    .append(ticket.getPassenger().getPassportNum()).append(",")
                    .append(ticket.getFlight().getFlightCode()).append(",")
                    .append(ticket.getFlight().getDate()).append("\n");
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // remove ticket from csv once if availble seats
    private boolean removeFromCSV(Ticket ticket, String filepath) {

        List<String> updatedLines = new ArrayList<>();
        String line;
        boolean remove = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath)))  {

            String header = reader.readLine(); //header
            updatedLines.add(header);

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",\\s*");

                // check if same
                if (details.length >=5 && 
                    details[3].trim().equals(ticket.getFlight().getFlightCode()) 
                    && details[2].trim().equals(ticket.getPassenger().getPassportNum())) {
                    
                    // all same dequeue
                    remove = true;
                    System.out.println("Removed from waiting list " + line);
                    continue;
                } else {
                    updatedLines.add(line);  // keep other lines

                }
            }

            // updated
            if (remove) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
                    for (String updatedLine : updatedLines) {
                        writer.write(updatedLine + "\n");
                        
                    }
                    
                    
                    // debug code
                    System.out.println("Updated waiting list");
                    return true;
                } 
            } 

        }catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    // load csv waiting list
    public void loadFromCSV(String filepath) {
        queue = new Queue<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            
            String line;
            reader.readLine();  // skip header

            while ((line = reader.readLine()) != null ) {
                String[] details = line.split(",\\s*");

                if (details.length ==5) {

                    
                    Ticket ticket = new Ticket(
                        new Passenger(details[1], details[2]),
                        TicketStatus.WAITING, 
                        new Flight(details[3], "", 0));
                    
                    ticket.setTicketId(details[0]);
                    //System.out.println("Load ticket id " + ticket.getTicketId());
                    queue.enqueue(ticket);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
