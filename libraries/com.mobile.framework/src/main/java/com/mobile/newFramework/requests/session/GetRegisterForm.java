package com.mobile.newFramework.requests.session;

import android.content.Context;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetRegisterForm extends BaseRequest<Form> {

    public GetRegisterForm(Context context, RequestBundle requestBundle, AigResponseCallback requester) {
        super(context, requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mContext, mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getRegisterForm(this);
    }
}

