package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.objects.addresses.AddressRegion;
import com.mobile.newFramework.objects.addresses.AddressRegions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;
import java.util.Map;

public class AigGetRegionsTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_REGIONS_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getRegions;
    }

    @Override
    public String getUrl() {
        return "https://www.jumia.com.ng/mobapi/v1.7/customer/address/regions/";
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
        AddressRegions addressRegions = (AddressRegions) response.getMetadata().getData();

        assertNotNull("Regions is null",addressRegions);
        for (AddressRegion region : addressRegions){
            assertNotNull("Regions is null", region);
            assertNotNull("Regions ID is null", region.getId());
            assertNotNull("Regions Name is null", region.getName());
        }
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}