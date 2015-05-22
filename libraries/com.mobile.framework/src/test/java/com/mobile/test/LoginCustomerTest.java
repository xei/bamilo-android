package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequestBundle;
import com.mobile.newFramework.requests.session.LoginCustomer;

import java.util.HashMap;

public class LoginCustomerTest extends BaseTestCase {

    BaseRequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Params
        // facebook_login=false
        // __autologin_requested__=true

        HashMap<String, String> data = new HashMap<>();
        data.put("Alice_Module_Customer_Model_LoginForm[email]", "sofias@jumia.com");
        data.put("Alice_Module_Customer_Model_LoginForm[password]", "123456");
        requestBundle = new BaseRequestBundle.Builder()
                .setUrl("https://www.jumia.com.ng/mobapi/v1.7/customer/login/")
                .setCache(EventType.LOGIN_EVENT.cacheTime)
                .setData(data)
                .setPriority(true)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new LoginCustomer(IS_AUTOMATED_TEST, requestBundle, this).execute();
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
