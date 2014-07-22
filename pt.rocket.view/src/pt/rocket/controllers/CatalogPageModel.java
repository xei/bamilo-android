package pt.rocket.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
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
import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.ProductSort;
import pt.rocket.helpers.GetProductsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import pt.rocket.view.fragments.Catalog;
import android.content.ContentValues;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

public class CatalogPageModel {

    private static final String TAG = CatalogPageModel.class.getName();

    private int pageNumber;

    private boolean isLandScape = false;

    private ProductSort sort = ProductSort.NONE;
    private Direction dir = Direction.ASCENDENT;

    private int MAX_PAGE_ITEMS = 18;
    private int NO_MORE_PAGES = -1;

    private int index;
    private String sortTitle;

    private static String productsURL;
    private static String searchQuery;
    private static String navigationPath;
    private static String title;
    private static int navigationSource;

    private boolean showList;

    private ContentValues filters;

    private int mFilterMD5 = -1;

    private ProductsListAdapter productsAdapter;

    private int lastItem;

    /**
     * Layout Stuff
     */
    private RelativeLayout relativeLayout;
    // Spnf - search_products_not_found
    private TextView textViewSpnf;
    private String text;

    // Ravb - retry_alert_view_button
    private Button buttonRavb;

    // pc products_content
    private RelativeLayout relativeLayoutPc;

    // Lm loading_more
    private LinearLayout linearLayoutLm;
    private ProgressBar progressBarLm;
    private TextView textViewLm;
    private GridView gridView;
    // Lb - loading_bar
    private LinearLayout linearLayoutLb;
    private LoadingBarView loadingBarView;
    private TextView textViewLb;

    private BaseActivity mActivity;

    private long mBeginRequestMillis;

    private boolean isLoadingMore = false;

    private int totalProducts = -1;

    // Flag used to stop the loading more when an error occurs
    private boolean receivedError = false;

    private Catalog mFragment;

    private int mSwitchMD5;

    public CatalogPageModel(int index, BaseActivity activity, Catalog mFragment) {
        this.index = index;
        this.mActivity = activity;
        setIndex(index);
        this.mFragment = mFragment;
        switch (index) {
        case 0: // <item > Copy of Best Rating for infinite scroll</item>
            // option
            sort = ProductSort.BESTRATING;
            dir = Direction.DESCENDENT;
            break;
        case 1: // <item >Popularity</item>
            sort = ProductSort.POPULARITY;
            dir = Direction.DESCENDENT;
            break;
        case 2: // <item >Price up</item>
            sort = ProductSort.NEWIN;
            dir = Direction.DESCENDENT;
            break;
        case 3: // <item >Price up</item>
            sort = ProductSort.PRICE;
            dir = Direction.ASCENDENT;
            break;
        case 4: // <item >Price down</item>
            sort = ProductSort.PRICE;
            dir = Direction.DESCENDENT;
            break;
        case 5: // <item >Name</item>
            // Offer option
            sort = ProductSort.NAME;
            dir = Direction.ASCENDENT;
            break;
        case 6: // <item >Brand</item>
            // option
            sort = ProductSort.BRAND;
            dir = Direction.ASCENDENT;
            break;
        case 7: // <item >Best Rating</item>
            // option
            sort = ProductSort.BESTRATING;
            dir = Direction.DESCENDENT;
            break;

        case 8: // <item >Copy of Popularity for infinite scroll</item>
            sort = ProductSort.POPULARITY;
            dir = Direction.DESCENDENT;
            break;

        }
    }

    /*
     * Get total number of products
     */
    public int getTotalProducts() {

        return totalProducts;
    }

    public void notifyContentDataSetChanged() {
        if (productsAdapter != null) {
            this.productsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * set CatalogPageModel's static variables</br> adjust the layout</br> create a new
     * ProductsListAdapter</br>
     * 
     * @param p
     *            productsURL
     * @param s
     *            searchQuery
     * @param n
     *            navigationPath
     * @param t
     *            title
     * @param navSource
     *            navigationSource
     * @param filterValues
     *            filters
     * @param showList
     *            show list (or grid)
     * @param totalUpdates
     *            number of global calls to setVariables
     */
    public void setVariables(String p, String s, String n, String t, int navSource,
            ContentValues filterValues, boolean showList, int totalUpdates) {

        Log.d(TAG, "FILTER SET VARIABLES: " + p + " " + s + " " + n + " " + t + " " + navSource);

        CatalogPageModel.productsURL = p;
        CatalogPageModel.searchQuery = s;
        CatalogPageModel.navigationPath = n;
        CatalogPageModel.title = t;
        CatalogPageModel.navigationSource = navSource;

        this.showList = showList;

        Log.i(TAG, "FILTER IS DIFF: "
                + ((filterValues != null) ? filterValues.getAsInteger("md5") : "IS NULL"));

        // Case no content
        if (!hasContent()) {
            Log.i(TAG, "CONTENT IS EMPTY GET CATALOG");
            this.filters = filterValues;
            threadRequest();
        }
        // Case new filter
        else if (filterValues != null && filterValues.getAsInteger("md5") != this.mFilterMD5) {
            Log.i(TAG, "FILTER IS DIFF: " + filterValues.getAsInteger("md5") + " "
                    + this.mFilterMD5);
            this.mFilterMD5 = filterValues.getAsInteger("md5");
            this.filters = filterValues;
            threadRequest();
            // Case update layout
        } else if (this.mSwitchMD5 != totalUpdates) {
            Log.i(TAG, "SWITCH LAYOUT");
            switchLayout(showList, 0);
            this.mSwitchMD5 = totalUpdates;
        } else {
            Log.i(TAG, "SHOW");
        }

    }

    private void threadRequest() {
        executeRequest();
        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // executeRequest();
        // }
        // }).run();
    }

    /**
     * adjust the layout</br> create a new ProductsListAdapter</br>
     * 
     * @param showList
     *            show list (or grid)
     * @param totalUpdates
     *            number of global calls to setVariables
     */
    public void switchLayout(boolean showList, int totalUpdates) {
        // save products from current productsAdapter to add to new Adapter
        // List<Product> products = productsAdapter.products;
        Log.d(TAG, "SWITCHING LAYOUT: " + showList + " " + totalUpdates + " " + isLandScape);
        // this.showList = showList;
        // this.totalUpdates = totalUpdates;
        // generateProductsListAdapter();
        onSwitchLayout();
        // productsAdapter.appendProducts(products);

        showCatalogContent();
        isLoadingMore = false;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // hide progress called on switch layout
                mActivity.dismissProgress();
            }
        }, 300);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        setText(index);
    }

    public String getText() {
        return text;
    }

    private void setText(int index) {
        this.text = String.format("Page %s", index);
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public String getTitle() {
        return sortTitle;
    }

    public void setTitle(String title) {
        this.sortTitle = title;
    }

    public Boolean getShowList() {
        return showList;
    }

    public int getTotalUpdates() {
        return mSwitchMD5;
    }

    public TextView getTextViewSpnf() {
        return textViewSpnf;
    }

    public void setTextViewSpnf(TextView textViewSpnf) {
        this.textViewSpnf = textViewSpnf;
    }

    public Button getButtonRavb() {
        return buttonRavb;
    }

    public void setButtonRavb(Button buttonRavb) {
        this.buttonRavb = buttonRavb;
    }

    public RelativeLayout getRelativeLayoutPc() {
        return relativeLayoutPc;
    }

    public void setRelativeLayoutPc(RelativeLayout relativeLayoutPc) {
        this.relativeLayoutPc = relativeLayoutPc;
    }

    public LinearLayout getLinearLayoutLm() {
        return linearLayoutLm;
    }

    public void setLinearLayoutLm(LinearLayout linearLayoutLm) {
        this.linearLayoutLm = linearLayoutLm;
    }

    public ProgressBar getProgressBarLm() {
        return progressBarLm;
    }

    public void setProgressBarLm(ProgressBar progressBarLm) {
        this.progressBarLm = progressBarLm;
    }

    public TextView getTextViewLm() {
        return textViewLm;
    }

    public void setTextViewLm(TextView textViewLm) {
        this.textViewLm = textViewLm;
    }

    /**
     * @return the gridView
     */
    public GridView getGridView() {
        return gridView;
    }

    /**
     * @param gridView
     *            the gridView to set
     */
    public void setGridView(GridView gridView, boolean isLandscape) {
        this.gridView = gridView;
        this.setLandScape(isLandscape);
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                int activePosition = position; // -
                                               // productsAdapter.getJumpConstant();

                if (activePosition > -1) {
                    // // Call Product Details

                    Log.i("TAG", "DIR=======>" + dir + " sort =====> " + sort);
                    JumiaApplication.INSTANCE.showRelatedItemsGlobal = true;
                    Product product = mFragment.getProduct((String) productsAdapter
                            .getItem(activePosition));
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, product.getUrl());
                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
                    bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrand() + " "
                            + product.getName());
                    if (title != null)
                        bundle.putString(ProductDetailsActivityFragment.PRODUCT_CATEGORY, title);
                    mActivity.onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
                            FragmentController.ADD_TO_BACK_STACK);
                }

            }
        });

    }

    public LinearLayout getLinearLayoutLb() {
        return linearLayoutLb;
    }

    public void setLinearLayoutLb(LinearLayout linearLayoutLb) {
        this.linearLayoutLb = linearLayoutLb;
    }

    public LoadingBarView getLoadingBarView() {
        return loadingBarView;
    }

    public void setLoadingBarView(LoadingBarView loadingBarView) {
        this.loadingBarView = loadingBarView;
    }

    public TextView getTextViewLb() {
        return textViewLb;
    }

    public void setTextViewLb(TextView textViewLb) {
        this.textViewLb = textViewLb;
    }

    /**
     * End of Layout Stuff
     */

    /**
     * Logic Stuff
     */

    private void executeRequest() {
        Log.d(TAG, "FILTER EXECUTE REQ");

        generateProductsListAdapter();

        getMoreProducts();
    }

    private void generateProductsListAdapter() {
        int numColumns = 1;
        if (showList) {
            if (isLandScape) {
                // Tablet uses 3 columns for both Grid and List
                numColumns = 3;
            } else {
                // Phone uses 1 column for List
                numColumns = 1;
            }
        } else {
            if (isLandScape) {
                // Tablet uses 3 columns for both Grid and List
                numColumns = 3;
            } else {
                // Phone uses 2 columns for Grid
                numColumns = 2;
            }
        }
        gridView.setNumColumns(numColumns);

        // initialize new adapter depending on view choosen
        productsAdapter = new ProductsListAdapter(mActivity, mFragment, showList, numColumns);

        pageNumber = 1;
        showProductsContent();
        productsAdapter.clearProducts();
    }

    /**
     * Update the layout
     */
    private void onSwitchLayout() {
        int numColumns = 1;
        if (showList) {
            if (isLandScape) {
                // Tablet uses 3 columns for both Grid and List
                numColumns = 3;
            } else {
                // Phone uses 1 column for List
                numColumns = 1;
            }
        } else {
            if (isLandScape) {
                // Tablet uses 3 columns for both Grid and List
                numColumns = 3;
            } else {
                // Phone uses 2 columns for Grid
                numColumns = 2;
            }
        }
        gridView.setNumColumns(numColumns);

        if (productsAdapter != null) {
            List<String> products = productsAdapter.products;
            productsAdapter = new ProductsListAdapter(mActivity, mFragment, showList, numColumns);
            productsAdapter.appendProducts(products);
            gridView.setAdapter(productsAdapter);
            gridView.setSelection(0);
            gridView.setOnScrollListener(scrollListener);
        } else {
            executeRequest();
        }

    }

    /**
     * gets the next page of products from the API
     */
    private void getMoreProducts() {
        Log.d(TAG, "FILTER GET MORE PRODUCTS");

        if (pageNumber != NO_MORE_PAGES) {
            // Test to see if we already have all the products available

            if (pageNumber == 1) {
                if (relativeLayout != null) {
                    hideProductsNotFound();
                    linearLayoutLb.setVisibility(View.VISIBLE);
                    showLoadingInfo();
                }
            }

            mBeginRequestMillis = System.currentTimeMillis();

            Log.d(TAG, "FILTER CREATE BUNDLE");

            Bundle bundle = new Bundle();
            bundle.putString(GetProductsHelper.PRODUCT_URL, productsURL);
            bundle.putString(GetProductsHelper.SEARCH_QUERY, searchQuery);
            bundle.putInt(GetProductsHelper.PAGE_NUMBER, pageNumber);
            bundle.putInt(GetProductsHelper.TOTAL_COUNT, MAX_PAGE_ITEMS);
            bundle.putInt(GetProductsHelper.SORT, sort.id);
            bundle.putInt(GetProductsHelper.DIRECTION, dir.id);
            bundle.putParcelable(GetProductsHelper.FILTERS, filters);

            // mActivity.showLoading(false);
            JumiaApplication.INSTANCE
                    .sendRequest(new GetProductsHelper(), bundle, responseCallback);

        } else {
            hideProductsLoading();
        }
    }

    private void showProductsContent() {
        Log.d(TAG, "showProductsContent");
        if (pageNumber == 1) {
            Log.i(TAG, "scrolling to position 0");
            gridView.setSelection(0);
        }
        gridView.setOnScrollListener(scrollListener);

        relativeLayoutPc.setVisibility(View.VISIBLE);
        textViewSpnf.setVisibility(View.GONE);
        linearLayoutLm.setVisibility(View.GONE);
        linearLayoutLm.refreshDrawableState();

        gridView.setAdapter(productsAdapter);

        productsAdapter.notifyDataSetChanged();

        // Loading is being hidden on getMoreProducts previously called on executeRequest()
        /*-if (relativeLayout != null) {
            linearLayoutLb.setVisibility(View.GONE);
            hideLoadingInfo();
        }*/
    }

    private OnScrollListener scrollListener = new OnScrollListener() {

        private int previousFirstVisibleItem = 0;
        private long previousEventTime = 0;
        private double speed = 0;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                Log.d(TAG, "  ---- > Scroll: Fling");
                RocketImageLoader.getInstance().stopProcessingQueue(Catalog.requestTag);
            } else {
                Log.d(TAG, "  ---- > Scroll: Idle");
                RocketImageLoader.getInstance().startProcessingQueue();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
            // Make your calculation stuff here. You have all your
            // needed info from the parameters of this function.

            // Sample calculation to determine if the last
            // item is fully visible.
            lastItem = firstVisibleItem + visibleItemCount;
            if (totalItemCount != 0 && lastItem == totalItemCount) {
                // Log.i(TAG, "onScroll: last item visible ");
                if (!isLoadingMore && !receivedError) {
                    // Log.i(TAG, "onScroll: last item visible and start loading" + pageNumber);
                    isLoadingMore = true;
                    showProductsLoading();
                    new Handler().postDelayed(new Runnable() {
                        
                        @Override
                        public void run() {
                            getMoreProducts();
                            
                        }
                    }, 300);
                    // new Thread(new Runnable() {
                    //
                    // @Override
                    // public void run() {
                    // getMoreProducts();
                    // }
                    // }).run();

                }
            } else {
                receivedError = false;
            }

            if (previousFirstVisibleItem != firstVisibleItem) {
                long currTime = System.currentTimeMillis();
                long timeToScrollOneElement = currTime - previousEventTime;
                speed = ((double) 1 / timeToScrollOneElement) * 1000;

                previousFirstVisibleItem = firstVisibleItem;
                previousEventTime = currTime;

                Log.d(TAG, "  ---- > Speed: " + speed + " elements/second");

                if (speed < 15) {
                    RocketImageLoader.getInstance().startProcessingQueue();
                } 
//                else {
//                    RocketImageLoader.getInstance().stopProcessingQueue();
//                }
            }

            // int lastVisibleItem = firstVisibleItem + visibleItemCount;
            //
            // if (totalItemCount != 0) {
            // int halfTotalItemCount = totalItemCount / 2;
            // if (halfTotalItemCount % 2 != 0)
            // halfTotalItemCount = halfTotalItemCount + 1;
            //
            // if (lastVisibleItem >= halfTotalItemCount) {
            // if (!isLoadingMore) {
            // isLoadingMore = true;
            // showProductsLoading();
            // getMoreProducts();
            // }
            //
            // } else if (lastVisibleItem == totalItemCount) {
            // if (!isLoadingMore) {
            // isLoadingMore = true;
            // showProductsLoading();
            // getMoreProducts();
            // }
            //
            // }
            //
            // } else {
            // isLoadingMore = false;
            // receivedError = false;
            // }

        }
    };

    private void showCatalogContent() {
        relativeLayoutPc.setVisibility(View.VISIBLE);
        textViewSpnf.setVisibility(View.GONE);
        linearLayoutLm.setVisibility(View.GONE);
        linearLayoutLm.refreshDrawableState();
        if (relativeLayout != null) {
            linearLayoutLb.setVisibility(View.GONE);
            hideLoadingInfo();
        }
    }

    private void showProductsLoading() {
        Log.d(TAG, "showProductsLoading");
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                linearLayoutLm.setVisibility(View.VISIBLE);
                linearLayoutLm.refreshDrawableState();   
            }
        }, 200);
    }

    private void hideProductsNotFound() {
        textViewSpnf.setVisibility(View.GONE);
        buttonRavb.setVisibility(View.GONE);
    }

    private void hideProductsLoading() {
        Log.d(TAG, "hideProductsLoading");
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                linearLayoutLm.setVisibility(View.GONE);
                linearLayoutLm.refreshDrawableState();
                textViewSpnf.setVisibility(View.GONE);
            }
        }, 200);
    }

    /**
     * This method generates a unique and always diferent MD5 hash based on a given key
     * 
     * @param key
     * @return the unique MD5
     */
    protected static String uniqueMD5(String key) {
        String md5String = "";
        try {
            Calendar calendar = Calendar.getInstance();
            key = key + calendar.getTimeInMillis();

            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            md5String = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md5String;

    }

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

    public ArrayList<CatalogFilter> getFilters() {
        return mFilters;
    }

    public ArrayList<CatalogFilter> mFilters;

    // private ArrayList<CatalogFilter> mCatalogFilters;

    // @Override
    // public void handleEvent(ResponseEvent event) {
    // if (event.getSuccess()) {
    // processSuccess((ResponseResultEvent<?>) event);
    // } else {
    // processError(event);
    // }
    //
    // }

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

    private void showProductsNotfound() {
        Log.d(TAG, "showProductsNotfound");
        relativeLayoutPc.setVisibility(View.GONE);
        textViewSpnf.setVisibility(View.VISIBLE);
        linearLayoutLm.setVisibility(View.GONE);
        linearLayoutLm.refreshDrawableState();
        buttonRavb.setVisibility(View.VISIBLE);
        buttonRavb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideProductsNotFound();
                getMoreProducts();

            }
        });
        if (relativeLayout != null) {
            linearLayoutLb.setVisibility(View.GONE);
            hideLoadingInfo();
        }
    }

    private void onErrorEvent(Bundle bundle) {
        if (mActivity.handleErrorEvent(bundle)) {
            return;
        }

        // Validate the request was performed by Filter
        boolean hasFilter = isRequestFromFilter();

        // Log.i(TAG, "code1 product list on error event");
        // EventType eventType = (EventType)
        // bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (errorCode != null && pageNumber == 1) {
            FeaturedBox featuredBox = null;

            Object featureBoxObject = bundle.get(Constants.BUNDLE_RESPONSE_KEY);
            if (featureBoxObject != null && featureBoxObject instanceof FeaturedBox) {
                featuredBox = (FeaturedBox) featureBoxObject;
            }

            // Show respective error layout
            if (!TextUtils.isEmpty(searchQuery) & !hasFilter) {
                // For search query
                mFragment.onErrorSearchResult(featuredBox);
            } else {
                // For category and filter
                showProductsNotfound();
            }

            mActivity.showContentContainer();
        } else {
            Log.d(TAG, "onErrorEvent: loading more products failed");
            hideProductsLoading();
            if (totalProducts != -1 && totalProducts > ((pageNumber - 1) * MAX_PAGE_ITEMS)) {
                Toast.makeText(mActivity, R.string.products_could_notloaded, Toast.LENGTH_SHORT)
                        .show();
            }
        }
        mBeginRequestMillis = System.currentTimeMillis();
        isLoadingMore = false;
        receivedError = true;
    }

    /**
     * Validate if the request was triggered by filter. Show the old catalog and warning the parent
     * fragment
     * 
     * @return boolean
     * @author sergiopereira
     */
    private boolean isRequestFromFilter() {
        if (filters != null && mFragment != null) {
            Log.d(TAG, "FILTERS: ON ERROR EVENT");
            // Sent to Parent that was received an error on load catalog
            mFragment.onErrorLoadingFilteredCatalog();
            return true;
        } else {
            Log.d(TAG, "FILTERS IS NULL");
            return false;
        }
    }

    public void setTotalItemLable() {
        TextView totalItems = (TextView) mActivity.findViewById(R.id.totalProducts);
        if (getTotalProducts() > 0) {
            StringBuilder total = new StringBuilder(" (").append(getTotalProducts()).append(" ")
                    .append(mActivity.getString(R.string.shoppingcart_items)).append(")");
            totalItems.setText(total.toString());
            totalItems.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Validate if has content
     * 
     * @return boolean
     * @author sergiopereira
     */
    public boolean hasContent() {
        return productsAdapter != null;
    }

    private void onSuccessEvent(Bundle bundle) {

        // TODO : Validate
        // mActivity.handleSuccessEvent(bundle);

        Log.d(TAG, "ON SUCCESS EVENT");

        // Get Products Event
        final ProductsPage productsPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);

        // Get Location
        final String location = bundle.getString(IMetaData.LOCATION);
        Log.d(TAG, "Location = " + location);

        // Validate title
        if (TextUtils.isEmpty(title)) {
            title = productsPage.getName();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // Valdiate products
                if (productsPage != null && productsPage.getTotalProducts() > 0) {
                    Log.d(TAG, "onSuccessEvent: products on page = "
                            + productsPage.getProducts().size() + " total products = "
                            + productsPage.getTotalProducts());

                    // TODO: Improve this behavior
                    if (index == 1 && pageNumber == 1) {
                        RelatedItemsTableHelper.insertRelatedItemsAndClear(mActivity,
                                productsPage.getProductsList());
                    } else if (index == 1 && pageNumber == 2) {
                        RelatedItemsTableHelper.insertRelatedItems(mActivity,
                                productsPage.getProductsList());
                    }

                    mFragment.addProductsCollection(productsPage.getProductsMap());

                    totalProducts = productsPage.getTotalProducts();
                    if (mFragment.isVisible()) {
                        // set total items lable
                        if (getTotalProducts() > 0)
                            mActivity.setTitleAndSubTitle(
                                    title,
                                    " (" + String.valueOf(getTotalProducts()) + " "
                                            + mActivity.getString(R.string.shoppingcart_items)
                                            + ")");
                        else
                            mActivity.setTitle(title);
                    }
                }

                if (location != null) {
                    checkRedirectFromSearch(location);
                }

                AnalyticsGoogle.get().trackLoadTiming(R.string.gproductlist, mBeginRequestMillis);

                if (!TextUtils.isEmpty(searchQuery)) {
                    String query = searchQuery.replaceAll("--", ", ");
                    if (mFragment.isVisible()) {
                        if (getTotalProducts() > 0)
                            mActivity.setTitleAndSubTitle(
                                    query,
                                    " (" + productsPage.getTotalProducts() + " "
                                            + mActivity.getString(R.string.shoppingcart_items)
                                            + ")");
                        else
                            mActivity.setTitle(query);
                    }
                    if (pageNumber == 1) {
                        TrackerDelegator.trackSearchViewSortMade(mActivity.getApplicationContext(),
                                query,
                                productsPage.getTotalProducts(), sort.name());

                        TrackerDelegator.trackSearchMade(mActivity.getApplicationContext(), query,
                                productsPage.getTotalProducts());
                        AnalyticsGoogle.get().trackSearch(query, productsPage.getTotalProducts());
                    }

                } else {
                    if (pageNumber == 1) {
                        TrackerDelegator.trackCategoryView(mActivity.getApplicationContext(),
                                title,
                                pageNumber);
                    }
                }

                try {
                    productsAdapter.appendProducts(productsPage.getProducts());
                } catch (NullPointerException e) {
                    Log.w(TAG, "NPE ON APPEND PRODUCTS: ");
                    e.printStackTrace();
                }

                // Log.i(TAG, "code1 " + productsPage.getProducts().size() + " pageNumber is : " +
                // pageNumber);

                pageNumber = productsPage.getProducts().size() >= productsPage.getTotalProducts() ? NO_MORE_PAGES
                        : pageNumber + 1;
                showCatalogContent();
                if (totalProducts < ((pageNumber - 1) * MAX_PAGE_ITEMS)) {
                    pageNumber = NO_MORE_PAGES;
                }
                isLoadingMore = false;
                if (productsPage.getProducts().size() >= productsPage.getTotalProducts()) {
                    isLoadingMore = true;
                }

                // Updated filter
                if (mFragment.isVisible()) {
                    mFragment.onSuccesLoadingFilteredCatalog(productsPage.getFilters());
                }

                mActivity.showContentContainer();

            }
        }, 200);
    }

    /**
     * @return the isLandScape
     */
    public boolean isLandScape() {
        return isLandScape;
    }

    /**
     * @param isLandScape
     *            the isLandScape to set
     */
    public void setLandScape(boolean isLandScape) {
        this.isLandScape = isLandScape;
    }

    private final void showLoadingInfo() {
        Log.d(TAG, "Showing loading info");
        if (loadingBarView != null) {
            loadingBarView.startRendering();
        }
    }

    /**
     * Hides the loading screen that appears on the front of the activity while it waits for the
     * data to arrive from the server
     */
    private void hideLoadingInfo() {
        Log.d(TAG, "Hiding loading info");
        if (loadingBarView != null) {
            loadingBarView.stopRendering();
        }
    }

    public void invalidatePage() {
        if (null != productsAdapter)
            productsAdapter.notifyDataSetChanged();
    }
}
