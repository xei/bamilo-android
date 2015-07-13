package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Reviews dynamic form helper.
 */
public class GetReviewFormHelper extends SuperBaseHelper {

    private static String TAG = GetReviewFormHelper.class.getSimpleName();

    public static final String PRODUCT_URL = "productUrl";

    @Override
    public EventType getEventType() {
        return EventType.GET_FORM_REVIEW_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return super.getRequestUrl(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new GetReviewForm(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getReviewForm);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        Form form = (Form) baseResponse.getMetadata().getData();
        form.sortForm(mEventType);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);
    }
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_FORM_REVIEW_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_REVIEW_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle GetReviewsHelper");
//
//        final ArrayList<Form> forms = new ArrayList<>();
//        JSONArray dataObject;
//
//        try {
//            dataObject = jsonObject
//                    .getJSONArray(RestConstants.JSON_DATA_TAG);
//
//            for (int i = 0; i < dataObject.length(); ++i) {
//                Form form = new Form();
//                form.setEventType(EventType.GET_FORM_REVIEW_EVENT);
//                JSONObject formObject = dataObject.getJSONObject(i);
//                if (!form.initialize(formObject)) {
//                    Log.e(TAG,
//                            "Error initializing the form using the data");
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
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_REVIEW_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetRatingsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_REVIEW_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_REVIEW_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
}
