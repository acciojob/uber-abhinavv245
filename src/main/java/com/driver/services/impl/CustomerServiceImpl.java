package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer=customerRepository2.findById(customerId).get();

		List<TripBooking> listOfTrips=customer.getTripBookingList();
		for(TripBooking trip:listOfTrips){
			if(trip.getStatus()==TripStatus.CONFIRMED){
				Driver driver=trip.getDriver();
				driver.getCab().setAvailable(true);
				driverRepository2.save(driver);
			}
		}
		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		//find the driver available
		List<Driver> driverList=driverRepository2.findAll();
		Collections.sort(driverList, new Comparator<Driver>() {
			@Override
			public int compare(Driver o1, Driver o2) {
				return o1.getDriverId()-o2.getDriverId();
			}
		});
		Driver tripDriver=null;
		for(Driver driver:driverList){
			if(driver.getCab().getAvailable()){
				tripDriver=driver;
				break;
			}
		}
		if(tripDriver==null) throw new Exception("No cab available!");
		//get the customer
		Customer customer=customerRepository2.findById(customerId).get();

		//set the basic attributes for the trip
		TripBooking bookedTrip= new TripBooking(fromLocation,toLocation,distanceInKm,customer,0);
		bookedTrip.setStatus(TripStatus.CONFIRMED);
		bookedTrip.setDriver(tripDriver);
		bookedTrip.setBill(distanceInKm*tripDriver.getCab().getPerKmRate());
        tripDriver.getCab().setAvailable(false);
		//add the trip the customer's trip list
		customer.getTripBookingList().add(bookedTrip);

		customerRepository2.save(customer);
		tripBookingRepository2.save(bookedTrip);

		return bookedTrip;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking= tripBookingRepository2.findById(tripId).get();

		//get the driver and make his cab free
		tripBooking.getDriver().getCab().setAvailable(true);
		//set the bill to zero
        tripBooking.setBill(0);
		//change the trip status to cancel
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking= tripBookingRepository2.findById(tripId).get();
        //get the driver
		Driver driver=tripBooking.getDriver();
		//make his cab free
		driver.getCab().setAvailable(true);
		//change the trip status to complete
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);
	}
}
