package com.mobile.test;

import com.mobile.service.objects.campaign.Campaign;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetCampaignTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_CAMPAIGN_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getCampaign;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/campaign/get/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("campaign_slug", "deals-of-the-day");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());

        Campaign campaign = (Campaign) response.getMetadata().getData();

        assertNotNull("Campaign is null",campaign);
        assertNotNull("Campaign Count is null",campaign.getCount());
        assertNotNull("Campaign Name is null",campaign.getName());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
