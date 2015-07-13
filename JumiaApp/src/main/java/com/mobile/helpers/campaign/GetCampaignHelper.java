package com.mobile.helpers.campaign;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
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
    
    private static String TAG = GetCampaignHelper.class.getSimpleName();

    public static final String CAMPAIGN_TAG = "campaign_slug";


    @Override
    public EventType getEventType() {
        return EventType.GET_CAMPAIGN_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(mEventType.action)).toString();

    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(CAMPAIGN_TAG, args.getString(CAMPAIGN_TAG));
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new GetCampaign(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCampaign);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        Campaign campaign = (Campaign) baseResponse.getMetadata().getData();

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, campaign);
    }


//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "ON REQUEST");
//        // Create bundle to send
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(CAMPAIGN_ID));
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE BUNDLE: " + jsonObject.toString());
//        try {
//            // Get data
//            JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
//            // Create campaign
//            Campaign campaign = new Campaign(dataObject);
//            // Add to bundle
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, campaign);
//        } catch (JSONException e) {
//            Log.w(TAG, "ERROR ON PARSE JSON", e);
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE RESPONSE BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
