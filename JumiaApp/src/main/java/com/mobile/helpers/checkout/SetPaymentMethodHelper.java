package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.SuperSetPaymentMethod;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.checkout.SetPaymentMethod;
import com.mobile.utils.CheckoutStepManager;

/**
 * Helper used to set the shipping address
 */
public class SetPaymentMethodHelper extends SuperBaseHelper {
    
    private static String TAG = SetPaymentMethodHelper.class.getSimpleName();
    
    public static final String FORM_CONTENT_VALUES = "content_values";

    @Override
    public EventType getEventType() {
        return EventType.SET_PAYMENT_METHOD_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new SetPaymentMethod(requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        SuperSetPaymentMethod responseData = (SuperSetPaymentMethod) baseResponse.getMetadata().getData();
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        // Get order summary from response
        bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, responseData.getOrderSummary());
        Print.i(TAG, "ORDER SUMMARY: " + responseData.getOrderSummary().toString());
         bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextFragment(responseData.getNextStep()));
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
//        Parcelable contentValues = args.getParcelable(FORM_CONTENT_VALUES);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "PARSE BUNDLE");
//        // Get and set next step
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutFragment(jsonObject));
//        // Get order summary from response
//        try {
//            OrderSummary orderSummary = new OrderSummary(jsonObject);
//            bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, orderSummary);
//            Log.i(TAG, "ORDER SUMMARY: " + orderSummary.toString());
//        } catch (NullPointerException | JSONException e) {
//            Log.w(TAG, "WARNING: PARSING ORDER RESPONSE", e);
//        }
//        // Return response
//        return bundle;
//    }
//
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
