package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;

import java.util.HashMap;

public class SetReviewRatingTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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
                .setEndPoint("http://alice-staging.jumia.com.ng/mobapi/v1.7/rating/addreview/")
                .setCache(EventType.REVIEW_RATING_PRODUCT_EVENT.cacheTime)
                .addQueryData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setRatingReview);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

    @Override
    public void onRequestError(BaseResponse response) {
        Print.d("TEST ERROR: " + response.hadSuccess());
        // tests returned then countdown semaphore
        mCountDownLatch.countDown();
    }

}