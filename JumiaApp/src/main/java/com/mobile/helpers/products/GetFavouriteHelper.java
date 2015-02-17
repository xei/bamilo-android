package com.mobile.helpers.products;

import java.util.ArrayList;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.database.FavouriteTableHelper;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.Favourite;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.interfaces.IResponseCallback;

import de.akquinet.android.androlog.Log;


public class GetFavouriteHelper implements IResponseCallback {

    public static String TAG = GetFavouriteHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_FAVOURITE_LIST;

    private int mNumberOfIncomplete = 0;

    private int counter = 0;

    private IResponseCallback mResponseCallback;

    /**
     * @param mNumberOfIncomplete
     * @param counter
     */
    public GetFavouriteHelper(IResponseCallback requester) {
        Log.d(TAG, "ON CONSTRUCTOR");
        // Get call back
        mResponseCallback = requester;
        // Get all items on database
        getCompleteFavouriteList();
    }
    
    /**
     * TODO
     */
    public void getCompleteFavouriteList() {
        Log.d(TAG, "ON GET COMPLETE FAVOURITE LIST");
        // Get incomplete items
        ArrayList<String> incompleteItems = FavouriteTableHelper.getIncompleteFavouriteList();
        // For each request to complete
        if (incompleteItems != null && (mNumberOfIncomplete = incompleteItems.size()) > 0) {
            for (String url : incompleteItems) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, url);
                JumiaApplication.INSTANCE.sendRequest(new GetProductHelper(), bundle, this);
            }
        } else {
            getFavouriteList();
        }
    }

    /**
     * TODO
     */
    private void getFavouriteList() {
        Log.d(TAG, "ON GET FAVOURITE LIST");
        ArrayList<Favourite> favourites = FavouriteTableHelper.getFavouriteList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_KEY, favourites);
        mResponseCallback.onRequestComplete(bundle);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        // Get event
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate event
        switch (eventType) {
        case GET_PRODUCT_EVENT:
            Log.d(TAG, "ON RESPONSE COMPLETE: GET_PRODUCT_EVENT");
            // Inc counter
            counter++;
            // Get complete product and update
            CompleteProduct completeProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            FavouriteTableHelper.updateFavouriteProduct(completeProduct);
            // Get all items already update and send to callback
            if (counter == mNumberOfIncomplete) getFavouriteList();
            break;
        default:
            Log.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
            break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        // Get event
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate event
        switch (eventType) {
        case GET_PRODUCT_EVENT:
            Log.d(TAG, "ON RESPONSE ERROR: GET_PRODUCT_EVENT");
            // Inc counter
            counter++;
            // in case the last response was an error
            if (counter == mNumberOfIncomplete) getFavouriteList();
            break;
        default:
            Log.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
    }

}
