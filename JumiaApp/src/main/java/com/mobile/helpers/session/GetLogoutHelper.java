/**
 * 
 */
package com.mobile.helpers.session;

import org.json.JSONObject;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;

import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetLogoutHelper extends BaseHelper {
    
    private static String TAG = GetLogoutHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.LOGOUT_EVENT;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.LOGOUT_EVENT.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetLogoutHelper");
        
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.i(TAG,"parseResponseBundle");
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG,"parseResponseBundle");
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
        //bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.LOGOUT_EVENT);
//        Log.i("REQUEST", "event EVENT_TYPE response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
