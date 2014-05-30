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
    protected boolean processed_login = false;
    protected boolean processed_test = false;
    
public void testSignupNewsletterIC() throws Throwable {
    test(BaseHelper.BASE_URL_CI);
}

public void testSignupNewsletterKE() throws Throwable {
	test(BaseHelper.BASE_URL_KE);
}

public void testSignupNewsletterMA() throws Throwable {
    test(BaseHelper.BASE_URL_MA);
}

public void testSignupNewsletterNG() throws Throwable {
    test(BaseHelper.BASE_URL_NG);
}

public void testSignupNewsletterEG() throws Throwable {
    test(BaseHelper.BASE_URL_EG);
}

public void testSignupNewsletterUG() throws Throwable {
	test(BaseHelper.BASE_URL_UG);
}

public void test(String url){
//	/**
//	 * Login
//	 */
//	Log.i(TAG, "mService => " + mService);
//    Bundle args_login = new Bundle();
//    ContentValues contentValues_login = new ContentValues();
//    contentValues_login.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
//    contentValues_login.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
//    args_login.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues_login);
//    args_login.putString(BaseHelper.KEY_COUNTRY, url+"/customer/login/");
//    sendRequest(args_login, new GetLoginHelper(), new IResponseCallback() {
//
//        @Override
//        public void onRequestError(Bundle bundle) {
//            // TODO Auto-generated method stub
//            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
//            processed_login = true;
//        }
//
//        @Override
//        public void onRequestComplete(Bundle bundle) {
//            // TODO Auto-generated method stub
//            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
//            processed_login = true;
//
//        }
//    });
//    //necessary in order to make the test wait for the server response
//    while (!processed_login) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
	
	/**
     * Signup Newsletter
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args_test = new Bundle();
    ContentValues contentValues_test = new ContentValues();
    contentValues_test.put(RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE, RequestConstants.SIGNUP_NEWSLETTER_CATEGORIE_VALUE);
    contentValues_test.put(RequestConstants.SIGNUP_NEWSLETTER_EMAIL, RequestConstants.CUSTOMER_NEW_EMAIL);
    args_test.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues_test);
    args_test.putString(BaseHelper.KEY_COUNTRY, url + "/newsletter/signup/");
    args_test.putBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY, false);
    sendRequest(args_test, new GetSignupNewsletterHelper(), new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            processed_test = true;
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
            processed_test = true;

        }
    });
    //necessary in order to make the test wait for the server response
    while (!processed_test) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

}