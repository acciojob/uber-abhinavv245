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
		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		Customer customer=customerRepository2.findById(customerId).get();

		//set the basic attributes for the trip
		TripBooking bookedTrip= new TripBooking(fromLocation,toLocation,distanceInKm,customer,0);
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
			if(driver.getCab().isAvailable()){
				tripDriver=driver;
				bookedTrip.setDriver(tripDriver);
				break;
			}
		}
		if(tripDriver==null) throw new Exception("No cab available!");
		bookedTrip.setTripStatus(TripStatus.CONFIRMED);
        tripDriver.getCab().setAvailable(false);
		//add the trip the customer's trip list
		customer.getTripBookingList().add(bookedTrip);
		//save the customer-trip booking will be automatically saved via bidrectional mapping
		customerRepository2.save(customer);
		return bookedTrip;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking= tripBookingRepository2.findById(tripId).get();

		//get the driver and make his cab free
		tripBooking.getDriver().getCab().setAvailable(true);

		//change the trip status to cancel
		tripBooking.setTripStatus(TripStatus.CANCELED);
		Customer customer=tripBooking.getCustomer();
		//update the parent
		customerRepository2.save(customer);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking= tripBookingRepository2.findById(tripId).get();
        //get the driver and cab rate , then calculate the bill
		Driver driver=tripBooking.getDriver();
		int rate=driver.getCab().getPerKmRate();
		int bill=tripBooking.getDistanceInKm()*rate;
		//set the bill
		tripBooking.setBill(bill);
		//get the driver and make his cab free
		driver.getCab().setAvailable(true);

		//change the trip status to complete
		tripBooking.setTripStatus(TripStatus.COMPLETED);

		Customer customer=tripBooking.getCustomer();

		//save the customer, trip details will be automatically updated
		customerRepository2.save(customer);

	}
}
