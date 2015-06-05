package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.GetProductOffers;

import java.util.HashMap;

public class GetProductOffersTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("all_offers", "1");
        requestBundle = new RequestBundle.Builder()
                .setUrl("http://integration-www.jumia.ug/mobapi/v1.7/gt-s7262-galaxy-star-plus-duo-dual-sim-4gb-hdd-512mb-ram-2mp-camera-black-25409.html")
                .setCache(EventType.GET_PRODUCT_OFFERS.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetProductOffers(requestBundle, this).execute();
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
