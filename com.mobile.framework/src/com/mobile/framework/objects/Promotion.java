/**
 * @author Manuel Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.framework.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;

/**
 * Class that represents an Category. Composed by id, name and
 * hasChildren
 * 
 * @author manuelsilva
 * 
 */
public class Promotion implements IJSONSerializable, Parcelable {
	
	public final static String TAG = LogTagHelper.create( Promotion.class );

    private String title;
    private String description;
    private String coupon_code;
    private String terms_conditions;
    private String end_date;
    
    private boolean is_valid;

    /**
     * Category empty constructor.
     */
    public Promotion() {
        title = "defaultName";
        description = "";
        coupon_code = "";
        terms_conditions = "";
        end_date = "";
        is_valid = true;
    }

    /**
     * Promotion constructor
     * 
     * @param t title
     *            	of the promotion
     * @param d description
     *            	of the promotion
     * @param c coupon code
     * 			  	of the promotion
     * @param tc terms and conditions
     * 				of the promotion
     * @param e end date
     * 				of the promotion
     */
    public Promotion(String t, String d, String c, String tc, String e) {
    	this.title = t;
    	this.description = d;
    	this.coupon_code = c;
    	this.terms_conditions = tc;
    	this.end_date = e;
    }

    
    public String getTitle(){
    	return this.title;
    }

    public String getDescription(){
    	return this.description;
    }
    
    public String getCouponCode(){
    	return this.coupon_code;
    }
    
    public String getTermsConditions(){
    	return this.terms_conditions;
    }
    
    public String getEndDate(){
    	return this.end_date;
    }

    public boolean getIsStillValid(){
    	return this.is_valid;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        title = jsonObject.optString(RestConstants.JSON_TITLE_TAG);
        title = title.replace("[bold]", "<b>");
        title = title.replace("[/bold]", "</b><code>");
		description = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG);
		description = description.replace("[bold]", "<b>");
		description = description.replace("[/bold]", "</b><code>");
		coupon_code = jsonObject.optString(RestConstants.JSON_COUPON_CODE_TAG);
		terms_conditions = jsonObject.optString(RestConstants.JSON_TERMS_CONDITIONS_TAG);
		terms_conditions = terms_conditions.replace("[bold]", "<b>");
		terms_conditions = terms_conditions.replace("[/bold]", "</b><code>");
		end_date = jsonObject.optString(RestConstants.JSON_END_DATE_TAG);
		is_valid = compareDates(end_date);
		
        return true;
    }
    
    /**
     * Verify if the promotion has expired, using the end date.
     * @param endDate
     * @return
     */
    private boolean compareDates(String endDate){
    	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		Date strDate = null;
		
		try {
			strDate = sdf.parse(end_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		
		/**
		 * (strDate.getTime()+(1000*60*60*24) )
		 * 1000*60*60*24 is one day in milliseconds and needs to be added in order to include the end day.
		 */
		if (strDate != null && (strDate.getTime()+(1000*60*60*24))< c.getTimeInMillis()) {
			return false;
		} else {
			return true;
		}
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

            jsonObject.put(RestConstants.JSON_TITLE_TAG, title);
            jsonObject.put(RestConstants.JSON_DESCRIPTION_TAG, description);
            jsonObject.put(RestConstants.JSON_COUPON_CODE_TAG, coupon_code);
            jsonObject.put(RestConstants.JSON_TERMS_CONDITIONS_TAG, terms_conditions);
            jsonObject.put(RestConstants.JSON_END_DATE_TAG, end_date);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
	    dest.writeString(title);
	    dest.writeString(description);
	    dest.writeString(coupon_code);
	    dest.writeString(terms_conditions);
	    dest.writeString(end_date);
	    dest.writeBooleanArray(new boolean[] {is_valid});
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private Promotion(Parcel in) {
        title = in.readString();
        description = in.readString();
        coupon_code = in.readString();
        terms_conditions = in.readString();
        end_date = in.readString();
        in.readBooleanArray(new boolean[] {is_valid});
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<Promotion> CREATOR = new Parcelable.Creator<Promotion>() {
        public Promotion createFromParcel(Parcel in) {
            return new Promotion(in);
        }

        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };
	
}
