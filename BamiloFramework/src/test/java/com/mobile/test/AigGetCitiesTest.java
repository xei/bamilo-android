package com.mobile.test;

import com.mobile.newFramework.objects.addresses.AddressCities;
import com.mobile.newFramework.objects.addresses.FormListItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetCitiesTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_CITIES_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getCities;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/customer/getaddresscities/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("region", "25");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());

        AddressCities addressCities = (AddressCities) response.getMetadata().getData();

        assertNotNull("List of Cities is null", addressCities);
        for (FormListItem city : addressCities){
            assertNotNull("City is null", city);
            assertNotNull("City ID is null", city.getValue());
            assertNotNull("City Value is null", city.getLabel());
        }
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
