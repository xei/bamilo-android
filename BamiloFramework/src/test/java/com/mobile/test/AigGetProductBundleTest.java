package com.mobile.test;

import com.mobile.service.objects.product.BundleList;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

//import com.mobile.service.requests.product.GetProductBundle;

public class AigGetProductBundleTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_BUNDLE;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getProductBundle;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/catalog/bundle/sku/SA948ELAB541NGAMZ";
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

        BundleList productBundle = (BundleList) response.getMetadata().getData();
        assertNotNull("Product Bundle is null", productBundle);
        assertNotNull("Product Bundle ID is null", productBundle.getBundleId());
        //assertNotNull("Product Budle Name is null", productBundle.getBundleName());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}