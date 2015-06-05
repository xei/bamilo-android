package com.mobile.newFramework.requests.configs;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.configs.CountryConfigs;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetCountryConfigurations extends BaseRequest<CountryConfigs> {

    public GetCountryConfigurations(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getCountryConfigurations(this);
    }
}
