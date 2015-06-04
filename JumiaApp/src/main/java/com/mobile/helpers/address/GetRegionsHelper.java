package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.TextUtils;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.AddressRegions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.address.GetRegions;

import com.mobile.framework.output.Print;

/**
 * Helper used to set the shipping address 
 * @author sergiopereira
 */
public class GetRegionsHelper extends SuperBaseHelper {
    
    private static String TAG = GetRegionsHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_REGIONS_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        String action = args.getString(Constants.BUNDLE_URL_KEY);
        if(TextUtils.isEmpty(action)) {
            action = mEventType.action;
        }
        return RemoteService.completeUri(Uri.parse(action)).toString();
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new GetRegions(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        AddressRegions regions = (AddressRegions) baseResponse.getMetadata().getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, regions);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }


            
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "REQUEST");
//        Bundle bundle = new Bundle();
//        String action = args.getString(Constants.BUNDLE_URL_KEY);
//        if(TextUtils.isEmpty(action)) action = EventType.GET_REGIONS_EVENT.action;
//        Log.d(TAG, "URL: " + action);
//        bundle.putString(Constants.BUNDLE_URL_KEY, action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGIONS_EVENT);
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
//        Log.d(TAG, "RESPONSE: " + jsonObject.toString());
//        try {
//            // Regions
//            ArrayList<AddressRegion> regions = new ArrayList<>();
//            // For each item
//            JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.JSON_DATA_TAG);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject json = jsonArray.getJSONObject(i);
//                // Save the region
//                regions.add(new AddressRegion(json));
//            }
//            // Save regions
//            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, regions);
//        } catch (JSONException e) {
//            Log.w(TAG, "PARSE EXCEPTION", e);
//            return parseErrorBundle(bundle);
//        }
//        Log.d(TAG, "PARSE WITH SUCCESS");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGIONS_EVENT);
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
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGIONS_EVENT);
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
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGIONS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
