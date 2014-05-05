package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetShippingMethodsHelper;
import pt.rocket.framework.testproject.helper.GetSignupHelper;
import pt.rocket.framework.testproject.helper.SetBillingMethodHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class GetShippingMethodsTest extends FrameworkServiceTests {
    private static String TAG = GetShippingMethodsTest.class.getSimpleName();
    protected boolean processed = false;
    protected boolean processed1 = false;
    protected boolean processed2 = false;
    
public void testGetShippingMethodsIC() throws Throwable {
    test(BaseHelper.BASE_URL_CI);
}

public void testGetShippingMethodsMA() throws Throwable {
    test(BaseHelper.BASE_URL_MA);
}

public void testGetShippingMethodsNG() throws Throwable {
    test(BaseHelper.BASE_URL_NG);
}

public void testGetShippingMethodsEG() throws Throwable {
    test(BaseHelper.BASE_URL_EG);
}

//UG and KE don't have NC yet

public void testGetShippingMethodsKE() throws Throwable {
  test(BaseHelper.BASE_URL_KE);
}

public void testGetShippingMethodsUG() throws Throwable {
  test(BaseHelper.BASE_URL_UG);
}

public void test(String url){
    /**
     * Login before adding to cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    ContentValues contentValues = new ContentValues();
    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
    args.putString(BaseHelper.KEY_COUNTRY, url + "/customer/login/");
    sendRequest(args, new GetLoginHelper(), new IResponseCallback() {

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
     * SetBilling
     */
    
    Bundle argsSetBilling = new Bundle();
    ContentValues contentValues1 = new ContentValues();
    contentValues1.put(RequestConstants.KEY_BILLING_METHOD_DIFFERENT,RequestConstants.BILLING_METHOD_DIFFERENT);
    
    if(url.equals(BaseHelper.BASE_URL_CI)){
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC);
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_IC);
    }else if(url.equals(BaseHelper.BASE_URL_EG)){
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG);
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_EG);
    }else if(url.equals(BaseHelper.BASE_URL_MA)){
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA);
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_MA);
    }else if(url.equals(BaseHelper.BASE_URL_NG)){
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_BILLING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG);
        contentValues1.put(RequestConstants.KEY_BILLING_METHOD_SHIPPING_ADDRESS_ID,RequestConstants.BILLING_METHOD_SHIPPING_ADDRESS_ID_NG);
    }
    

    argsSetBilling.putParcelable(GetSignupHelper.CONTENT_VALUES, contentValues1);
    argsSetBilling.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/billing/");
    sendRequest(argsSetBilling, new SetBillingMethodHelper(), new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle
                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue(
                    "Failed onRequestError - The base of the json is wrongly constructed, something is missing : "
                            + bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY),
                    jsonValidation);
            processed1 = true;
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle
                    .getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue(
                    "Failed onRequestComplete - The base of the json is wrongly constructed, something is missing",
                    jsonValidation);
            processed1 = true;

        }
    });
    // necessary in order to make the test wait for the server response
    while (!processed1) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Get Shipping Method
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args1 = new Bundle();
    args1.putString(BaseHelper.KEY_COUNTRY, url + "/multistep/shippingmethod/");
    sendRequest(args1, new GetShippingMethodsHelper(), new IResponseCallback() {

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