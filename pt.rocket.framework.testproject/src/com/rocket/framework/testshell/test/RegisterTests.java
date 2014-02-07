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

	@SmallTest
	public void testRegisterIC() throws Throwable {
		/**
		 * Login before changing password
		 */
		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_BIRTHDAY, RequestConstants.CUSTOMER_BIRTHDAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_DAY, RequestConstants.CUSTOMER_DAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_FIRST_NAME, RequestConstants.CUSTOMER_FIRST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_GENDER, RequestConstants.CUSTOMER_GENDER);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_LAST_NAME, RequestConstants.CUSTOMER_LAST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_MONTH, RequestConstants.CUSTOMER_MONTH);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_YEAR, RequestConstants.CUSTOMER_YEAR);
		args.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, contentValues);
		args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.ci/mobapi/customer/create/");
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

	@SmallTest
	public void testRegisterKE() throws Throwable {
		/**
		 * Login before changing password
		 */
		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_BIRTHDAY, RequestConstants.CUSTOMER_BIRTHDAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_DAY, RequestConstants.CUSTOMER_DAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_FIRST_NAME, RequestConstants.CUSTOMER_FIRST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_GENDER, RequestConstants.CUSTOMER_GENDER);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_LAST_NAME, RequestConstants.CUSTOMER_LAST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_MONTH, RequestConstants.CUSTOMER_MONTH);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_YEAR, RequestConstants.CUSTOMER_YEAR);
		args.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, contentValues);
		args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.co.ke/mobapi/customer/create/");
		args.putString(BaseHelper.KEY_COUNTRY_TAG, "KE");
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

	@SmallTest
	public void testRegisterMA() throws Throwable {
		/**
		 * Login before changing password
		 */
		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_BIRTHDAY, RequestConstants.CUSTOMER_BIRTHDAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_DAY, RequestConstants.CUSTOMER_DAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_FIRST_NAME, RequestConstants.CUSTOMER_FIRST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_GENDER, RequestConstants.CUSTOMER_GENDER);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_LAST_NAME, RequestConstants.CUSTOMER_LAST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_MONTH, RequestConstants.CUSTOMER_MONTH);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_YEAR, RequestConstants.CUSTOMER_YEAR);
		args.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, contentValues);
		args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.ma/mobapi/customer/create/");
		args.putString(BaseHelper.KEY_COUNTRY_TAG, "MA");
		sendRequest(args, new GetRegisterHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				Boolean jsonValidation = false;
            	
            	if(bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
            	if(bundle != null){
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            	} else {
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - Bundle is null! ", jsonValidation);
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

	@SmallTest
	public void testRegisterNG() throws Throwable {

		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_BIRTHDAY, RequestConstants.CUSTOMER_BIRTHDAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_DAY, RequestConstants.CUSTOMER_DAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_FIRST_NAME, RequestConstants.CUSTOMER_FIRST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_GENDER, RequestConstants.CUSTOMER_GENDER);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_LAST_NAME, RequestConstants.CUSTOMER_LAST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_MONTH, RequestConstants.CUSTOMER_MONTH);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_YEAR, RequestConstants.CUSTOMER_YEAR);
		args.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, contentValues);
		args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.com.ng/mobapi/customer/create/");
		args.putString(BaseHelper.KEY_COUNTRY_TAG, "NG");
		sendRequest(args, new GetRegisterHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				Boolean jsonValidation = false;
            	
            	if(bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
            	if(bundle != null){
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            	} else {
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - Bundle is null! ", jsonValidation);
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

	@SmallTest
	public void testRegisterEG() throws Throwable {
		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_BIRTHDAY, RequestConstants.CUSTOMER_BIRTHDAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_DAY, RequestConstants.CUSTOMER_DAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_FIRST_NAME, RequestConstants.CUSTOMER_FIRST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_GENDER, RequestConstants.CUSTOMER_GENDER);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_LAST_NAME, RequestConstants.CUSTOMER_LAST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_MONTH, RequestConstants.CUSTOMER_MONTH);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_YEAR, RequestConstants.CUSTOMER_YEAR);
		args.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, contentValues);
		args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.com.eg/mobapi/customer/create/");
		args.putString(BaseHelper.KEY_COUNTRY_TAG, "EG");
		sendRequest(args, new GetRegisterHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				Boolean jsonValidation = false;
            	
            	if(bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
            	if(bundle != null){
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            	} else {
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - Bundle is null! ", jsonValidation);
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
	
	@SmallTest
	public void testRegisterUG() throws Throwable {
		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_BIRTHDAY, RequestConstants.CUSTOMER_BIRTHDAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_DAY, RequestConstants.CUSTOMER_DAY);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_FIRST_NAME, RequestConstants.CUSTOMER_FIRST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_GENDER, RequestConstants.CUSTOMER_GENDER);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_LAST_NAME, RequestConstants.CUSTOMER_LAST_NAME);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_MONTH, RequestConstants.CUSTOMER_MONTH);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
		contentValues.put(RequestConstants.KEY_CREATE_CUSTOMER_YEAR, RequestConstants.CUSTOMER_YEAR);
		args.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, contentValues);
		args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.ug/mobapi/customer/create/");
		args.putString(BaseHelper.KEY_COUNTRY_TAG, "UG");
		sendRequest(args, new GetRegisterHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				Boolean jsonValidation = false;
            	
            	if(bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
            	if(bundle != null){
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            	} else {
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - Bundle is null! ", jsonValidation);
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
