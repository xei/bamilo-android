package com.mobile.test;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetImageResolutionsTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_RESOLUTIONS;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getImageResolutions;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/main/imageresolutions/";
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



        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
