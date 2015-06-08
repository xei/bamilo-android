package com.mobile.newFramework.requests.home;

import com.mobile.newFramework.objects.statics.StaticPage;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.rest.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetShopInShopPage extends BaseRequest<StaticPage> {

    public GetShopInShopPage(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getShopInShop(mRequestBundle.getData(), this);
    }
}
