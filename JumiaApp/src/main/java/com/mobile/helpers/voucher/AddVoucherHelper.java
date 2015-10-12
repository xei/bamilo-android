package com.mobile.helpers.voucher;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Set Voucher helper
 */
public class AddVoucherHelper extends SuperBaseHelper {

    private static String TAG = AddVoucherHelper.class.getSimpleName();

    public static final String VOUCHER_PARAM = "couponcode";

    @Override
    public EventType getEventType() {
        return EventType.ADD_VOUCHER;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.addVoucher);
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

}