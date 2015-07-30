package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;
import java.util.Map;

public class AigGetLoginFormTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_LOGIN_FORM_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getLoginForm;
    }

    @Override
    public String getUrl() {
        return "https://www.jumia.com.ng/mobapi/v1.7/forms/login/";
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