///**
// *
// */
//package com.mobile.helpers.checkout;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.Bundle;
//
//import com.mobile.framework.enums.RequestType;
//import com.mobile.framework.objects.OrderSummary;
//import com.mobile.framework.utils.Constants;
//import com.mobile.framework.utils.EventType;
//import com.mobile.framework.utils.Utils;
//import com.mobile.helpers.BaseHelper;
//import com.mobile.helpers.HelperPriorityConfiguration;
//import com.mobile.utils.CheckoutStepManager;
//
//import com.mobile.framework.output.Log;
//
///**
// * Helper used to ...
// * @author sergiopereira
// */
//public class GetMyOrderHelper extends BaseHelper {
//
//    private static String TAG = GetMyOrderHelper.class.getSimpleName();
//
//    private static final EventType EVENT_TYPE = EventType.GET_MY_ORDER_EVENT;
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "REQUEST");
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
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
//        Log.d(TAG, "PARSE BUNDLE: " + jsonObject.toString());
//        try {
//            // Get order
//            OrderSummary orderSummary = new OrderSummary(jsonObject);
//            // Get next step
//            bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutFragment(jsonObject));
//            // Set order
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, orderSummary);
//        } catch (JSONException e) {
//            Log.w(TAG, "ERROR ON PARSE: " + e.getMessage());
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
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
//}
