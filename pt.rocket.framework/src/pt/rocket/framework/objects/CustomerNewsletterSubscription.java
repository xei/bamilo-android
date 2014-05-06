/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Class used to save the newsletter subscription
 * @author sergiopereira
 */
public class CustomerNewsletterSubscription implements IJSONSerializable {
	
	private static final String TAG = CustomerNewsletterSubscription.class.getSimpleName();
	
	int mId;
	
	String mName;
	
	/**
	 * Empty constructor
	 */
	public CustomerNewsletterSubscription() {
		// ...
	}

	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		Log.d(TAG, "INITIALIZE: " + jsonObject);
		
		// FIXME : standardize
		
		if(jsonObject.has("id_newsletter_category"))
			mId = jsonObject.optInt("id_newsletter_category");
		if(jsonObject.has("id"))
			mId = jsonObject.optInt("id_newsletter_category");
		if(jsonObject.has("name"))
			mName = jsonObject.optString("name");
		if(jsonObject.has("description"))
			mName = jsonObject.optString("description");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		return null;
	}

	/*
	 * ########## GETTERS ########## 
	 */
	/**
	 * @return the mId
	 */
	public int getId() {
		return mId;
	}

	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}

	/*
	 * ########## SETTERS ########## 
	 */
	/**
	 * @param mId the mId to set
	 */
	public void setId(int mId) {
		this.mId = mId;
	}

	/**
	 * @param mName the mName to set
	 */
	public void setName(String mName) {
		this.mName = mName;
	}
	
}
