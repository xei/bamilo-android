package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetApiInfoHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ApiInfoTest extends FrameworkServiceTests {
    private static String TAG = ApiInfoTest.class.getSimpleName();
    protected boolean processed = false;

    public void testGetApiInfoIC() throws Throwable {
        test(BaseHelper.BASE_URL_CI);
    }
    
    public void testGetApiInfoMA() throws Throwable {
        test(BaseHelper.BASE_URL_MA);
    }
    
    public void testGetApiInfoKE() throws Throwable {
        test(BaseHelper.BASE_URL_KE);
    }
    
    public void testGetApiInfoNG() throws Throwable {
        test(BaseHelper.BASE_URL_NG);
    }
    
    public void testGetApiInfoUG() throws Throwable {
        test(BaseHelper.BASE_URL_UG);
    }
    
    public void testGetApiInfoEG() throws Throwable {
        test(BaseHelper.BASE_URL_EG);
    }
    
    public void testGetApiInfoGH() throws Throwable {
        test(BaseHelper.BASE_URL_GH);
    }
    
    @SmallTest
    public void test(String url) {
        Log.i(TAG, "mService => " + mService);
        final Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, url + "/main/md5?setDevice=mobileApi");
        sendRequest(args, new GetApiInfoHelper(), new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
                // TODO Auto-generated method stub
            	Boolean jsonValidation = false;
            	
            	if(bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
            	if(bundle != null){
            		assertTrue("[country]"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"[country] Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            	} else {
            		assertTrue("[country]"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"[country] Failed onRequestError - Bundle is null! ", jsonValidation);
            	}
                processed = true;
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                // TODO Auto-generated method stub
                Boolean jsonValidation = false;;
            	if(bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
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
