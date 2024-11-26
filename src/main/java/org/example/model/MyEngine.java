package org.example.model;

import org.example.eduni.distributions.Negexp;
import org.example.eduni.distributions.Normal;
import org.example.framework.ArrivalProcess;
import org.example.framework.Clock;
import org.example.framework.Engine;
import org.example.framework.Event;

public class MyEngine extends Engine {
	private ArrivalProcess arrivalProcess;
	private ServicePoint queueAutomat; // Queue number dispenser
	private ServicePoint[] transactionTellers;
	private ServicePoint accountTeller;

	public MyEngine() {
		// Initialize queue number automat (very fast service, mean=1 min, std=0.5)
		queueAutomat = new ServicePoint(
				new Normal(1, 0.5),
				eventList,
				EventType.DEP_AUTOMAT
		);

		// Initialize 3 transaction tellers (mean=8 min, std=3)
		transactionTellers = new ServicePoint[3];
		for(int i = 0; i < 3; i++) {
			transactionTellers[i] = new ServicePoint(
					new Normal(10, 5),
					eventList,
					EventType.valueOf("DEP_TELLER" + (i+1))
			);
		}

		// Initialize account operations teller (mean=15 min, std=5)
		accountTeller = new ServicePoint(
				new Normal(20, 10),
				eventList,
				EventType.DEP_TELLER4
		);

		// Customer arrivals follow negative exponential distribution (mean=10 min)
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
		Customer a;

		switch ((EventType)t.getType()) {
			case ARR_AUTOMAT:
				// Customer arrives at queue automat
				CustomerType type = Math.random() < 0.7 ?
						CustomerType.TRANSACTION_CLIENT :
						CustomerType.ACCOUNT_CLIENT;

				Customer newCustomer = new Customer(type);
				queueAutomat.addQueue(newCustomer);
				arrivalProcess.generateNextEvent();
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
				break;

			case DEP_TELLER1:
				a = transactionTellers[0].removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
				break;

			case DEP_TELLER2:
				a = transactionTellers[1].removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
				break;

			case DEP_TELLER3:
				a = transactionTellers[2].removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
				break;

			case DEP_TELLER4:
				a = accountTeller.removeQueue();
				a.setRemovalTime(Clock.getInstance().getClock());
				a.reportResults();
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

	private void printServicePointStats(ServicePoint sp, String name) {
		System.out.println(name + ":");
		System.out.println("  Average Service Time: " + sp.getAverageServiceTime());
		System.out.println("  Average Queue Time: " + sp.getAverageQueueTime());

	}
}
