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
	private ServicePoint[] accountTellers;
	private Map<String, List<Customer>> queueStatus = new HashMap<>();
	private SimulatorController controller;
	private final int numberOfStations;
	private double arrivalInterval;
	private int totalCustomersServed = 0;



	public MyEngine(SimulatorController controller) {
		this.controller = controller;
		this.numberOfStations = controller.getNumberOfStations();
		this.arrivalInterval = controller.getArrivalInterval();

		// Initialize queue automat
		queueAutomat = new ServicePoint(
				new Normal(1, 1),
				eventList,
				EventType.DEP_AUTOMAT
		);

		// Initialize transaction tellers
		transactionTellers = new ServicePoint[numberOfStations];
		for(int i = 0; i < numberOfStations; i++) {
			transactionTellers[i] = new ServicePoint(
					new Normal(controller.getTransactionServiceTime(), controller.getTransactionServiceTime()/2),
					eventList,
					EventType.valueOf("DEP_TELLER" + (i+1))
			);
		}

		// Initialize account tellers
		int numAccountTellers = controller.getNumberOfAccountStations();
		accountTellers = new ServicePoint[numAccountTellers];
		for(int i = 0; i < numAccountTellers; i++) {
			accountTellers[i] = new ServicePoint(
					new Normal(controller.getAccountServiceTime(), controller.getAccountServiceTime()/2),
					eventList,
					EventType.valueOf("DEP_ACCOUNT" + (i+1))
			);
		}

		// Initialize arrival process
		arrivalProcess = new ArrivalProcess(
				new Negexp(controller.getArrivalInterval()),
				eventList,
				EventType.ARR_AUTOMAT
		);
	}

	public void setArrivalInterval(double interval) {
		this.arrivalInterval = interval;
		arrivalProcess = new ArrivalProcess(
				new Negexp(arrivalInterval),
				eventList,
				EventType.ARR_AUTOMAT
		);
		arrivalProcess.generateNextEvent();  // Generate the next event immediately
	}


	@Override
	protected void initialize() {
		arrivalProcess = new ArrivalProcess(
				new Negexp(arrivalInterval),
				eventList,
				EventType.ARR_AUTOMAT
		);
		arrivalProcess.generateNextEvent();
	}

	@Override
	protected void runEvent(Event t) {
		while (isPaused()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
		long sleepTime = Math.max(0, controller.getSleepTime()); // Ensure non-negative value
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}
		try {
			Thread.sleep(controller.getSleepTime());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Customer a;
		EventType eventType = (EventType)t.getType();

		switch (eventType) {
			case ARR_AUTOMAT:
				double poissonMean;
				double percentage = controller.getClientDistribution();

				if (percentage >= 90) poissonMean = 0.01;
				else if (percentage >= 80) poissonMean = 0.1;
				else if (percentage >= 70) poissonMean = 0.3;
				else if (percentage >= 60) poissonMean = 0.5;
				else if (percentage >= 50) poissonMean = 0.7;
				else if (percentage >= 40) poissonMean = 0.9;
				else if (percentage >= 30) poissonMean = 1.1;
				else if (percentage >= 20) poissonMean = 1.3;
				else poissonMean = 1.5;

				CustomerType type = new Poisson(poissonMean).sample() == 0 ?
						CustomerType.TRANSACTION_CLIENT :
						CustomerType.ACCOUNT_CLIENT;
				System.out.println("Customer arrived: " + type);
				Customer newCustomer = new Customer(type);
				queueAutomat.addQueue(newCustomer);
				Event nextArrival = new Event(EventType.ARR_AUTOMAT,
						Clock.getInstance().getClock() + new Negexp(arrivalInterval).sample());
				eventList.add(nextArrival);
				updateQueueStatus();
				break;

			case DEP_AUTOMAT:
				a = queueAutomat.removeQueue();
				if (a != null) {
					if(a.getType() == CustomerType.TRANSACTION_CLIENT) {
						ServicePoint bestTeller = findShortestQueue(transactionTellers);
						bestTeller.addQueue(a);
					} else {
						ServicePoint bestAccountTeller = findShortestAccountQueue();
						bestAccountTeller.addQueue(a);
					}
				}
				updateQueueStatus();
				break;

			default:
				if (eventType.toString().startsWith("DEP_TELLER")) {
					int tellerIndex = Integer.parseInt(eventType.toString().substring(10)) - 1;
					if (tellerIndex < transactionTellers.length) {
						a = transactionTellers[tellerIndex].removeQueue();
						if (a != null) {
							a.setRemovalTime(Clock.getInstance().getClock());
							a.reportResults();
							totalCustomersServed++;
							controller.updateCustomerCount(totalCustomersServed);
						}
					}
				} else if (eventType.toString().startsWith("DEP_ACCOUNT")) {
					int accountIndex = Integer.parseInt(eventType.toString().substring(11)) - 1;
					if (accountIndex < accountTellers.length) {
						a = accountTellers[accountIndex].removeQueue();
						if (a != null) {
							a.setRemovalTime(Clock.getInstance().getClock());
							a.reportResults();
							totalCustomersServed++;
							controller.updateCustomerCount(totalCustomersServed);
						}
					}
				}
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

		// Check account tellers
		for (ServicePoint teller : accountTellers) {
			if (!teller.isReserved() && teller.isOnQueue()) {
				teller.beginService();
			}
		}
	}




	@Override
	protected void results() {
		StringBuilder stats = new StringBuilder();

		// Settings Section
		stats.append("\n=== Simulation Settings ===\n");
		stats.append("Number of Transaction Tellers: " + numberOfStations + "\n");
		stats.append(String.format("Client Arrival Interval: %.2f minutes\n", arrivalInterval));
		stats.append(String.format("Transaction Service Time: %.2f minutes\n", controller.getTransactionServiceTime()));
		stats.append(String.format("Account Service Time: %.2f minutes\n", controller.getAccountServiceTime()));
		stats.append(String.format("Client Distribution (Transaction/Account): %.1f%% / %.1f%%\n",
				controller.getClientDistribution(),
				(100 - controller.getClientDistribution())));

		// Results Section
		stats.append("\n=== Simulation Results ===\n");
		stats.append(String.format("Simulation ended at: %.2f minutes\n", Clock.getInstance().getClock()));
		stats.append("Total Customers Served: " + totalCustomersServed + "\n");

		// Service Points Statistics
		stats.append("\nQueue Automat Statistics:\n");
		stats.append(getServicePointStats(queueAutomat, "Queue Automat"));

		stats.append("\nTransaction Tellers Statistics:\n");
		int totalTransactionCustomers = 0;
		for(int i = 0; i < transactionTellers.length; i++) {
			totalTransactionCustomers += transactionTellers[i].getServedCustomers();
			stats.append(getServicePointStats(transactionTellers[i], "Teller " + (i+1)));
		}
		stats.append("Total Transaction Customers: " + totalTransactionCustomers + "\n");

		stats.append("\nAccount Operations Teller Statistics:\n");
		int totalAccountCustomers = 0;
		for(int i = 0; i < accountTellers.length; i++) {
			totalAccountCustomers += accountTellers[i].getServedCustomers();
			stats.append(getServicePointStats(accountTellers[i], "Account Teller " + (i+1)));
		}
		stats.append("Total Account Customers: " + totalAccountCustomers + "\n");

		controller.onSimulationComplete(stats.toString());
	}

	private String getServicePointStats(ServicePoint sp, String name) {
		return name + ":\n" +
				"  Customers Served: " + sp.getServedCustomers() + "\n" +
				String.format("  Average Service Time: %.2f minutes\n", sp.getAverageServiceTime()) +
				String.format("  Average Queue Time: %.2f minutes\n", sp.getAverageQueueTime());
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


private ServicePoint findShortestAccountQueue() {
	ServicePoint shortest = accountTellers[0];
	for(ServicePoint sp : accountTellers) {
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

		for(int i = 0; i < transactionTellers.length; i++) {
			queueStatus.put("teller" + (i+1), transactionTellers[i].getQueueCustomers());
		}

		for(int i = 0; i < accountTellers.length; i++) {
			queueStatus.put("account" + (i+1), accountTellers[i].getQueueCustomers());
		}

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
