package com.bamilo.android.appmodule.bamiloapp.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;

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
