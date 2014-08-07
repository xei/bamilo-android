package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetChangePasswordHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetShoppingCartRemoveItemHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ShoppingCartRemoveItemTest extends FrameworkServiceTests {
	private static String TAG = ShoppingCartRemoveItemTest.class.getSimpleName();
	protected boolean processed = false;

	public void testShoppingCartRemoveItemIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI, RequestConstants.PRODUCT_SKU_SIMPLE_CI);
	}

	public void testShoppingCartRemoveItemKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE, RequestConstants.PRODUCT_SKU_SIMPLE_KE);
	}

	public void testShoppingCartRemoveItemMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA, RequestConstants.PRODUCT_SKU_SIMPLE_MA);
	}

	public void testShoppingCartRemoveItemNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG, RequestConstants.PRODUCT_SKU_SIMPLE_NG);
	}

	public void testShoppingCartRemoveItemEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG, RequestConstants.PRODUCT_SKU_SIMPLE_EG);
	}

	public void testShoppingCartRemoveItemUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG, RequestConstants.PRODUCT_SKU_SIMPLE_UG);
	}
	
	public void testShoppingCartRemoveItemGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH, RequestConstants.PRODUCT_SKU_SIMPLE_GH);
	}

	public void test(String url, String sku){
		/**
		 * Login
		 */
		Log.i(TAG, "mService => " + mService);
		Bundle args1 = new Bundle();
		ContentValues contentValues = new ContentValues();
		contentValues.put(RequestConstants.KEY_LOGIN_EMAIL, RequestConstants.CUSTOMER_EMAIL);
		contentValues.put(RequestConstants.KEY_LOGIN_PASSWORD, RequestConstants.CUSTOMER_PASSWORD);
		args1.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, contentValues);
		args1.putString(BaseHelper.KEY_COUNTRY, url+"/customer/login/");
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
		 * Remove Item
		 */
		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		args.putString(BaseHelper.KEY_COUNTRY, url+"/order/remove?setDevice=mobileApi");
		ContentValues contentValues2 = new ContentValues();
		//contentValues2.put(RequestConstants.KEY_ADD_CART_SKU, RequestConstants.PRODUCT_SKU_UG);
		contentValues2.put(RequestConstants.KEY_ADD_CART_SKU_SIMPLE, sku);
		//contentValues2.put(RequestConstants.KEY_ADD_CART_QUANTITY, 1);
		args.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, contentValues2);
		sendRequest(args, new GetShoppingCartRemoveItemHelper(), new IResponseCallback() {

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
