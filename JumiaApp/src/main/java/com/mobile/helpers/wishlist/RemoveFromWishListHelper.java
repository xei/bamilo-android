package com.mobile.helpers.wishlist;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.cache.WishListCache;

import java.util.Map;

/**
 * Helper used to remove item from wish list
 *
 * @author sergiopereira
 *
 */
public class RemoveFromWishListHelper extends SuperBaseHelper {

    private String mSku;

    @Override
    public EventType getEventType() {
        return EventType.REMOVE_PRODUCT_FROM_WISH_LIST;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = super.getRequestData(args);
        mSku = data.get(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
        return data;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.removeFromWishList);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        // Remove item from wish list cache
        WishListCache.remove(mSku);
    }

    /**
     * Method used to create the request bundle.
     */
    public static Bundle createBundle(String sku) {
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
