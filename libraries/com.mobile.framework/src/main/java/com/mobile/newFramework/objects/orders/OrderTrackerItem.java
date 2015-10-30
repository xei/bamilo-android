/**
 * @author Manuel Silva
 * 
 * @version 1.01
 * 
 * 2013/10/22
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.newFramework.objects.orders;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents an Order Tracked Item
 * 
 * @author manuelsilva
 * 
 */
public class OrderTrackerItem extends ProductRegular{

	public final static String TAG = OrderTrackerItem.class.getSimpleName();

    private String delivery;
    private String quantity;
    private String status;
    private String status_update;

    /**
     * OrderTrackerItem empty constructor.
     */
    public OrderTrackerItem() {
    	quantity = "";
    	status = "";
    	status_update = "";
    }

//    /**
//     * OrderTrackerItem constructor
//     *
//     * @param s sku
//     *            	of the OrderTrackerItem
//     * @param n name
//     *            	of the OrderTrackerItem
//     * @param q quantity
//     * 			  	of the OrderTrackerItem
//     * @param st status
//     * 				of the OrderTrackerItem
//     * @param std status update date
//     * 				of the OrderTrackerItem
//     */
//    public OrderTrackerItem(String s, String n, String q, String st, String std) {
//    	this.sku = s;
//    	this.name = n;
//    	this.quantity = q;
//    	this.status = st;
//    	this.status_update = std;
//    }

    public String getQuantity(){
    	return this.quantity;
    }

    public String getStatus(){
    	return this.status;
    }

    public String getStatusUpdate(){
        return this.status_update;
    }

//    public String getUpdateDate(){
//    	return this.status_update;
//    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        delivery = jsonObject.getString(RestConstants.DELIVERY);
		quantity = jsonObject.getString(RestConstants.JSON_QUANTITY_TAG);
//		Log.i(TAG, "code1 name : "+name);
		try {
            JSONObject statusObject = jsonObject.getJSONObject(RestConstants.JSON_ORDER_STATUS_TAG);
			status = statusObject.optString(RestConstants.LABEL);
			status_update = statusObject.optString(RestConstants.UPDATED_AT);
//			Log.i(TAG, "code1 status : "+status);
		} catch (JSONException e) {
			e.printStackTrace();
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

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(RestConstants.JSON_QUANTITY_TAG, quantity);
            jsonObject.put(RestConstants.JSON_ORDER_STATUS_TAG, status);
            jsonObject.put(RestConstants.UPDATED_AT, status_update);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    public String getDelivery() {
        return delivery;
    }

}
