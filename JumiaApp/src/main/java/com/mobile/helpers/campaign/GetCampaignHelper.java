package com.mobile.helpers.campaign;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.pojo.BaseResponse;
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

    private static final String CAMPAIGN_TAG = "slug";

    @Override
    public EventType getEventType() {
        return EventType.GET_CAMPAIGN_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCampaign);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        Campaign campaign = (Campaign) baseResponse.getMetadata().getData();
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, campaign);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String campaignId) {
        // Item data
        ContentValues values = new ContentValues();
        values.put(CAMPAIGN_TAG, campaignId);
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}
