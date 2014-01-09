/**
 * 
 */
package pt.rocket.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.utils.JumiaApplication;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetLoginFormHelper extends BaseHelper {
    private static String TAG = GetLoginFormHelper.class.getSimpleName();

    public static final String LOGIN_CONTENT_VALUES = "contentValues";
    boolean saveCredentials = false;
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
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        android.util.Log.d("TRACK", "parseErrorBundle GetLoginHelper");
        // FIXME next line is just for test porpouse, to delete
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetLoginHelper");
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        if (saveCredentials) {
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
        }
        try {
            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, new Customer(jsonObject));
        return bundle;
    }
}
