/**
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.forms.Form;
import pt.rocket.forms.FormData;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.utils.JumiaApplication;
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

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.i(TAG, "code1 FormDataRegistry size is : "+JumiaApplication.INSTANCE.getFormDataRegistry().size());
        FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(
                EventType.GET_LOGIN_FORM_EVENT.action);
        String url = formData.getUrl();
        Log.i(TAG, "code1 getting login form : "+url);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        final ArrayList<Form> forms = new ArrayList<Form>();
        JSONArray dataObject;
        HashMap<String, FormData> formDataRegistry = new HashMap<String, FormData>();
        try {
            dataObject = jsonObject
                    .getJSONArray(RestConstants.JSON_DATA_TAG);

            for (int i = 0; i < dataObject.length(); ++i) {
                Form form = new Form();
                JSONObject formObject = dataObject.getJSONObject(i);
                if (!form.initialize(formObject)) {
                    Log.e(TAG,
                            "Error initializing the form using the data");
                }
                forms.add(form);
            }
            // formRegistry.put(action, forms);
            if (forms.size() > 0) {
                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_LOGIN_FORM_EVENT);
        return bundle;
    }
    
        @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetLoginFormHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_LOGIN_FORM_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_LOGIN_FORM_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
