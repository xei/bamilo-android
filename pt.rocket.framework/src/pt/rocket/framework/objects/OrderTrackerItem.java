/**
 * @author Manuel Silva
 * 
 * @version 1.01
 * 
 * 2013/10/22
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;

/**
 * Class that represents an Order Tracked Item
 * 
 * @author manuelsilva
 * 
 */
public class OrderTrackerItem implements IJSONSerializable {
	private final static String TAG = LogTagHelper.create( OrderTrackerItem.class );

    private String sku;
    private String name;
    private String quantity;
    private String status;
    private String status_update;
    
    /**
     * OrderTrackerItem empty constructor.
     */
    public OrderTrackerItem() {
    	sku = "";
    	name = "";
    	quantity = "";
    	status = "";
    	status_update = "";
    }

    /**
     * OrderTrackerItem constructor
     * 
     * @param s sku
     *            	of the OrderTrackerItem
     * @param n name
     *            	of the OrderTrackerItem
     * @param q quantity
     * 			  	of the OrderTrackerItem
     * @param st status
     * 				of the OrderTrackerItem
     * @param std status update date
     * 				of the OrderTrackerItem
     */
    public OrderTrackerItem(String s, String n, String q, String st, String std) {
    	this.sku = s;
    	this.name = n;
    	this.quantity = q;
    	this.status = st;
    	this.status_update = std;
    }

    
    public String getSku(){
    	return this.sku;
    }

    public String getName(){
    	return this.name;
    }
    
    public String getQuantity(){
    	return this.quantity;
    }
    
    public String getStatus(){
    	return this.status;
    }
    
    public String getUpdateDate(){
    	return this.status_update;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        sku = jsonObject.optString(RestConstants.JSON_SKU_TAG);
        name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
		quantity = jsonObject.optString(RestConstants.JSON_QUANTITY_TAG);
		Log.i(TAG, "code1 name : "+name);
		try {
			status = jsonObject.getJSONArray(RestConstants.JSON_ORDER_STATUS_TAG).getJSONObject(0).optString(RestConstants.JSON_ORDER_ITEM_STATUS_TAG);
			status_update = jsonObject.getJSONArray(RestConstants.JSON_ORDER_STATUS_TAG).getJSONObject(0).optString(RestConstants.JSON_ORDER_ITEM_STATUS_UPDATE_TAG);
			Log.i(TAG, "code1 status : "+status);
		} catch (JSONException e) {
			e.printStackTrace();
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
        try {

            jsonObject.put(RestConstants.JSON_SKU_TAG, sku);
            jsonObject.put(RestConstants.JSON_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_QUANTITY_TAG, quantity);
            jsonObject.put(RestConstants.JSON_ORDER_STATUS_TAG, status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
