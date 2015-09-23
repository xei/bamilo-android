/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.HashMap;
import java.util.Map;

/**
 * This helper will Clean the Shopping Cart by sending an empty update to the cart.
 * We need also to send an empty SetVoucher request to end this process.
 * 
 * @author Manuel Silva
 * 
 */
public class ClearShoppingCartHelper extends SuperBaseHelper {
    
    private static String TAG = ClearShoppingCartHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return new HashMap<>();
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_NOT_PRIORITARY;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.removeAllShoppingCart);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        PurchaseEntity cart = (PurchaseEntity) baseResponse.getMetadata().getData();

        //TODO move to observable
        JumiaApplication.INSTANCE.setCart(null);
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());

    }

}
