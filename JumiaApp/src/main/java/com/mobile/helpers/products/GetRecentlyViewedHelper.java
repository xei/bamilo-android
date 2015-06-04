package com.mobile.helpers.products;

import android.os.Bundle;

import com.mobile.framework.database.LastViewedTableHelper;
import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.product.LastViewedAddableToCart;

import java.util.ArrayList;

/**
 * 
 * @author Andre Lopes
 * 
 */
public class GetRecentlyViewedHelper {

    public static String TAG = GetRecentlyViewedHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_RECENLTLY_VIEWED_LIST;

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
        ArrayList<LastViewedAddableToCart> listLastViewed = LastViewedTableHelper.getLastViewedAddableToCartList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, listLastViewed);
        requester.onRequestComplete(bundle);
    }
}
