/**
 *
 */
package com.mobile.newFramework.objects.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * @author nutzer2
 * @modified Paulo Carvalho
 */
public class ShoppingCart implements IJSONSerializable, Parcelable {

	private static final String TAG = ShoppingCart.class.getSimpleName();

	private Map<String, ShoppingCartItem> mCartItems = new HashMap<>();
	private String mTotal;
	private double mTotalDouble = 0d;
	private double mTotalConverted = 0d;
	private int mCartCount;
	private String mVatValue;
	private String mVatValueConverted;
	private double mShippingValue;
	private double mExtraCosts;
	private String mSumCostsValue;
	private boolean hasSumCosts;
	private String mCouponDiscount;
	private String mCouponDiscountConverted;
	private String mCouponCode;

	private HashMap<String, String> mPriceRules;

	private String mSubTotal;
	private double mSubTotalDouble = 0d;
	private double mSubTotalConvertedDouble = 0d;
	private boolean mVatLabelEnable;

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
		JSONObject cartEntity = jsonObject.optJSONObject(RestConstants.JSON_CART_ENTITY);
		if (cartEntity == null){
			return false;
		}
		// Get cart value as string and double
		mTotal = cartEntity.getString(RestConstants.JSON_CART_TOTAL);
		mTotalDouble = cartEntity.optDouble(RestConstants.JSON_CART_TOTAL, 0);
		// Get cart value converted
		mTotalConverted = cartEntity.optDouble(RestConstants.JSON_CART_TOTAL_CONVERTED, 0d);
		// Get cart sub total
		mSubTotal = cartEntity.optString(RestConstants.JSON_CART_SUB_TOTAL);
		mSubTotalDouble = cartEntity.optDouble(RestConstants.JSON_CART_SUB_TOTAL,0d);
		mSubTotalConvertedDouble = cartEntity.optDouble(RestConstants.JSON_CART_SUB_TOTAL_CONVERTED, 0d);
		// Get cart count
		mCartCount = cartEntity.getInt(RestConstants.JSON_CART_COUNT_TAG);
		// VAT
		JSONObject vatObject = cartEntity.optJSONObject(RestConstants.JSON_CART_VAT);
		if(vatObject != null){
			mVatValue = vatObject.optString(RestConstants.JSON_CART_VALUE_TAG);
			mVatValueConverted = vatObject.optString(RestConstants.JSON_CART_VALUE_CONVERTED_TAG);
			mVatLabelEnable = vatObject.optBoolean(RestConstants.JSON_CART_VAT_LABEL_ENABLE);
		}
		// Delivery
		JSONObject deliveryObject = cartEntity.optJSONObject(RestConstants.JSON_CART_DELIVERY);
		if(deliveryObject != null){
			setShippingValue(deliveryObject.optDouble(RestConstants.JSON_CART_SHIPPING_VALUE_TAG));
		}
		// Coupon
		JSONObject couponObject = cartEntity.optJSONObject(RestConstants.JSON_CART_COUPON);
		if(couponObject != null){
			mCouponCode = couponObject.optString(RestConstants.JSON_CART_COUPON_CODE_TAG);
			mCouponDiscount = couponObject.optString(RestConstants.JSON_CART_VALUE_TAG);
			mCouponDiscountConverted = couponObject.optString(RestConstants.JSON_CART_VALUE_CONVERTED_TAG);
		}


		boolean sCosts = cartEntity.optBoolean(RestConstants.JSON_CART_SUM_COSTS_TAG);
		if (!sCosts) {
			hasSumCosts = false;
		} else {
			hasSumCosts = true;
			mSumCostsValue = cartEntity.optString(RestConstants.JSON_CART_SUM_COSTS_VALUE_TAG);
		}

		mExtraCosts = cartEntity.optDouble(RestConstants.JSON_CART_EXTRA_COSTS_TAG, 0);
		if (mCartCount > 0 && cartEntity.has(RestConstants.JSON_CART_PRODUCTS_TAG)) {
			fillCartHashMap(cartEntity.getJSONArray(RestConstants.JSON_CART_PRODUCTS_TAG));
		}

		JSONArray priceRules = cartEntity.optJSONArray(RestConstants.JSON_CART_PRICE_RULES_TAG);
		if (priceRules != null && priceRules.length() > 0) {
			mPriceRules = new HashMap<>();
			for (int i = 0; i < priceRules.length(); i++) {
				JSONObject pRulesElement = priceRules.optJSONObject(i);
				if (pRulesElement != null) {
					Print.d("code1rules : " + pRulesElement.getString(RestConstants.JSON_LABEL_TAG) + " value : "
							+ pRulesElement.getString(RestConstants.JSON_VALUE_TAG));
					mPriceRules.put(pRulesElement.getString(RestConstants.JSON_LABEL_TAG),
							pRulesElement.getString(RestConstants.JSON_VALUE_TAG));
				}
			}
		}
		Print.d("CART INIT: " + mTotal + " " + mTotalDouble + " " + mTotalConverted + " " + mCouponCode);
		return true;
	}

	/**
	 *
	 * @param cartArray
	 */
	private void fillCartHashMap(JSONArray cartArray) {
		try {
			for (int i = 0; i < cartArray.length() ; i++) {
				JSONObject cartObject = cartArray.getJSONObject(i);
				Print.d("ON SAVE CART ITEM");
				ShoppingCartItem item = new ShoppingCartItem();
				item.initialize(cartObject);
				mCartItems.put(item.getConfigSKU(), item);

			}
		} catch (JSONException e) {
			Print.d("fillCartHashMap: error" +e.toString());
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
	 * @return the cartValue
	 */
	public String getCartValue() {
		return mTotal;
	}

	/**
	 * @return the cartCount
	 */
	public int getCartCount() {
		return mCartCount;
	}


	public String getVatValue() {
		return this.mVatValue;
	}

	public double getShippingValue() {
		return mShippingValue;
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
	 * @return the sum_costs
	 */
	public boolean hasSumCosts() {
		return hasSumCosts;
	}

	/**
	 * @return the couponDiscount
	 */
	public String getCouponDiscount() {
		return mCouponDiscount;
	}

	/**
	 *
	 */
	public String getCouponCode() {
		return mCouponCode;
	}

	/**
	 * @return the price_rules
	 */
	public HashMap<String, String> getPriceRules() {
		return mPriceRules;
	}

	/**
	 * @return the mCartValueEuroConverted
	 */
	public double getCartValueEuroConverted() {
		return mTotalConverted;
	}

	/**
	 * Return the total price used for tracking
	 * @author sergiopereira
	 */
	public double getPriceForTracking() {
		Print.d("PRICE VALUE FOR TRACKING: " + mTotalDouble + " " + mTotalConverted);
		return mTotalConverted;
	}

	public String getSubTotal() {
		return mSubTotal;
	}

	public String getCouponDiscountConverted() {
		return mCouponDiscountConverted;
	}

	public String getVatValueConverted() {
		return mVatValueConverted;
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
		dest.writeString(mTotal);
		dest.writeInt(mCartCount);
		dest.writeString(mVatValue);
		dest.writeDouble(mShippingValue);
		dest.writeBooleanArray(new boolean[]{hasSumCosts});
		dest.writeDouble(mExtraCosts);
		dest.writeString(mSumCostsValue);
		dest.writeString(mCouponDiscount);
		dest.writeString(mCouponCode);
		dest.writeMap(mPriceRules);
		dest.writeDouble(mTotalDouble);
		dest.writeDouble(mTotalConverted);
		dest.writeString(mSubTotal);
		dest.writeDouble(mSubTotalDouble);
		dest.writeDouble(mSubTotalConvertedDouble);
		dest.writeBooleanArray(new boolean[]{mVatLabelEnable});
		dest.writeString(mCouponDiscountConverted);
		dest.writeString(mVatValueConverted);
	}

	/**
	 * Parcel constructor
	 *
	 * @param in
	 */
	public ShoppingCart(Parcel in) {
		in.readMap(mCartItems, ShoppingCartItem.class.getClassLoader());
		mTotal = in.readString();
		mCartCount = in.readInt();
		mVatValue = in.readString();
		setShippingValue(in.readDouble());
		in.readBooleanArray(new boolean[]{hasSumCosts});
		mExtraCosts = in.readDouble();
		setSumCostsValue(in.readString());
		mCouponDiscount = in.readString();
		mCouponCode = in.readString();
		mPriceRules = new HashMap<>();
		in.readMap(mPriceRules, String.class.getClassLoader());
		mTotalDouble = in.readDouble();
		mTotalConverted = in.readDouble();
		mSubTotal = in.readString();
		mSubTotalDouble = in.readDouble();
		mSubTotalConvertedDouble = in.readDouble();
		in.readBooleanArray(new boolean[] { mVatLabelEnable });
		mCouponDiscountConverted = in.readString();
		mVatValueConverted = in.readString();
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

	public boolean isVatLabelEnable() {
		return mVatLabelEnable;
	}

}
