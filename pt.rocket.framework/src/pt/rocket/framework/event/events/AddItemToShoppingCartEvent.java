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

public class AddItemToShoppingCartEvent extends MetaRequestEvent<ShoppingCartItem> {
	private static final EventType type = EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT;

	public AddItemToShoppingCartEvent(ShoppingCartItem item) {
		super(type, item);
	}

}
