/**
 * 
 */
package pt.rocket.helpers.configs;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified Manuel Silva
 */
public class GetFormsDatasetListHelper extends BaseHelper {
    
    private static String TAG = GetFormsDatasetListHelper.class.getSimpleName();
    
    public static final String URL = "url";
    public static final String KEY = "key";
    private String JSON_ID_TAG;
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        JSON_ID_TAG = args.getString(KEY);
        bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(URL));
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORMS_DATASET_LIST_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        // TODO Auto-generated method stub
    	Log.d(TAG, "parseResponseBundle GetTeasersHelper");
    	
        HashMap<String, String> values = new HashMap<String, String>();

        if (JSON_ID_TAG.substring(0, 3).equals("fk_")) {
            JSON_ID_TAG = JSON_ID_TAG.replace("fk_", "id_");
        }

        JSONArray dataObject;
        try {
            dataObject = jsonObject
                    .getJSONArray(RestConstants.JSON_DATA_TAG);
            
            String key = "";
            String value = "";

            for (int i = 0; i < dataObject.length(); i++) {
                JSONObject datasetObject = dataObject
                        .getJSONObject(i);

                key = String.valueOf(datasetObject.optInt(
                        JSON_ID_TAG, 0));
                value = datasetObject.optString(RestConstants.JSON_NAME_TAG);

                values.put(key, value);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    	bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, values);
        return bundle;
    }
    

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        android.util.Log.d("TRACK", "parseErrorBundle GetTeasersHelper");
     
        //FIXME next line is just for test porpouse, to delete
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetTeasersHelper");
        return bundle;
    }

}