package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class AddressTest extends FrameworkServiceTests {
    private static String TAG = AddressTest.class.getSimpleName();
    protected boolean processed = false;
    protected boolean processed1 = false;
    
public void testAddressIC() throws Throwable {
    test("https://www.jumia.ci");
}

public void testAddressKE() throws Throwable {
	test("https://www.jumia.co.ke");
}

public void testAddressMA() throws Throwable {
    test("https://www.jumia.ma");
}

public void testAddressNG() throws Throwable {
    test("https://www.jumia.com.ng");
}

public void testAddressEG() throws Throwable {
    test("https://www.jumia.com.eg");
}

public void testAddressUG() throws Throwable {
	test("https://www.jumia.co.ke");
}

public void test(String url){
    /**
     * Login before adding to cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args1 = new Bundle();
    ContentValues contentValues = new ContentValues();
    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
    args1.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/customer/login/");
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
    args.putString(BaseHelper.KEY_COUNTRY, url + "/mobapi/customer/address/list/");
    sendRequest(args, new GetAddressHelper(), new IResponseCallback() {

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