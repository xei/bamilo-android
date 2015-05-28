/**
 * 
 */
package com.mobile.helpers.account;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified Manuel Silva
 */
public class GetChangePasswordHelper extends BaseHelper {
    
    private static String TAG = GetChangePasswordHelper.class.getSimpleName();
    
    public static final String CONTENT_VALUES = "contentValues";
    
    private static final EventType EVENT_TYPE = EventType.CHANGE_PASSWORD_EVENT;
    
    private ContentValues savedValues;
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        savedValues = args.getParcelable(CONTENT_VALUES);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, savedValues);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }

    /*Bom
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
    	Log.d(TAG, "parseResponseBundle GetTeasersHelper");
    	savedValues.remove( "Alice_Module_Customer_Model_PasswordForm[password2]" );
        JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(savedValues);
    	bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetChangePassHelper");
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
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}