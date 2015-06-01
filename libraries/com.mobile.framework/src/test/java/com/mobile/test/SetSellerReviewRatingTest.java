package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.reviews.SetSellerRatingReview;

import java.util.HashMap;

public class SetSellerReviewRatingTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        HashMap<String, String> data = new HashMap<>();
// TODO this test should be temporary and for test porpouse only.
// TODO This helper should be merged with SetReviewRatingTest where th data is passed as parameters and the url should be the action on the form
        data.put("review[ratings][1]", "4");
//        data.put("review[ratings][2]", "4");
//        data.put("review[ratings][3]", "4");
//        data.put("review[sku]", "BL683ELADX3ENAFAMZ");
        data.put("review[name]", "test");
        data.put("review[title]", "test");
        data.put("review[comment]", "test");
        data.put("review[sellerId]", "128");

        requestBundle = new RequestBundle.Builder()
                .setUrl("http://alice-staging.jumia.com.ng/mobapi/v1.7/rating/addsellerreview/")
                .setCache(EventType.REVIEW_RATING_PRODUCT_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new SetSellerRatingReview(IS_AUTOMATED_TEST, requestBundle, this).execute();
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
