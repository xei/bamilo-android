package com.mobile.helpers.products;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Send Rating and/or Review to API
 * 
 * @author Paulo Carvalho
 * 
 */
public class RatingReviewProductHelper extends SuperBaseHelper {

    public static final String ACTION = "action";

    @Override
    public EventType getEventType() {
        return EventType.REVIEW_RATING_PRODUCT_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected String getEndPoint(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(ACTION))).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setRatingReview);
    }

    public static Bundle createBundle(String action, ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putString(RatingReviewProductHelper.ACTION, "/" + action);
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }


}
