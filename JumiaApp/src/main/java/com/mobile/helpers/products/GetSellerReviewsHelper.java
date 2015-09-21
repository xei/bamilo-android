package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

//import com.mobile.newFramework.requests.reviews.GetSellerReviews;

/**
 * Get Seller reviews
 * 
 * @author Paulo Carvalho
 * 
 */

/* deprecated by alexandrapires: use GetReviewsHelper instead */

@Deprecated
public class GetSellerReviewsHelper extends SuperBaseHelper {

    private static String TAG = GetSellerReviewsHelper.class.getSimpleName();

    public static final String SELLER_RATING = "seller_rating";

    public static final String PER_PAGE = "per_page";

    public static final String PAGE = "page";

    public static final String PRODUCT_URL = "productUrl";

    @Override
    public EventType getEventType() {
        return EventType.GET_SELLER_REVIEWS;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(PRODUCT_URL))).toString();
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(SELLER_RATING, "1");
        data.put(PER_PAGE, args.getString(PER_PAGE));
        data.put(PAGE, args.getString(PAGE));
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        //new GetSellerReviews(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getSellerReviews);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        ProductRatingPage productRatingPage = (ProductRatingPage) baseResponse.getMetadata().getData();
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productRatingPage);
    }

    //    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        Uri uri = Uri.parse(args.getString(PRODUCT_URL)).buildUpon()
//                .appendQueryParameter(SELLER_RATING, "1")
//                .appendQueryParameter(PER_PAGE, args.getString(PER_PAGE))
//                .appendQueryParameter(PAGE, args.getString(PAGE))
//                .build();
//
//        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle GetSellerReviewsHelper");
//        ProductRatingPage rating = new ProductRatingPage();
//        try {
//            //Log.d("SELLER", "OBJECT:" + jsonObject.toString(4));
//            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
//            rating.initialize(dataObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, rating);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetSellerReviewsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetSellerReviewsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
    
    
}
