package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.objects.ProductRatingPage;
import com.mobile.newFramework.objects.ProductReviewComment;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.GetReviews;

import java.util.HashMap;

public class GetReviewsTest extends BaseTestCase {

    RequestBundle requestBundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
//        http://www.jumia.com.ng:80/mobapi/v1.7/cordless-desk-phone-dp710-black-139824.html?seller_rating=1&per_page=18&page=1
//        http://www.jumia.com.ng/mobapi/v1.7/galaxy-note-4-charcoal-black-138645.html?rating=1&per_page=18&page=1
        HashMap<String, String> data = new HashMap<>();
        //Case is Product Reviews
        data.put("rating", "1");
        //Case is Seller Reviews
//        data.put("seller_rating", "1");
        data.put("per_page", "18");
        data.put("page", "1");
        requestBundle = new RequestBundle.Builder()
                .setUrl(" http://www.jumia.com.ng/mobapi/v1.7/galaxy-note-4-charcoal-black-138645.html")
                .setCache(EventType.GET_PRODUCT_REVIEWS_EVENT.cacheTime)
                .setData(data)
                .build();
    }

    @SmallTest
    public void testRequest() {
        System.out.println("TEST REQUEST");
        new GetReviews(IS_AUTOMATED_TEST, requestBundle, this).execute();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        System.out.println("TEST SUCCESS: " + response.success);
        System.out.println("############# REVIEWS #############");
        ProductRatingPage reviews = (ProductRatingPage) response.metadata.getData();
        assertNotNull(reviews);
        for (ProductReviewComment review : reviews.getReviewComments()) {
            assertNotNull(review);
            System.out.println(review.toString());
        }
        System.out.println("######################################");
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
