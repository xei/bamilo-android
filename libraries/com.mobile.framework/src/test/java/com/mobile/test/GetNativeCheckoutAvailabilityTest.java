package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.checkout.GetNativeCheckoutAvailable;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

public class GetNativeCheckoutAvailabilityTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

//        HashMap<String, String> data = new HashMap<>();

        requestBundle = new RequestBundle.Builder()
                .setUrl("http://alice-staging.jumia.com.ng/mobapi/v1.7/main/getconfig/module/configuration/key/native_checkout_mobile_api/")
                .setCache(EventType.NATIVE_CHECKOUT_AVAILABLE.cacheTime)
//                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new GetNativeCheckoutAvailable(requestBundle, this).execute();
//        BaseRequest<SuperNativeCheckoutAvailability> baseResponse = new BaseRequest<SuperNativeCheckoutAvailability>(IS_AUTOMATED_TEST,requestBundle,this){
//            @Override
//            public void execute() {
//
//            }
//        };
//        baseResponse.executeUseReflection("getNativeCheckoutAvailable");
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        Print.d("TEST ERROR: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}
