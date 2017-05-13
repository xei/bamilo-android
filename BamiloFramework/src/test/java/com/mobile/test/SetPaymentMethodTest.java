package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

import java.util.HashMap;

public class SetPaymentMethodTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        HashMap<String, String> data = new HashMap<>();

        data.put("paymentMethodForm[payment_method]", "1");
        data.put("name", "Cash On Delivery");

        requestBundle = new RequestBundle.Builder()
                .setEndPoint("http://alice-staging.jumia.com.ng/mobapi/v1.7/multistep/paymentmethod/")
                .setCache(EventType.SET_MULTI_STEP_SHIPPING.cacheTime)
                .addQueryData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        //new SetPaymentMethod(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setMultiStepPayment);
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
