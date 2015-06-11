package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.configs.AigRestContract;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;
import java.util.Map;

/**
 * Get Product Reviews helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetProductReviewsHelper extends SuperBaseHelper {

    private static String TAG = GetProductReviewsHelper.class.getSimpleName();

    public static final String PRODUCT_URL = "productUrl";

    public static final String PER_PAGE = "per_page";

    public static final String PAGE = "page";

    public static final String TOTAL_PAGES = "totalPages";

    public static final String RATING_TYPE = "ratingType";

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_REVIEWS_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(PRODUCT_URL))).toString();
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        String ratingType = args.getBoolean(RATING_TYPE) ? AigRestContract.REST_PARAM_RATING : AigRestContract.REST_PARAM_SELLER_RATING;
        data.put(ratingType, "1");
        data.put(PER_PAGE, args.getString(PER_PAGE));
        data.put(PAGE, args.getString(PAGE));
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new GetProductReviews(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductReviews);
   }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        //
        ProductRatingPage productRatingPage = (ProductRatingPage) baseResponse.getMetadata().getData();
        //
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productRatingPage);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        mRequester.onRequestError(generateErrorBundle(baseResponse));
    }


//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        String ratingType = RestContract.REST_PARAM_RATING;
//        if(!args.getBoolean(RATING_TYPE))
//            ratingType = RestContract.REST_PARAM_SELLER_RATING;
//
//        Uri uri = Uri.parse(args.getString(PRODUCT_URL)).buildUpon()
//                .appendQueryParameter(ratingType, "1")
//                .appendQueryParameter(PER_PAGE, args.getString(PER_PAGE))
//                .appendQueryParameter(PAGE, args.getString(PAGE))
//                .build();
//
//        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle GetProductReviewsHelper");
//        ProductRatingPage rating = new ProductRatingPage();
//        try {
//            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
//            rating.initialize(dataObject);
//            JSONObject reviewsObject = dataObject.optJSONObject(RestConstants.JSON_REVIEWS_TAG);
//            if (reviewsObject != null) {
//                JSONObject paginationObject = reviewsObject.optJSONObject(RestConstants.JSON_ORDER_PAGINATION_TAG);
//                if (paginationObject != null) {
//                    int totalPages = paginationObject.optInt(RestConstants.JSON_ORDER_TOTAL_PAGES_TAG, -1);
//                    int currentPage = paginationObject.optInt(RestConstants.JSON_ORDER_CURRENT_PAGE_TAG, -1);
//                    if (currentPage != -1 && totalPages != -1) {
//                        bundle.putInt(TOTAL_PAGES, totalPages);
//                        bundle.putInt(PAGE, currentPage);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, rating);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetProductReviewsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
