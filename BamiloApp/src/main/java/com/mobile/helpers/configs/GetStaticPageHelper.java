package com.mobile.helpers.configs;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;

/**
 * Get a static page
 */
public class GetStaticPageHelper extends SuperBaseHelper {

    public static String TAG = GetStaticPageHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_STATIC_PAGE;
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_PRIORITARY;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getStaticPage);
    }

    public static Bundle createBundle(String key) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.KEY, key);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }
    
}
