package com.mobile.newFramework.requests.session;

import android.content.Context;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.forms.SuperForm;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetForgotPasswordForm extends BaseRequest<SuperForm> {

    EventType type = EventType.GET_FORGET_PASSWORD_FORM_EVENT;

    public GetForgotPasswordForm(Context context, RequestBundle requestBundle, AigResponseCallback requester) {
        super(context, requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mContext, mRequestBundle.getUrl(), type.cacheTime).create(AigApiInterface.class);
        service.getForgotPasswordForm(this);
    }
}

