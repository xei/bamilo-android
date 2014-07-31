/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

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
import pt.rocket.framework.objects.CatalogFilter;
import pt.rocket.framework.objects.FeaturedBox;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.tracking.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Direction;
import pt.rocket.framework.utils.ProductSort;
import pt.rocket.helpers.GetProductsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.TrackerDelegator;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author nunocastro
 * 
 */
public class CatalogPageFragment extends BaseFragment {

    public static final String TAG = CatalogPageFragment.class.getSimpleName();

    public static final String PARAM_PAGE_INDEX = "param_page_index";
    public static final String PARAM_FILTERS = "param_filters";
    public static final String PARAM_TITLE = "param_title";
    public static final String PARAM_IS_LANDSCAPE = "param_is_landscape";
    
    private final String PRODUCT_LIST = "product_list";
    private final String PRODUCT_LIST_POSITION = "product_list_position";
    private final String LIST_TITLE = "list_title";

    private final int MAX_PAGE_ITEMS = 18;
    private final int NO_MORE_PAGES = -1;
    
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

    // Spnf - search_products_not_found
    private TextView textViewSpnf;
    // Ravb - retry_alert_view_button
    private Button buttonRavb;
    // pc products_content
    private RelativeLayout relativeLayoutPc;
    // Lm loading_more
    private LinearLayout linearLayoutLm;
    // Products grid view
    private GridView gridView;
    // Lb - loading_bar
    private LinearLayout linearLayoutLb;
//    private LoadingBarView loadingBarView;


    public static CatalogPageFragment newInstance(Bundle bundle) {
        CatalogPageFragment sCatalogPageFragment = new CatalogPageFragment();
        sCatalogPageFragment.setArguments(bundle);
        return sCatalogPageFragment;
    }

    /**
     * Empty constructor
     */
    public CatalogPageFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.products);
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

        parentFragment = (CatalogFragment)getBaseActivity().getSupportFragmentManager().findFragmentByTag(FragmentType.PRODUCT_LIST.toString());

        if (null != savedInstanceState) {
            mSavedProductsSKU = savedInstanceState.getStringArrayList(PRODUCT_LIST);
            mCurrentListPosition = savedInstanceState.getInt(PRODUCT_LIST_POSITION);
            mTitle = savedInstanceState.getString(LIST_TITLE);            
        }
        
        Bundle args = getArguments();
        if (null != args) {
            mPageIndex = args.getInt(PARAM_PAGE_INDEX, 0);
            
            switch (mPageIndex) {
            case 0: // <item > Copy of Best Rating for infinite scroll</item>
                // option
                mSort = ProductSort.BESTRATING;
                mDirection = Direction.DESCENDENT;
                break;
                
            case 1: // <item >Popularity</item>
                mSort = ProductSort.POPULARITY;
                mDirection = Direction.DESCENDENT;
                break;
                
            case 2: // <item >Price up</item>
                mSort = ProductSort.NEWIN;
                mDirection = Direction.DESCENDENT;
                break;
                
            case 3: // <item >Price up</item>
                mSort = ProductSort.PRICE;
                mDirection = Direction.ASCENDENT;
                break;
                
            case 4: // <item >Price down</item>
                mSort = ProductSort.PRICE;
                mDirection = Direction.DESCENDENT;
                break;
                
            case 5: // <item >Name</item>
                // Offer option
                mSort = ProductSort.NAME;
                mDirection = Direction.ASCENDENT;
                break;
                
            case 6: // <item >Brand</item>
                // option
                mSort = ProductSort.BRAND;
                mDirection = Direction.ASCENDENT;
                break;
                
            case 7: // <item >Best Rating</item>
                // option
                mSort = ProductSort.BESTRATING;
                mDirection = Direction.DESCENDENT;
                break;

            case 8: // <item >Copy of Popularity for infinite scroll</item>
                mSort = ProductSort.POPULARITY;
                mDirection = Direction.DESCENDENT;
                break;

            }
            
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED #" + mPageIndex);

        this.textViewSpnf = ((org.holoeverywhere.widget.TextView) view.findViewById(R.id.search_products_not_found));
        this.buttonRavb = ((Button) view.findViewById(R.id.retry_alert_view_button));
        this.relativeLayoutPc = ((RelativeLayout) view.findViewById(R.id.products_content));
        this.linearLayoutLm = ((LinearLayout) view.findViewById(R.id.loadmore));
        this.gridView = (GridView) view.findViewById(R.id.middle_productslist_list);
        this.gridView.setOnItemClickListener(onItemClickListener);

        this.linearLayoutLb = (LinearLayout) view.findViewById(R.id.loading_view_pager);
//        this.loadingBarView = ((LoadingBarView) this.linearLayoutLb.findViewById(R.id.loading_bar_view));
        
    }

    @Override
    public void onResume() {     
        super.onResume();        
        Log.i(TAG, "ON RESUME #" + mPageIndex );
        
        Bundle args = getArguments();        
        invalidateData(args, false);
    }
    
    @Override
    public void onPause() {     
        super.onPause();
        ProductsListAdapter adapter = (ProductsListAdapter)this.gridView.getAdapter();
        mSavedProductsSKU = adapter.getProductsList();
        mCurrentListPosition = this.gridView.getFirstVisiblePosition();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "ON SAVE INSTANCE STATE #" + mPageIndex);
        if (null != this.gridView) {
            ProductsListAdapter adapter = (ProductsListAdapter)this.gridView.getAdapter();
            if (null != adapter) {
                outState.putStringArrayList(PRODUCT_LIST, adapter.getProductsList());
                outState.putInt(PRODUCT_LIST_POSITION, this.gridView.getFirstVisiblePosition());                
            }
            outState.putString(LIST_TITLE, mTitle);
        }
        super.onSaveInstanceState(outState);
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
    public void invalidateData(final Bundle arguments, final boolean forceRefresh ) {

        if (null != getView() && null == gridView) {
            this.gridView = (GridView) getView().findViewById(R.id.middle_productslist_list); 
        }
        
        ContentValues newFilters = null;
        if (null != arguments) {
            updateLocalArguments(arguments);
            newFilters = arguments.getParcelable(PARAM_FILTERS);
        }
        ProductsListAdapter adapter = (ProductsListAdapter)gridView.getAdapter();
        boolean showList = parentFragment.getShowList();
        int switchMD5 = parentFragment.getSwitchMD5();        
        
        // Case no content
        if (null == adapter) {  
            Log.i(TAG, "ON RESUME -> Null Adapter");
            mFilters = newFilters;
            initializeCatalogPage(showList);
            
        } else if (newFilters != null && newFilters.getAsInteger("md5") != mFilterMD5) {  // Case new filter
            Log.i(TAG, "ON RESUME -> FILTER IS DIFF: " + newFilters.getAsInteger("md5") + " " + mFilterMD5);
            mFilterMD5 = newFilters.getAsInteger("md5");
            mFilters = newFilters;            
            mSavedProductsSKU = null;
            initializeCatalogPage(showList);
            
        } else if (mSwitchMD5 != switchMD5) {
            Log.i(TAG, "ON RESUME -> SWITCH LAYOUT");

            mCurrentListPosition = this.gridView.getFirstVisiblePosition();
            
            List<String> products = adapter.getProductsList();
            adapter = new ProductsListAdapter(getBaseActivity(), parentFragment, showList, updateGridColumns(showList), isFrench);
            adapter.appendProducts(products);
            gridView.setAdapter(adapter);
            gridView.setSelection(mCurrentListPosition);
            gridView.setOnScrollListener(onScrollListener);

            showCatalogContent();
            mIsLoadingMore = false;
            
            mSwitchMD5 = switchMD5;
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
 
    private void initializeCatalogPage(boolean showList) {
        Log.d(TAG, "ON RESUME - REQ -> Landscape ? " + mIsLandScape + "; Columns #" + updateGridColumns(showList));
        
        ProductsListAdapter adapter = (ProductsListAdapter)this.gridView.getAdapter();
        final boolean hasProducts = (null != mSavedProductsSKU);        
        
        // initialize new adapter depending on view choosen
        adapter = new ProductsListAdapter(getBaseActivity(), parentFragment, showList, updateGridColumns(showList), isFrench);         
        if (!hasProducts) {
            mPageNumber = 1;
        } else {
            Log.d(TAG, "ON RESUME - HAS PRODUCTS" );
            adapter.updateProducts(mSavedProductsSKU);
        }

        Log.d(TAG, "showProductsContent");
        if (mPageNumber == 1) {
            Log.i(TAG, "scrolling to position 0");
            this.gridView.setSelection(0);
        }
        this.gridView.setOnScrollListener(onScrollListener);

        relativeLayoutPc.setVisibility(View.VISIBLE);
        
        hideProductsLoading();

        gridView.setAdapter(adapter);
        
        if (!hasProducts) {
            getMoreProducts();
        } else {
            gridView.setSelection(mCurrentListPosition);    
            adapter.notifyDataSetChanged();
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
        //mIsLandScape = args.getBoolean(PARAM_IS_LANDSCAPE);
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
            // Test to see if we already have all the products available

            if (mPageNumber == 1 && null != getView()) {
                hideProductsNotFound();
                linearLayoutLb.setVisibility(View.VISIBLE);
//                showLoadingInfo();
            }

            mBeginRequestMillis = System.currentTimeMillis();

            Log.d(TAG, "FILTER CREATE BUNDLE");
            
            RocketImageLoader.getInstance().stopProcessingQueue();

            Bundle bundle = new Bundle();
            bundle.putString(GetProductsHelper.PRODUCT_URL, mProductsURL);
            bundle.putString(GetProductsHelper.SEARCH_QUERY, mSearchQuery);
            bundle.putInt(GetProductsHelper.PAGE_NUMBER, mPageNumber);
            bundle.putInt(GetProductsHelper.TOTAL_COUNT, MAX_PAGE_ITEMS);
            bundle.putInt(GetProductsHelper.SORT, mSort.id);
            bundle.putInt(GetProductsHelper.DIRECTION, mDirection.id);
            bundle.putParcelable(GetProductsHelper.FILTERS, mFilters);

            JumiaApplication.INSTANCE.sendRequest(new GetProductsHelper(), bundle, responseCallback);

        } else {
            hideProductsLoading();
        }
    }
    
    private void showCatalogContent() {
        relativeLayoutPc.setVisibility(View.VISIBLE);
        hideProductsLoading();        
        if (getView() != null) {
            linearLayoutLb.setVisibility(View.GONE);
//            hideLoadingInfo();
        }
    }    

    private int updateGridColumns(boolean showList) {
        // Tablet uses 3 columns for both Grid and List
        // Phone uses  2 columns for Grid
        // Phone uses  1 column  for List
        int numColumns = 1 + (mIsLandScape ? 2 : showList ? 0 : 1);        
        this.gridView.setNumColumns(numColumns);
        
        return numColumns;
    }
    
    private void showProductsNotfound() {
        Log.d(TAG, "showProductsNotfound");
        hideProductsLoading(false);        
        relativeLayoutPc.setVisibility(View.GONE);
        textViewSpnf.setVisibility(View.VISIBLE);
        buttonRavb.setVisibility(View.VISIBLE);
        buttonRavb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideProductsNotFound();
                getMoreProducts();
            }
        });
        if (getView() != null) {
            linearLayoutLb.setVisibility(View.GONE);
//            hideLoadingInfo();
        }
    }
    
    private void hideProductsNotFound() {
        textViewSpnf.setVisibility(View.GONE);
        buttonRavb.setVisibility(View.GONE);
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
                if (hideViewSpnf)
                    textViewSpnf.setVisibility(View.GONE);
            }
        }, 200);
    }
    
    
    // ---------------------------------------------------------------
    // ----- Listeners
    // ---------------------------------------------------------------

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int activePosition = position; // - productsAdapter.getJumpConstant();
            ProductsListAdapter adapter = (ProductsListAdapter) parent.getAdapter();

            if (-1 < activePosition && null != adapter) {
                // Call Product Details                
                Product product = parentFragment.getProduct((String) adapter.getItem(activePosition));

                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_URL, product.getUrl());
                bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, mNavigationSource);
                bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, mNavigationPath);
                bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrand() + " " + product.getName());
                // inform PDV that Related Items should be shown
                bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
                if (mTitle != null)
                    bundle.putString(ProductDetailsFragment.PRODUCT_CATEGORY, mTitle);
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,FragmentController.ADD_TO_BACK_STACK);
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
                RocketImageLoader.getInstance().stopProcessingQueue(); //CatalogFragment.requestTag
            } else {
                ProductsListAdapter adapter = (ProductsListAdapter)gridView.getAdapter();
                adapter.notifyDataSetChanged();
                RocketImageLoader.getInstance().startProcessingQueue();
                
            }
        }
        
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            
            // Sample calculation to determine if the last item is fully visible.             
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

            // Calculate the speed of the list is scrolling to, so that when the list slows down, re-enable the image loader processing queue
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
            
        }
    };
    
    
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

        // Get Products Event
        final ProductsPage productsPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);

        // Get Location
        final String location = bundle.getString(IMetaData.LOCATION);
        Log.d(TAG, "Location = " + location);

        // Validate title
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = productsPage.getName();
        }

        int totalProducts = productsPage == null ? 0 : productsPage.getTotalProducts();
        int numberProducts = (productsPage == null || productsPage.getProducts() == null) ? 0 : productsPage.getProducts().size();

        // Validate products
        if (productsPage != null && totalProducts > 0) {
            Log.d(TAG, "onSuccessEvent: products on page = " + numberProducts + " total products = " + totalProducts);
            
//            new Thread(new Runnable() {
//                
//                @Override
//                public void run() {
//                    try {
//                        // TODO: Improve this behavior
//                        if (mPageIndex == 1 && mPageNumber == 1) {
//                            RelatedItemsTableHelper.insertRelatedItemsAndClear(getBaseActivity(), productsPage.getProductsList());
//                        } else if (mPageIndex == 1 && mPageNumber == 2) {
//                            RelatedItemsTableHelper.insertRelatedItems(getBaseActivity(), productsPage.getProductsList());
//                        }
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }).start();

            try {
                if (mPageIndex == 1 && mPageNumber == 1) {
                    RelatedItemsTableHelper.insertRelatedItemsAndClear(getBaseActivity(), productsPage.getProductsList());
                } else if (mPageIndex == 1 && mPageNumber == 2) {
                    RelatedItemsTableHelper.insertRelatedItems(getBaseActivity(), productsPage.getProductsList());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
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

        AnalyticsGoogle.get().trackLoadTiming(R.string.gproductlist, mBeginRequestMillis);

        if (!TextUtils.isEmpty(mSearchQuery)) {
            String query = mSearchQuery.replaceAll("--", ", ");
            if (parentFragment.isVisible()) {
                if (mTotalProducts > 0)
                    getBaseActivity().setTitleAndSubTitle(query, " (" + productsPage.getTotalProducts() + " " + getBaseActivity().getString(R.string.shoppingcart_items) + ")");
                else
                    getBaseActivity().setTitle(query);
            }
            if (mPageNumber == 1) {
                TrackerDelegator.trackSearchViewSortMade(getBaseActivity().getApplicationContext(), query, totalProducts, mSort.name());

                TrackerDelegator.trackSearchMade(getBaseActivity().getApplicationContext(), query, totalProducts);
                AnalyticsGoogle.get().trackSearch(query, totalProducts);
            }

        } else {
            if (mPageNumber == 1) {
                TrackerDelegator.trackCategoryView(getBaseActivity().getApplicationContext(), mTitle, mPageNumber);
            }
        }

        try {
            ProductsListAdapter adapter = (ProductsListAdapter)this.gridView.getAdapter();
            if (null != adapter) {
                adapter.appendProducts(productsPage.getProducts());
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "NPE ON APPEND PRODUCTS: ");
            e.printStackTrace();
        }

        mPageNumber = numberProducts >= totalProducts ? NO_MORE_PAGES : mPageNumber + 1;
        showCatalogContent();
        if (mTotalProducts < ((mPageNumber - 1) * MAX_PAGE_ITEMS)) {
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
        
        RocketImageLoader.getInstance().startProcessingQueue();
    }
    
    private void onErrorEvent(Bundle bundle) {
        RocketImageLoader.getInstance().startProcessingQueue();
        
        if (getBaseActivity().handleErrorEvent(bundle)) {
            return;
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
                showProductsNotfound();
            }

        } else {
            Log.d(TAG, "onErrorEvent: loading more products failed");
            hideProductsLoading();
            if (mTotalProducts != -1 && mTotalProducts > ((mPageNumber - 1) * MAX_PAGE_ITEMS)) {
                Toast.makeText(getBaseActivity(), R.string.products_could_notloaded, Toast.LENGTH_SHORT).show();
            }
        }
        mBeginRequestMillis = System.currentTimeMillis();
        mIsLoadingMore = false;
        mReceivedError = true;
    }
    
}
