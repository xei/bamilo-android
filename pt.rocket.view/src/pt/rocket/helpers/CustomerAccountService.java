/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.service.services;

import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.Set;

import oak.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseErrorEvent;
import pt.rocket.framework.event.events.ChangePasswordEvent;
import pt.rocket.framework.event.events.FacebookLogInEvent;
import pt.rocket.framework.event.events.ForgetPasswordEvent;
import pt.rocket.framework.event.events.InitShopEvent;
import pt.rocket.framework.event.events.LogInEvent;
import pt.rocket.framework.event.events.StoreEvent;
import pt.rocket.framework.event.events.TrackOrderEvent;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.OrderTracker;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Service that manages the customer account. Responds to REGISTER_ACCOUNT_EVENT
 * 
 * @author GuilhermeSilva
 * 
 */
public class CustomerAccountService extends DarwinService {

	/**
	 * 
	 */	
	private static final String CRED_PREFS = "cred";

	private static final String TAG = LogTagHelper
			.create(CustomerAccountService.class);

	//private static final String JSON_USER_TAG = "user";

	private boolean loggedIn = false;
	
	private MyObscuredPrefs obscuredPreferences;
	
	private Integer shopId = null;
	
	public static final String INTERNAL_AUTOLOGIN_FLAG = "__autologin_requested__";
	
	/**
	 * 
	 */
	public CustomerAccountService() {
		super(EnumSet.noneOf(EventType.class), EnumSet.of(EventType.INIT_SHOP,
				EventType.LOGIN_EVENT, EventType.FACEBOOK_LOGIN_EVENT, EventType.LOGOUT_EVENT,
				EventType.REGISTER_ACCOUNT_EVENT,
				EventType.CHANGE_PASSWORD_EVENT, EventType.GET_CUSTOMER,
				EventType.FORGET_PASSWORD_EVENT, EventType.GET_TERMS_EVENT,
				EventType.STORE_LOGIN, EventType.TRACK_ORDER_EVENT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.service.DarwinService#onInit(android.content.Context)
	 */
	@Override
	public void onInit(Context context) {
		super.onInit(context);
		obscuredPreferences = new MyObscuredPrefs(context, context.getSharedPreferences(
				CRED_PREFS, Context.MODE_PRIVATE));
	}

	private static void forgetPassword(
			final ForgetPasswordEvent event) {
		
		RestServiceHelper.requestPost(event.eventType.action,
				event.value, new ResponseReceiver<Void>(
						event) {

					@Override
					public Void parseResponse(JSONObject response)
							throws JSONException {
						return null;
					}

				}, event.metaData);
	}

	private void loginEvent(RequestEvent request) {
		MetaRequestEvent<ContentValues> event;
		if (!(request instanceof MetaRequestEvent)
				|| ((MetaRequestEvent<ContentValues>) request).value == null
				|| ((MetaRequestEvent<ContentValues>) request).value.size() == 0) {
			event = new LogInEvent(getCredentials());
		} else {
			event = (MetaRequestEvent<ContentValues>) request;
		}
		if (event.value.size() > 0) {
			actionReturningCustomerEvent(event);
			Boolean saveCredentials = event.value
					.getAsBoolean(INTERNAL_AUTOLOGIN_FLAG);
			if (saveCredentials == null || !saveCredentials) {
				clearCredentials();
			}
		} else {
			EventManager.getSingleton().triggerResponseEvent(
					new ResponseErrorEvent(request, ErrorCode.REQUEST_ERROR));
		}
	}
	
	private void facebookLoginEvent(RequestEvent request) {
		MetaRequestEvent<ContentValues> event;
		if (!(request instanceof MetaRequestEvent)
				|| ((MetaRequestEvent<ContentValues>) request).value == null
				|| ((MetaRequestEvent<ContentValues>) request).value.size() == 0) {
			event = new FacebookLogInEvent(getCredentials());
		} else {
			event = (MetaRequestEvent<ContentValues>) request;
		}
		if (event.value.size() > 0) {
			actionReturningCustomerEvent(event);
			Boolean saveCredentials = event.value
					.getAsBoolean(INTERNAL_AUTOLOGIN_FLAG);
			if (saveCredentials == null || !saveCredentials) {
				clearCredentials();
			}
		} else {
			EventManager.getSingleton().triggerResponseEvent(
					new ResponseErrorEvent(request, ErrorCode.REQUEST_ERROR));
		}
	}

	private void trackOrder(final TrackOrderEvent event){
		Uri uri = Uri.parse(event.eventType.action).buildUpon().appendQueryParameter("ordernr", event.value).build();
		
		RestServiceHelper.requestGet(uri,
				new ResponseReceiver<OrderTracker>(event) {

					@Override
					public OrderTracker parseResponse(JSONObject metadataObject)
							throws JSONException {
						OrderTracker mOrderTracker = new OrderTracker();
						if(metadataObject != null ){
							mOrderTracker.initialize(metadataObject);
							return mOrderTracker;
						}
						
						return null;
					}
				}, event.metaData);
		
		
	}
	
	private void actionReturningCustomerEvent(
			MetaRequestEvent<ContentValues> event) {
		RestServiceHelper.requestPost(event.eventType.action, event.value,
				new CustomerResponseReceiver(event, event.value),
				event.metaData);
	}

	private void getCustomerEvent(RequestEvent event) {
		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
				new CustomerResponseReceiver(event, null),
				event.metaData);
	}

	/**
	 * Logs the user out and triggers the LogOutCompletedEvent.
	 */
	public void logOut(final RequestEvent event) {

		RestServiceHelper.requestPost(event.eventType.action,
				new ContentValues(), new ResponseReceiver<Void>(event) {

					@Override
					public Void parseResponse(JSONObject response)
							throws JSONException {
						clearCredentials();
						return null;
					}
				}, event.metaData);
	}

	/**
	 * Changes the customer password and triggers the change passowrd completed
	 * event.
	 * 
	 * @param values
	 * @param headerValues
	 */
	public void changePassword(final ChangePasswordEvent event) {
		final ContentValues savedValues = new ContentValues( event.value );
		RestServiceHelper.requestPost(event.eventType.action,
				event.value, new ResponseReceiver<Void>(
						event) {
					@Override
					public Void parseResponse(JSONObject response)
							throws JSONException {
						savedValues.remove( "Alice_Module_Customer_Model_PasswordForm[password2]" );
						storeCredentials(savedValues);
						return null;
					}
				}, event.metaData);
	}

	// Triggers and event that retrieves the string with the terms and
	// conditions
	public static void getTermsAndConditions(final RequestEvent event) {
		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
				new ResponseReceiver<String>(event) {

					@Override
					public String parseResponse(JSONObject metadataObject)
							throws JSONException {
						String text = "";
						JSONArray dataArray = metadataObject
								.getJSONArray(RestConstants.JSON_DATA_TAG);
						if (dataArray.length() > 0) {
							text = dataArray.getString(0);
						}
						return text;
					}
				}, event.metaData);
	}
	
	private void initSessionState(InitShopEvent event) {
		if(shopId != null) {
			clearCredentials();
		}
		shopId = event.value;
//		getSessionState(new RequestEvent(EventType.GET_SESSION_STATE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework
	 * .event.IEvent)
	 */
	@Override
	public void handleEvent(RequestEvent event) {
		switch (event.getType()) {
		case INIT_SHOP:
			initSessionState((InitShopEvent) event);
			break;
		case LOGOUT_EVENT:
			logOut(event);
			break;
		case CHANGE_PASSWORD_EVENT:
			changePassword((ChangePasswordEvent) event);
			break;
		case LOGIN_EVENT:
			loginEvent(event);
			break;
		case FACEBOOK_LOGIN_EVENT:
			facebookLoginEvent(event);
			break;
		case REGISTER_ACCOUNT_EVENT:
		case EDIT_ACCOUNT_EVENT:
			actionReturningCustomerEvent((MetaRequestEvent<ContentValues>) event);
			break;
		case GET_CUSTOMER:
			getCustomerEvent(event);
			break;
		case FORGET_PASSWORD_EVENT:
			forgetPassword((ForgetPasswordEvent) event);
			break;
		case GET_TERMS_EVENT:
			getTermsAndConditions(event);
			break;
		case STORE_LOGIN:
			storeLogin((StoreEvent) event);
			break;
		case TRACK_ORDER_EVENT:
			trackOrder((TrackOrderEvent) event);
			break;
		}
	}


	private void storeLogin(StoreEvent event) {
		ContentValues values = event.value;
		storeCredentials(values);
	}


	private class CustomerResponseReceiver extends ResponseReceiver<Customer> {

		private final ContentValues contentValues;

		/**
		 * @param requestEvent
		 */
		public CustomerResponseReceiver(RequestEvent requestEvent, ContentValues cv) {
			super(requestEvent);
			contentValues = cv;
		}

		@Override
		public Customer parseResponse(JSONObject userData) throws JSONException {
			boolean saveCredentials = contentValues == null ? false
					: !contentValues.containsKey(INTERNAL_AUTOLOGIN_FLAG) ? false
							: contentValues
									.getAsBoolean(INTERNAL_AUTOLOGIN_FLAG);
			if (saveCredentials) {
				storeCredentials(contentValues);
			}
			if (userData.has(RestConstants.JSON_USER_TAG)) {
				userData = userData.getJSONObject(RestConstants.JSON_USER_TAG);
			} else if (userData.has(RestConstants.JSON_DATA_TAG)) {
				userData = userData.getJSONObject(RestConstants.JSON_DATA_TAG);
			}

			return new Customer(userData);
		}

		@Override
		protected void onRequestError(JSONObject jsonObject, Bundle metaData) {
			super.onRequestError(jsonObject, metaData);
		}
	};


	private void clearCredentials() {
		obscuredPreferences.edit().clear().commit();
	}

	public boolean hasCredentials() {
		return obscuredPreferences.contains(INTERNAL_AUTOLOGIN_FLAG);
	}

	private void storeCredentials(ContentValues values) {
		Editor editor = obscuredPreferences.edit();
		for (Entry<String, ?> entry : values.valueSet()) {
			if(entry.getKey()!=null && entry.getValue() != null && entry.getValue().toString() != null){
				editor.putString(entry.getKey(), entry.getValue().toString());
			} else {
				Log.e(TAG, "MISSING PARAMETERS FROM API!");
			}

			
		}
		editor.commit();
	}
	
	public String getEmail() {
		for(Entry<String,?> entry : obscuredPreferences.getAll().entrySet()) {
			if(entry.getValue() instanceof CharSequence && entry.getKey().contains("email")) {
				return entry.getValue().toString();
			}
		}
		return null;
	}
	
	private ContentValues getCredentials() {
		ContentValues cv = new ContentValues();
		try {
			for(Entry<String,?> entry : obscuredPreferences.getAll().entrySet()) {
				if(entry.getValue() instanceof CharSequence) {
					cv.put(entry.getKey(), entry.getValue().toString());
				}
			}			
		} catch (RuntimeException e) {
			Log.e(TAG, "CUST.ACCOUNT : ERROR in ObscuredPrefs.");
			e.printStackTrace();
		}
		return cv;
	}

	private static class MyObscuredPrefs extends ObscuredSharedPreferences {

		public MyObscuredPrefs(Context context, SharedPreferences delegate) {
			super(context, delegate);
		}

		@Override
		public Set<String> getStringSet(String arg0, Set<String> arg1) {
			return null;
		}

		@Override
		protected char[] getSpecialCode() {
			return Long.toHexString(0x9ad8aa75257645bl).toCharArray();
		}

	}

}
