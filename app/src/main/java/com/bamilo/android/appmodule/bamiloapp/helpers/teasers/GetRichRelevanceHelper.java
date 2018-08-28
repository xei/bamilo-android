package com.bamilo.android.appmodule.bamiloapp.helpers.teasers;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;

/**
 * Get Rich Relevance info
 * used on Home Page
 *
 * @author Paulo Carvalho
 */
public class GetRichRelevanceHelper extends SuperBaseHelper {

    protected static String TAG = GetRichRelevanceHelper.class.getSimpleName();

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
        values.put(RestConstants.REQ, richRelevantHash);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }


}
