/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 */
import java.util.HashMap;

public class FlightSchedule {
    private String week;
    private HashMap<String, Flight> flights;

    public FlightSchedule(String week) {
        this.week = week;
        this.flights = new HashMap<>();
    }

    public String getWeek() {
        return week;
    }

    public void addFlight(Flight flight) {
        flights.put(flight.getFlightCode(), flight);
    }

    public Flight getFlight(String flightId) {
        return flights.get(flightId);
    }

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
