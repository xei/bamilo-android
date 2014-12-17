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

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.AddableToCartListAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.AddableToCart;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.AdjustTracker;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.cart.GetShoppingCartAddItemHelper;
import pt.rocket.helpers.products.GetFavouriteHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show all favourite items from database
 * 
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class FavouritesFragment extends BaseFragment implements IResponseCallback, OnClickListener {

    protected final static String TAG = LogTagHelper.create(FavouritesFragment.class);

    protected final static int SINGLE_ITEM = 1;

    protected final static int NO_ERROR = -1;
    
    protected final static int OUT_OF_STOCK_ERROR = -2;

    protected static boolean isOnAddingAllItemsToCart = false;

    protected AddableToCartListAdapter mAddableToCartAdapter;

    protected ArrayList<AddableToCart> mAddableToCartList;

    protected GridView mAddableToCartGridView;

    protected Button mAddAllToCartButton;

    protected int mNumberOfItemsForCart = AddableToCart.NO_SIMPLE_SELECTED;

    protected int mAddedItemsCounter = 0;

    protected ArrayList<Integer> mItemsNotAddedToCart = new ArrayList<Integer>();
    
    protected TrackingPage mPageName = TrackingPage.FAVORITES;

    /**
     * Empty constructor
     */
    public FavouritesFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Favorite,
                R.layout.favourites,
                R.string.favourites,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    protected FavouritesFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action,
            int layoutResId, int titleResId, KeyboardState adjust_state) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjust_state);
    }

    /**
     * Get a new instance
     * 
     * @return FavouritesFragment
     */
    public static FavouritesFragment getInstance() {
        return new FavouritesFragment();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Set the default value
        isOnAddingAllItemsToCart = false;
        // Retain the instance to receive callback from add all to cart
        setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get grid view
        mAddableToCartGridView = (GridView) view.findViewById(R.id.favourites_grid);
        // Get add to cart button
        mAddAllToCartButton = (Button) view.findViewById(R.id.button_shop_all);
        mAddAllToCartButton.setOnClickListener((OnClickListener) this);

        // Validate current state
        if (isOnAddingAllItemsToCart) {
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
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // Tracking page
        TrackerDelegator.trackPage(mPageName, getLoadTime(), false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVED INSTANCE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * 
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
     * 
     * @author sergiopereira
     */
    protected void showContent() {
        // Validate favourites
        if (mAddableToCartList != null && !mAddableToCartList.isEmpty()) {
            Log.i(TAG, "ON SHOW CONTENT");
            mAddableToCartAdapter = new AddableToCartListAdapter(getBaseActivity(), mAddableToCartList, (OnClickListener) this);
            mAddableToCartGridView.setAdapter(mAddableToCartAdapter);
            showFragmentContentContainer();
        } else {
            Log.i(TAG, "ON SHOW IS EMPTY");
            showEmpty();
        }
    }

    /**
     * Show empty content
     * 
     * @author Andre Lopes
     */
    protected void showEmpty() {
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
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case item
        if (id == R.id.addabletocart_item_container) onItemClick(view);
        // Case delete
        else if (id == R.id.button_delete) onClickDeleteItem(view);
        // Case add to cart
        else if (id == R.id.button_shop) onClickAddToCart(view);
        // Case add all
        else if (id == R.id.button_shop_all) onClickAddAllToCart();
        // Case simple
        else if (id == R.id.button_variant) onClickVariation(view);
        // Case size guide
        else if (id == R.id.dialog_list_size_guide_button) onClickSizeGuide(view);
        // Case continue shopping
        else if (id == R.id.fragment_root_empty_button) onClickContinueShopping();
        // Case unknown
        else Log.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
    }
    
    /**
     * Process the click on size guide.
     * @author sergiopereira
     */
    protected void onClickSizeGuide(View view){        
        try {
            // Get size guide url
            String url = (String) view.getTag();
            // Validate url
            if(!TextUtils.isEmpty(url)) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, url);
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_SIZE_GUIDE, bundle, FragmentController.ADD_TO_BACK_STACK);
            } else Log.w(TAG, "WARNING: SIZE GUIDE URL IS EMPTY");
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
        }
    }

    /**
     * Process the click on continue button
     * 
     * @author Andre Lopes
     */
    protected void onClickContinueShopping() {
        Log.i(TAG, "ON CLICK CONTINUE SHOPPING");
        getBaseActivity().onBackPressed();
    }

    /**
     * Process the click on variation button
     * 
     * @param view
     * @author sergiopereira
     */
    protected void onClickVariation(View view) {
        try {
            // Hide warning
            getBaseActivity().showWarningVariation(false);
            // Show dialog
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = (Favourite) mAddableToCartList.get(position);
            addableToCart.setFavoriteSelected(position);
            showVariantsDialog(addableToCart);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK VARIATION");
        }
    }

    /**
     * Process the click on item
     * 
     * @param view
     * @author sergiopereira
     */
    protected void onItemClick(View view) {
        Log.i(TAG, "ON ITEM CLICK");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = (Favourite) mAddableToCartList.get(position);
            addableToCart.setFavoriteSelected(position);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, addableToCart.getUrl());
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }

    /**
     * Process the click on delete button
     * 
     * @param view
     * @author sergiopereira
     */
    protected void onClickDeleteItem(View view) {
        Log.i(TAG, "ON CLICK DELETE ITEM");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            if (mAddableToCartList == null || mAddableToCartList.isEmpty()) {
                Log.e(TAG, "NULLPOINTER");
                throw new NullPointerException();
            }
            AddableToCart addableToCart = (Favourite) mAddableToCartList.get(position);
            // Remove item from database
            FavouriteTableHelper.removeFavouriteProduct(addableToCart.getSku());
            // Remove from cache
            mAddableToCartList.remove(addableToCart);
            // Update layout
            updateLayoutAfterAction();

            BaseFragment catalogFragment = (BaseFragment) getBaseActivity().getSupportFragmentManager().findFragmentByTag(FragmentType.PRODUCT_LIST.toString());
            if (null != catalogFragment) {
                catalogFragment.sendValuesToFragment(BaseFragment.FRAGMENT_VALUE_REMOVE_FAVORITE, addableToCart.getSku());
            }
            String sku = addableToCart.getSku();
            if(addableToCart.getSelectedSimple() != -1 && addableToCart.getSimples().size() > 0)
                sku = addableToCart.getSimples().get(addableToCart.getSelectedSimple()).getAttributeByKey(RestConstants.JSON_SKU_TAG);
            
            TrackerDelegator.trackRemoveFromFavorites(sku, addableToCart.getPriceForTracking(), addableToCart.getRatingsAverage());

            // Show Toast
            Toast.makeText(getBaseActivity(), getString(R.string.products_removed_favourite), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON DELETE ITEM");
        }
    }

    /**
     * Process the click on add all button
     * 
     * @author sergiopereira
     */
    protected void onClickAddAllToCart() {
        Log.i(TAG, "ON CLICK ADD ALL TO CART");
        try {
            if (validateVariations()) {
                onAddAllItemsToCart();
            } else {
                // Show the warning on header
                getBaseActivity().showWarningVariation(true);
                // Update content
                mAddableToCartAdapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON ADD ALL TO CART");
        }
    }

    /**
     * Process the click on add button
     * 
     * @param view
     * @author sergiopereira
     */
    protected void onClickAddToCart(View view) {
        Log.i(TAG, "ON CLICK ADD ITEM TO CART");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = (Favourite) mAddableToCartList.get(position);
            // Validate variation
            if (hasSelectedVariation(addableToCart)) {
                Log.i(TAG, "SELECTED VARIATION");
                onAddItemToCart(addableToCart, position);
            } else {
                Log.i(TAG, "NOT SELECTED VARIATION");
                mAddableToCartAdapter.notifyDataSetChanged();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING: IOB ON ADD ITEM TO CART", e);
            if(mAddableToCartAdapter != null) mAddableToCartAdapter.notifyDataSetChanged();
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON ADD ITEM TO CART", e);
            view.setEnabled(false);
        }
    }

    /**
     * Validate all items has selected variation
     * 
     * @return boolean
     * @author sergiopereira
     */
    protected boolean validateVariations() {
        boolean result = true;
        // For each item
        for (AddableToCart item : mAddableToCartList) {
            // Check selected variation
            boolean temp = hasSelectedVariation(item);
            // Save result if false
            if (!temp) {
                result = false;
            }
        }
        // Return result
        return result;
    }

    /**
     * Validate if item has a selected varititon
     * 
     * @param item
     * @return boolean
     * @author sergiopereira
     */
    protected boolean hasSelectedVariation(AddableToCart item) {
        Log.d(TAG, "ON VALIDATE VARIATIONS: " + item.hasSimples() + " " + item.getSelectedSimple());
        // Validate if has simples > 1 and has a selected position
        if (item.hasSimples() && item.getSelectedSimple() == AddableToCart.NO_SIMPLE_SELECTED) {
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
     * 
     * @param addableToCart
     * @param position
     * @author sergiopereira
     */
    protected void onAddItemToCart(AddableToCart addableToCart, int position) {
        Log.i(TAG, "ON EXECUTE ADD TO CART");
        // Show progress
        showActivityProgress();
        // Initialize cart vars
        isOnAddingAllItemsToCart = false;
        mAddedItemsCounter = 0;
        mNumberOfItemsForCart = SINGLE_ITEM;
        mItemsNotAddedToCart.clear();
        // Validate items
        if (addableToCart.isComplete()) {
            triggerAddProductToCart(addableToCart, position);
        } else {
            Log.w(TAG, "WARNING ITEM NOT COMPLETED: " + addableToCart.getName());
        }
    }

    /**
     * Add all items to cart if completed
     * 
     * @author sergiopereira
     */
    protected void onAddAllItemsToCart() {
        Log.i(TAG, "ON EXECUTE ADD ALL TO CART");
        // Show progress
        showActivityProgress();
        // Initialize cart vars
        isOnAddingAllItemsToCart = true;
        mAddedItemsCounter = 0;
        mNumberOfItemsForCart = mAddableToCartList.size();
        mItemsNotAddedToCart.clear();
        // Validate all items
        for (int i = 0; i < mNumberOfItemsForCart; i++) {
            if (mAddableToCartList.get(i).isComplete()) {
                // Add item to cart
                triggerAddProductToCart((Favourite) mAddableToCartList.get(i), i);
            } else {
                // Increment counter
                mAddedItemsCounter++;
                Log.w(TAG, "WARNING ITEM NOT COMPLETED: " + i + " " + mAddableToCartList.get(i).getName() + " " + mAddedItemsCounter);
                // Save the position
                if (mItemsNotAddedToCart != null) {
                    mItemsNotAddedToCart.add(i);
                }
                // Case all items are incomplete
                if (mAddedItemsCounter == mNumberOfItemsForCart) {
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
     * 
     * @param addableToCart
     * @param position
     * @author sergiopereira
     */
    protected synchronized void triggerAddProductToCart(AddableToCart addableToCart, int position) {
        triggerAddProductToCart(addableToCart, position, GetShoppingCartAddItemHelper.REMOVE_FAVOURITE_TAG);
    }

    /**
     * Trigger to add an item to cart
     * 
     * @param addableToCart
     * @param position
     * @param keyRemoveTable
     * @author sergiopereira
     */
    protected synchronized void triggerAddProductToCart(AddableToCart addableToCart, int position, String keyRemoveTable) {
        Log.i(TAG, "ON TRIGGER ADD TO CART: " + position);
        ProductSimple simple = getSelectedSimple(addableToCart);
        // Item data
        ContentValues values = new ContentValues();
        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        values.put(GetShoppingCartAddItemHelper.PRODUCT_TAG, addableToCart.getSku());
        values.put(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        values.put(GetShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        bundle.putInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, position);
        bundle.putString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG, addableToCart.getSku());
        bundle.putDouble(GetShoppingCartAddItemHelper.PRODUCT_PRICE_TAG, addableToCart.getPriceForTracking());
        bundle.putDouble(GetShoppingCartAddItemHelper.PRODUCT_RATING_TAG, addableToCart.getRatingsAverage());
        bundle.putBoolean(keyRemoveTable, true);
        // Trigger
        triggerContentEventWithNoLoading(new GetShoppingCartAddItemHelper(), bundle, (IResponseCallback) this);
        // Tracking
        trackAddtoCart(sku, addableToCart);
    }

    /**
     * Track add to cart
     * 
     * @param sku
     * @param addableToCart
     * @author sergiopereira
     */
    protected void trackAddtoCart(String sku, AddableToCart addableToCart) {
        try {
            // Tracking
            Bundle bundle = new Bundle();
            bundle.putString(TrackerDelegator.SKU_KEY, sku);
            bundle.putDouble(TrackerDelegator.PRICE_KEY, addableToCart.getPriceForTracking());
            bundle.putString(TrackerDelegator.NAME_KEY, addableToCart.getName());
            bundle.putString(TrackerDelegator.BRAND_KEY, addableToCart.getBrand());
            bundle.putDouble(TrackerDelegator.RATING_KEY, addableToCart.getRatingsAverage());
            bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, addableToCart.getMaxSavingPercentage());
            bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.WISHLISTPAGE);
            if(null != addableToCart && addableToCart.getCategories().size() > 0){
                bundle.putString(TrackerDelegator.CATEGORY_KEY, addableToCart.getCategories().get(0));
                if(null != addableToCart && addableToCart.getCategories().size() > 1){
                    bundle.putString(TrackerDelegator.SUBCATEGORY_KEY, addableToCart.getCategories().get(1));
                }
            } else {
                bundle.putString(TrackerDelegator.CATEGORY_KEY, "");
            }
            TrackerDelegator.trackProductAddedToCart(bundle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the seleted simple
     * 
     * @param addableToCart
     * @return ProductSimple
     * @author sergiopereira
     */
    protected ProductSimple getSelectedSimple(AddableToCart addableToCart) {
        Log.i(TAG, "ON GET SELECTED SIMPLE: " + addableToCart.getSimples().size());
        // Get item
        int selectedSimple = addableToCart.getSelectedSimple();
        // if (selectedSimple >= addableToCart.getSimples().size()) return null;
        // else if (selectedSimple == AddableToCart.NO_SIMPLE_SELECTED) return null;
        return addableToCart.getSimples().get(selectedSimple);
    }

    /**
     * ######### RESPONSE #########
     */

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON RESPONSE COMPLETE " + getId());
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
            mAddableToCartList = (ArrayList<AddableToCart>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            Log.d(TAG, "NUMBER : " + mAddableToCartList.size());
            // Show content
            showContent();
            
            Bundle params = new Bundle();
            Customer customer = JumiaApplication.CUSTOMER;
            
            params.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
            params.putString(AdjustTracker.CURRENCY_ISO, CurrencyFormatter.getCurrencyCode());
            
            if (null != customer) 
                params.putParcelable(AdjustTracker.CUSTOMER, customer);
            
            params.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
            params.putParcelableArrayList(TrackerDelegator.FAVOURITES_KEY, mAddableToCartList);
            TrackerDelegator.trackViewFavorites(params);         
            
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            // Update counter
            mAddedItemsCounter++;
            // Get data
            int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, -1);
            String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
            Log.i(TAG, "ON RESPONSE COMPLETE: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
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
     * 
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
            int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, AddableToCart.NO_SIMPLE_SELECTED);
            String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
            Log.i(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
            // Save the position
            if (mItemsNotAddedToCart != null) {
                mItemsNotAddedToCart.add(pos);
            }
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
     * 
     * @param bundle
     * @param pos
     * @return int - The string id
     */
    protected int checkTypeError(Bundle bundle, int pos) {
        // Generic error
        int error = R.string.error_please_try_again;
        // Get error code
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        // Validate error
        if (errorCode == ErrorCode.CONNECT_ERROR) {
            Log.i(TAG, "ON RESPONSE ERROR: CONNECT_ERROR");
            error = R.string.error_no_connection;
        } else if (errorCode == ErrorCode.REQUEST_ERROR) {
            HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            // CASE OUT OF STOCK
            if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                Log.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_SOLD_OUT");
                if (mAddableToCartList != null && !mAddableToCartList.isEmpty()) {
                    mAddableToCartList.get(pos).setVariationStockWarning(true);
                }
                // Return string out of stock. Commented due to error already being visible to user.
                //error = R.string.product_outof_stock; 
                error = OUT_OF_STOCK_ERROR;
                
                // CASE ERROR ADDING
            } else if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                Log.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_ERROR_ADDING");
                // Return error add to cart failed
                // error = R.string.error_add_to_cart_failed;
                if (mAddableToCartList != null && !mAddableToCartList.isEmpty()) {
                    mAddableToCartList.get(pos).setVariationStockWarning(true);
                }
                /// Return string out of stock. Commented due to error already being visible to user.
                //error = R.string.product_outof_stock;
                error = OUT_OF_STOCK_ERROR;
                
                // CASE UNKNOWN
            } else {
                Log.i(TAG, "ON RESPONSE ERROR: " + errorCode.toString());
            }
        }
        return error;
    }

    /**
     * Validates the response counter from success or error
     * 
     * @param success
     *            - from respective response
     * @param pos
     *            - current position
     * @param error
     *            - the string id for error
     * @author sergiopereira
     */
    protected void validateResponseCounter(boolean success, int pos, int error) {
        // Update adapter
        if (mAddedItemsCounter == mNumberOfItemsForCart) {
            // Set flag
            isOnAddingAllItemsToCart = false;
            if (!isOnStoppingProcess) {
                // CASE ALL ITEMS, get items not added to cart
                if (mNumberOfItemsForCart > SINGLE_ITEM) {
                    validateItemsNotAddedToCart();
                }
                // CASE ONE ITEM
                else {
                    validateItemWasAddedToCart(success, pos, error);
                }
                // Update layout
                updateLayoutAfterAction();
            }
            // Update cart
            getBaseActivity().updateCartInfo();
        }
    }

    /**
     * Validate the item was added to cart
     * 
     * @param success
     *            - from respective response
     * @param pos
     *            - current position
     * @param error
     *            - the string id for error
     * @author sergiopereira
     */
    protected void validateItemWasAddedToCart(boolean success, int pos, int error) {
        validateItemWasAddedToCart(success, pos, error, getString(R.string.favourite_add_to_cart));
    }

    protected void validateItemWasAddedToCart(boolean success, int pos, int error, String message) {
        // Assumed that was added to cart
        // Case added to cart
        if (success) {
            try{
                mAddableToCartList.remove(pos);
            }catch (IndexOutOfBoundsException  e){
                Log.w(TAG, "IndexOutOfBoundsException avoided when removing position: " + pos);
                message = getString(error);
            }
        }
        // Case not added to cart and no error to display
        else if (error != OUT_OF_STOCK_ERROR){
            message = getString(error);
        }
        // Show toast
        if (!isOnStoppingProcess && error != OUT_OF_STOCK_ERROR) {
            Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate all items were added to cart
     * 
     * @author sergiopereira
     */
    protected void validateItemsNotAddedToCart() {
        // Get the size of items not added
        int errorsSize = mItemsNotAddedToCart.size();
        // Get the size of favourites
        int favSize = mAddableToCartList.size();

        // Case some items not added
        if (errorsSize > 0 && errorsSize != favSize) {
            Log.i(TAG, "SOME ITEMS NOT ADDED TO CART: CREATE NEW ARRAY " + errorsSize + " " + favSize);
            getNotAddedItems();
            Toast.makeText(getBaseActivity(), getString(R.string.some_products_not_added), Toast.LENGTH_SHORT).show();
            // Case all items added
        } else if (errorsSize == 0) {
            Log.i(TAG, "ALL ITEMS ADDED TO CART: CLEAN ARRAY " + errorsSize + " " + favSize);
            mAddableToCartList.clear();
            Toast.makeText(getBaseActivity(), getString(R.string.favourite_addalltocart), Toast.LENGTH_SHORT).show();
            // Case zero items added
        } else {
            Log.i(TAG, "NO ITEMS ADDED TO CART: MANTAIN ARRAY " + errorsSize + " " + favSize);
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get not added items
     * 
     * @author sergiopereira
     */
    protected void getNotAddedItems() {
        Log.i(TAG, "ON GET NOT ADDED ITEMS");
        // Create new array
        ArrayList<AddableToCart> array = new ArrayList<AddableToCart>();
        // Add items not added to cart
        for (Integer pos : mItemsNotAddedToCart) {
            array.add((Favourite) mAddableToCartList.get(pos));
        }
        // Show new items
        mAddableToCartList = array;
        mAddableToCartGridView.setAdapter(null);
        mAddableToCartAdapter = new AddableToCartListAdapter(getBaseActivity(), mAddableToCartList, (OnClickListener) this);
        mAddableToCartGridView.setAdapter(mAddableToCartAdapter);
    }

    /**
     * Update the layout after user action
     * 
     * @author sergiopereira
     */
    protected synchronized void updateLayoutAfterAction() {
        Log.i(TAG, "ON UPDATE LAYOUT AFTER ACTION");
        // Update adapter
        mAddableToCartAdapter.notifyDataSetChanged();
        // Validate current state
        if (mAddableToCartList.isEmpty()) {
            showEmpty();
        }
        
        // Dismiss progress, used because of onClickClearAll() on RecentlyViewedFragment
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideActivityProgress();
            }
        }, 300);
    }

    /**
     * ###### DIALOG ######
     */

    protected final static String VARIATION_PICKER_ID = "variation_picker";

    protected void showVariantsDialog(AddableToCart addableToCart) {
        String title = getString(R.string.product_variance_choose);
        //
        ArrayList<String> mSimpleVariantsAvailable = new ArrayList<String>();
        ArrayList<String> mSimpleVariants = createSimpleVariants(addableToCart, mSimpleVariantsAvailable);
        //
        DialogListFragment dialogListFragment = DialogListFragment.newInstance(this,
                new FavouriteOnDialogListListener(addableToCart),
                VARIATION_PICKER_ID,
                title,
                mSimpleVariants,
                mSimpleVariantsAvailable,
                addableToCart.getSelectedSimple(),
                addableToCart.getSizeGuideUrl());

        dialogListFragment.show(getFragmentManager(), null);
    }

    protected class FavouriteOnDialogListListener implements OnDialogListListener {

        AddableToCart addableToCart;

        public FavouriteOnDialogListListener(AddableToCart addableToCart) {
            this.addableToCart = addableToCart;
        }

        @Override
        public void onDialogListItemSelect(String id, int position, String value) {
            Log.i(TAG, "size selected! onDialogListItemSelect : " + position);
            addableToCart.setChooseVariationWarning(false);
            addableToCart.setVariationStockWarning(false);
            addableToCart.setSelectedSimple(position);
            addableToCart.setSelectedSimpleValue(value);
            mAddableToCartAdapter.notifyDataSetChanged();
        }

    }

    protected ArrayList<String> createSimpleVariants(AddableToCart addableToCart, ArrayList<String> mSimpleVariantsAvailable) {
        Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants" + addableToCart.getName());
        ArrayList<ProductSimple> simples = new ArrayList<ProductSimple>(addableToCart.getSimples());
        ArrayList<String> variations = addableToCart.getKnownVariations();
        if (variations == null || variations.size() == 0) {
            variations = new ArrayList<String>();
            variations.add("size");
            variations.add("color");
            variations.add("variation");
        }
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(addableToCart.getSimples(), variations);

        ArrayList<String> variationValues = new ArrayList<String>();
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants in");
            String value = calcVariationStringForSimple(simple, foundKeys);

            /**
             * TODO: Uncommented to validate the stock
             * 
             * @author sergiopereira
             */
            // String quantity = simple.getAttributeByKey(ProductSimple.QUANTITY_TAG);
            // if (quantity != null && Long.parseLong(quantity) > 0) {
            variationValues.add(value);
            mSimpleVariantsAvailable.add(value);
            // } else {
            // variationValues.add(value);
            // }

        }

        return variationValues;
    }

    protected Set<String> scanSimpleAttributesForKnownVariants(ArrayList<ProductSimple> simples, ArrayList<String> variations) {
        Set<String> foundVariations = new HashSet<String>();
        Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants");
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants in");
            scanSimpleForKnownVariants(simple, foundVariations, variations);
        }
        return foundVariations;
    }

    protected void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations, ArrayList<String> variations) {
        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            Log.i(TAG, "scanSimpleForKnownVariations: variation = " + variation + " attr = " + attr);
            if (attr == null) {
                continue;
            }
            foundVariations.add(variation);
        }
    }

    protected String calcVariationStringForSimple(ProductSimple simple, Set<String> keys) {
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
