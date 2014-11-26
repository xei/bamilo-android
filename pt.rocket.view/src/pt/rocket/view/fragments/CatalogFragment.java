package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.CatalogPagerAdapter;
import pt.rocket.controllers.FeaturedItemsAdapter;
import pt.rocket.controllers.TipsPagerAdapter;
import pt.rocket.framework.Darwin;
import pt.rocket.components.androidslidingtabstrip.SlidingTabLayout;
import pt.rocket.framework.objects.CatalogFilter;
import pt.rocket.framework.objects.CatalogFilterOption;
import pt.rocket.framework.objects.FeaturedBox;
import pt.rocket.framework.objects.FeaturedItem;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.products.GetProductsHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TipsOnPageChangeListener;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogFilterFragment;
import pt.rocket.utils.dialogfragments.WizardPreferences;
import pt.rocket.utils.dialogfragments.WizardPreferences.WizardType;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

public class CatalogFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(CatalogFragment.class);

    public enum SortPages {        
        RATING,
        POPULARITY,
        NEW_IN,
        PRICE_UP,
        PRICE_DOWN,
        NAME,
        BRAND,
        DEFAULT
    }
    
    private static final String FILTER_VALUES_KEY = "filter_values";
    private static final String FILTER_STATE_KEY = "filter_state";
    private static final String TOTAL_KEY = "total_products";
    private static final String TITLE_KEY = "title";
    private static final String CURRENT_PAGE = "current_page";

    public static String requestTag = "CTLG_REQUEST";
    private static String PRODUCTS_LIST = "CTLG_PRODUCTS";

    private CatalogPagerAdapter mCatalogPagerAdapter;
    private ViewPager mViewPager;
    private ViewPager mFeaturedProductsViewPager;
    private ViewPager mFeaturedBrandsViewPager;

    // we save each page in a model
    private ArrayList<String> mSortOptions;

    private HashMap<String, Product> mProductsMap;
    private int mTotalProducts = 0;

    public static String productsURL;
    public static String searchQuery;
    public static String navigationPath;
    public static String title;
    public static int navigationSource;

    private View mProductsButtonsContainer;

    private View mFilterButton;

    private ImageView mSwitchLayoutButton;

    private ArrayList<CatalogFilter> mCatalogFilter;

    private ArrayList<CatalogFilter> mOldCatalogFilterState;

    private ContentValues mCatalogFilterValues;

    private boolean wasReceivedErrorEvent = false;

    private String[] mSavedOldCatalogData;

    private ContentValues mOldCatalogFilterValues;

    public static boolean isNotShowingDialogFilter = true;

    private boolean showList = true;
    private Drawable mShowListDrawable;
    private Drawable mShowGridDrawable;

    private int mSwitchMD5 = 0;

    private SharedPreferences sharedPreferences;

    private View mWizardContainer;

    private SlidingTabLayout mPagerTabStrip;

    private int mSavedPagerPosition = 1; // POPULARITY

    private SortPages currentPage = SortPages.DEFAULT;

    private static final String FEATURED_BOX = "FEATURED_BOX";

    private FeaturedBox mFeaturedBox;
    
    protected static String categoryId = "";
    
    protected static String categoryTree = "";
    
    protected static boolean hasFilterApllied = false;
    
    protected static Bundle filterParams;
    
    protected static String firstCatalogRequest = "";
    
    /**
     * Empty constructor
     */
    public CatalogFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.catalog_fragment_main,
                0,
                KeyboardState.NO_ADJUST_CONTENT);

        mProductsMap = new HashMap<String, Product>();
    }

    public static CatalogFragment getInstance(Bundle bundle) {
        Log.i(TAG, "getInstance");
        CatalogFragment catalogFragment = new CatalogFragment();
        
        // Clean static data
        categoryId = "";
        categoryTree = "";
        hasFilterApllied = false;
        filterParams = null;
        firstCatalogRequest = "";
        if(bundle != null && bundle.containsKey(ConstantsIntentExtra.CATEGORY_ID)){
            if(bundle.containsKey(ConstantsIntentExtra.CATEGORY_ID)){
                categoryId = bundle.getString(ConstantsIntentExtra.CATEGORY_ID);
            }
            if(bundle.containsKey(ConstantsIntentExtra.CATEGORY_TREE_NAME)){
                categoryTree = bundle.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
            }
        }
            
        return catalogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        title = "";
        if (null != savedInstanceState && savedInstanceState.containsKey(PRODUCTS_LIST)) {
            mTotalProducts = savedInstanceState.getInt(TOTAL_KEY);
            ArrayList<Product> products = savedInstanceState.getParcelableArrayList(PRODUCTS_LIST);
            for (Product prod : products) {
                mProductsMap.put(prod.getSKU(), prod);
            }
        }

        // Get saved state for filter
        if (savedInstanceState != null) {
            mCatalogFilter = savedInstanceState.getParcelableArrayList(FILTER_STATE_KEY);
            mCatalogFilterValues = savedInstanceState.getParcelable(FILTER_VALUES_KEY);
            title = savedInstanceState.getString(TITLE_KEY);
            if (null != mCatalogFilterValues) {
                Integer iMD5 = mCatalogFilterValues.getAsInteger("md5");
                if (null != iMD5) {
                    mFilterMD5 = iMD5;
                }
            }
            currentPage = (SortPages) savedInstanceState.getSerializable(CURRENT_PAGE);
        }

        // Get FeatureBox
        if (savedInstanceState != null && savedInstanceState.containsKey(FEATURED_BOX)) {
            mFeaturedBox = savedInstanceState.getParcelable(FEATURED_BOX);
        }

        mShowListDrawable = getResources().getDrawable(R.drawable.selector_catalog_listview);
        mShowGridDrawable = getResources().getDrawable(R.drawable.selector_catalog_gridview);

        sharedPreferences = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        showList = sharedPreferences.getBoolean(ConstantsSharedPrefs.KEY_SHOW_LIST_LAYOUT, true);

        Bundle arguments = getArguments();

        if (TextUtils.isEmpty(title)) {
            Log.d("FILTER","ONCREATE title:"+title);
            title = arguments.getString(ConstantsIntentExtra.CONTENT_TITLE);
        }

        productsURL = arguments.getString(ConstantsIntentExtra.CONTENT_URL);
        
        if(TextUtils.isEmpty(firstCatalogRequest)){
            firstCatalogRequest = productsURL;    
        }
        
        searchQuery = arguments.getString(ConstantsIntentExtra.SEARCH_QUERY);

        navigationSource = arguments.getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);

        navigationPath = arguments.getString(ConstantsIntentExtra.NAVIGATION_PATH);

        // only set currentPage if arguments exists and CATALOG_SORT_PAGE is defined
        if (arguments != null) {
            Object currentPageObject = arguments.getSerializable(ConstantsIntentExtra.CATALOG_SORT_PAGE);
            if (currentPageObject != null && currentPageObject instanceof SortPages) {
                currentPage = (SortPages) currentPageObject;
            }
        }

        // Save the current catalog data, used as a fall back
        saveCurrentCatalogDataForFilters();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mSortOptions = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.products_picker)));

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_products_list);
        mViewPager.setOnPageChangeListener(onPageChangeListener);

        mPagerTabStrip = (SlidingTabLayout) view.findViewById(R.id.catalog_pager_tag);
        mPagerTabStrip.setCustomTabView(R.layout.tab_simple_item, R.id.tab);

        // Get wizard container
        mWizardContainer = view.findViewById(R.id.tips_container);
        // Get buttons container
        mProductsButtonsContainer = view.findViewById(R.id.products_buttons_container);
        // Get switch layout button
        mSwitchLayoutButton = (ImageView) view.findViewById(R.id.products_switch_layout_button);
        mSwitchLayoutButton.setOnClickListener(this);
        // Set the switch layout button icon
        setSwitchLayoutButtonIcon();
        // Get filter button
        mFilterButton = view.findViewById(R.id.products_list_filter_button);
        // Set the button state if is selected or not
        setFilterButtonState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        Log.d(TAG, "ON RESUME");
        super.onResume();

        TrackerDelegator.trackPage(TrackingPage.PRODUCT_LIST, getLoadTime(), false);
        
        if (mTotalProducts > 0) {
            Log.d("FILTER"," ON RESUME 1 setTitle:"+title);
            getBaseActivity().setTitleAndSubTitle(title,
                    " (" + String.valueOf(mTotalProducts) + " " + getBaseActivity().getString(R.string.shoppingcart_items) + ")");
        } else {
            Log.d("FILTER"," ON RESUME 2 setTitle:"+title);
            getBaseActivity().setTitle(title);
        }

        Log.i(TAG, "DATA :  " + productsURL + " " + searchQuery + " " + navigationSource + " " + navigationPath);

        /*--
         * If restored from a rotation on "Undefined search terms", mFeaturedBox will be filled
         * Present "Undefined search terms" page without completing rest of onResume process
         */
        if (mFeaturedBox != null) {
            onErrorSearchResult(mFeaturedBox);

            return;
        }

        // Set catalog filters
        if (mCatalogFilter != null) {
            Log.i(TAG, "setFilterAction");
            enableFilterButton();
        }

        if (mCatalogPagerAdapter == null) {
            Log.d(TAG, "ON RESUME: ADAPTER IS NULL");
            Bundle params = new Bundle();
            Log.d("FILTER"," ON RESUME 3 setTitle:"+title);
            params.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
            params.putString(ConstantsIntentExtra.CONTENT_URL, productsURL);
            params.putString(ConstantsIntentExtra.SEARCH_QUERY, searchQuery);
            params.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
            params.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
            params.putParcelable(CatalogPageFragment.PARAM_FILTERS, mCatalogFilterValues);
            mCatalogPagerAdapter = new CatalogPagerAdapter(getChildFragmentManager(), mViewPager.getId(), mSortOptions, params,
                    BaseActivity.isTabletInLandscape(getBaseActivity()));
        } else {
            Log.d(TAG, "ON RESUME: ADAPTER IS NOT NULL");
            mCatalogPagerAdapter.setLandscapeMode(BaseActivity.isTabletInLandscape(getBaseActivity()));
            int totalProducts = mCatalogPagerAdapter.getCatalogPageTotalItems(mViewPager.getCurrentItem());
            if (totalProducts > 0 && getView() != null) {
                TextView totalItems = (TextView) getView().findViewById(R.id.totalProducts);
                StringBuilder total = new StringBuilder(" (").append(totalProducts).append(" ").append(getString(R.string.shoppingcart_items)).append(")");
                if (null != totalItems) {
                    totalItems.setText(total.toString());
                    totalItems.setVisibility(View.VISIBLE);
                }
            }
            mCatalogPagerAdapter.invalidateCatalogPages();
        }

        RocketImageLoader.getInstance().startProcessingQueue();
        mViewPager.setAdapter(mCatalogPagerAdapter);
        mPagerTabStrip.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(1);
        if (null != currentPage && currentPage != SortPages.DEFAULT) {
            mViewPager.setCurrentItem(currentPage.ordinal(), false);
            currentPage = SortPages.DEFAULT;
        } else {
            mViewPager.setCurrentItem(mSavedPagerPosition, false);
        }
        if (null != mCatalogPagerAdapter && null != mCatalogFilterValues) {
            mCatalogPagerAdapter.restoreFilters(mCatalogFilterValues);
        }
        // Show tips
        isToShowWizard();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        if (mViewPager != null)
            mSavedPagerPosition = mViewPager.getCurrentItem();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");

        // Reset static values
        mCatalogFilter = null;
        mOldCatalogFilterState = null;
        mCatalogPagerAdapter = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(FILTER_STATE_KEY, mCatalogFilter);
        outState.putParcelable(FILTER_VALUES_KEY, mCatalogFilterValues);
        outState.putParcelableArrayList(PRODUCTS_LIST, new ArrayList<Product>(mProductsMap.values()));
        outState.putInt(TOTAL_KEY, mTotalProducts);
        Log.d("FILTER"," onSaveInstanceState 4 setTitle:"+title);
        outState.putString(TITLE_KEY, title);

        // save current page to be restored after a rotation
        if (mViewPager != null) {
            int currentPage = mViewPager.getCurrentItem();
            if (currentPage >= 0) {
                outState.putSerializable(CURRENT_PAGE, SortPages.values()[currentPage]);
            }
        }

        // Persist featureBox if exists
        if (mFeaturedBox != null) {
            outState.putParcelable(FEATURED_BOX, mFeaturedBox);
        }
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        Product product = mProductsMap.get(values);
        if (null != product) {
            if (BaseFragment.FRAGMENT_VALUE_SET_FAVORITE == identifier) {
                product.getAttributes().setFavourite(true);
            } else if (BaseFragment.FRAGMENT_VALUE_REMOVE_FAVORITE == identifier) {
                product.getAttributes().setFavourite(false);
            }
            if (null != mCatalogPagerAdapter) {
                mCatalogPagerAdapter.invalidateCatalogPages();
            }

        }
    }

    /*
     * ######## CATALOG FILTER ########
     */
    /**
     * Method used to set the filter button.
     * 
     * @param filters
     * @author sergiopereira
     */
    public void onSuccesLoadingFilteredCatalog(ArrayList<CatalogFilter> filters) {
        // Validate the view
        if (mFilterButton == null) {
            Log.w(TAG, "FILTER VIEW IS NULL");
            return;
        }
        // Validate the current filter object
        if (mCatalogFilter != null) {
            Log.w(TAG, "DISCARTED: CURRENT FILTER IS NOT NULL");
            return;
        }
        // Validate the received data
        if (filters == null) {
            Log.w(TAG, "HIDE FILTERS: DATA IS NULL");
            return;
        }
        // Validate the received data
        if (filters.size() == 0) {
            Log.w(TAG, "HIDE FILTERS: DATA IS EMPTY");
            return;
        }

        Log.d(TAG, " ######## FILTER EXISTING  -> " + mCatalogFilterValues);

        // Save filters
        mCatalogFilter = filters;
        // Restore the old state
        matchFilterStateWithOldState();
        // Save the current catalog data
        saveCurrentCatalogDataForFilters();
        // Set the button behavior
        enableFilterButton();
        Log.i(TAG, "SAVED THE FILTER");
    }

    /**
     * Method used to process the error event Show the old filter
     * 
     * @author sergiopereira
     */
    public synchronized void onErrorLoadingFilteredCatalog() {
        // Process only one error event
        if (wasReceivedErrorEvent) {
            Log.w(TAG, "DISCARTED OTHER ERROR EVENT");
            return;
        }
        // Set the flag
        wasReceivedErrorEvent = true;
        // Restore the filter values for request
        if (mOldCatalogFilterValues != null)
            mCatalogFilterValues = mOldCatalogFilterValues;
        // Show the old filter
        if (mOldCatalogFilterState != null)
            mCatalogFilter = mOldCatalogFilterState;
        // Set listener
        mFilterButton.setOnClickListener(this);
        Log.d(TAG, "RECEIVED ERROR ON LOAD CATALOG WITH FILTERS");
    }

    /**
     * Show suggestion page when no results are found
     * 
     * @param featuredBox
     *            contains a list of featured products, a list of featured
     *            brands and error messages
     */
    public synchronized void onErrorSearchResult(FeaturedBox featuredBox) {
        // Get current view
        View view = getView();
        // Validate
        if (featuredBox != null && view != null) {
            // Persist featureBox for future rotations
            mFeaturedBox = featuredBox;
            // hide default products list
            view.findViewById(R.id.catalog_viewpager_container).setVisibility(View.GONE);
            view.findViewById(R.id.no_results_search_terms).setVisibility(View.VISIBLE);

            String errorMessage = featuredBox.getErrorMessage();
            // only process errorMessage if is available
            if (!TextUtils.isEmpty(errorMessage)) {
                TextView textViewErrorMessage = (TextView) view.findViewById(R.id.no_results_search_error_message);

                // set seachQuery in bold if it is converted
                SpannableStringBuilder spannableErrorMessage = getSpannableErrorMessageWithOriginalSearchQuery(errorMessage, searchQuery);
                if (spannableErrorMessage != null) {
                    textViewErrorMessage.setText(spannableErrorMessage);
                } else {
                    textViewErrorMessage.setText(errorMessage);
                }
            }

            String searchTips = featuredBox.getSearchTips();
            // only process searchTips if is available
            if (!TextUtils.isEmpty(searchTips)) {
                TextView textViewSearchTips = (TextView) view.findViewById(R.id.no_results_search_tips_text);

                // set searchTips in bold if is converted
                SpannableStringBuilder spannableSearchTips = getSpannableSearchTips(searchTips);
                if (spannableSearchTips != null) {
                    textViewSearchTips.setText(spannableSearchTips);
                } else {
                    textViewSearchTips.setText(errorMessage);
                }
            }

            // define how many items will be displayed on the viewPager
            int partialSize = 3;
            if (BaseActivity.isTabletInLandscape(getActivity())) {
                partialSize = 5;
            }

            String productsTitle = featuredBox.getProductsTitle();
            if (!TextUtils.isEmpty(productsTitle)) {
                ((TextView) view.findViewById(R.id.featured_products_title)).setText(productsTitle);
            }

            view.findViewById(R.id.featured_products).setVisibility(View.VISIBLE);
            generateFeaturedProductsLayout(featuredBox.getProducts(), partialSize);

            String brandsTitle = featuredBox.getBrandsTitle();
            if (!TextUtils.isEmpty(brandsTitle)) {
                ((TextView) view.findViewById(R.id.featured_brands_title)).setText(brandsTitle);
            }

            view.findViewById(R.id.featured_brands).setVisibility(View.VISIBLE);
            generateFeaturedBrandsLayout(featuredBox.getBrands(), partialSize);

            String noticeMessage = featuredBox.getNoticeMessage();
            if (!TextUtils.isEmpty(noticeMessage)) {
                ((TextView) view.findViewById(R.id.no_results_search_notice_message)).setText(noticeMessage);
            }
        } else {
            Log.e(TAG, "No featureBox!");
        }
    }

    /**
     * get spannableErrorMessage with <code>searchQuery</code> (text between
     * " ") replaced by original <code>searchQuery</code> and set in bold
     * 
     * @param errorMessage
     * @param searchQuery
     * @return <code>spannableErrorMessage</code> with original searchQuery set
     *         in bold, or <code>null</code> if processing wasn't successful
     */
    private SpannableStringBuilder getSpannableErrorMessageWithOriginalSearchQuery(String errorMessage, String searchQuery) {
        String startDelimiter = "\"";
        String endDelimiter = "\"";
        int startIndex = errorMessage.indexOf(startDelimiter);

        // some countries use others delimiters. try alternative
        if (startIndex == -1) {
            startDelimiter = "« ";
            endDelimiter = " »";
            startIndex = errorMessage.indexOf(startDelimiter);
        }
        // if finds delimiter process errorMessage
        if (startIndex > 0) {
            int startDelimiterLength = startDelimiter.length();
            int endDelimiterLength = endDelimiter.length();
            int lastEndIndex = 0;

            int endIndex = errorMessage.indexOf(endDelimiter, startIndex + startDelimiterLength);
            // get last index of delimitier
            while (endIndex > 0) {
                lastEndIndex = endIndex;
                endIndex = errorMessage.indexOf(endDelimiter, lastEndIndex + endDelimiterLength);
            }
            if (lastEndIndex > 0) {
                // Validate search value
                if (TextUtils.isEmpty(searchQuery))
                    searchQuery = "";

                // create SpannableStringBuilder with searchQuery replaced by
                // original
                SpannableStringBuilder spannableErrorMessage = new SpannableStringBuilder();
                spannableErrorMessage.append(errorMessage.substring(0, startIndex + startDelimiterLength));
                spannableErrorMessage.append(searchQuery);
                spannableErrorMessage.append(errorMessage.substring(lastEndIndex, errorMessage.length()));

                // set searchQuery on bold
                String newSearchQuery = startDelimiter + searchQuery + endDelimiter;
                startIndex = spannableErrorMessage.toString().indexOf(newSearchQuery);
                spannableErrorMessage.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIndex + startDelimiterLength, startIndex
                        + searchQuery.length() + endDelimiterLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                return spannableErrorMessage;
            }
        }
        return null;
    }

    /**
     * get spannableSearchtips with first line set in bold
     * 
     * @param searchTips
     * @return <code>spannableSearchtips</code> with first line set in bold, or
     *         <code>null</code> if processing wasn't successful
     */
    private SpannableStringBuilder getSpannableSearchTips(String searchTips) {
        // set first line bold, if searchTips has multiple lines
        if (searchTips.contains("\n")) {
            int endIndex = searchTips.indexOf("\n");
            if (endIndex > 0) {
                // check if Country is Nigeria
                SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                if (sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, "en").contains("en_NG")) {
                    // get next 2 paragraphs
                    int newEndIndex = searchTips.indexOf("\n", endIndex + 1);
                    if (newEndIndex > 0) {
                        newEndIndex = searchTips.indexOf("\n", newEndIndex + 1);
                        if (newEndIndex > 0) {
                            endIndex = newEndIndex;
                        }
                    }
                }
                SpannableStringBuilder spannableSearchTips = new SpannableStringBuilder(searchTips);
                spannableSearchTips.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                return spannableSearchTips;
            }
        }
        return null;
    }

    /**
     * Fill adapter with featured products
     * 
     * @param featuredProducts
     */
    private void generateFeaturedProductsLayout(ArrayList<FeaturedItem> featuredProducts, int partialSize) {
        View mLoadingFeaturedProducts = getView().findViewById(R.id.loading_featured_products);
        mFeaturedProductsViewPager = (ViewPager) getView().findViewById(R.id.featured_products_viewpager);
        // try to use portrait layout if there are less products than what the
        // default layout would
        // present
        if (featuredProducts.size() < partialSize) {
            partialSize = 3;
        }
        FeaturedItemsAdapter mFeaturedProductsAdapter = new FeaturedItemsAdapter(getBaseActivity(), featuredProducts, LayoutInflater.from(getActivity()),
                partialSize);
        mFeaturedProductsViewPager.setAdapter(mFeaturedProductsAdapter);
        mFeaturedProductsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedProducts.setVisibility(View.GONE);
    }

    /**
     * Fill adapter with featured brands
     * 
     * @param featuredBrandsList
     */
    private void generateFeaturedBrandsLayout(ArrayList<FeaturedItem> featuredBrandsList, int partialSize) {
        View mLoadingFeaturedBrands = getView().findViewById(R.id.loading_featured_brands);
        mFeaturedBrandsViewPager = (ViewPager) getView().findViewById(R.id.featured_brands_viewpager);
        // try to use portrait layout if there are less brands than what the
        // default layout would
        // present
        if (featuredBrandsList.size() < partialSize) {
            partialSize = 3;
        }
        FeaturedItemsAdapter mFeaturedBrandsAdapter = new FeaturedItemsAdapter(getBaseActivity(), featuredBrandsList, LayoutInflater.from(getActivity()),
                partialSize);
        mFeaturedBrandsViewPager.setAdapter(mFeaturedBrandsAdapter);
        mFeaturedBrandsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedBrands.setVisibility(View.GONE);
    }

    private void showButtonsContainer() {
        mProductsButtonsContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Show tips if is the first time the user uses the app.
     */
    private void isToShowWizard() {
        ViewPager viewPagerTips = (ViewPager) mWizardContainer.findViewById(R.id.viewpager_tips);
        Log.d(TAG, " --- TEST isToShowWizard -> " + WizardPreferences.isFirstTime(getBaseActivity(), WizardType.CATALOG));
        if (WizardPreferences.isFirstTime(getBaseActivity(), WizardType.CATALOG)) {

            viewPagerTips.setVisibility(View.VISIBLE);
            viewPagerTips.bringToFront();

            int[] tipsPages = { R.layout.products_tip_swipe_layout, R.layout.products_tip_favourite_layout };
            TipsPagerAdapter mTipsPagerAdapter = new TipsPagerAdapter(getBaseActivity(), getBaseActivity().getLayoutInflater(), mWizardContainer, tipsPages);
            viewPagerTips.setAdapter(mTipsPagerAdapter);
            viewPagerTips.setOnPageChangeListener(new TipsOnPageChangeListener(mWizardContainer, tipsPages));
            viewPagerTips.setCurrentItem(0);
            ((LinearLayout) mWizardContainer.findViewById(R.id.viewpager_tips_btn_indicator)).setVisibility(View.VISIBLE);
            ((LinearLayout) mWizardContainer.findViewById(R.id.viewpager_tips_btn_indicator)).setOnClickListener(this);
            Log.d(TAG, " --- TEST isToShowWizard -> Finished");
        } else {
            viewPagerTips.setVisibility(View.GONE);
        }
    }

    private int mFilterMD5 = 0;

    /**
     * Process the filter values
     * 
     * @param filterValues
     * @author sergiopereira
     */
    public void onSubmitFilterValues(ContentValues filterValues) {
        // Tracking
        trackingCatalogFilters(filterValues);       
        // Save the old data to restore in case of error event
        mOldCatalogFilterValues = mCatalogFilterValues;
        // Save the current filter values
        mCatalogFilterValues = filterValues;
        // Contains the new product URL (Category filter)
        if (filterValues.containsKey(GetProductsHelper.PRODUCT_URL)) {
            // Get product URL and remove it
            productsURL = filterValues.getAsString(GetProductsHelper.PRODUCT_URL);
            mCatalogFilterValues.put(GetProductsHelper.PRODUCT_URL, "");
            // Save the new filters to restore
            mOldCatalogFilterState = mCatalogFilter;
            // Clean the current category values
            mCatalogFilter = null;
            searchQuery = null;
            title = null;
            mFilterButton.setOnClickListener(null);
        }
        // Send the last saved catalog data that works
        else if (mSavedOldCatalogData != null) {
//            productsURL = mSavedOldCatalogData[0];
            productsURL = firstCatalogRequest;
            searchQuery = mSavedOldCatalogData[1];
            navigationPath = mSavedOldCatalogData[2];
//            title = mSavedOldCatalogData[3];
        }

        // Contains the new search query (Brand filter)
        if (filterValues.containsKey(GetProductsHelper.SEARCH_QUERY)) {
            searchQuery = filterValues.getAsString(GetProductsHelper.SEARCH_QUERY);
            // Used to indicate that has filter q=<BRAND>
            mCatalogFilterValues.put(GetProductsHelper.SEARCH_QUERY, "");
        }

        Log.i(TAG, "Updating totalUpdates: " + mSwitchMD5);
        mFilterMD5++;
        mCatalogFilterValues.put("md5", mFilterMD5);

        // Set the filter button selected or not
        setFilterButtonState(); // size
        // Error flag
        wasReceivedErrorEvent = false;

        mProductsMap = new HashMap<String, Product>();

        Bundle params = new Bundle();
        params.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
        params.putString(ConstantsIntentExtra.CONTENT_URL, productsURL);
        params.putString(ConstantsIntentExtra.SEARCH_QUERY, searchQuery);
        params.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
        params.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
        params.putParcelable(CatalogPageFragment.PARAM_FILTERS, mCatalogFilterValues);

        hasFilterApllied = true;
        
        filterParams = params;
        Log.d(TAG, " ----> FILTER TITLE :" + title);
     
        mCatalogPagerAdapter.updateParametersBundle(params);
        mCatalogPagerAdapter.resetClearProduct();       
        mCatalogPagerAdapter.notifyDataSetChanged();

    }
    
    /**
     * Tracking catalog filter values
     * @param filterValues
     */
    private void trackingCatalogFilters(ContentValues filterValues) {
        // Copy the content values for tracking
        TrackerDelegator.trackCatalogFilter(new ContentValues(filterValues));
    }

    /**
     * Save the current data to create a fall back point in case some request
     * filtered return error
     * 
     * @author sergiopereira
     */
    private void saveCurrentCatalogDataForFilters() {
        mSavedOldCatalogData = new String[4];
        mSavedOldCatalogData[0] = productsURL;
        mSavedOldCatalogData[1] = searchQuery;
        mSavedOldCatalogData[2] = navigationPath;
        mSavedOldCatalogData[3] = title;
    }

    /**
     * Method used to restore the old filter state when is performed a query
     * filtered by a new category. Match the old selection with the new filter
     * option.
     * 
     * @author sergiopereira
     */
    private void matchFilterStateWithOldState() {
        Log.i(TAG, "RESTORE THE OLD SELECTED STATE");
        CatalogFilter oldFilter;
        // Validate the old filter state
        if (mOldCatalogFilterState != null)
            // Restore the filter if match with the old
            for (CatalogFilter newFilter : mCatalogFilter) {
                Log.i(TAG, "RESTORE FILTER: " + newFilter.getName());
                // Locate the old filter
                oldFilter = locateFilter(mOldCatalogFilterState, newFilter.getId());
                // Validate old filter
                if (oldFilter != null) {
                    // Case generic filter
                    if (oldFilter.hasOptionSelected())
                        // Locate selected options and save
                        newFilter.setSelectedOption(locateOption(newFilter.getFilterOptions(), oldFilter.getSelectedOption()));
                    // Case price filter
                    else if (oldFilter.hasRangeValues()) {
                        // Save the range
                        newFilter.setRangeValues(oldFilter.getMinRangeValue(), oldFilter.getMaxRangeValue());
                    }
                }
            }
        else
            Log.i(TAG, "OLD SELECTED STATE IS NULL");
    }

    /**
     * Locate the current options in the old selected options and save the old
     * value.
     * 
     * @param newOptions
     * @param oldOptions
     * @return The match of the selected options
     * @author sergiopereira
     */
    private SparseArray<CatalogFilterOption> locateOption(ArrayList<CatalogFilterOption> newOptions, SparseArray<CatalogFilterOption> oldOptions) {
        // Array to save the selected options
        SparseArray<CatalogFilterOption> selectedOptions = new SparseArray<CatalogFilterOption>();
        // Loop filter options
        for (int i = 0; i < newOptions.size(); i++) {
            CatalogFilterOption curOption = newOptions.get(i);
            // Loop old selected options
            for (int j = 0; j < oldOptions.size(); j++) {
                CatalogFilterOption selectedOption = oldOptions.valueAt(j);
                // If has the same value
                if (curOption.getLabel().equals(selectedOption.getLabel())) {
                    // Set option as selected and save it
                    curOption.setSelected(true);
                    selectedOptions.put(i, curOption);
                    break;
                }
            }
        }
        return selectedOptions;
    }

    /**
     * Locate the current filter.
     * 
     * @param array
     * @param id
     * @return The old filter with saved state
     * @author sergiopereira
     */
    private CatalogFilter locateFilter(ArrayList<CatalogFilter> array, String id) {
        for (CatalogFilter item : array)
            if (item.getId().equals(id))
                return item;
        return null;
    }

    /**
     * Set the behavior for filter button
     * 
     * @author sergiopereira
     */
    private void enableFilterButton() {
        // Show buttons container
        showButtonsContainer();
        // Set listener
        mFilterButton.setVisibility(View.VISIBLE);
        mFilterButton.setOnClickListener(null);
        mFilterButton.setOnClickListener(this);
    }
    
    /**
     * Set the behavior for filter button
     * 
     * @author sergiopereira
     */    
    public void disableFilterButton() {
        if (mFilterButton != null) mFilterButton.setOnClickListener(null);
    }

    /**
     * Set the filter button state, to show as selected or not
     * 
     * @author sergiopereira
     */
    private void setFilterButtonState() {
        try {
            // Contains md5
            mFilterButton.setSelected((mCatalogFilterValues.size() == 1) ? false : true);
            Log.d(TAG, "SET FILTER BUTTON STATE: " + mFilterButton.isSelected());
        } catch (NullPointerException e) {
            Log.w(TAG, "BUTTON OR VALUE IS NULL", e);
        }
    }

    /**
     * Set the switch layout button icon (show the opposite icon)
     */
    private void setSwitchLayoutButtonIcon() {
        try {
            if (showList) {
                mSwitchLayoutButton.setImageDrawable(mShowGridDrawable);
            } else {
                mSwitchLayoutButton.setImageDrawable(mShowListDrawable);
            }
            Log.i(TAG, "SET SWITCH LAYOUT BUTTON STATE: " + mSwitchLayoutButton.isSelected());
        } catch (NullPointerException e) {
            Log.w(TAG, "BUTTON OR VALUE IS NULL", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(final View v) {
        // Get the view id
        int id = v.getId();
        // Validate the click
        if (id == R.id.products_list_filter_button && isNotShowingDialogFilter) {           
            Log.d(TAG, "ON CLICK: FILTER BUTTON");
            isNotShowingDialogFilter = false;
            // Validate current catalog filter
            if(mCatalogFilter == null) return;
            // TODO: Validate if is necessary Filter as static
            try {
                // Show dialog
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(DialogFilterFragment.FILTER_TAG, mCatalogFilter);
                DialogFilterFragment newFragment = DialogFilterFragment.newInstance(bundle, this);
                newFragment.show(getBaseActivity().getSupportFragmentManager(), "dialog");
            } catch (NullPointerException e) {
                Log.w(TAG, "WARNING: NPE ON SHOW DIALOG FRAGMENT");
            }
            
        } else if (id == R.id.viewpager_tips_btn_indicator) {
            WizardPreferences.changeState(getBaseActivity(), WizardType.CATALOG);
            mWizardContainer.findViewById(R.id.viewpager_tips).setVisibility(View.GONE);
            ((LinearLayout) mWizardContainer.findViewById(R.id.viewpager_tips_btn_indicator)).setVisibility(View.GONE);

        } else if (id == R.id.products_switch_layout_button) {
            Log.d(TAG, "ON CLICK: SWITCH LAYOUT BUTTON");
            v.setEnabled(false);

            // getBaseActivity().showProgress();
            
            showList = !showList;
            
            // Track
            TrackerDelegator.trackCatalogSwitchLayout((showList) ? "list" : "grid");

            // Save current layout used
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_LIST_LAYOUT, showList);
            editor.commit();

            setSwitchLayoutButtonIcon();

            // TODO: Valdiate this
            // update totalUpdates
            mSwitchMD5++;
            Log.i(TAG, "Updating totalUpdates: " + mSwitchMD5);
  
            // Redraw layout
            mCatalogPagerAdapter.invalidateCatalogPages();
            
            v.setEnabled(true);
        }
    }

    public boolean getShowList() {
        return showList;
    }

    public int getSwitchMD5() {
        return mSwitchMD5;
    }

    public String getSortTitle(int index) {
        return index < mSortOptions.size() ? mSortOptions.get(index) : "";
    }

    public Product getProduct(String sku) {
        return mProductsMap.get(sku);
    }

    public void setCatalogTitle(String title){
        getBaseActivity().setTitle(title);
    }
    
    public void addProductsCollection(Map<String, Product> products, String categoryTitle, int totalProductsCount) {
        title = categoryTitle;
        mTotalProducts = totalProductsCount;
        mProductsMap.putAll(products);

        if (mTotalProducts > 0)
            getBaseActivity().setTitleAndSubTitle(title,
                    " (" + String.valueOf(mTotalProducts) + " " + getBaseActivity().getString(R.string.shoppingcart_items) + ")");
        else
            getBaseActivity().setTitle(title);

        Bundle args = getArguments();
        if (null != args) {
            args.putString(ConstantsIntentExtra.CONTENT_TITLE, title);
        }

    }

    public void invalidatePages() {
        CatalogPagerAdapter adapter = (CatalogPagerAdapter) mViewPager.getAdapter();
        if (null != adapter) {
            adapter.invalidateCatalogPages();
            adapter.notifyDataSetChanged();
        }
    }

    public ArrayList<CatalogFilter> getCatalogFilter() {
        return mCatalogFilter;
    }

    // ---------------------------------------------------------------
    // ----- Listeners
    // ---------------------------------------------------------------

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            Log.d("FILTER","onPageSelected position:"+position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // ...
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                RocketImageLoader.getInstance().startProcessingQueue();
            } else {
                RocketImageLoader.getInstance().stopProcessingQueue();
            }

        }
    };
}
