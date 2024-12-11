package org.example.model;

import org.example.eduni.distributions.ContinuousGenerator;
import org.example.framework.Clock;
import org.example.framework.Event;
import org.example.framework.EventList;
import org.example.framework.Trace;

import java.util.LinkedList;
import java.util.List;

public class ServicePoint {
	private LinkedList<Customer> queue = new LinkedList<>();
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	private double serviceStartTime;
	private boolean reserved = false;
	private int servedCustomers = 0;
	private double totalServiceTime = 0;
	private double totalQueueTime = 0;

	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
	}

	public void addQueue(Customer a) {
		queue.add(a);
		double currentTime = Clock.getInstance().getClock();
		a.setQueueStartTime(currentTime);
		System.out.println("Added customer " + a.getId() + " to queue at time: " + currentTime);
	}

	public void beginService() {
		Customer customer = queue.peek();
		Trace.out(Trace.Level.INFO, "Starting a new service for the customer #" + customer.getId());

		totalQueueTime += Clock.getInstance().getClock() - customer.getQueueStartTime();
		serviceStartTime = Clock.getInstance().getClock();
		reserved = true;
		double serviceTime = generator.sample();

		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getClock() + serviceTime));
	}

	public Customer removeQueue() {
		reserved = false;
		servedCustomers++;
		totalServiceTime += Clock.getInstance().getClock() - serviceStartTime;
		return queue.poll();
	}

	public List<Customer> getQueueCustomers() {
		return new LinkedList<>(queue);
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

	public double getAverageServiceTime() {
		return servedCustomers > 0 ? totalServiceTime / servedCustomers : 0;
	}

	public double getAverageQueueTime() {
		return servedCustomers > 0 ? totalQueueTime / servedCustomers : 0;
	}
	public int getServedCustomers() {
		return servedCustomers;
	}
}
