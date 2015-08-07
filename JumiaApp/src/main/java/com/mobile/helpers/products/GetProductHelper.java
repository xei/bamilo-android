/**
 * 
 */
package com.mobile.helpers.products;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

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

    public static final String SKU_TAG = "sku";

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_EVENT;
    }

//    @Override
//    protected String getRequestUrl(Bundle args) {
//        return RestUrlUtils.completeUri(Uri.parse(args.getString(PRODUCT_URL))).toString();
//    }


    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> requestData = super.getRequestData(args);
        if(requestData == null){
            requestData = new HashMap<>();
        }

        if(args.containsKey(SKU_TAG)){
            requestData.put(SKU_TAG,args.getString(SKU_TAG));
        }

        return requestData;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductDetail);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        CompleteProduct product = (CompleteProduct) baseResponse.getMetadata().getData();
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, product);
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
