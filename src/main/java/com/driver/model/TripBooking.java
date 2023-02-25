package com.driver.model;

import javax.persistence.*;

@Entity
public class TripBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tripBookingId;
    private String fromLocation;
    private String toLocation;
    private int distanceInKm;
    @Enumerated(EnumType.STRING)
    private TripStatus status;
    private int bill;

    @ManyToOne
    @JoinColumn
    private Driver driverEntity;

    @ManyToOne
    @JoinColumn
    private Customer customer;

    public TripBooking() {
    }

    public TripBooking(int tripBookingId, String fromLocation, String toLocation, int distanceInKm, TripStatus tripStatus, int bill, Driver driver, Customer customer) {
        this.tripBookingId = tripBookingId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.distanceInKm = distanceInKm;
        this.status = tripStatus;
        this.bill = bill;
        this.driverEntity = driver;
        this.customer = customer;
    }

    public TripBooking(String fromLocation, String toLocation, int distanceInKm, Customer customer,int bill) {
        this.fromLocation=fromLocation;
        this.toLocation=toLocation;
        this.distanceInKm=distanceInKm;
        this.customer=customer;
        this.bill=bill;
    }

    public int getTripBookingId() {
        return tripBookingId;
    }

    public void setTripBookingId(int tripBookingId) {
        this.tripBookingId = tripBookingId;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public int getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(int distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public TripStatus getTripStatus() {
        return status;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.status = tripStatus;
    }

    public int getBill() {
        return bill;
    }

    public void setBill(int bill) {
        this.bill = bill;
    }

    public Driver getDriver() {
        return driverEntity;
    }

    public void setDriver(Driver driver) {
        this.driverEntity = driver;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}