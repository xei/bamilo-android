/**
 * 
 */
package com.mobile.helpers.session;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CustomerUtils;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.utils.CheckoutStepManager;
import android.content.ContentValues;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetLoginHelper extends BaseHelper {
    
    private static String TAG = GetLoginFormHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.LOGIN_EVENT;

    public static final String LOGIN_CONTENT_VALUES = "contentValues";
    boolean saveCredentials = true;
    ContentValues contentValues;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG);
        contentValues = args.getParcelable(LOGIN_CONTENT_VALUES);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.LOGIN_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        if (saveCredentials) {
            Log.i(TAG, "code1 saving credentials : ");
            contentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, false);
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
            Log.i(TAG, "code1 hasCredentials : "+JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials());
        }
        
        JSONObject jsonUser = null;
        try {
            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        JumiaApplication.CUSTOMER = new Customer(jsonUser); 
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER );
        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutStep(jsonObject));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetLoginHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}