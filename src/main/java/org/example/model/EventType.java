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
	DEP_TELLER6,    // Departure from teller 6
	DEP_TELLER7,
	DEP_ACCOUNT;	// Departure from teller 7

	public static EventType getTellerDeparture(int tellerNumber) {
		return valueOf("DEP_TELLER" + tellerNumber);
	}
}