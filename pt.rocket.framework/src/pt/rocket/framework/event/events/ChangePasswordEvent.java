/**
 * 
 * @author Nuno Castro
 * 
 * @version 1.01
 * 
 * 2012/08/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;
import android.content.ContentValues;

/**
 * Event used to change the customer password.
 * 
 * @author nunocastro
 * 
 */
public class ChangePasswordEvent extends MetaRequestEvent<ContentValues> {
	private static final EventType type = EventType.CHANGE_PASSWORD_EVENT;

	/**
	 * Base Constructor
	 * @param type
	 * @param value
	 */
	public ChangePasswordEvent(ContentValues value) {
		super(type, value);
		// TODO Auto-generated constructor stub
	}


}
