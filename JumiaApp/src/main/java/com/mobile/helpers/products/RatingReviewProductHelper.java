package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Send Rating and/or Review to API
 * 
 * @author Paulo Carvalho
 * 
 */
public class RatingReviewProductHelper extends BaseHelper {

    private static String TAG = RatingReviewProductHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.REVIEW_RATING_PRODUCT_EVENT;
    
    public static final String ACTION = "action";
    public static final String RATING_REVIEW_CONTENT_VALUES = "contentValues";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        ContentValues values = args.getParcelable(RATING_REVIEW_CONTENT_VALUES);
        String action = args.getString(ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, values);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "parseResponseBundle GetProductReviewsHelper");
        try {
            bundle.putString(Constants.BUNDLE_RESPONSE_KEY, jsonObject.getString("success"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetReviewProductHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

}
