package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetProductHelper extends SuperBaseHelper {
    
    protected static String TAG = GetProductHelper.class.getSimpleName();

    public static final String SKU_TAG = RestConstants.SKU;

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_DETAIL;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductDetail);
    }

    /**
     * Method specific for constructing bundle
     */
    public static Bundle createBundle(String sku, String richRelevanceHash) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        if(TextUtils.isNotEmpty(richRelevanceHash)) {
            values.put(RestConstants.REQ, richRelevanceHash);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }

}
