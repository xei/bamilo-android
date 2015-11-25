package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;

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
     * @param sku
     * @param richRelevanceHash
     * @return
     */
    public static Bundle createBundle(String sku, String richRelevanceHash) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        if(TextUtils.isNotEmpty(richRelevanceHash))
            values.put(RestConstants.JSON_RR_CLICK_REQUEST, richRelevanceHash);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }

}
