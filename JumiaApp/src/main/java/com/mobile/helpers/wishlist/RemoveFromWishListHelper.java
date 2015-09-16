package com.mobile.helpers.wishlist;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to remove item from wish list
 *
 * @author sergiopereira
 *
 */
public class RemoveFromWishListHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.REMOVE_PRODUCT_FROM_WISH_LIST;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.removeFromWishList);
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
