/**
 * 
 */
package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.reviews.GetReviewForm;

import com.mobile.framework.output.Print;

/**
 * Get Seller reviews dynamic form helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetSellerReviewFormHelper extends SuperBaseHelper {

    private static String TAG = GetSellerReviewFormHelper.class.getSimpleName();

    public static final String PRODUCT_URL = "productUrl";

    @Override
    public EventType getEventType() {
        return EventType.GET_FORM_SELLER_REVIEW_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return super.getRequestUrl(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new GetReviewForm(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        Form form = (Form) baseResponse.getMetadata().getData();
        form.sortForm(mEventType);
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

//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
//        return bundle;
//    }

//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle GetSellerReviewsHelper");
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
//                form.setEventType(EventType.GET_FORM_SELLER_REVIEW_EVENT);
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
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
//        return bundle;
//    }
    
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetRatingsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORM_SELLER_REVIEW_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
}
