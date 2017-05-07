package com.mobile.test;

import com.mobile.newFramework.objects.home.HomePageObject;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetHomePageTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_HOME_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getHome;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/main/home/";
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

        HomePageObject home = (HomePageObject) response.getMetadata().getData();
        assertNotNull("Home is null", home);
        assertNotNull("Home Has Teasers is null", home.hasTeasers());
        if (home.hasTeasers()) {
            for (BaseTeaserGroupType teaser_group : home.getTeasers().values()) {
                assertNotNull("Tease Group is null", teaser_group);
                assertNotNull("Tease Group Type is null", teaser_group.getType());
            }
        }

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
