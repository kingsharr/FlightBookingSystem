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
    private String passportNum;;

    // constructor
    public Passenger(String name, String passportNum) {
        this.name = name;
        this.passportNum = passportNum;
    }

    //getters 
    public String getName() {
        return name;
    }

    public String getPassportNum() {
        return passportNum;
    }

    //settters
    public void setName(String name) {
        this.name = name;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    @Override
    public String toString() {
        return "Passenger Name: " + name + ", ID: " + passportNum;
    }
}
