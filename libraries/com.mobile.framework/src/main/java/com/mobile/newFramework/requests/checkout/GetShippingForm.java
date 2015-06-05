package com.mobile.newFramework.requests.checkout;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.checkout.SuperGetShippingMethodsForm;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetShippingForm extends BaseRequest<SuperGetShippingMethodsForm> {

    public GetShippingForm(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getShippingMethodsForm(this);
    }

}
