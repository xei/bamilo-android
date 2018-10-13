package com.bamilo.android.appmodule.bamiloapp.helpers.products;

import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.database.LastViewedTableHelper;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.EventType;

import java.util.ArrayList;

/**
 * 
 * @author Andre Lopes
 * 
 */
public class GetRecentlyViewedHelper {

    public static String TAG = GetRecentlyViewedHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_RECENTLY_VIEWED_LIST;

    public GetRecentlyViewedHelper(IResponseCallback requester) {
        // Get all items on database
        getRecentlyViewedList(requester);
    }

    private void getRecentlyViewedList(IResponseCallback requester) {
        ArrayList<String> listLastViewed = LastViewedTableHelper.getLastViewedAddableToCartList();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.getMetadata().setData(listLastViewed);
        baseResponse.setEventType(EVENT_TYPE);
        requester.onRequestComplete(baseResponse);
    }
}
