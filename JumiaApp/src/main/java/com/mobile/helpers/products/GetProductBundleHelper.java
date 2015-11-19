/**
 * 
 */
package com.mobile.helpers.products;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Get Product Bundle Information helper
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetProductBundleHelper extends SuperBaseHelper {

    protected static String TAG = GetProductBundleHelper.class.getSimpleName();

    public static final String PRODUCT_SKU = "productSku";


    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_BUNDLE;
    }

    @Override
    protected String getRequestUrl(android.os.Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(EventType.GET_PRODUCT_BUNDLE.action + args.getString(PRODUCT_SKU))).toString();
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_NOT_PRIORITARY;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductBundle);
    }


    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String sku) {
        // Item data
        Bundle bundle = new Bundle();
        bundle.putString(GetProductBundleHelper.PRODUCT_SKU, sku);
        return bundle;
    }

}
