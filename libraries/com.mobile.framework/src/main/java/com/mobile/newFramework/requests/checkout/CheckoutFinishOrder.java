package com.mobile.newFramework.requests.checkout;

import com.mobile.newFramework.objects.checkout.SuperCheckoutFinish;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class CheckoutFinishOrder extends BaseRequest<SuperCheckoutFinish> {

    public CheckoutFinishOrder(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.checkoutFinish(mRequestBundle.getData(), this);
    }

}
