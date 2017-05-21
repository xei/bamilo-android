package com.mobile.test;

import com.mobile.service.objects.configs.ApiInformation;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetApiInformationTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_API_INFO;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getApiInformation;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/main/md5/";
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
        ApiInformation api_info = (ApiInformation) response.getMetadata().getData();
        assertNotNull("API Info is null", api_info);
        assertNotNull("API Info Version is null", api_info.getVersionInfo());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
