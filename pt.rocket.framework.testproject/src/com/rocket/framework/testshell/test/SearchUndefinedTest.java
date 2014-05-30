package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetCampaignHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetSearchUndefinedHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class SearchUndefinedTest extends FrameworkServiceTests {
    private static String TAG = SearchUndefinedTest.class.getSimpleName();
    protected boolean processed = false;
    protected boolean processed1 = false;
    
public void testSearchUndefinedIC() throws Throwable {
    test(BaseHelper.BASE_URL_CI);
}

public void testSearchUndefinedKE() throws Throwable {
	test(BaseHelper.BASE_URL_KE);
}

public void testSearchUndefinedMA() throws Throwable {
    test(BaseHelper.BASE_URL_MA);
}

public void testSearchUndefinedNG() throws Throwable {
    test(BaseHelper.BASE_URL_NG);
}

public void testSearchUndefinedEG() throws Throwable {
    test(BaseHelper.BASE_URL_EG);
}

public void testSearchUndefinedUG() throws Throwable {
	test(BaseHelper.BASE_URL_UG);
}

public void test(String url){
    /**
     * Campaign
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    args.putString(BaseHelper.KEY_COUNTRY, url + "/search?setDevice=mobileApi&q=teste123456");
    args.putBoolean(Constants.BUNDLE_GENERAL_RULES_FALSE_KEY, false);
    sendRequest(args, new GetSearchUndefinedHelper(), new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO Auto-generated method stub
        	Boolean jsonValidation = false;
        	if(bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY))
              bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
        	
        		
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
}

}