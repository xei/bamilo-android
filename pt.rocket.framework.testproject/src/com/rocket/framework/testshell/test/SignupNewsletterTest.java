package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetCampaignHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetManageNewsletterHelper;
import pt.rocket.framework.testproject.helper.GetSearchUndefinedHelper;
import pt.rocket.framework.testproject.helper.GetSignupNewsletterHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class SignupNewsletterTest extends FrameworkServiceTests {
	private static String TAG = SignupNewsletterTest.class.getSimpleName();
	protected boolean processed_signup_newsletter = false;

	public void testSignupNewsletterIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE);
	}

	public void testSignupNewsletterKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE);
	}

	public void testSignupNewsletterMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE);
	}

	public void testSignupNewsletterNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE);
	}

	public void testSignupNewsletterEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE_EG);
	}

	public void testSignupNewsletterUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE_UG);
	}

	public void testSignupNewsletterGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE);
	}

	public void test(String url, String categorie_value){
		/**
		 * Signup Newsletter
		 */
		Log.i(TAG, "mService => " + mService);
		Bundle args_signup_nesletter = new Bundle();
		ContentValues contentValues_signup_newsletter = new ContentValues();
		contentValues_signup_newsletter.put(RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE, categorie_value);
		contentValues_signup_newsletter.put(RequestConstants.SIGNUP_NEWSLETTER_EMAIL, RequestConstants.CUSTOMER_NEW_EMAIL);
		args_signup_nesletter.putParcelable(GetSignupNewsletterHelper.CONTENT_VALUES, contentValues_signup_newsletter);
		args_signup_nesletter.putString(BaseHelper.KEY_COUNTRY, url + "/newsletter/signup/");
		args_signup_nesletter.putBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY, false);

		sendRequest(args_signup_nesletter, new GetSignupNewsletterHelper(), new IResponseCallback() {

			@Override
			public void onRequestError(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
				processed_signup_newsletter = true;
			}

			@Override
			public void onRequestComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
				processed_signup_newsletter = true;

			}
		});
		//necessary in order to make the test wait for the server response
		while (!processed_signup_newsletter) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}