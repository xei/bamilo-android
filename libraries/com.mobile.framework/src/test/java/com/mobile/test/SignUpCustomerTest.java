package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.session.SignUpCustomer;

import java.util.HashMap;

public class SignUpCustomerTest extends BaseTestCase {

    RequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("register_signup[scenario]", "guest");
        data.put("register_signup[email]", "asdasdasdasd@gmalas.com");
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://www.jumia.com.ng/mobapi/v1.7/customer/create/")
                .setCache(EventType.SET_SIGNUP_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new SignUpCustomer(IS_AUTOMATED_TEST, requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.success);
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        System.out.println("TEST ERROR: " + response.success);
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}
