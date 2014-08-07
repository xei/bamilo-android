package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetApiInfoHelper;
import pt.rocket.framework.testproject.helper.GetChangePasswordHelper;
import pt.rocket.framework.testproject.helper.GetCustomerHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class CostumerTest extends FrameworkServiceTests {
    private static String TAG = CostumerTest.class.getSimpleName();
    protected boolean processed = false;

    public void testGetCostumerIC() throws Throwable {
    	test(BaseHelper.BASE_URL_CI);
    }
    
    public void testGetCostumerKE() throws Throwable {
    	test(BaseHelper.BASE_URL_KE);
    }
    
    public void testGetCostumerMA() throws Throwable {
    	test(BaseHelper.BASE_URL_MA);
    }
    
    public void testGetCostumerNG() throws Throwable {
    	test(BaseHelper.BASE_URL_NG);
    }
    
    public void testGetCostumerUG() throws Throwable {
    	test(BaseHelper.BASE_URL_UG);
    }
    
    public void testGetCostumerEG() throws Throwable {
    	test(BaseHelper.BASE_URL_EG);
    }
    
    public void testGetCostumerGH() throws Throwable {
    	test(BaseHelper.BASE_URL_GH);
    }
    
    @SmallTest
    public void test(String url) {
    	/**
    	 * Login
    	 */
    	Log.i(TAG, "mService => " + mService);
        Bundle args1 = new Bundle();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
        contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
        args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
        args1.putString(BaseHelper.KEY_COUNTRY, url + "/customer/login/");
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
         * Customer
         */
    	
    	Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, url + "/customer/getdetails?setDevice=mobileApi");
        sendRequest(args, new GetCustomerHelper(), new IResponseCallback() {

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
