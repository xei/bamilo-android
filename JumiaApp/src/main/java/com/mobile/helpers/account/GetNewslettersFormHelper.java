package com.mobile.helpers.account;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.session.GetNewsletterForm;
import com.mobile.newFramework.rest.RestUrlUtils;

/**
 * Helper used to get the form to edit an address
 */
public class GetNewslettersFormHelper extends SuperBaseHelper {
    
    private static String TAG = GetNewslettersFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_NEWSLETTERS_FORM_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        String url = EventType.GET_NEWSLETTERS_FORM_FALLBACK_EVENT.action;
        try {
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(mEventType.action);
            url = formData.getUrl();
        } catch (NullPointerException e) {
            Print.w(TAG, "FORM DATA IS NULL THEN GET FORM FALLBACK", e);
        }
        return RestUrlUtils.completeUri(Uri.parse(url)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new GetNewsletterForm(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getNewsletterForm);
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
//        Log.d(TAG, "REQUEST");
//        String url = FALL_BACK_EVENT_TYPE.action;
//        try {
//            url = JumiaApplication.INSTANCE.getFormDataRegistry().get(EVENT_TYPE.action).getUrl();
//        } catch (NullPointerException e) {
//            Log.w(TAG, "FORM DATA IS NULL THEN GET NEWSLETTER FORM FALLBACK", e);
//        }
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, url);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE JSON: " + jsonObject.toString());
//        try {
//            final ArrayList<Form> forms = new ArrayList<>();
//            JSONArray dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//            for (int i = 0; i < dataObject.length(); ++i) {
//                Form form = new Form();
//                JSONObject formObject = dataObject.getJSONObject(i);
//                if (!form.initialize(formObject))
//                    Log.e(TAG, "Error initializing the form using the data");
//                forms.add(form);
//            }
//            if (forms.size() > 0)
//                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
//        } catch (JSONException e) {
//            Log.d(TAG, "PARSE EXCEPTION: " , e);
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
    
}

