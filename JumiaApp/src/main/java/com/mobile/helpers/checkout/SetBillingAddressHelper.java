/**
 * 
 */
package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.SetBillingAddress;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;

/**
 * Helper used to set the shipping address 
 * @author sergiopereira
 */
public class SetBillingAddressHelper extends SuperBaseHelper {
    
    private static String TAG = SetBillingAddressHelper.class.getSimpleName();
    
    public static final String FORM_CONTENT_VALUES = "content_values";

    @Override
    public EventType getEventType() {
        return EventType.SET_BILLING_ADDRESS_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new SetBillingAddress(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setBillingAddress);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);

        //TODO move to observable
        SetBillingAddress billing = (SetBillingAddress) baseResponse.getMetadata().getData();
        bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, billing.getOrderSummary());
        Print.i(TAG, "ORDER SUMMARY: " + billing.getOrderSummary().toString());
        // Get and set next step
        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextFragment(billing.getNextStep()));
    }

// TODO: Send the respective value
    // billingForm[billingAddressId]
    // billingForm[shippingAddressDifferent]
    // billingForm[shippingAddressId]
            
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
//        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutStep(jsonObject));
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
