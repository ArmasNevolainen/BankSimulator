package org.example.model;

import org.example.framework.Clock;
import org.example.framework.Trace;

public class Customer {
	private double arrivalTime;
	private double removalTime;
	private int id;
	private CustomerType type;
	private static int i = 1;
	private static long sum = 0;

//	public enum CustomerType {
//		TRANSACTION_CLIENT, // For deposits/withdrawals
//		ACCOUNT_CLIENT     // For account operations
//	}

	public Customer(CustomerType type){
		this.type = type;
		id = i++;
		arrivalTime = Clock.getInstance().getClock();
	}

	public CustomerType getType() {
		return type;
	}
	public double getRemovalTime() {
		return removalTime;
	}

	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getId() {
		return id;
	}
	
	public void reportResults(){
		Trace.out(Trace.Level.INFO, "\nCustomer " + id + " ready! ");
		Trace.out(Trace.Level.INFO, "Customer "   + id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO,"Customer "    + id + " removed: " + removalTime);
		Trace.out(Trace.Level.INFO,"Customer "    + id + " stayed: "  + (removalTime - arrivalTime));

		sum += (removalTime - arrivalTime);
		double mean = sum/id;
		System.out.println("Current mean of the customer service times " + mean);
	}
}
