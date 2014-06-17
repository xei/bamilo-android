/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.holoeverywhere.widget.Toast;

import pt.rocket.controllers.FavouritesListAdapter;
import pt.rocket.controllers.FavouritesListAdapter.Item;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.GetFavouriteHelper;
import pt.rocket.helpers.GetShoppingCartAddItemHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import de.akquinet.android.androlog.Log;

/**
 * @author Andre Lopes
 * 
 */
public class FavouritesFragment extends BaseFragment {
    
    private final static String TAG = LogTagHelper.create(FavouritesFragment.class);

    private Context mContext;

    private long mBeginRequestMillis;

    private static View mainView;

    private static FavouritesFragment mFavouritesFragment;

    private FavouritesListAdapter mFavouritesAdapter;
    private ArrayList<Favourite> mFavourites;
    
    private boolean mFavouritesAddedToCartSuccessfull;
    private ArrayList<String> mFavouritesAddedToCart;

    private View mFavouritesNotFound;
    private GridView mFavouritesGrid;
    private Button mAddAllToCartButton;

    private View mLoadingView;

    //private RetainedFragment dataFragment;

    public FavouritesFragment() {

        super(EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW),
                NavigationAction.Favourite,
                R.string.my_favourites, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*-// find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            mFavourites = null;
        } else {
            mFavourites = dataFragment.favourites;
        }*/

        // TODO save favourites on onSaveInstanceState() 
        /*-Object favourites = savedInstanceState.getParcelableArrayList("favourites");
        if (favourites != null) {
            mFavourites = (ArrayList<Favourite>) favourites;
        }*/
        mFavourites = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mainView = inflater.inflate(R.layout.favourites, container, false);

        setAppContentLayout();

        return mainView;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    public static FavouritesFragment getInstance() {
        mFavouritesFragment = new FavouritesFragment();
        return mFavouritesFragment;
    }

    /*-class RetainedFragment extends Fragment {
        // data object we want to retain
        private ArrayList<Favourite> favourites;

        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // retain this fragment
            setRetainInstance(true);
        }

        public void setFavourites(ArrayList<Favourite> favourites) {
            this.favourites = favourites;
        }

        public ArrayList<Favourite> getFavourites() {
            return favourites;
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
     // store the data in the fragment
        dataFragment.setFavourites(mFavourites);
    }*/

    /*-@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(mContext, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(mContext, "portrait", Toast.LENGTH_SHORT).show();
        }
        
    }*/
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO make Favourites Parcelable
        //outState.putParcelableArrayList("favourites", mFavourites);

        super.onSaveInstanceState(outState);
    }

    private void setAppContentLayout() {
        if (mainView == null) {
            mainView = getView();
        }

        mFavouritesNotFound = mainView.findViewById(R.id.favourites_not_found);
        //currentPage.setGridView((GridView) currentPage.getRelativeLayout().findViewById(R.id.middle_productslist_list), isTabletInLandscape);
        mFavouritesGrid = (GridView) mainView.findViewById(R.id.middle_favouriteslist_list);

        mLoadingView = mainView.findViewById(R.id.loading_view_pager);

        mAddAllToCartButton = (Button) mainView.findViewById(R.id.favourites_shop_all);
        mAddAllToCartButton.setVisibility(View.GONE);
        mAddAllToCartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAllProducts()) {
                    getBaseActivity().showProgress();
                    executeAddAllProductsToCart();
                } else {
                    Toast.makeText(mContext, getString(R.string.favourite_variance_choose_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        boolean isTabletInLandscape = BaseActivity.isTabletInLandscape(getBaseActivity()); 
        int numColumns = isTabletInLandscape ? 2 : 1;
        mFavouritesGrid.setNumColumns(numColumns);
    }

    private void init() {
        Log.d(TAG, "INIT");
        mContext = getActivity();

        // if mFavourites was loaded after a screen rotation
        if (mFavourites != null && !mFavourites.isEmpty()) {
            mFavouritesAdapter = new FavouritesListAdapter(FavouritesFragment.this, mContext, mFavourites);
            mFavouritesGrid.setAdapter(mFavouritesAdapter);

            mAddAllToCartButton.setVisibility(View.VISIBLE);
        } else {
            mLoadingView.setVisibility(View.VISIBLE);
            //getBaseActivity().showProgress();
            // get Favourites
            /*-new AsyncTask<Void, Void, ArrayList<Favourite>>() {
                @Override
                protected ArrayList<Favourite> doInBackground(Void... params) {
                    //return FavouriteTableHelper.getFavouriteList();
                }

                protected void onPostExecute(ArrayList<Favourite> result) {
                    mFavourites = result;
                    if (mFavourites != null && !mFavourites.isEmpty()) {
                        loadFavourites(mFavourites);
                    } else {
                        showNoFavourites();
                        getBaseActivity().showContentContainer();
                    }
                };
            }.execute();*/

            new GetFavouriteHelper(responseCallback);
        }
    }

    /*-private void loadFavourites(ArrayList<Favourite> favourites) {
        for(Favourite favourite : favourites){
            if (!favourite.isComplete()) {
                Log.d(TAG, "another Favourite");
                loadProduct(favourite);
            }
        }
        mFavouritesAdapter = new FavouritesListAdapter(FavouritesFragment.this, mContext, favourites);
        mFavouritesGrid.setAdapter(mFavouritesAdapter);

        mAddAllToCartButton.setVisibility(View.VISIBLE);
        getBaseActivity().showContentContainer();
    }*/

    /*-private void loadProduct(Favourite favourite) {
        Log.d(TAG, "LOAD PRODUCT");
        mBeginRequestMillis = System.currentTimeMillis();
        Bundle bundle = new Bundle();
        bundle.putString(GetProductHelper.PRODUCT_URL, favourite.getUrl());
        triggerContentEvent(new GetProductHelper(), bundle, responseCallback);
        getBaseActivity().setProcessShow(false);
    }*/

    public void addToCart(BaseHelper helper, Bundle args, IResponseCallback responseCallback) {
        triggerContentEventWithNoLoading(helper, args, responseCallback);
    }

    private boolean validateAllProducts() {
        boolean allItemsValid = true;

        int favoritesSize = mFavourites.size();
        if (favoritesSize > 0) {
            for (int i = 0; i < favoritesSize; i++) {
                Favourite favourite = mFavourites.get(i);

                if (favourite != null) {
                    ProductSimple simple = mFavouritesAdapter.getSelectedSimple(favourite);
                    if (simple == null) {
                        View row = mFavouritesGrid.getChildAt(i);
                        if (row != null) {
                            Object tag = row.getTag();
                            if (tag != null && tag instanceof Item) {
                                Item item = (Item) tag;
                                mFavouritesAdapter.showChooseReminder(item);
                            }
                        }
                        allItemsValid = false;
                    }
                } else {
                    Log.e(TAG, "There are favourites set as null on mFavourites list");
                    allItemsValid = false;
                }
            }
        }

        return allItemsValid;
    }

    private void executeAddAllProductsToCart() {
        mFavouritesAddedToCart = new ArrayList<String>();
        mFavouritesAddedToCartSuccessfull = true;

        for (Favourite favourite : mFavourites) {
            ProductSimple simple = mFavouritesAdapter.getSelectedSimple(favourite);

            ContentValues values = new ContentValues();

            String sku = favourite.getSku();
            String skuSimple = simple.getAttributeByKey(ProductSimple.SKU_TAG);
            values.put("p", sku);
            values.put("sku", skuSimple);
            values.put("quantity", "" + 1);
            Bundle bundle = new Bundle();
            bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);

            // added sku to list for checking status
            mFavouritesAddedToCart.add(sku);

            addToCart(new GetShoppingCartAddItemHelper(), bundle, responseCallback);
        }
    }

    private void showNoFavourites() {
        Log.d(TAG, "showNoFavourites");

        mFavouritesNotFound.setVisibility(View.VISIBLE);
    }

    IResponseCallback responseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    public void onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON RESPONSE COMPLETE:");

        if (isOnStoppingProcess) return;

        getBaseActivity().handleSuccessEvent(bundle);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case GET_FAVOURITE_LIST:
            Log.d(TAG, "ON RESPONSE COMPLETE: GET_FAVOURITE_LIST");
            mFavourites = (ArrayList<Favourite>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);

            if (mFavourites != null && !mFavourites.isEmpty()) {
                mFavouritesAdapter = new FavouritesListAdapter(FavouritesFragment.this, mContext, mFavourites);
                mFavouritesGrid.setAdapter(mFavouritesAdapter);

                mAddAllToCartButton.setVisibility(View.VISIBLE);
            } else {
                showNoFavourites();
            }

            mLoadingView.setVisibility(View.GONE);

            Log.d(TAG, "FAV: " + mFavourites.size());
 
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            String skuAddedItem = (String) bundle.getSerializable(Constants.BUNDLE_METADATA_KEY);
            checkAddedItemToShoppingCart(skuAddedItem, true);
            break;
        default:
            Log.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
            break;
        }
    }

    @Override
    public boolean allowBackPressed() {
        ((BaseActivity) getActivity()).setProcessShow(true);
        return super.allowBackPressed();
    }

    public void onErrorEvent(Bundle bundle) {
        Log.d(TAG, "ON RESPONSE ERROR:");

//        if(baseActivity != null){
//            baseActivity.setProcessShow(true);
//        }
//        // Validate fragment visibility
//        if (isOnStoppingProcess) {
//            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
//            return;
//        }
//        
//        if(baseActivity.handleErrorEvent(bundle)){
//            return;
//        }

        if (isOnStoppingProcess) return;

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case GET_FAVOURITE_LIST:
            Log.d(TAG, "ON RESPONSE ERROR: GET_FAVOURITE_LIST");
            mLoadingView.setVisibility(View.GONE);
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            String skuAddedItem = (String) bundle.getSerializable(Constants.BUNDLE_METADATA_KEY);
            checkAddedItemToShoppingCart(skuAddedItem, false);
            break;
        default:
            Log.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
    }

    private void checkAddedItemToShoppingCart(String sku, boolean success) {
        if (!TextUtils.isEmpty(sku) && mFavouritesAddedToCart.contains(sku)) {
            mFavouritesAddedToCart.remove(sku);
            if (success) {
                // identify the Favourite through the sku and remove it from the list
                for (Favourite favourite : mFavourites) {
                    if (sku.equalsIgnoreCase(favourite.getSku())) {
                        removeFromFavourites(favourite);

                        break;
                    }
                }
            } else {
                // signal that an error occurred on adding all items to cart
                mFavouritesAddedToCartSuccessfull = false;
            }

            checkLastAddedItemToShoppingCart();
        }
    }

    private void checkLastAddedItemToShoppingCart() {
        if (mFavouritesAddedToCart.isEmpty()) {
            mFavouritesAdapter.notifyDataSetChanged();

            getBaseActivity().dismissProgress();

            if (!mFavouritesAddedToCartSuccessfull) {
                Toast.makeText(mContext, getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeFromFavourites(Favourite favourite) {
        FavouriteTableHelper.removeFavouriteProduct(favourite.getSku());

        mFavourites.remove(favourite);
        if (!mFavourites.isEmpty()) {
            for (Favourite fav : mFavourites) {
                if (fav.hasVariations) {
                    fav.hasVariations = null;
                }
            }
        }
        mFavouritesAdapter.notifyDataSetChanged();

        if (mFavourites.isEmpty()) {
            mFavouritesNotFound.setVisibility(View.VISIBLE);

            mAddAllToCartButton.setVisibility(View.GONE);
        }
    }
}
