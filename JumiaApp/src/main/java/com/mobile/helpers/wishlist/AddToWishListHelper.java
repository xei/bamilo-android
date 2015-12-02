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
        mSku = data.get(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
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
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
