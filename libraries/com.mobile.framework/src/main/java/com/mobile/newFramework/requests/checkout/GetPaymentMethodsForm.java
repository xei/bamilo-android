package com.mobile.newFramework.requests.checkout;

import com.mobile.newFramework.objects.checkout.SuperGetPaymentMethodsForm;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

/**
 * Created by pcarvalho on 6/1/15.
 */
public class GetPaymentMethodsForm extends BaseRequest<SuperGetPaymentMethodsForm> {

    public GetPaymentMethodsForm(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getPaymentMethodsForm(this);
    }
}
