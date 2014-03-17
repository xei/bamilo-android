/**
 * 
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

/**
 * Event used to get the phone number use in the call to order functionality. 
 * 
 * @author nunocastro
 *
 */
public class GetPromotionsEvent extends MetaRequestEvent<String> {
	private static final EventType type = EventType.GET_PROMOTIONS;
	
	public GetPromotionsEvent() {
		super(type, "");
	}

}
