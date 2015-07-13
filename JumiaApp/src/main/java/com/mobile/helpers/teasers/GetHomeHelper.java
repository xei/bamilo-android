package com.mobile.helpers.teasers;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.home.HomePageObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

/**
 * Helper used to get the home page
 * @author sergiopereira
 */
public class GetHomeHelper extends SuperBaseHelper {

    public static String TAG = GetHomeHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        // Request
//        new GetHomePage(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getHome);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_HOME_EVENT;
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        HomePageObject homePageObject = (HomePageObject) baseResponse.getMetadata().getData();
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, homePageObject);
    }

//    /**
//     * Create request
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.i(TAG, "ON REQUEST HOME");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(type.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        return bundle;
//    }
//
//    /**
//     * Parse the response
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE RESPONSE BUNDLE");
//        try {
//            // Get home
//            HomePageObject newHomePageObject = new HomePageObject();
//            newHomePageObject.initialize(jsonObject);
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, newHomePageObject);
//        } catch (JSONException e) {
//            Log.w(TAG, "WARNING: JE ON PARSE RESPONSE", e);
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
//        return bundle;
//    }
//
//    /**
//     * Error on parsing json
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.i(TAG, "ON PARSE ERROR");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /**
//     * Error on response
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.i(TAG, "ON ERROR RESPONSE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /**
//     * Parent error on parsing json
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
