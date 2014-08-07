package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetShoppingCartItemsHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ShoppingCartItemsTest extends FrameworkServiceTests {
	private static String TAG = ShoppingCartItemsTest.class.getSimpleName();
	protected boolean processed_login = false;
	protected boolean processed_items = false;

	public void testShoppingCartItemsIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI);
	}

	public void testShoppingCartItemsKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE);    
	}

	public void testShoppingCartItemsMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA);
	}

	public void testShoppingCartItemsNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG);
	}

	public void testShoppingCartItemsEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG);
	}

	public void testShoppingCartItemsUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG);
	}

	public void testShoppingCartItemsGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH);
	}

	public void test(String url){
		/**
		 * Login
		 */
		 Log.i(TAG, "mService => " + mService);
		 Bundle args_login = new Bundle();
		 ContentValues contentValues_login = new ContentValues();
		 contentValues_login.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		 contentValues_login.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		 args_login.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues_login);
		 args_login.putString(BaseHelper.KEY_COUNTRY, url + "/customer/login/");
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
		  * Cart Items
		  */
		 Log.i(TAG, "mService => " + mService);
		 Bundle args_items = new Bundle();
		 args_items.putString(BaseHelper.KEY_COUNTRY, url + "/order/cartdata?setDevice=mobileApi");
		 sendRequest(args_items, new GetShoppingCartItemsHelper(), new IResponseCallback() {

			 @Override
			 public void onRequestError(Bundle bundle) {
				 // TODO Auto-generated method stub
				 Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				 assertTrue("Failed onRequestError - The base of the json is wrongly constructed, something is missing : "+bundle.getString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY), jsonValidation);
				 processed_items = true;
			 }

			 @Override
			 public void onRequestComplete(Bundle bundle) {
				 // TODO Auto-generated method stub
				 Boolean jsonValidation = bundle.getBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY);
				 assertTrue("Failed onRequestComplete - The base of the json is wrongly constructed, something is missing", jsonValidation);
				 processed_items = true;

			 }
		 });
		 //necessary in order to make the test wait for the server response
		 while (!processed_items) {
			 try {
				 Thread.sleep(1000);
			 } catch (InterruptedException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
		 }


	}

}
