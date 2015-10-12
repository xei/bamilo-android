package com.mobile.helpers.products;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Product Reviews helper
 * 
 * @author alexandrapires: product and seller reviews
 */
public class GetReviewsHelper extends SuperBaseHelper {

    protected static String TAG = GetReviewsHelper.class.getSimpleName();

    public static final String SKU = GetProductHelper.SKU_TAG;

    public static final String PER_PAGE = "per_page";

    public static final String PAGE = "page";

    public static final String REST_PARAM_SELLER_RATING = "seller_rating";

    public static final String REST_PARAM_RATING = "rating";


    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_REVIEWS;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductReviews);
   }

}
