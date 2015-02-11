package com.mobile.helpers.products;

import java.util.ArrayList;

import com.mobile.framework.database.LastViewedTableHelper;
import com.mobile.framework.objects.LastViewedAddableToCart;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.interfaces.IResponseCallback;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author Andre Lopes
 * 
 */
public class GetRecentlyViewedHelper {

    public static String TAG = GetRecentlyViewedHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_RECENLTLYVIEWED_LIST;

    private IResponseCallback mResponseCallback;

    /**
     * 
     * @param requester
     */
    public GetRecentlyViewedHelper(IResponseCallback requester) {
        Log.d(TAG, "ON CONSTRUCTOR");
        // Get call back
        mResponseCallback = requester;
        // Get all items on database
        getRecentlyViewedList();
    }

    /**
     * TODO
     */
    private void getRecentlyViewedList() {
        Log.d(TAG, "ON GET FAVOURITE LIST");
        ArrayList<LastViewedAddableToCart> listLastViewed = LastViewedTableHelper.getLastViewedAddableToCartList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, listLastViewed);
        mResponseCallback.onRequestComplete(bundle);
    }
}