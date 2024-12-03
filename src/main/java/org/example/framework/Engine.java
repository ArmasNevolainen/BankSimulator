package org.example.framework;

import org.example.model.Customer;

import java.util.List;
import java.util.Map;

public abstract class Engine {
	private double simulationTime = 0;	// time when the simulation will be stopped
	private Clock clock;				// to simplify the code (clock.getClock() instead Clock.getInstance().getClock())
	protected EventList eventList;		// events to be processed are stored here
	protected QueueUpdateListener queueUpdateListener;
	protected boolean isPaused = false;

	public interface QueueUpdateListener {
		void onQueueUpdate(Map<String, List<Customer>> queueStatus);
	}

	public void setQueueUpdateListener(QueueUpdateListener listener) {
		this.queueUpdateListener = listener;
	}
	public Engine() {
		clock = Clock.getInstance();
		
		eventList = new EventList();
		
		// Service Points are created in simu.model-package's class who is inheriting the Engine class
	}

	public void setSimulationTime(double time) {	// define how long we will run the simulation
		simulationTime = time;
	}

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
	
	private void runBEvents() {
		while (eventList.getNextEventTime() == clock.getClock()){
			runEvent(eventList.remove());
		}
	}
	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean paused) {
		this.isPaused = paused;
	}
	private double currentTime(){
		return eventList.getNextEventTime();
	}
	
	private boolean simulate(){
		return clock.getClock() < simulationTime;
	}

	protected abstract void runEvent(Event t);	// Defined in simu.model-package's class who is inheriting the Engine class

	protected abstract void tryCEvents();		// Defined in simu.model-package's class who is inheriting the Engine class

	protected abstract void initialize(); 		// Defined in simu.model-package's class who is inheriting the Engine class

	protected abstract void results(); 			// Defined in simu.model-package's class who is inheriting the Engine class
}