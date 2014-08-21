/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.widget.Toast;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.FavouritesListAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.cart.GetShoppingCartAddItemHelper;
import pt.rocket.helpers.products.GetFavouriteHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show all favourite items from database
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class FavouritesFragment extends BaseFragment implements IResponseCallback, OnClickListener {
    
    public final static String TAG = LogTagHelper.create(FavouritesFragment.class);
    
    private final static int SINGLE_ITEM = 1;
    
    private final static int NO_ERROR = -1;
    
    public static boolean isOnAddingAllItemsToCart = false;

    private static FavouritesFragment sFavouritesFragment;

    private FavouritesListAdapter mFavouritesAdapter;
    
    private ArrayList<Favourite> mFavourites;

    private GridView mFavouritesGridView;
    
    private Button mAddAllToCartButton;

    private int mNumberOfItemsForCart = Favourite.NO_SIMPLE_SELECTED;
    
    private int mAddedItemsCounter = 0 ;
    
    private ArrayList<Integer> mItemsNotAddedToCart = new ArrayList<Integer>();

    /**
     * Empty constructor
     */
    public FavouritesFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.Favorite,
                R.layout.favourites,
                R.string.favourites,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /**
     * Get a new instance
     * @return FavouritesFragment
     */
    public static FavouritesFragment getInstance() {
        sFavouritesFragment = new FavouritesFragment();
        return sFavouritesFragment;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Set the default value
        isOnAddingAllItemsToCart = false;
        // Retain the instance to receive callbacks from add all to cart
        setRetainInstance(true);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get grid view
        mFavouritesGridView = (GridView) view.findViewById(R.id.favourites_grid);
        // Get add to cart button
        mAddAllToCartButton = (Button) view.findViewById(R.id.favourite_button_shop_all);
        mAddAllToCartButton.setOnClickListener((OnClickListener) this);
        
        // Validate current state
        if(isOnAddingAllItemsToCart) {
            // Show progress 
            Log.i(TAG, "IS ON ADDING ALL ITEMS");
            showActivityProgress();
        } else {
            // show Loading View
            showFragmentLoading();
            // Get favourites
            Log.i(TAG, "LOAD FAVOURITE ITEMS");
            new GetFavouriteHelper((IResponseCallback) this);
        }
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // Tracking page        
        TrackerDelegator.trackPage(TrackingPage.FAVORITES);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVED INSTANCE");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        // Clean flag
        isOnAddingAllItemsToCart = false;
    }
    
    /**
     * ######### LAYOUTS #########  
     */
    
    /**
     * Show content after get items
     * @author sergiopereira
     */
    private void showContent(){
        // Validate favourites
        if (mFavourites != null && !mFavourites.isEmpty()) {
            Log.i(TAG, "ON SHOW CONTENT");
            mFavouritesAdapter = new FavouritesListAdapter(getBaseActivity(), mFavourites, (OnClickListener) this);
            mFavouritesGridView.setAdapter(mFavouritesAdapter);
            showFragmentContentContainer();
        } else {
            Log.i(TAG, "ON SHOW IS EMPTY");
            showEmpty();
        }
    }
    
    /**
     * Show empty content
     * @author andre
     */
    private void showEmpty() {
        getBaseActivity().showWarningVariation(false);
        mAddAllToCartButton.setVisibility(View.GONE);
        mAddAllToCartButton.setOnClickListener(null);
        showFragmentEmpty(R.string.favourite_no_favourites, R.drawable.img_nofavourites, R.string.continue_shopping, (OnClickListener) this);
    }
    

    /**
     * ######### LISTENERS #########  
     */

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case item
        if( id == R.id.favourite_item_container) onItemClick(view);
        // Case delete
        else if( id == R.id.favourite_button_delete) onClickDeleteItem(view);
        // Case add to cart
        else if( id == R.id.favourite_button_shop) onClickAddToCart(view);
        // Case add all
        else if( id == R.id.favourite_button_shop_all) onClickAddAllToCart();
        // Case simple
        else if( id == R.id.favourite_button_variant) onClickVariation(view);
        // Case continue shopping
        else if( id == R.id.fragment_root_empty_button) onClickContinueShopping();
        // Case unknown
        else Log.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
    }
    
    /**
     * Process the click on continue button
     * @author andre
     */
    private void onClickContinueShopping() {
        Log.i(TAG, "ON CLICK CONTINUE SHOPPING");
        getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on variation button
     * @param view
     * @author sergiopereira
     */
    private void onClickVariation(View view) {
        try {
            // Hide warning
            getBaseActivity().showWarningVariation(false);
            // Show dialog
            int position = Integer.parseInt(view.getTag().toString());
            Favourite favourite = mFavourites.get(position);
            favourite.setFavoriteSelected(position);
            showVariantsDialog(favourite);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK VARIATION");
        }
    }
            
    /**
     * Process the click on item
     * @param view
     * @author sergiopereira
     */
    private void onItemClick(View view) {
        Log.i(TAG, "ON ITEM CLICK");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            Favourite favourite = mFavourites.get(position);
            favourite.setFavoriteSelected(position);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, favourite.getUrl());
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }
    
    /**
     * Process the click on delete button
     * @param view
     * @author sergiopereira
     */
    private void onClickDeleteItem(View view) {
        Log.i(TAG, "ON CLICK DELETE ITEM");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            if (mFavourites == null || mFavourites.isEmpty()) {
                Log.e(TAG, "NULLPOINTER");
                throw new NullPointerException();
            }
            Favourite favourite = mFavourites.get(position);
            // Remove item from database
            FavouriteTableHelper.removeFavouriteProduct(favourite.getSku());
            // Remove from cache
            mFavourites.remove(favourite);
            // Update layout
            updateLayoutAfterAction();
            
            BaseFragment catalogFragment = (BaseFragment)getBaseActivity().getSupportFragmentManager().
                    findFragmentByTag(FragmentType.PRODUCT_LIST.toString());            
            if (null != catalogFragment) {
                catalogFragment.sendValuesToFragment(BaseFragment.FRAGMENT_VALUE_REMOVE_FAVORITE, favourite.getSku());
            }
            TrackerDelegator.trackRemoveFromFavorites(favourite.getSku());
            
            // Show Toast
            Toast.makeText(getBaseActivity(), getString(R.string.products_removed_favourite), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON DELETE ITEM");
        }
    }
    
    /**
     * Process the click on add all button
     * @author sergiopereira
     */
    private void onClickAddAllToCart() {
        Log.i(TAG, "ON CLICK ADD ITEM TO CART");
        try {
            if (validateVariations()) {
                onAddAllItemsToCart();
            } else {
                // Show the warning on header
                getBaseActivity().showWarningVariation(true);
                // Update content
                mFavouritesAdapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON ADD ALL TO CART");
        }
    }
        
    /**
     * Process the click on add button
     * @param view
     * @author sergiopereira
     */
    private void onClickAddToCart(View view) {
        Log.i(TAG, "ON CLICK ADD ALL TO CART");
        int position = Integer.parseInt(view.getTag().toString());
        Favourite favourite = mFavourites.get(position);
        // Validate variation
        if(hasSelectedVariation(favourite)) {
            Log.i(TAG, "SELECTED VARIATION");
            onAddItemToCart(favourite, position);
        } else {
            Log.i(TAG, "NOT SELECTED VARIATION");
            mFavouritesAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * Validate all items has selected variation
     * @return boolean
     * @author sergiopereira
     */
    private boolean validateVariations(){
        boolean result = true;
        // For each item
        for (Favourite item : mFavourites) {
            // Check selected variation
            boolean temp = hasSelectedVariation(item);
            // Save result if false
            if(!temp) result = false;
        }
        // Return result
        return result;
    }
    
    /**
     * Validate if item has a selected varititon
     * @param item
     * @return boolean
     * @author sergiopereira
     */
    private boolean hasSelectedVariation(Favourite item) {
        Log.d(TAG, "ON VALIDATE VARIATIONS: " + item.hasSimples() + " " + item.getSelectedSimple());
        // Validate if has simples > 1 and has a selected position
        if(item.hasSimples() && item.getSelectedSimple() == Favourite.NO_SIMPLE_SELECTED) {
            // Set the item to show warning
            item.setChooseVariationWarning(true);
            // Return not selected
            return false;
        } 
        // Return selected
        return true;
    }
    
    /**
     * Add an item to cart if completed
     * @param favourite
     * @param position
     * @author sergiopereira
     */
    private void onAddItemToCart(Favourite favourite, int position){
        Log.i(TAG, "ON EXECUTE ADD TO CART");
        // Show progress
        showActivityProgress();
        // Initialize cart vars
        isOnAddingAllItemsToCart = false;
        mAddedItemsCounter = 0;
        mNumberOfItemsForCart = SINGLE_ITEM;
        mItemsNotAddedToCart.clear();
        // Validate items
        if(favourite.isComplete())
            triggerAddProductToCart(favourite, position);
        else
            Log.w(TAG, "WARNING ITEM NOT COMPLETED: " + favourite.getName());
    }
    
    /**
     * Add all items to cart if completed
     * @author sergiopereira
     */
    private void onAddAllItemsToCart() {
        Log.i(TAG, "ON EXECUTE ADD ALL TO CART");
        // Show progress
        showActivityProgress();
        // Initialize cart vars
        isOnAddingAllItemsToCart = true;
        mAddedItemsCounter = 0;
        mNumberOfItemsForCart = mFavourites.size();
        mItemsNotAddedToCart.clear();
        // Validate all items
        for (int i = 0; i < mNumberOfItemsForCart; i++) {
            if(mFavourites.get(i).isComplete()) {
                // Add item to cart
                triggerAddProductToCart(mFavourites.get(i), i);
            } else{
                // Increment counter
                mAddedItemsCounter++;
                Log.w(TAG, "WARNING ITEM NOT COMPLETED: " + i + " " + mFavourites.get(i).getName() + " " + mAddedItemsCounter);
                // Save the position
                if(mItemsNotAddedToCart != null) mItemsNotAddedToCart.add(i);
                // Case all items are incomplete
                if(mAddedItemsCounter == mNumberOfItemsForCart) {
                    // Show toast
                    Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
                    // Dismiss
                    hideActivityProgress();
                }
            }
        }
    }
    
    /**
     * ######### TRIGGERS #########  
     */
    
    /**
     * Trigger to add an item to cart
     * @param favourite
     * @param position
     * @author sergiopereira
     */
    private synchronized void triggerAddProductToCart(Favourite favourite, int position) {
        Log.i(TAG, "ON TRIGGER ADD TO CART: " + position);
        ProductSimple simple = getSelectedSimple(favourite);
        // Item data
        ContentValues values = new ContentValues();
        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        values.put(GetShoppingCartAddItemHelper.PRODUCT_TAG, favourite.getSku());
        values.put(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        values.put(GetShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        bundle.putInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, position);
        bundle.putString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG, favourite.getSku());
        bundle.putBoolean(GetShoppingCartAddItemHelper.REMOVE_FAVOURITE_TAG, true);
        // Trigger
        triggerContentEventWithNoLoading( new GetShoppingCartAddItemHelper(), bundle, (IResponseCallback) this);
        // Tracking
        trackAddtoCart(sku, favourite);
    }
    
    /**
     * Track add to cart
     * @param sku
     * @param favourite
     * @author sergiopereira
     */
    private void trackAddtoCart(String sku, Favourite favourite){
        try {
            // Tracking
            Bundle bundle = new Bundle();
            bundle.putString(TrackerDelegator.SKU_KEY, sku);
            bundle.putLong(TrackerDelegator.PRICE_KEY, favourite.getSpecialPriceDouble() > 0 ? favourite.getSpecialPriceDouble().longValue() : favourite.getPriceAsDouble().longValue());
            bundle.putString(TrackerDelegator.NAME_KEY, favourite.getName());
            bundle.putString(TrackerDelegator.BRAND_KEY, favourite.getBrand());
            bundle.putString(TrackerDelegator.CATEGORY_KEY, "");
            bundle.putString(TrackerDelegator.LOCATION_KEY, getString(R.string.mixprop_itemlocationwishlist));
            TrackerDelegator.trackProductAddedToCart(bundle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the seleted simple
     * @param favourite
     * @return ProductSimple
     * @author sergiopereira
     */
    public ProductSimple getSelectedSimple(Favourite favourite) {
        Log.i(TAG, "ON GET SELECTED SIMPLE: " + favourite.getSimples().size());
        // Get item
        int selectedSimple = favourite.getSelectedSimple();
        //if (selectedSimple >= favourite.getSimples().size()) return null;
        //else if (selectedSimple == Favourite.NO_SIMPLE_SELECTED) return null;
        return favourite.getSimples().get(selectedSimple);
    }

    /**
     * ######### RESPONSE #########  
     */

    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON RESPONSE COMPLETE " + getId() );
        // Get event type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate the current state 
        if (isOnStoppingProcess && eventType != EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT) {
            Log.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate the event type
        switch (eventType) {
        case GET_FAVOURITE_LIST:
            Log.i(TAG, "ON RESPONSE COMPLETE: GET_FAVOURITE_LIST");
            mFavourites = (ArrayList<Favourite>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            Log.d(TAG, "NUMBER : " + mFavourites.size());
            // Show content
            showContent();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            // Update counter
            mAddedItemsCounter++;
            // Get data
            int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, -1);
            String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
            Log.i(TAG, "ON RESPONSE COMPLETE: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart );    
            // Validate current counter
            validateResponseCounter(true, pos, NO_ERROR);
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
        Log.i(TAG, "ON RESPONSE COMPLETE");
        // Get type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate the current state 
        if (isOnStoppingProcess && eventType != EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT) {
            Log.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate type
        switch (eventType) {
        case GET_FAVOURITE_LIST:
            Log.d(TAG, "ON RESPONSE ERROR: GET_FAVOURITE_LIST");
            showFragmentContentContainer();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Log.d(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT");
            // Inc counter
            mAddedItemsCounter++;
            // Get item set stock error
            int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, Favourite.NO_SIMPLE_SELECTED);
            String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);            
            Log.i(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
            // Save the position
            if(mItemsNotAddedToCart != null) mItemsNotAddedToCart.add(pos);
            // Check type error is out of stock
            int error = checkTypeError(bundle, pos);
            // Validate current counter
            validateResponseCounter(false, pos, error);
        default:
            Log.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
    }
    
    /**
     * Method used to validate the error response
     * @param bundle
     * @param pos
     * @return int - The string id
     */
    private int checkTypeError(Bundle bundle, int pos) {
        // Generic error
        int error = R.string.error_please_try_again;
        // Get error code
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        // Validate error
        if(errorCode == ErrorCode.CONNECT_ERROR) {
            Log.i(TAG, "ON RESPONSE ERROR: CONNECT_ERROR"); 
            error = R.string.error_no_connection;
        } else if(errorCode == ErrorCode.REQUEST_ERROR) {
          HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
          // CASE OUT OF STOCK
          if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
              Log.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_SOLD_OUT");
              if(mFavourites != null && !mFavourites.isEmpty()) mFavourites.get(pos).setVariationStockWarning(true);
              // Return string out of stock
              error = R.string.product_outof_stock;
          // CASE ERROR ADDING    
          } else if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
              Log.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_ERROR_ADDING");
              // Return error add to cart failed
              //error = R.string.error_add_to_cart_failed;
              if(mFavourites != null && !mFavourites.isEmpty()) mFavourites.get(pos).setVariationStockWarning(true);
              // Return string out of stock
              error = R.string.product_outof_stock;
           // CASE UNKNOWN
          } else {
              Log.i(TAG, "ON RESPONSE ERROR: " + errorCode.toString()); 
          }
        }
        return error;
    }
    
    /**
     * Validates the response counter from success or error
     * @param success - from respective response
     * @param pos - current position
     * @param error - the string id for error
     * @author sergiopereira
     */
    private void validateResponseCounter(boolean success, int pos, int error){
        // Update adapter
        if (mAddedItemsCounter == mNumberOfItemsForCart) {
            // Set flag
            isOnAddingAllItemsToCart = false;
            if(!isOnStoppingProcess) {
                // CASE ALL ITEMS, get items not added to cart 
                if(mNumberOfItemsForCart > SINGLE_ITEM) validateItemsNotAddedToCart();
                // CASE ONE ITEM
                else validateItemWasAddedToCart(success, pos, error);
                // Update layout
                updateLayoutAfterAction();
            }
            // Update cart
            getBaseActivity().updateCartInfo();
        }
    }
    
    /**
     * Validate the item was added to cart
     * @param success - from respective response
     * @param pos - current position
     * @param error - the string id for error
     * @author sergiopereira
     */
    private void validateItemWasAddedToCart(boolean success, int pos, int error) {
        // Assumed that was added to cart
        String message = getString(R.string.favourite_add_to_cart);
        // Case added to cart
        if(success) mFavourites.remove(pos);
        // Case not added to cart
        else message = getString(error);
        // Show toast
        if(!isOnStoppingProcess) Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Validate all items were added to cart
     * @author sergiopereira
     */
    private void validateItemsNotAddedToCart() {
        // Get the size of items not added
        int errorsSize = mItemsNotAddedToCart.size();
        // Get the size of favourites
        int favSize = mFavourites.size();

        // Case some items not added
        if(errorsSize > 0 && errorsSize != favSize) {
            Log.i(TAG, "SOME ITEMS NOT ADDED TO CART: CREATE NEW ARRAY " + errorsSize + " " + favSize);
            getNotAddedItems();
            Toast.makeText(getBaseActivity(), getString(R.string.some_products_not_added), Toast.LENGTH_SHORT).show();
        // Case all items added    
        } else if (errorsSize == 0) {
            Log.i(TAG, "ALL ITEMS ADDED TO CART: CLEAN ARRAY " + errorsSize + " " + favSize);
            mFavourites.clear();
            Toast.makeText(getBaseActivity(), getString(R.string.favourite_addalltocart), Toast.LENGTH_SHORT).show();
        // Case zero items added
        } else {
            Log.i(TAG, "NO ITEMS ADDED TO CART: MANTAIN ARRAY " + errorsSize + " " + favSize);
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Get not added items
     * @author sergiopereira
     */
    private void getNotAddedItems(){
        Log.i(TAG, "ON GET NOT ADDED ITEMS");
        // Create new array
        ArrayList<Favourite> array = new ArrayList<Favourite>();
        // Add items not added to cart
        for (Integer pos : mItemsNotAddedToCart) array.add(mFavourites.get(pos));
        // Show new items
        mFavourites = array;
        mFavouritesGridView.setAdapter(null);
        mFavouritesAdapter = new FavouritesListAdapter(getBaseActivity(), mFavourites, (OnClickListener) this);
        mFavouritesGridView.setAdapter(mFavouritesAdapter);
    }
    
    /**
     * Update the layout after user action
     * @author sergiopereira
     */
    private synchronized void updateLayoutAfterAction() {
        Log.i(TAG, "ON UPDATE LAYOUT AFTER ACTION");
        // Dismiss
        hideActivityProgress();
        // Update adapter
        mFavouritesAdapter.notifyDataSetChanged();
        // Validate current state
        if (mFavourites.isEmpty()) showEmpty();
    }
    
    /**
     * ###### DIALOG ###### 
     */
    
    private final static String VARIATION_PICKER_ID = "variation_picker";

    
    private void showVariantsDialog(Favourite favourite) {
        String title = getString(R.string.product_variance_choose);
        //
        ArrayList<String> mSimpleVariantsAvailable = new ArrayList<String>();
        ArrayList<String> mSimpleVariants = createSimpleVariants(favourite, mSimpleVariantsAvailable);
        //
        DialogListFragment dialogListFragment = DialogListFragment.newInstance(getBaseActivity(),
                                                                                new FavouriteOnDialogListListener(favourite), 
                                                                                VARIATION_PICKER_ID, 
                                                                                title, 
                                                                                mSimpleVariants, 
                                                                                mSimpleVariantsAvailable, 
                                                                                favourite.getSelectedSimple());
  
        dialogListFragment.show(getFragmentManager(), null);
    }
    
    
    
    
    private class FavouriteOnDialogListListener implements OnDialogListListener {
        
        Favourite favourite;

        public FavouriteOnDialogListListener(Favourite favourite) {
            this.favourite = favourite;
        }

        @Override
        public void onDialogListItemSelect(String id, int position, String value) {
            Log.i(TAG, "size selected! onDialogListItemSelect : " + position);
            favourite.setChooseVariationWarning(false);
            favourite.setVariationStockWarning(false);
            favourite.setSelectedSimple(position);
            favourite.setSelectedSimpleValue(value);
            mFavouritesAdapter.notifyDataSetChanged();
        }

    }

    
    private ArrayList<String> createSimpleVariants(Favourite favourite, ArrayList<String> mSimpleVariantsAvailable) {
        Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants" + favourite.getName());
        ArrayList<ProductSimple> simples = (ArrayList<ProductSimple>) favourite.getSimples().clone();
        ArrayList<String> variations = favourite.getKnownVariations();
        if(variations == null || variations.size() == 0){
            variations = new ArrayList<String>();
            variations.add("size");
            variations.add("color");
            variations.add("variation");
        }
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(favourite.getSimples(), variations);

        ArrayList<String> variationValues = new ArrayList<String>();
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants in");
            String value = calcVariationStringForSimple(simple, foundKeys);
            
            /**
             * TODO: Uncommented to validate the stock
             * @author sergiopereira
             */
            //String quantity = simple.getAttributeByKey(ProductSimple.QUANTITY_TAG);
            //if (quantity != null && Long.parseLong(quantity) > 0) {
                variationValues.add(value);
                mSimpleVariantsAvailable.add(value);
            //} else {
            //    variationValues.add(value);
            //}

        }

        return variationValues;
    }
    
    private Set<String> scanSimpleAttributesForKnownVariants(ArrayList<ProductSimple> simples, ArrayList<String> variations) {
        Set<String> foundVariations = new HashSet<String>();
        Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants");
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants in");
            scanSimpleForKnownVariants(simple, foundVariations, variations);
        }
        return foundVariations;
    }

    private void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations, ArrayList<String> variations) {
        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            Log.i(TAG, "scanSimpleForKnownVariations: variation = " +  variation + " attr = " + attr);
            if (attr == null)
                continue;
            foundVariations.add(variation);
        }
    }
    
    private String calcVariationStringForSimple(ProductSimple simple, Set<String> keys) {
        String delim = ";";
        String loopDelim = "";
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = simple.getAttributeByKey(key);
            if (value != null) {
                sb.append(loopDelim);
                sb.append(value);
                loopDelim = delim;
            }
        }

        return sb.toString();
    }
        
}
