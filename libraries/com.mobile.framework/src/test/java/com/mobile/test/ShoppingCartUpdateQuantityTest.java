package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.cart.UpdateQuantityShoppingCart;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;

public class ShoppingCartUpdateQuantityTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("qty_SA948ELAAJLTNAFAMZ", "2");
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://integration-www.jumia.ug/mobapi/v1.7/order/cartchange/")
                .setCache(EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new UpdateQuantityShoppingCart(requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        System.out.println("TEST ERROR: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}
