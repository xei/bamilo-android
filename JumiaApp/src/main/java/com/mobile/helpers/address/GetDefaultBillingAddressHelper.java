package com.mobile.helpers.address;
///**
// * 
// */
//package com.mobile.helpers.address;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.mobile.framework.enums.RequestType;
//import com.mobile.framework.objects.Address;
//import com.mobile.framework.utils.Constants;
//import com.mobile.framework.utils.EventType;
//import com.mobile.framework.utils.Utils;
//import com.mobile.helpers.BaseHelper;
//import com.mobile.helpers.HelperPriorityConfiguration;
//import android.os.Bundle;
//import de.akquinet.android.androlog.Log;
//
///**
// * Helper used to ...
// * @author sergiopereira
// */
//public class GetDefaultBillingAddressHelper extends BaseHelper {
//    
//    private static String TAG = GetDefaultBillingAddressHelper.class.getSimpleName();
//    
//    private static final EventType EVENT_TYPE = EventType.GET_DEFAULT_BILLING_ADDRESS_EVENT;
//            
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "REQUEST");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
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
//        Log.d(TAG, "PARSE BUNDLE: " + jsonObject.toString());
//        try {
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, new Address(jsonObject.getJSONObject("data")));
//        } catch (JSONException e) {
//            Log.w(TAG, "ERROR ON PARSE: " + e.getMessage());
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
//}
