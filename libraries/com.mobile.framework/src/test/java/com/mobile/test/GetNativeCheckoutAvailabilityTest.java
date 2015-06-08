package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.checkout.SuperNativeCheckoutAvailability;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.checkout.GetNativeCheckoutAvailable;

import java.util.HashMap;

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
        System.out.println("TEST REQUEST");
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
        System.out.println("TEST SUCCESS: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        System.out.println("TEST ERROR: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}
