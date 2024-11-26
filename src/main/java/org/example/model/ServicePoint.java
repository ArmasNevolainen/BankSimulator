package org.example.model;

import org.example.eduni.distributions.ContinuousGenerator;
import org.example.framework.Clock;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.Trace;

import java.util.LinkedList;

// TODO:
// Service Point functionalities & calculations (+ variables needed) and reporting to be implemented
public class ServicePoint {
	private LinkedList<Customer> queue = new LinkedList<>(); // Data Structure used
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	private double serviceStartTime;
	private boolean reserved = false;
	private double queueStartTime;


	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
	}

	public void addQueue(Customer a) {
		queueStartTime = Clock.getInstance().getClock();
		queue.add(a);
	}

	public void beginService() {
		Trace.out(Trace.Level.INFO, "Starting a new service for the customer #" + queue.peek().getId());

		// Calculate and store queue time when service begins
		double queueTime = Clock.getInstance().getClock() - queueStartTime;
		totalQueueTime += queueTime;

		serviceStartTime = Clock.getInstance().getClock();
		reserved = true;
		double serviceTime = generator.sample();

		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getClock()+serviceTime));
	}

	public Customer removeQueue() {
		reserved = false;
		// Calculate and store service time when customer leaves
		double serviceTime = Clock.getInstance().getClock() - serviceStartTime;
		totalServiceTime += serviceTime;
		totalCustomers++;

		return queue.poll();
	}

	public boolean isReserved(){
		return reserved;
	}

	public boolean isOnQueue(){
		return queue.size() != 0;
	}

	public int getQueueLength() {
		return queue.size();
	}

	// Add statistics tracking
	private int totalCustomers = 0;
	private double totalServiceTime = 0;
	private double totalQueueTime = 0;

	public void addStatistics(double serviceTime, double queueTime) {
		totalCustomers++;
		totalServiceTime += serviceTime;
		totalQueueTime += queueTime;
	}

	public double getAverageServiceTime() {
		return totalCustomers > 0 ? totalServiceTime/totalCustomers : 0;
	}

	public double getAverageQueueTime() {
		return totalCustomers > 0 ? totalQueueTime/totalCustomers : 0;
	}
}
