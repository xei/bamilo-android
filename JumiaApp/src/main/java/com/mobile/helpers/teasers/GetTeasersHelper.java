///**
// *
// */
//package com.mobile.helpers.teasers;
//
//import android.os.Bundle;
//
//import com.mobile.framework.enums.RequestType;
//import com.mobile.framework.objects.Homepage;
//import com.mobile.framework.rest.RestConstants;
//import com.mobile.framework.utils.Constants;
//import com.mobile.framework.utils.EventType;
//import com.mobile.framework.utils.Utils;
//import com.mobile.helpers.BaseHelper;
//import com.mobile.helpers.HelperPriorityConfiguration;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//import de.akquinet.android.androlog.Log;
//
///**
// * Example helper
// *
// * @author Guilherme Silva
// * @modified sergiopereira
// */
//public class GetTeasersHelper extends BaseHelper {
//
//    private static String TAG = GetTeasersHelper.class.getSimpleName();
//
//    private static final EventType EVENT_TYPE = EventType.GET_TEASERS_EVENT;
//
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_TEASERS_EVENT.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "parseResponseBundle GetTeasersHelper");
//        try {
//            JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//            int dataArrayLength = dataArray.length();
//            if (dataArrayLength > 0) {
//                int defaultHomePage = 0;
//                ArrayList<Homepage> homepageSpecifications = new ArrayList<>();
//                for (int i = 0; i < dataArrayLength; ++i) {
//                    Homepage homepage = new Homepage();
//                    homepage.initialize(dataArray.getJSONObject(i));
//                    // Validate if is the default home page
//                    if (homepage.isDefault()) {
//                        defaultHomePage = i;
//                    }
//                    homepageSpecifications.add(homepage);
//                }
//                bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, homepageSpecifications);
//                bundle.putInt(RestConstants.JSON_HOMEPAGE_DEFAULT_TAG, defaultHomePage);
//                // Log.i(TAG, "Teasers size: " + homepageSpecifications.size());
//            } else {
//                Log.e(TAG, "Teasers size: 0");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetTeasersHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_TEASERS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
//}
