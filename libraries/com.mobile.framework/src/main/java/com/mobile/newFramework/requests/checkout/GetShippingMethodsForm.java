package com.mobile.newFramework.requests.checkout;

import com.mobile.newFramework.objects.checkout.SuperGetShippingMethodsForm;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

/**
 * Created by pcarvalho on 6/1/15.
 */
public class GetShippingMethodsForm extends BaseRequest<SuperGetShippingMethodsForm> {

    public GetShippingMethodsForm(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getShippingMethodsForm(this);
    }
}
