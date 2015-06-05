/**
 * 
 */
package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.GetProductDetail;
import com.mobile.newFramework.rest.RestUrlUtils;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetProductHelper extends SuperBaseHelper {
    
    private static String TAG = GetProductHelper.class.getSimpleName();
    
    public static final String PRODUCT_URL = "productUrl";


    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(PRODUCT_URL))).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new GetProductDetail(requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        //
        CompleteProduct product = (CompleteProduct) baseResponse.getMetadata().getData();
        //
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        mRequester.onRequestError(generateErrorBundle(baseResponse));
    }


//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, args.getString(PRODUCT_URL));
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
//        return bundle;
//    }
    
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "parseResponseBundle GetProductsHelper");
//        CompleteProduct product = new CompleteProduct();
//        boolean status = product.initialize(jsonObject);
//        // Validate product initialization
//        if(!status) return parseErrorBundle(bundle);
//        // Return product
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
//
//        return bundle;
//    }

//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetTeasersHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
