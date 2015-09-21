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
    private ArrayList<OrderTrackerItem> orderTracketItems;


    /**
     * OrderTracker empty constructor.
     */
	@SuppressWarnings("unused")
    public OrderTracker() {
    	order_id = "";
    	creation_date = "";
    	payment_method = "";
    	last_order_update = "";
    	orderTracketItems = new ArrayList<>();
    }

//    /**
//     * OrderTracker constructor
//     *
//     * @param o order_id
//     *            	of the order
//     * @param d creation_date
//     *            	of the order
//     * @param oI order tracker items
//     * 			  	of the order
//     * @param p payment method
//     * 				of the order
//     * @param l last order status update
//     * 				of the order
//     */
//    public OrderTracker(String o, String d, String p, String l, ArrayList<OrderTrackerItem> oI) {
//    	this.order_id = o;
//    	this.creation_date = d;
//    	this.payment_method = p;
//    	this.last_order_update = l;
//    	this.orderTracketItems = oI;
//    }


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
    	return this.orderTracketItems;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

		order_id = jsonObject.optString(RestConstants.JSON_ORDER_ID_TAG);
        creation_date = jsonObject.optString(RestConstants.JSON_ORDER_CREATION_DATE_TAG);
        payment_method = jsonObject.optString(RestConstants.PAYMENT_METHOD);
        last_order_update = jsonObject.optString(RestConstants.JSON_ORDER_LAST_UPDATE_TAG);
		JSONArray items = jsonObject.optJSONArray(RestConstants.PRODUCTS);

		for(int i = 0 ; i<items.length();i++){
			OrderTrackerItem mOrderTrackerItem = new OrderTrackerItem();
			try {
				mOrderTrackerItem.initialize(items.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			orderTracketItems.add(mOrderTrackerItem);
		}
        return true;
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
	    dest.writeList(orderTracketItems);
	}

	/**
	 * Parcel constructor
	 */
	private OrderTracker(Parcel in) {
    	order_id = in.readString();
    	creation_date = in.readString();
    	payment_method = in.readString();
    	last_order_update = in.readString();
    	orderTracketItems = new ArrayList<>();
    	in.readList(orderTracketItems, OrderTrackerItem.class.getClassLoader());
    }

	/**
	 * Create parcelable
	 */
	public static final Creator<OrderTracker> CREATOR = new Creator<OrderTracker>() {
        public OrderTracker createFromParcel(Parcel in) {
            return new OrderTracker(in);
        }

        public OrderTracker[] newArray(int size) {
            return new OrderTracker[size];
        }
    };

}
