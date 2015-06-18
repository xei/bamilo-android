package com.mobile.newFramework.requests.address;

import com.mobile.newFramework.objects.checkout.SuperSetBillingAddress;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class SetBillingAddress extends BaseRequest<SuperSetBillingAddress> {

    public SetBillingAddress(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.setBillingAddress(mRequestBundle.getData(), this);
    }
}
