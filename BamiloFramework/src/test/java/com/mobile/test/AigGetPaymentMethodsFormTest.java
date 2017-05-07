package com.mobile.test;

import com.mobile.newFramework.objects.checkout.MultiStepPayment;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetPaymentMethodsFormTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_MULTI_STEP_PAYMENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getMultiStepPayment;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/forms/paymentmethod/";
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
        MultiStepPayment form = (MultiStepPayment) response.getMetadata().getData();
        assertNotNull("Form is null", form);

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
