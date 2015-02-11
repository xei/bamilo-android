package com.mobile.helpers.session;
///**
// * 
// */
//package com.mobile.helpers.session;
//
//import org.json.JSONObject;
//
//import com.mobile.framework.enums.RequestType;
//import com.mobile.framework.utils.Constants;
//import com.mobile.framework.utils.EventType;
//import com.mobile.framework.utils.Utils;
//import com.mobile.helpers.BaseHelper;
//import android.os.Bundle;
//import de.akquinet.android.androlog.Log;
//
///**
// * Example helper
// * 
// * @author Manuel Silva
// * 
// */
//public class GetStoreLoginHelper extends BaseHelper {
//
//    private static String TAG = GetStoreLoginHelper.class.getSimpleName();
//
//    private static final EventType EVENT_TYPE = EventType.STORE_LOGIN;
//
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.STORE_LOGIN.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.STORE_LOGIN);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "parseResponseBundle");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.STORE_LOGIN);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetStoreLoginHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.STORE_LOGIN);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.STORE_LOGIN);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//}