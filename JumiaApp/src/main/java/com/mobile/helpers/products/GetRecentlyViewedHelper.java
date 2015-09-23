package com.mobile.helpers.products;

import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

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
