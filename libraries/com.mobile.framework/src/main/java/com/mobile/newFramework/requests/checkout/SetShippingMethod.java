package com.mobile.newFramework.requests.checkout;

import com.mobile.newFramework.objects.checkout.SuperSetShippingMethod;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class SetShippingMethod extends BaseRequest<SuperSetShippingMethod> {

    public SetShippingMethod(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.setShippingMethod(mRequestBundle.getData(), this);
    }
}
