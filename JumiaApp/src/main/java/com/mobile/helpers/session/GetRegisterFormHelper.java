/**
 * 
 */
package com.mobile.helpers.session;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.forms.Form;
import com.mobile.forms.FormData;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetRegisterFormHelper extends BaseHelper {
    
    private static String TAG = GetRegisterFormHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_REGISTRATION_FORM_EVENT;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        try {
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(EventType.GET_REGISTRATION_FORM_EVENT.action);
            String url = formData.getUrl();
            bundle.putString(Constants.BUNDLE_URL_KEY, url);
        } catch (NullPointerException e) {
            Log.w(TAG, "FORM DATA IS NULL THEN GET LOGIN FORM FALLBACK", e);
            bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_REGISTRATION_FORM_FALLBACK_EVENT.action);
        }
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        final ArrayList<Form> forms = new ArrayList<>();
        JSONArray dataObject;
        //HashMap<String, FormData> formDataRegistry = new HashMap<String, FormData>();
        try {
            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);

            for (int i = 0; i < dataObject.length(); ++i) {
                Form form = new Form();
                form.setEventType(EventType.GET_REGISTRATION_FORM_EVENT);
                JSONObject formObject = dataObject.getJSONObject(i);
                if (!form.initialize(formObject)) {
                    Log.e(TAG, "Error initializing the form using the data");
                }
                forms.add(form);
            }
            // formRegistry.put(action, forms);
            if (forms.size() > 0) {
                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetRegisterFormHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
