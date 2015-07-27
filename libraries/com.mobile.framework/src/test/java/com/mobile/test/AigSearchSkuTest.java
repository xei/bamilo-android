package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;
import java.util.Map;

public class AigSearchSkuTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.SEARCH_PRODUCT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.searchSku;
    }

    @Override
    public String getUrl() {
        return "https://www.jumia.com.ng/mobapi/v1.7/catalog/detail/";
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
        CompleteProduct completeProduct = (CompleteProduct) response.getMetadata().getData();
        assertNotNull("Product is null",completeProduct);
        assertNotNull("Product Name is null",completeProduct.getName());

        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
