/**
 * 
 */
package com.mobile.helpers.voucher;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Set Voucher helper
 * 
 * @author Manuel Silva
 * 
 */
public class RemoveVoucherHelper extends SuperBaseHelper {

    private static final String TAG = RemoveVoucherHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.REMOVE_VOUCHER;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.removeVoucher);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getContentData();
        BamiloApplication.INSTANCE.setCart(cart);
    }

}
