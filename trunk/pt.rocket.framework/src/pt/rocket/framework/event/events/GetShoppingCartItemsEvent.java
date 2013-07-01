/** 
 * @author nunocastro
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
 * Event used to get the list of shopping cart items from the service.
 * @author nunocastro
 */
public class GetShoppingCartItemsEvent extends MetaRequestEvent<Boolean> {
	
	public static final GetShoppingCartItemsEvent FORCE_API_CALL = new GetShoppingCartItemsEvent(
			true);

	public static final GetShoppingCartItemsEvent GET_FROM_CACHE = new GetShoppingCartItemsEvent(
			false);

	/**
	 * specifies if the should be the ones on service or if they should come
	 * from the server.
	 * 
	 * @param forceAPICall
	 */
	private GetShoppingCartItemsEvent(boolean forceAPICall) {
		super(EventType.GET_SHOPPING_CART_ITEMS_EVENT, forceAPICall);
	}

}
