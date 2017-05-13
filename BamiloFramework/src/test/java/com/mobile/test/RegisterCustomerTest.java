package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

import java.util.HashMap;

public class RegisterCustomerTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        HashMap<String, String> data = new HashMap<>();
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[gender]", "male");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[month]", "01");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[day]", "01");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[year]", "2010");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[newsletter_categories_subscribed][]", "7");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[last_name]", "qa");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[first_name]", "qa");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[password]", "123456");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[password2]", "123456");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_RegistrationForm[email]", "qawsedrftgyh@jumia.com");
        // __autologin_requested__=true
        requestBundle = new RequestBundle.Builder()
                .setEndPoint("https://www.jumia.com.ng/mobapi/v1.7/customer/create/")
                .setCache(EventType.REGISTER_ACCOUNT_EVENT.cacheTime)
                .addQueryData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(requestBundle, this).execute(AigApiInterface.registerCustomer);
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
