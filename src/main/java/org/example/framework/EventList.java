package org.example.framework;

import java.util.PriorityQueue;
/**
 * This class represents a list of events in the simulation.
 */
public class EventList {
	private PriorityQueue<Event> eventlist;
	/**
	 * Constructor for the EventList class.
	 */
	public EventList() {
		eventlist = new PriorityQueue<>();
	}
	/**
	 * Removes the next event from the event list.
	 * @return The next event.
	 */
	public Event remove() {
		Trace.out(Trace.Level.INFO,"Removing from the event list " + eventlist.peek().getType() + " " + eventlist.peek().getTime());
		return eventlist.remove();
	}
	/**
	 * Adds an event to the event list.
	 * @param t The event to add.
	 */
	public void add(Event t) {
		Trace.out(Trace.Level.INFO,"Adding to the event list " + t.getType() + " " + t.getTime());
		eventlist.add(t);
	}
	/**
	 * Returns the next event in the event list.
	 * @return The next event.
	 */
	public double getNextEventTime(){
		return eventlist.peek().getTime();
	}


}
