/**
 * 
 */
package com.mobile.helpers.configs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.app.JumiaApplication;
import com.mobile.forms.FormData;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetInitFormHelper extends BaseHelper {

    private static String TAG=GetInitFormHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.INIT_FORMS;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.INIT_FORMS.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        

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
                JumiaApplication.INSTANCE.getFormDataRegistry().put(formData.getAction(), formData);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.INIT_FORMS);
//        Log.i("REQUEST", "event EVENT_TYPE response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetInitFormsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
