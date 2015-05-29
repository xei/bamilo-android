/**
 * 
 */
package com.mobile.helpers.session;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.session.GetRegisterForm;

import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetRegisterFormHelper extends SuperBaseHelper {
    
    private static String TAG = GetRegisterFormHelper.class.getSimpleName();
    
    //private static final EventType EVENT_TYPE = EventType.GET_REGISTRATION_FORM_EVENT;

    @Override
    public EventType getEventType() {
        return EventType.GET_REGISTRATION_FORM_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        String url = EventType.GET_REGISTRATION_FORM_FALLBACK_EVENT.action;
        try {
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(mEventType.action);
            url = formData.getUrl();
        } catch (NullPointerException e) {
            Log.w(TAG, "FORM DATA IS NULL THEN GET LOGIN FORM FALLBACK", e);
        }
        return RemoteService.completeUri(Uri.parse(url)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new GetRegisterForm(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.success);
        Form form = (Form) baseResponse.metadata.getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.message);
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }



//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        try {
//            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(EventType.GET_REGISTRATION_FORM_EVENT.action);
//            String url = formData.getUrl();
//            bundle.putString(Constants.BUNDLE_URL_KEY, url);
//        } catch (NullPointerException e) {
//            Log.w(TAG, "FORM DATA IS NULL THEN GET LOGIN FORM FALLBACK", e);
//            bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_REGISTRATION_FORM_FALLBACK_EVENT.action);
//        }
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        return bundle;
//    }

//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        final ArrayList<Form> forms = new ArrayList<>();
//        JSONArray dataObject;
//        //HashMap<String, FormData> formDataRegistry = new HashMap<String, FormData>();
//        try {
//            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//
//            for (int i = 0; i < dataObject.length(); ++i) {
//                Form form = new Form();
//                form.setEventType(EventType.GET_REGISTRATION_FORM_EVENT);
//                JSONObject formObject = dataObject.getJSONObject(i);
//                if (!form.initialize(formObject)) {
//                    Log.e(TAG, "Error initializing the form using the data");
//                }
//                forms.add(form);
//            }
//            // formRegistry.put(action, forms);
//            if (forms.size() > 0) {
//                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
//        return bundle;
//    }
    
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetRegisterFormHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_REGISTRATION_FORM_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
