package com.mobile.helpers.session;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;

/**
 * Helper to get the forgot password form
 */
public class GetForgotPasswordFormHelper extends SuperBaseHelper {

    private static String TAG = GetForgotPasswordFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_FORGET_PASSWORD_FORM_EVENT;
    }



    @Override
    protected String getRequestUrl(Bundle args) {
        String url = EventType.GET_FORGET_PASSWORD_FORM_FALLBACK_EVENT.action;
        try {
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(mEventType.action);
            url = formData.getUrl();
        } catch (NullPointerException e) {
            Print.w(TAG, "FORM DATA IS NULL THEN GET LOGIN FORM FALLBACK", e);
        }
        return RestUrlUtils.completeUri(Uri.parse(url)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new GetForgotPasswordForm(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getForgotPasswordForm);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        Form form = (Form) baseResponse.getMetadata().getData();
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }


//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "ON REQUEST");
//
//        // Fall back
//        String url = FALL_BACK_EVENT_TYPE.action;
//        try {
//            // Get form
//            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(EVENT_TYPE.action);
//            url = formData.getUrl();
//        } catch (NullPointerException e) {
//            Log.w(TAG, "FORM DATA IS NULL THEN FORGET_PASSWORD_FORM FALLBACK", e);
//        }
//
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, url);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE");
//
//        final ArrayList<Form> forms = new ArrayList<>();
//        JSONArray dataObject;
//        try {
//            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//
//            for (int i = 0; i < dataObject.length(); ++i) {
//                Form form = new Form();
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
//            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORGET_PASSWORD_FORM_EVENT);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON PARSE ERROR");
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
//        Log.d(TAG, "ON PARSE RESPONSE ERROR");
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
