package simu.model;

import simu.framework.IEventType;

public enum EventType implements IEventType {
	ARR_AUTOMAT, // Arrival to Queue automat
	DEP_AUTOMAT, // Departure from Queue automat
	ARR_TELLER, // Arrival to teller queue
	DEP_TELLER1, // Departure from teller 1 (deposit/withdraw)
	DEP_TELLER2, // Departure from teller 2 (deposit/withdraw)
	DEP_TELLER3, // Departure from teller 3 (deposit/withdraw)
	DEP_TELLER4; // Departure from teller 4 (account operations)
}