package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.orders.GetOrdersList;

import java.util.HashMap;

public class GetOrdersListTest extends BaseTestCase {

    RequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        HashMap<String, String> data = new HashMap<>();
        data.put("per_page", "18");
        data.put("page", "1");

        requestBundle = new RequestBundle.Builder()
                .setUrl("https://www.jumia.com.ng/mobapi/v1.7/order/list/")
                .setCache(EventType.GET_MY_ORDERS_LIST_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetOrdersList(requestBundle, this).execute();
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
