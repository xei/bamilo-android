package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.constants.RequestConstants;
import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetAddressHelper;
import pt.rocket.framework.testproject.helper.GetCampaignHelper;
import pt.rocket.framework.testproject.helper.GetCountryConfigsHelper;
import pt.rocket.framework.testproject.helper.GetLoginHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class GetCountryConfigsTest extends FrameworkServiceTests {
	private static String TAG = GetCountryConfigsTest.class.getSimpleName();
	protected boolean processed = false;
	protected boolean processed1 = false;

	public void testCountryConfigsIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI);
	}

	public void testCountryConfigsKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE);
	}

	public void testCountryConfigsMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA);
	}

	public void testCountryConfigsNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG);
	}

	public void testCountryConfigsEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG);
	}

	public void testCountryConfigsUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG);
	}

	public void testCountryConfigsGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH);
	}

	public void test(String url){
		/**
		 * Country Configs
		 */
		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		args.putString(BaseHelper.KEY_COUNTRY, url+"/main/getcountryconfs/");
		sendRequest(args, new GetCountryConfigsHelper(), new IResponseCallback() {

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