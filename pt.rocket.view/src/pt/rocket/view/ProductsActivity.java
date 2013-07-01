package pt.rocket.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.ProductsListAdapter;
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
import pt.rocket.utils.DialogList;
import pt.rocket.utils.DialogList.OnDialogListListener;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows all products for a respective category.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by sergiopereira, 19/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author sergiopereira
 * 
 * @date 19/06/2012
 * 
 * @description
 * 
 */
public class ProductsActivity extends MyActivity implements
		OnClickListener, OnDialogListListener, OnScrollListener, RecyclerListener {
	private final static String TAG = LogTagHelper.create(ProductsActivity.class);

	private final static String ID_SORT_DIALOG = "sort";
	private final static String KEY_STATE_SORTER = "products_state_sorter";
	private final static String KEY_STATE_VIEW = "products_state_view";

    private DialogList mSortDialog;
    private String title;

	//
	private ProductsListAdapter productsAdapter;
	//
	private GridView productsList;
	private int currentOrientation = Configuration.ORIENTATION_PORTRAIT;
	private View loadingLayout;

	private int MAX_PAGE_ITEMS = 20;
	private int NO_MORE_PAGES = -1;

	boolean isLoadingMore = false;


	private int pageNumber;
	private ProductSort sort = ProductSort.NONE;
	private Direction dir = Direction.ASCENDENT;
	
	/**
	 * Context Menu
	 */
	ActionMode contextMenu;
	

	boolean isFirstBootBrands = true;
	boolean isFirstBootSales = true;
	boolean isFirstBootProducts = true;

	private boolean justonce = false;

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
    
    private View sortButton;
        
    private int listItemRecycleCount;
    private final static int MAX_RECYCLED_PROCESSED_COUNT = 200;
	
	/**
	 * 
	 */
	public ProductsActivity() {
		super(NavigationAction.Products,
		        EnumSet.of(MyMenuItem.SEARCH),
		        EnumSet.of(EventType.GET_PRODUCTS_EVENT),
		        EnumSet.noneOf(EventType.class),
		        0, R.layout.products);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		savedState = savedInstanceState;
        setAppContentLayout();
        init(getIntent());
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init( intent );
    }
	
	public void init( Intent intent ) {
		title = intent.getStringExtra(ConstantsIntentExtra.CONTENT_TITLE);
		setTitle(title);
		productsURL = intent.getExtras().getString(ConstantsIntentExtra.CONTENT_URL);
		Log.d(TAG, "onCreate: productsURL = " + productsURL);
		searchQuery = intent.getExtras().getString(ConstantsIntentExtra.SEARCH_QUERY);
		Log.d(TAG, "onCreate: searchQuery = " + searchQuery);
		
		navigationSource = intent.getIntExtra(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);
		navigationPath = intent.getStringExtra(ConstantsIntentExtra.NAVIGATION_PATH);
	    AnalyticsGoogle.get().trackSourceResWithPath(navigationSource, navigationPath);     

        productsAdapter = new ProductsListAdapter(this);
        productsList.setAdapter(productsAdapter);
        productsList.setRecyclerListener(this);
        listItemRecycleCount = 0;
        
		if (isFirstBootProducts) {
			isFirstBootProducts = false;
		}

		if (null != savedState && savedState.containsKey(KEY_STATE_VIEW)) {
			savedState.remove(KEY_STATE_VIEW);
		}
		
		MAX_PAGE_ITEMS = 20;

	    initSorter();
	    executeRequest();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    Log.i( "onDestroy" );
	    productsAdapter = null;
	    System.gc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveActivityState(outState);
	}

	/**
	 * Set the Products layout using inflate
	 */
	private void setAppContentLayout() {
	    productsContent = findViewById(R.id.products_content );
	    sortButton = findViewById(R.id.sorter_button);
        sortButton.setOnClickListener(this);
        productsList = (GridView) findViewById(R.id.middle_productslist_list);
		productsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                int activePosition = position; // -
                                                // productsAdapter.getJumpConstant();

                if (activePosition > -1) {
                    // // Call Product Details

                    Log.i("TAG", "DIR=======>" + dir + " sort =====> " + sort);

                    saveActivityState();                    
                    ActivitiesWorkFlow.productsDetailsActivity(
                            ProductsActivity.this, ((Product)productsAdapter.getItem(activePosition))
                                    .getUrl(), navigationSource, navigationPath);
                }

            }
        });
        productsList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, this));

		notfound = findViewById(R.id.search_products_not_found);
	    loadingLayout = findViewById(R.id.loadmore);
	}

	private void executeRequest() {
		pageNumber = 1;
		productsAdapter.clearProducts();
		showProductsContent();
		getMoreProducts();
	}

	/**
	 * Get Products Set header and Footer Layout
	 */
	@Override
	public void onResume() {
		super.onResume();
		restoreActivityState(savedState);
		AnalyticsGoogle.get().trackPage( R.string.gproductlist );
	}

	/**
	 * Clean all event listeners
	 */
	@Override
	public void onPause() {
		super.onPause();

		Log.d( TAG, "onPause" );

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

	}

	/**
	 * gets the activity current orientation
	 * 
	 * @return The flag indicating what is the current orientation for the
	 *         activity
	 */
	public int getCurrentOrientation() {
		return currentOrientation;
	}

	/**
	 * gets the next page of products from the API
	 */
	private void getMoreProducts() {

		if (pageNumber != NO_MORE_PAGES) {

			// Test to see if we already have all the products available

			if (pageNumber == 1) {
				showLoading();
			}

	    	beginRequestMillis = System.currentTimeMillis();			
			EventManager.getSingleton().triggerRequestEvent(
						new GetProductsEvent(productsURL, searchQuery, pageNumber, MAX_PAGE_ITEMS,
										sort, dir));
		} else {
			hideProductsLoading();
		}
	}

	/**
	 * Set all Products using Products Adapter
	 */
	private void hideProductsLoading() {
	    Log.d( TAG, "hideProductsLoading" );
		loadingLayout.setVisibility(View.GONE);
		loadingLayout.refreshDrawableState();
	    notfound.setVisibility(View.GONE);
	    isLoadingMore = false;
	}
	
	private void showProductsLoading() {
	    Log.d( TAG, "showProductsLoading" );
        loadingLayout = findViewById(R.id.loadmore);
        loadingLayout.setVisibility(View.VISIBLE);
        loadingLayout.refreshDrawableState();
        isLoadingMore = true;
	}
	
	private void showProductsNotfound() {
	    Log.d( TAG, "showProductsLoading");
	    productsContent.setVisibility( View.GONE );
	    notfound.setVisibility(View.VISIBLE );
	    loadingLayout.setVisibility(View.GONE);
        loadingLayout.refreshDrawableState();
        isLoadingMore = false;
	}
	
	private void showProductsContent() {
	    Log.d( TAG, "showProductsContent");
	    productsContent.setVisibility( View.VISIBLE );
	    notfound.setVisibility( View.GONE );
	    loadingLayout.setVisibility(View.GONE);
        loadingLayout.refreshDrawableState();
        isLoadingMore = false;
	}

	private void initSorter() {

		mSortOptions = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.products_picker)));
		mSortPosition = 0;
		if (null != savedState && savedState.containsKey(KEY_STATE_SORTER)) {
			Log.d(TAG,
					" SORTER SAVED STATE KEY=SORTER : "
							+ savedState.getInt(KEY_STATE_SORTER));
			mSortPosition = savedState.getInt(KEY_STATE_SORTER);
			savedState.remove(KEY_STATE_SORTER);
		}
	}

	private void setSort(int position) {
		mSortPosition = position;

		switch (position) {
		case 0: // <item >Popularity</item>
			sort = ProductSort.POPULARITY;
			dir = Direction.ASCENDENT;
			break;
		case 1: // <item >Price up</item>
			sort = ProductSort.PRICE;
			dir = Direction.ASCENDENT;			break;
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
	
    @Override
    public void onScroll(AbsListView lw, final int firstVisibleItem,
            final int visibleItemCount, final int totalItemCount) {

        // Make your calculation stuff here. You have all your
        // needed info from the parameters of this function.

        // Sample calculation to determine if the last
        // item is fully visible.
        final int lastItem = firstVisibleItem + visibleItemCount;
        if (totalItemCount != 0 && lastItem == totalItemCount) {
            // Log.d( TAG, "onScroll: last item visible ");
            if (!isLoadingMore) {
                showProductsLoading();
                getMoreProducts();
            }            
        }
    }
    


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // noop
    }

	private void saveActivityState(Bundle currentState) {
		currentState.putInt(KEY_STATE_SORTER, mSortPosition);
	}

	private void saveActivityState() {

		Log.d(TAG, " --- SESSION STATE  ----  =======>>>>> saveActivityState");
		SharedPreferences sharedPrefs = getSharedPreferences(
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

		SharedPreferences prefs = getSharedPreferences(
				ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		;

		currentState.putInt(KEY_STATE_SORTER, prefs.getInt(KEY_STATE_SORTER, -1));
		currentState.putInt(KEY_STATE_VIEW, prefs.getInt(KEY_STATE_VIEW, -1));
	}

	@Override
	public void onClick(View v) {
		Log.d( TAG, "sorter position = " + mSortPosition );
		
		int id = v.getId();
		if (id == R.id.sorter_button) {
			mSortDialog = new DialogList(this, ID_SORT_DIALOG, getString( R.string.product_sort),
					mSortOptions, mSortPosition);
			mSortDialog.show();
		}
	}

	@Override
	public void onDialogListItemSelect(String id, int position, String value) {
		if (id.equals(ID_SORT_DIALOG)) {
			setSort(position);
			executeRequest();
		}

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
        MAX_PAGE_ITEMS = 10;

        return;
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // Get Products Event
        ProductsPage productsPage = (ProductsPage) event.result;
        Log.d( TAG, "onSuccessEvent: products on page = " + productsPage.getProducts().size() + 
                " total products = " + productsPage.getTotalProducts());
                
        String location = event.metaData.getString( IMetaData.LOCATION );
        Log.d( TAG, "Location = " + location );
        checkRedirectFromSearch(location);
        
        
        AnalyticsGoogle.get().trackLoadTiming(R.string.gproductlist, beginRequestMillis);
        System.gc();
        if (!TextUtils.isEmpty(searchQuery)) {
            setTitle(title + " (" + productsPage.getTotalProducts() + ")");
        }
        productsAdapter.appendProducts(productsPage.getProducts());
        showProductsContent();

        Log.i(TAG, " " + productsPage.getProducts().size());
        pageNumber = productsPage.getProducts().size() < MAX_PAGE_ITEMS ? NO_MORE_PAGES
                : pageNumber + 1;
        if (productsPage.getProducts().size() < MAX_PAGE_ITEMS) {
            isLoadingMore = true;
        }
        AnalyticsGoogle.get().trackSearch(searchQuery, productsPage.getTotalProducts());
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        return true;
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (!event.errorCode.isNetworkError() && pageNumber == 1 && productsAdapter.getCount() == 0) {
            Log.d(TAG, "onErrorEvent: No products to show");
            showProductsNotfound();
            showContentContainer();
            return true;
        } else {
            Log.d( TAG, "onErrorEvent: loading more products failed");
            hideProductsLoading();
            Toast.makeText(getApplicationContext(), R.string.products_could_notloaded, Toast.LENGTH_LONG).show();
            return true;
        }
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
}
