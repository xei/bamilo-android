//package com.mobile.framework.objects;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.mobile.framework.rest.RestConstants;
//
//import com.mobile.newFramework.utils.output.Log;
//
///**
// * Class used to save the newsletter subscription
// * @author sergiopereira
// */
//public class CustomerNewsletterSubscription implements IJSONSerializable {
//
//	private static final String TAG = CustomerNewsletterSubscription.class.getSimpleName();
//
//	int mId;
//
//	String mName;
//
//	/**
//	 * Empty constructor
//	 */
//	public CustomerNewsletterSubscription() {
//		// ...
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//	 */
//	@Override
//	public boolean initialize(JSONObject jsonObject) throws JSONException {
//		Log.d(TAG, "INITIALIZE: " + jsonObject);
//
//		// FIXME: NAFAMZ-6518
//		if(jsonObject.has(RestConstants.JSON_NEWSLETTER_CATEGORY_ID_TAG))
//			mId = jsonObject.optInt(RestConstants.JSON_NEWSLETTER_CATEGORY_ID_TAG);
//		if(jsonObject.has(RestConstants.JSON_ID_TAG))
//			mId = jsonObject.optInt(RestConstants.JSON_ID_TAG);
//		if(jsonObject.has(RestConstants.JSON_NAME_TAG))
//			mName = jsonObject.optString(RestConstants.JSON_NAME_TAG);
//		if(jsonObject.has(RestConstants.JSON_DESCRIPTION_TAG))
//			mName = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG);
//		return true;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//	 */
//	@Override
//	public JSONObject toJSON() {
//		return null;
//	}
//
//	/*
//	 * ########## GETTERS ##########
//	 */
//	/**
//	 * @return the mId
//	 */
//	public int getId() {
//		return mId;
//	}
//
//	/**
//	 * @return the mName
//	 */
//	public String getName() {
//		return mName;
//	}
//
//	/*
//	 * ########## SETTERS ##########
//	 */
//	/**
//	 * @param mId the mId to set
//	 */
//	public void setId(int mId) {
//		this.mId = mId;
//	}
//
//	/**
//	 * @param mName the mName to set
//	 */
//	public void setName(String mName) {
//		this.mName = mName;
//	}
//
//}
