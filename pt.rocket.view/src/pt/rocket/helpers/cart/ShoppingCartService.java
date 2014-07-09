package pt.rocket.helpers.cart;
///**
// * @author Guilherme Silva
// * @modified Manuel Silva
// * @version 1.01
// * 
// * 2012/06/18
// * 
// * Copyright (c) Rocket Internet All Rights Reserved
// */
//package pt.rocket.helpers;
//
//import java.util.EnumSet;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import pt.rocket.framework.objects.MinOrderAmount;
//import pt.rocket.framework.objects.ShoppingCart;
//import pt.rocket.framework.objects.ShoppingCartItem;
//import pt.rocket.framework.utils.LogTagHelper;
//import android.content.ContentValues;
//import android.net.Uri;
//import android.os.Bundle;
//import de.akquinet.android.androlog.Log;
//
///**
// * Service that manages the shopping cart.
// * 
// * @author GuilhermeSilva
// * 
// */
//public class ShoppingCartService {
//
//	private static final String TAG = LogTagHelper.create(ShoppingCartService.class);
//
//	/**
//	 * Registry of the the product sku/simpleData arraylist
//	 */
//	Map<String, Map<String, String>> itemSimpleDataRegistry =
//			new HashMap<String, Map<String,String>>();
//
//	private ShoppingCart cart;
//	
//	private ResponseListener loginLogoutListener = new ResponseListener() {
//		
//		@Override
//		public boolean removeAfterHandlingEvent() {
//			return false;
//		}
//		
//		@Override
//		public void handleEvent(ResponseEvent event) {
//			if (event.getSuccess()) {
//				Log.d(TAG, "Handling event: " + event);
//				handleShoppingCartEvent(GetShoppingCartItemsEvent.FORCE_API_CALL);
//			}
//		}
//		
//		@Override
//		public String getMD5Hash() {
//			return null;
//		}
//	};
//
//	/**
//	 * @param handledEvents
//	 */
//	public ShoppingCartService() {
//		super(EnumSet.noneOf(EventType.class), EnumSet.of(EventType.INIT_SHOP,
//				EventType.GET_SHOPPING_CART_ITEMS_EVENT,
//				EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
//				EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT,
//				EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT));
//		EventManager.getSingleton().addResponseListener(
//				loginLogoutListener,
//				EnumSet.of(EventType.LOGOUT_EVENT, EventType.LOGIN_EVENT,
//						EventType.REGISTER_ACCOUNT_EVENT));
//	}
//
//	/**
//	 * Adds item to the shopping cart.
//	 * 
//	 * @param item
//	 *            to be added. Must contain product sku, product simple sku and quantity.
//	 */
//	public void handleShoppingCartEvent(AddItemToShoppingCartEvent event) {
//
////		ShoppingCartItem item = event.value;
////
////		ContentValues values = new ContentValues();
////
////		// add the simple data to the registry
////		if (item.getSimpleData() != null) {
////			itemSimpleDataRegistry.put(item.getConfigSKU(), item.getSimpleData());
////		}
////
////		values.put("p", item.getConfigSKU());
////		values.put("sku", item.getConfigSimpleSKU());
////		values.put("quantity", "" + item.getQuantity());
////
////		RestServiceHelper.requestPost(event.eventType.action, values,
////				new ShoppingCartResponseReceiver(event), event.metaData);
//	}
//
//
//	/**
//	 * Gets the items ands triggers the GetShoppingCartItemsCompletedEvent.
//	 */
//	public void handleShoppingCartEvent(GetShoppingCartItemsEvent event) {
//
////		Log.d(TAG, "getItems: going to get the shopping cart items.");
////		if (!event.value) {
////			if ( cart == null) {
////				cart = new ShoppingCart(itemSimpleDataRegistry);
////			}
////			Log.d(TAG, "getItems: getting local data.");
////			EventManager.getSingleton().triggerResponseEvent(
////					new ResponseResultEvent<ShoppingCart>(event, cart, null, new Bundle()));
////		} else {
////			Log.d(TAG, "getItems: getting api data.");
////			RestServiceHelper.requestGet(event.eventType.action,
////					new ShoppingCartResponseReceiver(event), event.metaData);
////		}
//	}
//
//	private void handleShoppingCartEvent(ChangeItemQuantityInShoppingCartEvent event) {
//
////		ContentValues values = new ContentValues();
////		for (ShoppingCartItem item : event.value) {
////			values.put("qty_" + item.getConfigSimpleSKU(), String.valueOf(item.getQuantity()));
////		}
////		RestServiceHelper.requestPost(event.eventType.action, values,
////				new ShoppingCartResponseReceiver(event), event.metaData);
//	}
//	
//	private void handleMinOrderAmountEvent(RequestEvent event) {
//		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
//				new ResponseReceiver<MinOrderAmount>(event) {
//
//					@Override
//					public MinOrderAmount parseResponse(JSONObject metadataObject)
//							throws JSONException {
//						MinOrderAmount minAmount = new MinOrderAmount();
//						minAmount.initialize(metadataObject);
//						return minAmount;
//					}
//				}, event.metaData);
//	}
//
//	private void updateShoppingCartItems(JSONObject metadataObject) throws JSONException {
////		this.cart = null;
////		ShoppingCart cart = new ShoppingCart(itemSimpleDataRegistry);
////		cart.initialize(metadataObject);
////		this.cart = cart;
//	}
//
//	private class ShoppingCartResponseReceiver extends ResponseReceiver<ShoppingCart> {
//
//		/**
//		 * @param requestEvent
//		 */
//		public ShoppingCartResponseReceiver(RequestEvent requestEvent) {
//			super(requestEvent);
//		}
//
//		@Override
//		public ShoppingCart parseResponse(JSONObject response) throws JSONException {
//			updateShoppingCartItems(response);
//			return cart;
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see pt.rocket.framework.event.RequestListener#handleEvent(pt.rocket.framework.event.IEvent)
//	 */
//	@Override
//	public void handleEvent(RequestEvent event) {
//		switch (event.eventType) {
//		case INIT_SHOP:
//			itemSimpleDataRegistry = new HashMap<String, Map<String, String>>();
//			cart = null;
//			break;
//		case ADD_ITEM_TO_SHOPPING_CART_EVENT:
//			handleShoppingCartEvent((AddItemToShoppingCartEvent) event);
//			break;
//		case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
//			handleShoppingCartEvent((RemoveItemFromShoppingCartEvent) event);
//			break;
//		case GET_SHOPPING_CART_ITEMS_EVENT:
//			handleShoppingCartEvent((GetShoppingCartItemsEvent) event);
//			break;
//		case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
//			handleShoppingCartEvent((ChangeItemQuantityInShoppingCartEvent) event);
//			break;
////		case GET_MIN_ORDER_AMOUNT:
////			handleMinOrderAmountEvent(event);
////			break;
//		}
//	}
//}
