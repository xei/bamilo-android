package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetChangePasswordHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetShoppingCartChangeItemQuantityHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ShoppingCartChangetemQuantityTest extends FrameworkServiceTests {
    private static String TAG = ShoppingCartChangetemQuantityTest.class.getSimpleName();
    protected boolean processed = false;
    
public void testShoppingCartChangeItemQuantityIC() throws Throwable {
    	/**
    	 * Login before adding to cart
    	 */
    	Log.i(TAG, "mService => " + mService);
        Bundle args1 = new Bundle();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
        contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
        args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
        args1.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.ci/mobapi/customer/login/");
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
         * Add to Cart
         */
        Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.ci/mobapi/order/cartchange/");
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_CI);
        contentValues2.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, RequestConstants.PRODUCT_SKU_SIMPLE_CI);
        contentValues2.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
        args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
        sendRequest(args, new GetShoppingCartChangeItemQuantityHelper(), new IResponseCallback() {

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

public void testShoppingCartChangeItemQuantityKE() throws Throwable {
	/**
	 * Login before adding to cart
	 */
	Log.i(TAG, "mService => " + mService);
    Bundle args1 = new Bundle();
    ContentValues contentValues = new ContentValues();
    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
    args1.putString(BaseHelper.KEY_COUNTRY, "http://www.jumia.co.ke/mobapi/customer/login/");
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
     * Add to Cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.co.ke/mobapi/order/cartchange/");
    ContentValues contentValues2 = new ContentValues();
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_KE);
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, RequestConstants.PRODUCT_SKU_SIMPLE_KE);
    contentValues2.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
    args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
    sendRequest(args, new GetShoppingCartChangeItemQuantityHelper(), new IResponseCallback() {

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

public void testShoppingCartChangeItemQuantityMA() throws Throwable {
	/**
	 * Login before adding to cart
	 */
	Log.i(TAG, "mService => " + mService);
    Bundle args1 = new Bundle();
    ContentValues contentValues = new ContentValues();
    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
    args1.putString(BaseHelper.KEY_COUNTRY, "http://www.jumia.ma/mobapi/customer/login/");
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
     * Add to Cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.ma/mobapi/order/cartchange/");
    ContentValues contentValues2 = new ContentValues();
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_MA);
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, RequestConstants.PRODUCT_SKU_SIMPLE_MA);
    contentValues2.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
    args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
    sendRequest(args, new GetShoppingCartChangeItemQuantityHelper(), new IResponseCallback() {

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

public void testShoppingCartChangeItemQuantityNG() throws Throwable {
	/**
	 * Login before adding to cart
	 */
	Log.i(TAG, "mService => " + mService);
    Bundle args1 = new Bundle();
    ContentValues contentValues = new ContentValues();
    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
    args1.putString(BaseHelper.KEY_COUNTRY, "http://www.jumia.com.ng/mobapi/customer/login/");
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
     * Add to Cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.com.ng/mobapi/order/cartchange/");
    ContentValues contentValues2 = new ContentValues();
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_NG);
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, RequestConstants.PRODUCT_SKU_SIMPLE_NG);
    contentValues2.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
    args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
    sendRequest(args, new GetShoppingCartChangeItemQuantityHelper(), new IResponseCallback() {

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

public void testShoppingCartChangeItemQuantityEG() throws Throwable {
	/**
	 * Login before changing password
	 */
	Log.i(TAG, "mService => " + mService);
    Bundle args1 = new Bundle();
    ContentValues contentValues = new ContentValues();
    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
    args1.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.com.eg/mobapi/customer/login/");
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
     * Add to Cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.com.eg/mobapi/order/cartchange/");
    ContentValues contentValues2 = new ContentValues();
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_EG);
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, RequestConstants.PRODUCT_SKU_SIMPLE_EG);
    contentValues2.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
    args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
    sendRequest(args, new GetShoppingCartChangeItemQuantityHelper(), new IResponseCallback() {

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

public void testShoppingCartChangeItemQuantityUG() throws Throwable {
	/**
	 * Login before adding to cart
	 */
	Log.i(TAG, "mService => " + mService);
    Bundle args1 = new Bundle();
    ContentValues contentValues = new ContentValues();
    contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
    contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
    args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
    args1.putString(BaseHelper.KEY_COUNTRY, "http://www.jumia.ug/mobapi/customer/login/");
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
     * Add to Cart
     */
    Log.i(TAG, "mService => " + mService);
    Bundle args = new Bundle();
    args.putString(BaseHelper.KEY_COUNTRY, "https://www.jumia.ug/mobapi/order/cartchange/");
    ContentValues contentValues2 = new ContentValues();
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_UG);
    contentValues2.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, RequestConstants.PRODUCT_SKU_SIMPLE_UG);
    contentValues2.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
    args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
    sendRequest(args, new GetShoppingCartChangeItemQuantityHelper(), new IResponseCallback() {

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
