/**
 * 
 */
package com.mobile.helpers.account;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Helper used to submit the form where an user subscribes to newsletter
 * 
 * @author Andre Lopes
 *
 */
@Deprecated
public class HomeNewslettersSignupHelper extends BaseHelper {
    
    private static String TAG = HomeNewslettersSignupHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.HOME_NEWSLETTERS_SIGNUP_FORM_EVENT;
    
    private static final EventType FALL_BACK_EVENT_TYPE = EventType.HOME_NEWSLETTERS_SIGNUP_FORM_FALLBACK_EVENT;

    public static final String FORM_CONTENT_VALUES = "form_content_values";

    public static final String NEWSLETTER_SIGNUP_URL = "url";
    
    private Parcelable contentValues;

    /* (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        contentValues = args.getParcelable(FORM_CONTENT_VALUES);

        // set url based on response from GetHomeNewslettersSignupFormHelper
        String url = args.getString(NEWSLETTER_SIGNUP_URL);
        if (TextUtils.isEmpty(url)) {
            url = FALL_BACK_EVENT_TYPE.action;
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }

    /* (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE: " + jsonObject);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        return bundle;
    }

    /* (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE RESPONSE BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }

}
