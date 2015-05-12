/**
 * 
 */
package com.mobile.helpers.session;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.Bundle;

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

import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetRegisterHelper extends BaseHelper {
    
    private static String TAG=GetRegisterHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.REGISTER_ACCOUNT_EVENT;
    
    public static final String REGISTER_CONTENT_VALUES = "contentValues";
    
    boolean saveCredentials = true;
    
    ContentValues contentValues;
    
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        contentValues = args.getParcelable(REGISTER_CONTENT_VALUES);
        contentValues.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.REGISTER_ACCOUNT_EVENT.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        if (saveCredentials) {
            Log.i(TAG, "code1 saving credentials : ");
            contentValues.remove("Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[newsletter_categories_subscribed][]");
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
            Log.i(TAG, "code1 hasCredentials : "+JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials());
        }
        try {
            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JumiaApplication.CUSTOMER = new Customer(jsonObject);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetRegisterHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}