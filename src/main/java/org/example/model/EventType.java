package org.example.model;

import org.example.framework.IEventType;

public enum EventType implements IEventType {
	ARR_AUTOMAT,    // Arrival to Queue automat
	DEP_AUTOMAT,    // Departure from Queue automat
	ARR_TELLER,     // Arrival to teller queue
	DEP_TELLER1,    // Departure from teller 1
	DEP_TELLER2,    // Departure from teller 2
	DEP_TELLER3,    // Departure from teller 3
	DEP_TELLER4,    // Departure from teller 4
	DEP_TELLER5,    // Departure from teller 5
	DEP_ACCOUNT1,
	DEP_ACCOUNT2,
	DEP_ACCOUNT3,
	DEP_ACCOUNT4,
	DEP_ACCOUNT5;


	public static EventType getTellerDeparture(int tellerNumber) {
		return valueOf("DEP_TELLER" + tellerNumber);
	}
}