package pt.rocket.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.EnumSet;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.akquinet.android.androlog.Log;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.IMetaData;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetProductsEvent;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Direction;
import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.ProductSort;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class CatalogPageModel implements ResponseListener {

    private static final String TAG = CatalogPageModel.class.getName();

    private int pageNumber;

    private boolean isLandScape = false;
    
    private ProductSort sort = ProductSort.NONE;
    private Direction dir = Direction.ASCENDENT;

    private int MAX_PAGE_ITEMS = 15;
    private int NO_MORE_PAGES = -1;

    private int index;
    private String sortTitle;

    private static String productsURL;
    private static String searchQuery;
    private static String navigationPath;
    private static String title;
    private static int navigationSource;

    private ProductsListAdapter productsAdapter;

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
    private ListView listView;
    private GridView gridView;
    // Lb - loading_bar
    private LinearLayout linearLayoutLb;
    private LoadingBarView loadingBarView;
    private TextView textViewLb;

    // Pt - products_tip
    private RelativeLayout relativeLayoutPt;
    private ImageView imageViewPt;
    private TextView textViewPt;

    private Activity mActivity;

    private String md5Hash;

    private long mBeginRequestMillis;

    private static boolean isLoadingMore = false;

    private int totalProducts = -1;

    public CatalogPageModel(int index, Activity activity) {
        this.index = index;
        this.mActivity = activity;
        setIndex(index);
        md5Hash = uniqueMD5(TAG + index);
        switch (index) {
        case 0: // <item > Copy of Brand for infinite scroll</item>
            // TODO when available change this to Sales
            // option
            sort = ProductSort.BRAND;
            dir = Direction.ASCENDENT;
            break;
        case 1: // <item >Popularity</item>
            sort = ProductSort.POPULARITY;
            dir = Direction.DESCENDENT;
            break;
        case 2: // <item >Price up</item>
            sort = ProductSort.PRICE;
            dir = Direction.ASCENDENT;
            break;
        case 3: // <item >Price down</item>
            sort = ProductSort.PRICE;
            dir = Direction.DESCENDENT;
            break;
        case 4: // <item >Name</item>
            // TODO when available change this to Top
            // Offer option
            sort = ProductSort.NAME;
            dir = Direction.ASCENDENT;
            break;
        case 5: // <item >Brand</item>
            // TODO when available change this to Sales
            // option
            sort = ProductSort.BRAND;
            dir = Direction.ASCENDENT;
            break;

        case 6: // <item >Copy of Popularity for infinite scroll</item>
            sort = ProductSort.POPULARITY;
            dir = Direction.DESCENDENT;
            break;

        }
    }

    public void setVariables(String p, String s, String n, String t, int navSource) {
        CatalogPageModel.productsURL = p;
        CatalogPageModel.searchQuery = s;
        CatalogPageModel.navigationPath = n;
        CatalogPageModel.title = t;
        CatalogPageModel.navigationSource = navSource;

        if (index == 1) {
            showTips();
        }

        EventManager.getSingleton().addResponseListener(this,
                EnumSet.of(EventType.GET_PRODUCTS_EVENT));
        executeRequest();
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

    public ListView getListView() {
        return listView;
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
    public void setGridView(GridView gridView) {
        this.gridView = gridView;
        this.setLandScape(true);
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                int activePosition = position; // -
                                               // productsAdapter.getJumpConstant();

                if (activePosition > -1) {
                    // // Call Product Details

                    Log.i("TAG", "DIR=======>" + dir + " sort =====> " + sort);

                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL,
                            ((Product) productsAdapter.getItem(activePosition)).getUrl());
                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
                    if(title != null)
                        bundle.putString(ProductDetailsActivityFragment.PRODUCT_CATEGORY, title);
                    ((BaseActivity) mActivity).onSwitchFragment(FragmentType.PRODUCT_DETAILS,
                            bundle, FragmentController.ADD_TO_BACK_STACK);
                }

            }
        });

    }

    public void setListView(ListView listView) {        
        this.listView = listView;
        this.setLandScape(false);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                int activePosition = position; // -
                                               // productsAdapter.getJumpConstant();

                if (activePosition > -1) {
                    // // Call Product Details

                    Log.i("TAG", "DIR=======>" + dir + " sort =====> " + sort);

                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL,
                            ((Product) productsAdapter.getItem(activePosition)).getUrl());
                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
                    if(title != null)
                        bundle.putString(ProductDetailsActivityFragment.PRODUCT_CATEGORY, title);
                    ((BaseActivity) mActivity).onSwitchFragment(FragmentType.PRODUCT_DETAILS,
                            bundle, FragmentController.ADD_TO_BACK_STACK);
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

    public RelativeLayout getRelativeLayoutPt() {
        return relativeLayoutPt;
    }

    public void setRelativeLayoutPt(RelativeLayout relativeLayoutPt) {
        this.relativeLayoutPt = relativeLayoutPt;
    }

    public ImageView getImageViewPt() {
        return imageViewPt;
    }

    public void setImageViewPt(ImageView imageViewPt) {
        this.imageViewPt = imageViewPt;
    }

    /**
     * @return the textViewPt
     */
    public TextView getTextViewPt() {
        return textViewPt;
    }

    /**
     * @param textViewPt
     *            the textViewPt to set
     */
    public void setTextViewPt(TextView textViewPt) {
        this.textViewPt = textViewPt;
    }

    /**
     * End of Layout Stuff
     */

    /**
     * Logic Stuff
     */

    private void executeRequest() {
        productsAdapter = new ProductsListAdapter(mActivity);
        pageNumber = 1;
        showProductsContent();
        productsAdapter.clearProducts();
        getMoreProducts();
    }

    /**
     * gets the next page of products from the API
     */
    private void getMoreProducts() {
        Log.d(TAG, "GET MORE PRODUCTS");

        if (pageNumber != NO_MORE_PAGES) {
            // Test to see if we already have all the products available

            if (pageNumber == 1) {
                if (relativeLayout != null) {
                    hideProductsNotFound();
                    linearLayoutLb.setVisibility(View.VISIBLE);
                }
            }
            EventManager.getSingleton().addResponseListener(this,
                    EnumSet.of(EventType.GET_PRODUCTS_EVENT));

            mBeginRequestMillis = System.currentTimeMillis();
            EventManager.getSingleton().triggerRequestEvent(
                    new GetProductsEvent(productsURL, searchQuery, pageNumber, MAX_PAGE_ITEMS,
                            sort, dir, md5Hash));
        } else {
            hideProductsLoading();
        }
    }

    private void showProductsContent() {
        Log.d(TAG, "showProductsContent");
        if(this.isLandScape){
            if (pageNumber == 1) {
                Log.i(TAG, "scrolling to position 0");
                gridView.setSelection(0);
            }
            gridView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

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
                        Log.i(TAG, "onScroll: last item visible ");
                        if (!isLoadingMore) {
                            Log.i(TAG, "onScroll: last item visible and start loading" + pageNumber);
                            isLoadingMore = true;
                            showProductsLoading();
                            getMoreProducts();
                        }
                    }

                }
            });
        } else {
            if (pageNumber == 1) {
                Log.i(TAG, "scrolling to position 0");
                listView.setSelection(0);
            }
            listView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

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
                        Log.i(TAG, "onScroll: last item visible ");
                        if (!isLoadingMore) {
                            Log.i(TAG, "onScroll: last item visible and start loading" + pageNumber);
                            isLoadingMore = true;
                            showProductsLoading();
                            getMoreProducts();
                        }
                    }

                }
            });
        }
        
        relativeLayoutPc.setVisibility(View.VISIBLE);
        textViewSpnf.setVisibility(View.GONE);
        linearLayoutLm.setVisibility(View.GONE);
        linearLayoutLm.refreshDrawableState();
        
        if(this.isLandScape){
            gridView.setAdapter(productsAdapter);
        } else {
            listView.setAdapter(productsAdapter);    
        }
        
        if (relativeLayout != null) {
            linearLayoutLb.setVisibility(View.GONE);
        }
    }
    
    private void showCatalogContent(){
        relativeLayoutPc.setVisibility(View.VISIBLE);
        textViewSpnf.setVisibility(View.GONE);
        linearLayoutLm.setVisibility(View.GONE);
        linearLayoutLm.refreshDrawableState();
        if (relativeLayout != null) {
            linearLayoutLb.setVisibility(View.GONE);
        }
    }

    private void showProductsLoading() {
        Log.d(TAG, "showProductsLoading");
        linearLayoutLm.setVisibility(View.VISIBLE);
        linearLayoutLm.refreshDrawableState();
    }

    private void hideProductsNotFound() {
        textViewSpnf.setVisibility(View.GONE);
        buttonRavb.setVisibility(View.GONE);
    }

    private void hideProductsLoading() {
        Log.d(TAG, "hideProductsLoading");
        linearLayoutLm.setVisibility(View.GONE);
        linearLayoutLm.refreshDrawableState();
        textViewSpnf.setVisibility(View.GONE);
    }

    /**
     * Show tips if is the first time the user uses the app.
     */
    private void showTips() {
        final SharedPreferences sharedPrefs =
                mActivity.getSharedPreferences(
                        ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (sharedPrefs.getBoolean(ConstantsSharedPrefs.KEY_SHOW_PRODUCTS_TIPS, true)) {
            RelativeLayout productsTip = (RelativeLayout) relativeLayout
                    .findViewById(R.id.products_tip);
            productsTip.setVisibility(View.VISIBLE);
            productsTip.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setVisibility(View.GONE);

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PRODUCTS_TIPS,
                            false);
                    editor.commit();
                    return false;
                }
            });
        }
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

    @Override
    public void handleEvent(ResponseEvent event) {
        if (event.getSuccess()) {
            processSuccess((ResponseResultEvent<?>) event);
        } else {
            processError(event);
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
            relativeLayout.findViewById(R.id.loading_view_pager).setVisibility(View.GONE);
        }
    }

    private void processError(ResponseEvent event) {
        if (event.errorCode != null && pageNumber == 1) {
            showProductsNotfound();
            ((BaseActivity) mActivity).showContentContainer();
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
    }

    private void processSuccess(ResponseResultEvent<?> event) {
        Log.d(TAG, "ON SUCCESS EVENT");

        // Get Products Event
        ProductsPage productsPage = (ProductsPage) event.result;
        Log.d(TAG, "onSuccessEvent: products on page = " + productsPage.getProducts().size() +
                " total products = " + productsPage.getTotalProducts());
        if (productsPage != null && productsPage.getTotalProducts() > 0) {
            totalProducts = productsPage.getTotalProducts();
        }

        String location = event.metaData.getString(IMetaData.LOCATION);
        Log.d(TAG, "Location = " + location);
        checkRedirectFromSearch(location);

        AnalyticsGoogle.get().trackLoadTiming(R.string.gproductlist, mBeginRequestMillis);

        if (searchQuery != null && !TextUtils.isEmpty(searchQuery)) {
            ((BaseActivity) mActivity).setTitle(searchQuery + " ("
                    + productsPage.getTotalProducts() + ")");
            
            if(pageNumber == 1){
                TrackerDelegator.trackSearchViewSortMade(mActivity.getApplicationContext(), searchQuery,
                        productsPage.getTotalProducts(), sort.name());
                
                TrackerDelegator.trackSearchMade(mActivity.getApplicationContext(), searchQuery,
                        productsPage.getTotalProducts());
            }
            
        } else {
            if(pageNumber == 1){
                TrackerDelegator.trackCategoryView(mActivity.getApplicationContext(), title,
                    pageNumber);
            }
        }

        productsAdapter.appendProducts(productsPage.getProducts());

        Log.i(TAG, "code1 " + productsPage.getProducts().size() + " pageNumber is : " + pageNumber);

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

        AnalyticsGoogle.get().trackSearch(searchQuery, productsPage.getTotalProducts());
    }

    @Override
    public boolean removeAfterHandlingEvent() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getMD5Hash() {
        // TODO Auto-generated method stub
        return md5Hash;
    }

    /**
     * @return the isLandScape
     */
    public boolean isLandScape() {
        return isLandScape;
    }

    /**
     * @param isLandScape the isLandScape to set
     */
    public void setLandScape(boolean isLandScape) {
        this.isLandScape = isLandScape;
    }
}
