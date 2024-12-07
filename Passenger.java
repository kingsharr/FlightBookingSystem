/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 *
 * 
 * this class contains all details about the passengers 
 */
public class Passenger {
    
    private String name;
    private String passengerId;

    // constructor
    public Passenger(String name, String passengerId) {
        this.name = name;
        this.passengerId = passengerId;
    }

    //getters 
    public String getName() {
        return name;
    }

    public String getPassengerId() {
        return passengerId;
    }

    //settters
    public void setName(String name) {
        this.name = name;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    @Override
    public String toString() {
        return "Passenger Name: " + name + ", ID: " + passengerId;
    }
}
