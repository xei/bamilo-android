package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.CreateAddressHelper;
import pt.rocket.framework.testproject.helper.GetBillingAddressHelper;
import pt.rocket.framework.testproject.helper.GetChangePasswordHelper;
import pt.rocket.framework.testproject.helper.GetFinishHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetMyOrderHelper;
import pt.rocket.framework.testproject.helper.GetPaymentMethodsHelper;
import pt.rocket.framework.testproject.helper.GetPollHelper;
import pt.rocket.framework.testproject.helper.GetShippingMethodsHelper;
import pt.rocket.framework.testproject.helper.GetShoppingCartAddItemHelper;
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
	protected boolean processedlogin = false;
	protected boolean processed_add_item= false;
	protected boolean processedgetbilling = false;
	protected boolean processedsetbilling = false;
	protected boolean processedgetshipping = false;
	protected boolean processedsetshipping = false;
	protected boolean processedgetpayment = false;
	protected boolean processedsetpayment = false;
	protected boolean processedfinish = false;

//	public void testFinishIC() throws Throwable {
//		test("https://www.jumia.ci", RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC, "DigitalDelivery");
//	}
//
//	public void testFinishKE() throws Throwable {
////		test("https://alice-staging.jumia.co.ke", "DigitalDelivery");
//	}
//	
//	public void testFinishMA() throws Throwable {
//		test("https://www.jumia.ma", RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA, "DigitalDelivery");
//	}
	
	public void testFinishNG() throws Throwable {
		test("https://www.jumia.com.ng", RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG, "DigitalDelivery");
	}

//	public void testFinishEG() throws Throwable {
//		test("https://www.jumia.com.eg", RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG, "DigitalDelivery");
//	}
//
//	public void testFinishUG() throws Throwable {
////		test("https://alice-staging.jumia.ug","DigitalDelivery");
//	}
	
//	public void testFinishStaging_NG() throws Throwable {
//		test("https://alice-staging.jumia.com.ng", RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG, "DigitalDelivery");
//	}

	private void test(String url, String billing_address, String shippinng_method) {
		/**
	     * Login 
	     */
	    Log.i(TAG, "mService => " + mService);
	    Bundle argslogin = new Bundle();
	    ContentValues contentValueslogin = new ContentValues();
	    contentValueslogin.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
	    contentValueslogin.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
	    argslogin.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValueslogin);
	    argslogin.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/customer/login/");
	    sendRequest(argslogin, new GetLoginHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
	            processedlogin = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
	            processedlogin = true;

	        }
	    });
	    //necessary in order to make the test wait for the server response
	    while (!processedlogin) {
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    
//	    /**
//	     * Add to Cart
//	     */
//	    Log.i(TAG, "mService => " + mService);
//	    Bundle args_add_item = new Bundle();
//	    args_add_item.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.com.ng/mobapi/order/add?setDevice=mobileApi");
//	    ContentValues contentValues_add_item = new ContentValues();
//	    contentValues_add_item.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_NG);
//	    contentValues_add_item.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, RequestConstants.PRODUCT_SKU_SIMPLE_NG);
//	    contentValues_add_item.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
//	    args_add_item.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues_add_item);
//	    sendRequest(args_add_item, new GetShoppingCartAddItemHelper(), new IResponseCallback() {
//
//	        @Override
//	        public void onRequestError(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
//	            processed_add_item = true;
//	        }
//
//	        @Override
//	        public void onRequestComplete(Bundle bundle) {
//	            // TODO Auto-generated method stub
//	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
//	            processed_add_item = true;
//
//	        }
//	    });
//	    //necessary in order to make the test wait for the server response
//	    while (!processed_add_item) {
//	        try {
//	            Thread.sleep(1000);
//	        } catch (InterruptedException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	    }
//	    
	    /**
	     * GetBilling
	     */
	    
	    Bundle argsGetBilling = new Bundle();
//	    ContentValues contentValuesgetbilling = new ContentValues();
//	    contentValuesgetbilling.put(RequestConstants.KEY_BILLING_METHOD_DIFFERENT,RequestConstants.BILLING_METHOD_DIFFERENT);
//
//    	contentValuesgetbilling.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,billing_address);
//    	contentValuesgetbilling.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,billing_address);
//    	
//	    argsGetBilling.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValuesgetbilling);
	    argsGetBilling.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/billing/");
	    sendRequest(argsGetBilling, new GetBillingAddressHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle
	                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue(
	                    "Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
	                            + bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
	                    jsonValidation);
	            processedgetbilling = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle
	                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue(
	                    "Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
	                    jsonValidation);
	            processedgetbilling = true;

	        }
	    });
	    // necessary in order to make the test wait for the server response
	    while (!processedgetbilling) {
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * SetBilling
	     */
	    
	    Bundle argsSetBilling = new Bundle();
	    ContentValues contentValuessetbilling = new ContentValues();
	    contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_DIFFERENT,RequestConstants.BILLING_METHOD_DIFFERENT);

    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,billing_address);
    	contentValuessetbilling.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,billing_address);
    	
	    argsSetBilling.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValuessetbilling);
	    argsSetBilling.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/billing/");
	    sendRequest(argsSetBilling, new SetBillingMethodHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle
	                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue(
	                    "Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
	                            + bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
	                    jsonValidation);
	            processedsetbilling = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle
	                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue(
	                    "Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
	                    jsonValidation);
	            processedsetbilling = true;

	        }
	    });
	    // necessary in order to make the test wait for the server response
	    while (!processedsetbilling) {
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * Get Shipping Method
	     */
	    Log.i(TAG, "mService => " + mService);
	    Bundle argsgetshipping = new Bundle();
	    argsgetshipping.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/shippingmethod/");
	    sendRequest(argsgetshipping, new GetShippingMethodsHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
	            processedgetshipping = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
	            processedgetshipping = true;

	        }
	    });
	    //necessary in order to make the test wait for the server response
	    while (!processedgetshipping) {
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
		Bundle argssetshipping = new Bundle();
		ContentValues contentValuessetshipping = new ContentValues();
		
//		if(url.equals("https://www.jumia.ma")){
//			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_METHOD,RequestConstants.SET_SHIPPING_METHOD2);
//			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_STATION,RequestConstants.SET_SHIPPING_STATION);
//			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_REGION,RequestConstants.SET_SHIPPING_REGION);
//		}else{
//			contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_METHOD,RequestConstants.SET_SHIPPING_METHOD);
//		}
		contentValuessetshipping.put(RequestConstants.KEY_SET_SHIPPING_METHOD,shippinng_method);
		

		argssetshipping.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValuessetshipping);
		argssetshipping.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/shippingmethod/");
		sendRequest(argssetshipping, new SetShippingMethodHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processedsetshipping = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processedsetshipping = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processedsetshipping) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
	     * Get Payment Method
	     */
	    Log.i(TAG, "mService => " + mService);
	    Bundle argsgetpayment = new Bundle();
	    argsgetpayment.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/paymentmethod/");
	    sendRequest(argsgetpayment, new GetPaymentMethodsHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
	            processedgetpayment = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
	            processedgetpayment = true;

	        }
	    });
	    //necessary in order to make the test wait for the server response
	    while (!processedgetpayment) {
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
		Bundle argssetpayment = new Bundle();
		ContentValues contentValuessetpayment = new ContentValues();
		
		if(url.equals("https://www.jumia.ma")){
			contentValuessetpayment.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_MA);
		}else if(url.equals("https://www.jumia.com.ng")){
			contentValuessetpayment.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_NG);
		}else if(url.equals("https://www.jumia.ci")){
			contentValuessetpayment.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_IC);
		}else{
			contentValuessetpayment.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_EG);
		}

		argssetpayment.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValuessetpayment);
		argssetpayment.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/paymentmethod/");
		sendRequest(argssetpayment, new SetPaymentMethodHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processedsetpayment = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processedsetpayment = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processedsetpayment) {
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
		Bundle argsfinish = new Bundle();
//		ContentValues contentValues1 = new ContentValues();
//		contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_BILLING_ADDRESS_ID);
//		contentValues1.put(RequestConstants.KEY_BILLING_METHOD_DIFFERENT,RequestConstants.BILLING_METHOD_DIFFERENT);
//		contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID);
//		args2.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues1);
		argsfinish.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/finish/");
		sendRequest(argsfinish, new GetFinishHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processedfinish = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processedfinish = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processedfinish) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}