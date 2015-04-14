/**
 * 
 */
package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.ProductRatingPage;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Get Seller reviews
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetSellerReviewsHelper extends BaseHelper {

    private static String TAG = GetSellerReviewsHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_SELLER_REVIEWS;

    public static final String SELLER_RATING = "seller_rating";
    public static final String PER_PAGE = "per_page";
    public static final String PAGE = "page";
    public static final String PRODUCT_URL = "productUrl";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        Uri uri = Uri.parse(args.getString(PRODUCT_URL)).buildUpon()
                .appendQueryParameter(SELLER_RATING, "1")
                .appendQueryParameter(PER_PAGE, args.getString(PER_PAGE))
                .appendQueryParameter(PAGE, args.getString(PAGE))
                .build();

        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d("TRACK", "parseResponseBundle GetSellerReviewsHelper");
        ProductRatingPage rating = new ProductRatingPage();
        try {
            //Log.d("SELLER", "OBJECT:" + jsonObject.toString(4));
            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            rating.initialize(dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return parseErrorBundle(bundle);
        }

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, rating);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetSellerReviewsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetSellerReviewsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SELLER_REVIEWS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    
}
