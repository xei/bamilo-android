package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetCallToOrderHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class CallToOrder extends FrameworkServiceTests {
    private static String TAG = CallToOrder.class.getSimpleName();
    protected boolean processed = false;

    @SmallTest
    public void testCallToOrderIC() throws Throwable {
        Log.i(TAG, "mService => " + mService);
        final Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, BaseHelper.BASE_URL_CI+"/main/getconfig/module/configurationml/key/phone_number/");
        sendRequest(args, new GetCallToOrderHelper(), new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
            	Boolean jsonValidation = false;
            	
            	if(bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
            	if(bundle != null){
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            	} else {
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - Bundle is null! ", jsonValidation);
            	}
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
    
//    @SmallTest
//    public void testCallToOrderKE() throws Throwable {
//        Log.i(TAG, "mService => " + mService);
//        Bundle args = new Bundle();
//        args.putString(BaseHelper.KEY_COUNTRY, "http://www.jumia.co.ke/mobapi/main/getconfig/module/configurationml/key/phone_number/");
//        sendRequest(args, new GetCallToOrderHelper(), new IResponseCallback() {
//
//            @Override
//            public void onRequestError(Bundle bundle) {
//                // TODO Auto-generated method stub
//                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//                assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
//                processed = true;
//            }
//
//            @Override
//            public void onRequestComplete(Bundle bundle) {
//                // TODO Auto-generated method stub
//                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//                assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
//                processed = true;
//
//            }
//        });
//        //necessary in order to make the test wait for the server response
//        while (!processed) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//    
    @SmallTest
    public void testCallToOrderMA() throws Throwable {
        Log.i(TAG, "mService => " + mService);
        final Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, BaseHelper.BASE_URL_MA+"/main/getconfig/module/configurationml/key/phone_number/");
        sendRequest(args, new GetCallToOrderHelper(), new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
            	Boolean jsonValidation = false;
            	
            	if(bundle != null && bundle.containsKey(Constants.BUNDLE_JSON_VALIDATION_KEY)){
            		jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
            	}
            	if(bundle != null){
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
            	} else {
            		assertTrue("<<"+args.getString(BaseHelper.KEY_COUNTRY_TAG)+"<< Failed onRequestError - Bundle is null! ", jsonValidation);
            	}
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
    
    @SmallTest
    public void testCallToOrderNG() throws Throwable {
        Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, BaseHelper.BASE_URL_NG+"/main/getconfig/module/configurationml/key/phone_number/");
        sendRequest(args, new GetCallToOrderHelper(), new IResponseCallback() {

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
    
    @SmallTest
    public void testCallToOrderEG() throws Throwable {
        Log.i(TAG, "mService => " + mService);
        Bundle args = new Bundle();
        args.putString(BaseHelper.KEY_COUNTRY, BaseHelper.BASE_URL_EG+"/main/getconfig/module/configurationml/key/phone_number/");
        
        sendRequest(args, new GetCallToOrderHelper(), new IResponseCallback() {

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

//    public void testCallToOrderUG() throws Throwable {
//        Log.i(TAG, "mService => " + mService);
//        Bundle args = new Bundle();
//        args.putString(BaseHelper.KEY_COUNTRY, "http://www.jumia.ug/mobapi/main/getconfig/module/configurationml/key/phone_number/");
//        sendRequest(args, new GetCallToOrderHelper(), new IResponseCallback() {
//
//            @Override
//            public void onRequestError(Bundle bundle) {
//                // TODO Auto-generated method stub
//                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//                assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
//                processed = true;
//            }
//
//            @Override
//            public void onRequestComplete(Bundle bundle) {
//                // TODO Auto-generated method stub
//                Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
//                assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
//                processed = true;
//
//            }
//        });
//        //necessary in order to make the test wait for the server response
//        while (!processed) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//    
}
