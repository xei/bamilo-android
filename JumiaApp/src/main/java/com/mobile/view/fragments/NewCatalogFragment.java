package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.CatalogGridAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.CatalogPage;
import com.mobile.framework.objects.FeaturedBox;
import com.mobile.framework.objects.Product;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.products.GetCatalogPageHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnDialogFilterListener;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.catalog.CatalogSort;
import com.mobile.utils.catalog.FeaturedBoxHelper;
import com.mobile.utils.dialogfragments.DialogFilterFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * 
 * @author sergiopereira
 *
 */
public class NewCatalogFragment extends BaseFragment implements IResponseCallback, OnViewHolderClickListener, OnDialogFilterListener, OnDialogListListener {
    
    private static final String TAG = NewCatalogFragment.class.getSimpleName();
    
    private RecyclerView mGridView;

    private View mSortButton;

    private View mFilterButton;

    private View mCollumnsButton;

    private View mTopButton;

    private View mLoadingMore;

    private String mCatalogUrl;

    private String mSearchQuery;
    
    private String mBrandQuery;

    private CatalogPage mCatalogPage;

    private String mTitle;

    private View mNoResultStub;

    private View mWizardStub;

    private ContentValues mCurrentFilterValues = new ContentValues();

    private CatalogSort mSelectedSort = CatalogSort.POPULARITY;
    
    /**
     * 
     * @param bundle
     * @return
     */
    public static NewCatalogFragment getInstance(Bundle bundle) {
        NewCatalogFragment catalogFragment = new NewCatalogFragment();
        catalogFragment.setArguments(bundle);
        return catalogFragment;
    }

    /**
     * Empty constructor
     */
    public NewCatalogFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.catalog_fragment_main_new,
                NO_TITLE,
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
        
        // Get data from arguments
        Bundle arguments = getArguments();
        if(arguments != null) {
            Log.i(TAG, "ARGUMENTS: " + arguments.toString());
            mTitle = arguments.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mCatalogUrl = arguments.getString(ConstantsIntentExtra.CONTENT_URL);
            mSearchQuery = arguments.getString(ConstantsIntentExtra.SEARCH_QUERY);
        }

        // Get data from saved instance
        if (savedInstanceState != null) {
            Log.i(TAG, "SAVED STATE: " + savedInstanceState.toString());
            mTitle = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mCatalogUrl = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_URL);
            mSearchQuery = savedInstanceState.getString(ConstantsIntentExtra.SEARCH_QUERY);
            mCatalogPage = savedInstanceState.getParcelable(ConstantsIntentExtra.CATALOG_PAGE);
            mCurrentFilterValues = savedInstanceState.getParcelable("current_filter_values");
            mBrandQuery = savedInstanceState.getString("current_filter_brand");
            mSelectedSort = CatalogSort.values()[savedInstanceState.getInt("sort")];
        }
    }
    

    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CRETED");
        
        // Load user preferences
        boolean isToShowGridLayout = CustomerPreferences.getCatalogLayout(getBaseActivity());
        int numberOfColumns = isToShowGridLayout ? R.integer.catalog_grid_num_columns : R.integer.catalog_list_num_columns; 
        
        // Get sort button 
        mSortButton = view.findViewById(R.id.catalog_bar_button_sort);
        mSortButton.setOnClickListener(this);
        // Get filter button
        mFilterButton = view.findViewById(R.id.catalog_bar_button_filter);
        // Get switch button
        mCollumnsButton = view.findViewById(R.id.catalog_bar_button_columns);
        mCollumnsButton.setOnClickListener(this);
        mCollumnsButton.setSelected(isToShowGridLayout);
        // Get up button
        mTopButton = view.findViewById(R.id.catalog_button_top);
        mTopButton.setOnClickListener(this);
        // Get feature box
        mNoResultStub = view.findViewById(R.id.catalog_no_result_stub);
        // Get wizard
        mWizardStub = view.findViewById(R.id.catalog_wizard_stub);
        // Get loading more
        mLoadingMore = view.findViewById(R.id.catalog_loading_more);
        // Get grid view
        mGridView = (RecyclerView) view.findViewById(R.id.catalog_grid_view);
        mGridView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getBaseActivity(), getResources().getInteger(numberOfColumns));
        manager.setSmoothScrollbarEnabled(true);
        mGridView.setLayoutManager(manager);
        
        mGridView.setOnScrollListener(new OnScrollListener() {
            
            private int visibleItemCount;
            private int total;
            private int firstVisibleItem;
            private int last;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // TODO Auto-generated method stub
                super.onScrolled(recyclerView, dx, dy);
                
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                
                int columns = manager.getSpanCount();
                
                visibleItemCount = recyclerView.getChildCount();
                total = manager.getItemCount();
                firstVisibleItem = manager.findFirstVisibleItemPosition();
                last = manager.findLastVisibleItemPosition();
                
                /*--
                Log.i(TAG, "ON SCROLL: " +
                		"CC:" + visibleItemCount + " " +
                		"IC:" + totalItemCount + " " +
                		"FI:" + firstVisibleItem + " " +
                		"LI:" + lastVisibleItem);
                 */
                
                int page = mCatalogPage.getPage();
                int max = mCatalogPage.getMaxPages();
                
                //Log.i(TAG, "MAX PAGES: " + page + " " + max);
                
                // Show or hide top button after 3 arrow
                if(last > columns * 3) mTopButton.setVisibility(View.VISIBLE);
                else  mTopButton.setVisibility(View.GONE);
                
                // Load more items
                if(page < max && last + 1 == total && mLoadingMore.getVisibility() == View.GONE) {
                    mLoadingMore.setVisibility(View.VISIBLE);
                    triggerGetPaginatedCalalog();
                }
                
            }
            
            /*
             * (non-Javadoc)
             * @see android.support.v7.widget.RecyclerView.OnScrollListener#onScrollStateChanged(android.support.v7.widget.RecyclerView, int)
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "ON SCROLL STATE CHANGED: " + newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    RocketImageLoader.getInstance().stopProcessingQueue();
                } else {
                    RocketImageLoader.getInstance().startProcessingQueue();
                }
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
        // Case URL or QUERY is empty show continue shopping 
        if(TextUtils.isEmpty(mCatalogUrl) && TextUtils.isEmpty(mSearchQuery)) showContinueShopping();
        // Case catalog is null get catalog from URL
        else if(mCatalogPage == null) triggerGetPaginatedCalalog();
        // Case catalog was recover
        else onRecoverCatalogContainer(mCatalogPage);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE");
        // Save the current content
        outState.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        outState.putString(ConstantsIntentExtra.CONTENT_URL, mCatalogUrl);
        outState.putString(ConstantsIntentExtra.SEARCH_QUERY, mSearchQuery);
        outState.putParcelable(ConstantsIntentExtra.CATALOG_PAGE, mCatalogPage);
        outState.putParcelable("current_filter_values", mCurrentFilterValues);
        outState.putString("current_filter_brand", mBrandQuery);
        outState.putInt("sort", mSelectedSort.ordinal());
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
    
    /*
     * ############## LAYOUT ##############
     */
    
    /**
     * 
     * @param catalogPage
     */
    private void onRecoverCatalogContainer(CatalogPage catalogPage) {
        Log.i(TAG, "ON RECOVER CATALOG");
        // Create adapter new data
        CatalogGridAdapter adapter = new CatalogGridAdapter(getBaseActivity(), catalogPage.getProducts());
        adapter.setOnViewHolderClickListener(this);
        mGridView.setAdapter(adapter);
        // Set filter button
        setFilterButtonActionState(catalogPage.hasFilters());
        // Set the filter button selected or not
        setFilterButtonState();
        // Set sort button
        // TODO
        // Validate loading more view 
        if(isLoadingMore()) mLoadingMore.setVisibility(View.GONE);
        // Show container
        showFragmentContentContainer();
    }
    
    
    /**
     * 
     * @param catalogPage
     */
    private void onUpdateCatalogContainer(CatalogPage catalogPage) {
        Log.i(TAG, "ON UPDATE CATALOG CONTAINER: " + catalogPage.getPage());
        
        // Case first time save catalog
        if(mCatalogPage == null) mCatalogPage = catalogPage;
        // Case load more then update data or Case filter applied then replace the data
        else mCatalogPage.update(catalogPage);
        
        // Set filter button
        setFilterButtonActionState(mCatalogPage.hasFilters());
        
        // Validate current catalog page
        CatalogGridAdapter adapter = (CatalogGridAdapter) mGridView.getAdapter();
        if(adapter == null) {
            // Create adapter new data
            adapter = new CatalogGridAdapter(getBaseActivity(), catalogPage.getProducts());
            adapter.setOnViewHolderClickListener(this);
            mGridView.setAdapter(adapter);
        }
        // Case load more append the new data
        else if(isLoadingMore()) {
            // Hide loading
            mLoadingMore.setVisibility(View.GONE);
            // Append new data
            adapter.updateData(catalogPage.getProducts());
        }
        // Case filter applied/clean replace the current data
        else {
            // Replace the data
            adapter.replaceData(catalogPage.getProducts());            
        }
        // Show container
        showFragmentContentContainer();
    }
    
    
    /**
     * Set the filter button state, to show as selected or not
     * 
     * @author sergiopereira
     */
    private void setFilterButtonState() {
        try {
            // Contains md5
            mFilterButton.setSelected((mCurrentFilterValues.size() == 0) ? false : true);
            Log.d(TAG, "SET FILTER BUTTON STATE: " + mFilterButton.isSelected());
        } catch (NullPointerException e) {
            Log.w(TAG, "BUTTON OR VALUE IS NULL", e);
        }
    }
    
    /**
     * set button state when catalog show no internet connection error
     */
    private void setFilterButtonActionState(boolean selectable){
        if (mFilterButton != null) {
            if (!selectable) {
                mFilterButton.setOnClickListener(null);
                mFilterButton.setEnabled(false);
            } else {
                mFilterButton.setOnClickListener(this);
                mFilterButton.setEnabled(true);
            }
        }
    }
    
    /**
     * 
     * @return
     */
    private boolean isLoadingMore() {
        return mLoadingMore != null ? mLoadingMore.getVisibility() == View.VISIBLE : false ; 
    }
    
    /**
     * 
     */
    private void showFilterNoResult() {
        Log.i(TAG, "ON SHOW FILTER NO RESULT");
        // Set title
        getBaseActivity().setSubTitle(" (" + 0 + " " + getBaseActivity().getString(R.string.shoppingcart_items) + ")");
        // Show layout
        showFragmentEmpty(R.string.catalog_no_results, R.drawable.img_filternoresults, R.string.catalog_edit_filters, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ON CLICK: FILTER BUTTON");
                onClickFilterButton();
            }
        });
    }
    
    /**
     * 
     * @param stub
     * @param featuredBox
     */
    private void showFeaturedBoxNoResult(FeaturedBox featuredBox) {
        Log.i(TAG, "ON SHOW FEATURED BOX");
        // Inflate view
        mNoResultStub.setVisibility(View.VISIBLE);
        // Show featured box
        if(FeaturedBoxHelper.show(this, mSearchQuery, featuredBox)) {
            // Case success show container
            showFragmentContentContainer();
        } else {
            // Case fail show continue
            Log.e(TAG, "No featureBox!");
            showContinueShopping();
        }
    }
    
    /*
     * ############## LISTENERS ##############
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickErrorButton(android.view.View)
     */
    @Override
    protected void onClickErrorButton(View view) {
        super.onClickErrorButton(view);
        onResume();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onRetryRequest(com.mobile.framework.utils.EventType)
     */
    @Override
    protected void onRetryRequest(EventType eventType) {
        onResume();
    }
        
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.OnViewHolderClickListener#onViewHolderClick(android.support.v7.widget.RecyclerView.Adapter, android.view.View, int)
     */
    @Override
    public void onViewHolderClick(Adapter<?> adapter, View view, int position, Object extra) {
        // Get item
        Product product = ((CatalogGridAdapter) adapter).getItem(position);
        // Call Product Details        
        if (product != null) {
            // Show product
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, product.getUrl());
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrand() + " " + product.getName());
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true); 
            // Goto PDV
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Toast.makeText(getBaseActivity(), R.string.error_occured, Toast.LENGTH_SHORT).show();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Log.i(TAG, "ON CLICK VIEW");
        // Get id
        int id = view.getId();
        // Case sort button
        if(id == R.id.catalog_bar_button_sort) onClickSortButton();
        // Case filter button
        else if(id == R.id.catalog_bar_button_filter) onClickFilterButton();
        // Case columns button
        else if(id == R.id.catalog_bar_button_columns) onClickSwichColumnsButton(view);
        // Case top button
        else if(id == R.id.catalog_button_top) onClickScrollTopButton();
        // Case default
        else super.onClick(view); 
    }
    


    /**
     * 
     */
    private void onClickFilterButton() {
        Log.i(TAG, "ON CLICK FILTER BUTTON");
        try {
            // Show dialog
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(DialogFilterFragment.FILTER_TAG, mCatalogPage.getFilters());
            DialogFilterFragment newFragment = DialogFilterFragment.newInstance(bundle, this);
            newFragment.show(getBaseActivity().getSupportFragmentManager(), "dialog");
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON SHOW DIALOG FRAGMENT");
        }
    }
    
    /**
     * Process the filter values
     * 
     * @param filterValues
     * @author sergiopereira
     */
    public void onSubmitFilterValues(ContentValues filterValues) {
        Log.i(TAG, "ON SUBMIT FILTER VALUES: " + filterValues.toString());
        // Validate new filterValues and current are the same
        // TODO
        // Contains the new search query (Brand filter)
        if (filterValues.containsKey(DialogFilterFragment.BRAND)) {
            // Used to indicate that has filter q=<BRAND>
            mBrandQuery = filterValues.getAsString(DialogFilterFragment.BRAND);
            //filterValues.put("brand", filterValues.getAsString(GetProductsHelper.SEARCH_QUERY));
            //filterValues.remove(GetProductsHelper.SEARCH_QUERY);
        // Clean brand filter
        } else {
            mBrandQuery = null;
        }
        // Save the current filter values
        mCurrentFilterValues = filterValues;
        // Set the filter button selected or not
        setFilterButtonState();
        // Get new catalog
        triggerGetFilteredOrSortedCatalog();
    }

    /**
     * Process the click on Columns button
     * @param view
     */
    private void onClickSwichColumnsButton(View view) {
        Log.i(TAG, "ON CLICK COLUMNS BUTTON");
        // Case selected is showing the GRID LAYOUT and the LIST ICON
        boolean isShowingGridLayout = view.isSelected();
        // Save user preference 
        CustomerPreferences.saveCatalogLayout(getBaseActivity(), !isShowingGridLayout);
        // Update the icon
        view.setSelected(!isShowingGridLayout);
        // Update the columns and layout
        int numberOfColumns = isShowingGridLayout ? R.integer.catalog_list_num_columns : R.integer.catalog_grid_num_columns;
        GridLayoutManager manager = (GridLayoutManager) mGridView.getLayoutManager();
        manager.setSpanCount(getResources().getInteger(numberOfColumns));
        ((CatalogGridAdapter) mGridView.getAdapter()).updateLayout(!isShowingGridLayout);
    }
    
    /**
     * 
     */
    private void onClickScrollTopButton() {
        Log.i(TAG, "ON CLICK SCROLL TOP BUTTON");
        // TODO
        mGridView.smoothScrollToPosition(0);
    }
    
    /**
     * 
     */
    private void onClickSortButton() {
        Log.i(TAG, "ON CLICK SORT BUTTON");
        // Create array list of strings
        ArrayList<String> mSortOptions = new ArrayList<String>();
        for (CatalogSort sort : CatalogSort.values()) {
            mSortOptions.add(getString(sort.name));
        }
        // Show dialog
        DialogListFragment
        .newInstance(this, (OnDialogListListener) this, "sort", getString(R.string.sort_by), mSortOptions, mSelectedSort.ordinal())
        .show(getChildFragmentManager(), null);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener#onDialogListItemSelect(java.lang.String, int, java.lang.String)
     */
    @Override
    public void onDialogListItemSelect(String id, int position, String value) {
        // Get selected sort position
        mSelectedSort  = CatalogSort.values()[position];
        // Get new data
        triggerGetFilteredOrSortedCatalog();
    }
    
    /*
     * ############## TRIGGERS ##############
     */
    /**
     * 
     */
    private void triggerGetFilteredOrSortedCatalog() {
        // Get first page
        triggerGetCatalogPage(GetCatalogPageHelper.INITIAL_PAGE_NUMBER);
    }
    
    /**
     * 
     */
    private void triggerGetPaginatedCalalog() {
        // Get next page
        int page = mCatalogPage == null ? GetCatalogPageHelper.INITIAL_PAGE_NUMBER : mCatalogPage.getPage() + 1;
        // Get catalog page
        triggerGetCatalogPage(page);
    }
    
    /**
     * Trigger used to get a catalog.<br>
     * Is sent the URL, arguments and indication to save or not related items.
     * @author sergiopereira
     */
    private void triggerGetCatalogPage(int page) {
        Log.i(TAG, "TRIGGER GET PAGINATED CATALOG");
        // Create catalog request parameters
        ContentValues catalogValues = new ContentValues();
        catalogValues.put(GetCatalogPageHelper.QUERY, TextUtils.isEmpty(mBrandQuery) ? mSearchQuery : mBrandQuery);
        catalogValues.put(GetCatalogPageHelper.PAGE, page);
        catalogValues.put(GetCatalogPageHelper.MAX_ITEMS, GetCatalogPageHelper.MAX_ITEMS_PER_PAGE);
        catalogValues.put(GetCatalogPageHelper.SORT, mSelectedSort.id);
        catalogValues.put(GetCatalogPageHelper.DIRECTION, mSelectedSort.direction);
        catalogValues.putAll(mCurrentFilterValues);
        
        // Create bundle with url and parameters
        Bundle bundle = new Bundle();
        bundle.putString(GetCatalogPageHelper.URL, mCatalogUrl);
        bundle.putParcelable(GetCatalogPageHelper.CATALOG_ARGUMENTS, catalogValues);
        bundle.putBoolean(GetCatalogPageHelper.SAVE_RELATED_ITEMS, saveRelatedItems(page));
        
        // Case initial request or load more
        if(page == GetCatalogPageHelper.INITIAL_PAGE_NUMBER) {
            triggerContentEvent(new GetCatalogPageHelper(), bundle, this);
        } else {
            triggerContentEventNoLoading(new GetCatalogPageHelper(), bundle, this);
        }
    }
    
    /**
     * Validate if is to save some request items as related items.
     * @param page
     * @return true or false
     * @author sergiopereira
     */
    private boolean saveRelatedItems(int page) {
        try {
            // Is to save related items in case popularity sort, first page and not filter applied
            return  mCurrentFilterValues.size() == 0 && 
                    mSelectedSort.ordinal() == CatalogSort.POPULARITY.ordinal() &&
                    page == GetCatalogPageHelper.INITIAL_PAGE_NUMBER;   
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
        Log.i(TAG, "ON SUCCESS");
        // Validate fragment state
        if (isOnStoppingProcess){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        } 
        
        // TODO
        CatalogPage catalogPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        Log.i(TAG, "CATALOG PAGE: " + catalogPage.getPage());
        onUpdateCatalogContainer(catalogPage);
    }


    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON ERROR: " + mCurrentFilterValues.toString());
        // Validate fragment state
        if (isOnStoppingProcess){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        } 
        // Get error code
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        int type = bundle.getInt(Constants.BUNDLE_OBJECT_TYPE_KEY);
        // Case error on load more data
        if(isLoadingMore()) {
            // TODO: improve the method to mark the loading more error
            mLoadingMore.setVisibility(View.INVISIBLE);
            if (errorCode == ErrorCode.SERVER_IN_MAINTENANCE) super.handleErrorEvent(bundle);
            else if (errorCode == ErrorCode.NO_NETWORK) ToastFactory.ERROR_NO_CONNECTION.show(getBaseActivity());
            else ToastFactory.ERROR_CATALOG_LOAD_MORE.show(getBaseActivity());
        } 
        // Case error on request data with filters
        else if(mCurrentFilterValues !=  null && mCurrentFilterValues.size() > 0) {
            Log.i(TAG, "ON SHOW FILTER NO RESULT");
            showFilterNoResult();
        }
        // Case error on request data without filters
        else if (errorCode != null && errorCode == ErrorCode.REQUEST_ERROR && type == GetCatalogPageHelper.FEATURE_BOX_TYPE) {
            Log.i(TAG, "ON SHOW NO RESULT");
            // Get feature box
            FeaturedBox featuredBox = (FeaturedBox) bundle.get(Constants.BUNDLE_RESPONSE_KEY);
            // Show no result layout
            showFeaturedBoxNoResult(featuredBox);
        }
        // Case network errors
        else if (super.handleErrorEvent(bundle));
        // Case unexpected eror
        else showContinueShopping();
    }




    
}
