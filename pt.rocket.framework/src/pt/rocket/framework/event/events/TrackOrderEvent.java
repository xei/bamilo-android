/**
 * @author Manuel Silva
 * 
 * @version 1.01
 * 
 * 2013/10/22
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;
import android.content.ContentValues;

/**
 * Event used to track an order status.
 * @author Manuel Silva
 *
 */
public class TrackOrderEvent extends MetaRequestEvent<String> {
	private static final EventType type = EventType.TRACK_ORDER_EVENT;

	public TrackOrderEvent(String value) {
		super(type, value);
	}

}
