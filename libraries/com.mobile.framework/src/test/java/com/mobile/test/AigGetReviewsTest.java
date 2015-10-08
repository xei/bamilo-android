package com.mobile.test;

import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

//import com.mobile.newFramework.requests.reviews.GetProductReviews;

public class AigGetReviewsTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_REVIEWS;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getProductReviews;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/galaxy-note-4-charcoal-black-138645.html";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("per_page", "18");
        data.put("page", "1");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());

        ProductRatingPage productRatingPage = (ProductRatingPage) response.getMetadata().getData();
        assertNotNull("Rating Page is null", productRatingPage);
        assertNotNull("Rating Page Average is null", productRatingPage.getAverage());
        assertNotNull("Rating Page Product Name is null", productRatingPage.getProductName());
        assertNotNull("Rating Page Product Sku is null", productRatingPage.getProductSku());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
