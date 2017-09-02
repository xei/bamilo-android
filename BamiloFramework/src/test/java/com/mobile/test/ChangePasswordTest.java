package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

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
                .setEndPoint("https://www.jumia.com.ng/mobapi/v1.7/customer/changepass/")
                .setCache(EventType.FORGET_PASSWORD_EVENT.cacheTime)
                .addQueryData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(requestBundle, this).execute(AigApiInterface.changePassword);
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
