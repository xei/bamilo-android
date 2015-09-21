package com.mobile.test;

import com.mobile.newFramework.objects.statics.StaticPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetShopInShopTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_API_INFO;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getShopInShop;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/main/getstatic/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("key", "voucher-fireworks");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());

        StaticPage staticPage = (StaticPage) response.getMetadata().getData();
        assertNotNull("Static Page is null", staticPage);
        assertNotNull("Static Page Has Featured Boxes is null", staticPage.hasFeaturedBoxes());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
