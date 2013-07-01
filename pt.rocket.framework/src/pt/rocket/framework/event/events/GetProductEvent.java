/** 
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

/**
 * Event used to get a complete product from the mobile api.
 * 
 * @author GuilhermeSilva
 *
 */
public class GetProductEvent extends MetaRequestEvent<String> {
	private static EventType type = EventType.GET_PRODUCT_EVENT;

    /**
	 * @param type
	 * @param value
	 */
	public GetProductEvent(String value) {
		super(type, value);
	}



}
