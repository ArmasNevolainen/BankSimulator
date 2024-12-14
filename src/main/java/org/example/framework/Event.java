package org.example.framework;
/**
 * This class represents an event in the simulation.
 */
public class Event implements Comparable<Event> {
	private IEventType type;
	private double time;
	/**
	 * Constructor for the Event class.
	 * @param type The type of the event.
	 * @param time The time of the event.
	 */
	public Event(IEventType type, double time){
		this.type = type;
		this.time = time;
	}
	/**
	 * Returns the type of the event.
	 * @param type The type to be set for this event
	 */
	public void setType(IEventType type) {
		this.type = type;
	}
	/**
	 * Gets the event type.
	 * @return The event type
	 */
	public IEventType getType() {
		return type;
	}
	/**
	 /**
	 * Sets the event time.
	 * @param time The time to set
	 */
	public void setTime(double time) {
		this.time = time;
	}
	/**
	 * Gets the event time.
	 * @return The event time
	 */
	public double getTime() {
		return time;
	}
	/**
	 * Compares the time of the event to another event.
	 * @param arg The event to compare to.
	 * @return -1 if the time of this event is less than the time of the other event, 1 if the time of this event is greater than the time of the other event, 0 if they are equal.
	 */

	@Override
	public int compareTo(Event arg) {
		if (this.time < arg.time) return -1;
		else if (this.time > arg.time) return 1;
		return 0;
	}
}
