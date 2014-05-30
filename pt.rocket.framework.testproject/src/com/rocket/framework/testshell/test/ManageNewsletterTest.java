package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetCampaignHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetManageNewsletterHelper;
import pt.rocket.framework.testproject.helper.GetSearchUndefinedHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class ManageNewsletterTest extends FrameworkServiceTests {
    private static String TAG = ManageNewsletterTest.class.getSimpleName();
    protected boolean processed_login = false;
    protected boolean processed_test = false;
    
public void testManageNewsletterIC() throws Throwable {
    test(BaseHelper.BASE_URL_CI);
}

public void testManageNewsletterKE() throws Throwable {
	test(BaseHelper.BASE_URL_KE);
}

public void testManageNewsletterMA() throws Throwable {
    test(BaseHelper.BASE_URL_MA);
}

public void testManageNewsletterNG() throws Throwable {
    test(BaseHelper.BASE_URL_NG);
}

public void testManageNewsletterEG() throws Throwable {
    test(BaseHelper.BASE_URL_EG);
}

public void testManageNewsletterUG() throws Throwable {
	test(BaseHelper.BASE_URL_UG);
}

public void test(String url){
	/**
	 * Login
	 */
	Log.i(TAG, "mService => " + mService);
    Bundle args_login = new Bundle();
    ContentValues contentValues_login = new ContentValues();
    contentValues_login.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues_login.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args_login.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues_login);
    args_login.putString(BaseHelper.KEY_COUNTRY, url+"/customer/login/");
    sendRequest(args_login, new GetLoginHelper(), new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            processed_login = true;
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
            processed_login = true;

        }
    });
    //necessary in order to make the test wait for the server response
    while (!processed_login) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	/**
     * Manage Newsletter
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args_test = new Bundle();
    args_test.putString(BaseHelper.KEY_COUNTRY, url + "/customer/managenewsletters/");
    args_test.putBoolean(Constants.BUNDLE_GENERAL_RULES_FALSE_KEY, false);
    sendRequest(args_test, new GetManageNewsletterHelper(), new IResponseCallback() {

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