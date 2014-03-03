/**
 * 
 */
package pt.rocket.helpers.address;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.AddressCity;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import pt.rocket.utils.JSONConstants;
import android.os.Bundle;
import android.util.Log;

/**
 * Helper used to set the shipping address 
 * @author sergiopereira
 */
public class GetCitiesHelper extends BaseHelper {
    
    private static String TAG = GetCitiesHelper.class.getSimpleName();
    
    public static String REGION_ID_TAG = "region_id";
    
    public static String CUSTOM_TAG = "custom_tag";
    
    private static final EventType type = EventType.GET_CITIES_EVENT;

    private String customTag;
    
            
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle bundle) {
        Log.d(TAG, "REQUEST");
        // Bundle bundle = new Bundle();
        customTag = bundle.getString(CUSTOM_TAG);
        // Get region
        int region = bundle.getInt(REGION_ID_TAG);
        // Get action
        String action = bundle.getString(Constants.BUNDLE_URL_KEY, type.action);        
        // Validate action
        if(action.contains("fk_customer_address_region")) action = action.replace("fk_customer_address_region", "" + region);
        else action += "?region=" + region; 
        
        Log.d(TAG, "URL: " + action);
        
        bundle.putString(Constants.BUNDLE_URL_KEY, action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "RESPONSE: " + jsonObject.toString());
        try {
            // Regions
            ArrayList<AddressCity> cities = new ArrayList<AddressCity>();
            // For each item
            JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.JSON_DATA_TAG);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                // Save the region
                cities.add(new AddressCity(json));
            }
            // Save regions
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, cities);
            bundle.putString(CUSTOM_TAG, customTag);
        } catch (JSONException e) {
            Log.w(TAG, "PARSE EXCEPTION", e);
            return parseErrorBundle(bundle);
        }
        Log.d(TAG, "PARSE WITH SUCCESS");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "PARSE ERROR BUNDLE");
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
        android.util.Log.d(TAG, "PARSE RESPONSE BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
