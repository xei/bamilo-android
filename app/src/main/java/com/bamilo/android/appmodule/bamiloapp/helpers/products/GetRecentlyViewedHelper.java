package com.bamilo.android.appmodule.bamiloapp.helpers.products;

import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.database.LastViewedTableHelper;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.output.Print;

import java.util.ArrayList;

/**
 * 
 * @author Andre Lopes
 * 
 */
public class GetRecentlyViewedHelper {

    public static String TAG = GetRecentlyViewedHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_RECENTLY_VIEWED_LIST;

    /**
     * 
     * @param requester
     */
    public GetRecentlyViewedHelper(IResponseCallback requester) {
        Print.d(TAG, "ON CONSTRUCTOR");
        // Get all items on database
        getRecentlyViewedList(requester);
    }

    /**
     * TODO
     */
    private void getRecentlyViewedList(IResponseCallback requester) {
        Print.d(TAG, "ON GET FAVOURITE LIST");
        ArrayList<String> listLastViewed = LastViewedTableHelper.getLastViewedAddableToCartList();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.getMetadata().setData(listLastViewed);
        baseResponse.setEventType(EVENT_TYPE);
        requester.onRequestComplete(baseResponse);
    }
}
