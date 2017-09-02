/**
 * 
 */
package com.mobile.helpers.voucher;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

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
