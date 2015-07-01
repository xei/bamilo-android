package com.mobile.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.objects.product.ProductReviewComment;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;

//import com.mobile.newFramework.requests.reviews.GetProductReviews;

public class GetReviewsTest extends BaseTestCase {

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
        Print.d("TEST REQUEST");
        //new GetProductReviews(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductReviews);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(BaseResponse response) {
        Print.d("TEST SUCCESS: " + response.hadSuccess());
        Print.d("############# REVIEWS #############");
        ProductRatingPage rating = (ProductRatingPage) response.getMetadata().getData();
        assertNotNull(rating);
        for (ProductReviewComment review : rating.getReviewComments()) {
            assertNotNull(review);
            Print.d(review.toString());
        }
        Print.d("######################################");
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
