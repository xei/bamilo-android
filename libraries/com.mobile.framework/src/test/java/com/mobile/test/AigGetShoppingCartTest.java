package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
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
        ShoppingCart shoppingCart = (ShoppingCart) response.getMetadata().getData();
        assertNotNull("Cart is null", shoppingCart);
        assertNotNull("Cart Count is null", shoppingCart.getCartCount());
        //assertFalse("Success is false", response.hadSuccess());
        //Assert.fail("Success is false");
    }
}
