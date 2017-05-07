package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;

public class EditAddressTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: save_shipping_rg_position=24
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[first_name]=test
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: save_shipping_ct_position=0
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[fk_customer_address_region]=25
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[fk_customer_address_city]=11
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[last_name]=test
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[fk_customer_address_postcode]=
//                05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[address1]=test
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[additional_phone]=
//                05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[is_default_shipping]=1
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[id_customer_address]=
//                05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[city]=ABULE EGBA
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[address2]=
//                05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[phone]=1234567890
//        05-29 14:01:23.345  29429-29451/com.jumia.android.dev D/RestClientSingleton﹕ post: Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[is_default_billing]=0



        HashMap<String, String> data = new HashMap<>();
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[is_default_billing]", "0");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[phone]", "1234567890");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[address2]", "");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[city]", "ABULE EGBA");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[id_customer_address]", "");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[is_default_shipping]", "1");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[additional_phone]", "");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[address1]", "test");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[fk_customer_address_postcode]", "");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[last_name]", "test");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[fk_customer_address_city]", "11");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[fk_customer_address_region]", "25");
        data.put("Alice_Module_Mobapi_Form_Ext1m7_Customer_AddressForm[first_name]", "test");

        requestBundle = new RequestBundle.Builder()
                .setEndPoint("https://www.jumia.com.ng/mobapi/v1.7/customer/address/edit")
                .setCache(EventType.EDIT_ADDRESS_EVENT.cacheTime)
                .addQueryData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(requestBundle, this).execute(AigApiInterface.editAddress);
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
