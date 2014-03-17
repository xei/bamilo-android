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
 * Event used to trigger the forgot passord functionality from the remote api.
 * @author nunocastro
 *
 */
public class ForgetPasswordEvent extends MetaRequestEvent<ContentValues> {

	private static final EventType type = EventType.FORGET_PASSWORD_EVENT;

    /**
	 * @param type
	 * @param value
	 */
	public ForgetPasswordEvent(ContentValues value) {
		super(type, value);
		// TODO Auto-generated constructor stub
	}





}
