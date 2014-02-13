/**
 * @author Manuel Silva
 * 
 * @version 1.1
 * 
 * 2013/10/22
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONObject;

import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents an Order Tracked
 * 
 * @author manuelsilva
 * 
 */
public class OrderSummary implements IJSONSerializable, Parcelable {

	public final static String TAG = LogTagHelper.create(OrderSummary.class);

	private String mSubTotal;

	private String mShippingFee;

	private String mTotal;

	private ArrayList<String> mProducts;

	private Address mShippingAddress;

	private Address mBillingAddress;

	private ShippingMethods mShippingMethod;

	private PaymentMethods mPaymentOptions;

	/**
	 * OrderTracker empty constructor.
	 */
	public OrderSummary() {
	}

	/**
	 * @return the mSubTotal
	 */
	public String getSubTotal() {
		return mSubTotal;
	}

	/**
	 * @return the mShippingFee
	 */
	public String getShippingFee() {
		return mShippingFee;
	}

	/**
	 * @return the mTotal
	 */
	public String getTotal() {
		return mTotal;
	}

	/**
	 * @return the mProducts
	 */
	public ArrayList<String> getProducts() {
		return mProducts;
	}

	/**
	 * @return the mShippingAddress
	 */
	public Address getShippingAddress() {
		return mShippingAddress;
	}

	/**
	 * @return the mBillingAddress
	 */
	public Address getBillingAddress() {
		return mBillingAddress;
	}

	/**
	 * @return the mShippingMethod
	 */
	public ShippingMethods getShippingMethod() {
		return mShippingMethod;
	}

	/**
	 * @return the mPaymentOptions
	 */
	public PaymentMethods getPaymentOptions() {
		return mPaymentOptions;
	}

	/**
	 * @param mSubTotal
	 *            the mSubTotal to set
	 */
	public void setSubTotal(String mSubTotal) {
		this.mSubTotal = mSubTotal;
	}

	/**
	 * @param mShippingFee
	 *            the mShippingFee to set
	 */
	public void setShippingFee(String mShippingFee) {
		this.mShippingFee = mShippingFee;
	}

	/**
	 * @param mTotal
	 *            the mTotal to set
	 */
	public void setTotal(String mTotal) {
		this.mTotal = mTotal;
	}

	/**
	 * @param mProducts
	 *            the mProducts to set
	 */
	public void setProducts(ArrayList<String> mProducts) {
		this.mProducts = mProducts;
	}

	/**
	 * @param mShippingAddress
	 *            the mShippingAddress to set
	 */
	public void setShippingAddress(Address mShippingAddress) {
		this.mShippingAddress = mShippingAddress;
	}

	/**
	 * @param mBillingAddress
	 *            the mBillingAddress to set
	 */
	public void setBillingAddress(Address mBillingAddress) {
		this.mBillingAddress = mBillingAddress;
	}

	/**
	 * @param mShippingMethod
	 *            the mShippingMethod to set
	 */
	public void setShippingMethod(ShippingMethods mShippingMethod) {
		this.mShippingMethod = mShippingMethod;
	}

	/**
	 * @param mPaymentOptions
	 *            the mPaymentOptions to set
	 */
	public void setPaymentOptions(PaymentMethods mPaymentOptions) {
		this.mPaymentOptions = mPaymentOptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {

		// order_id = jsonObject.optString(RestConstants.JSON_ORDER_ID_TAG);
		// creation_date =
		// jsonObject.optString(RestConstants.JSON_ORDER_CREATION_DATE_TAG);
		// payment_method =
		// jsonObject.optString(RestConstants.JSON_ORDER_PAYMENT_METHOD_TAG);
		// last_order_update =
		// jsonObject.optString(RestConstants.JSON_ORDER_LAST_UPDATE_TAG);
		// JSONObject items =
		// jsonObject.optJSONObject(RestConstants.JSON_ORDER_ITEM_COLLECTION_TAG);
		//
		// Iterator keys = items.keys();
		//
		// while(keys.hasNext()){
		// OrderTrackerItem mOrderTrackerItem = new OrderTrackerItem();
		// try {
		// mOrderTrackerItem.initialize(items.getJSONObject((String)
		// keys.next()));
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// orderTracketItems.add(mOrderTrackerItem);
		// }
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO
		return null;
	}

	/**
	 * ########### Parcelable ###########
	 * 
	 * @author sergiopereira
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
		// TODO
		// dest.writeString(order_id);
		// dest.writeString(creation_date);
		// dest.writeString(payment_method);
		// dest.writeString(last_order_update);
		// dest.writeList(orderTracketItems);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	private OrderSummary(Parcel in) {
		// TODO
		// order_id = in.readString();
		// creation_date = in.readString();
		// payment_method = in.readString();
		// last_order_update = in.readString();
		// in.readList(orderTracketItems,
		// OrderTrackerItem.class.getClassLoader());
	}

	/**
	 * Create parcelable
	 */
	public static final Parcelable.Creator<OrderSummary> CREATOR = new Parcelable.Creator<OrderSummary>() {
		public OrderSummary createFromParcel(Parcel in) {
			return new OrderSummary(in);
		}

		public OrderSummary[] newArray(int size) {
			return new OrderSummary[size];
		}
	};

}
