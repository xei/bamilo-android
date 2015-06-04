/**
 * 
 */
package com.mobile.helpers.search;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.SearchProductDetail;

import java.util.HashMap;
import java.util.Map;

import com.mobile.framework.output.Print;

/**
 * Search a Product using sku
 * 
 * @author sergiopereira
 * 
 */
public class GetSearchProductHelper extends SuperBaseHelper {
    
    private static String TAG = GetSearchProductHelper.class.getSimpleName();
    
    //private static final EventType EVENT_TYPE = EventType.SEARCH_PRODUCT;
    
    public static final String SKU_TAG = "sku";

    @Override
    public EventType getEventType() {
        return EventType.SEARCH_PRODUCT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(SKU_TAG, args.getString(SKU_TAG));
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new SearchProductDetail(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        //
        CompleteProduct product = (CompleteProduct) baseResponse.getMetadata().getData();
        //
        Bundle bundle = super.generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        mRequester.onRequestError(generateErrorBundle(baseResponse));
    }


//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "DEEP LINK ON GENERATE REQUEST");
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        Uri uri = Uri.parse(EVENT_TYPE.action).buildUpon().appendQueryParameter(SKU_TAG, args.getString(SKU_TAG)).build();
//        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE BUNDLE");
//        CompleteProduct product = new CompleteProduct();
//        product.initialize(jsonObject);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);
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
//        Log.d(TAG, "ON PARSE ERROR BUNDLE");
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
//        Log.d(TAG, "ON PARSE RESPONSE ERROR BUNDLE");
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
