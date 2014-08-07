package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetRegisterHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

public class RegisterTests extends FrameworkServiceTests {
	private static String TAG = RegisterTests.class.getSimpleName();
	protected boolean processed = false;

	public void testRegisterKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE);
	}

	public void testRegisterMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA);
	}

	public void testRegisterNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG);
	}

	public void testRegisterEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG);
	}

	public void testRegisterUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG);
	}
	
	public void testRegisterIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI);
	}
	
	public void testRegisterGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH);
	}
	
	@SmallTest
	public void test(String url){
		/**
		 * Login before changing password
		 */
		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_BIRTHDAY, RequestConstants.CUSTOMER_BIRTHDAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_DAY, RequestConstants.CUSTOMER_DAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_EMAIL, RequestConstants.CUSTOMER_NEW_EMAIL);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_FIRST_NAME, RequestConstants.CUSTOMER_FIRST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_GENDER, RequestConstants.CUSTOMER_GENDER);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_LAST_NAME, RequestConstants.CUSTOMER_LAST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_MONTH, RequestConstants.CUSTOMER_MONTH);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_YEAR, RequestConstants.CUSTOMER_YEAR);
		//contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_NEWSLETTER, RequestConstants.CUSTOMER_NEWSLETTER);
		args.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, contentValues);
		args.putString(BaseHelper.KEY_COUNTRY, url + "/customer/create/");
		args.putString(BaseHelper.KEY_COUNTRY_TAG, "IC");
		sendRequest(args, new GetRegisterHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				Boolean jsonValidation = false;

				if (bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)) {
					jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				}
				if (bundle != null) {
					assertTrue(
							"<<"
									+ args.getString(BaseHelper.KEY_COUNTRY_TAG)
									+ "<< Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
									+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
				} else {
					assertTrue("<<" + args.getString(BaseHelper.KEY_COUNTRY_TAG)
							+ "<< Failed onRequestError - Bundle is null! ", jsonValidation);
				}
				processed = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processed = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processed) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
