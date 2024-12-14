package org.example.framework;

import org.example.model.Customer;

import java.util.List;
import java.util.Map;
/**
 * The Engine class is the core of the simulation. It is responsible for running the simulation and
 * keeping track of the time. The Engine class is abstract and must be inherited by a class that
 * implements the abstract methods.
 */
public abstract class Engine {
	private double simulationTime = 0;	// time when the simulation will be stopped
	private Clock clock;// to simplify the code (clock.getClock() instead Clock.getInstance().getClock())
	/** Event list containing events to be processed */
	protected EventList eventList;		// events to be processed are stored here
	/** Listener for queue updates */
	protected QueueUpdateListener queueUpdateListener;
	/** Flag indicating if simulation is paused */
	protected boolean isPaused = false;
	/**
	 * Interface for receiving queue update notifications
	 */
	public interface QueueUpdateListener {
		/**
		 * Called when queue status changes
		 * @param queueStatus Current status of all queues
		 */
		void onQueueUpdate(Map<String, List<Customer>> queueStatus);
	}
	/**
	 * Sets the QueueUpdateListener.
	 * @param listener The QueueUpdateListener to set.
	 */
	public void setQueueUpdateListener(QueueUpdateListener listener) {
		this.queueUpdateListener = listener;
	}
	/**
	 * Constructor for the Engine class.
	 */
	public Engine() {
		clock = Clock.getInstance();
		
		eventList = new EventList();
		
		// Service Points are created in simu.model-package's class who is inheriting the Engine class
	}
	/**
	 * Sets the simulation time.
	 * @param time The time to set.
	 */
	public void setSimulationTime(double time) {	// define how long we will run the simulation
		simulationTime = time;
	}
	/**
	 * Runs the simulation.
	 */
	public void run(){
		initialize(); // creating, e.g., the first event

		while (simulate()) {
			Trace.out(Trace.Level.INFO, "\nA-phase: time is " + currentTime());
			clock.setClock(currentTime());
			
			Trace.out(Trace.Level.INFO, "\nB-phase:" );
			runBEvents();
			
			Trace.out(Trace.Level.INFO, "\nC-phase:" );
			tryCEvents();

		}

		results();
	}
	/**
	 * Runs the B-phase events.
	 */
	private void runBEvents() {
		while (eventList.getNextEventTime() == clock.getClock()){
			runEvent(eventList.remove());
		}
	}
	/**
	 * Boolean to check if the simulation is paused.
	 * @return true if paused, false otherwise
	 */
	public boolean isPaused() {
		return isPaused;
	}
	/**
	 * Pauses the simulation.
	 * @param paused The value to set.
	 */
	public void setPaused(boolean paused) {
		this.isPaused = paused;
	}
	/**
	 * Returns the current time.
	 * @return The current time.
	 */
	private double currentTime(){
		return eventList.getNextEventTime();
	}

	/**
	 * Returns the simulation time.
	 * @return
	 */
	private boolean simulate(){
		return clock.getClock() < simulationTime;
	}
	/**
	 * Abstract method to run the event.
	 * @param t The event to run.
	 */
	protected abstract void runEvent(Event t);	// Defined in simu.model-package's class who is inheriting the Engine class
	/**
	 * Abstract method to try the C-phase events.
	 */
	protected abstract void tryCEvents();		// Defined in simu.model-package's class who is inheriting the Engine class
	/**
	 * Abstract method to initialize the simulation.
	 */
	protected abstract void initialize(); 		// Defined in simu.model-package's class who is inheriting the Engine class
	/**
	 * Abstract method to get the results of the simulation.
	 */
	protected abstract void results(); 			// Defined in simu.model-package's class who is inheriting the Engine class
}