/**
 * @author Manuel Silva
 * 
 */
package com.mobile.newFramework.requests.cart;


import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class RemoveItemShoppingCart extends BaseRequest<ShoppingCart> {

    public RemoveItemShoppingCart(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.removeItemShoppingCart(mRequestBundle.getData(), this);
    }
}