/**
 * Event used to register the customer in the database. After the register is completed the customer is automatically log in the system.
 * Listen for the event RegisterAccountEvent for the response of this event.
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;
import android.content.ContentValues;

/**
 * Event used to register a certain customer.
 * 
 * @author GuilhermeSilva
 * 
 */
public class RegisterAccountEvent extends MetaRequestEvent<ContentValues> {
	/**
	 * @param type
	 * @param value
	 */
	public RegisterAccountEvent(ContentValues value) {
		super(type, value);
	}

	private static final EventType type = EventType.REGISTER_ACCOUNT_EVENT;

}
