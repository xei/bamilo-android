package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetSignupHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

public class SignupTest extends FrameworkServiceTests {
	private static String TAG = SignupTest.class.getSimpleName();
	protected boolean processed = false;
	protected boolean processed1 = false;
	protected boolean processed2 = false;

//	@SmallTest
//	public void testGetSignupIC() throws Throwable {
//		executeLogout("https://alice-staging.jumia.ci");
//	}

	@SmallTest
	public void testGetSignupKE() throws Throwable {
		executeLogout("https://alice-staging.jumia.co.ke");
	}
//
//	@SmallTest
//	public void testGetSignupMA() throws Throwable {
//		executeLogout("https://www.jumia.ma");
//	}
//
//	@SmallTest
//	public void testGetSignupEG() throws Throwable {
//		executeLogout("https://www.jumia.com.eg");
//	}
//
//	@SmallTest
//	public void testGetSignupNG() throws Throwable {
//		executeLogout("https://www.jumia.com.ng");
//	}
//
//	@SmallTest
//	public void testGetSignupUG() throws Throwable {
//		executeLogout("https://www.jumia.ug");
//	}

	private void executeLogout(String url) {

		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		ContentValues contentValues1 = new ContentValues();
		contentValues1.put(RequestConstants.KEY_CUSTOMER_SIGNUP_EMAIL,RequestConstants.CUSTOMER_SIGNUP_EMAIL);
		contentValues1.put(RequestConstants.KEY_CUSTOMER_SIGNUP_SCENARIO,RequestConstants.CUSTOMER_SIGNUP_SCENARIO);
		args.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues1);
		args.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/customer/create/");
		args.putBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY, false);
		sendRequest(args, new GetSignupHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue(
						"Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
								+ bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
						jsonValidation);
				processed = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle
						.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
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
