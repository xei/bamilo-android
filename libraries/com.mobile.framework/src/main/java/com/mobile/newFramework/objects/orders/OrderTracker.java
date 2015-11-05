///**
// * @author Manuel Silva
// *
// * @version 1.1
// *
// * 2013/10/22
// *
// * Copyright (c) Rocket Internet All Rights Reserved
// */
//package com.mobile.newFramework.objects.orders;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.objects.IJSONSerializable;
//import com.mobile.newFramework.objects.RequiredJson;
//import com.mobile.newFramework.pojo.RestConstants;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Class that represents an Order Tracked
// *
// * @author manuelsilva
// *
// */
//public class OrderTracker implements IJSONSerializable, Parcelable {
//
//	public final static String TAG = OrderTracker.class.getSimpleName();
//
//    private String order_id;
//    private String creation_date;
//    private String payment_method;
//    private String last_order_update;
//
//	private String order_number;
//	private String date;
//	private String grand_total;
//	private int total_products;
//
//
//
//
//
//	private ArrayList<OrderTrackerItem> orderTracketItems;
//
//
//    /**
//     * OrderTracker empty constructor.
//     */
//	@SuppressWarnings("unused")
//    public OrderTracker() {
//    	order_id = "";
//    	setCreation_date("");
//    	setPayment_method("");
//    	setLast_order_update("");
//    	orderTracketItems = new ArrayList<>();
//    }
//
////    /**
////     * OrderTracker constructor
////     *
////     * @param o order_id
////     *            	of the order
////     * @param d creation_date
////     *            	of the order
////     * @param oI order tracker items
////     * 			  	of the order
////     * @param p payment method
////     * 				of the order
////     * @param l last order status update
////     * 				of the order
////     */
////    public OrderTracker(String o, String d, String p, String l, ArrayList<OrderTrackerItem> oI) {
////    	this.order_id = o;
////    	this.creation_date = d;
////    	this.payment_method = p;
////    	this.last_order_update = l;
////    	this.orderTracketItems = oI;
////    }
//
//
//    public String getId(){
//    	return this.order_id;
//    }
//
//    public String getDate(){
//    	return this.getCreation_date();
//    }
//
//    public String getPaymentMethod(){
//    	return this.getPayment_method();
//    }
//
////    public String getLastUpdateDate(){
////    	return this.last_order_update;
////    }
//
//    public ArrayList<OrderTrackerItem> getOrderTrackerItems(){
//    	return this.orderTracketItems;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//
////		order_id = jsonObject.optString(RestConstants.JSON_ORDER_ID_TAG);
//        setCreation_date(jsonObject.optString(RestConstants.CREATION_DATE));
//        setPayment_method(jsonObject.optJSONObject(RestConstants.PAYMENT).optString("label"));
//        setLast_order_update(jsonObject.optString(RestConstants.JSON_ORDER_LAST_UPDATE_TAG));
//
//		setOrder_number(jsonObject.optString(RestConstants.JSON_ORDER_NUMBER));
//		setDate(jsonObject.optString(RestConstants.JSON_COMMENT_DATE_TAG));
//		setGrand_total(jsonObject.optString(RestConstants.JSON_GRAND_TOTAL));
//		setTotal_products(jsonObject.optInt(RestConstants.JSON_TOTAL_PRODUCTS_TAG));
//
//
//		JSONArray items = jsonObject.optJSONArray(RestConstants.PRODUCTS);
//		for(int i = 0 ; i<items.length();i++){
//			OrderTrackerItem mOrderTrackerItem = new OrderTrackerItem();
//			try {
//				mOrderTrackerItem.initialize(items.getJSONObject(i));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			orderTracketItems.add(mOrderTrackerItem);
//		}
//        return true;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//    @Override
//    public JSONObject toJSON() {
//
//        return new JSONObject();
////        try {
////
////            jsonObject.put(RestConstants.JSON_ORDER_ID_TAG, order_id);
////            jsonObject.put(RestConstants.JSON_ORDER_CREATION_DATE_TAG, creation_date);
////            jsonObject.put(RestConstants.JSON_COUPON_CODE_TAG, coupon_code);
////            jsonObject.put(RestConstants.JSON_TERMS_CONDITIONS_TAG, terms_conditions);
////            jsonObject.put(RestConstants.JSON_END_DATE_TAG, end_date);
////
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////        return jsonObject;
//    }
//
//	@Override
//	public RequiredJson getRequiredJson() {
//		return RequiredJson.METADATA;
//	}
//
//
//	/**
//     * ########### Parcelable ###########
//     * @author sergiopereira
//     */
//
//    /*
//     * (non-Javadoc)
//     * @see android.os.Parcelable#describeContents()
//     */
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//	    dest.writeString(order_id);
//	    dest.writeString(getCreation_date());
//	    dest.writeString(getPayment_method());
//	    dest.writeString(getLast_order_update());
//	    dest.writeList(orderTracketItems);
//	}
//
//	/**
//	 * Parcel constructor
//	 */
//	private OrderTracker(Parcel in) {
//    	order_id = in.readString();
//    	setCreation_date(in.readString());
//    	setPayment_method(in.readString());
//    	setLast_order_update(in.readString());
//    	orderTracketItems = new ArrayList<>();
//    	in.readList(orderTracketItems, OrderTrackerItem.class.getClassLoader());
//    }
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Creator<OrderTracker> CREATOR = new Creator<OrderTracker>() {
//        public OrderTracker createFromParcel(Parcel in) {
//            return new OrderTracker(in);
//        }
//
//        public OrderTracker[] newArray(int size) {
//            return new OrderTracker[size];
//        }
//    };
//
//	public String getCreation_date() {
//		return creation_date;
//	}
//
//	public void setCreation_date(String creation_date) {
//		this.creation_date = creation_date;
//	}
//
//	public String getPayment_method() {
//		return payment_method;
//	}
//
//	public void setPayment_method(String payment_method) {
//		this.payment_method = payment_method;
//	}
//
//	public String getLast_order_update() {
//		return last_order_update;
//	}
//
//	public void setLast_order_update(String last_order_update) {
//		this.last_order_update = last_order_update;
//	}
//
//	public String getOrder_number() {
//		return order_number;
//	}
//
//	public void setOrder_number(String order_number) {
//		this.order_number = order_number;
//	}
//
//	public void setDate(String date) {
//		this.date = date;
//	}
//
//	public String getGrand_total() {
//		return grand_total;
//	}
//
//	public void setGrand_total(String grand_total) {
//		this.grand_total = grand_total;
//	}
//
//	public int getTotal_products() {
//		return total_products;
//	}
//
//	public void setTotal_products(int total_products) {
//		this.total_products = total_products;
//	}
//}
