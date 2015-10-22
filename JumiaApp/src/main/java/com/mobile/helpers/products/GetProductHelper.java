/**
 * 
 */
package com.mobile.helpers.products;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetProductHelper extends SuperBaseHelper {
    
    protected static String TAG = GetProductHelper.class.getSimpleName();

    public static final String SKU_TAG = "sku";

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_DETAIL;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductDetail);
    }

    public static Bundle createBundle(String sku) {
        ContentValues values = new ContentValues();
        values.put(GetProductHelper.SKU_TAG, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
