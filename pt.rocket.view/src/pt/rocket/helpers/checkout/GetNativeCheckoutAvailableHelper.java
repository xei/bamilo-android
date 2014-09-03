package pt.rocket.helpers.checkout;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Helper used to verify if the native checkout is available
 * @author Manuel Silva
 */
public class GetNativeCheckoutAvailableHelper extends BaseHelper {
    
    private static String TAG = GetNativeCheckoutAvailableHelper.class.getSimpleName();

    private static final EventType type = EventType.NATIVE_CHECKOUT_AVAILABLE;
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();    
        
        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE");
        boolean isAvailable = false;
        JSONObject dataObject;
        try {
            
            dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            if(dataObject.has(RestConstants.JSON_NATIVE_CHECKOUT_AVAILABLE) && dataObject.getString(RestConstants.JSON_NATIVE_CHECKOUT_AVAILABLE).equalsIgnoreCase("1")){
                isAvailable = true;
            }
          
            if(isAvailable){
                Log.i(TAG, "native checkout is available!");
            } else {
                Log.i(TAG, "native checkout is not available!"+jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.d(TAG, "PARSE JSON", e);
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_RESPONSE_KEY,  isAvailable);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        de.akquinet.android.androlog.Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        de.akquinet.android.androlog.Log.d(TAG, "PARSE RESPONSE BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
