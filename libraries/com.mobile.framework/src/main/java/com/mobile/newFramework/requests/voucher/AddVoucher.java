package com.mobile.newFramework.requests.voucher;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.Voucher;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class AddVoucher extends BaseRequest<Voucher> {

    public AddVoucher(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.addVoucher(mRequestBundle.getData(), this);
    }

}