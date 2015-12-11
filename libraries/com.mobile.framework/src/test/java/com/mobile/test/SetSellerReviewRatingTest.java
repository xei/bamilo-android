package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;

public class SetSellerReviewRatingTest extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        HashMap<String, String> data = new HashMap<>();
        data.put("review[ratings][1]", "4");
//        data.put("review[ratings][2]", "4");
//        data.put("review[ratings][3]", "4");
//        data.put("review[sku]", "BL683ELADX3ENAFAMZ");
        data.put("review[name]", "test");
        data.put("review[title]", "test");
        data.put("review[comment]", "test");
        data.put("review[sellerId]", "128");

        requestBundle = new RequestBundle.Builder()
                .setEndPoint("http://alice-staging.jumia.com.ng/mobapi/v1.7/rating/addsellerreview/")
                .setCache(EventType.REVIEW_RATING_PRODUCT_EVENT.cacheTime)
                .addQueryData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        Print.d("TEST REQUEST");
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setSellerReview);
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
