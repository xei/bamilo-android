/**
 * 
 */
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

/**
 * Get Product Offers
 * 
 * @author Paulo Carvalho
 * 
 */
public class GetProductOffersHelper extends SuperBaseHelper {

    protected static String TAG = GetProductOffersHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCT_OFFERS;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getProductOffers);
    }
    
    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String sku) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        values.put(RestConstants.ALL_OFFERS, true);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }
    
}
