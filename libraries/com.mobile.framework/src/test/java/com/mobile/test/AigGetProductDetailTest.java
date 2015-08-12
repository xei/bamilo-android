package com.mobile.test;

import com.mobile.newFramework.objects.product.NewProductComplete;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetProductDetailTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_DETAIL;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getProductDetail;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/hero-3-tripod-mounts-black-205562.html";
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

        NewProductComplete completeProduct = (NewProductComplete) response.getMetadata().getData();
        assertNotNull("Product is null", completeProduct);
        assertNotNull("Product Simple is null", completeProduct.getSimples());
        assertNotNull("Product has seller is null", completeProduct.hasSeller());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
