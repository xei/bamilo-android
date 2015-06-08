package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.ValidateProducts;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;

public class ValidateProductsTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("products[0]", "HP246ELAE4ZWNAFAMZ");
        data.put("products[1]", "DE168ELACU1NNAFAMZ");
        data.put("products[2]", "HP246ELADB9QNAFAMZ");
        data.put("products[3]", "LE842ELAC3ZVNGAMZ");

        requestBundle = new RequestBundle.Builder()
                .setUrl(" http://www.jumia.com.ng/mobapi/v1.7/catalog/validate/")
                .setCache(EventType.VALIDATE_PRODUCTS.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new ValidateProducts(requestBundle, this).execute();
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
