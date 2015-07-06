package com.mobile.test;

import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;
import java.util.List;
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
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        //assertTrue("Success is true", response.hadSuccess());
        assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");

        //
//        //final boolean success = response.hadSuccess();
//        try {
//            analyzeOnErrorEvent(response);
//        } finally {
//            // tests returned then countdown semaphore
//            mCountDownLatch.countDown();
//
//            //assertFunction();
//            org.junit.Assert.fail("Success is false");
//        }
    }

    public void analyzeOnErrorEvent(final BaseResponse response) {

        //final JumiaError jumiaError = response.getError();
        ErrorCode errorCode = response.getError().getErrorCode();
        Map<String, List<String>> errorMessages = response.getErrorMessages();
        // emit a test fail on main thread so that it can be catched and reported by the fail caching mechanism
        junit.framework.Assert.fail("Request failed error code: " + errorCode + ". Message: " + (errorMessages != null ? errorMessages.toString() : " no message") + " when requesting: " + getEventType());
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
