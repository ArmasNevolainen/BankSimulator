package org.example.framework;
/**
 * This class represents the clock of the simulation.
 */
public class Clock {
	private double clock;
	private static Clock instance;
	
	private Clock(){
		clock = 0;
	}
	/**
	 * Returns the instance of the Clock class.
	 * @return The instance of the Clock class.
	 */
	public static Clock getInstance(){
		if (instance == null){
			instance = new Clock();
		}
		return instance;
	}
	/**
	 * Sets the clock to the given value.
	 * @param clock The value to set the clock to.
	 */
	public void setClock(double clock){
		this.clock = clock;
	}
	/**
	 * Returns the current value of the clock.
	 * @return The current value of the clock.
	 */
	public double getClock(){
		return clock;
	}
	/**
	 * Resets the clock to 0.
	 */
	public void reset() {
		clock = 0;
	}
}
