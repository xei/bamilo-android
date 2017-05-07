package com.mobile.helpers.cart;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.BamiloApplication;
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
public class ShoppingCartChangeItemQuantityHelper extends SuperBaseHelper {
    
    private static final String TAG = ShoppingCartChangeItemQuantityHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.updateQuantityShoppingCart);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        BamiloApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getContentData();
        BamiloApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Track the new cart value
        TrackerDelegator.trackAddToCart(cart);
    }

    public static Bundle createBundle(String sku, int quantity) {
        Bundle bundle = new Bundle();
        ContentValues values = new ContentValues();
        values.put(RestConstants.SKU, sku);
        values.put(RestConstants.QUANTITY, quantity);
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
