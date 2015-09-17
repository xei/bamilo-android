//package com.mobile.helpers.checkout;
//
//import android.os.Bundle;
//
//import com.mobile.helpers.SuperBaseHelper;
//import com.mobile.newFramework.objects.checkout.SuperNativeCheckoutAvailability;
//import com.mobile.newFramework.pojo.BaseResponse;
//import com.mobile.newFramework.requests.BaseRequest;
//import com.mobile.newFramework.requests.RequestBundle;
//import com.mobile.newFramework.rest.interfaces.AigApiInterface;
//import com.mobile.newFramework.utils.Constants;
//import com.mobile.newFramework.utils.EventType;
//import com.mobile.newFramework.utils.output.Print;
//
///**
// * Helper used to verify if the native checkout is available
// * @author Manuel Silva
// */
//public class GetNativeCheckoutAvailableHelper extends SuperBaseHelper {
//
//    private static String TAG = GetNativeCheckoutAvailableHelper.class.getSimpleName();
//
//    @Override
//    public EventType getEventType() {
//        return EventType.NATIVE_CHECKOUT_AVAILABLE;
//    }
//
//    @Override
//    public void onRequest(RequestBundle requestBundle) {
////        new GetNativeCheckoutAvailable(requestBundle, this).execute();
//        new BaseRequest(requestBundle, this).execute(AigApiInterface.getNativeCheckoutAvailable);
//    }
//
//    @Override
//    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
//        super.createSuccessBundleParams(baseResponse, bundle);
//        SuperNativeCheckoutAvailability nativeCheckoutAvailability = (SuperNativeCheckoutAvailability) baseResponse.getMetadata().getData();
//        bundle.putBoolean(Constants.BUNDLE_RESPONSE_KEY,  nativeCheckoutAvailability.isAvailable());
//        Print.i(TAG, "Native Checkout is available: " + nativeCheckoutAvailability.isAvailable());
//    }
//
////    /*
////     * (non-Javadoc)
////     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
////     */
////    @Override
////    public Bundle generateRequestBundle(Bundle args) {
////        Log.d(TAG, "REQUEST");
////        Bundle bundle = new Bundle();
////
////        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
////        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
////        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
////        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
////        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
////        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
////        return bundle;
////    }
////
////    /*
////     * (non-Javadoc)
////     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
////     */
////    @Override
////    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
////        Log.d(TAG, "PARSE BUNDLE");
////        boolean isAvailable = false;
////        JSONObject dataObject;
////        try {
////
////            dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
////            if(dataObject.has(RestConstants.JSON_NATIVE_CHECKOUT_AVAILABLE) && dataObject.getString(RestConstants.JSON_NATIVE_CHECKOUT_AVAILABLE).equalsIgnoreCase("1")){
////                isAvailable = true;
////            }
////
////            if(isAvailable){
////                Log.i(TAG, "native checkout is available!");
////            } else {
////                Log.i(TAG, "native checkout is not available!"+jsonObject.toString());
////            }
////        } catch (JSONException e) {
////            Log.d(TAG, "PARSE JSON", e);
////            return parseErrorBundle(bundle);
////        }
////        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
////        bundle.putBoolean(Constants.BUNDLE_RESPONSE_KEY,  isAvailable);
////        return bundle;
////    }
////
////    /*
////     * (non-Javadoc)
////     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
////     */
////    @Override
////    public Bundle parseErrorBundle(Bundle bundle) {
////        Log.d(TAG, "PARSE ERROR BUNDLE");
////        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
////        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
////        return bundle;
////    }
////
////    /*
////     * (non-Javadoc)
////     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
////     */
////    @Override
////    public Bundle parseResponseErrorBundle(Bundle bundle) {
////        Log.d(TAG, "PARSE RESPONSE BUNDLE");
////        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
////        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
////        return bundle;
////    }
////
////    @Override
////    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
////        return parseResponseErrorBundle(bundle);
////    }
//}
