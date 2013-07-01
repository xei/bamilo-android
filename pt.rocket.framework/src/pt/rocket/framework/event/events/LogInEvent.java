/**
 * Event used to log in the user. Any class that triggers this event can listen
 * the response (LogInCompletedEvent).
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
 * Event used to log in the user. Any class that triggers this event can listen
 * the response (LogInCompletedEvent).
 * 
 * @author Guilherme Silva
 * 
 */
public class LogInEvent extends MetaRequestEvent<ContentValues> {
	
	public final static LogInEvent TRY_AUTO_LOGIN = new LogInEvent(null);
	

	/**
	 * @param type
	 * @param value
	 */
	public LogInEvent(ContentValues value) {
		super(EventType.LOGIN_EVENT, value);
	}

}
