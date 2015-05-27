package com.mobile.newFramework.requests.reviews;

import android.content.Context;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.forms.SuperForm;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetRatingForm extends BaseRequest<SuperForm> {

    EventType type = EventType.GET_FORM_RATING_EVENT;

    public GetRatingForm(Context context, RequestBundle requestBundle, AigResponseCallback requester) {
        super(context, requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mContext, mRequestBundle.getUrl(), type.cacheTime).create(AigApiInterface.class);
        service.getRatingForm(this);
    }
}

