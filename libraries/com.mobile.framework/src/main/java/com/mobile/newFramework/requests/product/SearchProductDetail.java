package com.mobile.newFramework.requests.product;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.product.CompleteProduct;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class SearchProductDetail extends BaseRequest<CompleteProduct> {

    public SearchProductDetail(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.searchProductDetail(mRequestBundle.getData(), this);
    }
}
