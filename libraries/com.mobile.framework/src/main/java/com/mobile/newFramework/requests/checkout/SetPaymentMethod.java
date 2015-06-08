package com.mobile.newFramework.requests.checkout;

import com.mobile.newFramework.objects.checkout.SuperSetPaymentMethod;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class SetPaymentMethod extends BaseRequest<SuperSetPaymentMethod> {

    public SetPaymentMethod(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.setPaymentMethod(mRequestBundle.getData(), this);
    }
}
