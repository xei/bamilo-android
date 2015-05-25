package com.mobile.newFramework.requests.campaign;

import android.content.Context;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.BaseRequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetCampaign extends BaseRequest {


    public GetCampaign(Context context, BaseRequestBundle requestBundle, AigResponseCallback requester) {
        super(context, requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mContext, mRequestBundle.getUrl(), mRequestBundle.getCache()).create(AigApiInterface.class);
        service.getCampaign(mRequestBundle.getData(), this);
    }

}
