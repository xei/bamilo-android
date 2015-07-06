package com.mobile.test;


import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;

/**
 * Created by rsoares on 5/27/15.
 */
public class ShoppingCartAddBundleTest extends BaseTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("product-simple-selector[2]", "HP017ELAC0DKNAFAMZ-158431");
        data.put("product-item-selector[1]" , "HP017ELAC93ZNAFAMZ");
        data.put("product-item-selector[0]" , "AC008ELAC7XCNAFAMZ");
        data.put("product-item-selector[2]" ,"HP017ELAC0DKNAFAMZ");
        data.put("product-simple-selector[0]" , "AC008ELAC7XCNAFAMZ-171230");
        data.put("product-simple-selector[1]" , "HP017ELAC93ZNAFAMZ-173013");
        data.put("bundleId" , "1058");
        requestBundle = new RequestBundle.Builder()
                .setUrl("https://www.jumia.ci/mobapi/v1.7/order/addBundle/")
                .setCache(EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addBundleShoppingCart);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        super.onRequestComplete(response);
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        super.onRequestError(response);
        mCountDownLatch.countDown();
    }
}
