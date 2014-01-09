/**
 * 
 */
package pt.rocket.helpers;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.forms.FormData;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;

import android.os.Bundle;
import android.util.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetInitFormHelper extends BaseHelper {
    private static String TAG=GetInitFormHelper.class.getSimpleName();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.INIT_FORMS.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        // TODO Auto-generated method stub
    	android.util.Log.d("TRACK", "parseErrorBundle GetLoginHelper");
    	//FIXME next line is just for test porpouse, to delete
    	bundle.putString(Constants.BUNDLE_URL_KEY, " GetLoginHelper");
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        HashMap<String, FormData> formDataRegistry = new HashMap<String, FormData>();

        JSONArray dataArray = null;
        try {
            dataArray = jsonObject
                    .getJSONArray(RestConstants.JSON_DATA_TAG);
            int dataArrayLength = dataArray.length();
            for (int i = 0; i < dataArrayLength; ++i) {
                JSONObject formDataObject = dataArray
                        .getJSONObject(i);
                FormData formData = new FormData();
                formData.initialize(formDataObject);
                formDataRegistry.put(formData.getAction(), formData);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Log.i(TAG,"parseResponseBundle");
        return bundle;
    }
}
