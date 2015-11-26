package com.mobile.helpers.teasers;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Rich Relevance info
 * used on Home Page
 *
 * @author Paulo Carvalho
 */
public class GetRichRelevanceHelper extends SuperBaseHelper {

    protected static String TAG = GetRichRelevanceHelper.class.getSimpleName();

    public static final String URL = Constants.BUNDLE_URL_KEY;

    @Override
    public EventType getEventType() {
        return EventType.GET_RICH_RELEVANCE_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getRichRelevance);
    }

    public static Bundle createBundle(String richRelevantHash) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.JSON_REQUEST, richRelevantHash);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }


}
