package com.mobile.helpers.products;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.reviews.SetProductRatingReview;

import java.util.Map;

import com.mobile.framework.output.Print;

/**
 * Send Rating and/or Review to API
 * 
 * @author Paulo Carvalho
 * 
 */
public class RatingReviewProductHelper extends SuperBaseHelper {

    private static String TAG = RatingReviewProductHelper.class.getSimpleName();
    
    public static final String ACTION = "action";

    public static final String RATING_REVIEW_CONTENT_VALUES = "contentValues";

    @Override
    public EventType getEventType() {
        return EventType.REVIEW_RATING_PRODUCT_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RemoteService.completeUri(Uri.parse(args.getString(ACTION))).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new SetProductRatingReview(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        mRequester.onRequestComplete(generateSuccessBundle(baseResponse));
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        mRequester.onRequestError(generateErrorBundle(baseResponse));
    }


//
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        ContentValues values = args.getParcelable(RATING_REVIEW_CONTENT_VALUES);
//        String action = args.getString(ACTION);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, values);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "parseResponseBundle GetProductReviewsHelper");
//        try {
//            bundle.putString(Constants.BUNDLE_RESPONSE_KEY, jsonObject.getString("success"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetReviewProductHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REVIEW_RATING_PRODUCT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }

}
