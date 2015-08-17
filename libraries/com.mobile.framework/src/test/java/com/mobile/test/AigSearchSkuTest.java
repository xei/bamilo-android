package com.mobile.test;

import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigSearchSkuTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_DETAIL;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.searchSku;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/catalog/detail/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("sku", "AP044ELABRH2NGAMZ");
        return data;
    }

    @Override
    public void testResponse(BaseResponse response) {
        Print.d("RESPONSE SUCCESS: " + response.hadSuccess());
        assertTrue("Success is true", response.hadSuccess());
        ProductComplete completeProduct = (ProductComplete) response.getMetadata().getData();
        assertNotNull("Product is null",completeProduct);
        assertNotNull("Product Name is null",completeProduct.getName());

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
