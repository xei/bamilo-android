package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetBillingAddressHelper;
import pt.rocket.framework.testproject.helper.GetCitiesHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetRegionsHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class CitiesTest extends FrameworkServiceTests {
    private static String TAG = CitiesTest.class.getSimpleName();
    protected boolean processed = false;
    protected boolean processed1 = false;
    
public void testCitiesIC() throws Throwable {
  test(BaseHelper.BASE_URL_CI,319);
}

public void testCitiesKE() throws Throwable {
    test(BaseHelper.BASE_URL_KE, 233);
}

public void testCitiesMA() throws Throwable {
  test(BaseHelper.BASE_URL_MA,94);
}

public void testCitiesNG() throws Throwable {
  test(BaseHelper.BASE_URL_NG,25);
}

public void testCitiesEG() throws Throwable {
  test(BaseHelper.BASE_URL_EG,182);
}

public void testCitiesUG() throws Throwable {
  test(BaseHelper.BASE_URL_UG, 229);
}

public void test(String url, int region_id){
    /**
     * Login before adding to cart
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
     * Address
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    args.putString(BaseHelper.KEY_COUNTRY, url + "/customer/address/cities/?region="+region_id);
    sendRequest(args, new GetCitiesHelper(), new IResponseCallback() {

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
}

}