package com.mobile.view.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.WishListAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.mobile.helpers.wishlist.GetWishListHelper;
import com.mobile.helpers.wishlist.RemoveFromWishListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.ToastManager;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * Catalog fragment.
 *
 * @author sergiopereira
 */
public class WishListFragment extends BaseFragment implements IResponseCallback, DialogSimpleListFragment.OnDialogListListener {

    private static final String TAG = WishListFragment.class.getSimpleName();

    private GridView mListView;

    private int mSelectedPositionToDelete = -1;

    /**
     * Create and return a new instance.
     */
    public static WishListFragment getInstance() {
        return new WishListFragment();
    }

    /**
     * Empty constructor
     */
    public WishListFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Favorite,
                R.layout._def_wishlist_fragment,
                R.string.favourites,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Get data from saved instance
        if (savedInstanceState != null) {
            Log.i(TAG, "SAVED STATE: " + savedInstanceState.toString());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // List view
        mListView = (GridView) view.findViewById(R.id.wish_list_grid);
        // Add
        view.findViewById(R.id.button_shop_all).setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // Validate customer is logged in
        if (JumiaApplication.isCustomerLoggedIn()) {
            triggerGetWishListInitialPage();
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }


    /**
     * Show content after get items
     *
     * @author sergiopereira
     */
    protected void showContent(ArrayList<ProductMultiple> wishList) {
        // Validate favourites
        if (CollectionUtils.isNotEmpty(wishList)) {
            Print.i(TAG, "ON SHOW CONTENT");
            WishListAdapter listAdapter = new WishListAdapter(getBaseActivity(), wishList, this);
            mListView.setAdapter(listAdapter);
            showFragmentContentContainer();
        } else {
            Print.i(TAG, "ON SHOW IS EMPTY");
            showErrorFragment(ErrorLayoutFactory.NO_FAVOURITES_LAYOUT, this);
        }
    }

    /**
     * Method used to remove the selected position.
     */
    private void removeSelectedPosition() {
        try {
            ProductMultiple item = ((WishListAdapter) mListView.getAdapter()).getItem(mSelectedPositionToDelete);
            ((WishListAdapter) mListView.getAdapter()).remove(item);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Log.i(TAG, "WARNING: EXCEPTION ON REMOVE SELECTED POSITION: " + mSelectedPositionToDelete);
        }
    }

    /*
     * ############## LISTENERS ##############
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Log.i(TAG, "ON CLICK");
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
        else super.onClick(view);

    }

    /**
     * Process the click on size guide.
     *
     * @author sergiopereira
     */
    protected void onClickSizeGuide(View view) {
        try {
            // Get size guide url
            String url = (String) view.getTag();
            // Validate url
            if (!TextUtils.isEmpty(url)) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, url);
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_SIZE_GUIDE, bundle, FragmentController.ADD_TO_BACK_STACK);
            } else Print.w(TAG, "WARNING: SIZE GUIDE URL IS EMPTY");
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
        }
    }

    /**
     * Process the click on variation button.
     */
    protected void onClickVariation(View view) {
        try {
            int position = (int) view.getTag(R.id.target_position);
            ProductMultiple product = ((WishListAdapter) mListView.getAdapter()).getItem(position);
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    getString(R.string.product_variance_choose),
                    product,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
        }
    }

    @Override
    public void onDialogListItemSelect(int position) {
        // Update the recently adapter
        ((WishListAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onDialogListClickView(View view) {
        // Process the click in the main method
        onClick(view);
    }

    /**
     * Process the click on item
     */
    protected void onItemClick(View view) {
        Print.i(TAG, "ON ITEM CLICK");
        String sku = (String) view.getTag(R.id.target_sku);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, sku);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on delete button
     */
    protected void onClickDeleteItem(View view) {
        Print.i(TAG, "ON CLICK DELETE ITEM");
        // Get position
        mSelectedPositionToDelete = (int) view.getTag(R.id.target_position);
        String sku = (String) view.getTag(R.id.target_sku);
        triggerRemoveFromWishList(sku);
    }

    /**
     * Process the click on add all button
     */
    protected void onClickAddAllToCart() {
        Print.i(TAG, "ON CLICK ADD ALL TO CART");
        try {
            // Map
            HashMap<String, String> productBySku = new HashMap<>();
            // Validate all items
            WishListAdapter adapter = (WishListAdapter) mListView.getAdapter();
            int size = adapter.getCount();
            for (int i = 0; i < size; i++) {
                // Get item
                ProductMultiple item = adapter.getItem(i);
                // Validate has simple variation selected
                ProductSimple simple = item.getSelectedSimple();
                // Validate simple
                if (simple != null) {
                    productBySku.put(item.getSku(), simple.getSku());
                } else {
                    showUnexpectedErrorWarning();
                    break;
                }
            }
            // Sent
            if (!productBySku.isEmpty()) {
                triggerAddAllItems(productBySku);
            }

        } catch (NullPointerException | IllegalStateException e) {
            Print.e(TAG, "WARNING: EXCEPTION ON ADD ALL TO CART", e);
            getBaseActivity().warningFactory.showWarning(WarningFactory.PROBLEM_FETCHING_DATA);
        }
    }

    /**
     * Process the click on add button
     */
    protected void onClickAddToCart(View view) {
        Print.i(TAG, "ON CLICK ADD ITEM TO CART");
        // Get position
        int position = (int) view.getTag(R.id.target_position);
        // Get item
        ProductMultiple product = ((WishListAdapter) mListView.getAdapter()).getItem(position);
        // Validate has simple variation selected
        ProductSimple simple = product.getSelectedSimple();
        // Case add item to cart
        if (simple != null) {
            triggerAddProductToCart(product.getSku(), simple.getSku());
        }
        // Case select a simple variation
        else if (product.hasMultiSimpleVariations()) {
            onClickVariation(view);
        }
        // Case error unexpected
        else {
            showUnexpectedErrorWarning();
        }
    }

    /*
     * ############## TRIGGERS ##############
     */

    private void triggerGetWishListInitialPage() {
        triggerContentEvent(new GetWishListHelper(), null, this);
    }

    private void triggerAddAllItems(HashMap<String, String> values) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShoppingCartAddMultipleItemsHelper.ADD_ITEMS, values);
        triggerContentEventProgress(new ShoppingCartAddMultipleItemsHelper(), bundle, this);
    }

    private void triggerRemoveFromWishList(String sku) {
        triggerContentEventProgress(new RemoveFromWishListHelper(), RemoveFromWishListHelper.createBundle(sku), this);
    }

    protected synchronized void triggerAddProductToCart(String sku, String simpleSku) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku, simpleSku), this);
    }

    /*
     * ############## RESPONSES ##############
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS");
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Hide progress
        hideActivityProgress();
        // Validate event type
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                ToastManager.show(getBaseActivity(), ToastManager.SUCCESS_ADDED_CART);
                break;
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                removeSelectedPosition();
                ToastManager.show(getBaseActivity(), ToastManager.SUCCESS_REMOVED_FAVOURITE);
                break;
            case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
                // TODO
                break;
            case GET_WISH_LIST:
            default:
                ArrayList<ProductMultiple> wishList = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
                showContent(wishList);
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON REQUEST ERROR");
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Case No Network
        if (super.handleErrorEvent(bundle)) {
            Log.i(TAG, "HANDLE BASE FRAGMENT");
            return;
        }
        // Hide progress
        hideActivityProgress();
        // Validate event type
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                ToastManager.show(getBaseActivity(), ToastManager.ERROR_PRODUCT_OUT_OF_STOCK);
                break;
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                showUnexpectedErrorWarning();
                break;
            case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
                // TODO
                break;
            case GET_WISH_LIST:
            default:
                showContinueShopping();
                break;
        }
    }

}