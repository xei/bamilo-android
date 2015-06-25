package com.mobile.test;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

public class AigLoginCustomerTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.LOGIN_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.loginCustomer;
    }

    @Override
    public String getUrl() {
        return "https://www.jumia.com.ng/mobapi/v1.7/customer/login/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("Alice_Module_Customer_Model_LoginForm[email]", "sofias@jumia.com");
        data.put("Alice_Module_Customer_Model_LoginForm[password]", "123456");
        return data;
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        Print.d("TEST ERROR: " + response.hadSuccess());
        //final boolean success = response.hadSuccess();
        try {
            analyzeOnErrorEvent(response);
        } finally {
            // tests returned then countdown semaphore
            mCountDownLatch.countDown();

            //assertFunction();
            Assert.fail("Success is false");
        }
    }

    /*
    public synchronized void assertFunction(){
        new Runnable() {
            @Override
            public void run() {
                Assert.fail("Success is false");
                //Assert.fail("Request failed error code: " + errorCode + ". Message: " + (errorListFinal != null ? errorListFinal.toString() : " no message") + " when requesting: " + eventType);
            }
        };
    }*/

}
