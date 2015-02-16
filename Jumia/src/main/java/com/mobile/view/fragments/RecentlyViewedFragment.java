/**
 * 
 */
package com.mobile.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.mobile.components.customfontviews.Button;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.AddableToCartListAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.database.LastViewedTableHelper;
import com.mobile.framework.objects.AddableToCart;
import com.mobile.framework.objects.LastViewedAddableToCart;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.GetShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetRecentlyViewedHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show all last viewed items from database
 * 
 * @author Andre Lopes
 */
public class RecentlyViewedFragment extends FavouritesFragment implements IResponseCallback, OnClickListener {

    protected final static String TAG = LogTagHelper.create(RecentlyViewedFragment.class);

    private static RecentlyViewedFragment mRecentlyViewedFragment;

    private Button mClearAllButton;

    /**
     * Empty constructor
     */
    public RecentlyViewedFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.RecentlyView,
                R.layout.recentlyviewed,
                R.string.recently_viewed,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /**
     * Get a new instance
     * 
     * @return RecentlyViewedFragment
     */
    public static RecentlyViewedFragment getInstance() {
        mRecentlyViewedFragment = new RecentlyViewedFragment();
        return mRecentlyViewedFragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Set the default value
        isOnAddingAllItemsToCart = false;
        // Retain the instance to receive callbacks from add all to cart
        setRetainInstance(true);
        // Track page name
        super.mPageName = TrackingPage.RECENTLY_VIEWED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get grid view
        mAddableToCartGridView = (GridView) view.findViewById(R.id.recentlyviewed_grid);
        // Get clear all button
        mClearAllButton = (Button) view.findViewById(R.id.recentlyviewed_button_grey);
        mClearAllButton.setOnClickListener((OnClickListener) this);
        // Get add to cart button
        mAddAllToCartButton = (Button) view.findViewById(R.id.button_shop_all);
        //mAddAllToCartButton.setOnClickListener((OnClickListener) this);
        mAddAllToCartButton.setVisibility(View.GONE);

        // Validate current state
        if (isOnAddingAllItemsToCart) {
            // Show progress
            Log.i(TAG, "IS ON ADDING ALL ITEMS");
            showActivityProgress();
        } else {
            // show Loading View
            showFragmentLoading();
            // Get RecentlyViewed
            Log.i(TAG, "LOAD LAST VIEWED ITEMS");
            new GetRecentlyViewedHelper((IResponseCallback) this);
        }
    }

    /**
     * ######### LAYOUTS #########
     */

    /**
     * Show empty content
     * 
     * @author Andre Lopes
     */
    protected void showEmpty() {
        getBaseActivity().showWarningVariation(false);
        mClearAllButton.setVisibility(View.GONE);
        mClearAllButton.setOnClickListener(null);
        mAddAllToCartButton.setVisibility(View.GONE);
        mAddAllToCartButton.setOnClickListener(null);
        showFragmentEmpty(R.string.recentlyview_no_searches, R.drawable.img_norecentview, R.string.continue_shopping, (OnClickListener) this);
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
        // Case add to cart
        else if (id == R.id.button_shop) onClickAddToCart(view);
        // Case clear all
        else if (id == R.id.recentlyviewed_button_grey) onClickClearAll();
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

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.FavouritesFragment#onClickSizeGuide(android.view.View)
     */
    @Override
    protected void onClickSizeGuide(View view) {
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
     * Process the click on variation button
     * 
     * @param view
     * @author sergiopereira
     */
    @Override
    protected void onClickVariation(View view) {
        try {
            // Hide warning
            getBaseActivity().showWarningVariation(false);
            // Show dialog
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = (LastViewedAddableToCart) mAddableToCartList.get(position);
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
    @Override
    protected void onItemClick(View view) {
        Log.i(TAG, "ON ITEM CLICK");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = (LastViewedAddableToCart) mAddableToCartList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, addableToCart.getUrl());
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }

    /**
     * Process the click on clear all button
     * 
     * @author sergiopereira
     */
    private void onClickClearAll() {
        Log.i(TAG, "ON CLICK CLEAR ALL ITEMS");
        // Show progress
        showActivityProgress();

        LastViewedTableHelper.deleteAllLastViewed();
        // needed to update mRecentSearchesAdapter
        for (int i = mAddableToCartList.size() - 1; i >= 0; i--) {
            mAddableToCartList.remove(i);
        }

        updateLayoutAfterAction();
    }

    /**
     * Process the click on add button
     * 
     * @param view
     * @author sergiopereira
     */
    @Override
    protected void onClickAddToCart(View view) {
        Log.i(TAG, "ON CLICK ADD ALL TO CART");
        int position = Integer.parseInt(view.getTag().toString());
        AddableToCart addableToCart = (LastViewedAddableToCart) mAddableToCartList.get(position);
        // Validate variation
        if (hasSelectedVariation(addableToCart)) {
            Log.i(TAG, "SELECTED VARIATION");
            onAddItemToCart(addableToCart, position);
        } else {
            Log.i(TAG, "NOT SELECTED VARIATION");
            mAddableToCartAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Add all items to cart if completed
     * 
     * @author sergiopereira
     */
    @Override
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
                triggerAddProductToCart((LastViewedAddableToCart) mAddableToCartList.get(i), i);
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
                    // Set flag as default
                    isOnAddingAllItemsToCart = false;
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
    @Override
    protected synchronized void triggerAddProductToCart(AddableToCart addableToCart, int position) {
        triggerAddProductToCart(addableToCart, position, GetShoppingCartAddItemHelper.REMOVE_RECENTLYVIEWED_TAG);
    }

    /**
     * Track add to cart
     * 
     * @param sku
     * @param addableToCart
     * @author sergiopereira
     */
    @Override
    protected void trackAddtoCart(String sku, AddableToCart addableToCart) {
        super.trackAddtoCart(sku, addableToCart);
    }

    /**
     * ######### RESPONSE #########
     */

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
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
        case GET_RECENLTLYVIEWED_LIST:
            Log.i(TAG, "ON RESPONSE COMPLETE: GET_RECENLTLYVIEWED_LIST");
            mAddableToCartList = (ArrayList<AddableToCart>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            Log.d(TAG, "NUMBER : " + mAddableToCartList.size());
            // Show content
            showContent();
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
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
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
        case GET_RECENLTLYVIEWED_LIST:
            Log.d(TAG, "ON RESPONSE ERROR: GET_RECENLTLYVIEWED_LIST");
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
    @Override
    protected void validateItemWasAddedToCart(boolean success, int pos, int error) {
        validateItemWasAddedToCart(success, pos, error, getString(R.string.added_to_shop_cart_dialog_text));
    }

    /**
     * Get not added items
     * 
     * @author sergiopereira
     */
    @Override
    protected void getNotAddedItems() {
        Log.i(TAG, "ON GET NOT ADDED ITEMS");
        // Create new array
        ArrayList<AddableToCart> array = new ArrayList<AddableToCart>();
        // Add items not added to cart
        for (Integer pos : mItemsNotAddedToCart) {
            array.add((LastViewedAddableToCart) mAddableToCartList.get(pos));
        }
        // Show new items
        mAddableToCartList = array;
        mAddableToCartGridView.setAdapter(null);
        mAddableToCartAdapter = new AddableToCartListAdapter(getBaseActivity(), mAddableToCartList, (OnClickListener) this);
        mAddableToCartGridView.setAdapter(mAddableToCartAdapter);
    }
}
