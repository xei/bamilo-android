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
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents an Order Tracked
 * 
 * @author manuelsilva
 * 
 */
public class OrderTracker implements IJSONSerializable, Parcelable {
	
	public final static String TAG = LogTagHelper.create( OrderTracker.class );

    private String order_id;
    private String creation_date;
    private String payment_method;
    private String last_order_update;
    private ArrayList<OrderTrackerItem> orderTracketItems;


    /**
     * OrderTracker empty constructor.
     */
    public OrderTracker() {
    	order_id = "";
    	creation_date = "";
    	payment_method = "";
    	last_order_update = "";
    	orderTracketItems = new ArrayList<OrderTrackerItem>();
    }

    /**
     * OrderTracker constructor
     * 
     * @param o order_id
     *            	of the order
     * @param d creation_date
     *            	of the order
     * @param oI order tracker items
     * 			  	of the order
     * @param p payment method
     * 				of the order
     * @param l last order status update
     * 				of the order
     */
    public OrderTracker(String o, String d, String p, String l, ArrayList<OrderTrackerItem> oI) {
    	this.order_id = o;
    	this.creation_date = d;
    	this.payment_method = p;
    	this.last_order_update = l;
    	this.orderTracketItems = oI;
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
    
    public String getLastUpdateDate(){
    	return this.last_order_update;
    }
    
    public ArrayList<OrderTrackerItem> getOrderTrackerItems(){
    	return this.orderTracketItems;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        order_id = jsonObject.optString(RestConstants.JSON_ORDER_ID_TAG);
        creation_date = jsonObject.optString(RestConstants.JSON_ORDER_CREATION_DATE_TAG);
        payment_method = jsonObject.optString(RestConstants.JSON_ORDER_PAYMENT_METHOD_TAG);
        last_order_update = jsonObject.optString(RestConstants.JSON_ORDER_LAST_UPDATE_TAG);
		JSONObject items = jsonObject.optJSONObject(RestConstants.JSON_ORDER_ITEM_COLLECTION_TAG);
		
		Iterator keys = items.keys();
		
		while(keys.hasNext()){	
			OrderTrackerItem mOrderTrackerItem = new OrderTrackerItem();
			try {
				mOrderTrackerItem.initialize(items.getJSONObject((String) keys.next()));
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
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {

        JSONObject jsonObject = new JSONObject();
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
        return jsonObject;
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
	 * @param in
	 */
	private OrderTracker(Parcel in) {
    	order_id = in.readString();
    	creation_date = in.readString();
    	payment_method = in.readString();
    	last_order_update = in.readString();
    	in.readList(orderTracketItems, OrderTrackerItem.class.getClassLoader());
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<OrderTracker> CREATOR = new Parcelable.Creator<OrderTracker>() {
        public OrderTracker createFromParcel(Parcel in) {
            return new OrderTracker(in);
        }

        public OrderTracker[] newArray(int size) {
            return new OrderTracker[size];
        }
    };
    
}
