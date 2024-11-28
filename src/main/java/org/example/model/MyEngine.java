package org.example.model;

import org.example.eduni.distributions.Negexp;
import org.example.eduni.distributions.Normal;
import org.example.eduni.distributions.Poisson;
import org.example.framework.ArrivalProcess;
import org.example.framework.Clock;
import org.example.framework.Engine;
import org.example.framework.Event;
import org.example.controller.SimulatorController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyEngine extends Engine {
	private ArrivalProcess arrivalProcess;
	private ServicePoint queueAutomat; // Queue number dispenser
	private ServicePoint[] transactionTellers;
	private ServicePoint accountTeller;
	private Map<String, List<Customer>> queueStatus = new HashMap<>();
	private SimulatorController controller;



	public MyEngine(SimulatorController controller) {
		this.controller = controller;
		// Initialize queue number automat (very fast service, mean=1 min, std=0.5)
		queueAutomat = new ServicePoint(
				new Normal(2, 0.5),
				eventList,
				EventType.DEP_AUTOMAT
		);

		// Initialize 3 transaction tellers (mean=10 min, std=5)
		transactionTellers = new ServicePoint[3];
		for(int i = 0; i < 3; i++) {
			transactionTellers[i] = new ServicePoint(
					new Normal(20, 5),
					eventList,
					EventType.valueOf("DEP_TELLER" + (i+1))
			);
		}

		// Initialize account operations teller (mean=20 min, std=10)
		accountTeller = new ServicePoint(
				new Normal(40, 10),
				eventList,
				EventType.DEP_TELLER4
		);

		// Customer arrivals follow negative exponential distribution (mean=3 min)
		arrivalProcess = new ArrivalProcess(
				new Negexp(3),
				eventList,
				EventType.ARR_AUTOMAT
		);

	}

	@Override
	protected void initialize() {
		arrivalProcess.generateNextEvent();
	}

	@Override
	protected void runEvent(Event t) {
		try {
			Thread.sleep(controller.getSleepTime());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Customer a;

		switch ((EventType)t.getType()) {
			case ARR_AUTOMAT:
				// Customer arrives at queue automat
				CustomerType type = new Poisson(0.2).sample() == 0 ?
						CustomerType.TRANSACTION_CLIENT :
						CustomerType.ACCOUNT_CLIENT;
				System.out.println("Customer arrived: " + type);
				Customer newCustomer = new Customer(type);
				queueAutomat.addQueue(newCustomer);
				arrivalProcess.generateNextEvent();
				updateQueueStatus();
				break;

			case DEP_AUTOMAT:
				// Customer gets queue number and moves to appropriate queue
				a = queueAutomat.removeQueue();
				if(a.getType() == CustomerType.TRANSACTION_CLIENT) {
					ServicePoint bestTeller = findShortestQueue(transactionTellers);
					bestTeller.addQueue(a);
				} else {
					accountTeller.addQueue(a);
				}
				updateQueueStatus();
				break;

			case DEP_TELLER1:
				a = transactionTellers[0].removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
				updateQueueStatus();
				break;

			case DEP_TELLER2:
				a = transactionTellers[1].removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
				updateQueueStatus();
				break;

			case DEP_TELLER3:
				a = transactionTellers[2].removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
				updateQueueStatus();
				break;

			case DEP_TELLER4:
				a = accountTeller.removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
				updateQueueStatus();
				break;
		}
	}

	@Override
	protected void tryCEvents() {
		// Check queue automat
		if (!queueAutomat.isReserved() && queueAutomat.isOnQueue()) {
			queueAutomat.beginService();
		}

		// Check transaction tellers
		for (ServicePoint teller : transactionTellers) {
			if (!teller.isReserved() && teller.isOnQueue()) {
				teller.beginService();
			}
		}

		// Check account teller
		if (!accountTeller.isReserved() && accountTeller.isOnQueue()) {
			accountTeller.beginService();
		}
	}

	@Override
	protected void results() {
		System.out.println("\n========= Simulation Results =========");
		System.out.println("Simulation ended at: " + Clock.getInstance().getClock());

		System.out.println("\nQueue Automat Statistics:");
		printServicePointStats(queueAutomat, "Queue Automat");

		System.out.println("\nTransaction Tellers Statistics:");
		for(int i = 0; i < transactionTellers.length; i++) {
			printServicePointStats(transactionTellers[i], "Teller " + (i+1));
		}

		System.out.println("\nAccount Operations Teller Statistics:");
		printServicePointStats(accountTeller, "Account Teller");
	}

	private ServicePoint findShortestQueue(ServicePoint[] points) {
		ServicePoint shortest = points[0];
		for(ServicePoint sp : points) {
			if(sp.getQueueLength() < shortest.getQueueLength()) {
				shortest = sp;
			}
		}
		return shortest;
	}

	private void updateQueueStatus() {
		System.out.println("Engine updating queue status");
		queueStatus.clear();
		queueStatus.put("automat", queueAutomat.getQueueCustomers());
		queueStatus.put("teller1", transactionTellers[0].getQueueCustomers());
		queueStatus.put("teller2", transactionTellers[1].getQueueCustomers());
		queueStatus.put("teller3", transactionTellers[2].getQueueCustomers());
		queueStatus.put("account", accountTeller.getQueueCustomers());

		System.out.println("Current queue status: " + queueStatus);
		notifyQueueUpdate(queueStatus);
	}

	public void setQueueUpdateListener(QueueUpdateListener listener) {
		this.queueUpdateListener = listener;
	}

	private void notifyQueueUpdate(Map<String, List<Customer>> queueStatus) {
		System.out.println("Engine notifying listener with queue status");
		if (queueUpdateListener != null) {
			System.out.println("Listener is present, calling onQueueUpdate");
			queueUpdateListener.onQueueUpdate(new HashMap<>(queueStatus));  // Send a copy of the map
		} else {
			System.out.println("No queue update listener registered");
		}
	}





	private void printServicePointStats(ServicePoint sp, String name) {
		System.out.println(name + ":");
		System.out.println("  Average Service Time: " + sp.getAverageServiceTime());
		System.out.println("  Average Queue Time: " + sp.getAverageQueueTime());

	}
}
