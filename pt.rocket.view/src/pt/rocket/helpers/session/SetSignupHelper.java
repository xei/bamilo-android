/**
 * 
 */
package pt.rocket.helpers.session;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class SetSignupHelper extends BaseHelper {
    
    private static String TAG = SetSignupHelper.class.getSimpleName();

    public static final String FORM_CONTENT_VALUES = "content_values";
    
    private static final EventType type = EventType.SET_SIGNUP_EVENT;

    boolean saveCredentials = true;
    
    ContentValues contentValues;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        contentValues = args.getParcelable(FORM_CONTENT_VALUES);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE: " + jsonObject);        
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
}
