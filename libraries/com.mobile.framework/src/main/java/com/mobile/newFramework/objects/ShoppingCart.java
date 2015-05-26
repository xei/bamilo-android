/**
 * 
 */
package com.mobile.newFramework.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.DarwinRegex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author nutzer2
 * @modified Manuel Silva
 */
public class ShoppingCart implements IJSONSerializable, Parcelable {

	private static final String TAG = ShoppingCart.class.getSimpleName();

	private Map<String, ShoppingCartItem> mCartItems = new HashMap<>();
	private String mCartValue;
	private double mCartValueAsDouble = 0d;
	private double mCartValueConverted = 0d;
	private int mCartCount;
	private String mVatValue;
	private double mShippingValue;
	private double mExtraCosts;
	private String mSumCostsValue;
	private boolean hasSumCosts;

	private String mCartCleanValue;
	private String mCouponDiscount;
	private String mCouponCode;

	private HashMap<String, String> mPriceRules;

    private String mSubTotal;
	private double mSubTotalDouble = 0d;
    private double mSubTotalConvertedDouble = 0d;

	/**
	 * Constructor
	 */
	public ShoppingCart() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		// Get cart value as string and double
		mCartValue = jsonObject.getString(RestConstants.JSON_CART_VALUE_TAG);
		mCartValueAsDouble = jsonObject.optDouble(RestConstants.JSON_CART_VALUE_TAG, 0);
        // Get cart sub total
        mSubTotal = jsonObject.optString(RestConstants.JSON_CART_SUB_TOTAL);
        mSubTotalDouble = jsonObject.optDouble(RestConstants.JSON_CART_SUB_TOTAL,0d);
        mSubTotalConvertedDouble = jsonObject.optDouble(RestConstants.JSON_CART_SUB_TOTAL_CONVERTED, 0d);
		// Get cart value converted
		mCartValueConverted = jsonObject.optDouble(RestConstants.JSON_CART_VALUE_CONVERTED_TAG, 0d);
		// Get cart clean value
		mCartCleanValue = jsonObject.optString(RestConstants.JSON_CART_CLEAN_VALUE_TAG);
		mCartCount = jsonObject.getInt(RestConstants.JSON_CART_COUNT_TAG);
		mVatValue = jsonObject.optString(RestConstants.JSON_CART_VAT_VALUE_TAG);
		setShippingValue(jsonObject.optDouble(RestConstants.JSON_CART_SHIPPING_VALUE_TAG));
		mCouponDiscount = jsonObject.optString(RestConstants.JSON_CART_COUPON_VALUE_TAG);
		mCouponCode = jsonObject.optString(RestConstants.JSON_CART_COUPON_CODE_TAG);
		String sCosts = jsonObject.optString(RestConstants.JSON_CART_SUM_COSTS_TAG);
		if (sCosts != null && sCosts.equals("0")) {
			hasSumCosts = false;
		} else {
			hasSumCosts = true;
			mSumCostsValue = jsonObject.optString(RestConstants.JSON_CART_SUM_COSTS_VALUE_TAG);
		}

		mExtraCosts = jsonObject.optDouble(RestConstants.JSON_CART_EXTRA_COSTS_TAG, 0);
		if (mCartCount > 0 && jsonObject.has(RestConstants.JSON_CART_ITEMS_TAG)) {
			fillCartHashMap(jsonObject.getJSONObject(RestConstants.JSON_CART_ITEMS_TAG));
		}

		JSONArray priceRules = jsonObject.optJSONArray(RestConstants.JSON_CART_PRICE_RULES_TAG);
		if (priceRules != null && priceRules.length() > 0) {
			mPriceRules = new HashMap<>();
			for (int i = 0; i < priceRules.length(); i++) {
				JSONObject pRulesElement = priceRules.optJSONObject(i);
				if (pRulesElement != null) {
					System.out.println("code1rules : " + pRulesElement.getString(RestConstants.JSON_LABEL_TAG) + " value : "
							+ pRulesElement.getString(RestConstants.JSON_VALUE_TAG));
					mPriceRules.put(pRulesElement.getString(RestConstants.JSON_LABEL_TAG),
							pRulesElement.getString(RestConstants.JSON_VALUE_TAG));
				}
			}
		}

		System.out.println("CART INIT: " + mCartValue + " " + mCartValueAsDouble + " " + mCartValueConverted + " " + mCouponCode);

		return true;
	}

	/**
	 *
	 * @param cartObject
	 */
	private void fillCartHashMap(JSONObject cartObject) {
		JSONObject itemObject;
		System.out.println("ON SAVE CART ITEM");

		@SuppressWarnings("rawtypes")
		Iterator iter = cartObject.keys();
		while (iter.hasNext()) {
			try {
				String key = (String) iter.next();
				itemObject = cartObject.getJSONObject(key);
				ShoppingCartItem item = new ShoppingCartItem(key, null);
				item.initialize(itemObject);
				mCartItems.put(key, item);
			} catch (JSONException e) {
				System.out.println("fillCartHashMap: error" +e.toString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public RequiredJson getRequiredJson() {
		return RequiredJson.METADATA;
	}

	/*
	 * ########### GETTERS AND SETTERS ###########
	 */

	/**
	 * @return the cartItems
	 */
	public Map<String, ShoppingCartItem> getCartItems() {
		return mCartItems;
	}

	/**
	 * @param cartItems
	 *            the cartItems to set
	 */
	public void setCartItems(HashMap<String, ShoppingCartItem> cartItems) {
		this.mCartItems = cartItems;
	}

	/**
	 * @return the cartValue
	 */
	public String getCartValue() {
		return mCartValue;
	}

	/**
	 * @param cartValue
	 *            the cartValue to set
	 */
	public void setCartValue(String cartValue) {
		this.mCartValue = cartValue;
	}

	/**
	 * @return the cartCount
	 */
	public int getCartCount() {
		return mCartCount;
	}

	/**
	 * @param cartCount
	 *            the cartCount to set
	 */
	public void setCartCount(int cartCount) {
		this.mCartCount = cartCount;
	}

	public String getVatValue() {
		return this.mVatValue;
	}

	public double getShippingValue() {
		return mShippingValue;
	}

	/**
	 * @return the cartCleanValue
	 */
	public String getCartCleanValue() {
		return (!TextUtils.isEmpty(mCartCleanValue)) ? mCartCleanValue.replaceAll(DarwinRegex.CART_VALUE, "") : mCartValue;
	}

	/**
	 * @param cartCleanValue
	 *            the cartCleanValue to set
	 */
	public void setCartCleanValue(String cartCleanValue) {
		this.mCartCleanValue = cartCleanValue;
	}

	/**
	 * @return the extra_costs
	 */
	public double getExtraCosts() {
		return mExtraCosts;
	}

	/**
	 * @return the extra_costs
	 */
	public String getSumCostsValue() {
		return mSumCostsValue;
	}

	/**
	 * @param extraCosts
	 *            the extra_costs to set
	 */
	public void setExtraCosts(double extraCosts) {
		this.mExtraCosts = extraCosts;
	}

	/**
	 * @return the sum_costs
	 */
	public boolean hasSumCosts() {
		return hasSumCosts;
	}

	/**
	 * @param sum_costs
	 *            the sum_costs to set
	 */
	public void setSumCosts(boolean sum_costs) {
		this.hasSumCosts = sum_costs;
	}

	/**
	 * @return the couponDiscount
	 */
	public String getCouponDiscount() {
		return mCouponDiscount;
	}

	/**
	 * @param couponDiscount
	 *            the couponDiscount to set
	 */
	public void setCouponDiscount(String couponDiscount) {
		this.mCouponDiscount = couponDiscount;
    }

	/**
	 *
	 */
    public String getCouponCode() {
        return mCouponCode;
    }

    /**
     *
     */
    public void setCouponCode(String couponCode) {
        this.mCouponCode = couponCode;
    }

	/**
	 * @return the price_rules
	 */
	public HashMap<String, String> getPriceRules() {
		return mPriceRules;
	}

	/**
	 * @param price_rules
	 *            the price_rules to set
	 */
	public void setPriceRules(HashMap<String, String> price_rules) {
		this.mPriceRules = price_rules;
	}

	/**
	 * @return the mCartValueAsDouble
	 */
	public double getCartValueAsDouble() {
		return mCartValueAsDouble;
	}

	/**
	 * @param cartValueAsDouble the mCartValueAsDouble to set
	 */
	public void setCartValueAsDouble(double cartValueAsDouble) {
		this.mCartValueAsDouble = cartValueAsDouble;
	}

	/**
	 * @return the mCartValueEuroConverted
	 */
	public double getCartValueEuroConverted() {
		return mCartValueConverted;
	}

	/**
	 * @param cartValueEuroConverted the mCartValueEuroConverted to set
	 */
	public void setCartValueEuroConverted(double cartValueEuroConverted) {
		this.mCartValueConverted = cartValueEuroConverted;
	}

	/**
	 * Return the total price used for tracking
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		System.out.println("PRICE VALUE FOR TRACKING: " + mCartValueAsDouble + " " + mCartValueConverted);
		return mCartValueConverted;
	}

    public double getSubTotalDouble() {
        return mSubTotalDouble;
    }

    public void setSubTotalDouble(double mSubTotal) {
        this.mSubTotalDouble = mSubTotal;
    }

    public double getmubTotalConvertedDouble() {
        return mSubTotalConvertedDouble;
    }

    public void setSubTotalConvertedDouble(double mSubTotalConverted) {
        this.mSubTotalConvertedDouble = mSubTotalConverted;
    }

    public String getSubTotal() {
        return mSubTotal;
    }

    public void setSubTotal(String mSubTotal) {
        this.mSubTotal = mSubTotal;
    }

	/*
	 * ########### PARCELABLE ###########
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(mCartItems);
		dest.writeString(mCartValue);
		dest.writeString(mCartCleanValue);
		dest.writeInt(mCartCount);
		dest.writeString(mVatValue);
		dest.writeDouble(mShippingValue);
		dest.writeBooleanArray(new boolean[] { hasSumCosts });
		dest.writeDouble(mExtraCosts);
		dest.writeString(mSumCostsValue);
		dest.writeString(mCouponDiscount);
		dest.writeString(mCouponCode);
		dest.writeMap(mPriceRules);
	    dest.writeDouble(mCartValueAsDouble);
	    dest.writeDouble(mCartValueConverted);
        dest.writeString(mSubTotal);
        dest.writeDouble(mSubTotalDouble);
        dest.writeDouble(mSubTotalConvertedDouble);
	}

	/**
	 * Parcel constructor
	 *
	 * @param in
	 */
	public ShoppingCart(Parcel in) {
		in.readMap(mCartItems, ShoppingCartItem.class.getClassLoader());
		mCartValue = in.readString();
		mCartCleanValue = in.readString();
		mCartCount = in.readInt();
		mVatValue = in.readString();
		setShippingValue(in.readDouble());
		in.readBooleanArray(new boolean[] { hasSumCosts });
		mExtraCosts = in.readDouble();
		setSumCostsValue(in.readString());
		mCouponDiscount = in.readString();
		mCouponCode = in.readString();
		mPriceRules = new HashMap<>();
		in.readMap(mPriceRules, String.class.getClassLoader());
		mCartValueAsDouble = in.readDouble();
		mCartValueConverted = in.readDouble();
        mSubTotal = in.readString();
        mSubTotalDouble = in.readDouble();
        mSubTotalConvertedDouble = in.readDouble();
	}

	/**
	 * Create parcelable
	 */
	public static final Creator<ShoppingCart> CREATOR = new Creator<ShoppingCart>() {
		public ShoppingCart createFromParcel(Parcel in) {
			return new ShoppingCart(in);
		}

		public ShoppingCart[] newArray(int size) {
			return new ShoppingCart[size];
		}
	};

    public void setSumCostsValue(String mSumCostsValue) {
        this.mSumCostsValue = mSumCostsValue;
    }

    public void setShippingValue(double mShippingValue) {
        this.mShippingValue = mShippingValue;
    }
}
