package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetProductListHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ProductListTests extends FrameworkServiceTests {
    private static String TAG = ProductListTests.class.getSimpleName();
    protected boolean processed = false;

    @SmallTest
    public void testProductListIC() throws Throwable {
        test(BaseHelper.BASE_URL_CI+"/telephonie?page=1&maxitems=14&sort=price&dir=asc");
    }
    
    @SmallTest
    public void testProductListKE() throws Throwable {
        test(BaseHelper.BASE_URL_KE+"/womens-dresses?page=1&maxitems=14&sort=price&dir=asc");
    }
    
    @SmallTest
    public void testProductListMA() throws Throwable {
        test(BaseHelper.BASE_URL_MA+"/vetements-femmes-mode?page=1&maxitems=14&sort=price&dir=asc");
    }
    
    @SmallTest
    public void testProductListNG() throws Throwable {
        test(BaseHelper.BASE_URL_NG+"/womens-dresses?page=1&maxitems=14&sort=price&dir=asc");
    }
    
    @SmallTest
    public void testProductListEG() throws Throwable {
        test(BaseHelper.BASE_URL_EG+"/womens-clothing?page=1&maxitems=14&sort=price&dir=asc");
    }

    public void testProductListUG() throws Throwable {
        test(BaseHelper.BASE_URL_UG+"/womens-dresses?page=1&maxitems=14&sort=price&dir=asc");
        
    }
    
//    public void testProductList_Staging_NG() throws Throwable {
//        test(BaseHelper.BASE_URL_STAGING_NG+"/womens-dresses?page=1&maxitems=14&sort=price&dir=asc");
//        
//    }
    
    public void test(String url) throws Throwable {
        Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, url);
        sendRequest(args, new GetProductListHelper(), new IResponseCallback() {

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
