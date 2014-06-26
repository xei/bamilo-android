package pt.rocket.helpers;

import java.util.ArrayList;

import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.objects.LastViewedAddableToCart;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.interfaces.IResponseCallback;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author Andre Lopes
 * 
 */
public class GetRecentlyViewedHelper {

    public static String TAG = GetRecentlyViewedHelper.class.getSimpleName();

    private static final EventType type = EventType.GET_RECENLTLYVIEWED_LIST;

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
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, listLastViewed);
        mResponseCallback.onRequestComplete(bundle);
    }
}
