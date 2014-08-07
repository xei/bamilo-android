package com.rocket.framework.testshell.test;

import pt.rocket.framework.testproject.helper.BaseHelper;
import pt.rocket.framework.testproject.helper.GetTrackOrderHelper;
import pt.rocket.framework.testproject.interfaces.IResponseCallback;
import pt.rocket.framework.testproject.utils.Log;
import pt.rocket.framework.utils.Constants;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;


public class TrackOrderTest extends FrameworkServiceTests {
	private static String TAG = TrackOrderTest.class.getSimpleName();
	protected boolean processed = false;

	public void testGetTrackOrderIC() throws Throwable {
		test(BaseHelper.BASE_URL_CI+"/order/trackingorder/?setDevice=mobileApi&ordernr=300044656"); 
	}

	public void testGetTrackOrderKE() throws Throwable {
		test(BaseHelper.BASE_URL_KE+"/order/trackingorder/?setDevice=mobileApi&ordernr=300093356"); 
	}

	public void testGetTrackOrderMA() throws Throwable {
		test(BaseHelper.BASE_URL_MA+"/order/trackingorder/?setDevice=mobileApi&ordernr=300799592");
	}

	public void testGetTrackOrderNG() throws Throwable {
		test(BaseHelper.BASE_URL_NG+"/order/trackingorder/?setDevice=mobileApi&ordernr=300726581");
	}

	public void testGetTrackOrderEG() throws Throwable {
		test(BaseHelper.BASE_URL_EG+"/order/trackingorder/?setDevice=mobileApi&ordernr=300873796");
	}

	public void testGetTrackOrderUG() throws Throwable {
		test(BaseHelper.BASE_URL_UG+"/order/trackingorder/?setDevice=mobileApi&ordernr=400033556");
	}

	public void testGetTrackOrderGH() throws Throwable {
//		test(BaseHelper.BASE_URL_GH+"/order/trackingorder/?setDevice=mobileApi&ordernr=300000073");
		test(BaseHelper.BASE_URL_GH+"/order/trackingorder/?setDevice=mobileApi&ordernr=300000019");
	}

	@SmallTest
	public void test(String url){
		Log.i(TAG, "mService => " + mService);
		Bundle args = new Bundle();
		args.putString(BaseHelper.KEY_COUNTRY, url);

		sendRequest(args, new GetTrackOrderHelper(), new IResponseCallback() {

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
