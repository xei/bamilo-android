package pt.rocket.helpers.products;

import java.util.ArrayList;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.interfaces.IResponseCallback;
import android.os.Bundle;
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
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
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
            // Get complete product
            CompleteProduct completeProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            // Update entry
            FavouriteTableHelper.updateFavouriteProduct(
                    completeProduct.getSku(),
                    completeProduct.getSimples(),
                    completeProduct.getVariations(),
                    completeProduct.getKnownVariations());
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
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
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
