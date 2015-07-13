package com.mobile.helpers.configs;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.FormsIndex;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper to get forms index Get forms index
 */
public class GetInitFormHelper extends SuperBaseHelper {

    private static String TAG = GetInitFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.INIT_FORMS;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return super.getRequestUrl(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getFormsIndex);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        FormsIndex formsIndex = (FormsIndex) baseResponse.getMetadata().getData();
        //TODO move to observable
        JumiaApplication.INSTANCE.setFormDataRegistry(formsIndex);
    }

//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.INIT_FORMS.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        return bundle;
//    }

//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//
//
//        JSONArray dataArray = null;
//        try {
//            dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//            int dataArrayLength = dataArray.length();
//            for (int i = 0; i < dataArrayLength; ++i) {
//                JSONObject formDataObject = dataArray.getJSONObject(i);
//                FormData formData = new FormData();
//                formData.initialize(formDataObject);
//                JumiaApplication.INSTANCE.getFormDataRegistry().put(formData.getAction(), formData);
//            }
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
//        return bundle;
//    }
    
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetInitFormsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.INIT_FORMS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
