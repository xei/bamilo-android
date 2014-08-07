package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetCampaignHelper;
import pt.rocket.framework.testproject.helper.GetCartDataHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetManageNewsletterHelper;
import pt.rocket.framework.testproject.helper.GetSearchUndefinedHelper;
import pt.rocket.framework.testproject.helper.GetShoppingCartAddItemHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class CartDataTest extends FrameworkServiceTests {
    private static String TAG = CartDataTest.class.getSimpleName();
    protected boolean processed_login = false;
    protected boolean processed_add = false;
    protected boolean processed_test = false;
    
public void testCartDataIC() throws Throwable {
    test(BaseHelper.BASE_URL_CI, RequestConstants.PRODUCT_SKU_CI, RequestConstants.PRODUCT_SKU_SIMPLE_CI);
}

public void testCartDataKE() throws Throwable {
	test(BaseHelper.BASE_URL_KE, RequestConstants.PRODUCT_SKU_KE, RequestConstants.PRODUCT_SKU_SIMPLE_KE);
}

public void testCartDataMA() throws Throwable {
    test(BaseHelper.BASE_URL_MA, RequestConstants.PRODUCT_SKU_MA, RequestConstants.PRODUCT_SKU_SIMPLE_MA);
}

public void testCartDataNG() throws Throwable {
    test(BaseHelper.BASE_URL_NG, RequestConstants.PRODUCT_SKU_NG, RequestConstants.PRODUCT_SKU_SIMPLE_NG);
}

public void testCartDataEG() throws Throwable {
    test(BaseHelper.BASE_URL_EG, RequestConstants.PRODUCT_SKU_EG, RequestConstants.PRODUCT_SKU_SIMPLE_EG);
}

public void testCartDataUG() throws Throwable {
	test(BaseHelper.BASE_URL_UG, RequestConstants.PRODUCT_SKU_UG, RequestConstants.PRODUCT_SKU_SIMPLE_UG);
}

public void testCartDataGH() throws Throwable {
	test(BaseHelper.BASE_URL_GH, RequestConstants.PRODUCT_SKU_GH, RequestConstants.PRODUCT_SKU_SIMPLE_GH);
}

public void test(String url, String sku, String simple){
	/**
	 * Login
	 */
	Log.i(TAG, "mService => " + mService);
    Bundle args_login = new Bundle();
    ContentValues contentValues_login = new ContentValues();
    contentValues_login.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues_login.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args_login.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues_login);
    args_login.putString(BaseHelper.KEY_COUNTRY, url+"/customer/login/");
    sendRequest(args_login, new GetLoginHelper(), new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            processed_login = true;
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
            processed_login = true;

        }
    });
    //necessary in order to make the test wait for the server response
    while (!processed_login) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Add to Cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args_add = new Bundle();
    args_add.putString(BaseHelper.KEY_COUNTRY, url+"/order/add?setDevice=mobileApi");
    
    ContentValues contentValues_add = new ContentValues();
    contentValues_add.put(RequestConstants.KEY_ADD_CART_SKU, sku);
    contentValues_add.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, simple);
    contentValues_add.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
    args_add.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, contentValues_add);
    sendRequest(args_add, new GetShoppingCartAddItemHelper(), new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            processed_add = true;
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
            processed_add = true;

        }
    });
    //necessary in order to make the test wait for the server response
    while (!processed_add) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	/**
     * Cart Data
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args_test = new Bundle();
    args_test.putString(BaseHelper.KEY_COUNTRY, url + "/order/cartdata/");
    sendRequest(args_test, new GetCartDataHelper(), new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            processed_test = true;
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            // TODO Auto-generated method stub
            Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
            processed_test = true;

        }
    });
    //necessary in order to make the test wait for the server response
    while (!processed_test) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

}