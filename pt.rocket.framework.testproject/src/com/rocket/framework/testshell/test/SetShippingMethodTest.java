//package com.rocket.framework.testshell.test;
//
//import pt.rocket.framework.testproject.constants.RequestConstants;
//import pt.rocket.framework.testproject.helper.BaseHelper;
//import pt.rocket.framework.testproject.helper.CreateAddressHelper;
//import pt.rocket.framework.testproject.helper.GetLoginHelper;
//import pt.rocket.framework.testproject.helper.GetPollHelper;
//import pt.rocket.framework.testproject.helper.GetShippingMethodsHelper;
//import pt.rocket.framework.testproject.helper.GetSignupHelper;
//import pt.rocket.framework.testproject.helper.SetBillingMethodHelper;
//import pt.rocket.framework.testproject.helper.SetShippingMethodHelper;
//import pt.rocket.framework.testproject.interfaces.IResponseCallback;
//import pt.rocket.framework.testproject.utils.Log;
//import pt.rocket.framework.utils.Constants;
//import android.content.ContentValues;
//import android.os.Bundle;
//import android.test.suitebuilder.annotation.SmallTest;
//
//@SmallTest
//public class SetShippingMethodTest extends FrameworkServiceTests {
//	private static String TAG = SetShippingMethodTest.class.getSimpleName();
//	protected boolean processedlogin = false;
//	protected boolean processedsetbilling = false;
//	protected boolean processedgetshipping = false;
//	protected boolean processedsetshipping = false;
//	
//	public void testSetShippingMethodIC() throws Throwable {
//		test(BaseHelper.BASE_URL_CI, "DigitalDelivery");
//	}
//
//	public void testSetShippingMethodKE() throws Throwable {
//		test(BaseHelper.BASE_URL_KE, "teste");
//	}
//	
//	public void testSetShippingMethodMA() throws Throwable {
//		test(BaseHelper.BASE_URL_MA, "teste");
//	}
//	
//	public void testSetShippingMethodNG() throws Throwable {
//		test(BaseHelper.BASE_URL_NG, "teste");
//	}
//
//	public void testSetShippingMethodEG() throws Throwable {
//		test(BaseHelper.BASE_URL_EG, "DigitalDelivery");
//	}
//
//	public void testSetShippingMethodUG() throws Throwable {
//		test(BaseHelper.BASE_URL_UG, "teste");
//	}
//	
//	public void testSetShippingMethodGH() throws Throwable {
//		test(BaseHelper.BASE_URL_GH, "teste");
//	}
//
//	private void test(String url, String shippinng_method) {
//		/**
//	     * Login before adding to cart
//	     */
//	    Log.i(TAG, "mService => " + mService);
//	    Bundle argslogin = new Bundle();
//	    ContentValues contentValueslogin = new ContentValues();
//	    contentValueslogin.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
//	    contentValueslogin.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
//	    argslogin.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValueslogin);
//	    argslogin.putString(BaseHelper.KEY_COUNTRY, url + "/customer/login/");
//	    sendRequest(argslogin, new GetLoginHelper(), new IResponseCallback() {
//
//	        @Override
//	        public void onRequestError(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
//	            processedlogin = true;
//	        }
//
//	        @Override
//	        public void onRequestComplete(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
//	            processedlogin = true;
//
//	        }
//	    });
//	    //necessary in order to make the test wait for the server response
//	    while (!processedlogin) {
//	        try {
//	            Thread.sleep(1000);
//	        } catch (InterruptedException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	    }
//	    /**
//	     * SetBilling
//	     */
//	    
//	    Bundle argsSetBilling = new Bundle();
//	    ContentValues contentValuessetbilling = new ContentValues();
//	    contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_DIFFERENT,RequestConstants.BILLING_METHOD_DIFFERENT);
//	    
//	    if(url.equals(BaseHelper.BASE_URL_CI)){
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC);
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC);
//	    }else if(url.equals(BaseHelper.BASE_URL_EG)){
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG);
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG);
//	    }else if(url.equals(BaseHelper.BASE_URL_MA)){
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA);
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA);
//	    }else if(url.equals(BaseHelper.BASE_URL_NG)){
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG);
//	    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG);
//	    }
//	    
//
//	    argsSetBilling.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValuessetbilling);
//	    argsSetBilling.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/billing/");
//	    sendRequest(argsSetBilling, new SetBillingMethodHelper(), new IResponseCallback() {
//
//	        @Override
//	        public void onRequestError(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle
//	                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue(
//	                    "Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
//	                            + bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
//	                    jsonValidation);
//	            processedsetbilling = true;
//	        }
//
//	        @Override
//	        public void onRequestComplete(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle
//	                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue(
//	                    "Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
//	                    jsonValidation);
//	            processedsetbilling = true;
//
//	        }
//	    });
//	    // necessary in order to make the test wait for the server response
//	    while (!processedsetbilling) {
//	        try {
//	            Thread.sleep(1000);
//	        } catch (InterruptedException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	    }
//	    
//	    /**
//	     * Get Shipping Method
//	     */
//	    Log.i(TAG, "mService => " + mService);
//	    Bundle argsgetshipping = new Bundle();
//	    argsgetshipping.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/shippingmethod/");
//	    sendRequest(argsgetshipping, new GetShippingMethodsHelper(), new IResponseCallback() {
//
//	        @Override
//	        public void onRequestError(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
//	            processedgetshipping = true;
//	        }
//
//	        @Override
//	        public void onRequestComplete(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
//	            processedgetshipping = true;
//
//	        }
//	    });
//	    //necessary in order to make the test wait for the server response
//	    while (!processedgetshipping) {
//	        try {
//	            Thread.sleep(1000);
//	        } catch (InterruptedException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	    }
//	    /**
//	     * Set Shipping
//	     */
//		Log.i(TAG, "mService => " + mService);
//		Bundle argssetshipping = new Bundle();
//		ContentValues contentValuessetshipping = new ContentValues();
//		
////		if(url.equals("https://www.jumia.ma")){
////			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_METHOD,RequestConstants.SET_SHIPPING_METHOD2);
////			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_STATION,RequestConstants.SET_SHIPPING_STATION);
////			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_REGION,RequestConstants.SET_SHIPPING_REGION);
////		}else{
////			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_METHOD,RequestConstants.SET_SHIPPING_METHOD);
////		}
//		contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_METHOD,shippinng_method);
//		
//
//		argssetshipping.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValuessetshipping);
//		argssetshipping.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/shippingmethod/");
//		sendRequest(argssetshipping, new SetShippingMethodHelper(), new IResponseCallback() {
//
//			@Override
//			public void onRequestError(Bundle bundle) {
//				// TODO Auto-generated method stub
//				Boolean jsonValidation = bundle
//						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//				assertTrue(
//						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
//								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
//						jsonValidation);
//				processedsetshipping = true;
//			}
//
//			@Override
//			public void onRequestComplete(Bundle bundle) {
//				// TODO Auto-generated method stub
//				Boolean jsonValidation = bundle
//						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//				assertTrue(
//						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
//						jsonValidation);
//				processedsetshipping = true;
//
//			}
//		});
//		// necessary in order to make the test wait for the server response
//		while (!processedsetshipping) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//}