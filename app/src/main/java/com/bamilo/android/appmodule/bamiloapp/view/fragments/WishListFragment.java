package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.framework.components.recycler.DividerItemDecoration;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.WishListGridAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartAddItemHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.wishlist.GetWishListHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.wishlist.RemoveFromWishListHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.interfaces.OnWishListViewHolderClickListener;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.campaign.CampaignItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.product.WishList;
import com.bamilo.android.framework.service.objects.product.pojo.ProductMultiple;
import com.bamilo.android.framework.service.objects.product.pojo.ProductSimple;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.HeaderFooterGridView;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogSimpleListFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * WishList fragment with pagination.
 *
 * @author sergiopereira
 */
public class WishListFragment extends BaseFragment implements IResponseCallback, DialogSimpleListFragment.OnDialogListListener, OnWishListViewHolderClickListener {

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
    private boolean pageTracked = false;
    private MainEventModel addToCartEventModel;

    /**
     * Empty constructor
     */
    public WishListFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.BASKET, MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.SAVED,
                R.layout._def_wishlist_fragment,
                R.string.saved,
                NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get data from saved instance
        if (savedInstanceState != null) {
            mWishList = savedInstanceState.getParcelable(ConstantsIntentExtra.DATA);
            sForceReloadWishListFromNetwork = savedInstanceState.getBoolean(ConstantsIntentExtra.FLAG_1);
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.WISH_LIST.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Loading more view
        mLoadingMore = view.findViewById(R.id.wish_list_loading_more);
        // List view
        mListView = (HeaderFooterGridView) view.findViewById(R.id.wish_list_grid);
        mListView.addOnScrollListener(onScrollListener);
        // Columns
        mNumberOfColumns = getResources().getInteger(R.integer.favourite_num_columns);
        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setGridLayoutManager(mNumberOfColumns);
        mListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
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
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
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
        if (!BamiloApplication.isCustomerLoggedIn()) {
            onLoginRequired();
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
            showWishListError(mWishList);
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
        WishListGridAdapter listAdapter = new WishListGridAdapter(wishList.getProducts(), this);
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
     * Show the wish list error, case first time or not.
     */
    protected void showWishListError(WishList currentWishList) {
        if (currentWishList == null) {
            showErrorFragment(ErrorLayoutFactory.NO_FAVOURITES_LAYOUT, this);
        } else {
            showWarningErrorMessage(getString(R.string.error_problem_fetching_data));
        }
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
    public void onClickVariation(View view) {
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
        // Case from sendPurchaseRecommend button
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
    public void onDialogSizeListClickView(int position, CampaignItem item) {

    }

    @Override
    public void onDialogListDismiss() {
        mClickedBuyButton = null;
    }

    /**
     * Process the click on item
     */
    public void onItemClick(View view) {
        Print.i(TAG, "ON ITEM CLICK");
        String sku = (String) view.getTag(R.id.target_sku);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on delete button
     */
    public void onClickDeleteItem(View view) {
        Print.i(TAG, "ON CLICK DELETE ITEM");
        try {
            // Get position
            mSelectedPositionToDelete = (int) view.getTag(R.id.target_position);
            String sku = (String) view.getTag(R.id.target_sku);
            // Get item
            WishListGridAdapter adapter = (WishListGridAdapter) mListView.getAdapter();
            TrackerDelegator.trackRemoveFromFavorites(adapter.getItem(mSelectedPositionToDelete));

            // Global Tracker
            SimpleEventModel removeFromWishListEventModel = new SimpleEventModel();
            removeFromWishListEventModel.category = getString(TrackingPage.WISH_LIST.getName());
            removeFromWishListEventModel.action = EventActionKeys.REMOVE_FROM_WISHLIST;
            removeFromWishListEventModel.label = sku;
            removeFromWishListEventModel.value = SimpleEventModel.NO_VALUE;
            TrackerManager.trackEvent(getContext(), EventConstants.RemoveFromWishList, removeFromWishListEventModel);

            // Trigger to remove
            triggerRemoveFromWishList(sku);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
        }
    }

    /**
     * Process the click on add button
     */
    public void onClickAddToCart(View view) {
        Print.i(TAG, "ON CLICK ADD ITEM TO CART");
        // Get position
        int position = (int) view.getTag(R.id.target_position);
        // Get item
        ProductMultiple product = ((WishListGridAdapter) mListView.getAdapter()).getItem(position);
        // Validate has simple variation selected
        ProductSimple simple = product.getSelectedSimple();
        // Case add item to cart
        if (simple != null) {
            triggerAddProductToCart(simple.getSku());
            TrackerDelegator.trackFavouriteAddedToCart(product, simple.getSku(), mGroupType);

            addToCartEventModel = new MainEventModel(getString(TrackingPage.WISH_LIST.getName()),
                    EventActionKeys.ADD_TO_CART, product.getSku(), (long) product.getPrice(), null);
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

    @Override
    protected void onLoginRequired() {
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

    protected synchronized void triggerAddProductToCart(String sku) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku), this);
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
        EventType eventType = baseResponse.getEventType();
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            return;
        }
        // Hide progress
        hideActivityProgress();
        // Validate event
        super.handleSuccessEvent(baseResponse);
        // Validate event type
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                // Tracking add to cart
                if (addToCartEventModel != null) {
                    PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
                    if (cart != null) {
                        addToCartEventModel.customAttributes =
                                MainEventModel.createAddToCartEventModelAttributes(addToCartEventModel.label, (long) cart.getTotal(), true);
                    } else {
                        addToCartEventModel.customAttributes =
                                MainEventModel.createAddToCartEventModelAttributes(addToCartEventModel.label, 0, true);
                    }
                    TrackerManager.trackEvent(getContext(), EventConstants.AddToCart, addToCartEventModel);
                }
                break;
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                // Remove selected position
                removeSelectedPosition();
                break;
            case GET_WISH_LIST:
                if (!pageTracked) {
//                    TrackerDelegator.trackPage(TrackingPage.WISH_LIST, getLoadTime(), false);

                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.WISH_LIST.getName()), getString(R.string.gaScreen),
                            "",
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }
                // Hide loading more
                setLoadingMore(false);
                // Show content
                showContent((WishList) baseResponse.getContentData());
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            return;
        }
        // Call super
        if(super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Hide progress
        hideActivityProgress();
        // Validate event type
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                break;
            case GET_WISH_LIST:
            default:
                showContinueShopping();
                isErrorOnLoadingMore = isLoadingMoreData;
                setLoadingMore(false);
                break;
        }
    }

    /*
     * ######## SCROLL STATE ########
     */

    private final OnScrollListener onScrollListener = new OnScrollListener() {

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
                setLoadingMore(true);
                triggerGetPaginatedWishList(mWishList.getPage() + 1);
            }
        }
    };
}