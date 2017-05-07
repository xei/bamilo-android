/**
 * 
 */
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
