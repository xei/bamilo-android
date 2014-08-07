package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetFacebookLoginHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class FacebookLoginTest extends FrameworkServiceTests {
	private static String TAG = FacebookLoginTest.class.getSimpleName();
	protected boolean processed = false;

	public void testGetFacebookLoginIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI);
	}
	
	public void testGetFacebookLoginKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE);
	}
	
	public void testGetFacebookLoginMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA);
	}
	
	public void testGetFacebookLoginNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG);
	}
	
	public void testGetFacebookLoginUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG);
	}
	
	public void testGetFacebookLoginEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG);
	}
	
	public void testGetFacebookLoginGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH);
	}

	@SmallTest
	public void test(String url) {
		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		ContentValues values = new ContentValues();
		values.put("email", RequestConstants.FACEBOOK_CUSTOMER_EMAIL);
		values.put("first_name", RequestConstants.FACEBOOK_CUSTOMER_FIRST_NAME);
		values.put("last_name", RequestConstants.FACEBOOK_CUSTOMER_LAST_NAME);
		values.put("birthday", RequestConstants.FACEBOOK_CUSTOMER_BIRTHDAY);
		values.put("gender", RequestConstants.FACEBOOK_CUSTOMER_GENDER);
		args.putParcelable(GetFacebookLoginHelper.LOGIN_CONTENT_VALUES, values);

		args.putString(BaseHelper.KEY_COUNTRY, url + "/customer/facebooklogin?setDevice=mobileApi&facebook=true");
		sendRequest(args, new GetFacebookLoginHelper(), new IResponseCallback() {

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
	}
}
