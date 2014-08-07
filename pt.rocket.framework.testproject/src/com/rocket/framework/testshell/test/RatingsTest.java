package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetRatingsHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class RatingsTest extends FrameworkServiceTests {
    private static String TAG = RatingsTest.class.getSimpleName();
    protected boolean processed = false;

    public void testGetRatingsIC() throws Throwable {
        test(BaseHelper.BASE_URL_CI);
    }
    
    public void testGetRatingsMA() throws Throwable {
        test(BaseHelper.BASE_URL_MA);
    }
    
    public void testGetRatingsEG() throws Throwable {
        test(BaseHelper.BASE_URL_EG);
    }
    
    public void testGetRatingsNG() throws Throwable {
        test(BaseHelper.BASE_URL_NG);
    }
    
    public void testGetRatingsUG() throws Throwable {
        test(BaseHelper.BASE_URL_UG);
    }
    
    public void testGetRatingsKE() throws Throwable {
        test(BaseHelper.BASE_URL_KE);
    }
    
    public void testGetRatingsGH() throws Throwable {
        test(BaseHelper.BASE_URL_GH);
    }
    
    @SmallTest
    public void test(String url) {
        Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, url + "/rating/options/");
        
        sendRequest(args, new GetRatingsHelper(), new IResponseCallback() {

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
