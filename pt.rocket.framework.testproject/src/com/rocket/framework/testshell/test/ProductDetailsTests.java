package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetCategoriesHelper;
import pt.rocket.framework.testproject.helper.GetProductDetailsHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;

public class ProductDetailsTests extends FrameworkServiceTests {
	private static String TAG = ProductDetailsTests.class.getSimpleName();
	protected boolean processed = false;

	public void testProductDetailsIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI+"/Chemise-Crepon-a-Manches-Courtes---Bleu-53325.html");
	}

	public void testProductDetailsKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE+"/Brown-Strap-Sandals-14350.html");
	}

	public void testProductDetailsMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA+"/iPad-2---Blanc---16-Go---Wifi-9636.html");
	}

	public void testProductDetailsNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG+"/sollatek-tv-guard-230v-5a-uk-adaptor-36093.html");
	}

	public void testProductDetailsEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG+"/Grey-%26-Burgundy-Two-tone-Canvas-Classic-Sneakers-149512.html");
	}

	public void testProductDetailsUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG+"/samsung-galaxy-note-3-32-gb-black-12713.html");
	}

	public void testProductDetailsGH() throws Throwable {
		test(BaseHelper.BASE_URL_GH+"/samsung-np-n102s-laptop-intel-atom-processor-2gb-ram-320gb-hdd-10.1-intel-gma-windows-7-1086.html");
	}

	@SmallTest
	public void test(String url) {
		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		args.putString(BaseHelper.KEY_COUNTRY, url );
		sendRequest(args, new GetProductDetailsHelper(), new IResponseCallback() {

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
