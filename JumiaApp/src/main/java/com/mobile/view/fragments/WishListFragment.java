package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.WishListGridAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.wishlist.GetWishListHelper;
import com.mobile.helpers.wishlist.RemoveFromWishListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnWishListViewHolderClickListener;
import com.mobile.newFramework.objects.product.WishList;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.catalog.HeaderFooterGridView;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * WishList fragment with pagination.
 *
 * @author sergiopereira
 */
public class WishListFragment extends BaseFragment implements IResponseCallback, DialogSimpleListFragment.OnDialogListListener {

    private static final String TAG = WishListFragment.class.getSimpleName();

    /**
     *  Flag used to reload the wish list content from network.
     *  Used only from PDV.
     */
    public static boolean sForceReloadWishListFromNetwork;

    private HeaderFooterGridView mListView;

    private View mLoadingMore;

    private WishList mWishList;

    private int mSelectedPositionToDelete = -1;

    private boolean isLoadingMoreData = false;

    private int mNumberOfColumns = 1;

    private boolean isErrorOnLoadingMore = false;

    private View mClickedBuyButton;

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
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.Saved,
                R.layout._def_wishlist_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
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
            Log.i(TAG, "GET DATA FROM SAVED STATE");
            mWishList = savedInstanceState.getParcelable(ConstantsIntentExtra.DATA);
            sForceReloadWishListFromNetwork = savedInstanceState.getBoolean(ConstantsIntentExtra.FLAG_1);
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
        // Loading more view
        mLoadingMore = view.findViewById(R.id.wish_list_loading_more);
        // List view
        mListView = (HeaderFooterGridView) view.findViewById(R.id.wish_list_grid);
        mListView.addOnScrollListener(onScrollListener);
        // Columns
        mNumberOfColumns = getResources().getInteger(R.integer.favourite_num_columns);
        mListView.setHasFixedSize(true);
        mListView.setGridLayoutManager(mNumberOfColumns);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        // Validate the state
        onValidateDataState();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE");
        outState.putParcelable(ConstantsIntentExtra.DATA, mWishList);
        outState.putBoolean(ConstantsIntentExtra.FLAG_1, sForceReloadWishListFromNetwork);
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
        Log.i(TAG, "ON STOP");
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
     * Method used to validate the current state.<br>
     * - Case logged in or not<br>
     * - Case first page<br>
     * - Case restore saved state<br>
     */
    private void onValidateDataState() {
        Print.i(TAG, "ON VALIDATE DATA STATE");
        // Validate customer is logged in
        if (!JumiaApplication.isCustomerLoggedIn()) {
            switchToLoginFragment();
        }
        // Case first time
        else if (mWishList == null || sForceReloadWishListFromNetwork) {
            sForceReloadWishListFromNetwork = false;
            mWishList = null;
            triggerGetPaginatedWishList(IntConstants.FIRST_PAGE);
        }
        // Case recover saved state
        else {
            showWishListContainer(mWishList);
        }
    }

    /**
     * Show content after get items
     *
     * @author sergiopereira
     */
    protected void showContent(WishList wishList) {
        Print.i(TAG, "ON SHOW CONTENT");
        // Case empty
        if (wishList == null || !wishList.hasProducts()) {
            showErrorFragment(ErrorLayoutFactory.NO_FAVOURITES_LAYOUT, this);
        }
        // Case first time
        else if(mWishList == null || wishList.getPage() == IntConstants.FIRST_PAGE) {
            mWishList = wishList;
            showWishListContainer(mWishList);
        }
        // Case load more
        else {
            mWishList.update(wishList);
            updateWishListContainer();
        }
    }

    /**
     * Show the wish list container as first time.
     */
    protected void showWishListContainer(WishList wishList) {
        WishListGridAdapter listAdapter = new WishListGridAdapter(wishList.getProducts(), new OnWishListViewHolderClickListener() {
            @Override
            public void onItemClick(View view) {
                WishListFragment.this.onItemClick(view);
            }

            @Override
            public void onClickDeleteItem(View view) {
                WishListFragment.this.onClickDeleteItem(view);
            }

            @Override
            public void onClickAddToCart(View view) {
                WishListFragment.this.onClickAddToCart(view);
            }

            @Override
            public void onClickVariation(View view) {
                WishListFragment.this.onClickVariation(view);
            }
        });
        mListView.setAdapter(listAdapter);
        showFragmentContentContainer();
    }

    /**
     * Update the wish list container
     */
    protected void updateWishListContainer() {
        // Update content
        WishListGridAdapter adapter = (WishListGridAdapter) mListView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    /**
     * Method used to remove the selected position.
     */
    private void removeSelectedPosition() {
        try {
            WishListGridAdapter adapter = (WishListGridAdapter) mListView.getAdapter();
            ProductMultiple item = adapter.getItem(mSelectedPositionToDelete);
            adapter.remove(item);
            // Case empty
            if(adapter.isEmpty()) {
                mWishList = null;
                showErrorFragment(ErrorLayoutFactory.NO_FAVOURITES_LAYOUT, this);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Log.i(TAG, "WARNING: EXCEPTION ON REMOVE SELECTED POSITION: " + mSelectedPositionToDelete);
        }
    }

    /**
     * Method used to hide/show/disable/enable the loading more.
     */
    private void setLoadingMore(boolean isLoading) {
        isLoadingMoreData = isLoading;
        mLoadingMore.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
        onValidateDataState();
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
        Print.i(TAG, "ON CLICK SIZE GUIDE");
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
        Print.i(TAG, "ON CLICK TO SHOW VARIATION LIST");
        try {
            int position = (int) view.getTag(R.id.target_position);
            ProductMultiple product = ((WishListGridAdapter) mListView.getAdapter()).getItem(position);
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
        Print.i(TAG, "ON CLICK VARIATION LIST ITEM");
        // Update the recently adapter
        updateWishListContainer();
        // Case from buy button
        if(mClickedBuyButton != null) {
            onClickAddToCart(mClickedBuyButton);
        }
    }

    @Override
    public void onDialogListClickView(View view) {
        // Process the click in the main method
        onClick(view);
    }

    @Override
    public void onDialogListDismiss() {
        mClickedBuyButton = null;
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
     * Process the click on add button
     */
    protected void onClickAddToCart(View view) {
        Print.i(TAG, "ON CLICK ADD ITEM TO CART");
        // Get position
        int position = (int) view.getTag(R.id.target_position);
        // Get item
        ProductMultiple product = ((WishListGridAdapter) mListView.getAdapter()).getItem(position);
        // Validate has simple variation selected
        ProductSimple simple = product.getSelectedSimple();
        // Case add item to cart
        if (simple != null) {
            triggerAddProductToCart(product.getSku(), simple.getSku());
            TrackerDelegator.trackFavouriteAddedToCart(product, simple.getSku(), mGroupType);
        }
        // Case select a simple variation
        else if (product.hasMultiSimpleVariations()) {
            mClickedBuyButton = view;
            onClickVariation(view);
        }
        // Case error unexpected
        else {
            showUnexpectedErrorWarning();
        }
    }

    /**
     * Request login
     */
    private void switchToLoginFragment() {
        // Pop entries until home
        getBaseActivity().popBackStackEntriesUntilTag(FragmentType.HOME.toString());
        // Goto Login and next WishList
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.WISH_LIST);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /*
     * ############## TRIGGERS ##############
     */

    private void triggerGetPaginatedWishList(int page) {
        if(page == IntConstants.FIRST_PAGE) {
            triggerContentEvent(new GetWishListHelper(), GetWishListHelper.createBundle(page), this);
        } else {
            triggerContentEventNoLoading(new GetWishListHelper(), GetWishListHelper.createBundle(page), this);
        }
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
    public void onRequestComplete(BaseResponse baseResponse) {
        Log.i(TAG, "ON SUCCESS");
        EventType eventType = baseResponse.getEventType();
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Hide progress
        hideActivityProgress();
        // Validate event
        super.handleSuccessEvent(baseResponse);
        // Validate event type
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                showAddToCartCompleteMessage(baseResponse);
                break;
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                removeSelectedPosition();
                showInfoAddToSaved();
                break;
            case GET_WISH_LIST:
            default:
                // Hide loading more
                setLoadingMore(false);
                // Show content
                WishList wishList = (WishList) baseResponse.getMetadata().getData();
                showContent(wishList);
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Log.i(TAG, "ON REQUEST ERROR");
        EventType eventType = baseResponse.getEventType();
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
                if (!super.handleErrorEvent(baseResponse)) {
                    showInfoAddToShoppingCartOOS();
                }
                break;
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                if (!super.handleErrorEvent(baseResponse)) {
                    showUnexpectedErrorWarning();
                }
                break;
            case GET_WISH_LIST:
            default:
                // Validate error
                if (!super.handleErrorEvent(baseResponse)) {
                    try {
                        Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();
                        if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_CUSTOMER_NOT_LOGGED_IN)) {
                            switchToLoginFragment();
                        } else {
                            showContinueShopping();
                        }
                    } catch (ClassCastException | NullPointerException e) {
                        showContinueShopping();
                    }
                }
                isErrorOnLoadingMore = isLoadingMoreData;
                setLoadingMore(false);
                break;
        }
    }

    /*
     * ######## SCROLL STATE ########
     */

    private OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            // ...
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
            int totalItemCount = manager.getItemCount();
            int visibleItemCount = manager.findLastCompletelyVisibleItemPosition();
            int firstVisibleItem = manager.findFirstVisibleItemPosition();

            // Force scroll to up until one row to disable error flag
            boolean isScrollingUp = totalItemCount != 0 && firstVisibleItem + visibleItemCount <= totalItemCount - mNumberOfColumns;
            if (isErrorOnLoadingMore && isScrollingUp) {
                isErrorOnLoadingMore = false;
            }
            // Bottom reached
            boolean isBottomReached = totalItemCount != 0 && visibleItemCount + 1 == totalItemCount;
            // Case bottom reached and has more pages loading the next page
            if (!isErrorOnLoadingMore && !isLoadingMoreData && isBottomReached && mWishList != null && mWishList.hasMorePages()) {
                Log.i(TAG, "LOAD MORE DATA");
                setLoadingMore(true);
                triggerGetPaginatedWishList(mWishList.getPage() + 1);
            }
        }
    };
}