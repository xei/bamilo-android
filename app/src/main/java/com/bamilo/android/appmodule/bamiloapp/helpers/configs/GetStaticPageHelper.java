package com.bamilo.android.appmodule.bamiloapp.helpers.configs;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.HelperPriorityConfiguration;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;

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
