/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ProductsListAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.RelatedItemsTableHelper;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.objects.FeaturedBox;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.tracking.AdjustTracker;
import pt.rocket.framework.tracking.TrackingEvent;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Direction;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.ProductSort;
import pt.rocket.helpers.products.GetProductsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogFilterFragment;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author nunocastro
 * 
 */
public class CatalogPageFragment extends BaseFragment implements OnClickListener{

    public static final String TAG = CatalogPageFragment.class.getSimpleName();

    public static final String PARAM_PAGE_INDEX = "param_page_index";
    public static final String PARAM_FILTERS = "param_filters";
    public static final String PARAM_TITLE = "param_title";
    public static final String PARAM_IS_LANDSCAPE = "param_is_landscape";

    private final String PRODUCT_LIST = "product_list";
    private final String PRODUCT_LIST_POSITION = "product_list_position";
    private final String LIST_TITLE = "list_title";

    private final int MAX_PAGE_ITEMS = 24;
    private final int NO_MORE_PAGES = -1;

    private int numItemsToLoad = MAX_PAGE_ITEMS;

    private final int SCROLL_DELAY = 250;

    private CatalogFragment parentFragment;

    private ProductSort mSort = ProductSort.NONE;
    private Direction mDirection = Direction.ASCENDENT;

    private boolean mIsLandScape = false;

    private int mPageIndex = 0;
    private String mProductsURL;
    private String mSearchQuery;
    private String mNavigationPath;
    private String mTitle;
    private int mNavigationSource;
    private ContentValues mFilters;

    private long mBeginRequestMillis;
    private int mTotalProducts = -1;
    private boolean mIsLoadingMore = false;
    private int mPageNumber;
    private int mFilterMD5 = -1;
    private int mSwitchMD5;
    private ArrayList<String> mSavedProductsSKU;
    private int mCurrentListPosition = -1;

    // Flag used to stop the loading more when an error occurs
    private boolean mReceivedError = false;
    private boolean isFrench = false;

    // pc products_content
    private RelativeLayout relativeLayoutPc;
    // Lm loading_more
    private LinearLayout linearLayoutLm;
    // Products grid view
    private GridView gridView;
    // Button to go to top of list
    private Button btnToplist;

    // private LoadingBarView loadingBarView;

    private int numColumns;
    private int mFirstVisibleItem;

    private boolean reattached = false;

    private boolean isScrolling = false;
    
    private Bundle mReceivedDataInBackgroung = null;

    private TrackingEvent mTrackSortEvent = TrackingEvent.CATALOG_FROM_CATEGORIES;

    private final static int POPULARITY_PAGE_NUMBER = 1;
    
    private boolean trackViewScreen = false;
    
    private String categoryId = "";
    
    private boolean isFromCategory = true;
    
    private boolean isProductClear = false;
    
    /**
     * 
     * @param bundle
     * @return 
     * @author sergiopereira
     */
    public static CatalogPageFragment newInstance(Bundle bundle) {
        CatalogPageFragment catalogPageFragment = new CatalogPageFragment();
        catalogPageFragment.setArguments(bundle);
        return catalogPageFragment;
    }

    /**
     * Empty constructor
     */
    public CatalogPageFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.catalog_fragment_page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");

        SharedPreferences sharedPrefs = activity.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        isFrench = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, "en").contains("fr");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
//        CatalogFragment.hasFilterApllied = false;
        
        Bundle args = getArguments();
        
        parentFragment = (CatalogFragment) getBaseActivity().getSupportFragmentManager().findFragmentByTag(FragmentType.PRODUCT_LIST.toString());

        if (null != savedInstanceState) {            
           mSavedProductsSKU = savedInstanceState.getStringArrayList(PRODUCT_LIST);
            mCurrentListPosition = savedInstanceState.getInt(PRODUCT_LIST_POSITION);
            mTitle = savedInstanceState.getString(LIST_TITLE);
            Log.d(TAG, "SAVE INSTANCE STATE null != " +mSavedProductsSKU);
        }
        
        if (null != args) {
            mPageIndex = args.getInt(PARAM_PAGE_INDEX, 0);

            switch (mPageIndex) {
            case 0: // <item > Copy of Best Rating for infinite scroll</item>
                // option
                mSort = ProductSort.BESTRATING;
                mDirection = Direction.DESCENDENT;
                mTrackSortEvent = TrackingEvent.CATALOG_SORT_RATING;
                break;

            case 1: // <item >Popularity</item>
                mSort = ProductSort.POPULARITY;
                mDirection = Direction.DESCENDENT;
                mTrackSortEvent = TrackingEvent.CATALOG_SORT_POPULARIRY;
                break;

            case 2: // <item >Price up</item>
                mSort = ProductSort.NEWIN;
                mDirection = Direction.DESCENDENT;
                mTrackSortEvent = TrackingEvent.CATALOG_SORT_NEW_IN;
                break;

            case 3: // <item >Price up</item>
                mSort = ProductSort.PRICE;
                mDirection = Direction.ASCENDENT;
                mTrackSortEvent = TrackingEvent.CATALOG_SORT_PRICE_UP;
                break;

            case 4: // <item >Price down</item>
                mSort = ProductSort.PRICE;
                mDirection = Direction.DESCENDENT;
                mTrackSortEvent = TrackingEvent.CATALOG_SORT_PRICE_DOWN;
                break;

            case 5: // <item >Name</item>
                // Offer option
                mSort = ProductSort.NAME;
                mDirection = Direction.ASCENDENT;
                mTrackSortEvent = TrackingEvent.CATALOG_SORT_NAME;
                break;

            case 6: // <item >Brand</item>
                // option
                mSort = ProductSort.BRAND;
                mDirection = Direction.ASCENDENT;
                mTrackSortEvent = TrackingEvent.CATALOG_SORT_BRAND;
                break;
            }
            

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED #" + mPageIndex);
        
        if (savedInstanceState != null) {
            reattached = true;
        }

        if(isProductClear){
            mSavedProductsSKU = null;
//            isProductClear = false;
        } else {
            if (null != savedInstanceState)  mSavedProductsSKU = savedInstanceState.getStringArrayList(PRODUCT_LIST);
        }
        
        this.relativeLayoutPc = (RelativeLayout) view.findViewById(R.id.products_content);
        this.linearLayoutLm = (LinearLayout) view.findViewById(R.id.loadmore);
        this.gridView = (GridView) view.findViewById(R.id.middle_productslist_list);
        this.gridView.setOnItemClickListener(onItemClickListener);

        this.btnToplist = (Button) view.findViewById(R.id.btn_toplist);
        this.btnToplist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                smoothScroll();
            }
        });
        
        if(TextUtils.isEmpty(CatalogFragment.categoryId))isFromCategory = false;
        else isFromCategory = true;
    }

    private void smoothScroll() {
        // Use this flag to signal onSuccess() that scroll is in progress
        isScrolling = true;

        // Position scroll on position nItemsScroll and smooth scroll to position 0
        int nItemsToScroll = numColumns * 5;
        Log.d(TAG, "mFirstVisibleItem: " + mFirstVisibleItem + "; nItemsToScroll: " + nItemsToScroll);
        // Position scroll if past nItemsScroll. Delay smoothScroll
        if (mFirstVisibleItem > nItemsToScroll) {
            gridView.setSelection(nItemsToScroll);
        }
        gridView.smoothScrollToPosition(0);

        // Force scroll on position 0 after DELAY
        gridView.postDelayed(new Runnable() {
            @Override
            public void run() {
                gridView.setSelection(0);
                // after another DELAY verify is position was done and reset isScrolling
                // otherwise only onSuccess() will try to scroll further on
                gridView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mFirstVisibleItem == 0) {
                            isScrolling = false;
                        }
                    }
                }, SCROLL_DELAY);
            }
        }, SCROLL_DELAY);
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME #" + mPageIndex);
//        if(!CatalogFragment.hasFilterApllied){
            // Get arguments
            Bundle args = getArguments();
            boolean forceReload = false;
            /**
             * Validated if was received new arguments from invalidateData() and fragment is not visible.
             * Clean saved products and force reload.
             */
            if (mReceivedDataInBackgroung != null) {
                args = (Bundle) mReceivedDataInBackgroung.clone();
                mSavedProductsSKU = null;
                mReceivedDataInBackgroung = null;
            }
            // Show
            invalidateData(args, forceReload);
    }
    

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) trackViewCatalog();
        try {
            super.setUserVisibleHint(isVisibleToUser);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON android.support.v4.app.Fragment.setUserVisibleHint");
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        ProductsListAdapter adapter = (ProductsListAdapter) this.gridView.getAdapter();
        if (mTotalProducts > 0 && adapter != null) {
            mSavedProductsSKU = adapter.getProductsList();
            mCurrentListPosition = this.gridView.getFirstVisiblePosition();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "ON SAVE INSTANCE STATE #" + mPageIndex);
        if (null != this.gridView) {
            ProductsListAdapter adapter = (ProductsListAdapter) this.gridView.getAdapter();
            if (null != adapter) {
//                if(CatalogFragment.hasFilterApllied){
//                    outState.putStringArrayList(PRODUCT_LIST, null);   
//                } else {
                    outState.putStringArrayList(PRODUCT_LIST, adapter.getProductsList());
//                }
                outState.putInt(PRODUCT_LIST_POSITION, this.gridView.getFirstVisiblePosition());
            }
            outState.putString(LIST_TITLE, mTitle);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * track catalog view method
     */
    private void trackViewCatalog() {

            final Handler trackHandler = new Handler();
            final Runnable tracRunnable = new Runnable() {
                
                @Override
                public void run() {
                    
                    try {
                        ProductsListAdapter adapter = (ProductsListAdapter) gridView.getAdapter();
                        if (null != adapter && null != adapter.getProductsList() && adapter.getProductsList().size() > 0 && !isDetached() && isVisible()) {
                            Bundle bundle = new Bundle();
                            bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
                            bundle.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
                            if (JumiaApplication.CUSTOMER != null) bundle.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER); 
                            bundle.putString(AdjustTracker.CATEGORY, mTitle);
                            if(!TextUtils.isEmpty(categoryId) && TextUtils.isEmpty(CatalogFragment.categoryId)) bundle.putString(AdjustTracker.CATEGORY_ID, categoryId);
                            else bundle.putString(AdjustTracker.CATEGORY_ID, CatalogFragment.categoryId);
                            bundle.putString(AdjustTracker.TREE, CatalogFragment.categoryTree);    
                            bundle.putStringArrayList(AdjustTracker.TRANSACTION_ITEM_SKUS, adapter.getProductsList());
                            TrackerDelegator.trackPage(TrackingPage.PRODUCT_LIST_SORTED, getLoadTime(), false);
                            TrackerDelegator.trackPageForAdjust(TrackingPage.PRODUCT_LIST_SORTED, bundle);
                        }
                        else {
                            trackHandler.postDelayed(this, 300);
                        }
                    } catch (Exception e) {
                        Log.e("TRACK","EXCEPTION");
                        e.printStackTrace();
                    }
                }
            };
            trackHandler.postDelayed(tracRunnable, 300);

            TrackerDelegator.trackCatalogSorter(mSort.name().toString());
    }
    
    /*
     * Get total number of products
     */
    public int getTotalProducts() {
        return mTotalProducts;
    }

    public void restoreFilters(ContentValues filters) {
        mFilterMD5 = filters.getAsInteger("md5");
        mFilters = filters;
    }

    /*
     * force the refresh of the list to update the status of each item
     */
    public void invalidateData(final Bundle arguments, final boolean forceRefresh) {
        Log.i(TAG, "ON INVALIDATE DATA ");

        // update GridView when visible or if reattached after a rotation
        if (!isDetached() && (isVisible() || reattached)) {
            reattached = false;
            if (null != getView() && null == gridView) {
                this.gridView = (GridView) getView().findViewById(R.id.middle_productslist_list);
            } else if (null == getView() && null == gridView) {
                // This should never happen. Here to prevent crashes when fragment is not available
                return;
            }
    
            ContentValues newFilters = null;
            if (null != arguments) {
                updateLocalArguments(arguments);
                newFilters = arguments.getParcelable(PARAM_FILTERS);
            }
            ProductsListAdapter adapter = (ProductsListAdapter) gridView.getAdapter();
            boolean showList = parentFragment.getShowList();
            int switchMD5 = parentFragment.getSwitchMD5();
    
            // Case no content
            if (null == adapter) {
                Log.i(TAG, "ON RESUME -> Null Adapter");
                mFilters = newFilters;
                
                initializeCatalogPage(showList);
                
            } else if (newFilters != null && newFilters.getAsInteger("md5") != mFilterMD5) { // Case new filter
                Log.i(TAG, "ON RESUME -> FILTER IS DIFF: " + newFilters.getAsInteger("md5") + " " + mFilterMD5);
                mFilterMD5 = newFilters.getAsInteger("md5");
                mFilters = newFilters;
                mSavedProductsSKU = null;
                initializeCatalogPage(showList);
    
            } else if (mSwitchMD5 != switchMD5) {
                Log.i(TAG, "ON RESUME -> SWITCH LAYOUT");
    
                mCurrentListPosition = this.gridView.getFirstVisiblePosition();
    
                List<String> products = adapter.getProductsList();
                updateGridColumns(showList);
                adapter = new ProductsListAdapter(getBaseActivity(), parentFragment, showList, numColumns, isFrench);
                adapter.appendProducts(products);
                gridView.setAdapter(adapter);
                gridView.setSelection(mCurrentListPosition);
                gridView.setOnScrollListener(onScrollListener);
    
                if (products == null || products.isEmpty()) {
                    if (mFilters != null) {
                        showFiltersNoResults();
                    } else {
                        showProductsNotfound();
                    }
                } else {
                    showCatalogContent();
                }
    
                mIsLoadingMore = false;
    
                mSwitchMD5 = switchMD5;
            } else {
            }
    
            if (forceRefresh) {
                if (null != adapter) {
                    Bundle params = getArguments();
                    params.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
                    params.putString(ConstantsIntentExtra.CONTENT_URL, mProductsURL);
                    params.putString(ConstantsIntentExtra.SEARCH_QUERY, mSearchQuery);
                    params.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, mNavigationSource);
                    params.putString(ConstantsIntentExtra.NAVIGATION_PATH, mNavigationPath);
                    params.putParcelable(PARAM_FILTERS, mFilters);
    
                    Log.d(TAG, " --- INVALIDATE DATA on Adapter -> " + adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        // Case fragment is in background
        else if (null != arguments) { 
            Log.i(TAG, "ON INVALIDATE DATA: IS NOT VISIBLE");
            mReceivedDataInBackgroung = arguments;
        }
    }

    private void initializeCatalogPage(boolean showList) {
        initializeCatalogPage(showList, false);
    }

    /**
     * 
     * @param showList
     * @param forceGetMoreProducts used to ignore existing products and request products
     */
    private void initializeCatalogPage(boolean showList, boolean forceGetMoreProducts) {
        updateGridColumns(showList);
        Log.d(TAG, "ON RESUME - REQ -> Landscape ? " + mIsLandScape + "; Columns #" + numColumns);

        final boolean hasProducts = (null != mSavedProductsSKU && mSavedProductsSKU.size() > 0 );

        // initialize new adapter depending on view choosen
        ProductsListAdapter adapter = new ProductsListAdapter(getBaseActivity(), parentFragment, showList, numColumns, isFrench);
        if (!hasProducts) {
            mPageNumber = 1;
        } else {
            adapter.updateProducts(mSavedProductsSKU);
        }

        Log.d(TAG, "showProductsContent");
        if (mPageNumber == 1) {
            Log.i(TAG, "scrolling to position 0");
            this.gridView.setSelection(0);
        }
        this.gridView.setOnScrollListener(onScrollListener);

        relativeLayoutPc.setVisibility(View.VISIBLE);

        gridView.setAdapter(adapter);

        if (!hasProducts) {
            getMoreProducts();
        } else {
            gridView.setSelection(mCurrentListPosition);
            adapter.notifyDataSetChanged();

            if (adapter.getProductsList() == null || adapter.getProductsList().isEmpty()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mSavedProductsSKU != null && mFilters != null) {
                            showFiltersNoResults();
                        } else {
                            showProductsNotfound();
                        };
                    }
                }, 200);
            }
        }

    }

    private void updateLocalArguments(Bundle args) {
        updateLocalArguments(args, false);
    }

    private void updateLocalArguments(Bundle args, boolean updateFilters) {
        mTitle = args.getString(ConstantsIntentExtra.CONTENT_TITLE);
        mProductsURL = args.getString(ConstantsIntentExtra.CONTENT_URL);
        mSearchQuery = args.getString(ConstantsIntentExtra.SEARCH_QUERY);
        mNavigationSource = args.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);
        mNavigationPath = args.getString(ConstantsIntentExtra.NAVIGATION_PATH);
        // mIsLandScape = args.getBoolean(PARAM_IS_LANDSCAPE);
        mIsLandScape = BaseActivity.isTabletInLandscape(getBaseActivity());
        if (updateFilters)
            mFilters = args.getParcelable(PARAM_FILTERS);
    }

    /**
     * gets the next page of products from the API
     */
    private void getMoreProducts() {
        Log.d(TAG, "FILTER GET MORE PRODUCTS");

        if (mPageNumber != NO_MORE_PAGES) {

            mBeginRequestMillis = System.currentTimeMillis();

            Log.d(TAG, "FILTER CREATE BUNDLE");

            RocketImageLoader.getInstance().stopProcessingQueue();

            Bundle bundle = new Bundle();
            bundle.putString(GetProductsHelper.PRODUCT_URL, mProductsURL);
            bundle.putString(GetProductsHelper.SEARCH_QUERY, mSearchQuery);
            bundle.putInt(GetProductsHelper.PAGE_NUMBER, mPageNumber);
            bundle.putInt(GetProductsHelper.TOTAL_COUNT, numItemsToLoad);
            bundle.putInt(GetProductsHelper.SORT, mSort.id);
            bundle.putInt(GetProductsHelper.DIRECTION, mDirection.id);
            
            if(CatalogFragment.hasFilterApllied){
                
                mFilters = CatalogFragment.filterParams.getParcelable(PARAM_FILTERS);
                
                if(CatalogFragment.filterParams.containsKey(ConstantsIntentExtra.CONTENT_URL))
                    bundle.putString(GetProductsHelper.PRODUCT_URL, CatalogFragment.filterParams.getString(ConstantsIntentExtra.CONTENT_URL));
                
                if(CatalogFragment.filterParams.containsKey(ConstantsIntentExtra.SEARCH_QUERY))
                    bundle.putString(GetProductsHelper.SEARCH_QUERY, CatalogFragment.filterParams.getString(ConstantsIntentExtra.SEARCH_QUERY));
                
                
            }
            
            bundle.putParcelable(GetProductsHelper.FILTERS, mFilters );
            
            if(JumiaApplication.mIsBound){
                if(mPageNumber == 1) triggerContentEvent(new GetProductsHelper(), bundle, responseCallback);
                else triggerContentEventWithNoLoading(new GetProductsHelper(), bundle, responseCallback);

            } else {
                showFragmentRetry(this);
            }
        } else {
            hideProductsLoading();
        }
    }

    private void showCatalogContent() {
        relativeLayoutPc.setVisibility(View.VISIBLE);
        hideProductsLoading();
        if (getView() != null) {
            showFragmentContentContainer();
            // hideLoadingInfo();
        }
    }

    private void updateGridColumns(boolean showList) {
        // Tablet 10'' uses 4 columns for Grid
        // Tablet 7'' uses 3 columns for Grid
        // Tablet 10'' uses 3 columns for List
        // Tablet 7'' uses 2 columns for List
        // Phone uses 2 columns for Grid
        // Phone uses 1 column for List
        if (showList) {
            int numColumns = getBaseActivity().getResources().getInteger(R.integer.catalog_list_num_columns);
            this.numColumns = numColumns;
        } else {
            int numColumns = getBaseActivity().getResources().getInteger(R.integer.catalog_grid_num_columns);
            this.numColumns = numColumns;
        }
        this.gridView.setNumColumns(this.numColumns);
    }

    private void showProductsNotfound() {
        Log.d(TAG, "showProductsNotfound");
        hideProductsLoading(false);
        relativeLayoutPc.setVisibility(View.GONE);
        showFragmentRetry(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideProductsNotFound();
                getMoreProducts();
            }
        });
    }

    private void hideProductsNotFound() {
        showFragmentContentContainer();
    }

    private void showFiltersNoResults() {
        mSavedProductsSKU = null;
        Log.d(TAG, "showFiltersNoResults");
        hideProductsLoading(false);
        relativeLayoutPc.setVisibility(View.GONE);

        if (parentFragment.isVisible() && null != getBaseActivity()) {
            getBaseActivity().setSubTitle(" (" + 0 + " " + getBaseActivity().getString(R.string.shoppingcart_items) + ")");
        }
        showFragmentEmpty(R.string.catalog_no_results, R.drawable.img_filternoresults, R.string.catalog_edit_filters, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ON CLICK: FILTER BUTTON");
                if(parentFragment != null && parentFragment.getCatalogFilter() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(DialogFilterFragment.FILTER_TAG, parentFragment.getCatalogFilter());
                    DialogFilterFragment newFragment = DialogFilterFragment.newInstance(bundle, parentFragment);
                    newFragment.show(getBaseActivity().getSupportFragmentManager(), "dialog");
                }
            }
        });
    }

    private void showProductsLoading() {
        Log.d(TAG, "showProductsLoading");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                linearLayoutLm.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    private void hideProductsLoading() {
        hideProductsLoading(true);
    }

    private void hideProductsLoading(final boolean hideViewSpnf) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                linearLayoutLm.setVisibility(View.GONE);
                if (hideViewSpnf) {
                    showFragmentContentContainer();
                }
            }
        }, 200);
    }

    // ---------------------------------------------------------------
    // ----- Listeners
    // ---------------------------------------------------------------

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int activePosition = position;
            ProductsListAdapter adapter = (ProductsListAdapter) parent.getAdapter();

            if (-1 < activePosition && null != adapter) {
                // Call Product Details
                Product product = parentFragment.getProduct((String) adapter.getItem(activePosition));
                if (product != null) {
                    // Validate if dialog is on screen 
                    if(!CatalogFragment.isNotShowingDialogFilter) return;
                    // Disable filter button 
                    parentFragment.disableFilterButton();
                    // Show product
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, product.getUrl());
                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, mNavigationSource);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, mNavigationPath);
                    bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrand() + " " + product.getName());
                    if(!TextUtils.isEmpty(CatalogFragment.categoryTree)){
                        bundle.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME, CatalogFragment.categoryTree);
                    }
                    // inform PDV that Related Items should be shown
                    bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
                    if (mTitle != null) bundle.putString(ProductDetailsFragment.PRODUCT_CATEGORY, mTitle);
                    // Goto PDV
                    getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    Toast.makeText(getBaseActivity(), R.string.error_occured, Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    private OnScrollListener onScrollListener = new OnScrollListener() {

        private int previousFirstVisibleItem = 0;
        private long previousEventTime = 0;
        private double speed = 0;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                RocketImageLoader.getInstance().stopProcessingQueue(); // CatalogFragment.requestTag
            } else {
                // TODO test if notifyDataSetChanged is really needed
                /*-ProductsListAdapter adapter = (ProductsListAdapter) gridView.getAdapter();
                adapter.notifyDataSetChanged();*/
                RocketImageLoader.getInstance().startProcessingQueue();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            // Sample calculation to determine if the last item is fully
            // visible.
            if (totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
                if (!mIsLoadingMore && !mReceivedError) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mIsLoadingMore = true;
                            showProductsLoading();
                            getMoreProducts();
                        }
                    });
                }
            } else {
                mReceivedError = false;
            }

            // Calculate the speed of the list is scrolling to, so that when the
            // list slows down, re-enable the image loader processing queue
            if (previousFirstVisibleItem != firstVisibleItem) {
                long currTime = System.currentTimeMillis();
                long timeToScrollOneElement = currTime - previousEventTime;
                speed = ((double) 1 / timeToScrollOneElement) * 1000;

                previousFirstVisibleItem = firstVisibleItem;
                previousEventTime = currTime;

                if (speed < 10) {
                    RocketImageLoader.getInstance().startProcessingQueue();
                }
            }

            // show or hide btnToplist
            setBtnToplistVisibility(firstVisibleItem);
        }
    };

    /**
     * Hide button to scroll to top if first item of list is visible
     * 
     * @param firstVisibleItem
     */
    private void setBtnToplistVisibility(int firstVisibleItem) {
        if (firstVisibleItem == 0) {
            btnToplist.setVisibility(View.GONE);
            mFirstVisibleItem = 0;
        } else {
            btnToplist.setVisibility(View.VISIBLE);
            mFirstVisibleItem = firstVisibleItem;
        }
    }

    // ---------------------------------------------------------------
    // ----- API Request handlers
    // ---------------------------------------------------------------

    private IResponseCallback responseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    private void onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");
        
        // Validate fragment state
        if (isOnStoppingProcess) return;
        isProductClear = false;
        // Get Products Event
        final ProductsPage productsPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        categoryId = productsPage.getCategoryId();
        // Get Location
        final String location = bundle.getString(IMetaData.LOCATION);
        Log.d(TAG, "Location = " + location);

        // Validate title
//        if (TextUtils.isEmpty(mTitle)) {
            mTitle = productsPage.getName();
           
            if(CatalogFragment.filterParams != null && CatalogFragment.filterParams.containsKey(ConstantsIntentExtra.SEARCH_QUERY) &&
                    !TextUtils.isEmpty(CatalogFragment.filterParams.getString(ConstantsIntentExtra.SEARCH_QUERY)) ){
                   mTitle = CatalogFragment.filterParams.getString(ConstantsIntentExtra.SEARCH_QUERY);
            }
//        }

        int totalProducts = productsPage == null ? 0 : productsPage.getTotalProducts();
        int numberProducts = (productsPage == null || productsPage.getProducts() == null) ? 0 : productsPage.getProducts().size();

        // Validate products
        if (productsPage != null && totalProducts > 0) {
            Log.d(TAG, "onSuccessEvent: products on page = " + numberProducts + " total products = " + totalProducts);

            final int pageNumber = mPageNumber;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        // Persist related Items when initially loading products for POPULARITY tab
                        if (mPageIndex == POPULARITY_PAGE_NUMBER && pageNumber == 1) {
                            RelatedItemsTableHelper.insertRelatedItemsAndClear(getBaseActivity(), productsPage.getProductsList());
                        }/*- else if (mPageIndex == POPULARITY_PAGE_NUMBER && mPageNumber == 2) {
                            RelatedItemsTableHelper.insertRelatedItems(getBaseActivity(), productsPage.getProductsList());
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            parentFragment.addProductsCollection(productsPage.getProductsMap(), mTitle, productsPage.getTotalProducts());

            mTotalProducts = totalProducts;
        }

        if (location != null) {
            // check if redirected from search
            Uri uri = Uri.parse(location);
            if (null != mSearchQuery && !uri.getPath().equals(RestContract.REST_BASE_PATH + "/search/") && TextUtils.isEmpty(mProductsURL)) {
                Log.d(TAG, "detected redirect - setting properties according");
                mSearchQuery = null;
                mProductsURL = location;
            }
        }

        Bundle params = new Bundle();
        params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gproductlist);
        params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
        TrackerDelegator.trackLoadTiming(params);

        if (!TextUtils.isEmpty(mSearchQuery)) {
            String query = mSearchQuery.replaceAll("--", ", ");
            if (parentFragment.isVisible()) {
                if (mTotalProducts > 0)
                    getBaseActivity().setTitleAndSubTitle(query,
                            " (" + productsPage.getTotalProducts() + " " + getBaseActivity().getString(R.string.shoppingcart_items) + ")");
                else
                    getBaseActivity().setTitle(query);
            }
            if (mPageNumber == 1 && JumiaApplication.INSTANCE != null && JumiaApplication.INSTANCE.trackSearch) {
                params = new Bundle();
                params.putString(TrackerDelegator.SEARCH_CRITERIA_KEY, query);
                params.putLong(TrackerDelegator.SEARCH_RESULTS_KEY, totalProducts);
                params.putString(TrackerDelegator.SORT_KEY, mSort.name());
               
                if(JumiaApplication.INSTANCE.trackSearchCategory)
                    params.putString(AdjustTracker.CATEGORY, mTitle);
                
                if(!TextUtils.isEmpty(categoryId) && TextUtils.isEmpty(CatalogFragment.categoryId))
                    params.putString(AdjustTracker.CATEGORY_ID, categoryId);
                else params.putString(AdjustTracker.CATEGORY_ID, CatalogFragment.categoryId);
                
                JumiaApplication.INSTANCE.trackSearchCategory = false;
                JumiaApplication.INSTANCE.trackSearch = false;
                TrackerDelegator.trackSearch(params);
                TrackerDelegator.trackPage(TrackingPage.SEARCH_SCREEN, getLoadTime(), true);
                TrackerDelegator.trackViewCatalog(mTitle,"",mPageNumber);
            }

        } else if (mPageNumber == 1) {
            params = new Bundle();
            params.putString(TrackerDelegator.CATEGORY_KEY, mTitle);
            params.putInt(TrackerDelegator.PAGE_NUMBER_KEY, mPageNumber);
            params.putSerializable(TrackerDelegator.LOCATION_KEY, mTrackSortEvent);
            TrackerDelegator.trackCategoryView(params);
//            TrackerDelegator.trackViewCatalog(mTitle,"",mPageNumber);
            if (trackViewScreen && !isFromCategory) {
                isFromCategory = true;
                CatalogFragment.categoryId = categoryId;
                trackViewCatalog();
                TrackerDelegator.trackViewCatalog(mTitle,"",mPageNumber);
            }
        }

        try {
            ProductsListAdapter adapter = (ProductsListAdapter) this.gridView.getAdapter();
            if (null != adapter) {
                adapter.appendProducts(productsPage.getProducts());
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "NPE ON APPEND PRODUCTS: ");
            e.printStackTrace();
        }

        mPageNumber = numberProducts >= totalProducts ? NO_MORE_PAGES : mPageNumber + 1;
        TrackerDelegator.trackViewCatalog(mTitle,"",mPageNumber);
        showCatalogContent();
        if (mTotalProducts < ((mPageNumber - 1) * numItemsToLoad)) {
            mPageNumber = NO_MORE_PAGES;
        }

        mIsLoadingMore = false;
        if (numberProducts >= totalProducts) {
            mIsLoadingMore = true;
        }

        // Updated filter
        if (parentFragment.isVisible()) {
            parentFragment.onSuccesLoadingFilteredCatalog(productsPage.getFilters());
        }

        // if smoothScroll didn't finish the job properly, finish it here
        if (isScrolling) {
            isScrolling = false;
            // if still not in position 0, wait 2 DELAYS and force scroll to position 0 
            if (mFirstVisibleItem > 0) {
                gridView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gridView.setSelection(0);
                    }
                }, 2 * SCROLL_DELAY);
            }
        }

        RocketImageLoader.getInstance().startProcessingQueue();
    }
    /**
     * Show continue
     * @author sergiopereira
     */
    private void showContinueShopping() {
        Log.i(TAG, "ON SHOW RETRY LAYOUT");
        showFragmentEmpty(R.string.server_error, android.R.color.transparent, R.string.continue_shopping, this);
    }
    
    private void onErrorEvent(Bundle bundle) {
        Log.d(TAG, "ON ERROR EVENT");
    	
        // Validate fragment state
        if (isOnStoppingProcess) return;
    	
        RocketImageLoader.getInstance().startProcessingQueue();


        /**
         * Show the retry layout for ErrorCode.NO_NETWORK
         * TODO: Update this method to use the new layout for NO_NETWORK_CONNECTION
         * @author ricardo
         */
        final EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        if(eventType != null){
            ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
            
            Log.d(TAG, "ERROR CODE: " + errorCode.toString());
            
            if(errorCode == ErrorCode.NO_NETWORK){
                ((CatalogFragment) getParentFragment()).disableCatalogButtons();
                showFragmentRetry(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CatalogFragment) getParentFragment()).enableCatalogButtons();
                        getMoreProducts();
                    }
                }, R.string.no_connect_dialog_content);
                return;
            } else if (errorCode == ErrorCode.HTTP_STATUS) {
                showContinueShopping();
                return;
            } else if (getBaseActivity().handleErrorEvent(bundle)) {
                return;
            }
        }
        
        // Validate the request was performed by Filter
        boolean hasFilter = false;
        if (mFilters != null && parentFragment != null) {
            Log.d(TAG, "FILTERS: ON ERROR EVENT");
            // Sent to Parent that was received an error on load catalog
            parentFragment.onErrorLoadingFilteredCatalog();
            hasFilter = true;
        }

        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (errorCode != null && mPageNumber == 1) {
            FeaturedBox featuredBox = null;

            Object featureBoxObject = bundle.get(Constants.BUNDLE_RESPONSE_KEY);
            if (featureBoxObject != null && featureBoxObject instanceof FeaturedBox) {
                featuredBox = (FeaturedBox) featureBoxObject;
            }

            // Show respective error layout
            if (!TextUtils.isEmpty(mSearchQuery) && !hasFilter) {
                // For search query
                parentFragment.onErrorSearchResult(featuredBox);
            } else {
                // For category and filter
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSavedProductsSKU = null;
                        // Show layout for filters
                        if(mFilters != null) showFiltersNoResults();
                        // Show layout for retry
                        else showProductsNotfound();
                    }
                }, 200);
            }

        } else {
            Log.d(TAG, "onErrorEvent: loading more products failed");
            hideProductsLoading();
            if (mTotalProducts != -1 && mTotalProducts > ((mPageNumber - 1) * numItemsToLoad)) {
                Toast.makeText(getBaseActivity(), R.string.products_could_notloaded, Toast.LENGTH_SHORT).show();
                /*- TODO showProductsNotfound();*/
            }
        }
        mBeginRequestMillis = System.currentTimeMillis();
        mIsLoadingMore = false;
        mReceivedError = true;
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        // Case retry
        if (id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Case continue
        else if(id == R.id.fragment_root_empty_button) onClickContinueButton();
        // Case unknown
        else Log.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
    }
    
    /**
     * Process the click in retry button
     * @author sergiopereira
     */
    private void onClickRetryButton() {
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, getArguments(), FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Process the click in continue shopping
     * @author sergiopereira
     */
    private void onClickContinueButton() {
        getBaseActivity().onBackPressed();
    }
    
    /**
     * method used to set flag to after filter is applied to clean all products
     * @param toClear
     */
    public void setProductClear(boolean toClear){
        isProductClear = toClear;
    }
}
