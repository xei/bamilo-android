package com.bamilo.android.appmodule.bamiloapp.helpers.wishlist;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.cache.WishListCache;

import java.util.Map;

/**
 * Helper used to add an item to wish list.
 *
 * @author sergiopereira
 *
 */
public class AddToWishListHelper extends SuperBaseHelper {
    public static String ADD_TO_WISHLIST = "add_to_wishlist";
    private String mSku;

    @Override
    public EventType getEventType() {
        return EventType.ADD_PRODUCT_TO_WISH_LIST;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = super.getRequestData(args);
        mSku = data.get(RestConstants.SKU);
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addToWishList);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        // Add item to wish list cache
        WishListCache.add(mSku);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String sku) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
