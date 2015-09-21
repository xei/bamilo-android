package com.mobile.test;

import com.mobile.newFramework.objects.product.OfferList;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetProductOffersTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_OFFERS;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getProductOffers;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/sony-70-inch-kdl-70r550a-bravia-3d-internet-led-backlight-tv-85191.html";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("all_offers", "1");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());
        OfferList productOffers = (OfferList) response.getMetadata().getData();
        assertNotNull("Product Offers is null", productOffers);
        assertNotNull("Product Offers Total Offers is null", productOffers.getTotalOffers());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
