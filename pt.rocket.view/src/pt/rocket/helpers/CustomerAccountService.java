/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.helpers;

import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.Set;

import oak.ObscuredSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.OrderTracker;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.EventType;
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
public class CustomerAccountService {

	/**
	 * 
	 */	


	private static final String TAG = LogTagHelper
			.create(CustomerAccountService.class);

	//private static final String JSON_USER_TAG = "user";

	
	
	
	
	
	
	
	
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
		
	}

	private static void forgetPassword(
			final ForgetPasswordEvent event) {
		
//		RestServiceHelper.requestPost(event.eventType.action,
//				event.value, new ResponseReceiver<Void>(
//						event) {
//
//					@Override
//					public Void parseResponse(JSONObject response)
//							throws JSONException {
//						return null;
//					}
//
//				}, event.metaData);
	}

	private void loginEvent(RequestEvent request) {
//		MetaRequestEvent<ContentValues> event;
//		if (!(request instanceof MetaRequestEvent)
//				|| ((MetaRequestEvent<ContentValues>) request).value == null
//				|| ((MetaRequestEvent<ContentValues>) request).value.size() == 0) {
//			event = new LogInEvent(getCredentials());
//		} else {
//			event = (MetaRequestEvent<ContentValues>) request;
//		}
//		if (event.value.size() > 0) {
//			actionReturningCustomerEvent(event);
//			Boolean saveCredentials = event.value
//					.getAsBoolean(INTERNAL_AUTOLOGIN_FLAG);
//			if (saveCredentials == null || !saveCredentials) {
//				clearCredentials();
//			}
//		} else {
//			EventManager.getSingleton().triggerResponseEvent(
//					new ResponseErrorEvent(request, ErrorCode.REQUEST_ERROR));
//		}
	}
	
	private void facebookLoginEvent(RequestEvent request) {
//		MetaRequestEvent<ContentValues> event;
//		if (!(request instanceof MetaRequestEvent)
//				|| ((MetaRequestEvent<ContentValues>) request).value == null
//				|| ((MetaRequestEvent<ContentValues>) request).value.size() == 0) {
//			event = new FacebookLogInEvent(getCredentials());
//		} else {
//			event = (MetaRequestEvent<ContentValues>) request;
//		}
//		if (event.value.size() > 0) {
//			actionReturningCustomerEvent(event);
//			Boolean saveCredentials = event.value
//					.getAsBoolean(INTERNAL_AUTOLOGIN_FLAG);
//			if (saveCredentials == null || !saveCredentials) {
//				clearCredentials();
//			}
//		} else {
//			EventManager.getSingleton().triggerResponseEvent(
//					new ResponseErrorEvent(request, ErrorCode.REQUEST_ERROR));
//		}
	}

	private void trackOrder(final TrackOrderEvent event){
//		Uri uri = Uri.parse(event.eventType.action).buildUpon().appendQueryParameter("ordernr", event.value).build();
//		
//		RestServiceHelper.requestGet(uri,
//				new ResponseReceiver<OrderTracker>(event) {
//
//					@Override
//					public OrderTracker parseResponse(JSONObject metadataObject)
//							throws JSONException {
//						OrderTracker mOrderTracker = new OrderTracker();
//						if(metadataObject != null ){
//							mOrderTracker.initialize(metadataObject);
//							return mOrderTracker;
//						}
//						
//						return null;
//					}
//				}, event.metaData);
//		
//		
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
//			storeLogin((StoreEvent) event);
			break;
		case TRACK_ORDER_EVENT:
//			trackOrder((TrackOrderEvent) event);
			break;
		}
	}





//	private class CustomerResponseReceiver extends ResponseReceiver<Customer> {
//
//		private final ContentValues contentValues;
//
//		/**
//		 * @param requestEvent
//		 */
//		public CustomerResponseReceiver(RequestEvent requestEvent, ContentValues cv) {
//			super(requestEvent);
//			contentValues = cv;
//		}
//
//		@Override
//		public Customer parseResponse(JSONObject userData) throws JSONException {
////			boolean saveCredentials = contentValues == null ? false
////					: !contentValues.containsKey(INTERNAL_AUTOLOGIN_FLAG) ? false
////							: contentValues
////									.getAsBoolean(INTERNAL_AUTOLOGIN_FLAG);
////			if (saveCredentials) {
////				storeCredentials(contentValues);
////			}
////			if (userData.has(RestConstants.JSON_USER_TAG)) {
////				userData = userData.getJSONObject(RestConstants.JSON_USER_TAG);
////			} else if (userData.has(RestConstants.JSON_DATA_TAG)) {
////				userData = userData.getJSONObject(RestConstants.JSON_DATA_TAG);
////			}
////
////			return new Customer(userData);
//		}
//
//		@Override
//		protected void onRequestError(JSONObject jsonObject, Bundle metaData) {
//			super.onRequestError(jsonObject, metaData);
//		}
//	};




	




}
