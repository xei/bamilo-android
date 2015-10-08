package com.mobile.helpers.products;

import android.os.Bundle;

import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.utils.Constants;
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
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putStringArrayList(Constants.BUNDLE_RESPONSE_KEY, listLastViewed);
        requester.onRequestComplete(bundle);
    }
}
