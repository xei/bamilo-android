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

	public void testCallToOrderIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI);
	}

	public void testCallToOrderKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE);
	}

	public void testCallToOrderMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA);
	}

	public void testCallToOrderNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG);
	}

	public void testCallToOrderUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG);
	}

	public void testCallToOrderEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG);
	}

	public void testCallToOrderGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH);
	}

	@SmallTest
	public void test(String url) {
		Log.i(TAG, "mService => " + mService);
		final Bundle args = new Bundle();
		args.putString(BaseHelper.KEY_COUNTRY, url + "/main/getconfig/module/configurationml/key/phone_number/");
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

}
