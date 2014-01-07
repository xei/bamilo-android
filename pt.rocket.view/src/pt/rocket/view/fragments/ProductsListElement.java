///**
// * 
// */
//package pt.rocket.view.fragments;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Calendar;
//import java.util.EnumSet;
//
//import pt.rocket.constants.ConstantsIntentExtra;
//import pt.rocket.constants.ConstantsSharedPrefs;
//import pt.rocket.controllers.ProductsListAdapter;
//import pt.rocket.controllers.fragments.FragmentController;
//import pt.rocket.controllers.fragments.FragmentType;
//import pt.rocket.framework.event.EventManager;
//import pt.rocket.framework.event.EventType;
//import pt.rocket.framework.event.IMetaData;
//import pt.rocket.framework.event.ResponseEvent;
//import pt.rocket.framework.event.ResponseListener;
//import pt.rocket.framework.event.ResponseResultEvent;
//import pt.rocket.framework.event.events.GetProductsEvent;
//import pt.rocket.framework.objects.Product;
//import pt.rocket.framework.objects.ProductsPage;
//import pt.rocket.framework.rest.RestContract;
//import pt.rocket.framework.utils.AnalyticsGoogle;
//import pt.rocket.framework.utils.Direction;
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.framework.utils.ProductSort;
//import pt.rocket.utils.DialogList;
//import pt.rocket.utils.TrackerDelegator;
//import pt.rocket.view.BaseActivity;
//import pt.rocket.view.R;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.GridView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
//
//import de.akquinet.android.androlog.Log;
//
///**
// * @author sergiopereira
// * @modified manuelsilva
// */
//public class ProductsListElement implements 
//       OnScrollListener, ResponseListener {
//
//    private static final String TAG = LogTagHelper.create(ProductsListElement.class);
//
//    private View mainView;
//
//    boolean isFirstBootBrands = true;
//
//    // private View sortButton;
//    private final static String KEY_STATE_SORTER = "products_state_sorter";
//    private final static String KEY_STATE_VIEW = "products_state_view";
//
//    private String title;
//
//    //
//    private ProductsListAdapter productsAdapter;
//    //
//    private GridView productsListGridView;
//    private ListView productsListView;
//    private int currentOrientation = Configuration.ORIENTATION_PORTRAIT;
//    private View loadingLayout;
//
//    private int MAX_PAGE_ITEMS = 15;
//    private int NO_MORE_PAGES = -1;
//
//    private static boolean isLoadingMore = false;
//
//    private int pageNumber;
//    private ProductSort sort = ProductSort.NONE;
//    private Direction dir = Direction.ASCENDENT;
//
//    private Bundle savedState;
//
//    private String productsURL = null;
//    private String searchQuery = null;
//
//    private View notfound;
//    private View refreshAlertView;
//
//    private int mSortPosition = DialogList.NO_INITIAL_POSITION;
//
//    boolean flag = false;
//
//    private long mBeginRequestMillis;
//
//    private String navigationPath;
//
//    private int navigationSource;
//
//    private View productsContent;
//
//    private int totalProducts = -1;
//
//    public static final String INTENT_POSITION_EXTRA = "extra_position";
//
//    private Context mContext;
//    
//    private String md5Hash;
//    
//    private int pagePosition;
//    
//    /**
//     * Empty constructor
//     */
//    public ProductsListElement(Context ctx, int pos) {
//        this.mContext = ctx;
//        this.pagePosition = pos;
//        EventManager.getSingleton().addResponseListener(this, EnumSet.of(EventType.GET_PRODUCTS_EVENT));
//    }
//
//    public View createView(LayoutInflater inflater) {
//        Log.i(TAG, "ON CREATE VIEW");
//        mainView = inflater.inflate(R.layout.products, null, false);
//        return mainView;
//    }
//    
//    public View getElement(){
//        return mainView;
//    }
//
//    public void onStart() {
//
//        md5Hash = uniqueMD5(TAG, pagePosition);
//
//        Log.i(TAG, " MD5Hash -> " + md5Hash);
//
//        // Inflate Products Layout
//        setAppContentLayout();
//        productsURL = ProductsViewFragment.productsURL;
//        Log.d(TAG, "onCreate: productsURL = " + productsURL);
//        searchQuery = ProductsViewFragment.searchQuery;
//        
//        if (pagePosition == 0) {
//            showTips();
//        }
//        
//        setSort(pagePosition);
//
//        Log.d(TAG, "onCreate: searchQuery = " + searchQuery);
//        
//        navigationSource =  ProductsViewFragment.navigationSource;
//
//        navigationPath = ProductsViewFragment.navigationPath;
//
//        AnalyticsGoogle.get().trackSourceResWithPath(navigationSource, navigationPath);
//
//        productsAdapter = new ProductsListAdapter(mContext);
//        
//        if(((BaseActivity) mContext).isTabletInLandscape()){
//            productsListGridView.setAdapter(productsAdapter);
//
//        } else {
//            productsListView.setAdapter(productsAdapter);
//
//        }
//
//        if (null != savedState && savedState.containsKey(KEY_STATE_VIEW)) {
//            savedState.remove(KEY_STATE_VIEW);
//        }
//        
//        Log.i(TAG, "ON START");
//        ((BaseActivity) mContext).setTitle(ProductsViewFragment.title);
//
//        if (productsAdapter != null && productsAdapter.getCount() == 0) {
//            executeRequest();
//        }
//    }
//
//    /**
//     * Set the Products layout using inflate
//     */
//    private void setAppContentLayout() {
//
//        productsContent = mainView.findViewById(R.id.products_content);
//        if(((BaseActivity) mContext).isTabletInLandscape()){
//            productsListGridView = (GridView) mainView.findViewById(R.id.middle_productslist_list);
//            productsListGridView.setOnItemClickListener(new OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View view,
//                        int position, long id) {
//
//                    int activePosition = position;
//
//                    if (activePosition > -1) {
//                        // // Call Product Details
//
//                        Log.i("TAG", "DIR=======>" + dir + " sort =====> " + sort);
//
//                        Bundle bundle = new Bundle();
//                        bundle.putString(ConstantsIntentExtra.CONTENT_URL, ((Product) productsAdapter.getItem(activePosition)).getUrl());
//                        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
//                        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
//                        ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
//                    }
//
//                }
//            });
//            productsListGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true,
//                    true, this));
//        } else {
//            productsListView = (ListView) mainView.findViewById(R.id.middle_productslist_list);
//            productsListView.setOnItemClickListener(new OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View view,
//                        int position, long id) {
//
//                    int activePosition = position;
//
//                    if (activePosition > -1) {
//                        // // Call Product Details
//
//                        Log.i("TAG", "DIR=======>" + dir + " sort =====> " + sort);
//                        Bundle bundle = new Bundle();
//                        bundle.putString(ConstantsIntentExtra.CONTENT_URL, ((Product) productsAdapter.getItem(activePosition)).getUrl());
//                        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
//                        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
//                        ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
//                    }
//
//                }
//            });
//            productsListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true,
//                    true, this));
//        }
//        
//
//        notfound = mainView.findViewById(R.id.search_products_not_found);
//        refreshAlertView = mainView.findViewById(R.id.retry_alert_view_button);
//        loadingLayout = mainView.findViewById(R.id.loadmore);
//    }
//    
//    /**
//     * Show tips if is the first time the user uses the app.
//     */
//    private void showTips(){
//        final SharedPreferences sharedPrefs =
//                mContext.getSharedPreferences(
//                        ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//
//        if (sharedPrefs.getBoolean(ConstantsSharedPrefs.KEY_SHOW_PRODUCTS_TIPS, true)) {
//            RelativeLayout productsTip = (RelativeLayout) mainView.findViewById(R.id.products_tip);
//            productsTip.setVisibility(View.VISIBLE);
//            productsTip.setOnTouchListener(new OnTouchListener() {
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    v.setVisibility(View.GONE);
//
//                    SharedPreferences.Editor editor = sharedPrefs.edit();
//                    editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PRODUCTS_TIPS,
//                            false);
//                    editor.commit();
//                    return false;
//                }
//            });
//        }
//    }
//
//    private void executeRequest() {
//        pageNumber = 1;
//        productsAdapter.clearProducts();
//        showProductsContent();
//        getMoreProducts();
//    }
//
//    private void showProductsContent() {
//        Log.d(TAG, "showProductsContent");
////        if (pageNumber == 1) {
////            if(((BaseActivity) mContext).isTabletInLandscape()){
////                productsListGridView.post(new Runnable() {
////                    @Override
////                    public void run() {
////                        productsListGridView.setSelection(0);
////                    }
////                });    
////            } else {
////                productsListView.post(new Runnable() {
////                    @Override
////                    public void run() {
////                        productsListView.setSelection(0);
////                    }
////                });
////            }
////            
////
////        }
//        productsContent.setVisibility(View.VISIBLE);
//        notfound.setVisibility(View.GONE);
//        loadingLayout.setVisibility(View.GONE);
//        loadingLayout.refreshDrawableState();
//        if (mainView != null) {
//            mainView.findViewById(R.id.loading_view_pager).setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * gets the activity current orientation
//     * 
//     * @return The flag indicating what is the current orientation for the activity
//     */
//    public int getCurrentOrientation() {
//        return currentOrientation;
//    }
//
//    /**
//     * gets the next page of products from the API
//     */
//    private void getMoreProducts() {
//        Log.d(TAG, "GET MORE PRODUCTS");
//
//        if (pageNumber != NO_MORE_PAGES) {
//            // Test to see if we already have all the products available
//
//            if (pageNumber == 1) {
//                if (mainView != null) {
//                    hideProductsNotFound();
//                    mainView.findViewById(R.id.loading_view_pager).setVisibility(View.VISIBLE);
//                }
//
//                // ((BaseActivity) getActivity()).showLoading();
//            }
//
//            mBeginRequestMillis = System.currentTimeMillis();
//            EventManager.getSingleton().triggerRequestEvent(
//                    new GetProductsEvent(productsURL, searchQuery, pageNumber, MAX_PAGE_ITEMS,
//                            sort, dir, md5Hash));
//        } else {
//            hideProductsLoading();
//        }
//    }
//
//    private void setSort(int position) {
//        mSortPosition = position;
//        switch (position) {
//        case 0: // <item >Popularity</item>
//            sort = ProductSort.POPULARITY;
//            dir = Direction.DESCENDENT;
//            break;
//        case 1: // <item >Price up</item>
//            sort = ProductSort.PRICE;
//            dir = Direction.ASCENDENT;
//            break;
//        case 2: // <item >Price down</item>
//            sort = ProductSort.PRICE;
//            dir = Direction.DESCENDENT;
//            break;
//        case 3: // <item >Name</item>
//            // TODO when available change this to Top
//            // Offer option
//            sort = ProductSort.NAME;
//            dir = Direction.ASCENDENT;
//            break;
//        case 4: // <item >Brand</item>
//            // TODO when available change this to Sales
//            // option
//            sort = ProductSort.BRAND;
//            dir = Direction.ASCENDENT;
//            break;
//        }
//    }
//
////    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
////        ProductsPage productsPage = (ProductsPage) event.result;
////        
////        if (productsPage != null && productsPage.getTotalProducts() > 0) {
////            totalProducts = productsPage.getTotalProducts();
////        }
////        
////        String location = event.metaData.getString(IMetaData.LOCATION);
////        Log.d(TAG, "Location = " + location);
////        checkRedirectFromSearch(location);
////
////        AnalyticsGoogle.get().trackLoadTiming(R.string.gproductlist, mBeginRequestMillis);
////        
////        if (searchQuery != null && !TextUtils.isEmpty(searchQuery)) {
////            ((BaseActivity) mContext).setTitle(searchQuery + " (" + productsPage.getTotalProducts() + ")");
////            TrackerDelegator.trackSearchMade(mContext.getApplicationContext(), searchQuery,
////                    productsPage.getTotalProducts());
////        } else {
////            TrackerDelegator.trackCategoryView(mContext.getApplicationContext(), title,
////                    pageNumber);
////        }
////        
////        productsAdapter.appendProducts(productsPage.getProducts());
////    }
////    
//    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
//        Log.d(TAG, "ON SUCCESS EVENT "+pagePosition);
//        // sortButton.setOnClickListener(this);
//        // Get Products Event
//        ProductsPage productsPage = (ProductsPage) event.result;
//        Log.d(TAG, "onSuccessEvent: products on page = " + productsPage.getProducts().size() +
//                " total products = " + productsPage.getTotalProducts());
//        if (productsPage != null && productsPage.getTotalProducts() > 0) {
//            totalProducts = productsPage.getTotalProducts();
//        }
//
//        
//
//        String location = event.metaData.getString(IMetaData.LOCATION);
//        Log.d(TAG, "Location = " + location);
//        checkRedirectFromSearch(location);
//
//        AnalyticsGoogle.get().trackLoadTiming(R.string.gproductlist, mBeginRequestMillis);
//        
//        if (searchQuery != null && !TextUtils.isEmpty(searchQuery)) {
//            ((BaseActivity) mContext).setTitle(searchQuery + " (" + productsPage.getTotalProducts() + ")");
//            TrackerDelegator.trackSearchMade(mContext.getApplicationContext(), searchQuery,
//                    productsPage.getTotalProducts());
//        } else {
//            TrackerDelegator.trackCategoryView(mContext.getApplicationContext(), title,
//                    pageNumber);
//        }
//
//        productsAdapter.appendProducts(productsPage.getProducts());
//
////        showProductsContent();
//
//        Log.i(TAG, "code1 " + productsPage.getProducts().size() + " pageNumber is : "+pageNumber);
//        pageNumber = productsPage.getProducts().size() >= productsPage.getTotalProducts() ? NO_MORE_PAGES
//                : pageNumber + 1;
//
//        if (totalProducts < ((pageNumber - 1) * MAX_PAGE_ITEMS)) {
//            pageNumber = NO_MORE_PAGES;
//        }
//        isLoadingMore = false;
//        if (productsPage.getProducts().size() >= productsPage.getTotalProducts()) {
//            isLoadingMore = true;
//        }
//        
//        AnalyticsGoogle.get().trackSearch(searchQuery, productsPage.getTotalProducts());
//
//        // Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
//        // Debug.getMemoryInfo(memoryInfo);
//
//        return true;
//    }
//
//    private void checkRedirectFromSearch(String location) {
//        Log.d(TAG, "url = " + productsURL);
//        Log.d(TAG, "search = " + searchQuery);
//        Log.d(TAG, "location = " + location);
//
//        Uri uri = Uri.parse(location);
//        if (searchQuery == null) {
//            return;
//        } else if (uri.getPath().equals(RestContract.REST_BASE_PATH + "/search/")) {
//            return;
//        } else if (!TextUtils.isEmpty(productsURL)) {
//            return;
//        }
//
//        Log.d(TAG, "detected redirect - setting properties according");
//        searchQuery = null;
//        productsURL = location;
//
//        return;
//    }
//
//    protected boolean onErrorEvent(ResponseEvent event) {
//        if (event.errorCode != null && pageNumber == 1) {
//            showProductsNotfound();
//            ((BaseActivity) mContext).showContentContainer();
//        } else {
//            Log.d(TAG, "onErrorEvent: loading more products failed");
//            hideProductsLoading();
//
//            if (totalProducts != -1 && totalProducts > ((pageNumber - 1) * MAX_PAGE_ITEMS)) {
//                Toast.makeText(mContext, R.string.products_could_notloaded, Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//        mBeginRequestMillis = System.currentTimeMillis();
//        isLoadingMore = false;
//        return true;
//    }
//
//    private void showProductsNotfound() {
//        Log.d(TAG, "showProductsNotfound");
//        productsContent.setVisibility(View.GONE);
//        notfound.setVisibility(View.VISIBLE);
//        loadingLayout.setVisibility(View.GONE);
//        loadingLayout.refreshDrawableState();
//        refreshAlertView.setVisibility(View.VISIBLE);
//        refreshAlertView.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                hideProductsNotFound();
//                getMoreProducts();
//                
//            }
//        });
//        if (mainView != null) {
//            mainView.findViewById(R.id.loading_view_pager).setVisibility(View.GONE);
//        }
//    }
//    
//    private void hideProductsNotFound(){
//        notfound.setVisibility(View.GONE);
//        refreshAlertView.setVisibility(View.GONE);
//    }
//
//    /**
//     * Set all Products using Products Adapter
//     */
//    private void hideProductsLoading() {
//        Log.d(TAG, "hideProductsLoading");
//        loadingLayout.setVisibility(View.GONE);
//        loadingLayout.refreshDrawableState();
//        notfound.setVisibility(View.GONE);
//    }
////
////    @Override
////    public void onMovedToScrapHeap(View view) {
////        if (listItemRecycleCount > MAX_RECYCLED_PROCESSED_COUNT) {
////            listItemRecycleCount = 0;
////            Log.d(TAG, "onMovedToScrapHead: gc requested");
////            System.gc();
////        } else {
////            listItemRecycleCount++;
////        }
////    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//            int totalItemCount) {
//        // Make your calculation stuff here. You have all your
//        // needed info from the parameters of this function.
//
//        // Sample calculation to determine if the last
//        // item is fully visible.
//        final int lastItem = firstVisibleItem + visibleItemCount;
//        if (totalItemCount != 0 && lastItem == totalItemCount) {
//            Log.i( TAG, "onScroll: last item visible ");
//            if (!isLoadingMore) {
//                Log.i( TAG, "onScroll: last item visible and start loading"+pageNumber);
//                isLoadingMore = true;
//                showProductsLoading();
//                getMoreProducts();
//            }
//        }
//    }
//
//    private void showProductsLoading() {
//        Log.d(TAG, "showProductsLoading");
//        loadingLayout = mainView.findViewById(R.id.loadmore);
//        loadingLayout.setVisibility(View.VISIBLE);
//        loadingLayout.refreshDrawableState();
//    }
//
//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        // noop
//    }
//
//    @Override
//    public void handleEvent(ResponseEvent event) {
//        if(event.getSuccess()){
//            onSuccessEvent((ResponseResultEvent<?>) event);
//        } else {
//            onErrorEvent(event);
//        }
//        
//    }
//
//    @Override
//    public boolean removeAfterHandlingEvent() {
//        return true;
//    }
//
//    @Override
//    public String getMD5Hash() {
//        return md5Hash;
//    }
//    
//    /**
//     * This method generates a unique and always diferent MD5 hash based on a given key 
//     * @param key 
//     * @return the unique MD5 
//     */
//    protected static String uniqueMD5(String key, int pos) { 
//        String md5String = "";
//        try {
//            Calendar calendar = Calendar.getInstance();
//            key = key + pos + calendar.getTimeInMillis() ;
//        
//            // Create MD5 Hash
//            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
//            digest.update(key.getBytes());
//            byte messageDigest[] = digest.digest();
//     
//            // Create Hex String
//            StringBuffer hexString = new StringBuffer();
//            for (int i=0; i<messageDigest.length; i++) {
//                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
//            }
//            md5String = hexString.toString();
//     
//         } catch (NoSuchAlgorithmException e) {
//             e.printStackTrace();
//         }
//        
//        return md5String;
// 
//    }         
//}
