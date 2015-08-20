package com.mobile.test;

import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AigGetAddressesListTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_CUSTOMER_ADDRESSES_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getAddressesList;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/customer/getaddresslist/";
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
        Addresses address_list = (Addresses) response.getMetadata().getData();
        assertNotNull("List of Addresses is null",address_list.getAddresses());
        Address address;

        Iterator ite = address_list.getAddresses().entrySet().iterator();
        while (ite.hasNext()){
            Map.Entry pair = (Map.Entry) ite.next();
            address = (Address) pair.getValue();
            assertNotNull("Address is null",address);
            assertNotNull("Address First Name is null",address.getFirstName());
        }



        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
