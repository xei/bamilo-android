package pt.rocket.view.fragments;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;
import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.helpers.GetFavouriteHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.view.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FavouritesFragment extends BaseFragment implements IResponseCallback {
    
    public static String TAG = FavouritesFragment.class.getSimpleName();

    
    
    public static BaseFragment newInstance(Bundle bundle) {
        return new FavouritesFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout l = new LinearLayout(getBaseActivity());
        l.setBackgroundColor(getResources().getColor(R.color.red_basic));
        return l;
    }
    
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // XXX
        
        new GetFavouriteHelper(this);
    }
    
    


    // XXX
    @Override
    public void onRequestComplete(Bundle bundle) {
     // Get event
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate event
        switch (eventType) {
        case GET_FAVOURITE_LIST:
            Log.d(TAG, "ON RESPONSE COMPLETE: GET_FAVOURITE_LIST");
            ArrayList<Favourite> favourites = (ArrayList<Favourite>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            
            Log.d(TAG, "FAV: " + favourites.size());
 
            break;
        default:
            Log.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
            break;
        }
        
    }


    // XXX
    @Override
    public void onRequestError(Bundle bundle) {
     // Get event
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate event
        switch (eventType) {
        case GET_FAVOURITE_LIST:
            Log.d(TAG, "ON RESPONSE ERROR: GET_FAVOURITE_LIST");
            break;
        default:
            Log.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
        
    }




}
