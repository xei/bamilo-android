package com.mobile.newFramework.requests.campaign;

import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetCampaign extends BaseRequest<Campaign> {


    public GetCampaign(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getCampaign(mRequestBundle.getData(), this);
    }

}
