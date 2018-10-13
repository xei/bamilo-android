package com.bamilo.android.appmodule.bamiloapp.helpers.cart;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

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
