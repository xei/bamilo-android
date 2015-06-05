package com.mobile.newFramework.requests.search;

import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.objects.search.Suggestions;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class SearchSuggestions extends BaseRequest<Suggestions> {

    public SearchSuggestions(RequestBundle requestBundle, AigResponseCallback requester) {
        super(requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mRequestBundle.toRestAdapterInit()).create(AigApiInterface.class);
        service.getSearchSuggestions(mRequestBundle.getData(), this);
    }
}
