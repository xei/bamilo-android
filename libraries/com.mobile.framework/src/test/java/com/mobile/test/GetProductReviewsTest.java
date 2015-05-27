package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.reviews.GetProductReviews;

import java.util.HashMap;

public class GetProductReviewsTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, String> data = new HashMap<>();
        data.put("rating", "1");
        data.put("per_page", "18");
        data.put("page", "1");
        requestBundle = new RequestBundle.Builder()
                .setUrl("http://www.jumia.com.ng/mobapi/v1.7/galaxy-s4-i9500-black-53281.html")
                .setCache(EventType.GET_PRODUCT_REVIEWS_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetProductReviews(IS_AUTOMATED_TEST, requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.success);
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        System.out.println("TEST ERROR: " + response.success);
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}
