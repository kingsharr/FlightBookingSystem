/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flightds;

/**
 *
 * @author sharr
 */
public class Passenger {
    private String name;
    private String passengerId;

    public Passenger(String name, String passengerId) {
        this.name = name;
        this.passengerId = passengerId;
    }

    public String getName() {
        return name;
    }

    public String getPassengerId() {
        return passengerId;
    }

    @Override
    public String toString() {
        return "Passenger Name: " + name + ", ID: " + passengerId;
    }
}
