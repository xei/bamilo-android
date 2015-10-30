/**
 * @author Manuel Silva
 *
 * @version 1.1
 *
 * 2013/10/22
 *
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents an Order Tracked
 *
 * @author manuelsilva
 *
 */
public class OrderTracker implements IJSONSerializable, Parcelable {

	public final static String TAG = OrderTracker.class.getSimpleName();

    private String order_id;
    private String creation_date;
    private String payment_method;
    private String last_order_update;
    private ArrayList<OrderTrackerItem> orderTrackerItems;
	private int total_products;
	private String grand_total;
	private Address shippingAddress;
	private Address billingAddress;


    /**
     * OrderTracker empty constructor.
     */
	@SuppressWarnings("unused")
    public OrderTracker() {
    	order_id = "";
    	creation_date = "";
    	payment_method = "";
    	last_order_update = "";
    	orderTrackerItems = new ArrayList<>();
    }

	/**
	 * Create parcelable
	 */
	public static Creator<OrderTracker> getCREATOR() {
		return CREATOR;
	}


	public String getId(){
    	return this.order_id;
    }

    public String getDate(){
    	return this.creation_date;
    }

    public String getPaymentMethod(){
    	return this.payment_method;
    }

//    public String getLastUpdateDate(){
//    	return this.last_order_update;
//    }

    public ArrayList<OrderTrackerItem> getOrderTrackerItems(){
    	return this.orderTrackerItems;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

		try {

			order_id = jsonObject.optString(RestConstants.ORDER_NUMBER);
			creation_date = jsonObject.optString(RestConstants.JSON_ORDER_CREATION_DATE_TAG);
			JSONObject paymentObject = jsonObject.optJSONObject(RestConstants.PAYMENT);

			if (paymentObject != null)
				payment_method = paymentObject.optString(RestConstants.LABEL);

			total_products = jsonObject.optInt(RestConstants.JSON_TOTAL_PRODUCTS_TAG);
			grand_total = jsonObject.optString(RestConstants.JSON_GRAND_TOTAL);

			JSONObject billingAddressObject = jsonObject.optJSONObject(RestConstants.JSON_ORDER_BIL_ADDRESS_TAG);
			if (billingAddressObject != null) {
				billingAddress = new Address();
				billingAddress.initialize(billingAddressObject);
			}


			JSONObject shippingAddressObject = jsonObject.optJSONObject(RestConstants.JSON_ORDER_SHIP_ADDRESS_TAG);
			if (shippingAddressObject != null) {
				shippingAddress = new Address();
				shippingAddress.initialize(shippingAddressObject);
			}

			// last_order_update = jsonObject.optString(RestConstants.JSON_ORDER_LAST_UPDATE_TAG);
			JSONArray items = jsonObject.optJSONArray(RestConstants.PRODUCTS);

			for (int i = 0; i < items.length(); i++) {
				OrderTrackerItem mOrderTrackerItem = new OrderTrackerItem();
				try {
					mOrderTrackerItem.initialize(items.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				orderTrackerItems.add(mOrderTrackerItem);
			}



			return true;

		}catch(Exception ex){
			ex.printStackTrace();
		}

		return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {

        return new JSONObject();
//        try {
//
//            jsonObject.put(RestConstants.JSON_ORDER_ID_TAG, order_id);
//            jsonObject.put(RestConstants.JSON_ORDER_CREATION_DATE_TAG, creation_date);
//            jsonObject.put(RestConstants.JSON_COUPON_CODE_TAG, coupon_code);
//            jsonObject.put(RestConstants.JSON_TERMS_CONDITIONS_TAG, terms_conditions);
//            jsonObject.put(RestConstants.JSON_END_DATE_TAG, end_date);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
    }

	@Override
	public RequiredJson getRequiredJson() {
		return RequiredJson.METADATA;
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


	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}

	public String getLast_order_update() {
		return last_order_update;
	}

	public void setLast_order_update(String last_order_update) {
		this.last_order_update = last_order_update;
	}

	public void setOrderTrackerItems(ArrayList<OrderTrackerItem> orderTrackerItems) {
		this.orderTrackerItems = orderTrackerItems;
	}

	public int getTotal_products() {
		return total_products;
	}

	public void setTotal_products(int total_products) {
		this.total_products = total_products;
	}

	public String getGrand_total() {
		return grand_total;
	}

	public void setGrand_total(String grand_total) {
		this.grand_total = grand_total;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}





	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(order_id);
	    dest.writeString(creation_date);
	    dest.writeString(payment_method);
	    dest.writeString(last_order_update);
	    dest.writeList(orderTrackerItems);
		dest.writeInt(total_products);
		dest.writeString(grand_total);
		dest.writeParcelable(shippingAddress,flags);
		dest.writeParcelable(billingAddress,flags);
	}

	/**
	 * Parcel constructor
	 */
	private OrderTracker(Parcel in) {
    	order_id = in.readString();
    	creation_date = in.readString();
    	payment_method = in.readString();
    	last_order_update = in.readString();
    	orderTrackerItems = new ArrayList<>();
    	in.readList(orderTrackerItems, OrderTrackerItem.class.getClassLoader());
		total_products = in.readInt();
		grand_total = in.readString();
		shippingAddress = new Address(in);
		billingAddress = new Address(in);
    }

	private static final Creator<OrderTracker> CREATOR = new Creator<OrderTracker>() {
        public OrderTracker createFromParcel(Parcel in) {
            return new OrderTracker(in);
        }

        public OrderTracker[] newArray(int size) {
            return new OrderTracker[size];
        }
    };


}
