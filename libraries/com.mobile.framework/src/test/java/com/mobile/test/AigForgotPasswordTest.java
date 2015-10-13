package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigForgotPasswordTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.FORGET_PASSWORD_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.forgotPassword;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/customer/forgotpassword/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("Alice_Module_Mobapi_Form_Customer_ForgotPasswordForm[email]", "testjumia+mobile1444619621092@jumia.com");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());


        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
