package com.mobile.newFramework.requests.configs;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.configs.AvailableCountries;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetAvailableCountries extends BaseRequest<AvailableCountries> {

    public GetAvailableCountries(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getAvailableCountries(this);
    }

}
