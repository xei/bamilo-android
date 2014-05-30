/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
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
 * @modified Manuel Silva
 */
public class ShoppingCart implements IJSONSerializable, Parcelable {

	private static final String TAG = ShoppingCart.class.getSimpleName();

	private Map<String, ShoppingCartItem> cartItems = new HashMap<String, ShoppingCartItem>();
	private String cartValue;
	private int cartCount;
	private String vat_value;
	private String shipping_value;
	private String extra_costs;
	private String sum_costs_value;
	private boolean sum_costs;
	private Map<String, Map<String, String>> itemSimpleDataRegistry;

	private String cartCleanValue;
	private String couponDiscount;
	
	private HashMap<String, String> price_rules;
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
		cartValue = jsonObject.getString(RestConstants.JSON_CART_VALUE_TAG);
		cartCleanValue = jsonObject.optString(RestConstants.JSON_CART_CLEAN_VALUE_TAG);
		cartCount = jsonObject.getInt(RestConstants.JSON_CART_COUNT_TAG);
		vat_value = jsonObject.optString(RestConstants.JSON_CART_VAT_VALUE_TAG);
		shipping_value = jsonObject.optString(RestConstants.JSON_CART_SHIPPING_VALUE_TAG);
		couponDiscount = jsonObject.optString(RestConstants.JSON_CART_COUPON_VALUE_TAG);
		String sCosts = jsonObject.optString(RestConstants.JSON_CART_SUM_COSTS_TAG);
		if(sCosts != null && sCosts.equalsIgnoreCase("0")){
			sum_costs = false;	
		} else {
			sum_costs = true;
			sum_costs_value = jsonObject.optString(RestConstants.JSON_CART_SUM_COSTS_VALUE_TAG);
		}
		
		extra_costs = jsonObject.optString(RestConstants.JSON_CART_EXTRA_COSTS_TAG);
		if (cartCount > 0 && jsonObject.has(RestConstants.JSON_CART_ITEMS_TAG)) {
			fillCartHashMap(jsonObject.getJSONObject(RestConstants.JSON_CART_ITEMS_TAG));
		}
		
		JSONArray priceRules = jsonObject.optJSONArray(RestConstants.JSON_CART_PRICE_RULES_TAG);
		if(priceRules != null && priceRules.length() > 0){
			price_rules = new HashMap<String, String>();
			for (int i = 0; i < priceRules.length(); i++) {
				JSONObject pRulesElement = priceRules.optJSONObject(i);
				if(pRulesElement != null){
					Log.i(TAG, "code1rules : "+pRulesElement.getString(RestConstants.JSON_LABEL_TAG)+ " value : "+ pRulesElement.getString(RestConstants.JSON_VALUE_TAG));
					price_rules.put(pRulesElement.getString(RestConstants.JSON_LABEL_TAG), pRulesElement.getString(RestConstants.JSON_VALUE_TAG));
				}
			}
		}
		return true;
	}
	
	private void fillCartHashMap(JSONObject cartObject) {
		JSONObject itemObject;
		Log.d(TAG, "ON SAVE CART ITEM");

		@SuppressWarnings("rawtypes")
		Iterator iter = cartObject.keys();
		while (iter.hasNext()) {
			try {
				String key = (String) iter.next();

				itemObject = cartObject.getJSONObject(key);

				ShoppingCartItem item = new ShoppingCartItem(key, null);
				item.initialize(itemObject);

				if (itemSimpleDataRegistry.containsKey(item.getConfigSKU()))
					item.setSimpleData(itemSimpleDataRegistry.get(item.getConfigSKU()));

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
	    dest.writeBooleanArray(new boolean[]{sum_costs});
	    dest.writeString(extra_costs);
	    dest.writeString(sum_costs_value);
	    dest.writeString(couponDiscount);
	    dest.writeMap(price_rules);
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
		in.readBooleanArray(new boolean[]{sum_costs});
	    extra_costs = in.readString();
	    sum_costs_value = in.readString();
	    couponDiscount = in.readString();
	    in.readMap(price_rules, null);
    }
		
	/**
	 * @return the extra_costs
	 */
	public String getExtraCosts() {
		return extra_costs;
	}
	
	/**
     * @return the extra_costs
     */
    public String getSumCostsValue() {
        return sum_costs_value;
    }

	/**
	 * @param extra_costs the extra_costs to set
	 */
	public void setExtraCosts(String extra_costs) {
		this.extra_costs = extra_costs;
	}

	/**
	 * @return the sum_costs
	 */
	public boolean isSumCosts() {
		return sum_costs;
	}

	/**
	 * @param sum_costs the sum_costs to set
	 */
	public void setSumCosts(boolean sum_costs) {
		this.sum_costs = sum_costs;
	}

	/**
	 * @return the couponDiscount
	 */
	public String getCouponDiscount() {
		return couponDiscount;
	}

	/**
	 * @param couponDiscount the couponDiscount to set
	 */
	public void setCouponDiscount(String couponDiscount) {
		this.couponDiscount = couponDiscount;
	}

	/**
	 * @return the price_rules
	 */
	public HashMap<String, String> getPriceRules() {
		return price_rules;
	}

	/**
	 * @param price_rules the price_rules to set
	 */
	public void setPriceRules(HashMap<String, String> price_rules) {
		this.price_rules = price_rules;
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
