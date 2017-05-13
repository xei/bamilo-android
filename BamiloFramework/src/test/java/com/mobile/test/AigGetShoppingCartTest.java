package com.mobile.test;

import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigGetShoppingCartTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.GET_SHOPPING_CART_ITEMS_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.getShoppingCart;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/order/cartdata/";
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
        PurchaseEntity purchaseEntity = (PurchaseEntity) response.getMetadata().getData();
        assertNotNull("Cart is null", purchaseEntity);
        assertNotNull("Cart Count is null", purchaseEntity.getCartCount());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
