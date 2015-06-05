package com.mobile.newFramework.requests.address;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.AddressCities;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetCities extends BaseRequest<AddressCities> {

    public GetCities(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getCities(mRequestBundle.getData(), this);
    }
}

