package com.mobile.test;

import com.mobile.service.forms.Form;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetSignUpFormTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_SIGNUP_FORM_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getSignUpForm;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/forms/registersignup/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = null;
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());
        Form change_password_form = (Form) response.getMetadata().getData();
        assertNotNull("Form is null", change_password_form);

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
