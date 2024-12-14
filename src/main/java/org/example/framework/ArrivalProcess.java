package org.example.framework;

import org.example.eduni.distributions.ContinuousGenerator;
/**
 * This class represents the arrival process of the simulation.
 * It generates the next event and adds it to the event list.
 */

public class ArrivalProcess {
	private ContinuousGenerator generator;
	private EventList eventList;
	private IEventType type;
	/**
	 * Constructor for the ArrivalProcess class.
	 * @param g The generator for the arrival process.
	 * @param tl The event list.
	 * @param type The type of the event.
	 */
	public ArrivalProcess(ContinuousGenerator g, EventList tl, IEventType type) {
		this.generator = g;
		this.eventList = tl;
		this.type = type;
	}
	/**
	 * Generates the next event and adds it to the event list.
	 */
	public void generateNextEvent() {
		Event t = new Event(type, Clock.getInstance().getClock() + generator.sample());
		eventList.add(t);
	}
}
