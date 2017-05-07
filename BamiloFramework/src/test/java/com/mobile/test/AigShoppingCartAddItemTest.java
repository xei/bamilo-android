package com.mobile.test;

import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.test.suites.AigMobApiNigeriaTestSuite;

import java.util.HashMap;
import java.util.Map;

public class AigShoppingCartAddItemTest extends AigTestCase {

    @Override
    public EventType getEventType() {
        return EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT;
    }

    @Override
    public String getAigInterfaceName() {
        return AigApiInterface.addItemShoppingCart;
    }

    @Override
    public String getUrl() {
        return AigMobApiNigeriaTestSuite.HOST+"/order/add/";
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("quantity", "1");
        data.put("sku", "SA948ELAB541NGAMZ-78965");
        data.put("p", "SA948ELAB541NGAMZ");
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
