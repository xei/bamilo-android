package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.statics.StaticTermsConditions;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;
import java.util.Map;

/**
 * Get Product Information helper
 */
public class GetTermsConditionsHelper extends SuperBaseHelper {

    private static String TAG = GetTermsConditionsHelper.class.getSimpleName();

    public static final String KEY = "key";

    @Override
    public EventType getEventType() {
        return EventType.GET_TERMS_EVENT;
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_NOT_PRIORITARY;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(KEY, "terms_mobile");
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new GetTermsConditions(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getTermsAndConditions);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        StaticTermsConditions termsConditions = (StaticTermsConditions) baseResponse.getMetadata().getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putString(Constants.BUNDLE_RESPONSE_KEY, termsConditions.getHtml());
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }




//
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_TERMS_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY,
//                HelperPriorityConfiguration.IS_NOT_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TERMS_EVENT);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }

//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle GetTermsConditionsHelper");
//        String text = "";
//        JSONArray dataArray;
//        try {
//            dataArray = jsonObject
//                    .getJSONArray(RestConstants.JSON_DATA_TAG);
//
//            if (dataArray.length() > 0) {
//                text = dataArray.getString(0);
//            }
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        bundle.putString(Constants.BUNDLE_RESPONSE_KEY, text);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TERMS_EVENT);
//        return bundle;
//    }
//
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetTermsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TERMS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TERMS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
    
}
