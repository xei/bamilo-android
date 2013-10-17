/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.ProductsListAdapter;
import pt.rocket.framework.components.ScrollViewEx.OnScrollBottomReachedListener;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.IMetaData;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetProductsEvent;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Direction;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ProductSort;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.DialogList;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ProductsFragment extends BaseFragment implements OnClickListener,
        OnDialogListListener, OnScrollListener, RecyclerListener {

    private static final String TAG = LogTagHelper.create(ProductsFragment.class);

    private final static int MAX_RECYCLED_PROCESSED_COUNT = 200;

    private View mainView;

    boolean isFirstBootBrands = true;
    boolean isFirstBootProducts = true;

    // private View sortButton;
    private final static String ID_SORT_DIALOG = "sort";
    private final static String KEY_STATE_SORTER = "products_state_sorter";
    private final static String KEY_STATE_VIEW = "products_state_view";

    private DialogListFragment mSortDialog;
    private String title;

    //
    private ProductsListAdapter productsAdapter;
    //
    private GridView productsList;
    private int currentOrientation = Configuration.ORIENTATION_PORTRAIT;
    private View loadingLayout;

    private int MAX_PAGE_ITEMS = 15;
    private int NO_MORE_PAGES = -1;

    private static boolean isLoadingMore = false;

    private int pageNumber;
    private ProductSort sort = ProductSort.NONE;
    private Direction dir = Direction.ASCENDENT;

    private Bundle savedState;

    private String productsURL = null;
    private String searchQuery = null;

    View notfound;

    private ArrayList<String> mSortOptions;
    private int mSortPosition = DialogList.NO_INITIAL_POSITION;

    boolean flag = false;

    private long beginRequestMillis;

    private String navigationPath;

    private int navigationSource;

    private View productsContent;

    private int listItemRecycleCount;

    private static ProductsFragment productsFragment;

    private int totalProducts = -1;

    public static final String INTENT_POSITION_EXTRA = "extra_position";

    /**
     * Get instance
     * 
     * @return
     */
    public static ProductsFragment getInstance() {
        if (productsFragment == null)
            productsFragment = new ProductsFragment();
        return productsFragment;
    }

    /**
     * Empty constructor
     */
    public ProductsFragment() {
        super(EnumSet.of(EventType.GET_PRODUCTS_EVENT), EnumSet.noneOf(EventType.class));
        this.setRetainInstance(true);
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {

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

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        mainView = inflater.inflate(R.layout.products, container, false);

        savedState = savedInstanceState;

        md5Hash = uniqueMD5(TAG);

        Log.i(TAG, " MD5Hash -> " + md5Hash);

        // Inflate Products Layout
        setAppContentLayout();

        title = getActivity().getIntent().getStringExtra(ConstantsIntentExtra.CONTENT_TITLE);
        getActivity().setTitle(title);
        productsURL = getActivity().getIntent().getExtras()
                .getString(ConstantsIntentExtra.CONTENT_URL);
        Log.d(TAG, "onCreate: productsURL = " + productsURL);
        searchQuery = getActivity().getIntent().getExtras()
                .getString(ConstantsIntentExtra.SEARCH_QUERY);
        Bundle args = getArguments();
        int pos = args.getInt(INTENT_POSITION_EXTRA);
        setSort(pos);
        Log.i(TAG, "code1 creating ProductsFragment " + pos);

        Log.d(TAG, "onCreate: searchQuery = " + searchQuery);
        navigationSource = getActivity().getIntent().getIntExtra(
                ConstantsIntentExtra.NAVIGATION_SOURCE, -1);
        navigationPath = getActivity().getIntent().getStringExtra(
                ConstantsIntentExtra.NAVIGATION_PATH);
        AnalyticsGoogle.get().trackSourceResWithPath(navigationSource, navigationPath);

        productsAdapter = new ProductsListAdapter(getActivity());
        productsList.setAdapter(productsAdapter);
        productsList.setRecyclerListener(this);
        listItemRecycleCount = 0;

        if (isFirstBootProducts) {
            isFirstBootProducts = false;
        }

        if (null != savedState && savedState.containsKey(KEY_STATE_VIEW)) {
            savedState.remove(KEY_STATE_VIEW);
        }

        initSorter();
//        executeRequest();

        return mainView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveActivityState(outState);
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {

        productsContent = mainView.findViewById(R.id.products_content);
        // sortButton = mainView.findViewById(R.id.sorter_button);

        productsList = (GridView) mainView.findViewById(R.id.middle_productslist_list);
        productsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                int activePosition = position; // -
                                               // productsAdapter.getJumpConstant();

                if (activePosition > -1) {
                    // // Call Product Details

                    Log.i("TAG", "DIR=======>" + dir + " sort =====> " + sort);

                    saveActivityState();
                    ActivitiesWorkFlow.productsDetailsActivity(getActivity(),
                            ((Product) productsAdapter.getItem(activePosition)).getUrl(),
                            navigationSource, navigationPath);
                }

            }
        });
        productsList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true,
                true, this));

        notfound = mainView.findViewById(R.id.search_products_not_found);
        loadingLayout = mainView.findViewById(R.id.loadmore);
    }

    private void executeRequest() {
        pageNumber = 1;
        productsAdapter.clearProducts();
        showProductsContent();
        getMoreProducts();
    }

    private void showProductsContent() {
        Log.d(TAG, "showProductsContent");
        if (pageNumber == 1) {
            productsList.post(new Runnable() {
                @Override
                public void run() {
                    productsList.setSelection(0);
                }
            });

        }
        productsContent.setVisibility(View.VISIBLE);
        notfound.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        loadingLayout.refreshDrawableState();
        if (mainView != null) {
            mainView.findViewById(R.id.loading_view_pager).setVisibility(View.GONE);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        restoreActivityState(savedState);
        if (productsAdapter != null && productsAdapter.getCount() == 0) {
            executeRequest();
        }
        // Uncomment if u need to allow sort button again.
        // if(mSortDialog!=null && mSortDialog.isVisible()){
        // mSortDialog.dismiss();
        // }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "ON PAUSE");

        if (sort.equals(ProductSort.POPULARITY)) {
            mSortPosition = 0;
            saveActivityState();
        } else if (sort.equals(ProductSort.PRICE) && dir.equals(Direction.ASCENDENT)) {
            mSortPosition = 1;
            saveActivityState();
        } else if (sort.equals(ProductSort.PRICE) && dir.equals(Direction.DESCENDENT)) {
            mSortPosition = 2;
            saveActivityState();
        } else if (sort.equals(ProductSort.NAME)) {
            mSortPosition = 3;
            saveActivityState();
        } else if (sort.equals(ProductSort.BRAND)) {
            mSortPosition = 4;
            saveActivityState();
        }

        if (mSortDialog != null && mSortDialog.isVisible()) {
            mSortDialog.dismiss();
            Log.i(TAG, "code1 onpause");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
        if (mSortDialog != null && mSortDialog.isVisible()) {
            mSortDialog.dismiss();
            Log.i(TAG, "code1 onstop");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
        productsAdapter = null;
        if (mSortDialog != null && mSortDialog.isVisible()) {
            mSortDialog.dismiss();
            Log.i(TAG, "code1 destroy view");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSortDialog != null && mSortDialog.isVisible()) {
            Log.i(TAG, "code1 destroy");
            mSortDialog.dismiss();
        }

    }

    /**
     * gets the activity current orientation
     * 
     * @return The flag indicating what is the current orientation for the activity
     */
    public int getCurrentOrientation() {
        return currentOrientation;
    }

    /**
     * gets the next page of products from the API
     */
    private void getMoreProducts() {
        Log.d(TAG, "GET MORE PRODUCTS");

        if (pageNumber != NO_MORE_PAGES) {
            // Test to see if we already have all the products available

            if (pageNumber == 1) {
                if (mainView != null) {
                    mainView.findViewById(R.id.loading_view_pager).setVisibility(View.VISIBLE);
                }

                // ((BaseActivity) getActivity()).showLoading();
            }

            beginRequestMillis = System.currentTimeMillis();
            EventManager.getSingleton().triggerRequestEvent(
                    new GetProductsEvent(productsURL, searchQuery, pageNumber, MAX_PAGE_ITEMS,
                            sort, dir, md5Hash));
            TrackerDelegator.trackCategoryView(getActivity().getApplicationContext(), title,
                    pageNumber);
        } else {
            hideProductsLoading();
        }
    }

    private void initSorter() {
        mSortOptions = new ArrayList<String>(Arrays.asList(getResources().getStringArray(
                R.array.products_picker)));
        mSortPosition = 0;
        if (null != savedState && savedState.containsKey(KEY_STATE_SORTER)) {
            Log.d(TAG, " SORTER SAVED STATE KEY=SORTER : " + savedState.getInt(KEY_STATE_SORTER));
            mSortPosition = savedState.getInt(KEY_STATE_SORTER);
            savedState.remove(KEY_STATE_SORTER);
        }
    }

    private void setSort(int position) {
        mSortPosition = position;
        switch (position) {
        case 0: // <item >Popularity</item>
            sort = ProductSort.POPULARITY;
            dir = Direction.DESCENDENT;
            break;
        case 1: // <item >Price up</item>
            sort = ProductSort.PRICE;
            dir = Direction.ASCENDENT;
            break;
        case 2: // <item >Price down</item>
            sort = ProductSort.PRICE;
            dir = Direction.DESCENDENT;
            break;
        case 3: // <item >Name</item>
            // TODO when available change this to Top
            // Offer option
            sort = ProductSort.NAME;
            dir = Direction.ASCENDENT;
            break;
        case 4: // <item >Brand</item>
            // TODO when available change this to Sales
            // option
            sort = ProductSort.BRAND;
            dir = Direction.ASCENDENT;
            break;
        }
    }

    private void saveActivityState(Bundle currentState) {
        currentState.putInt(KEY_STATE_SORTER, mSortPosition);
    }

    private void saveActivityState() {

        Log.d(TAG, " --- SESSION STATE  ----  =======>>>>> saveActivityState");
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Log.i(TAG, "SORTER =====> " + mSortPosition);
        editor.putInt(KEY_STATE_SORTER, mSortPosition);
        editor.commit();
    }

    private void restoreActivityState(Bundle currentState) {
        if (currentState == null)
            return;

        Log.w(" --- SESSION STATE  ----", "  =======>>>>> restoreActivityState");
        // currentState = new Bundle();

        SharedPreferences prefs = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        ;

        currentState.putInt(KEY_STATE_SORTER, prefs.getInt(KEY_STATE_SORTER, -1));
        currentState.putInt(KEY_STATE_VIEW, prefs.getInt(KEY_STATE_VIEW, -1));
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "sorter position = " + mSortPosition);

        int id = v.getId();
        if (id == R.id.sorter_button) {
            // FragmentTransaction ft =
            // getActivity().getSupportFragmentManager().beginTransaction();
            //
            if (mSortDialog != null) {
                mSortDialog.dismiss();
            }

            mSortDialog = DialogListFragment.newInstance(getActivity(),
                    (OnDialogListListener) this, ID_SORT_DIALOG, getString(R.string.product_sort),
                    mSortOptions, mSortPosition);

            mSortDialog.show(getActivity().getSupportFragmentManager(), null);

        }

    }

    @Override
    public void onDialogListItemSelect(String id, int position, String value) {
        Log.i(TAG, "onDialogListItemSelect ");
        if (id.equals(ID_SORT_DIALOG)) {
            setSort(position);
            executeRequest();
        }
    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        Log.d(TAG, "ON SUCCESS EVENT");
        // sortButton.setOnClickListener(this);
        // Get Products Event
        ProductsPage productsPage = (ProductsPage) event.result;
        Log.d(TAG, "onSuccessEvent: products on page = " + productsPage.getProducts().size() +
                " total products = " + productsPage.getTotalProducts());
        if (productsPage != null && productsPage.getTotalProducts() > 0) {
            totalProducts = productsPage.getTotalProducts();
        }

        TrackerDelegator.trackSearchMade(getActivity().getApplicationContext(), searchQuery,
                productsPage.getTotalProducts());

        String location = event.metaData.getString(IMetaData.LOCATION);
        Log.d(TAG, "Location = " + location);
        checkRedirectFromSearch(location);

        AnalyticsGoogle.get().trackLoadTiming(R.string.gproductlist, beginRequestMillis);
        System.gc();
        if (!TextUtils.isEmpty(searchQuery)) {
            getActivity().setTitle(title + " (" + productsPage.getTotalProducts() + ")");
        }

        productsAdapter.appendProducts(productsPage.getProducts());

        showProductsContent();

        Log.i(TAG, "code1 " + productsPage.getProducts().size() + " pageNumber is : "+pageNumber);
        pageNumber = productsPage.getProducts().size() >= productsPage.getTotalProducts() ? NO_MORE_PAGES
                : pageNumber + 1;

        if (totalProducts < ((pageNumber - 1) * MAX_PAGE_ITEMS)) {
            pageNumber = NO_MORE_PAGES;
        }
        isLoadingMore = false;
        if (productsPage.getProducts().size() >= productsPage.getTotalProducts()) {
            isLoadingMore = true;
        }
        
        AnalyticsGoogle.get().trackSearch(searchQuery, productsPage.getTotalProducts());

        // Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        // Debug.getMemoryInfo(memoryInfo);

        return true;
    }

    private void checkRedirectFromSearch(String location) {
        Log.d(TAG, "url = " + productsURL);
        Log.d(TAG, "search = " + searchQuery);
        Log.d(TAG, "location = " + location);

        Uri uri = Uri.parse(location);
        if (searchQuery == null) {
            return;
        } else if (uri.getPath().equals(RestContract.REST_BASE_PATH + "/search/")) {
            return;
        } else if (!TextUtils.isEmpty(productsURL)) {
            return;
        }

        Log.d(TAG, "detected redirect - setting properties according");
        searchQuery = null;
        productsURL = location;

        return;
    }

    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (event.errorCode.isNetworkError() && pageNumber == 1) {
            ((BaseActivity) getActivity()).showWarning(false);
            ((BaseActivity) getActivity()).showError(new GetProductsEvent(productsURL, searchQuery,
                    pageNumber, MAX_PAGE_ITEMS,
                    sort, dir, md5Hash));
        } else if (!event.errorCode.isNetworkError() && pageNumber == 1
                && productsAdapter.getCount() == 0) {
            Log.d(TAG, "onErrorEvent: No products to show");
            showProductsNotfound();
            ((BaseActivity) getActivity()).showContentContainer();
        } else {
            Log.d(TAG, "onErrorEvent: loading more products failed");
            hideProductsLoading();

            if (totalProducts != -1 && totalProducts > ((pageNumber - 1) * MAX_PAGE_ITEMS)) {
                Toast.makeText(getActivity(), R.string.products_could_notloaded, Toast.LENGTH_SHORT)
                        .show();
            }
        }
        
        isLoadingMore = false;
        return true;
    }

    private void showProductsNotfound() {
        Log.d(TAG, "showProductsLoading");
        productsContent.setVisibility(View.GONE);
        notfound.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        loadingLayout.refreshDrawableState();
        if (mainView != null) {
            mainView.findViewById(R.id.loading_view_pager).setVisibility(View.GONE);
        }
    }

    /**
     * Set all Products using Products Adapter
     */
    private void hideProductsLoading() {
        Log.d(TAG, "hideProductsLoading");
        loadingLayout.setVisibility(View.GONE);
        loadingLayout.refreshDrawableState();
        notfound.setVisibility(View.GONE);
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        // Log.d( TAG, "onMovedToScrapHead: listItemRecycleCount = " + listItemRecycleCount);
        if (listItemRecycleCount > MAX_RECYCLED_PROCESSED_COUNT) {
            listItemRecycleCount = 0;
            Log.d(TAG, "onMovedToScrapHead: gc requested");
            System.gc();
        } else {
            listItemRecycleCount++;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // Make your calculation stuff here. You have all your
        // needed info from the parameters of this function.

        // Sample calculation to determine if the last
        // item is fully visible.
        final int lastItem = firstVisibleItem + visibleItemCount;
        if (totalItemCount != 0 && lastItem == totalItemCount) {
            Log.i( TAG, "onScroll: last item visible ");
            if (!isLoadingMore) {
                Log.i( TAG, "onScroll: last item visible and start loading"+pageNumber);
                isLoadingMore = true;
                showProductsLoading();
                getMoreProducts();
            }
        }
    }

    private void showProductsLoading() {
        Log.d(TAG, "showProductsLoading");
        loadingLayout = getView().findViewById(R.id.loadmore);
        loadingLayout.setVisibility(View.VISIBLE);
        loadingLayout.refreshDrawableState();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // noop
    }
}
