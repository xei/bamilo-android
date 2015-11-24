package com.mobile.helpers.campaign;

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
 * Helper used to get a campaign
 * @author sergiopereira
 */
public class GetCampaignHelper extends SuperBaseHelper {
    
    protected static String TAG = GetCampaignHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_CAMPAIGN_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCampaign);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String id) {
        // Item data
        ContentValues values = new ContentValues();
        values.put(RestConstants.SLUG, id);
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }
}
