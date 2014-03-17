/** 
 * Event used to add new items to the customer wishlist. The assigned service
 * responds using the AddItemsToWishListCompletedEvent
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

import java.util.Collection;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

/**
 * Event used to add new items to the customer wishlist. The assigned service
 * responds using the AddItemsToWishListCompletedEvent
 * 
 * @author GuilhermeSilva
 * 
 *         Deprecated since the wishlist activity is deprecated
 */
public class ModifyWishListEvent extends MetaRequestEvent<Collection<String>> {

	/**
	 * Base constructor
	 * @param type the type of the event
	 * @param productSkus sku of the stored products
	 */
	public ModifyWishListEvent(EventType type, Collection<String> productSkus) {
		super(type, productSkus);
	}

}
