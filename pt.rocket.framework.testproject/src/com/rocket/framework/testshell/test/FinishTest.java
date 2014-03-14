package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.CreateAddressHelper;
import pt.rocket.framework.testproject.helper.GetFinishHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetMyOrderHelper;
import pt.rocket.framework.testproject.helper.GetPollHelper;
import pt.rocket.framework.testproject.helper.GetSignupHelper;
import pt.rocket.framework.testproject.helper.SetBillingMethodHelper;
import pt.rocket.framework.testproject.helper.SetPaymentMethodHelper;
import pt.rocket.framework.testproject.helper.SetShippingMethodHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class FinishTest extends FrameworkServiceTests {
	private static String TAG = FinishTest.class.getSimpleName();
	protected boolean processed = false;
	protected boolean processed1 = false;
	protected boolean processed2 = false;
	protected boolean processed3 = false;
	protected boolean processed4 = false;
	protected boolean processed5 = false;

	public void testFinishIC() throws Throwable {
		test("https://www.jumia.ci");
	}

//	public void testFinishKE() throws Throwable {
//		test("https://alice-staging.jumia.co.ke");
//	}
	
//	public void testFinishMA() throws Throwable {
//		test("https://www.jumia.ma");
//	}
	
	public void testFinishNG() throws Throwable {
		test("https://www.jumia.com.ng");
	}

	public void testFinishEG() throws Throwable {
		test("https://www.jumia.com.eg");
	}

//	public void testFinishUG() throws Throwable {
//		test("https://alice-staging.jumia.ug");
//	}

	private void test(String url) {
		/**
		 * Login
		 */
		Log.i(TAG, "mService => " + mService);
	    Bundle args = new Bundle();
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
	    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
	    args.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
	    args.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/customer/login/");
	    sendRequest(args, new GetLoginHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
	            processed = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
	            processed = true;

	        }
	    });
	    //necessary in order to make the test wait for the server response
	    while (!processed) {
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
		
//	    /**
//	     * Poll
//	     */
//		Log.i(TAG, "mService => " + mService);
//		Bundle args5 = new Bundle();
//		ContentValues contentValues5 = new ContentValues();
//		contentValues5.put(RequestConstants.KEY_POLL_ANSWER,RequestConstants.POLL_ANSWER);
//		args5.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues5);
//		args5.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/multistep/checkoutpoll/");
//		sendRequest(args5, new GetPollHelper(), new IResponseCallback() {
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
//				processed5 = true;
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
//				processed5 = true;
//
//			}
//		});
//		// necessary in order to make the test wait for the server response
//		while (!processed5) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	    
	    /**
	     * Set Billing
	     */
		Log.i(TAG, "mService => " + mService);
		Bundle args1 = new Bundle();
		ContentValues contentValues1 = new ContentValues();
		contentValues1.put(RequestConstants.KEY_BILLING_METHOD_DIFFERENT,RequestConstants.BILLING_METHOD_DIFFERENT);
		
		if(url.equals("https://www.jumia.ci")){
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC);
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC);
		}else if(url.equals("https://www.jumia.com.eg")){
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG);
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG);
		}else if(url.equals("https://www.jumia.ma")){
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA);
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA);
		}else if(url.equals("https://www.jumia.com.ng")){
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG);
			contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG);
		}
		

		args1.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues1);
		args1.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/billing/");
		sendRequest(args1, new SetBillingMethodHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processed1 = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processed1 = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processed1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
	     * Set Shipping
	     */
		Log.i(TAG, "mService => " + mService);
		Bundle args3 = new Bundle();
		ContentValues contentValues3 = new ContentValues();
		
		if(url.equals("https://www.jumia.ma")){
			contentValues3.put(RequestConstants.KEY_SET_SHIPPING_METHOD,RequestConstants.SET_SHIPPING_METHOD2);
			contentValues3.put(RequestConstants.KEY_SET_SHIPPING_STATION,RequestConstants.SET_SHIPPING_STATION);
			contentValues3.put(RequestConstants.KEY_SET_SHIPPING_REGION,RequestConstants.SET_SHIPPING_REGION);
		}else{
			contentValues3.put(RequestConstants.KEY_SET_SHIPPING_METHOD,RequestConstants.SET_SHIPPING_METHOD);
		}
		
		

		args3.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues3);
		args3.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/shippingmethod/");
		sendRequest(args3, new SetShippingMethodHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processed3 = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processed3 = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processed3) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
	     * Set Payment
	     */
		Log.i(TAG, "mService => " + mService);
		Bundle args4 = new Bundle();
		ContentValues contentValues4 = new ContentValues();
		
		if(url.equals("https://www.jumia.ma")){
			contentValues4.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_MA);
		}else if(url.equals("https://www.jumia.com.ng")){
			contentValues4.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_NG);
		}else if(url.equals("https://www.jumia.ci")){
			contentValues4.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_IC);
		}else{
			contentValues4.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_EG);
		}

		args4.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues4);
		args4.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/paymentmethod/");
		sendRequest(args4, new SetPaymentMethodHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processed4 = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processed4 = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processed4) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    
	    /**
	     * Finish
	     */
		Log.i(TAG, "mService => " + mService);
		Bundle args2 = new Bundle();
//		ContentValues contentValues1 = new ContentValues();
//		contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_BILLING_ADDRESS_ID);
//		contentValues1.put(RequestConstants.KEY_BILLING_METHOD_DIFFERENT,RequestConstants.BILLING_METHOD_DIFFERENT);
//		contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID);
//		args2.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues1);
		args2.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/finish/");
		sendRequest(args2, new GetFinishHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processed2 = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processed2 = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processed2) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
