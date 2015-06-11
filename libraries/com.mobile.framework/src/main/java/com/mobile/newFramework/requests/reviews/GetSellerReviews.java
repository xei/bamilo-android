package com.mobile.newFramework.requests.reviews;

import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class GetSellerReviews extends BaseRequest<ProductRatingPage> {

    public GetSellerReviews(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getSellerReviews(mRequestBundle.getData(), this);
    }
}
