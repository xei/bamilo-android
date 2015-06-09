package com.mobile.newFramework.requests.session;

import com.mobile.newFramework.objects.checkout.CheckoutStepSignUp;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class SignUpCustomer extends BaseRequest<CheckoutStepSignUp> {

    public SignUpCustomer(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.signUpCustomer(mRequestBundle.getData(), this);
    }
}
