/**
 * 
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

/**
 * Event used to get the categories from the api
 * @author nutzer2
 *
 */
public class GetCategoriesEvent extends MetaRequestEvent<String> {
	
	public static final GetCategoriesEvent ALL = new GetCategoriesEvent(null);
	
	/**
	 * @param type
	 * @param value
	 */
	public GetCategoriesEvent(String value) {
		super(EventType.GET_CATEGORIES_EVENT, value);
	}

}
