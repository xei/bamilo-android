/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.DarwinRegex;

import de.akquinet.android.androlog.Log;

/**
 * @author nutzer2
 * 
 */
public class ShoppingCart implements IJSONSerializable, Parcelable {

	private static final String TAG = ShoppingCart.class.getSimpleName();

	private Map<String, ShoppingCartItem> cartItems = new HashMap<String, ShoppingCartItem>();
	private String cartValue;
	private int cartCount;
	private String vat_value;
	private String shipping_value;
	private Map<String, Map<String, String>> itemSimpleDataRegistry;

	private String cartCleanValue;

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
	
	public String getVatValue(){
		return this.vat_value;
	}
	
	public String getShippingValue(){
		return this.shipping_value;
	}
	
	/**
	 * @return the cartCleanValue
	 */
	public String getCartCleanValue() {
		return (!TextUtils.isEmpty(cartCleanValue)) ? cartCleanValue.replaceAll(DarwinRegex.CART_VALUE, "") : cartValue;
	}

	/**
	 * @param cartCleanValue the cartCleanValue to set
	 */
	public void setCartCleanValue(String cartCleanValue) {
		this.cartCleanValue = cartCleanValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException{
		cartValue = jsonObject.getString(RestConstants.JSON_CART_VALUE_TAG).replaceAll(DarwinRegex.CART_VALUE, "");
		cartCleanValue = jsonObject.optString(RestConstants.JSON_CART_CLEAN_VALUE_TAG);
		cartCount = jsonObject.getInt(RestConstants.JSON_CART_COUNT_TAG);
		vat_value = jsonObject.optString(RestConstants.JSON_CART_VAT_VALUE_TAG);
		shipping_value = jsonObject.optString(RestConstants.JSON_CART_SHIPPING_VALUE_TAG);
		if (cartCount > 0 && jsonObject.has(RestConstants.JSON_CART_ITEMS_TAG)) {
			fillCartHashMap(jsonObject.getJSONObject(RestConstants.JSON_CART_ITEMS_TAG));
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
		return null;
	}
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeMap(cartItems);
	    dest.writeString(cartValue);
	    dest.writeString(cartCleanValue);
	    dest.writeInt(cartCount);
	    dest.writeString(vat_value);
	    dest.writeString(shipping_value);
	    dest.writeMap(itemSimpleDataRegistry);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private ShoppingCart(Parcel in) {
		in.readMap(cartItems, ShoppingCartItem.class.getClassLoader());
		cartValue = in.readString();
		cartCleanValue = in.readString();
		cartCount = in.readInt();
		vat_value = in.readString();
		shipping_value = in.readString();
		in.readMap(itemSimpleDataRegistry, null);
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<ShoppingCart> CREATOR = new Parcelable.Creator<ShoppingCart>() {
        public ShoppingCart createFromParcel(Parcel in) {
            return new ShoppingCart(in);
        }

        public ShoppingCart[] newArray(int size) {
            return new ShoppingCart[size];
        }
    };

}
