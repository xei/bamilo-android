package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

import java.util.HashMap;

public class SignUpCustomerTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("register_signup[scenario]", "guest");
        data.put("register_signup[email]", "asdasdasdasd@gmalas.com");
        requestBundle = new RequestBundle.Builder()
                .setEndPoint("https://www.jumia.com.ng/mobapi/v1.7/customer/create/")
                .setCache(EventType.GUEST_LOGIN_EVENT.cacheTime)
                .addQueryData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        //new SignUpCustomer(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.signUpCustomer);
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
