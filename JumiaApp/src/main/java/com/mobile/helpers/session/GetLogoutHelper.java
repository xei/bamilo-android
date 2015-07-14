package com.mobile.helpers.session;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetLogoutHelper extends SuperBaseHelper {
    
    private static String TAG = GetLogoutHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.LOGOUT_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new LogoutCustomer(requestBundle, this).execute();
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.logoutCustomer);
    }


//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.LOGOUT_EVENT.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetLogoutHelper");
//
//        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.i(TAG,"parseResponseBundle");
//        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG,"parseResponseBundle");
//        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGOUT_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
