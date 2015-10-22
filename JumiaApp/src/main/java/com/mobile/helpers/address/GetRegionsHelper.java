package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to set the shipping address 
 * @author sergiopereira
 */
public class GetRegionsHelper extends SuperBaseHelper {
    
    public static String TAG = GetRegionsHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_REGIONS_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(Constants.BUNDLE_URL_KEY))).toString();
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getRegions);
    }

    public static Bundle createBundle(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        return bundle;
    }

}
