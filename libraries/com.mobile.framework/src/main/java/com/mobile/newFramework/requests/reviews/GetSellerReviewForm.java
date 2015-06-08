package com.mobile.newFramework.requests.reviews;

import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.utils.EventType;

public class GetSellerReviewForm extends BaseRequest {

    EventType type = EventType.GET_FORM_SELLER_REVIEW_EVENT;

    public GetSellerReviewForm(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getSellerReviewForm(this);
    }
}
