package com.mobile.helpers.products;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Product Reviews helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */

/* deprecated by alexandrapires: use GetReviewsHelper instead */

@Deprecated
public class GetProductReviewsHelper extends SuperBaseHelper {

    protected static String TAG = GetProductReviewsHelper.class.getSimpleName();

    public static final String SKU = GetProductHelper.SKU_TAG;

    public static final String PER_PAGE = "per_page";

    public static final String PAGE = "page";

    public static final String REST_PARAM_SELLER_RATING = "seller_rating";


    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_REVIEWS;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductReviews);
   }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        ProductRatingPage productRatingPage = (ProductRatingPage) baseResponse.getMetadata().getData();
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productRatingPage);
    }

}
