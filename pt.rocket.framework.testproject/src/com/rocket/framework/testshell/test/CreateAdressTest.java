package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.CreateAddressHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetPollHelper;
import pt.rocket.framework.testproject.helper.GetSignupHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class CreateAdressTest extends FrameworkServiceTests {
	private static String TAG = CreateAdressTest.class.getSimpleName();
	protected boolean processed = false;
	protected boolean processed1 = false;
	protected boolean processed2 = false;

	public void testCreateAddressIC() throws Throwable {
		test("https://www.jumia.ci");
	}

	public void testCreateAddressKE() throws Throwable {
		test("https://www.jumia.co.ke");
	}
	
	public void testCreateAddressMA() throws Throwable {
		test("https://www.jumia.ma");
	}
	
	public void testCreateAddressNG() throws Throwable {
		test("https://www.jumia.com.ng");
	}

	public void testCreateAddressEG() throws Throwable {
		test("https://www.jumia.com.eg");
	}

	public void testCreateAddressUG() throws Throwable {
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
	     * Create Address
	     */
		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		ContentValues contentValues1 = new ContentValues();
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_FIRST_NAME,RequestConstants.CREATE_ADDRESS_FIRST_NAME);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_LAST_NAME,RequestConstants.CREATE_ADDRESS_LAST_NAME);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_ADDRESS1,RequestConstants.CREATE_ADDRESS_ADDRESS1);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_ADDRESS2,RequestConstants.CREATE_ADDRESS_ADDRESS2);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_CITY,RequestConstants.CREATE_ADDRESS_CITY);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_PHONE,RequestConstants.CREATE_ADDRESS_PHONE);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_FK_REGION,RequestConstants.CREATE_ADDRESS_FK_REGION);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_FK_CITY,RequestConstants.CREATE_ADDRESS_FK_CITY);
		contentValues1.put(RequestConstants.KEY_CREATE_ADDRESS_COUNTRY,RequestConstants.CREATE_ADDRESS_COUNTRY);
		
		args.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues1);
		args.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/customer/address/create/");
		sendRequest(args, new CreateAddressHelper(), new IResponseCallback() {

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
