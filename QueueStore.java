// this class is to handle all queue and store waiting list 
// passenger details to csv

package flightds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class QueueStore {
    
    private Queue<Ticket> queue;

    public QueueStore(Queue<Ticket> queue) {
        this.queue = queue;
    }

    // enqueue n save to csv
    public void enqueueSave(Ticket ticket, String filepath) {
        queue.enqueue(ticket);
        saveToCSV(filepath);
    }

    // dequeueu n remove frm csv
    public Ticket dequeueRemove(String filepath) {
        Ticket ticket2 = queue.dequeue();
        if (ticket2 != null) {
            removeFromCSV(ticket2, filepath);
        }
        
        return ticket2;
        
    }

    // save queueu to csv file
    private void saveToCSV(String filepath) {
        try (FileWriter fileWriter = new FileWriter(filepath, false)) {

            for (Ticket ticket : queue.getQueue()) {
                fileWriter.append(ticket.getPassenger().getPassengerId()).append(", ")
                .append(ticket.getPassenger().getName()).append(", ")
                .append(ticket.getStatus().toString()).append(", ")
                .append(ticket.getFlight().getFlightCode()).append("\n");
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // remove ticket from csv once if availble seats
    private void removeFromCSV(Ticket ticket, String filepath) {

        File inputFile = new File(filepath);
        File tempFile = new File("flightds/waitinglist_temp.csv");
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            FileWriter writer = new FileWriter(tempFile); // temp file

            String line;
            boolean remove = false;

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(", ");

                // check if same
                if (details[0].equals(ticket.getPassenger().getPassengerId()) && 
                details[1].equals(ticket.getPassenger().getName()) && 
                details[2].equals(ticket.getFlight().getFlightCode()) && 
                details[3].equals(ticket.getStatus().toString()) && !remove) {

                    // all same dequeue
                    remove = true;
                    continue;
                } else {
                    writer.append(line).append("\n");   // write lines to temp file

                }
            }

            // replace ori file with updated temp
            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                System.out.println("Removed from waiting list");
            } else {
                System.out.println("Error updating csv waiting list");
            }


            reader.close();
        }catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // load csv waiting list
    public void loadFromCSV(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            
            String line;

            while ((line = reader.readLine()) != null ) {
                String[] details = line.split(", ");

                if (details.length ==4) {
                    
                    Ticket ticket = new Ticket(
                        new Passenger(details[0], details[1]),
                        TicketStatus.valueOf(details[2]), 
                        new Flight(details[3], "", 0));

                    queue.enqueue(ticket);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
