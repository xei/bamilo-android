/**
 * 
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

/**
 * @author nutzer2
 *
 */
public class InitShopEvent extends MetaRequestEvent<Integer> {

	/**
	 * @param type
	 * @param value
	 */
	public InitShopEvent(Integer value) {
		super(EventType.INIT_SHOP, value);
	}

}
