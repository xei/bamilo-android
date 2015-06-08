package com.mobile.newFramework.requests.cart;

import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class AddMultipleItemsShoppingCart extends BaseRequest<ShoppingCart> {

    public AddMultipleItemsShoppingCart(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.addMultipleItemsShoppingCart(mRequestBundle.getData(), this);
    }
}