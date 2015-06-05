package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.session.ChangePassword;

import java.util.HashMap;

public class ChangePasswordTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();


        HashMap<String, String> data = new HashMap<>();
        data.put("Alice_Module_Customer_Model_PasswordForm[email]", "sofias@jumia.com");
        data.put("Alice_Module_Customer_Model_PasswordForm[password]", "123456");
        data.put("Alice_Module_Customer_Model_PasswordForm[password2]", "123456");
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://www.jumia.com.ng/mobapi/v1.7/customer/changepass/")
                .setCache(EventType.FORGET_PASSWORD_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new ChangePassword(requestBundle, this).execute();
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
