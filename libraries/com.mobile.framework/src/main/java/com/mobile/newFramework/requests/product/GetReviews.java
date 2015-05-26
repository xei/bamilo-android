package com.mobile.newFramework.requests.product;

import android.content.Context;

import com.mobile.newFramework.objects.ProductRatingPage;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetReviews extends BaseRequest<ProductRatingPage> {

    public GetReviews(Context context, RequestBundle requestBundle, AigResponseCallback requester) {
        super(context, requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mContext, mRequestBundle.getUrl(), mRequestBundle.getCache()).create(AigApiInterface.class);
        service.getReviews(mRequestBundle.getData(),this);
    }
}
