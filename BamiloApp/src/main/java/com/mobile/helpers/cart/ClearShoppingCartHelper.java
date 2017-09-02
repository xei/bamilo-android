/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;

/**
 * This helper will Clean the Shopping Cart by sending an empty update to the cart.
 * We need also to send an empty SetVoucher request to end this process.
 * 
 * @author Manuel Silva
 * 
 */
public class ClearShoppingCartHelper extends SuperBaseHelper {
    
    @Override
    public EventType getEventType() {
        return EventType.CLEAR_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public boolean hasPriority() {
        return HelperPriorityConfiguration.IS_NOT_PRIORITARY;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.clearShoppingCart);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        BamiloApplication.INSTANCE.setCart(null);
    }

}
