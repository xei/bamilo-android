package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.SuperGetPaymentMethodsForm;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

/**
 * Helper used to get the payment methods
 */
public class GetPaymentMethodsHelper extends SuperBaseHelper {

    private static String TAG = GetPaymentMethodsHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_PAYMENT_METHODS_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new GetPaymentMethodsForm(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getPaymentMethodsForm);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);

        // Create bundle
        SuperGetPaymentMethodsForm responseData = (SuperGetPaymentMethodsForm) baseResponse.getMetadata().getData();
        bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, responseData.getOrderSummary());
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, responseData.getForm());

        //TODO move to observable
        JumiaApplication.INSTANCE.setPaymentMethodForm(null);
        JumiaApplication.setPaymentsInfoList(responseData.getForm().getFieldKeyMap().get("payment_method").paymentInfoList);
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "REQUEST");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "PARSE BUNDLE");
//        try {
//            // Get shipping methods
//            JSONObject formJSON = jsonObject.getJSONObject("paymentMethodForm");
//            Log.d(TAG, "FORM JSON: " + formJSON.toString());
//            Form form = new Form();
//            if (!form.initialize(formJSON))
//                Log.e(TAG, "Error initializing the form using the data");
//            OrderSummary orderSummary = new OrderSummary(jsonObject);
//            bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, orderSummary);
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);
//        } catch (JSONException e) {
//            Log.d(TAG, "PARSE EXCEPTION: ", e);
//            return parseErrorBundle(bundle);
//        }
//        Log.i(TAG, "PARSE JSON: SUCCESS");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
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
//     *
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
