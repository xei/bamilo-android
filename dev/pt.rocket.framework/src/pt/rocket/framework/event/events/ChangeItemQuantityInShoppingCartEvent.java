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

import java.util.List;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;
import pt.rocket.framework.objects.ShoppingCartItem;

/**
 * Event used to change the quantity an item from the shopping cart.
 * 
 * @author GuilhermeSilva
 * 
 */
public class ChangeItemQuantityInShoppingCartEvent extends MetaRequestEvent<List<ShoppingCartItem>> {

	private static final EventType type = EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;

	/**
	 * ChangeItemQuantityInShoppingCartEvent param constructor.
	 * 
	 * @param full
	 *            items in the shopping cart list
	 */
	public ChangeItemQuantityInShoppingCartEvent(List<ShoppingCartItem> items) {
		super(type, items);
	}

}
