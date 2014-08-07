package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetLoginFormHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class LoginFormTests extends FrameworkServiceTests {
    private static String TAG=LoginFormTests.class.getSimpleName();
    private Boolean processed=false;
    
    public void testGetLoginFormIC() throws Throwable {
        test(BaseHelper.BASE_URL_CI);
    }
    
    public void testGetLoginFormKE() throws Throwable {
        test(BaseHelper.BASE_URL_KE);
    }
    
    public void testGetLoginFormMA() throws Throwable {
        test(BaseHelper.BASE_URL_MA);
    }
    
    public void testGetLoginFormNG() throws Throwable {
        test(BaseHelper.BASE_URL_NG);
    }
    
    public void testGetLoginFormUG() throws Throwable {
        test(BaseHelper.BASE_URL_UG);
    }
    
    public void testGetLoginFormEG() throws Throwable {
        test(BaseHelper.BASE_URL_EG);
    }
     
    public void testGetLoginFormGH() throws Throwable {
        test(BaseHelper.BASE_URL_GH);
    }
    
    @SmallTest
    public void test(String url) {

    	Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, url + "/forms/login/" );
        sendRequest(args, new GetLoginFormHelper(), new IResponseCallback() {
            
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
