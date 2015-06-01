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
import com.mobile.newFramework.requests.session.GetSignUpForm;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class GetSignUpFormHelper extends SuperBaseHelper {
    
    private static String TAG = GetSignUpFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_SIGNUP_FORM_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        String url = EventType.GET_SIGNUP_FORM_FALLBACK_EVENT.action;
        try {
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(mEventType.action);
            url = formData.getUrl();
        } catch (NullPointerException e) {
            Log.w(TAG, "FORM DATA IS NULL THEN GET FORM FALLBACK", e);
        }
        return RemoteService.completeUri(Uri.parse(url)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new GetSignUpForm(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
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

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "REQUEST");
//        String url = fallback.action;
//        try {
//            url = JumiaApplication.INSTANCE.getFormDataRegistry().get(EVENT_TYPE.action).getUrl();
//        } catch (NullPointerException e) {
//            Log.w(TAG, "GET SIGN UP FORM FROM FALLBACK");
//        }
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, url);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "PARSE BUNDLE");
//        final ArrayList<Form> forms = new ArrayList<>();
//        JSONArray dataObject;
//        try {
//            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//            for (int i = 0; i < dataObject.length(); ++i) {
//                Form form = new Form();
//                JSONObject formObject = dataObject.getJSONObject(i);
//                if (!form.initialize(formObject)) Log.e(TAG, "Error initializing the form using the data");
//                forms.add(form);
//            }
//            if (CollectionUtils.isNotEmpty(forms)) {
//                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
//            }
//        } catch (JSONException e) {
//            Log.d(TAG, "PARSE JSON", e);
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE RESPONSE BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
