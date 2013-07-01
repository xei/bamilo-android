/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * @author nutzer2
 * 
 */
public class ShoppingCart implements IJSONSerializable {

	private static final String TAG = ShoppingCart.class.getSimpleName();

	private static final String JSON_CART_VALUE_TAG = "cartValue";
	private static final String JSON_CART_COUNT_TAG = "cartCount";
	private static final String JSON_CART_ITEMS_TAG = "cartItems";

	private Map<String, ShoppingCartItem> cartItems = new HashMap<String, ShoppingCartItem>();
	private String cartValue;
	private int cartCount;
	private Map<String, Map<String, String>> itemSimpleDataRegistry;

	/**
	 * 
	 */
	public ShoppingCart(Map<String, Map<String, String>> itemSimpleDataRegistry) {
		this.itemSimpleDataRegistry = itemSimpleDataRegistry;
	}

	/**
	 * @return the cartItems
	 */
	public Map<String, ShoppingCartItem> getCartItems() {
		return cartItems;
	}

	/**
	 * @param cartItems the cartItems to set
	 */
	public void setCartItems(HashMap<String, ShoppingCartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/**
	 * @return the cartValue
	 */
	public String getCartValue() {
		return cartValue;
	}

	/**
	 * @param cartValue the cartValue to set
	 */
	public void setCartValue(String cartValue) {
		this.cartValue = cartValue;
	}

	/**
	 * @return the cartCount
	 */
	public int getCartCount() {
		return cartCount;
	}

	/**
	 * @param cartCount the cartCount to set
	 */
	public void setCartCount(int cartCount) {
		this.cartCount = cartCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException{

		cartValue = jsonObject.getString(JSON_CART_VALUE_TAG);
		cartCount = jsonObject.getInt(JSON_CART_COUNT_TAG);
		if (cartCount > 0 && jsonObject.has(JSON_CART_ITEMS_TAG)) {
			fillCartHashMap(jsonObject.getJSONObject(JSON_CART_ITEMS_TAG));
		}
		return true;
	}
	
	private void fillCartHashMap(JSONObject cartObject) {
		JSONObject itemObject;
		Log.d(TAG, "fillCartHashMap: Filling the shopping cart.");

		@SuppressWarnings("rawtypes")
		Iterator iter = cartObject.keys();
		while (iter.hasNext()) {
			try {
				String key = (String) iter.next();

				itemObject = cartObject.getJSONObject(key);

				ShoppingCartItem item = new ShoppingCartItem(key, null);
				item.initialize(itemObject);

				if (itemSimpleDataRegistry.containsKey(item
								.getConfigSKU())) {
					item.setSimpleData(itemSimpleDataRegistry.get(item
							.getConfigSKU()));
				}

				cartItems.put(key, item);

			} catch (JSONException e) {
				Log.e(TAG, "fillCartHashMap: error", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
