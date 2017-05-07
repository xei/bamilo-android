package com.mobile.test;

import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

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
        return AigMobApiNigeriaTestSuite.HOST+"/customer/login/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("Alice_Module_Customer_Model_LoginForm[email]", "testjumia+mobile1444619621092@jumia.com");
        data.put("Alice_Module_Customer_Model_LoginForm[password]", "123456");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());

        CheckoutStepLogin login_response = (CheckoutStepLogin) response.getMetadata().getData();
        assertNotNull("Customer is null", login_response.getCustomer());
        assertNotNull("Customer First Name is null", login_response.getCustomer().getFirstName());
        //assertNotNull("Customer Middle Name is null", login_response.getCustomer().getMiddleName());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
