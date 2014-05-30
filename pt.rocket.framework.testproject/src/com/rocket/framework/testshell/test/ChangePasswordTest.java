package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetChangePasswordHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ChangePasswordTest extends FrameworkServiceTests {
    private static String TAG = ChangePasswordTest.class.getSimpleName();
    protected boolean processed = false;
    protected boolean processed1 = false;
    protected boolean processed2 = false;
    
    @SmallTest
    public void testGetChangePasswordIC() throws Throwable {
    	executeChangePassword(BaseHelper.BASE_URL_CI);
    }
    
    @SmallTest
    public void testGetChangePasswordKE() throws Throwable {
    	executeChangePassword(BaseHelper.BASE_URL_KE);
    }
    
    @SmallTest
    public void testGetChangePasswordMA() throws Throwable {
    	executeChangePassword(BaseHelper.BASE_URL_MA);
    }
    
    @SmallTest
    public void testGetChangePasswordEG() throws Throwable {
    	executeChangePassword(BaseHelper.BASE_URL_EG);
    }
    
    @SmallTest
    public void testGetChangePasswordNG() throws Throwable {
    	executeChangePassword(BaseHelper.BASE_URL_NG);
    }
    
    @SmallTest
    public void testGetChangePasswordUG() throws Throwable {
    	executeChangePassword(BaseHelper.BASE_URL_UG);
    }
    
    private void executeChangePassword(String url){
    	/**
    	 * Login before changing password
    	 */
    	Log.i(TAG, "mService => " + mService);
        Bundle args1 = new Bundle();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
        contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
        args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
        args1.putString(BaseHelper.KEY_COUNTRY, url+"/customer/login/");
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
         * Change Password
         */
        Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, url+"/customer/changepass/");
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(RequestConstants.KEY_CHANGE_PASSWORD_EMAIL, RequestConstants.CUSTOMER_EMAIL);
        contentValues2.put(RequestConstants.KEY_CHANGE_PASSWORD_PASSWORD, RequestConstants.CUSTOMER_PASSWORD_2);
        contentValues2.put(RequestConstants.KEY_CHANGE_PASSWORD_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD_2);
        args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
        args.putBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY, false);
        sendRequest(args, new GetChangePasswordHelper(), new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
                // TODO Auto-generated method stub
                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
                assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
                processed1 = true;
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                // TODO Auto-generated method stub
                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
                assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
                processed1 = true;

            }
        });
        //necessary in order to make the test wait for the server response
        while (!processed1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
   
      
        
        /**
         * Restore Password
         */
        Log.i(TAG, "mService => " + mService);
        Bundle args2 = new Bundle();
        args2.putString(BaseHelper.KEY_COUNTRY, url+"/customer/changepass/");
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(RequestConstants.KEY_CHANGE_PASSWORD_EMAIL, RequestConstants.CUSTOMER_EMAIL);
        contentValues1.put(RequestConstants.KEY_CHANGE_PASSWORD_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
        contentValues1.put(RequestConstants.KEY_CHANGE_PASSWORD_PASSWORD2, RequestConstants.CUSTOMER_PASSWORD);
        args2.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues1);
        args2.putBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY, false);
        sendRequest(args2, new GetChangePasswordHelper(), new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
                // TODO Auto-generated method stub
                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
                assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
                processed2 = true;
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                // TODO Auto-generated method stub
                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
                assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
                processed2 = true;

            }
        });
        //necessary in order to make the test wait for the server response
        while (!processed2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    

}
