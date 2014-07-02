package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetLogoutHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

public class LogoutTest extends FrameworkServiceTests {
	private static String TAG = LogoutTest.class.getSimpleName();
	protected boolean processed_login = false;
	protected boolean processed_logout = false;

	@SmallTest
	public void testGetLogoutIC() throws Throwable {
		executeLogout(BaseHelper.BASE_URL_CI);
	}

	@SmallTest
	public void testGetLogoutKE() throws Throwable {
		executeLogout(BaseHelper.BASE_URL_KE);
	}

	@SmallTest
	public void testGetLogoutMA() throws Throwable {
		executeLogout(BaseHelper.BASE_URL_MA);
	}

	@SmallTest
	public void testGetLogoutEG() throws Throwable {
		executeLogout(BaseHelper.BASE_URL_EG);
	}

	@SmallTest
	public void testGetLogoutNG() throws Throwable {
		executeLogout(BaseHelper.BASE_URL_NG);
	}

	@SmallTest
	public void testGetLogoutUG() throws Throwable {
		executeLogout(BaseHelper.BASE_URL_UG);
	}

	private void executeLogout(String url) {
		/**
		 * Login before changing password
		 */
		Log.i(TAG, "mService => " + mService);
		Bundle args_login = new Bundle();
		ContentValues contentValues_login = new ContentValues();
		contentValues_login.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues_login.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		args_login.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues_login);
		args_login.putString(BaseHelper.KEY_COUNTRY, url + "/customer/login/");
		sendRequest(args_login, new GetLoginHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processed_login = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processed_login = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processed_login) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Logout
		 */
		Log.i(TAG, "mService => " + mService);
		Bundle args_logout = new Bundle();
		ContentValues contentValues_logout = new ContentValues();
		contentValues_logout.put(RequestConstants.KEY_LOGIN_EMAIL,RequestConstants.CUSTOMER_EMAIL);
		contentValues_logout.put(RequestConstants.KEY_LOGIN_PASSWORD,RequestConstants.CUSTOMER_PASSWORD);
		args_logout.putParcelable(GetLogoutHelper.LOGOUT_CONTENT_VALUES, contentValues_logout);
		args_logout.putString(BaseHelper.KEY_COUNTRY, url + "/customer/logout/");
		args_logout.putBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY, false);
		sendRequest(args_logout, new GetLogoutHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processed_logout = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
						jsonValidation);
				processed_logout = true;

			}
		});
		// necessary in order to make the test wait for the server response
		while (!processed_logout) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
