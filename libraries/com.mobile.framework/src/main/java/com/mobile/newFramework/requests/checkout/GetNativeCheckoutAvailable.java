package com.mobile.newFramework.requests.checkout;

import com.mobile.newFramework.objects.checkout.SuperNativeCheckoutAvailability;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class GetNativeCheckoutAvailable extends BaseRequest<SuperNativeCheckoutAvailability> {

    public GetNativeCheckoutAvailable(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getNativeCheckoutAvailable(this);
    }
}
