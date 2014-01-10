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
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified Manuel Silva
 */
public class GetCustomerHelper extends BaseHelper {
    
    private static String TAG = GetCustomerHelper.class.getSimpleName();
    
    public static final String CUSTOMER_CONTENT_VALUES = "contentValues";
    
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_CUSTOMER.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
    	Log.d(TAG, "parseResponseBundle GetTeasersHelper");
        try {
            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, new Customer(jsonObject));
        return bundle;
    }
    

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d("TRACK", "parseErrorBundle GetCustomerHelper");
    
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCustomerHelper");
        return bundle;
    }

}