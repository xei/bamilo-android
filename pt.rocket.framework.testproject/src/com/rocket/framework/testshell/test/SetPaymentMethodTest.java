package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.CreateAddressHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetPollHelper;
import pt.rocket.framework.testproject.helper.GetSignupHelper;
import pt.rocket.framework.testproject.helper.SetPaymentMethodHelper;
import pt.rocket.framework.testproject.helper.SetShippingMethodHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class SetPaymentMethodTest extends FrameworkServiceTests {
	private static String TAG = SetPaymentMethodTest.class.getSimpleName();
	protected boolean processed = false;
	protected boolean processed1 = false;
	protected boolean processed2 = false;

	public void testSetPaymentMethodIC() throws Throwable {
		test("https://www.jumia.ci");
	}

	public void testSetPaymentMethodKE() throws Throwable {
		test("https://www.jumia.co.ke");
	}
	
	public void testSetPaymentMethodMA() throws Throwable {
		test("https://www.jumia.ma");
	}
	
	public void testSetPaymentMethodNG() throws Throwable {
		test("https://www.jumia.com.ng");
	}

	public void testSetPaymentMethodEG() throws Throwable {
		test("https://www.jumia.com.eg");
	}

	public void testSetPaymentMethodUG() throws Throwable {
		test("https://www.jumia.ug");
	}

	private void test(String url) {
		/**
		 * Login
		 */
		Log.i(TAG, "mService => " + mService);
	    Bundle args1 = new Bundle();
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
	    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
	    args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
	    args1.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/customer/login/");
	    sendRequest(args1, new GetLoginHelper(), new IResponseCallback() {

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
		
	    /**
	     * Set Payment
	     */
		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		ContentValues contentValues1 = new ContentValues();
		
		if(url.equals("https://www.jumia.ma")){
			contentValues1.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_MA);
		}else if(url.equals("https://www.jumia.com.ng")){
			contentValues1.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_NG);
		}else if(url.equals("https://www.jumia.ci")){
			contentValues1.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_IC);
		}else{
			contentValues1.put(RequestConstants.KEY_SET_PAYMENT_METHOD,RequestConstants.SET_PAYMENT_METHOD_EG);
		}

		args.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues1);
		args.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/v1/multistep/paymentmethod/");
		sendRequest(args, new SetPaymentMethodHelper(), new IResponseCallback() {

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
	}
}