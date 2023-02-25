package com.driver.model;

import javax.persistence.*;

@Entity
public class Cab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private int perKmRate;
    private boolean available;

    @OneToOne
    @JoinColumn
    private Driver driver;

    public Cab() {
    }

    public Cab(int id, int perKmRate, boolean available, Driver driver) {
        this.Id = id;
        this.perKmRate = perKmRate;
        this.available = available;
        this.driver = driver;
    }

    public Cab(int perKmRate, boolean available,Driver driver) {
        this.perKmRate=perKmRate;
        this.available=available;
        this.driver=driver;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public int getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(int perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}