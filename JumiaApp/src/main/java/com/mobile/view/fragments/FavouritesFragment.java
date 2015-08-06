package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.AddableToCartListAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.product.AddableToCart;
import com.mobile.newFramework.objects.product.ProductSimple;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class used to show all favourite items from database
 *
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class FavouritesFragment extends BaseFragment implements IResponseCallback, OnClickListener {

    protected final static String TAG = FavouritesFragment.class.getSimpleName();

    protected final static int SINGLE_ITEM = 1;

    protected final static int NO_ERROR = -1;

    protected final static int OUT_OF_STOCK_ERROR = -2;

    protected AddableToCartListAdapter mAddableToCartAdapter;

    protected ArrayList<AddableToCart> mAddableToCartList;

    protected GridView mAddableToCartGridView;

    protected Button mAddAllToCartButton;

    protected int mNumberOfItemsForCart = AddableToCart.NO_SIMPLE_SELECTED;

    protected int mAddedItemsCounter = 0;

    protected ArrayList<Integer> mItemsNotAddedToCart = new ArrayList<>();

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


    protected FavouritesFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int layoutResId, int titleResId, KeyboardState adjust_state) {
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
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Retain the instance to receive callback from add all to cart
        setRetainInstance(true);
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
        mAddableToCartGridView = (GridView) view.findViewById(R.id.favourites_grid);
        // Get add to cart button
        mAddAllToCartButton = (Button) view.findViewById(R.id.button_shop_all);
        mAddAllToCartButton.setOnClickListener(this);
        // Show Loading View
        showFragmentLoading();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
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
        Print.i(TAG, "ON SAVED INSTANCE");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
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
            Print.i(TAG, "ON SHOW CONTENT");
            mAddableToCartAdapter = new AddableToCartListAdapter(getBaseActivity(), mAddableToCartList, this);
            mAddableToCartGridView.setAdapter(mAddableToCartAdapter);
            showFragmentContentContainer();
        } else {
            Print.i(TAG, "ON SHOW IS EMPTY");
            showEmpty();
        }
    }

    /**
     * Show empty content
     *
     * @author Andre Lopes
     */
    protected void showEmpty() {
        getBaseActivity().warningFactory.hideWarning();
        mAddAllToCartButton.setVisibility(View.GONE);
        mAddAllToCartButton.setOnClickListener(null);
        showErrorFragment(ErrorLayoutFactory.NO_FAVOURITES_LAYOUT, this);
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
        // Case unknown
        else Print.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
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
            } else Print.w(TAG, "WARNING: SIZE GUIDE URL IS EMPTY");
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
        }
    }

//    /**
//     * Process the click on continue button
//     *
//     * @author Andre Lopes
//     */
//    protected void onClickContinueShopping() {
//        Print.i(TAG, "ON CLICK CONTINUE SHOPPING");
//        getBaseActivity().onBackPressed();
//    }

    /**
     * Process the click on variation button
     *
     * @param view
     * @author sergiopereira
     */
    protected void onClickVariation(View view) {
        try {
            // Hide warning
            getBaseActivity().warningFactory.hideWarning();
            // Show dialog
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = mAddableToCartList.get(position);
            addableToCart.setFavoriteSelected(position);
            showVariantsDialog(addableToCart);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK VARIATION");
        }
    }

    /**
     * Process the click on item
     *
     * @param view
     * @author sergiopereira
     */
    protected void onItemClick(View view) {
        Print.i(TAG, "ON ITEM CLICK");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = mAddableToCartList.get(position);
            addableToCart.setFavoriteSelected(position);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, addableToCart.getUrl());
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }

    /**
     * Process the click on delete button
     *
     * @param view
     * @author sergiopereira
     */
    protected void onClickDeleteItem(View view) {
        Print.i(TAG, "ON CLICK DELETE ITEM");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            if (mAddableToCartList == null || mAddableToCartList.isEmpty()) {
                Print.e(TAG, "NULLPOINTER");
                throw new NullPointerException();
            }
            AddableToCart addableToCart = mAddableToCartList.get(position);
            // Remove from cache
            mAddableToCartList.remove(addableToCart);
            // Update layout
            updateLayoutAfterAction();

            String sku = addableToCart.getSku();
            if(addableToCart.getSelectedSimple() != -1 && addableToCart.getSimples().size() > 0)
                sku = addableToCart.getSimples().get(addableToCart.getSelectedSimple()).getAttributeByKey(RestConstants.JSON_SKU_TAG);

            // Show Toast
            Toast.makeText(getBaseActivity(), getString(R.string.products_removed_favourite), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON DELETE ITEM");
        }
    }

    /**
     * Process the click on add all button
     *
     * @author sergiopereira
     */
    protected void onClickAddAllToCart() {
        Print.i(TAG, "ON CLICK ADD ALL TO CART");
        try {
            if (validateVariations()) {
                addAllItemsToCart();
            } else {
                // Show the warning on header
                getBaseActivity().warningFactory.showWarning(WarningFactory.CHOOSE_ONE_SIZE);
                // Update content
                mAddableToCartAdapter.notifyDataSetChanged();
            }
        } catch (NullPointerException | IllegalStateException e) {
            Print.e(TAG, "WARNING: EXCEPTION ON ADD ALL TO CART", e);
            getBaseActivity().warningFactory.showWarning(WarningFactory.PROBLEM_FETCHING_DATA);
        }
    }

    /**
     * Process the click on add button
     *
     * @param view
     * @author sergiopereira
     */
    protected void onClickAddToCart(View view) {
        Print.i(TAG, "ON CLICK ADD ITEM TO CART");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            AddableToCart addableToCart = mAddableToCartList.get(position);
            // Validate variation
            if (hasSelectedVariation(addableToCart)) {
                Print.i(TAG, "SELECTED VARIATION");
                onAddItemToCart(addableToCart, position);
            } else {
                Print.i(TAG, "NOT SELECTED VARIATION");
                mAddableToCartAdapter.notifyDataSetChanged();
            }
        } catch (IndexOutOfBoundsException e) {
            Print.w(TAG, "WARNING: IOB ON ADD ITEM TO CART", e);
            if(mAddableToCartAdapter != null) mAddableToCartAdapter.notifyDataSetChanged();
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON ADD ITEM TO CART", e);
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
     * Validate if item has a selected variation
     *
     * @param item
     * @return boolean
     * @author sergiopereira
     */
    protected boolean hasSelectedVariation(AddableToCart item) {
        Print.d(TAG, "ON VALIDATE VARIATIONS: " + item.hasSimples() + " " + item.getSelectedSimple());
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
        Print.i(TAG, "ON EXECUTE ADD TO CART");
        // Show progress
        showActivityProgress();
        // Initialize cart vars
        mAddedItemsCounter = 0;
        mNumberOfItemsForCart = SINGLE_ITEM;
        mItemsNotAddedToCart.clear();
        // Validate items
        if (addableToCart.isComplete()) {
            triggerAddProductToCart(addableToCart, position);
        } else {
            Print.w(TAG, "WARNING ITEM NOT COMPLETED: " + addableToCart.getName());
        }
    }

    /**
     * Add all items to cart if completed
     *
     *
     */
    protected void addAllItemsToCart() throws IllegalStateException{
        Print.i(TAG, "ON EXECUTE ADD ALL TO CART");
        // Initialize cart vars

        mNumberOfItemsForCart = mAddableToCartList.size();
        HashMap<String, String> productBySku = new HashMap<>();

        for (int i = 0; i < mNumberOfItemsForCart; i++) {
            AddableToCart addableToCart = mAddableToCartList.get(i);
            if (addableToCart.isComplete()) {
                ProductSimple simple = getSelectedSimple(addableToCart);
                String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);

                productBySku.put(sku, addableToCart.getSku());
            }

        }

        if (productBySku.size() != 0) {
            showActivityProgress();
            triggerAddAllItems(productBySku);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Add all items to cart if completed
     * Deprecated, use {@link #addAllItemsToCart()} instead.
     * @author sergiopereira
     *
     */
//    @Deprecated
//    protected void onAddAllItemsToCart() {
//        Print.i(TAG, "ON EXECUTE ADD ALL TO CART");
//         Show progress
//        showActivityProgress();
//         Initialize cart vars
//        mAddedItemsCounter = 0;
//        mNumberOfItemsForCart = mAddableToCartList.size();
//        mItemsNotAddedToCart.clear();
        // Validate all items
//        for (int i = 0; i < mNumberOfItemsForCart; i++) {
//            if (mAddableToCartList.get(i).isComplete()) {
                // Add item to cart
//                triggerAddProductToCart(mAddableToCartList.get(i), i);
//            } else {
//                 Increment counter
//                mAddedItemsCounter++;
//                Print.w(TAG, "WARNING ITEM NOT COMPLETED: " + i + " " + mAddableToCartList.get(i).getName() + " " + mAddedItemsCounter);
                // Save the position
//                if (mItemsNotAddedToCart != null) {
//                    mItemsNotAddedToCart.add(i);
//                }
                // Case all items are incomplete
//                if (mAddedItemsCounter == mNumberOfItemsForCart) {
//                     Show toast
//                    Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
                    // Dismiss
//                    hideActivityProgress();
//                }
//            }
//        }
//    }

    /**
     * ######### TRIGGERS #########
     */

    /**
     * Trigger to add all items to cart
     *
     * @author ricardo
     */
    private void triggerAddAllItems(HashMap<String, String> values) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShoppingCartAddMultipleItemsHelper.ADD_ITEMS, values);
        // Trigger
        triggerContentEventNoLoading(new ShoppingCartAddMultipleItemsHelper(), bundle, this);
    }

    /**
     * Trigger to add an item to cart
     *
     * @param addableToCart
     * @param position
     * @author sergiopereira
     */
    protected synchronized void triggerAddProductToCart(AddableToCart addableToCart, int position) {
        triggerAddProductToCart(addableToCart, position, ShoppingCartAddItemHelper.REMOVE_FAVOURITE_TAG);
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
        Print.i(TAG, "ON TRIGGER ADD TO CART: " + position);
        ProductSimple simple = getSelectedSimple(addableToCart);
        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        // Item data
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_TAG, addableToCart.getSku());
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        values.put(ShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putInt(ShoppingCartAddItemHelper.PRODUCT_POS_TAG, position);
        bundle.putString(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, addableToCart.getSku());
        bundle.putDouble(ShoppingCartAddItemHelper.PRODUCT_PRICE_TAG, addableToCart.getPriceForTracking());
        bundle.putDouble(ShoppingCartAddItemHelper.PRODUCT_RATING_TAG, addableToCart.getRatingsAverage());
        bundle.putBoolean(keyRemoveTable, true);
        // Trigger
        triggerContentEventNoLoading(new ShoppingCartAddItemHelper(), bundle, this);
        // Tracking
        trackAddToCart(sku, addableToCart);
    }

    /**
     * Track add to cart
     *
     * @param sku
     * @param addableToCart
     * @author sergiopereira
     */
    protected void trackAddToCart(String sku, AddableToCart addableToCart) {
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
            if (null != addableToCart && addableToCart.getCategories().size() > 0){
                int categoriesSize = addableToCart.getCategories().size();
                bundle.putString(TrackerDelegator.CATEGORY_KEY, addableToCart.getCategories().get(categoriesSize - 1));
                if (null != addableToCart && categoriesSize > 1) {
                    bundle.putString(TrackerDelegator.SUBCATEGORY_KEY, addableToCart.getCategories()
                            .get(categoriesSize - 2));
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
        Print.i(TAG, "ON GET SELECTED SIMPLE: " + addableToCart.getSimples().size());
        // Get item
        int selectedSimple = addableToCart.getSelectedSimple();
//         if (selectedSimple >= addableToCart.getSimples().size()) return null;
//         else if (selectedSimple == AddableToCart.NO_SIMPLE_SELECTED) return null;
        return addableToCart.getSimples().get(selectedSimple);
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
        case GET_FAVOURITE_LIST:
            Print.i(TAG, "ON RESPONSE COMPLETE: GET_FAVOURITE_LIST");
            mAddableToCartList = (ArrayList<AddableToCart>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            //Print.d(TAG, "NUMBER : " + mAddableToCartList.size());
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
            int pos = bundle.getInt(ShoppingCartAddItemHelper.PRODUCT_POS_TAG, -1);
            String sku = bundle.getString(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
            Print.i(TAG, "ON RESPONSE COMPLETE: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
            // Validate current counter
            validateResponseCounter(true, pos, NO_ERROR);
            break;
        case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
            onAddItemsToShoppingCartRequestSuccess(bundle);
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

        if(super.handleErrorEvent(bundle)){
            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            hideActivityProgress();
            return;
        }

        // Validate type
        switch (eventType) {
        case GET_FAVOURITE_LIST:
            Print.d(TAG, "ON RESPONSE ERROR: GET_FAVOURITE_LIST");
            showFragmentContentContainer();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Print.d(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT");
            // Inc counter
            mAddedItemsCounter++;
            // Get item set stock error
            int pos = bundle.getInt(ShoppingCartAddItemHelper.PRODUCT_POS_TAG, AddableToCart.NO_SIMPLE_SELECTED);
            String sku = bundle.getString(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
            Print.i(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
            // Save the position
            if (mItemsNotAddedToCart != null) {
                mItemsNotAddedToCart.add(pos);
            }
            // Check type error is out of stock
            int error = checkTypeError(bundle, pos);
            // Validate current counter
            validateResponseCounter(false, pos, error);
        case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
            onAddItemsToShoppingCartRequestError(bundle);
            break;
        default:
            Print.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
    }

    /**
     *
     * @author ricardo
     */
    private void onAddItemsToShoppingCartRequestError(Bundle bundle){
        hideActivityProgress();
        if (bundle.containsKey(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY)) {
            ArrayList<String> notAdded = bundle.getStringArrayList(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            if (!CollectionUtils.isEmpty(notAdded)) {
                if (notAdded.size() == 1) {
                    ToastFactory.ERROR_PRODUCT_OUT_OF_STOCK.show(getBaseActivity());
                } else if (notAdded.size() > 1) {
                    ToastFactory.ERROR_ADDED_TO_CART.show(getBaseActivity());
                }
            }
        } else {
            ToastFactory.ERROR_UNEXPECTED_PLEASE_RETRY.show(getBaseActivity());
        }
    }

    /**
     *
     * @author ricardo
     */
    private void onAddItemsToShoppingCartRequestSuccess(Bundle bundle){
        hideActivityProgress();

        if (bundle.containsKey(Constants.BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY)) {
            ArrayList<String> added = bundle.getStringArrayList(Constants.BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY);

            for (String aSku : added) {
                int index = -1;
                for(int i = 0; i<mAddableToCartList.size(); i++){
                    if(mAddableToCartList.get(i).getSku().equals(aSku)){
                        index = i;
                    }
                }
                if(index != -1) {
                    mAddableToCartList.remove(index);
                }
            }
        }

        if (bundle.containsKey(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY)) {
            ArrayList<String> notAdded = bundle.getStringArrayList(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

            if (!CollectionUtils.isEmpty(notAdded)) {
                if (notAdded.size() == 1) {
                    ToastFactory.ERROR_PRODUCT_OUT_OF_STOCK.show(getBaseActivity());
                } else if (notAdded.size() > 1) {
                    ToastFactory.ERROR_ADDED_TO_CART.show(getBaseActivity());
                }
            }
        }
        updateLayoutAfterAction();
        getBaseActivity().updateCartInfo();
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
            Print.i(TAG, "ON RESPONSE ERROR: CONNECT_ERROR");
            error = R.string.error_no_connection;
        } else if (errorCode == ErrorCode.REQUEST_ERROR) {
            HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            // CASE OUT OF STOCK
            if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                Print.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_SOLD_OUT");
                if (mAddableToCartList != null && !mAddableToCartList.isEmpty()) {
                    mAddableToCartList.get(pos).setVariationStockWarning(true);
                }
                // Return string out of stock. Commented due to error already being visible to user.
                // error = R.string.product_outof_stock;
                error = OUT_OF_STOCK_ERROR;

                // CASE ERROR ADDING
            } else if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                Print.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_ERROR_ADDING");
                // Return error add to cart failed
                // error = R.string.error_add_to_cart_failed;
                if (mAddableToCartList != null && !mAddableToCartList.isEmpty()) {
                    mAddableToCartList.get(pos).setVariationStockWarning(true);
                }
                /// Return string out of stock. Commented due to error already being visible to user.
                // error = R.string.product_outof_stock;
                error = OUT_OF_STOCK_ERROR;

                // CASE UNKNOWN
            } else {
                Print.i(TAG, "ON RESPONSE ERROR: " + errorCode.toString());
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
            // Validate fragment state
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
            try {
                mAddableToCartList.remove(pos);
            } catch (IndexOutOfBoundsException e) {
                Print.w(TAG, "IndexOutOfBoundsException avoided when removing position: " + pos);
                message = getString(error);
            }
        }
        // Case not added to cart and no error to display
        else if (error != OUT_OF_STOCK_ERROR) {
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
            Print.i(TAG, "SOME ITEMS NOT ADDED TO CART: CREATE NEW ARRAY " + errorsSize + " " + favSize);
            getNotAddedItems();
            Toast.makeText(getBaseActivity(), getString(R.string.some_products_not_added), Toast.LENGTH_SHORT).show();
            // Case all items added
        } else if (errorsSize == 0) {
            Print.i(TAG, "ALL ITEMS ADDED TO CART: CLEAN ARRAY " + errorsSize + " " + favSize);
            mAddableToCartList.clear();
            Toast.makeText(getBaseActivity(), getString(R.string.favourite_addalltocart), Toast.LENGTH_SHORT).show();
            // Case zero items added
        } else {
            Print.i(TAG, "NO ITEMS ADDED TO CART: MANTAIN ARRAY " + errorsSize + " " + favSize);
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get not added items
     *
     * @author sergiopereira
     */
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
     * Update the layout after user action
     *
     * @author sergiopereira
     */
    protected synchronized void updateLayoutAfterAction() {
        Print.i(TAG, "ON UPDATE LAYOUT AFTER ACTION");
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
        ArrayList<String> mSimpleVariantsAvailable = new ArrayList<>();
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
        public void onDialogListItemSelect(int position, String value) {
            Print.i(TAG, "size selected! onDialogListItemSelect : " + position);
            addableToCart.setChooseVariationWarning(false);
            addableToCart.setVariationStockWarning(false);
            addableToCart.setSelectedSimple(position);
            addableToCart.setSelectedSimpleValue(value);
            mAddableToCartAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDismiss() {
        }

    }

    protected ArrayList<String> createSimpleVariants(AddableToCart addableToCart, ArrayList<String> mSimpleVariantsAvailable) {
        Print.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants" + addableToCart.getName());
        ArrayList<ProductSimple> simples = new ArrayList<>(addableToCart.getSimples());
        ArrayList<String> variations = addableToCart.getKnownVariations();
        if (variations == null || variations.size() == 0) {
            variations = new ArrayList<>();
            variations.add("size");
            variations.add("color");
            variations.add("variation");
        }
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(addableToCart.getSimples(), variations);

        ArrayList<String> variationValues = new ArrayList<>();
        for (ProductSimple simple : simples) {
            Print.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants in");
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
        Set<String> foundVariations = new HashSet<>();
        Print.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants");
        for (ProductSimple simple : simples) {
            Print.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants in");
            scanSimpleForKnownVariants(simple, foundVariations, variations);
        }
        return foundVariations;
    }

    protected void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations, ArrayList<String> variations) {
        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            Print.i(TAG, "scanSimpleForKnownVariations: variation = " + variation + " attr = " + attr);
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
