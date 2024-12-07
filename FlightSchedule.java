/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 * this class is to manage all flight schedules
 */
import java.util.HashMap;

public class FlightSchedule {
    
    private String week;
    private HashMap<String, Flight> flights;    // use hashmap to store flight details

    public FlightSchedule(String week) {
        this.week = week;
        this.flights = new HashMap<>();
    }

    public String getWeek() {
        return week;
    }

    // add flight based on flight id from flight class
    public void addFlight(Flight flight) {
        flights.put(flight.getFlightCode(), flight);
    }

    // returns specified flight details ignoring casing
    public Flight getFlight(String flightId) {
        
        for (String key : flights.keySet()) {
            if (key.equalsIgnoreCase(flightId)) {
                return flights.get(key);  
            }
        }
        return null;  // not found
    }

    // display all flight sch for the week 
    public void listFlights() {
        if (flights.isEmpty()) {
            System.out.println("No flights scheduled for this week.");
        } else {
            System.out.println("Flights for the week: " + week);
            for (Flight flight : flights.values()) {
                System.out.println(flight);
            }
        }
    }
}
