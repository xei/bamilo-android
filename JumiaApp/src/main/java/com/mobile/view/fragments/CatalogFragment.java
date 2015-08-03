package com.mobile.view.fragments;

import android.content.ContentValues;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AbsListView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.products.GetCatalogPageHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnDialogFilterListener;
import com.mobile.interfaces.OnHeaderClickListener;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.catalog.CatalogPage;
import com.mobile.newFramework.objects.catalog.FeaturedBox;
import com.mobile.newFramework.objects.catalog.ITargeting;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.objects.product.Product;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.catalog.CatalogGridAdapter;
import com.mobile.utils.catalog.CatalogGridView;
import com.mobile.utils.catalog.CatalogSort;
import com.mobile.utils.catalog.FeaturedBoxHelper;
import com.mobile.utils.catalog.UICatalogHelper;
import com.mobile.utils.dialogfragments.DialogFilterFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.dialogfragments.WizardPreferences;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Catalog fragment.
 *
 * @author sergiopereira
 */
public class CatalogFragment extends BaseFragment implements IResponseCallback, OnViewHolderClickListener, OnDialogFilterListener, OnDialogListListener, OnHeaderClickListener {

    private static final String TAG = CatalogFragment.class.getSimpleName();

    private final static String TRACK_LIST = "list";

    private final static String TRACK_GRID = "grid";

    private final static int FIRST_POSITION = 0;

    private final static int EMPTY_CATALOG = 0;

    private CatalogGridView mGridView;

    private TextView mSortButton;

    private View mFilterButton;

    private View mTopButton;

    private String mCatalogUrl;

    private CatalogPage mCatalogPage;

    private String mTitle;

    private View mNoResultStub;

    private ViewStub mWizardStub;

    private ContentValues mCurrentFilterValues = new ContentValues();

    private CatalogSort mSelectedSort = CatalogSort.POPULARITY;

    private boolean mErrorLoading = false;

    private boolean isLoadingMoreData = false;

    private int mNumberOfColumns;

    private int mTopButtonActivateLine;

    private boolean mSortOrFilterApplied; // Flag to reload or not an initial catalog in case generic error

    private boolean mIsToShowGridLayout = false;

    private String mCategoryId; // Verify if catalog page was open via navigation drawer

    private String mCategoryTree;

    private ContentValues mQueryValues;

    /**
     * Create and return a new instance.
     *
     * @param bundle - arguments
     */
    public static CatalogFragment getInstance(Bundle bundle) {
        CatalogFragment catalogFragment = new CatalogFragment();
        catalogFragment.setArguments(bundle);
        return catalogFragment;
    }

    /**
     * Empty constructor
     */
    public CatalogFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.catalog_fragment_main,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
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
        // Load line to active top button
        mTopButtonActivateLine = setButtonActiveLine(mIsToShowGridLayout);
        // Get data from arguments (Home/Categories/Deep link)
        Bundle arguments = getArguments();
        if (arguments != null) {
            Print.i(TAG, "ARGUMENTS: " + arguments.toString());
            mTitle = arguments.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mQueryValues = new ContentValues();
            setParameters(arguments.getString(ConstantsIntentExtra.CONTENT_URL));
            if (arguments.containsKey(ConstantsIntentExtra.CATALOG_SORT)) {
                mSelectedSort = CatalogSort.values()[arguments.getInt(ConstantsIntentExtra.CATALOG_SORT)];
            }
//            // Verify if catalog page was open via navigation drawer
            mCategoryId = arguments.getString(ConstantsIntentExtra.CATALOG_SOURCE);
            mCategoryTree = arguments.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
        }

        // Get data from saved instance
        if (savedInstanceState != null) {
            Print.i(TAG, "SAVED STATE: " + savedInstanceState.toString());
            mTitle = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mCatalogUrl = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_URL);
            mQueryValues = savedInstanceState.getParcelable(ConstantsIntentExtra.CATALOG_QUERY_VALUES);
            mCatalogPage = savedInstanceState.getParcelable(ConstantsIntentExtra.CATALOG_PAGE);
            mCurrentFilterValues = savedInstanceState.getParcelable(ConstantsIntentExtra.CATALOG_FILTER_VALUES);
            mSelectedSort = CatalogSort.values()[savedInstanceState.getInt(ConstantsIntentExtra.CATALOG_SORT)];
            mSortOrFilterApplied = savedInstanceState.getBoolean(ConstantsIntentExtra.CATALOG_CHANGES_APPLIED);
        }
        // Track most viewed category
        TrackerDelegator.trackCategoryView();
    }

    private void setParameters(String url){
        UrlQuerySanitizer query = new UrlQuerySanitizer(url);
        mCatalogUrl = url.substring(0, url.indexOf('?'));
        mQueryValues.put(GetCatalogPageHelper.MAX_ITEMS, GetCatalogPageHelper.MAX_ITEMS_PER_PAGE);
        mQueryValues.put(GetCatalogPageHelper.SORT, mSelectedSort.id);
        mQueryValues.put(GetCatalogPageHelper.DIRECTION, mSelectedSort.direction);

        for (UrlQuerySanitizer.ParameterValuePair filter : query.getParameterList()) {
            mQueryValues.put(filter.mParameter, filter.mValue);
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
        mIsToShowGridLayout = CustomerPreferences.getCatalogLayout(getBaseActivity());
        mNumberOfColumns = getResources().getInteger(mIsToShowGridLayout ? R.integer.catalog_grid_num_columns : R.integer.catalog_list_num_columns);
        // Get sort button 
        mSortButton = (TextView) view.findViewById(R.id.catalog_bar_button_sort);
        // Get filter button
        mFilterButton = view.findViewById(R.id.catalog_bar_button_filter);
        // Get switch button
        View mColumnsButton = view.findViewById(R.id.catalog_bar_button_columns);
        mColumnsButton.setOnClickListener(this);
        mColumnsButton.setSelected(mIsToShowGridLayout);
        mTopButtonActivateLine = setButtonActiveLine(mIsToShowGridLayout);
        // Get up button
        mTopButton = view.findViewById(R.id.catalog_button_top);
        mTopButton.setOnClickListener(this);
        // Get feature box
        mNoResultStub = view.findViewById(R.id.catalog_no_result_stub);
        // Get wizard
        mWizardStub = (ViewStub) view.findViewById(R.id.catalog_wizard_stub);
        // Get grid view
        mGridView = (CatalogGridView) view.findViewById(R.id.catalog_grid_view);
        mGridView.setHasFixedSize(true);
        mGridView.setGridLayoutManager(mNumberOfColumns);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.setOnScrollListener(onRecyclerScrollListener);
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
        // Navigation
        if (!TextUtils.isEmpty(mCategoryId) && getBaseActivity() != null) {
            getBaseActivity().updateNavigationCategorySelection(mCategoryId);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE");
        // Save the current content
        outState.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        outState.putString(ConstantsIntentExtra.CONTENT_URL, mCatalogUrl);
        outState.putParcelable(ConstantsIntentExtra.CATALOG_QUERY_VALUES, mQueryValues);
        outState.putParcelable(ConstantsIntentExtra.CATALOG_PAGE, mCatalogPage);
        outState.putParcelable(ConstantsIntentExtra.CATALOG_FILTER_VALUES, mCurrentFilterValues);
        outState.putInt(ConstantsIntentExtra.CATALOG_SORT, mSelectedSort.ordinal());
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
        if (TextUtils.isEmpty(mCatalogUrl) && (!mQueryValues.containsKey(GetCatalogPageHelper.CATEGORY) || !mQueryValues.containsKey(GetCatalogPageHelper.QUERY))) {
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
            UICatalogHelper.setFilterButtonState(mFilterButton, mCurrentFilterValues.size() > 0);
        }
        // Case catalog was recover
        else {
            onRecoverCatalogContainer(mCatalogPage);
            TrackerDelegator.trackCatalogPageContent(mCatalogPage, mCategoryTree, mQueryValues.getAsString(GetCatalogPageHelper.CATEGORY));
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
        UICatalogHelper.setCatalogTitle(getBaseActivity(), mTitle, mCatalogPage.getTotal());
        // Set sort button
        setSortButton();
        // Set filter button
        UICatalogHelper.setFilterButtonActionState(mFilterButton, catalogPage.hasFilters(), this);
        // Set the filter button selected or not
        UICatalogHelper.setFilterButtonState(mFilterButton, mCurrentFilterValues.size() > 0);
        // Create adapter new data
        CatalogGridAdapter adapter = new CatalogGridAdapter(getBaseActivity(), catalogPage.getProducts());
        adapter.setOnViewHolderClickListener(this);
        mGridView.setAdapter(adapter);
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
        // Validate if is to show wizard
        UICatalogHelper.isToShowWizard(this, mWizardStub, this);
    }

    /**
     * Updated the catalog container.
     *
     * @param catalogPage - The current catalog page
     */
    private void onUpdateCatalogContainer(CatalogPage catalogPage) {
        Print.i(TAG, "ON UPDATE CATALOG CONTAINER: " + catalogPage.getPage());
        // Case first time save catalog
        if (mCatalogPage == null) {
            mCatalogPage = catalogPage;
        }
        // Case load more then update data or Case filter applied then replace the data
        else {
            mCatalogPage.update(catalogPage);
        }

        // Validate current catalog page
        CatalogGridAdapter adapter = (CatalogGridAdapter) mGridView.getAdapter();
        if (adapter == null) {
            // Create adapter new data
            adapter = new CatalogGridAdapter(getBaseActivity(), mCatalogPage.getProducts());
            adapter.setOnViewHolderClickListener(this);
            mGridView.setAdapter(adapter);
            // Set filter button
            UICatalogHelper.setFilterButtonActionState(mFilterButton, catalogPage.hasFilters(), this);
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
            adapter = new CatalogGridAdapter(getBaseActivity(), mCatalogPage.getProducts());
            adapter.setOnViewHolderClickListener(this);
            mGridView.setAdapter(adapter);
            // Hide the goto top button
            UICatalogHelper.hideGotoTopButton(getBaseActivity(), mTopButton);
        }

        // Save title
        mTitle = catalogPage.getName();
        // Set title bar
        UICatalogHelper.setCatalogTitle(getBaseActivity(), mTitle, mCatalogPage.getTotal());
        // Show header
        if (catalogPage.getPage() == CatalogPage.FIRST_PAGE) {
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
        // Validate if is to show wizard
        UICatalogHelper.isToShowWizard(this, mWizardStub, this);
    }

    /**
     * Method responsible for validating if the catalog page has banner, and if it so, show it
     */
    private void showHeaderBanner(){
        // Show header
        if(mGridView != null && mCatalogPage.getmCatalogBanner() != null){
            ((CatalogGridAdapter) mGridView.getAdapter()).setOnHeaderClickListener(this);
            mGridView.setHeaderView(mCatalogPage);
        }
    }

    /**
     * Set the sort button with the current sort selection.
     */
    private void setSortButton() {
        mSortButton.setText(getString(mSelectedSort.name));
        mSortButton.setOnClickListener(this);
    }

    /**
     * Show the no filter layout error.
     *
     * @param  stringId The message.
     */
    private void showFilterError(int stringId) {
        Print.i(TAG, "ON SHOW FILTER NO RESULT");
        // Set title
        UICatalogHelper.setCatalogTitle(getBaseActivity(), mTitle, EMPTY_CATALOG);
        // Show layout
//        showFragmentEmpty(stringId, R.drawable.img_filternoresults, R.string.catalog_edit_filters, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Print.d(TAG, "ON CLICK: FILTER BUTTON");
//                onClickFilterButton();
//            }
//        });
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
    private void showFilterUnexpectedError(){
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
    public void onViewHolderClick(Adapter<?> adapter, int position) {
        // Get item
        Product product = ((CatalogGridAdapter) adapter).getItem(position);
        // Call Product Details        
        if (product != null) {
            // Show product
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, product.getUrl());
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrand() + " " + product.getName());
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
            bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
            // Goto PDV
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            ToastFactory.ERROR_OCCURRED.show(getBaseActivity());
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
        if (id == R.id.catalog_bar_button_sort) {
            onClickSortButton();
        }
        // Case filter button
        else if (id == R.id.catalog_bar_button_filter) {
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
        // Case wizard
        else if (id == R.id.catalog_wizard_button_ok) {
            onClickWizardButton();
        }
        // Case default
        else {
            super.onClick(view);
        }
    }

    /**
     * Process the click on wizard button
     */
    private void onClickWizardButton() {
        Print.i(TAG, "ON CLICK FILTER BUTTON");
        WizardPreferences.changeState(getBaseActivity(), WizardPreferences.WizardType.CATALOG);
        mWizardStub.setVisibility(View.GONE);
    }

    /**
     * Process the click on filter button
     */
    private void onClickFilterButton() {
        Print.i(TAG, "ON CLICK FILTER BUTTON");
        try {
            // Show dialog
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(DialogFilterFragment.FILTER_TAG, mCatalogPage.getFilters());
            DialogFilterFragment newFragment = DialogFilterFragment.newInstance(bundle, this);
            newFragment.show(getBaseActivity().getSupportFragmentManager(), null);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SHOW DIALOG FRAGMENT");
        }
    }

    /**
     * Process the filter values.
     *
     * @param filterValues - the new content values from dialog
     */
    public void onSubmitFilterValues(ContentValues filterValues) {
        Print.i(TAG, "ON SUBMIT FILTER VALUES: " + filterValues.toString());
        // Contains the new search query (Brand filter)
//        if (filterValues.containsKey(DialogFilterFragment.BRAND)) {
//            // Used to indicate that has filter q=<BRAND>
//            mBrandQuery = filterValues.getAsString(DialogFilterFragment.BRAND);
//        }
//        // Clean brand filter
//        else {
//            mBrandQuery = null;
//        }
        // Save the current filter values
        mCurrentFilterValues = filterValues;
        // Set the filter button selected or not
        UICatalogHelper.setFilterButtonState(mFilterButton, mCurrentFilterValues.size() > 0);
        // Flag to reload or not an initial catalog in case generic error
        mSortOrFilterApplied = true;
        // Get new catalog
        triggerGetInitialCatalogPage();
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
            // Case selected is showing the GRID LAYOUT and the LIST ICON
            boolean mIsToShowGridLayout = button.isSelected();
            // Save user preference
            CustomerPreferences.saveCatalogLayout(getBaseActivity(), !mIsToShowGridLayout);
            // Update the icon
            button.setSelected(!mIsToShowGridLayout);
            //change back to top line number
            mTopButtonActivateLine = setButtonActiveLine(!mIsToShowGridLayout);
            // Update the number of columns
            mNumberOfColumns = getResources().getInteger(!mIsToShowGridLayout ? R.integer.catalog_grid_num_columns : R.integer.catalog_list_num_columns);
            // Update the columns and layout
            GridLayoutManager manager = (GridLayoutManager) mGridView.getLayoutManager();
            manager.setSpanCount(mNumberOfColumns);
            manager.requestLayout();
            ((CatalogGridAdapter) mGridView.getAdapter()).updateLayout(!mIsToShowGridLayout);
            // Track catalog
            TrackerDelegator.trackCatalogSwitchLayout((!mIsToShowGridLayout) ? TRACK_LIST : TRACK_GRID);
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
    private int setButtonActiveLine(Boolean isShowingGridLayout){
        if (!isShowingGridLayout) {
            return getResources().getInteger(R.integer.activate_go_top_buttom_line);
        } else {
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
        DialogListFragment.newInstance(this, this, "sort", getString(R.string.sort_by), mSortOptions, mSelectedSort.ordinal()).show(getChildFragmentManager(), null);
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
    private OnScrollListener onRecyclerScrollListener = new OnScrollListener() {

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
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Print.i(TAG, "ON SCROLL STATE CHANGED: " + newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                RocketImageLoader.getInstance().stopProcessingQueue();
            } else {
                RocketImageLoader.getInstance().startProcessingQueue();
                setVisibilityTopButton(recyclerView);
            }
        }
    };

    protected void setVisibilityTopButton(RecyclerView recyclerView){
        // Set the goto top button
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        int last = manager.findLastVisibleItemPosition();
        // Show or hide top button after X arrow
        if (last > mNumberOfColumns * mTopButtonActivateLine) {
            UICatalogHelper.showGotoTopButton(getBaseActivity(), mTopButton);
        } else {
            UICatalogHelper.hideGotoTopButton(getBaseActivity(), mTopButton);
        }
    }

    /*
     * ############## TRIGGERS ##############
     */

    /**
     * Trigger the initialized catalog.<br> Used for filter and sort.
     */
    private void triggerGetInitialCatalogPage() {
        // Get first page
        triggerGetCatalogPage(GetCatalogPageHelper.FIRST_PAGE_NUMBER);
    }

    /**
     * Trigger the paginated catalog.
     */
    private void triggerGetPaginatedCatalog() {
        // Get next page
        int page = mCatalogPage == null ? GetCatalogPageHelper.FIRST_PAGE_NUMBER : mCatalogPage.getPage() + 1;
        // Get catalog page
        triggerGetCatalogPage(page);
    }

    /**
     * Trigger used to get a catalog.<br> Is sent the URL, arguments and indication to save or not related items.
     */
    private void triggerGetCatalogPage(int page) {
        Print.i(TAG, "TRIGGER GET PAGINATED CATALOG");
        // Create catalog request parameters
//        ContentValues catalogValues = new ContentValues();
//        catalogValues.put(GetCatalogPageHelper.QUERY, TextUtils.isEmpty(mBrandQuery) ? mSearchQuery : mBrandQuery);
        mQueryValues.put(GetCatalogPageHelper.PAGE, page);
        // Get filters
        mQueryValues.putAll(mCurrentFilterValues);
        // Create bundle with url and parameters
        Bundle bundle = new Bundle();
        bundle.putString(GetCatalogPageHelper.URL, mCatalogUrl);
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, mQueryValues);
        bundle.putBoolean(GetCatalogPageHelper.SAVE_RELATED_ITEMS, isToSaveRelatedItems(page));
        // Case initial request or load more
        if (page == GetCatalogPageHelper.FIRST_PAGE_NUMBER) {
            triggerContentEvent(new GetCatalogPageHelper(), bundle, this);
        } else {
            triggerContentEventNoLoading(new GetCatalogPageHelper(), bundle, this);
        }
    }

    /**
     * Validate if is to save some request items as related items.<br> Indicate to save related items in case:<br> - NO FILTER && POPULARITY &&
     * FIRST_PAGE_NUMBER
     *
     * @param page - the current page number
     * @return true or false
     */
    private boolean isToSaveRelatedItems(int page) {
        try {
            // Is to save related items in case popularity sort, first page and not filter applied
            return mCurrentFilterValues.size() == 0 &&
                    mSelectedSort.ordinal() == CatalogSort.POPULARITY.ordinal() &&
                    page == GetCatalogPageHelper.FIRST_PAGE_NUMBER;
        } catch (NullPointerException e) {
            return false;
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
    public void onRequestComplete(Bundle bundle) {
        Print.i(TAG, "ON SUCCESS");
        // Validate fragment state
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get the catalog
        CatalogPage catalogPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        // Case valid success response
        if (catalogPage != null && catalogPage.hasProducts()) {
            // Mark to reload an initial catalog
            mSortOrFilterApplied = false;
            Print.i(TAG, "CATALOG PAGE: " + catalogPage.getPage());
            onUpdateCatalogContainer(catalogPage);

            if (catalogPage.getPage() == 1) {
                TrackerDelegator.trackCatalogPageContent(mCatalogPage, mCategoryTree, mQueryValues.getAsString(GetCatalogPageHelper.CATEGORY));
            }

        }
        // Case invalid success response
        else {
            Print.w(TAG, "WARNING: RECEIVED INVALID CATALOG PAGE");
            showContinueShopping();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Print.i(TAG, "ON ERROR: " + mCurrentFilterValues.toString());
        // Validate fragment state
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get error code
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        int type = bundle.getInt(Constants.BUNDLE_OBJECT_TYPE_KEY);
        // Case error on load more data
        if (isLoadingMoreData) {
            Print.i(TAG, "ON ERROR RESPONSE: IS LOADING MORE");
            onLoadingMoreRequestError(bundle);
        }
        // Case error on request data with filters
        else if (errorCode != null && errorCode == ErrorCode.REQUEST_ERROR && CollectionUtils.isNotEmpty(mCurrentFilterValues)) {
            Print.i(TAG, "ON SHOW FILTER NO RESULT");
            showFilterNoResult();
        }
        // Case error on request data without filters
        else if (errorCode != null && errorCode == ErrorCode.REQUEST_ERROR && type == GetCatalogPageHelper.FEATURE_BOX_TYPE) {
            Print.i(TAG, "ON SHOW NO RESULT");
            // Get feature box
            FeaturedBox featuredBox = (FeaturedBox) bundle.get(Constants.BUNDLE_RESPONSE_KEY);
            // Show no result layout
            showFeaturedBoxNoResult(featuredBox);
        }
        // Case network errors except No network
        else if(errorCode != null && errorCode.isNetworkError()
                && errorCode != ErrorCode.NO_NETWORK
                && errorCode != ErrorCode.HTTP_STATUS
                && errorCode != ErrorCode.SERVER_OVERLOAD
                && errorCode != ErrorCode.SERVER_IN_MAINTENANCE
                && CollectionUtils.isNotEmpty(mCurrentFilterValues)){
            showFilterUnexpectedError();
        }
        // Case No Network
        else if (super.handleErrorEvent(bundle)) {
            Print.i(TAG, "HANDLE BASE FRAGMENT");
        }
        // Case unexpected error
        else {
            showContinueShopping();
        }
    }

    /**
     * Process the error code
     *
     * @param bundle    - the request bundle
     */
    private void onLoadingMoreRequestError(Bundle bundle) {
        // Mark error on loading more
        mErrorLoading = true;
        // Scroll to hide the loading view
        mGridView.stopScroll();
        mGridView.scrollBy(0, - getResources().getDimensionPixelSize(R.dimen.catalog_footer_height));
        // Show respective warning indicating to use the warning bar
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.SMALL_TASK);
        // Case super not handle the error show unexpected error
        if(!super.handleErrorEvent(bundle)) showUnexpectedErrorWarning();
    }

    @Override
    public void onHeaderClick(String targetType, String url, String title) {
        ITargeting.TargetType target = ITargeting.TargetType.byValue(targetType);
        Bundle bundle = new Bundle();
        switch (target){
            case CATALOG:
                onClickCatalog(url, title, bundle);
                break;
            case CAMPAIGN:
                //http://integration-www.jumia.ug/mobapi/v1.7/campaign/get/?campaign_slug=samsung_madness
                onClickCampaign(url, title, bundle);
                break;
            case PRODUCT:
                // http://integration-www.jumia.ug/mobapi/v1.7/blue-long-sleeves-shirt-straight-cut-24932.html
                onClickProduct(url, bundle);
                break;
            case SHOP:
                // http://integration-www.jumia.ug/mobapi/1.7/main/getstatic/?key=buy-2-get-1-free
                onClickInnerShop(url, title, bundle);
                break;
            default:
                break;
        }
    }

    protected void onClickCampaign(String targetUrl, String targetTitle, Bundle bundle) {
        // Tracking event
        AnalyticsGoogle.get().trackEvent(TrackingEvent.SHOW_CAMPAIGN, targetTitle, 0l);
        // Create campaign using the URL
        ArrayList<TeaserCampaign> campaigns = createSingleCampaign(targetTitle, targetUrl);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, campaigns);
        bundle.putInt(CampaignsFragment.CAMPAIGN_POSITION_TAG, 0);
        getBaseActivity().onSwitchFragment(FragmentType.CAMPAIGNS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

}