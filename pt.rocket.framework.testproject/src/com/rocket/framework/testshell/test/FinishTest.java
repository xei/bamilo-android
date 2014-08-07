package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.CreateAddressHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
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
	protected boolean processed_add= false;
	protected boolean processedaddress = false;
	protected boolean processedgetbilling = false;
	protected boolean processedsetbilling = false;
	protected boolean processedgetshipping = false;
	protected boolean processedsetshipping = false;
	protected boolean processedgetpayment = false;
	protected boolean processedsetpayment = false;
	protected boolean processedfinish = false;

	public void testFinishIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI, RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC, RequestConstants.SHIPPING_METHOD_IC, RequestConstants.SET_PAYMENT_METHOD_IC,RequestConstants.PRODUCT_SKU_CI, RequestConstants.PRODUCT_SKU_SIMPLE_CI);
	}

	public void testFinishKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE, RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_KE, RequestConstants.SHIPPING_METHOD_KE, RequestConstants.SET_PAYMENT_METHOD_KE,RequestConstants.PRODUCT_SKU_KE, RequestConstants.PRODUCT_SKU_SIMPLE_KE);
	}
	
	public void testFinishMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA, RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA, RequestConstants.SHIPPING_METHOD_MA, RequestConstants.SET_PAYMENT_METHOD_MA,RequestConstants.PRODUCT_SKU_MA, RequestConstants.PRODUCT_SKU_SIMPLE_MA);
	}
	
	public void testFinishNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG, RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG, RequestConstants.SHIPPING_METHOD_NG, RequestConstants.SET_PAYMENT_METHOD_NG,RequestConstants.PRODUCT_SKU_NG, RequestConstants.PRODUCT_SKU_SIMPLE_NG);
	}

	public void testFinishEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG, RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG, RequestConstants.SHIPPING_METHOD_EG, RequestConstants.SET_PAYMENT_METHOD_EG,RequestConstants.PRODUCT_SKU_EG, RequestConstants.PRODUCT_SKU_SIMPLE_EG);
	}

	public void testFinishUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG, RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_UG, RequestConstants.SHIPPING_METHOD_UG, RequestConstants.SET_PAYMENT_METHOD_UG,RequestConstants.PRODUCT_SKU_UG, RequestConstants.PRODUCT_SKU_SIMPLE_UG);
	}
	
	public void testFinishGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH, RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_GH, RequestConstants.SHIPPING_METHOD_GH, RequestConstants.SET_PAYMENT_METHOD_GH,RequestConstants.PRODUCT_SKU_GH, RequestConstants.PRODUCT_SKU_SIMPLE_GH);
	}

	private void test(String url, String billing_address, String shippinng_method, String payment_method, String sku, String simple) {
		/**
	     * Login 
	     */
	    Log.i(TAG, "mService => " + mService);
	    Bundle argslogin = new Bundle();
	    ContentValues contentValueslogin = new ContentValues();
	    contentValueslogin.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
	    contentValueslogin.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
	    argslogin.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValueslogin);
	    argslogin.putString(BaseHelper.KEY_COUNTRY, url + "/customer/login/");
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
	    
	    /**
	     * Add to Cart
	     */
	    Log.i(TAG, "mService => " + mService);
	    Bundle args_add = new Bundle();
	    args_add.putString(BaseHelper.KEY_COUNTRY, url+"/order/add?setDevice=mobileApi");
	    
	    ContentValues contentValues_add = new ContentValues();
	    contentValues_add.put(RequestConstants.KEY_ADD_CART_SKU, sku);
	    contentValues_add.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, simple);
	    contentValues_add.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
	    args_add.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, contentValues_add);
	    sendRequest(args_add, new GetShoppingCartAddItemHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
	            processed_add = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
	            processed_add = true;

	        }
	    });
	    //necessary in order to make the test wait for the server response
	    while (!processed_add) {
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * Address
	     */
	    Log.i(TAG, "mService => " + mService);
	    Bundle args_address = new Bundle();
	    args_address.putString(BaseHelper.KEY_COUNTRY, url + "/customer/address/list/");
	    sendRequest(args_address, new GetAddressHelper(), new IResponseCallback() {

	        @Override
	        public void onRequestError(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
	            processedaddress = true;
	        }

	        @Override
	        public void onRequestComplete(Bundle bundle) {
	            // TODO Auto-generated method stub
	            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
	            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
	            processedaddress = true;

	        }
	    });
	    //necessary in order to make the test wait for the server response
	    while (!processedaddress) {
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    
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
	    argsGetBilling.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/billing/");
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
	    argsSetBilling.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/billing/");
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
	    argsgetshipping.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/shippingmethod/");
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
		argssetshipping.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/shippingmethod/");
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
	    argsgetpayment.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/paymentmethod/");
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
		
		contentValuessetpayment.put(RequestConstants.KEY_SET_PAYMENT_METHOD,payment_method);
		argssetpayment.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValuessetpayment);
		argssetpayment.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/paymentmethod/");
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
		argsfinish.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/finish/");
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