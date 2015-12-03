/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class ShoppingCartRemoveItemHelper extends SuperBaseHelper {

    private static final String TAG = ShoppingCartRemoveItemHelper.class.getSimpleName();

    // private static final EventType EVENT_TYPE = EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT;

    public static final String ITEM = "item";

//    public static final String UPDATE_CART = "update_cart";

    private static final boolean isToUpdateCart = true;

    @Override
    public EventType getEventType() {
        return EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.removeItemShoppingCart);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        //TODO move to observable
        // TODO: VALIDATE THIS ???
        // Don't continue if isToUpdateCart was set as false on generateRequestBundle()
        if (!isToUpdateCart) return;

        JumiaApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount(), cart.getAttributeSetIdList());
    }

    public static Bundle createBundle(String sku, boolean isToUpdateCart) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        isToUpdateCart = isToUpdateCart;
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }

}
