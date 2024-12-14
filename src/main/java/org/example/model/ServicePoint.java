package org.example.model;

import org.example.eduni.distributions.ContinuousGenerator;
import org.example.framework.Clock;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.Trace;

import java.util.LinkedList;
import java.util.List;
/**
 * Represents a service point in the bank simulation system.
 * Manages customer queuing, service timing, and statistics collection.
 */
public class ServicePoint {
	/** Queue of customers waiting for service */
	private LinkedList<Customer> queue = new LinkedList<>();
	/** Random number generator for service times */
	private ContinuousGenerator generator;
	/** List of scheduled events */
	private EventList eventList;
	/** Type of departure event for this service point */
	private EventType eventTypeScheduled;
	/** Start time of the current service */
	private double serviceStartTime;
	/** Indicates if service point is currently serving a customer */
	private boolean reserved = false;
	/** Total number of customers served */
	private int servedCustomers = 0;
	/** Cumulative service time for all customers */
	private double totalServiceTime = 0;
	/** Cumulative queue waiting time for all customers */
	private double totalQueueTime = 0;


	/**
	 * Creates a new service point with specified service time distribution.
	 *
	 * @param generator Service time distribution generator
	 * @param eventList Event scheduling system
	 * @param type Type of departure event for this service point
	 */
	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
	}
	/**
	 * Adds a customer to the service queue and records queue entry time.
	 *
	 * @param a Customer to be added to queue
	 */
	public void addQueue(Customer a) {
		queue.add(a);
		double currentTime = Clock.getInstance().getClock();
		a.setQueueStartTime(currentTime);
		System.out.println("Added customer " + a.getId() + " to queue at time: " + currentTime);
	}
	/**
	 * Starts serving the next customer in queue.
	 * Updates timing statistics and schedules departure event.
	 */
	public void beginService() {
		Customer customer = queue.peek();
		Trace.out(Trace.Level.INFO, "Starting a new service for the customer #" + customer.getId());

		totalQueueTime += Clock.getInstance().getClock() - customer.getQueueStartTime();
		serviceStartTime = Clock.getInstance().getClock();
		reserved = true;
		double serviceTime = generator.sample();

		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getClock() + serviceTime));
	}
	/**
	 * Removes and returns the served customer from queue.
	 * Updates service statistics.
	 *
	 * @return The customer who completed service
	 */
	public Customer removeQueue() {
		reserved = false;
		servedCustomers++;
		totalServiceTime += Clock.getInstance().getClock() - serviceStartTime;
		return queue.poll();
	}
	/**
	 * Gets a defensive copy of the current queue.
	 *
	 * @return List of customers currently in queue
	 */
	public List<Customer> getQueueCustomers() {
		return new LinkedList<>(queue);
	}
	/**
	 * Checks if the service point is currently serving a customer.
	 *
	 * @return True if the service point is serving a customer
	 */
	public boolean isReserved(){
		return reserved;
	}
	/**
	 * Checks if the service point has customers waiting in queue.
	 *
	 * @return True if the service point has customers in queue
	 */
	public boolean isOnQueue(){
		return queue.size() != 0;
	}
	/**
	 * Gets the number of customers currently waiting in queue.
	 *
	 * @return Number of customers in queue
	 */
	public int getQueueLength() {
		return queue.size();
	}
	/**
	 * Calculates the average service time per customer.
	 *
	 * @return Average service time in simulation units
	 */
	public double getAverageServiceTime() {
		return servedCustomers > 0 ? totalServiceTime / servedCustomers : 0;
	}
	/**
	 * Calculates the average time customers spend in queue.
	 *
	 * @return Average queue waiting time in simulation units
	 */
	public double getAverageQueueTime() {
		return servedCustomers > 0 ? totalQueueTime / servedCustomers : 0;
	}
	/**
	 * Gets the total number of customers served by this service point.
	 *
	 * @return Total number of customers served
	 */
	public int getServedCustomers() {
		return servedCustomers;
	}
}
