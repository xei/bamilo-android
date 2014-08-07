package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetChangePasswordHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.helper.GetShoppingCartAddItemHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class ShoppingCartAddItemTest extends FrameworkServiceTests {
	private static String TAG = ShoppingCartAddItemTest.class.getSimpleName();
	protected boolean processed = false;
	protected boolean processed_add = false;

	public void testShoppingCartAddItemIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI, RequestConstants.PRODUCT_SKU_CI, RequestConstants.PRODUCT_SKU_SIMPLE_CI);
	}

	public void testShoppingCartAddItemKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE, RequestConstants.PRODUCT_SKU_KE, RequestConstants.PRODUCT_SKU_SIMPLE_KE);
	}

	public void testShoppingCartAddItemMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA, RequestConstants.PRODUCT_SKU_MA, RequestConstants.PRODUCT_SKU_SIMPLE_MA);
	}

	public void testShoppingCartAddItemNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG, RequestConstants.PRODUCT_SKU_NG, RequestConstants.PRODUCT_SKU_SIMPLE_NG);
	}

	public void testShoppingCartAddItemEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG, RequestConstants.PRODUCT_SKU_EG, RequestConstants.PRODUCT_SKU_SIMPLE_EG);
	}

	public void testShoppingCartAddItemUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG, RequestConstants.PRODUCT_SKU_UG, RequestConstants.PRODUCT_SKU_SIMPLE_UG);
	}
	
	public void testShoppingCartAddItemGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH, RequestConstants.PRODUCT_SKU_GH, RequestConstants.PRODUCT_SKU_SIMPLE_GH);
	}

	public void test(String url, String sku, String simple){
		/**
		 * Login before adding to cart
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
	}
}


