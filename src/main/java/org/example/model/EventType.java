package org.example.model;

import org.example.framework.IEventType;

/**
 * This enum represents the type of an event in the simulation.
 * ARR_AUTOMAT: Arrival to Queue automat
 * DEP_AUTOMAT: Departure from Queue automat
 * ARR_TELLER: Arrival to teller queue
 * DEP_TELLER1: Departure from teller 1
 * DEP_TELLER2: Departure from teller 2
 * DEP_TELLER3: Departure from teller 3
 * DEP_TELLER4: Departure from teller 4
 * DEP_TELLER5: Departure from teller 5
 * DEP_ACCOUNT1: Departure from account 1
 * DEP_ACCOUNT2: Departure from account 2
 * DEP_ACCOUNT3: Departure from account 3
 * DEP_ACCOUNT4: Departure from account 4
 * DEP_ACCOUNT5: Departure from account 5
 */

public enum EventType implements IEventType {
	/** Customer arrives at queue automat */
	ARR_AUTOMAT,
	/** Customer departs from queue automat */
	DEP_AUTOMAT,
	/** Customer arrives at teller */
	ARR_TELLER,
	/** Customer departs from teller 1 */
	DEP_TELLER1,
	/** Customer departs from teller 2 */
	DEP_TELLER2,
	/** Customer departs from teller 3 */
	DEP_TELLER3,
	/** Customer departs from teller 4 */
	DEP_TELLER4,
	/** Customer departs from teller 5 */
	DEP_TELLER5,
	/** Customer departs from account teller 1 */
	DEP_ACCOUNT1,
	/** Customer departs from account teller 2 */
	DEP_ACCOUNT2,
	/** Customer departs from account teller 3 */
	DEP_ACCOUNT3,
	/** Customer departs from account teller 4 */
	DEP_ACCOUNT4,
	/** Customer departs from account teller 5 */
	DEP_ACCOUNT5;

}