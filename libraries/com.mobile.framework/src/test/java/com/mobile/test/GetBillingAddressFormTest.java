package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.address.GetBillingAddressForm;
import com.mobile.newFramework.utils.EventType;

public class GetBillingAddressFormTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        requestBundle = new RequestBundle.Builder()
                .setUrl("http://alice-staging.jumia.com.ng/mobapi/v1.7/multistep/billing/")
                .setCache(EventType.GET_BILLING_FORM_EVENT.cacheTime)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetBillingAddressForm(requestBundle, this).execute();
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
