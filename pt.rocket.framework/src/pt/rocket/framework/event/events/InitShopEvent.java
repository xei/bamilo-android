/**
 * 
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

/**
 * Event used to initialize the api connection and get the session token
 * @author nutzer2
 *
 */
public class InitShopEvent extends MetaRequestEvent<Integer> {

	/**
	 * Base constructor
	 * @param value
	 */
	public InitShopEvent(Integer value) {
		super(EventType.INIT_SHOP, value);
	}

}
