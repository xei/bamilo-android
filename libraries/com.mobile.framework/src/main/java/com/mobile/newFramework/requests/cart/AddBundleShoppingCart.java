package com.mobile.newFramework.requests.cart;

import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class AddBundleShoppingCart extends BaseRequest<ShoppingCart> {

    public AddBundleShoppingCart(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.addBundleShoppingCart(mRequestBundle.getData(), this);
    }
}
