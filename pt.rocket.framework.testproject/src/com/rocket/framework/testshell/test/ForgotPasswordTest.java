package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetForgotPasswordHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ForgotPasswordTest extends FrameworkServiceTests {
    private static String TAG = ForgotPasswordTest.class.getSimpleName();
    protected boolean processed = false;

    public void testGetForgotPasswordUG() throws Throwable {
    	test(BaseHelper.BASE_URL_UG, RequestConstants.KEY_FORGOT_PASSWORD_EMAIL_NEW);
    }

    public void testGetForgotPasswordIC() throws Throwable {
    	test(BaseHelper.BASE_URL_CI, RequestConstants.KEY_FORGOT_PASSWORD_EMAIL_NEW);
    }

    public void testGetForgotPasswordKE() throws Throwable {
    	test(BaseHelper.BASE_URL_KE, RequestConstants.KEY_FORGOT_PASSWORD_EMAIL_NEW);
    }

    public void testGetForgotPasswordNG() throws Throwable {
    	test(BaseHelper.BASE_URL_NG, RequestConstants.KEY_FORGOT_PASSWORD_EMAIL_NEW);
    }

    public void testGetForgotPasswordEG() throws Throwable {
    	test(BaseHelper.BASE_URL_EG, RequestConstants.KEY_FORGOT_PASSWORD_EMAIL_NEW);
    }

    public void testGetForgotPasswordMA() throws Throwable {
    	test(BaseHelper.BASE_URL_MA, RequestConstants.KEY_FORGOT_PASSWORD_EMAIL_NEW);
    }
    
    public void testGetForgotPasswordGH() throws Throwable {
    	test(BaseHelper.BASE_URL_GH, RequestConstants.KEY_FORGOT_PASSWORD_EMAIL);
    }
    	
	public void test(String url, String forgot_password_key)  {
        Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        ContentValues contentValues = new ContentValues();
        contentValues.put(forgot_password_key, RequestConstants.CUSTOMER_EMAIL);
        args.putParcelable(GetForgotPasswordHelper.CONTENT_VALUES, contentValues);
        args.putString(BaseHelper.KEY_COUNTRY, url+"/customer/forgotpassword?setDevice=mobileApi");
        args.putBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY, false);
        
        sendRequest(args, new GetForgotPasswordHelper(), new IResponseCallback() {

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
