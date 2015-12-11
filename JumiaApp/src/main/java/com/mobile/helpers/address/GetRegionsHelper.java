package com.mobile.helpers.address;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

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
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getRegions);
    }

    public static Bundle createBundle(String endpoint) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(endpoint));
        return bundle;
    }

}
