package org.example.model;

import org.example.framework.Clock;
import org.example.framework.Trace;
/**
 * Represents a customer in the bank simulation system.
 * Tracks customer timing, type, and statistical information.
 *
 * @author Group 3
 * @version 1.0
 */
public class Customer {
	/** Time when customer enters the system */
	private double arrivalTime;
	/** Time when customer leaves the system */
	private double removalTime;
	/** Time when customer enters the queue */
	private double queueStartTime;
	/** Unique identifier for the customer */
	private int id;
	/** Type of customer */
	private CustomerType type;
	/** Static counter for customer IDs */
	private static int i = 1;
	/** Static counter for customer service times */
	private static long sum = 0;
	/**
	 * Constructs a new Customer with the specified type.
	 *
	 * @param type The type of the customer
	 */
	public Customer(CustomerType type){
		this.type = type;
		id = i++;
		arrivalTime = Clock.getInstance().getClock();
	}
	/**
	 * Returns the type of the customer.
	 * @return The type of the customer.
	 */
	public CustomerType getType() {
		return type;
	}
	/**
	 * Resets the customer counter and timing statistics.
	 * Used when starting a new simulation.
	 */
	public static void resetCustomerCount() {
		i = 1;
		sum = 0;
	}
	/**
	 * Gets the time when customer left the system.
	 *
	 * @return Removal time in simulation units
	 */
	public double getRemovalTime() {
		return removalTime;
	}

	/**
	 * Sets the time when customer leaves the system.
	 *
	 * @param removalTime Time of departure in simulation units
	 */
	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	/**
	 * Gets the customer's unique identifier.
	 *
	 * @return Customer ID number
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}
	/**
	 * Sets the time when customer enters the system.
	 *
	 * @param arrivalTime Time of arrival in simulation units
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	/**
	 * Sets the time when customer enters the queue.
	 *
	 * @param time Time of arrival in queue in simulation units
	 */
	public void setQueueStartTime(double time) {
		this.queueStartTime = time;
	}
	/**
	 * Gets the time when customer enters the queue.
	 *
	 * @return Time of arrival in queue in simulation units
	 */
	public double getQueueStartTime() {
		return queueStartTime;
	}
	/**
	 * Gets the customer's unique identifier.
	 *
	 * @return Customer ID number
	 */
	public int getId() {
		return id;
	}
	/**
	 * Reports customer service statistics to the trace log.
	 * Calculates and displays mean service time across all customers.
	 */
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
