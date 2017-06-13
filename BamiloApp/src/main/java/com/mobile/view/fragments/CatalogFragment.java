package com.mobile.view.fragments;

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
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.emarsys.predict.RecommendedItem;
import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.EventConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.EventFactory;
import com.mobile.helpers.products.GetCatalogPageHelper;
import com.mobile.helpers.search.SearchHelper;
import com.mobile.helpers.wishlist.AddToWishListHelper;
import com.mobile.helpers.wishlist.RemoveFromWishListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnProductViewHolderClickListener;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.mobile.extlibraries.emarsys.predict.recommended.RecommendManager;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.catalog.Catalog;
import com.mobile.service.objects.catalog.CatalogPage;
import com.mobile.service.objects.catalog.FeaturedBox;
import com.mobile.service.objects.catalog.filters.CatalogFilter;
import com.mobile.service.objects.product.pojo.ProductRegular;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.rest.errors.ErrorCode;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.catalog.CatalogGridAdapter;
import com.mobile.utils.catalog.CatalogSort;
import com.mobile.utils.catalog.FeaturedBoxHelper;
import com.mobile.utils.catalog.HeaderFooterGridView;
import com.mobile.utils.catalog.UICatalogUtils;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.dialogfragments.CatalogPageToastView;
import com.mobile.utils.dialogfragments.DialogSortListFragment;
import com.mobile.utils.dialogfragments.DialogSortListFragment.OnDialogListListener;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * Catalog fragment.
 *
 * @author sergiopereira
 */
public class CatalogFragment extends BaseFragment implements IResponseCallback, OnProductViewHolderClickListener, OnDialogListListener {

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

    //DROID-10
    private long mGABeginRequestMillis;

    private boolean showNoResult;
    private boolean sortChanged = false;

    /**
     * Empty constructor
     */
    public CatalogFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.CATALOG,
                R.layout.catalog_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }

    /*
     * ############ LIFE CYCLE ############
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        mGABeginRequestMillis = System.currentTimeMillis();
        // Load line to active top button
        mTopButtonActivateLine = setButtonActiveLine(mLevel);
        // Get data from arguments (Home/Categories/Deep link)
        Bundle arguments = getArguments();
        if (arguments != null) {
            Print.i(TAG, "ON RECEIVE ARGUMENTS: " + arguments);
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
            // Verify if catalog page was open via navigation drawer
            mCategoryTree = arguments.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
            //Get category content/main category
            mMainCategory = arguments.getString(RestConstants.MAIN_CATEGORY);
            mCurrentFilterValues = arguments.getParcelable(FILTER_TAG);
            if (mCurrentFilterValues == null) {
                mCurrentFilterValues = new ContentValues();
            }
            // Get sort from Deep Link
            if(arguments.containsKey(ConstantsIntentExtra.CATALOG_SORT)){
                mSelectedSort = CatalogSort.values()[arguments.getInt(ConstantsIntentExtra.CATALOG_SORT)];
            }


            showNoResult = false;

        }

        // Get data from saved instance
        if (savedInstanceState != null) {
            Print.i(TAG, "ON SAVED STATE: " + savedInstanceState);
            mTitle = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mQueryValues = savedInstanceState.getParcelable(ConstantsIntentExtra.CATALOG_QUERY_VALUES);
            mCatalogPage = savedInstanceState.getParcelable(ConstantsIntentExtra.CATALOG_PAGE);
            mCurrentFilterValues = savedInstanceState.getParcelable(ConstantsIntentExtra.CATALOG_FILTER_VALUES);
            mSelectedSort = CatalogSort.values()[savedInstanceState.getInt(ConstantsIntentExtra.CATALOG_SORT)];
            mSortOrFilterApplied = savedInstanceState.getBoolean(ConstantsIntentExtra.CATALOG_CHANGES_APPLIED);
            mCatalogGridPosition = savedInstanceState.getInt(ConstantsIntentExtra.CATALOG_PAGE_POSITION, IntConstants.INVALID_POSITION);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Load user preferences
        mLevel = Integer.parseInt(CustomerPreferences.getCatalogLayout(getBaseActivity()));
        // Get number of columns
        int dimen = mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID ? R.integer.catalog_grid_num_columns : R.integer.catalog_list_num_columns;
        mNumberOfColumns = getResources().getInteger(dimen);
        // Get sort button
        mSortButton = (TextView) view.findViewById(R.id.catalog_bar_button_sort);
        mSortDescription = (TextView) view.findViewById(R.id.catalog_bar_description_sort);
        mSortIcon = (ImageView) view.findViewById(R.id.catalog_bar_sort);
        // Get filter button
        mFilterButton = view.findViewById(R.id.catalog_filter_button);
        // Get switch button
        ImageView mColumnsButton = (ImageView) view.findViewById(R.id.catalog_bar_button_columns);
        mColumnsButton.setOnClickListener(this);
        mColumnsButton.setImageLevel(mLevel);
        mTopButtonActivateLine = setButtonActiveLine(mLevel);
        // Get up button
        mTopButton = view.findViewById(R.id.catalog_button_top);
        mTopButton.setOnClickListener(this);
        // Get feature box
        mNoResultStub = view.findViewById(R.id.catalog_no_result_stub);
        // Get grid view
        mGridView = (HeaderFooterGridView) view.findViewById(R.id.catalog_grid_view);
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
        Print.i(TAG, "ON START");
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
        Print.i(TAG, "ON RESUME");
        // Track current catalog page
        TrackerDelegator.trackPage(TrackingPage.PRODUCT_LIST, getLoadTime(), false);
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
        Print.i(TAG, "ON SAVE INSTANCE STATE");
        if (mGridView != null) {
            mCatalogGridPosition = ((GridLayoutManager) mGridView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        // Save the current content
        outState.putInt(ConstantsIntentExtra.CATALOG_PAGE_POSITION, mCatalogGridPosition);
        outState.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        outState.putParcelable(ConstantsIntentExtra.CATALOG_QUERY_VALUES, mQueryValues);
        outState.putParcelable(ConstantsIntentExtra.CATALOG_PAGE, mCatalogPage);
        outState.putParcelable(ConstantsIntentExtra.CATALOG_FILTER_VALUES, mCurrentFilterValues);
        outState.putInt(ConstantsIntentExtra.CATALOG_SORT, mSelectedSort != null ? mSelectedSort.ordinal() : CatalogSort.POPULARITY.ordinal());
        outState.putBoolean(ConstantsIntentExtra.CATALOG_CHANGES_APPLIED, mSortOrFilterApplied);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        if (mGridView != null) {
            mCatalogGridPosition = ((GridLayoutManager) mGridView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
        // Remove scroll listener
        if (mGridView != null) {
            mGridView.removeOnScrollListener(mOnScrollListener);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    @Override
    public void notifyFragment(@Nullable Bundle bundle) {
        super.notifyFragment(bundle);
        if(bundle != null && bundle.containsKey(FilterMainFragment.FILTER_TAG)){
            onSubmitFilterValues((ContentValues) bundle.getParcelable(FilterMainFragment.FILTER_TAG));
        }
        /*if(bundle != null && bundle.containsKey(FilterMainFragment.FILTER_CATEGORY)){
            mQueryValues.remove("category");
            mQueryValues.put("category", bundle.getString(FilterMainFragment.FILTER_CATEGORY));
        }*/

    }

    /*
     * ############## LAYOUT ##############
     */

    /**
     * Validate the current data.<br>
     * - Case required arguments are empty show continue shopping<br>
     * - Case is empty get data<br>
     * - Case not empty show data<br>
     */
    private void onValidateDataState() {
        Print.i(TAG, "ON VALIDATE DATA STATE");
        // Case URL or QUERY is empty show continue shopping
        if (!mQueryValues.containsKey(RestConstants.CATEGORY) && !mQueryValues.containsKey(RestConstants.SELLER) &&
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
            TextView filterDesc = (TextView) mFilterButton.findViewById(R.id.catalog_bar_description_filter);

            UICatalogUtils.setFilterButtonState(mFilterButton, mCurrentFilterValues, filterDesc, mCatalogPage);

        }
        // Case catalog was recover
        else {
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
        Print.i(TAG, "ON RECOVER CATALOG");
        // Set title bar
        UICatalogUtils.setCatalogTitle(getBaseActivity(), mTitle);
        // Set sort button
        setSortButton();
        // Set filter button
        UICatalogUtils.setFilterButtonActionState(mFilterButton, catalogPage.hasFilters(), this);
        // Set the filter button selected or not
        TextView filterDesc = (TextView) mFilterButton.findViewById(R.id.catalog_bar_description_filter);
        UICatalogUtils.setFilterButtonState(mFilterButton, mCurrentFilterValues, filterDesc, mCatalogPage);
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
        setFilterDescription(catalogPage);

    }

    /**
     * Updated the catalog container.
     *
     * @param catalogPage - The current catalog page
     */
    private void onUpdateCatalogContainer(CatalogPage catalogPage) {
        Print.i(TAG, "ON UPDATE CATALOG CONTAINER: " + catalogPage.getPage());
        // Case first time save catalog
        mCatalogPage = catalogPage;
        /*if (mCatalogPage == null) {
            mCatalogPage = catalogPage;
        }
        // Case load more then update data or Case filter applied then replace the data
        else {
            mCatalogPage.update(catalogPage);
        }*/

        // Validate current catalog page
        CatalogGridAdapter adapter = (CatalogGridAdapter) mGridView.getAdapter();
        if (adapter == null) {
            // Create adapter new data
            setCatalogAdapter(mCatalogPage);
            // Set filter button
            UICatalogUtils.setFilterButtonActionState(mFilterButton, mCatalogPage.hasFilters(), this);
            TextView filterDesc = (TextView) mFilterButton.findViewById(R.id.catalog_bar_description_filter);
            UICatalogUtils.setFilterButtonState(mFilterButton, mCurrentFilterValues, filterDesc, mCatalogPage);
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
        if (mCurrentFilterValues.size()==0) {
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
        Print.i(TAG, "ON SHOW FILTER NO RESULT");
        // Set title
        UICatalogUtils.setCatalogTitle(getBaseActivity(), mTitle);
        // Show layout
        showErrorFragment(stringId, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Print.d(TAG, "ON CLICK: FILTER BUTTON");
                onClickFilterButton();
            }
        });
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
        Print.i(TAG, "ON SHOW FEATURED BOX");
        // Inflate view
        mNoResultStub.setVisibility(View.VISIBLE);
        // Show featured box
        if (FeaturedBoxHelper.show(this, featuredBox)) {
            // Case success show container
            showFragmentContentContainer();

        } else {
            // Case fail show continue
            Print.e(TAG, "No featureBox!");
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
                    triggerAddToWishList(mClicked.getSku());
                    TrackerDelegator.trackAddToFavorites(mClicked);
                    TrackerManager.postEvent(getBaseActivity(), EventConstants.AddToFavorites, EventFactory.addToFavorites(mClicked.getCategoryKey(), true));
                }
                args.remove(AddToWishListHelper.ADD_TO_WISHLIST);
            }
            // Case remove from wish list
            else if (args.containsKey(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST)) {
                ProductRegular mClicked = args.getParcelable(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST);
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
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrandName() + " " + product.getName());
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
            // Goto PDV
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            showWarningErrorMessage(getString(R.string.error_occured));
        }
    }

    @Override
    public void onViewHolderItemClick(View view,RecyclerView.Adapter<?> adapter, int position) {
        // Get item
        mWishListItemClicked = ((CatalogGridAdapter) adapter).getItem(position);
        // Validate customer is logged in
        if (BamiloApplication.isCustomerLoggedIn()) {
            if (view.isSelected()) {
                triggerRemoveFromWishList(mWishListItemClicked.getSku());
                TrackerDelegator.trackRemoveFromFavorites(mWishListItemClicked);
            } else {
                triggerAddToWishList(mWishListItemClicked.getSku());
                TrackerDelegator.trackAddToFavorites(mWishListItemClicked);
                TrackerManager.postEvent(getBaseActivity(), EventConstants.AddToFavorites, EventFactory.addToFavorites(mMainCategory, true));
            }
        } else {
            // Save values to end action after login
            final Bundle args = getArguments();
            if(args != null) {
                if (view.isSelected()) {
                    args.putParcelable(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST, mWishListItemClicked);
                } else {
                    args.putParcelable(AddToWishListHelper.ADD_TO_WISHLIST, mWishListItemClicked);
                }
            }
            // Goto login
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Updated the clicked product to add to or remove from wish list.
     */
    private void updateWishListProduct() {
        if(mWishListItemClicked != null && mGridView != null && mGridView.getAdapter() != null) {
            mGridView.getAdapter().notifyDataSetChanged();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Print.i(TAG, "ON CLICK VIEW");
        // Get id
        int id = view.getId();
        // Case sort button
        if (id == R.id.catalog_bar_button_sort || id == R.id.catalog_bar_sort || id == R.id.catalog_sort_button
                || id == R.id.catalog_bar_description_sort) {
            onClickSortButton();
        }
        // Case filter button
        else if (id == R.id.catalog_bar_button_filter || id == R.id.catalog_bar_filter || id == R.id.catalog_filter_button
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

    private void onClickFilterButton(){
        Print.i(TAG, "ON CLICK FILTER BUTTON");
        try {
            // Show dialog
            Bundle bundle = new Bundle();
            bundle.putString("category_url", mKey);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, mKey);
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
            ArrayList<CatalogFilter> filters = mCatalogPage.getFilters();
            bundle.putParcelableArrayList(FILTER_TAG, filters);
            getBaseActivity().onSwitchFragment(FragmentType.FILTERS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SHOW DIALOG FRAGMENT");
        }






    }

    /**
     * Process the filter values.
     *
     * @param filterValues - the new content values from dialog
     */
    private void onSubmitFilterValues(ContentValues filterValues) {
        Print.i(TAG, "ON SUBMIT FILTER VALUES: " + filterValues);
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
        TrackerDelegator.trackCatalogFilter(mCurrentFilterValues);
    }

    /**
     * Process the click on Columns button
     *
     * @param button - the clicked view
     */
    private void onClickSwitchColumnsButton(View button) {
        Print.i(TAG, "ON CLICK COLUMNS BUTTON");
        try {
            mLevel =((ImageView)button).getDrawable().getLevel();
            switchCatalogView();
            // Save user preference
            CustomerPreferences.saveCatalogLayout(getBaseActivity(), ""+mLevel);
            // Update the icon
            ((ImageView)button).setImageLevel(mLevel);
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
            TrackerDelegator.trackCatalogSwitchLayout(trackView());
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SWITCH CATALOG COLUMNS", e);
        }
    }

    /**
     * Process the click on button to go top
     */
    private void onClickGotoTopButton() {
        Print.i(TAG, "ON CLICK SCROLL TOP BUTTON");
        GridLayoutManager manager = (GridLayoutManager) mGridView.getLayoutManager();
        int columns = manager.getSpanCount();
        // Scroll faster until mark line
        mGridView.scrollToPosition(columns * mTopButtonActivateLine);
        // Scroll smooth until top position
        mGridView.post(new Runnable() {
            @Override
            public void run() {
                mGridView.smoothScrollToPosition(FIRST_POSITION);
            }
        });
    }

    /**
     * method that calculates the line number where back to top button shows
     *
     * @return line number
     */
    private int setButtonActiveLine(int level){
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
        Print.i(TAG, "ON CLICK SORT BUTTON");
        // Create array list of strings
        ArrayList<String> mSortOptions = new ArrayList<>();
        for (CatalogSort sort : CatalogSort.values()) {
            mSortOptions.add(getString(sort.name));
        }
        // Show dialog
        DialogSortListFragment.newInstance(this, this, "sort", getString(R.string.sort_by), mSortOptions, mSelectedSort.ordinal()).show(getChildFragmentManager(), null);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener#onDialogListItemSelect(java.lang.String, int, java.lang.String)
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
        TrackerDelegator.trackCatalogSorter(mSelectedSort);
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

    protected void setVisibilityTopButton(RecyclerView recyclerView) {
        // Set the goto top button
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        int last = manager.findLastVisibleItemPosition();
        // Show or hide top button after X arrow
        if (last > mNumberOfColumns * mTopButtonActivateLine) {
            UICatalogUtils.showGotoTopButton(getBaseActivity(), mTopButton);
        } else {
            UICatalogUtils.hideGotoTopButton(getBaseActivity(), mTopButton);
        }
    }

    /*
     * ############## TRIGGERS ##############
     */

    /**
     * Trigger to add item from wish list.
     */
    private void triggerAddToWishList(String sku) {
        triggerContentEventProgress(new AddToWishListHelper(), AddToWishListHelper.createBundle(sku), this);
    }

    /**
     * Trigger to remove item from wish list.
     */
    private void triggerRemoveFromWishList(String sku) {
        triggerContentEventProgress(new RemoveFromWishListHelper(), RemoveFromWishListHelper.createBundle(sku), this);
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
     * Trigger used to get a catalog.<br> Is sent the URL, arguments and indication to save or not related items.
     */
    private void triggerGetCatalogPage(int page) {
        Print.i(TAG, "TRIGGER GET PAGINATED CATALOG");
        // Create catalog request parameters
        mQueryValues.put(RestConstants.PAGE, page);
        // Get filters
        if (mCurrentFilterValues != null) mQueryValues.putAll(mCurrentFilterValues);
        // Get Sort
        if (mSelectedSort != null && TextUtils.isNotEmpty(mSelectedSort.path)) {
            mQueryValues.put(RestConstants.SORT, mSelectedSort.path);
        }
        // Case initial request or load more
        if (page == IntConstants.FIRST_PAGE) {
            triggerContentEvent(new GetCatalogPageHelper(), GetCatalogPageHelper.createBundle(mQueryValues), this);
        } else {
            triggerContentEventNoLoading(new GetCatalogPageHelper(), GetCatalogPageHelper.createBundle(mQueryValues), this);
        }
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
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        super.handleSuccessEvent(baseResponse);
        // Hide dialog progress
        hideActivityProgress();
        // Validate event type
        switch (eventType) {
            case REMOVE_PRODUCT_FROM_WISH_LIST:
            case ADD_PRODUCT_TO_WISH_LIST:
                updateWishListProduct();
                break;

            case GET_CATALOG_EVENT:
            default:
                onRequestCatalogSuccess(baseResponse);

                //DROID-10
                TrackerDelegator.trackScreenLoadTiming(R.string.gaCatalog, mGABeginRequestMillis, mTitle);
                break;
        }
    }

    /**
     * Process the catalog success response.
     */
    private void onRequestCatalogSuccess(BaseResponse baseResponse) {
        // Get the catalog
        CatalogPage catalogPage = ((Catalog) baseResponse.getMetadata().getData()).getCatalogPage();
        sendRecommend(catalogPage);
        // Case valid success response
        if (catalogPage != null && catalogPage.hasProducts()) {
            // Mark to reload an initial catalog
            mSortOrFilterApplied = false;

            // IF API is not retrieving the sort, and we already have a sort selected, keep it, otherwise use POPULARITY
            mSelectedSort = TextUtils.isNotEmpty(catalogPage.getSort()) ?
                    CatalogSort.valueOf(catalogPage.getSort()) :
                    (mSelectedSort != null ? mSelectedSort : CatalogSort.POPULARITY);

            Print.i(TAG, "CATALOG PAGE: " + catalogPage.getPage()+" "+mSelectedSort);
            onUpdateCatalogContainer(catalogPage);
            if (catalogPage.getPage() == 1) {
                TrackerDelegator.trackCatalogPageContent(mCatalogPage, mCategoryTree, mMainCategory);
                TrackerManager.postEvent(getBaseActivity(), EventConstants.Search, EventFactory.search(mMainCategory, SearchHelper.getSearchTermsCommaSeparated(catalogPage.getSearchTerm())));

                int actionBarHeight = 180;
                TypedValue tv = new TypedValue();
                if (getBaseActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
                }
                actionBarHeight *= 2;
                int dp = (int) (getResources().getDimension(R.dimen.dimen_10dp) / getResources().getDisplayMetrics().density);

                actionBarHeight += dp;//TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, R.dimen.dimen_10dp, getResources().getDisplayMetrics());
                Toast toast = CatalogPageToastView.makeText(getBaseActivity(), "تعداد محصول: " + catalogPage.getTotal(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, actionBarHeight);
                toast.show();
            }
        }
        // Case invalid success response
        else {
            Print.w(TAG, "WARNING: RECEIVED INVALID CATALOG PAGE");
            showContinueShopping();
        }
        if (mCurrentFilterValues.size()==0) {
            setFilterDescription(catalogPage);
        }

    }

    private void setFilterDescription(CatalogPage catalogPage) {
        TextView filterDesc = (TextView) mFilterButton.findViewById(R.id.catalog_bar_description_filter);
        String filterNames = "";

        if (catalogPage.hasFilters()) {
            filterNames = catalogPage.getFilters().get(0).getName();
            if (catalogPage.getFilters().size()>1) filterNames = filterNames + ", " + catalogPage.getFilters().get(1).getName();
            if (catalogPage.getFilters().size()>2) filterNames = filterNames + ", ...";
        }
        filterDesc.setText(filterNames);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR");
        // Get error code
        EventType eventType = baseResponse.getEventType();
        // Validate fragment state
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
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
            Print.i(TAG, "ON ERROR RESPONSE: IS LOADING MORE");
            onLoadingMoreRequestError(baseResponse);
        }
        // Case error on request data with filters
        else if (errorCode == ErrorCode.REQUEST_ERROR && CollectionUtils.isNotEmpty(mCurrentFilterValues)) {
            Print.i(TAG, "ON SHOW FILTER NO RESULT");
            showFilterNoResult();
        }
        // Case error on request data without filters
        else if (errorCode == ErrorCode.REQUEST_ERROR && catalog != null && catalog.getFeaturedBox() != null) {
            Print.i(TAG, "ON SHOW NO RESULT");
            // Get feature box
            FeaturedBox featuredBox = catalog.getFeaturedBox();
            // Show no result layout
            showFeaturedBoxNoResult(featuredBox);
            showNoResult = true;
        }
        // Case network errors except No network
        else if (ErrorCode.isNetworkError(errorCode)
                && errorCode != ErrorCode.NO_CONNECTIVITY
                && errorCode != ErrorCode.HTTP_STATUS
                && errorCode != ErrorCode.SERVER_OVERLOAD
                && errorCode != ErrorCode.SERVER_IN_MAINTENANCE
                && CollectionUtils.isNotEmpty(mCurrentFilterValues)) {
            showFilterUnexpectedError();
        }
        // Case No Network
        else if (super.handleErrorEvent(baseResponse)) {
            Print.i(TAG, "HANDLE BASE FRAGMENT");
        }
        // Case unexpected error
        else {
            showContinueShopping();
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
        if (!super.handleErrorEvent(baseResponse)) showUnexpectedErrorWarning();
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
    private void switchCatalogView(){
        if(mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_LIST){
            mLevel = CatalogGridAdapter.ITEM_VIEW_TYPE_SINGLE;
        } else if(mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID) {
            mLevel = CatalogGridAdapter.ITEM_VIEW_TYPE_LIST;
        } else {
            mLevel = CatalogGridAdapter.ITEM_VIEW_TYPE_GRID;
        }
    }

    /**
     * Gets the number of columns defined for a specfic view
     * @return columns number
     */
    private int updateCatalogColumnsNumber(){
        if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID) {
            return getResources().getInteger(R.integer.catalog_grid_num_columns);
        } else {
            return getResources().getInteger(R.integer.catalog_list_num_columns);
        }
    }

    /**
     *
     * @return the type of the current catalog view
     */
    private String trackView(){
        if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_GRID) {
            return  TRACK_GRID;
        } else if (mLevel == CatalogGridAdapter.ITEM_VIEW_TYPE_LIST){
            return TRACK_LIST;
        } else {
            return TRACK_SINGLE;
        }
    }

    private void setCatalogAdapter(CatalogPage catalogPage){
        CatalogGridAdapter adapter = new CatalogGridAdapter(getBaseActivity(), catalogPage.getProducts());
        // Add listener
        adapter.setOnViewHolderClickListener(this);
        mGridView.setAdapter(adapter);
        if(mCatalogGridPosition >= IntConstants.DEFAULT_POSITION){
            mGridView.getLayoutManager().scrollToPosition(mCatalogGridPosition);
        }

    }

    private void sendRecommend(CatalogPage catalogPage) {
        /*recommendedAdapter.clear();
        recommendedAdapter.notifyDataSetChanged();
        recyclerView.invalidate();*/
        RecommendManager recommendManager = new RecommendManager();
        RecommendListCompletionHandler handler = new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {

            }
        };


       /* ArrayList<ProductRegular> productList = catalogPage.getProducts();

        List<String> excludedItems = null;

        if (productList != null && productList.size() > 0) {
            if (productList.size() > 1) {
                excludedItems = new ArrayList<>();
                for (int i = 1; i > productList.size(); i++) {
                    excludedItems.add(productList.get(i).getSku());
                }
            }

            recommendManager.sendRelatedRecommend(null,
                    catalogPage.getSearchTerm(),
                    productList.get(0).getSku(),
                    excludedItems,
                    handler);

        }*/
        //else {

        ArrayList<String> categories = catalogPage.getBreadcrumb();
        if (categories != null && categories.size()>0) {
            String category = android.text.TextUtils.join(">", categories);

            recommendManager.sendCategoryRecommend(catalogPage.getSearchTerm(), category, handler);
        }
        //}
    }

}