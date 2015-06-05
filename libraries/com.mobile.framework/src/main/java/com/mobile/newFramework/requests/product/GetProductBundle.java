package com.mobile.newFramework.requests.product;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.product.ProductBundle;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetProductBundle extends BaseRequest<ProductBundle> {

    public GetProductBundle(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getProductBundle(this);
    }
}
