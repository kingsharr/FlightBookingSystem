/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 *
 * @author sharr
 * this class is to manage all flight schedules
 */
import java.util.HashMap;
import java.util.List;

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
    /*
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
    */
    // display all flight sch for the week as list
    public List<Flight> listFlights()  {
    if (flights.isEmpty()) {
        System.out.println("No flights scheduled for this week.");
        return new ArrayList<>();  // return empty list 
    } else {

        // sort ascending and numerical
        List<Flight> sortFlight = new ArrayList<>(flights.values());
        Collections.sort(sortFlight, new Comparator<Flight>() {
            @Override
            public int compare(Flight f1, Flight f2) {  // check numerical
                int code1 = Integer.parseInt(f1.getFlightCode().split("-")[0].substring(2));
                int code2 = Integer.parseInt(f2.getFlightCode().split("-")[0].substring(2));
                return Integer.compare(code1, code2);
            }
        });


        System.out.println("Flights for the week: " + week);

        for (Flight flight : sortFlight) {
            System.out.println(flight);
        }
        return sortFlight;  // return generated flights
    }

    }
}
