package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.ProductListAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.products.GetCatalogPageHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.search.SearchHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.wishlist.AddToWishListHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.wishlist.RemoveFromWishListHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.interfaces.OnProductViewHolderClickListener;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModelFactory;
import com.bamilo.android.appmodule.bamiloapp.preferences.CustomerPreferences;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.CatalogGridAdapter;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.CatalogSort;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.FeaturedBoxHelper;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.HeaderFooterGridView;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.UICatalogUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.CatalogPageToastView;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogSortListFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogSortListFragment.OnDialogListListener;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.framework.service.objects.catalog.Catalog;
import com.bamilo.android.framework.service.objects.catalog.CatalogPage;
import com.bamilo.android.framework.service.objects.catalog.FeaturedBox;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogFilters;
import com.bamilo.android.framework.service.objects.catalog.filters.FilterSelectionController;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Catalog fragment.
 *
 * @author sergiopereira
 */
public class CatalogFragment extends BaseFragment implements IResponseCallback,
        OnProductViewHolderClickListener, OnDialogListListener {

    private static final String TAG = CatalogFragment.class.getSimpleName();

    private final static String TRACK_LIST = "list";

    private final static String TRACK_GRID = "grid";

    private final static String TRACK_SINGLE = "single";
    public final static String FILTER_TAG = "catalog_filters";

    private final static int FIRST_POSITION = 0;

    private HeaderFooterGridView mGridView;

    private TextView mSortButton;
    private TextView mSortDescription;
    private ImageView mSortIcon;

    private View mFilterButton;

    private View mTopButton;

    private CatalogPage mCatalogPage;

    private String mTitle;

    private View mNoResultStub;

    private ContentValues mCurrentFilterValues = new ContentValues();

    private CatalogSort mSelectedSort;

    private boolean mErrorLoading = false;

    private boolean isLoadingMoreData = false;

    private int mNumberOfColumns;

    private int mTopButtonActivateLine;

    private boolean mSortOrFilterApplied; // Flag to reload or not an initial catalog in case generic error

    private int mCatalogGridPosition = IntConstants.INVALID_POSITION;

    private String mCategoryTree;

    private String mMainCategory;

    private ContentValues mQueryValues = new ContentValues();

    private ProductRegular mWishListItemClicked = null;

    private int mLevel = CatalogGridAdapter.ITEM_VIEW_TYPE_LIST;

    private String mKey;

    private String searchQuery;

    //DROID-10
    private long mGABeginRequestMillis;

    private boolean showNoResult;
    private boolean sortChanged = false;
    private FragmentType mTargetType;
    private boolean pageTracked = false;

    private MainEventModel addToWishListEventModel;
    private SimpleEventModel removeFromWishListEventModel;

    /**
     * Empty constructor
     */
    public CatalogFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET,
                MyMenuItem.MY_PROFILE),
                NavigationAction.CATALOG,
                R.layout.catalog_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGABeginRequestMillis = System.currentTimeMillis();
        // Load line to active top button
        mTopButtonActivateLine = setButtonActiveLine(mLevel);
        // Get data from arguments (Home/Categories/Deep link)
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTargetType = (FragmentType) arguments
                    .getSerializable(ConstantsIntentExtra.TARGET_TYPE);
            // Get key
            mKey = arguments.getString(ConstantsIntentExtra.CONTENT_ID);
            // Get title
            mTitle = arguments.getString(ConstantsIntentExtra.CONTENT_TITLE);
            // Get catalog type (Hash|Seller|Brand|Category|DeepLink)
            mQueryValues = UICatalogUtils.saveCatalogType(arguments, mQueryValues, mKey);
            // Default catalog values
            mQueryValues.put(RestConstants.MAX_ITEMS, IntConstants.MAX_ITEMS_PER_PAGE);
            // In case of searching by keyword
            UICatalogUtils.saveSearchQuery(arguments, mQueryValues);
            searchQuery = arguments.getString(ConstantsIntentExtra.SEARCH_QUERY);
            // Verify if catalog page was open via navigation drawer
            mCategoryTree = arguments.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
            //Get category content/main category
            mMainCategory = arguments.getString(RestConstants.MAIN_CATEGORY);
            mCurrentFilterValues = arguments.getParcelable(FILTER_TAG);
            if (mCurrentFilterValues == null) {
                mCurrentFilterValues = new ContentValues();
            }
            // Get sort from Deep Link
            if (arguments.containsKey(ConstantsIntentExtra.CATALOG_SORT)) {
                mSelectedSort = CatalogSort.values()[arguments
                        .getInt(ConstantsIntentExtra.CATALOG_SORT)];
                sortChanged = true;
            }
            showNoResult = false;
        }

        // Track screen without timing
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CATALOG.getName()),
                getString(R.string.gaScreen), mTitle, getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        mCatalogPage = null;
        // Load user preferences
        mLevel = Integer.parseInt(CustomerPreferences.getCatalogLayout(getBaseActivity()));
        // Get number of columns
        int dimen = mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID
                ? R.integer.catalog_grid_num_columns : R.integer.catalog_list_num_columns;
        mNumberOfColumns = getResources().getInteger(dimen);
        // Get sort button
        mSortButton = view.findViewById(R.id.catalog_bar_button_sort);
        mSortDescription = view.findViewById(R.id.catalog_bar_description_sort);
        mSortIcon = view.findViewById(R.id.catalog_bar_sort);
        // Get filter button
        mFilterButton = view.findViewById(R.id.catalog_filter_button);
        // Get switch button
        ImageView mColumnsButton = view.findViewById(R.id.catalog_bar_button_columns);
        mColumnsButton.setOnClickListener(this);
        mColumnsButton.setImageLevel(mLevel);
        mTopButtonActivateLine = setButtonActiveLine(mLevel);
        // Get up button
        mTopButton = view.findViewById(R.id.catalog_button_top);
        mTopButton.setOnClickListener(this);
        // Get feature box
        mNoResultStub = view.findViewById(R.id.catalog_no_result_stub);
        // Get grid view
        mGridView = view.findViewById(R.id.catalog_grid_view);
        mGridView.setHasFixedSize(true);
        mGridView.setGridLayoutManager(mNumberOfColumns);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.addOnScrollListener(mOnScrollListener);
        //mGridView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //mGridView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        mGridView.post(new Runnable() {
            @Override
            public void run() {
                setVisibilityTopButton(mGridView);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();

        // Validate data
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCurrentFilterValues = arguments.getParcelable(FILTER_TAG);
            if (mCurrentFilterValues == null) {
                mCurrentFilterValues = new ContentValues();
            }
        }
        onValidateDataState();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();

        // Verify if is comming from login after trying to add/remove item from cart.
        retryWishListActionLoggedIn();

    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mGridView != null) {
            mCatalogGridPosition = ((GridLayoutManager) mGridView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mGridView != null) {
            mCatalogGridPosition = ((GridLayoutManager) mGridView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove scroll listener
        if (mGridView != null) {
            mGridView.removeOnScrollListener(mOnScrollListener);
        }
    }

    @Override
    public void notifyFragment(@Nullable Bundle bundle) {
        super.notifyFragment(bundle);

        if (bundle != null && bundle.containsKey(FilterMainFragment.FILTER_TAG)) {
            onSubmitFilterValues(bundle.getParcelable(FilterMainFragment.FILTER_TAG));
        }
    }

    /**
     * Validate the current data.<br> - Case required arguments are empty show continue shopping<br>
     * - Case is empty get data<br> - Case not empty show data<br>
     */
    private void onValidateDataState() {
        // Case URL or QUERY is empty show continue shopping
        if (!mQueryValues.containsKey(RestConstants.CATEGORY) && !mQueryValues
                .containsKey(RestConstants.SELLER) &&
                !mQueryValues.containsKey(RestConstants.QUERY) && TextUtils.isEmpty(mKey)) {
            showContinueShopping();
        }
        // Case catalog is null get catalog from URL
        else if (mCatalogPage == null) {
            triggerGetPaginatedCatalog();

        }
        // Case sort or filter applied
        else if (mSortOrFilterApplied) {
            triggerGetInitialCatalogPage();
            // Set the filter button selected or not
            setFilterButtonState(mCurrentFilterValues, mCatalogPage);

        }
        // Case catalog was recover
        else {
//            mCurrentFilterValues = new FilterSelectionController(mCatalogPage.getFilters()).getValues();
            onRecoverCatalogContainer(mCatalogPage);
            TrackerDelegator.trackCatalogPageContent(mCatalogPage, mCategoryTree, mMainCategory);
        }
    }

    /**
     * Recover the catalog container.
     *
     * @param catalogPage - the saved instance
     */
    private void onRecoverCatalogContainer(CatalogPage catalogPage) {
        // Set title bar
        UICatalogUtils.setCatalogTitle(getBaseActivity(), mTitle);
        // Set sort button
        setSortButton();
        // Set filter button
        UICatalogUtils.setFilterButtonActionState(mFilterButton, catalogPage.hasFilters(), this);
        // Set the filter button selected or not
        setFilterButtonState(mCurrentFilterValues, catalogPage);
        // Create adapter new data
        setCatalogAdapter(catalogPage);
        // Validate loading more view 
        isLoadingMoreData = false;
        // Validate if user can load more pages
        if (mCatalogPage.hasMorePagesToLoad()) {
            mGridView.showFooterView();
        } else {
            mGridView.hideFooterView();
        }
        // Show header
        showHeaderBanner();
        // Show container
        showFragmentContentContainer();
        if (mCurrentFilterValues.size() == 0) {
            setFilterDescription(catalogPage);
        }

    }

    /**
     * Updated the catalog container.
     *
     * @param catalogPage - The current catalog page
     */
    private void onUpdateCatalogContainer(CatalogPage catalogPage) {
        // Case first time save catalog
        //mCatalogPage = catalogPage;
        if (mCatalogPage == null) {
            mCatalogPage = catalogPage;
        }
        // Case load more then update data or Case filter applied then replace the data
        else {
            mCatalogPage.update(catalogPage);
        }

        FilterSelectionController filterSelectionController = new FilterSelectionController(
                mCatalogPage.getFilters());
        mCurrentFilterValues = filterSelectionController.getValues();

        // Validate current catalog page
        CatalogGridAdapter adapter = (CatalogGridAdapter) mGridView.getAdapter();
        if (adapter == null) {
            // Create adapter new data
            setCatalogAdapter(mCatalogPage);
            // Set filter button
            UICatalogUtils
                    .setFilterButtonActionState(mFilterButton, mCatalogPage.hasFilters(), this);
            setFilterButtonState(mCurrentFilterValues, mCatalogPage);
            // Set sort button
            setSortButton();
        }
        // Case load more append the new data
        else if (isLoadingMoreData) {
            // Hide loading
            isLoadingMoreData = false;
            // Append new data
            adapter.notifyDataSetChanged();
        }
        // Case filter applied/clean replace the current data
        else {
            setCatalogAdapter(mCatalogPage);
            // Hide the goto top button
            UICatalogUtils.hideGotoTopButton(getBaseActivity(), mTopButton);
        }

        // Save title
        mTitle = catalogPage.getName();
        // Set title bar
        UICatalogUtils.setCatalogTitle(getBaseActivity(), mTitle);
        // Show header
        if (catalogPage.getPage() == IntConstants.FIRST_PAGE) {
            showHeaderBanner();
        }
        // Validate if user can load more pages
        if (mCatalogPage.hasMorePagesToLoad()) {
            mGridView.showFooterView();
        } else {
            mGridView.hideFooterView();
        }
        // Show container
        showFragmentContentContainer();
        if (mCurrentFilterValues.size() == 0) {
            setFilterDescription(catalogPage);
        }
    }

    /**
     * Method responsible for validating if the catalog page has banner, and if it so, show it
     */
    private void showHeaderBanner() {
        // Show header
        if (mGridView != null && mCatalogPage.getCatalogBanner() != null) {
            mGridView.setHeaderView(mCatalogPage.getCatalogBanner());
        }
    }

    /**
     * Set the sort button with the current sort selection.
     */
    private void setSortButton() {
        mSortDescription.setText(getString(mSelectedSort.name));
        mSortDescription.setOnClickListener(this);
        mSortButton.setOnClickListener(this);
        mSortDescription.setSelected(sortChanged);
        mSortButton.setSelected(sortChanged);
        mSortIcon.setSelected(sortChanged);
    }

    /**
     * Show the no filter layout error.
     *
     * @param stringId The message.
     */
    private void showFilterError(@ErrorLayoutFactory.LayoutErrorType int stringId) {
        // Set title
        UICatalogUtils.setCatalogTitle(getBaseActivity(), mTitle);
        // Show layout
        showErrorFragment(stringId, v -> {
            onClickFilterErrorButton();
        });
    }

    private void onClickFilterErrorButton() {
        getBaseActivity().onBackPressed();
    }

    /**
     * Show the no filter result.
     */
    private void showFilterNoResult() {
        showFilterError(ErrorLayoutFactory.CATALOG_NO_RESULTS);
    }

    /**
     * Show the no filter unexpected error.
     */
    private void showFilterUnexpectedError() {
        showFilterError(ErrorLayoutFactory.CATALOG_UNEXPECTED_ERROR);
    }

    /**
     * Show the received feature box from an invalid query
     *
     * @param featuredBox - all data to show the feature box
     */
    private void showFeaturedBoxNoResult(FeaturedBox featuredBox) {
        // Inflate view
        mNoResultStub.setVisibility(View.VISIBLE);
        // Show featured box
        if (FeaturedBoxHelper.show(this, featuredBox)) {
            // Case success show container
            showFragmentContentContainer();

        } else {
            // Case fail show continue
            showContinueShopping();
        }
    }

    /**
     * Method used to retry the wish list action after login.
     */
    private void retryWishListActionLoggedIn() {
        // Verify if is comming from login after trying to add/remove item from cart.
        final Bundle args = getArguments();
        if (CollectionUtils.isNotEmpty(args) && BamiloApplication.isCustomerLoggedIn()) {
            // Case add to wish list
            if (args.containsKey(AddToWishListHelper.ADD_TO_WISHLIST)) {
                ProductRegular mClicked = args.getParcelable(AddToWishListHelper.ADD_TO_WISHLIST);
                if (mClicked != null) {
                    triggerAddToWishList(mClicked.getSku(), mClicked.getCategoryKey());
                    TrackerDelegator.trackAddToFavorites(mClicked);
//                    TrackerManager.trackEvent(getBaseActivity(), EmarsysEventConstants.AddToFavorites, EmarsysEventFactory.addToFavorites(mClicked.getCategoryKey(), true));
                }
                args.remove(AddToWishListHelper.ADD_TO_WISHLIST);
            }
            // Case remove from wish list
            else if (args.containsKey(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST)) {
                ProductRegular mClicked = args
                        .getParcelable(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST);
                if (mClicked != null) {
                    triggerRemoveFromWishList(mClicked.getSku());
                    TrackerDelegator.trackRemoveFromFavorites(mClicked);
                }
                args.remove(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST);
            }
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
        // Validate data
        onValidateDataState();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.OnViewHolderClickListener#onViewHolderClick(android.support.v7.widget.RecyclerView.Adapter, android.view.View, int)
     */
    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
        // Get item
        ProductRegular product = ((CatalogGridAdapter) adapter).getItem(position);
        // Call Product Details
        if (product != null) {
            // Show product
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, product.getSku());
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE,
                    product.getBrandName() + " " + product.getName());
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
            bundle.putInt(ConstantsIntentExtra.PRODUCT_POSITION, position);
            // Goto PDV
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
                    FragmentController.ADD_TO_BACK_STACK);
        } else {
            showWarningErrorMessage(getString(R.string.error_occured));
        }
    }

    @Override
    public void onViewHolderItemClick(View view, RecyclerView.Adapter<?> adapter, int position) {
        // Get item
        mWishListItemClicked = ((CatalogGridAdapter) adapter).getItem(position);
        // Validate customer is logged in
        if (BamiloApplication.isCustomerLoggedIn()) {
            if (view.isSelected()) {
                triggerRemoveFromWishList(mWishListItemClicked.getSku());
                TrackerDelegator.trackRemoveFromFavorites(mWishListItemClicked);
            } else {
                triggerAddToWishList(mWishListItemClicked.getSku(), mMainCategory);
                TrackerDelegator.trackAddToFavorites(mWishListItemClicked);
//                TrackerManager.trackEvent(getBaseActivity(), EmarsysEventConstants.AddToFavorites, EmarsysEventFactory.addToFavorites(mMainCategory, true));
            }
        } else {
            // Save values to end action after login
            final Bundle args = getArguments();
            if (args != null) {
                if (view.isSelected()) {
                    args.putParcelable(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST,
                            mWishListItemClicked);
                } else {
                    args.putParcelable(AddToWishListHelper.ADD_TO_WISHLIST, mWishListItemClicked);
                }
            }
            // Goto login
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE,
                    FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Updated the clicked product to add to or remove from wish list.
     */
    private void updateWishListProduct() {
        if (mWishListItemClicked != null && mGridView != null && mGridView.getAdapter() != null) {
            mGridView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * After favouriting a product in ProductDetail we have to update it's favourite icon in Catalog too
     */
    public void updateProduct(int position) {
        if (mGridView != null && mGridView.getAdapter() != null && mGridView.getAdapter() instanceof ProductListAdapter)
            ((ProductListAdapter) mGridView.getAdapter()).updateFavorite(position);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case sort button
        if (id == R.id.catalog_bar_button_sort || id == R.id.catalog_bar_sort
                || id == R.id.catalog_sort_button
                || id == R.id.catalog_bar_description_sort) {
            onClickSortButton();
        }
        // Case filter button
        else if (id == R.id.catalog_bar_button_filter || id == R.id.catalog_bar_filter
                || id == R.id.catalog_filter_button
                || id == R.id.catalog_bar_description_filter) {
            onClickFilterButton();
        }
        // Case columns button
        else if (id == R.id.catalog_bar_button_columns) {
            onClickSwitchColumnsButton(view);
        }
        // Case top button
        else if (id == R.id.catalog_button_top) {
            onClickGotoTopButton();
        }
        // Case default
        else {
            super.onClick(view);
        }
    }

    private void onClickFilterButton() {
        try {
            // Show dialog
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.TARGET_TYPE, mTargetType);
            bundle.putString(ConstantsIntentExtra.CATEGORY_URL, mKey);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, mKey);
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
            CatalogFilters filters = (CatalogFilters) mCatalogPage.getFilters();
            bundle.putInt(ConstantsIntentExtra.CATALOG_SORT,
                    mSelectedSort != null ? mSelectedSort.ordinal()
                            : CatalogSort.POPULARITY.ordinal());
            bundle.putString(FILTER_TAG, filters.toJSON().toString());
            bundle.putParcelableArrayList(RestConstants.SUB_CATEGORIES,
                    mCatalogPage.getSubCategories());
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, searchQuery);
            getBaseActivity().onSwitchFragment(FragmentType.FILTERS, bundle, false);
        } catch (Exception ignored) {
        }
    }

    /**
     * Process the filter values.
     *
     * @param filterValues - the new content values from dialog
     */
    private void onSubmitFilterValues(ContentValues filterValues) {
        mCatalogGridPosition = IntConstants.DEFAULT_POSITION;
        //Remove old filters from final request values
        for (String key : mCurrentFilterValues.keySet()) {
            mQueryValues.remove(key);
        }
        // Save the current filter values
        mCurrentFilterValues = filterValues;
        // Flag to reload or not an initial catalog in case generic error
        mSortOrFilterApplied = true;
        // Track catalog filtered
        SimpleEventModel sem = SimpleEventModelFactory
                .createModelForCatalogFilter(mCurrentFilterValues);
        TrackerManager.trackEvent(getContext(), EventConstants.SearchFiltered, sem);
    }

    /**
     * Process the click on Columns button
     *
     * @param button - the clicked view
     */
    private void onClickSwitchColumnsButton(View button) {
        try {
            mLevel = ((ImageView) button).getDrawable().getLevel();
            switchCatalogView();
            // Save user preference
            CustomerPreferences.saveCatalogLayout(getBaseActivity(), "" + mLevel);
            // Update the icon
            ((ImageView) button).setImageLevel(mLevel);
            //change back to top line number
            mTopButtonActivateLine = setButtonActiveLine(mLevel);
            // Update the number of columns
            mNumberOfColumns = updateCatalogColumnsNumber();
            // Update the columns and layout
            GridLayoutManager manager = (GridLayoutManager) mGridView.getLayoutManager();
            manager.setSpanCount(mNumberOfColumns);
            manager.requestLayout();
            ((CatalogGridAdapter) mGridView.getAdapter()).updateLayout(mLevel);
            // Track catalog
            SimpleEventModel sem =
                    new SimpleEventModel(CategoryConstants.CATALOG,
                            EventActionKeys.CATALOG_VIEW_CHANGED,
                            trackView(), SimpleEventModel.NO_VALUE);
            TrackerManager.trackEvent(getContext(), EventConstants.CatalogViewChanged, sem);
        } catch (NullPointerException e) {
        }
    }

    /**
     * Process the click on button to go top
     */
    private void onClickGotoTopButton() {
        GridLayoutManager manager = (GridLayoutManager) mGridView.getLayoutManager();
        int columns = manager.getSpanCount();
        // Scroll faster until mark line
        mGridView.scrollToPosition(columns * mTopButtonActivateLine);
        // Scroll smooth until top position
        mGridView.post(() -> mGridView.smoothScrollToPosition(FIRST_POSITION));
    }

    /**
     * method that calculates the line number where back to top button shows
     *
     * @return line number
     */
    private int setButtonActiveLine(int level) {
        switch (level) {
            case CatalogGridAdapter.ITEM_VIEW_TYPE_LIST:
                return getResources().getInteger(R.integer.activate_go_top_buttom_line);
            case CatalogGridAdapter.ITEM_VIEW_TYPE_GRID:
                return getResources().getInteger(R.integer.activate_go_top_buttom_line_grid);
            case CatalogGridAdapter.ITEM_VIEW_TYPE_SINGLE:
                return getResources().getInteger(R.integer.activate_go_top_buttom_line_single);
            default:
                return getResources().getInteger(R.integer.activate_go_top_buttom_line_grid);
        }
    }

    /**
     * Process the click on sort button
     */
    private void onClickSortButton() {
        // Create array list of strings
        ArrayList<String> mSortOptions = new ArrayList<>();
        for (CatalogSort sort : CatalogSort.values()) {
            mSortOptions.add(getString(sort.name));
        }
        // Show dialog
        DialogSortListFragment
                .newInstance(this, this, "sort", getString(R.string.sort_by), mSortOptions,
                        mSelectedSort.ordinal()).show(getChildFragmentManager(), null);
    }

    /*
     * (non-Javadoc)
     * @see <com.bamilo.android.framework.components.com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogListFragment.OnDialogListListener#onDialogListItemSelect(java.lang.String, int, java.lang.String)
     */
    @Override
    public void onDialogListItemSelect(int position, String value) {
        // Get selected sort position
        mSelectedSort = CatalogSort.values()[position];
        // Set sort button
        sortChanged = true;
        setSortButton();
        // Flag to reload or not an initial catalog in case generic error
        mSortOrFilterApplied = true;
        // Get new data
        triggerGetInitialCatalogPage();
        // Track catalog sorted
        SimpleEventModel sem =
                new SimpleEventModel(CategoryConstants.CATALOG, EventActionKeys.CATALOG_SORT,
                        mSelectedSort.toString(), SimpleEventModel.NO_VALUE);
        TrackerManager.trackEvent(getContext(), EventConstants.CatalogSortChanged, sem);


        String sortingKey = null;
        switch(mSelectedSort) {
            case NAME:
                sortingKey = "name";
                break;
            case BRAND:
                sortingKey = "brand";
                break;
            case NEW_IN:
                sortingKey = "newest";
                break;
            case PRICE_UP:
                sortingKey = "price-asc";
                break;
            case POPULARITY:
                sortingKey = "popularity";
                break;
            case PRICE_DOWN:
                sortingKey = "price-desc";
                break;
            case BEST_RATING:
                sortingKey = "score";
                break;
        }
        EventTracker.INSTANCE.sortProductsList(sortingKey);

    }

    @Override
    public void onDismiss() {
    }

    /**
     * The listener for grid view
     */
    private final OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Get the grid layout manager
            GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
            // Get the size
            int total = manager.getItemCount();
            // Get the last view for loading
            int last = manager.findLastCompletelyVisibleItemPosition();
            // Get the current page
            int page = mCatalogPage.getPage();
            // Get the max number of pages
            int max = mCatalogPage.getMaxPages();
            // Case error loading more, go up until to total - 1 to enable the loading more data
            if (isLoadingMoreData && mErrorLoading && last < total - 1) {
                isLoadingMoreData = false;
                mErrorLoading = false;
            }
            // Case loading visible then load more items
            else if (!isLoadingMoreData && page < max && last + 1 == total) {
                isLoadingMoreData = true;
                triggerGetPaginatedCatalog();
            }
        }

        /*
         * (non-Javadoc)
         * @see android.support.v7.widget.RecyclerView.OnScrollListener#onScrollStateChanged(android.support.v7.widget.RecyclerView, int)
         */
        /*@Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Print.i(TAG, "ON SCROLL STATE CHANGED: " + newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                RocketImageLoader.getInstance().stopProcessingQueue();
            } else {
                RocketImageLoader.getInstance().startProcessingQueue();
                setVisibilityTopButton(recyclerView);
            }
        }*/
    };

    // TODO: 8/26/2017 correct scroll to top functionality
    protected void setVisibilityTopButton(RecyclerView recyclerView) {
        // Set the goto top button
        /*GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        int last = manager.findLastVisibleItemPosition();
        // Show or hide top button after X arrow
        if (last > mNumberOfColumns * mTopButtonActivateLine) {
            UICatalogUtils.showGotoTopButton(getBaseActivity(), mTopButton);
        } else {
            UICatalogUtils.hideGotoTopButton(getBaseActivity(), mTopButton);
        }*/
    }

    /*
     * ############## TRIGGERS ##############
     */

    /**
     * Trigger to add item from wish list.
     */
    private void triggerAddToWishList(String sku, String categoryKey) {
        addToWishListEventModel = new MainEventModel(getString(TrackingPage.CATALOG.getName()),
                EventActionKeys.ADD_TO_WISHLIST, sku,
                SimpleEventModel.NO_VALUE,
                MainEventModel.createAddToWishListEventModelAttributes(sku, categoryKey, true));
        if (mCatalogPage != null && mCatalogPage.getProducts() != null) {
            for (ProductRegular item : mCatalogPage.getProducts()) {
                if (item.getSku().equals(sku)) {
                    addToWishListEventModel.value = (long) item.getPrice();
                    addToWishListEventModel.label =  item.getSku();
                    break;
                }
            }
        }
        triggerContentEventNoLoading(new AddToWishListHelper(),
                AddToWishListHelper.createBundle(sku), this);
    }

    /**
     * Trigger to remove item from wish list.
     */
    private void triggerRemoveFromWishList(String sku) {
        removeFromWishListEventModel = new SimpleEventModel();
        removeFromWishListEventModel.category = getString(TrackingPage.CART.getName());
        removeFromWishListEventModel.action = EventActionKeys.REMOVE_FROM_WISHLIST;
        removeFromWishListEventModel.label = sku;
        removeFromWishListEventModel.value = SimpleEventModel.NO_VALUE;
        if (mCatalogPage != null && mCatalogPage.getProducts() != null) {
            for (ProductRegular item : mCatalogPage.getProducts()) {
                if (item.getSku().equals(sku)) {
                    removeFromWishListEventModel.value = (long) item.getPrice();
                    break;
                }
            }
        }
        triggerContentEventNoLoading(new RemoveFromWishListHelper(),
                RemoveFromWishListHelper.createBundle(sku), this);
    }

    /**
     * Trigger the initialized catalog.<br> Used for filter and sort.
     */
    private void triggerGetInitialCatalogPage() {
        // Get first page
        triggerGetCatalogPage(IntConstants.FIRST_PAGE);
    }

    /**
     * Trigger the paginated catalog.
     */
    private void triggerGetPaginatedCatalog() {
        // Get next page
        int page = mCatalogPage == null ? IntConstants.FIRST_PAGE : mCatalogPage.getPage() + 1;
        // Get catalog page
        triggerGetCatalogPage(page);
    }

    /**
     * Trigger used to get a catalog.<br> Is sent the URL, arguments and indication to save or not
     * related items.
     */
    private void triggerGetCatalogPage(int page) {
        // Create catalog request parameters
        mQueryValues.put(RestConstants.PAGE, page);
        // Get filters
        if (mCurrentFilterValues != null) {
            mQueryValues.putAll(mCurrentFilterValues);
        }
        // Get Sort
        if (mSelectedSort != null && TextUtils.isNotEmpty(mSelectedSort.path) && !mSelectedSort
                .equals(CatalogSort.POPULARITY)) {
            mQueryValues.put(RestConstants.SORT, mSelectedSort.path);
        }
        // Case initial request or load more
        if (page == IntConstants.FIRST_PAGE) {
            triggerContentEvent(new GetCatalogPageHelper(),
                    GetCatalogPageHelper.createBundle(mQueryValues), this);
        } else {
            triggerContentEventNoLoading(new GetCatalogPageHelper(),
                    GetCatalogPageHelper.createBundle(mQueryValues), this);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        super.handleSuccessEvent(baseResponse);

        EventType eventType = baseResponse.getEventType();
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            return;
        }

        // Hide dialog progress
        hideActivityProgress();
        // Validate event type
        switch (eventType) {
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                if (removeFromWishListEventModel != null) {
//                    TrackerManager.trackEvent(getContext(), EventConstants.RemoveFromWishList,
//                            removeFromWishListEventModel);
                    try {
                        EventTracker.INSTANCE.removeFromWishList(
                                removeFromWishListEventModel.label,
                                "",
                                0,
                                "",
                                1
                        );
                    }catch (Exception e) {

                    }


                }
                updateWishListProduct();
                break;
            case ADD_PRODUCT_TO_WISH_LIST:
                if (addToWishListEventModel != null) {
//                    TrackerManager.trackEvent(getContext(), EventConstants.AddToWishList,
//                            addToWishListEventModel);
                    try {
                        EventTracker.INSTANCE.addToWishList(
                                removeFromWishListEventModel.label,
                                mWishListItemClicked.getName(),
                                (long) mWishListItemClicked.getPrice(),
                                mWishListItemClicked.getCategoryId(),
                                1
                        );
                    }catch (Exception e) {

                    }

                }
                updateWishListProduct();
                break;

            case GET_CATALOG_EVENT:
            default:
                onRequestCatalogSuccess(baseResponse);
                break;
        }
    }

    /**
     * Process the catalog success response.
     */
    private void onRequestCatalogSuccess(BaseResponse baseResponse) {
        if (!pageTracked) {
            BaseScreenModel screenModel = new BaseScreenModel(
                    getString(TrackingPage.CATALOG.getName()), getString(R.string.gaScreen), "",
                    getLoadTime());
            TrackerManager.trackScreenTiming(getContext(), screenModel);

            pageTracked = true;
        }
        // Get the catalog
        CatalogPage catalogPage = ((Catalog) baseResponse.getMetadata().getData()).getCatalogPage();
//        sendRecommend(catalogPage);
        // Case valid success response
        if (catalogPage != null && catalogPage.hasProducts()) {
            // Mark to reload an initial catalog
            mSortOrFilterApplied = false;

            // IF API is not retrieving the sort, and we already have a sort selected, keep it, otherwise use POPULARITY
            mSelectedSort = TextUtils.isNotEmpty(catalogPage.getSort()) ?
                    CatalogSort.valueOf(catalogPage.getSort()) :
                    (mSelectedSort != null ? mSelectedSort : CatalogSort.POPULARITY);

            onUpdateCatalogContainer(catalogPage);
            if (catalogPage.getPage() == 1) {
                TrackerDelegator
                        .trackCatalogPageContent(mCatalogPage, mCategoryTree, mMainCategory);

                // Global tracking
                MainEventModel searchEventModel = new MainEventModel(CategoryConstants.CATALOG,
                        EventActionKeys.SEARCH,
                        catalogPage.getSearchTerm(), catalogPage.getTotal(),
                        MainEventModel.createSearchEventModelAttributes(mMainCategory,
                                SearchHelper.getSearchTermsCommaSeparated(
                                        catalogPage.getSearchTerm())));
//                TrackerManager.trackEvent(getContext(), EventConstants.Search, searchEventModel);
                EventTracker.INSTANCE.search(catalogPage.getSearchTerm());

                int actionBarHeight = 180;
                TypedValue tv = new TypedValue();
                if (getBaseActivity().getTheme()
                        .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                            getResources().getDisplayMetrics());
                }
                actionBarHeight *= 2;
                int dp = (int) (getResources().getDimension(R.dimen.dimen_10dp) / getResources()
                        .getDisplayMetrics().density);

                actionBarHeight += dp;//TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, R.dimen.dimen_10dp, getResources().getDisplayMetrics());
                Toast toast = CatalogPageToastView
                        .makeText(getBaseActivity(), "تعداد محصول: " + catalogPage.getTotal(),
                                Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, actionBarHeight);
                toast.show();
            }
        }
        // Case invalid success response
        else {
            showContinueShopping();
        }
        if (mCurrentFilterValues.size() == 0) {
            setFilterDescription(catalogPage);
        }

    }

    private void setFilterButtonState(ContentValues filterValues, CatalogPage catalogPage) {
        TextView filterDesc = mFilterButton.findViewById(R.id.catalog_bar_description_filter);
        boolean hasFilter = filterValues.size() > 0;
        mFilterButton.setSelected(hasFilter);
        if (hasFilter) {
            String description;
            List<String> filterNamesArray = new ArrayList<>();
            for (String entry : filterValues.keySet()) {
                for (int index = 0; index < catalogPage.getFilters().size(); index++) {
                    if (catalogPage.getFilters().get(index).getId().equals(entry)) {
                        filterNamesArray.add(catalogPage.getFilters().get(index).getName());
                    }
                }
            }
            if (filterValues.containsKey(RestConstants.SPECIAL_PRICE)) {
                filterNamesArray.add(getString(R.string.special_price_filter));
            }
            if (filterNamesArray.size() == 1) {
                description = filterNamesArray.get(0);
            } else if (filterNamesArray.size() > 1) {
                description = getString(R.string.filters_last_delimiter,
                        android.text.TextUtils.join(getString(R.string.filters_separator),
                                filterNamesArray.subList(0, filterNamesArray.size() - 1)),
                        filterNamesArray.get(filterNamesArray.size() - 1));
            } else {
                description = android.text.TextUtils.join(getString(R.string.filters_separator),
                        filterNamesArray.subList(0, filterNamesArray.size() - 1));
            }
            filterDesc.setText(description);
        } else {
            if (!catalogPage.hasFilters()) {
                mFilterButton.setEnabled(false);
            } else {
                setFilterDescription(catalogPage);
            }
        }
    }

    private void setFilterDescription(CatalogPage catalogPage) {
        TextView filterDesc = mFilterButton.findViewById(R.id.catalog_bar_description_filter);
        String filterNames = "";

        if (catalogPage.hasFilters()) {
            List<String> filterNamesArray = new ArrayList<>();
            for (CatalogFilter filter : catalogPage.getFilters()) {
                filterNamesArray.add(filter.getName());
            }
            if (filterNamesArray.size() == 1) {
                filterNames = filterNamesArray.get(0);
            } else if (filterNamesArray.size() > 1) {
                filterNames = getString(R.string.filters_last_delimiter,
                        android.text.TextUtils.join(getString(R.string.filters_separator),
                                filterNamesArray.subList(0, filterNamesArray.size() - 1)),
                        filterNamesArray.get(filterNamesArray.size() - 1));
            } else {
                filterNames = android.text.TextUtils.join(getString(R.string.filters_separator),
                        filterNamesArray.subList(0, filterNamesArray.size() - 1));
            }
        }
        filterDesc.setText(filterNames);

        setSortButton();
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Get error code
        EventType eventType = baseResponse.getEventType();
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            return;
        }
        // Validate event type
        switch (eventType) {
            case REMOVE_PRODUCT_FROM_WISH_LIST:
            case ADD_PRODUCT_TO_WISH_LIST:
                // Hide dialog progress
                hideActivityProgress();
                // Validate error
                if (!super.handleErrorEvent(baseResponse)) {
                    showUnexpectedErrorWarning();
                }
                break;
            case GET_SUBCATEGORIES_EVENT:
                hideActivityProgress();
                break;

            case GET_CATALOG_EVENT:
            default:
                onRequestCatalogError(baseResponse);
                break;
        }
    }

    /**
     * Process the catalog error response.
     */
    private void onRequestCatalogError(BaseResponse baseResponse) {

        int errorCode = baseResponse.getError().getCode();

        Catalog catalog = (Catalog) baseResponse.getContentData();
        // Case error on load more data
        if (isLoadingMoreData) {
            onLoadingMoreRequestError(baseResponse);
        }
        // Case error on request data with filters
        else if (errorCode == ErrorCode.REQUEST_ERROR && CollectionUtils
                .isNotEmpty(mCurrentFilterValues)) {
            showFilterNoResult();
        }
        // Case error on request data without filters
        else if (errorCode == ErrorCode.REQUEST_ERROR && catalog != null
                && catalog.getFeaturedBox() != null) {
            // Get feature box
            FeaturedBox featuredBox = catalog.getFeaturedBox();
            // Show no result layout
            showFeaturedBoxNoResult(featuredBox);
            showNoResult = true;
        }
        // Case No Network
        else if (super.handleErrorEvent(baseResponse)) {
        }
        // Case unexpected error
        else {
            showFragmentNetworkErrorRetry();
        }
    }

    /**
     * Process the error code
     */
    private void onLoadingMoreRequestError(BaseResponse baseResponse) {
        // Mark error on loading more
        mErrorLoading = true;
        // Scroll to hide the loading view
        mGridView.stopScroll();
        mGridView.scrollBy(0, -getResources().getDimensionPixelSize(R.dimen.catalog_footer_height));
        // Show respective warning indicating to use the warning bar
        baseResponse.setEventTask(EventTask.ACTION_TASK);
        // Case super not handle the error show unexpected error
        if (!super.handleErrorEvent(baseResponse)) {
            showUnexpectedErrorWarning();
        }
    }

    @Override
    public void onHeaderClick(String target, String title) {
        // Parse target link
        new TargetLink(getWeakBaseActivity(), target)
                .addTitle(title)
                .enableWarningErrorMessage()
                .run();
    }

    /**
     * switch the icon and type of catalog view depending on the previous one
     */
    private void switchCatalogView() {
        if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_LIST) {
            mLevel = CatalogGridAdapter.ITEM_VIEW_TYPE_SINGLE;
        } else if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID) {
            mLevel = CatalogGridAdapter.ITEM_VIEW_TYPE_LIST;
        } else {
            mLevel = CatalogGridAdapter.ITEM_VIEW_TYPE_GRID;
        }
    }

    /**
     * Gets the number of columns defined for a specfic view
     *
     * @return columns number
     */
    private int updateCatalogColumnsNumber() {
        if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID) {
            return getResources().getInteger(R.integer.catalog_grid_num_columns);
        } else {
            return getResources().getInteger(R.integer.catalog_list_num_columns);
        }
    }

    /**
     * @return the type of the current catalog view
     */
    private String trackView() {
        if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID) {
            return TRACK_GRID;
        } else if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_LIST) {
            return TRACK_LIST;
        } else {
            return TRACK_SINGLE;
        }
    }

    private void setCatalogAdapter(CatalogPage catalogPage) {
        CatalogGridAdapter adapter = new CatalogGridAdapter(getBaseActivity(),
                catalogPage.getProducts());
        // Add listener
        adapter.setOnViewHolderClickListener(this);
        mGridView.setAdapter(adapter);
        if (mCatalogGridPosition >= IntConstants.DEFAULT_POSITION) {
            mGridView.getLayoutManager().scrollToPosition(mCatalogGridPosition);
        }

    }

//    private void sendRecommend(CatalogPage catalogPage) {
//        RecommendManager recommendManager = new RecommendManager();
//        RecommendListCompletionHandler handler = (category, data) -> {
//        };
//
//        ArrayList<String> categories = catalogPage.getBreadcrumb();
//        if (categories != null && categories.size() > 0) {
//            String category = android.text.TextUtils.join(">", categories);
//            recommendManager
//                    .sendCategoryRecommend(catalogPage.getSearchTerm(), category, 6, handler);
//        }
//    }
}