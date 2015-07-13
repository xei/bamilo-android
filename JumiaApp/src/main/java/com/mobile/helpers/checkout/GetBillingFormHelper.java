/**
 * 
 */
package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.objects.checkout.SuperGetBillingForm;
import com.mobile.newFramework.objects.orders.OrderSummary;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;


/**
 * Helper used to get all customer addresses 
 * @author sergiopereira
 *
 */
public class GetBillingFormHelper extends SuperBaseHelper {
    
    private static String TAG = GetBillingFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_BILLING_FORM_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new GetBillingAddressForm(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getBillingAddressForm);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        SuperGetBillingForm billingForm = (SuperGetBillingForm)baseResponse.getMetadata().getData();
        // Create form
        Form form = billingForm.getForm();
        // Create addresses
        Addresses addresses = billingForm.getAddresses();
        // Get order summary
        OrderSummary orderSummary = billingForm.getOrderSummary();
        // Create bundle

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, addresses);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, form);
        bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, orderSummary);
    }

    //    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "GENERATE BUNDLE");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
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
//        try {
//            // Get json object
//            JSONObject jsonForm = jsonObject.getJSONObject(RestConstants.JSON_BILLING_FORM_TAG);
//            JSONObject jsonList = jsonObject.getJSONObject(RestConstants.JSON_CUSTOMER_TAG).getJSONObject(RestConstants.JSON_ADDRESS_LIST_TAG);
//            // Create form
//            Form form = new Form();
//            form.initialize(jsonForm);
//            // Create addresses
//            Addresses addresses = new Addresses(jsonList);
//            // Get order summary
//            OrderSummary orderSummary = new OrderSummary(jsonObject);
//            // Add to bundle
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, addresses);
//            bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, form);
//            bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, orderSummary);
//            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        } catch (Exception e) {
//            Log.d(TAG, "PARSE EXCEPTION:", e);
//            return parseErrorBundle(bundle);
//        }
//        Log.i(TAG, "PARSE JSON: SUCCESS");
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
//        Log.d(TAG, "PARSE ERROR RESPONSE");
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