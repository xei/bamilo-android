package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

public class GetShippingMethodsFormTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        requestBundle = new RequestBundle.Builder()
                .setUrl("http://alice-staging.jumia.com.ng/mobapi/v1.7/multistep/shippingmethod/")
                .setCache(EventType.GET_SHIPPING_METHODS_EVENT.cacheTime)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        //new GetShippingMethodsForm(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getShippingMethodsForm);
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
