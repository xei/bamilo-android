/**
 *
 */
package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.mobile.components.customfontviews.Button;
import com.mobile.controllers.AddableToCartListAdapter;
import com.mobile.framework.database.LastViewedTableHelper;
import com.mobile.framework.objects.AddableToCart;
import com.mobile.framework.output.Print;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.GetShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetRecentlyViewedHelper;
import com.mobile.helpers.products.ValidateProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show all last viewed items from database
 *
 * @author Andre Lopes
 */
public class RecentlyViewedFragment extends FavouritesFragment implements IResponseCallback {

    protected final static String TAG = LogTagHelper.create(RecentlyViewedFragment.class);

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
        return new RecentlyViewedFragment();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
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
        Print.i(TAG, "ON VIEW CREATED");
        // Get grid view
        mAddableToCartGridView = (GridView) view.findViewById(R.id.recentlyviewed_grid);
        // Get clear all button
        mClearAllButton = (Button) view.findViewById(R.id.recentlyviewed_button_grey);
        mClearAllButton.setOnClickListener(this);
        // Get add to cart button
        mAddAllToCartButton = (Button) view.findViewById(R.id.button_shop_all);
        mAddAllToCartButton.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Show Loading View
        showFragmentLoading();
        // Get RecentlyViewed
        Print.i(TAG, "LOAD LAST VIEWED ITEMS");
        new GetRecentlyViewedHelper(this);

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
        getBaseActivity().warningFactory.hideWarning();
        mClearAllButton.setVisibility(View.GONE);
        mClearAllButton.setOnClickListener(null);
        mAddAllToCartButton.setVisibility(View.GONE);
        mAddAllToCartButton.setOnClickListener(null);
        showFragmentEmpty(R.string.recentlyview_no_searches, R.drawable.img_norecentview, R.string.continue_shopping, this);
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
        super.onClick(view);
        // Get view id
        int id = view.getId();

        if (id == R.id.recentlyviewed_button_grey){
            onClickClearAll();
        } else {
            // Case unknown
            Print.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
        }
    }

    /**
     * Process the click on clear all button
     *
     * @author sergiopereira
     */
    private void onClickClearAll() {
        Print.i(TAG, "ON CLICK CLEAR ALL ITEMS");
        // Show progress
        showActivityProgress();
        // Delete all items in database
        LastViewedTableHelper.deleteAllLastViewed();
        // needed to update mRecentSearchesAdapter
        for (int i = mAddableToCartList.size() - 1; i >= 0; i--) {
            mAddableToCartList.remove(i);
        }

        updateLayoutAfterAction();
    }

    /**
     * Add all items to cart if completed
     *
     * @author sergiopereira
     */
    @Override
    protected void onAddAllItemsToCart() {
        Print.i(TAG, "ON EXECUTE ADD ALL TO CART");
        // Show progress
        showActivityProgress();
        // Initialize cart vars
        mAddedItemsCounter = 0;
        mNumberOfItemsForCart = mAddableToCartList.size();
        mItemsNotAddedToCart.clear();
        // Validate all items
        for (int i = 0; i < mNumberOfItemsForCart; i++) {
            if (mAddableToCartList.get(i).isComplete()) {
                // Add item to cart
                triggerAddProductToCart(mAddableToCartList.get(i), i);
            } else {
                // Increment counter
                mAddedItemsCounter++;
                Print.w(TAG, "WARNING ITEM NOT COMPLETED: " + i + " " + mAddableToCartList.get(i).getName() + " " + mAddedItemsCounter);
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
        Print.i(TAG, "ON RESPONSE COMPLETE " + getId());
        // Get event type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate the current state
        if (isOnStoppingProcess && eventType != EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT) {
            Print.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate the event type
        switch (eventType) {
            case GET_RECENLTLY_VIEWED_LIST:
                Print.i(TAG, "ON RESPONSE COMPLETE: GET_RECENLTLY_VIEWED_LIST");
                mAddableToCartList = (ArrayList<AddableToCart>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
                Print.d(TAG, "NUMBER : " + mAddableToCartList.size());
                if (!CollectionUtils.isEmpty(mAddableToCartList)) {
                    triggerValidateRecentlyViewed(mAddableToCartList);
                } else {
                    Print.i(TAG, "ON SHOW IS EMPTY");
                    showEmpty();
                }
                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                // Update counter
                mAddedItemsCounter++;
                // Get data
                int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, -1);
                String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
                Print.i(TAG, "ON RESPONSE COMPLETE: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
                // Validate current counter
                validateResponseCounter(true, pos, NO_ERROR);
                break;
            case VALIDATE_PRODUCTS:
                mAddableToCartList = (ArrayList<AddableToCart>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);

                if (!CollectionUtils.isEmpty(mAddableToCartList)) {
                    showContent();
                } else {
                    showEmpty();
                }
                //USING AN ASYNC TASK
//            new updateRecentViewedDatabaseTask().execute(mAddableToCartList);
                break;
            default:
                Print.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
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
        Print.i(TAG, "ON ERROR RESPONSE");
        // Get type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate the current state
        if (isOnStoppingProcess && eventType != EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT) {
            Print.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }

        if (super.handleErrorEvent(bundle)) {
            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            hideActivityProgress();
            return;
        }

        // Validate type
        switch (eventType) {
            case GET_RECENLTLY_VIEWED_LIST:
                Print.d(TAG, "ON RESPONSE ERROR: GET_RECENTLY_VIEWED_LIST");
                showFragmentContentContainer();
                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                Print.d(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT");
                // Inc counter
                mAddedItemsCounter++;
                // Get item set stock error
                int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, AddableToCart.NO_SIMPLE_SELECTED);
                String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
                Print.i(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
                // Save the position
                if (mItemsNotAddedToCart != null) {
                    mItemsNotAddedToCart.add(pos);
                }
                // Check type error is out of stock
                int error = checkTypeError(bundle, pos);
                // Validate current counter
                validateResponseCounter(false, pos, error);
                break;
            case VALIDATE_PRODUCTS:
                Print.d(TAG, "ON RESPONSE ERROR: VALIDATE_PRODUCTS");
                showContent();
                break;
            default:
                Print.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
                break;
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        if (!CollectionUtils.isEmpty(mAddableToCartList)) {
            triggerValidateRecentlyViewed(mAddableToCartList);
        } else {
            showEmpty();
        }
    }

    /**
     * Validate the item was added to cart
     *
     * @param success - from respective response
     * @param pos     - current position
     * @param error   - the string id for error
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
        Print.i(TAG, "ON GET NOT ADDED ITEMS");
        // Create new array
        ArrayList<AddableToCart> array = new ArrayList<>();
        // Add items not added to cart
        for (Integer pos : mItemsNotAddedToCart) {
            array.add(mAddableToCartList.get(pos));
        }
        // Show new items
        mAddableToCartList = array;
        mAddableToCartGridView.setAdapter(null);
        mAddableToCartAdapter = new AddableToCartListAdapter(getBaseActivity(), mAddableToCartList, this);
        mAddableToCartGridView.setAdapter(mAddableToCartAdapter);
    }

    /**
     * send a array of products sku's to be validated by the API
     *
     * @param addableToCartList
     */
    private void triggerValidateRecentlyViewed(ArrayList<AddableToCart> addableToCartList) {
        ContentValues values = new ContentValues();

        for (int i = 0; i < addableToCartList.size(); i++) {
            values.put(String.format(ValidateProductHelper.VALIDATE_PRODUCTS_KEY, i), addableToCartList.get(i).getSku());
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelable(Constants.CONTENT_VALUES, values);

        triggerContentEvent(new ValidateProductHelper(), bundle, this);
    }

}
