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
import pt.rocket.framework.objects.ShoppingCartItem;

/**
 * Event used to remove an item from the shopping cart.
 * 
 * @author GuilhermeSilva
 *
 */
public class RemoveItemFromShoppingCartEvent extends MetaRequestEvent<ShoppingCartItem> {
	private static final EventType type = EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT;

    /**
	 * @param type
	 * @param value
	 */
	public RemoveItemFromShoppingCartEvent(
			ShoppingCartItem value) {
		super(type, value);
	}



}
