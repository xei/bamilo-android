package com.mobile.helpers.campaign;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper used to get a campaign
 * @author sergiopereira
 */
public class GetCampaignHelper extends SuperBaseHelper {
    
    protected static String TAG = GetCampaignHelper.class.getSimpleName();

    public static final String CAMPAIGN_TAG = "slug";


    @Override
    public EventType getEventType() {
        return EventType.GET_CAMPAIGN_EVENT;
    }


    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(CAMPAIGN_TAG, args.getString(CAMPAIGN_TAG));
        return data;
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

}
