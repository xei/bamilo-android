/**
 * 
 */
package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.ProductBundle;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.GetProductBundle;

import com.mobile.framework.output.Print;

/**
 * Get Product Bundle Information helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetProductBundleHelper extends SuperBaseHelper {

    private static String TAG = GetProductBundleHelper.class.getSimpleName();

    public static final String PRODUCT_SKU = "productSku";


    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_BUNDLE;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RemoteService.completeUri(Uri.parse(EventType.GET_PRODUCT_BUNDLE.action + args.getString(PRODUCT_SKU))).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new GetProductBundle(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        //
        ProductBundle productBundle = (ProductBundle) baseResponse.getMetadata().getData();
        //
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productBundle);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, baseResponse.getError().getErrorCode());
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        mRequester.onRequestError(bundle);
    }

//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_PRODUCT_BUNDLE.action + args.getString(PRODUCT_SKU));
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "parseResponseBundle GetProductsBundleHelper");
//        // Return bundle
//        ProductBundle productBundle = new ProductBundle();
//        productBundle.initialize(jsonObject);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productBundle);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetBundleHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_BUNDLE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
