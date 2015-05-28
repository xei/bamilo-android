/**
 * 
 */
package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.rest.RestContract;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.newFramework.objects.product.ProductRatingPage;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Get Product Reviews helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetProductReviewsHelper extends BaseHelper {

    private static String TAG = GetProductReviewsHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_PRODUCT_REVIEWS_EVENT;

    public static final String PRODUCT_URL = "productUrl";
    public static final String PER_PAGE = "per_page";
    public static final String PAGE = "page";
    public static final String TOTAL_PAGES = "totalPages";
    public static final String RATING_TYPE = "ratingType";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        String ratingType = RestContract.REST_PARAM_RATING;
        if(!args.getBoolean(RATING_TYPE))
            ratingType = RestContract.REST_PARAM_SELLER_RATING;
        
        Uri uri = Uri.parse(args.getString(PRODUCT_URL)).buildUpon()
                .appendQueryParameter(ratingType, "1")
                .appendQueryParameter(PER_PAGE, args.getString(PER_PAGE))
                .appendQueryParameter(PAGE, args.getString(PAGE))
                .build();

        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d("TRACK", "parseResponseBundle GetProductReviewsHelper");
        ProductRatingPage rating = new ProductRatingPage();
        try {
            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            rating.initialize(dataObject);
            JSONObject reviewsObject = dataObject.optJSONObject(RestConstants.JSON_REVIEWS_TAG);
            if (reviewsObject != null) {
                JSONObject paginationObject = reviewsObject.optJSONObject(RestConstants.JSON_ORDER_PAGINATION_TAG);
                if (paginationObject != null) {
                    int totalPages = paginationObject.optInt(RestConstants.JSON_ORDER_TOTAL_PAGES_TAG, -1);
                    int currentPage = paginationObject.optInt(RestConstants.JSON_ORDER_CURRENT_PAGE_TAG, -1);
                    if (currentPage != -1 && totalPages != -1) {
                        bundle.putInt(TOTAL_PAGES, totalPages);
                        bundle.putInt(PAGE, currentPage);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, rating);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetProductReviewsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_REVIEWS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
