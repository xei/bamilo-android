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
public class GetResolutionsEvent extends MetaRequestEvent<String> {
	
	public static final GetResolutionsEvent ALL = new GetResolutionsEvent();
	
	/**
	 * @param type
	 * @param value
	 */
	public GetResolutionsEvent() {
		super(EventType.GET_RESOLUTIONS, null);
	}

}
