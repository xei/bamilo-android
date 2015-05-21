package com.mobile.newFramework.requests.configs;

import android.content.Context;

import com.mobile.framework.utils.EventType;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.interfaces.AigResponseCallback;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.BaseRequestBundle;
import com.mobile.newFramework.rest.AigRestAdapter;

public class GetImageResolutions extends BaseRequest {

    EventType type = EventType.GET_RESOLUTIONS;

    public GetImageResolutions(Context context, BaseRequestBundle requestBundle, AigResponseCallback requester) {
        super(context, requestBundle, requester);
    }

    @Override
    public void execute() {
        AigApiInterface service = AigRestAdapter.getRestAdapter(mContext, mRequestBundle.getUrl(), type.cacheTime).create(AigApiInterface.class);
        service.getImageResolutions(this);
    }
}
