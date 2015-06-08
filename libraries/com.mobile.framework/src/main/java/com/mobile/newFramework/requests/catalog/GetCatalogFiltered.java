package com.mobile.newFramework.requests.catalog;

import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;

public class GetCatalogFiltered extends BaseRequest<Catalog> {

    public GetCatalogFiltered(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter( mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getCatalogFiltered(mRequestBundle.getData(), this);
    }

}
