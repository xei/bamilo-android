/**
 * 
 */
package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.ProductOffers;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.product.GetProductOffers;
import com.mobile.newFramework.rest.RestUrlUtils;

import java.util.HashMap;
import java.util.Map;

import com.mobile.framework.output.Print;

/**
 * Get Product Offers
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetProductOffersHelper extends SuperBaseHelper {

    private static String TAG = GetProductOffersHelper.class.getSimpleName();

    public static final String ALL_OFFERS = "all_offers";

    public static final String PRODUCT_URL = "productUrl";


    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_OFFERS;
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
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(ALL_OFFERS, "1");
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new GetProductOffers(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        //
        ProductOffers productOffers = (ProductOffers) baseResponse.getMetadata().getData();
        //
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productOffers);
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
//        Uri uri = Uri.parse(args.getString(PRODUCT_URL)).buildUpon().appendQueryParameter(ALL_OFFERS, "1").build();
//        bundle.putString(Constants.BUNDLE_URL_KEY, uri.toString());
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle GetProductOffersHelper");
//        ProductOffers productOffers = new ProductOffers(jsonObject);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, productOffers);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetProductOffersHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetProductOffersHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCT_OFFERS);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
    
    
}
