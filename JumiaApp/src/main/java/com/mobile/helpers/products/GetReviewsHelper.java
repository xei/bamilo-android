package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.RestConstants;
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

    public static final int REVIEWS_PER_PAGE = 18;


    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_REVIEWS;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        //test
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductReviews);
   }

    public static Bundle createBundle(String sku,int pageNumber) {

        //all these parameters are placed between {} in request
        ContentValues values = new ContentValues();

        values.put(GetReviewsHelper.SKU, sku);
        values.put(GetReviewsHelper.REST_PARAM_RATING, true);
        values.put(GetReviewsHelper.PAGE, pageNumber);
        values.put(GetReviewsHelper.PER_PAGE, REVIEWS_PER_PAGE);


        Bundle bundle = new Bundle();
        bundle.putParcelable(RestConstants.PARAM_1, values); //parameters map

        return bundle;
    }

}
