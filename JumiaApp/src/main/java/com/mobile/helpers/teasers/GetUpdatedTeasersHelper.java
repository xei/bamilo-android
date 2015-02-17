///**
// * 
// */
//package com.mobile.helpers.teasers;
//
//import java.util.ArrayList;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.mobile.framework.enums.RequestType;
//import com.mobile.framework.objects.Homepage;
//import com.mobile.framework.rest.RestClientSingleton;
//import com.mobile.framework.rest.RestConstants;
//import com.mobile.framework.utils.Constants;
//import com.mobile.framework.utils.EventType;
//import com.mobile.framework.utils.Utils;
//import com.mobile.helpers.BaseHelper;
//import android.os.Bundle;
//import android.text.TextUtils;
//import de.akquinet.android.androlog.Log;
//
///**
// * Helper used to check if is necessary update the teaser collection using MD5 from response
// * @author sergiopereira
// */
//public class GetUpdatedTeasersHelper extends BaseHelper {
//   
//    private static String TAG = GetUpdatedTeasersHelper.class.getSimpleName();
//    
//    public static final String OLD_MD5_KEY = "old_md5";
//    
//    public static final String MD5_KEY = "md5";
//       
//    private static final EventType EVENT_TYPE = EventType.GET_UPDATED_TEASERS_EVENT;
//    
//    private String mOldMd5 = null;
//    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "ON REQUEST");
//        // Get and save it old md5
//        mOldMd5 = args.getString(OLD_MD5_KEY);
//        // Create request
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE");
//        // Get new MD5
//        String md5 = jsonObject.optString(RestConstants.JSON_MD5_TAG);
//        Log.d(TAG, "MD5 VALUES: " + mOldMd5 + " " + md5);
//        // Validate if some md5 is empty
//        if(TextUtils.isEmpty(md5) || TextUtils.isEmpty(mOldMd5))
//            return parseResponseErrorBundle(bundle);
//        // MD5 are equal then discard response
//        if(!TextUtils.isEmpty(md5) && !TextUtils.isEmpty(mOldMd5) && mOldMd5.equals(md5))
//            return parseResponseErrorBundle(bundle);
//        // Remove the old request from cache
//        RestClientSingleton.sRestClientSingleton.moveEntryInCache(EVENT_TYPE.action, EventType.GET_TEASERS_EVENT.action);
//        // MD5 are different then parse response
//        try {
//            parseJson(jsonObject, bundle);
//            bundle.putString(MD5_KEY, md5);
//            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//        return bundle;
//    }
//    
//    /**
//     * Parse json and create the teaser collection
//     * @param jsonObject
//     * @param bundle
//     * @throws JSONException
//     * @author sergiopereira
//     */
//    private void parseJson(JSONObject jsonObject, Bundle bundle) throws JSONException{
//        JSONArray dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//        int dataArrayLength = dataArray.length();
//        if (dataArrayLength > 0) {
//            ArrayList<Homepage> homepageSpecifications = new ArrayList<Homepage>();
//            for (int i = 0; i < dataArrayLength; ++i) {
//                Homepage homepage = new Homepage();
//                homepage.initialize(dataArray.getJSONObject(i));
//                homepageSpecifications.add(homepage);
//            }
//            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, homepageSpecifications);
//            Log.i(TAG, "Teasers size: " + homepageSpecifications.size());
//        } else {
//            Log.e(TAG, "Teasers size: 0");
//        }
//    }
//    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON PARSE ERROR");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON RESPONSE ERROR");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//    
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
//}