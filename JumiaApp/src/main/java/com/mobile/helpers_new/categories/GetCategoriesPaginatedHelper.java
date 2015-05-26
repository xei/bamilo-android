package com.mobile.helpers_new.categories;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.service.RemoteService;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers_new.BaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.categories.GetCategoriesPaginated;

import java.util.HashMap;

public class GetCategoriesPaginatedHelper extends BaseHelper {

    private static final EventType EVENT_TYPE = EventType.GET_CATEGORIES_EVENT;

    @Override
    public void onPreRequest(Bundle args) {
        super.onPreRequest(args);
    }

    @Override
    public void onRequest(Bundle args, IResponseCallback callback) {
        HashMap<String, String> data = new HashMap<>();
        data.put("paginate", "1");
        RequestBundle requestBundle = new RequestBundle.Builder()
                .setUrl(RemoteService.completeUri(Uri.parse(EVENT_TYPE.action)).toString())
                .setCache(EventType.GET_CATEGORIES_EVENT.cacheTime)
                .setData(data)
                .build();
        new GetCategoriesPaginated(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Bundle bundle = new Bundle();
        //bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, baseResponse);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
    }

}
