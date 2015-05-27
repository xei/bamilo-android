package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.SetProductRatingReview;

import java.util.HashMap;

public class SetReviewRatingTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Params
        // facebook_login=false
        // __autologin_requested__=true

        HashMap<String, String> data = new HashMap<>();

        data.put("ReviewForm[ratings][1]", "4");
        data.put("ReviewForm[ratings][2]", "4");
        data.put("ReviewForm[ratings][3]", "4");
        data.put("ReviewForm[name]", "test");
        data.put("ReviewForm[sku]", "BL683ELADX3ENAFAMZ");
        data.put("ReviewForm[title]", "test");
        data.put("ReviewForm[comment]", "test");
//        data.put("review[sellerId]", "28");

        requestBundle = new RequestBundle.Builder()
                .setUrl("http://alice-staging.jumia.com.ng/mobapi/v1.7/rating/addreview/")
                .setCache(EventType.REVIEW_RATING_PRODUCT_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new SetProductRatingReview(IS_AUTOMATED_TEST, requestBundle, this).execute();
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
