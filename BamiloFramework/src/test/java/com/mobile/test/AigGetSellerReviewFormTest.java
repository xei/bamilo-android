package com.mobile.test;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetSellerReviewFormTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_FORM_SELLER_REVIEW_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getSellerReviewForm;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/forms/sellerreview/";
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
