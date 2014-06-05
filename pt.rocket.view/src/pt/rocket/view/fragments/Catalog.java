package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import org.holoeverywhere.widget.Button;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.CatalogPageModel;
import pt.rocket.controllers.FeaturedItemsAdapter;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.objects.CatalogFilter;
import pt.rocket.framework.objects.CatalogFilterOption;
import pt.rocket.framework.objects.FeaturedBox;
import pt.rocket.framework.objects.FeaturedItem;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetProductsHelper;
import pt.rocket.utils.JumiaCatalogViewPager;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogFilterFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

public class Catalog extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(Catalog.class);

    private static Catalog mCatalogFragment;
    private CatalogPagerAdaper mCatalogPagerAdapter;
    private JumiaCatalogViewPager mViewPager;
    private PagerTabStrip pagerTabStrip;

    private ViewPager mFeaturedProductsViewPager;
    private ViewPager mFeaturedBrandsViewPager;

    private final int TAB_PREV_ID = 0;
    private final int TAB_CURR_ID = 1;
    private final int TAB_NEXT_ID = 2;

    private final int TAB_INDICATOR_HEIGHT = 0;
    private final int TAB_UNDERLINE_HEIGHT = 1;
    private final int TAB_STRIP_COLOR = android.R.color.transparent;

    private static final int PAGE_MIDDLE = 1;

    private LayoutInflater mInflater;
    private int mSelectedPageIndex = 1;
    private int mLastSelectedPageIndex = 1;
    // we save each page in a model
    private ArrayList<String> mSortOptions;
    private CatalogPageModel[] mCatalogPageModel;

    public static String productsURL;
    public static String searchQuery;
    public static String navigationPath;
    public static String title;
    public static int navigationSource;
    private int currentPosition = 1;

    private View mProductsButtonsContainer;

    private View mFilterButton;

    private ImageView mSwitchLayoutButton;

    private ArrayList<CatalogFilter> mCatalogFilter;

    private ArrayList<CatalogFilter> mOldCatalogFilterState;

    private ContentValues mCatalogFilterValues;

    private boolean wasReceivedErrorEvent = false;

    private String[] mSavedOldCatalogData;

    private ContentValues mOldCatalogFilterValues;

    public static boolean isNotShowing = true;

    private boolean showList = true;
    private Drawable mShowListDrawable;
    private Drawable mShowGridDrawable;

    private int totalUpdates = 0;
    
    private SharedPreferences sharedPreferences;

    public Catalog() {
        super(EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW),
                NavigationAction.Products,
                R.string.products, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public static Catalog getInstance() {
        Log.i(TAG, "getInstance");
        // if (mProductsViewFragment == null) {
        mCatalogFragment = new Catalog();
        // }
        return mCatalogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortOptions = new ArrayList<String>(Arrays.asList(getResources().getStringArray(
                R.array.products_picker)));
        mCatalogPageModel = new CatalogPageModel[mSortOptions.size()];

        mShowListDrawable = getResources().getDrawable(R.drawable.selector_catalog_listview);
        mShowGridDrawable = getResources().getDrawable(R.drawable.selector_catalog_gridview);

        sharedPreferences = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        showList = sharedPreferences.getBoolean(ConstantsSharedPrefs.KEY_SHOW_LIST_LAYOUT, true);

        Log.i(TAG, "onCreate");
        setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mInflater = inflater;
        View view = inflater.inflate(R.layout.products_frame, container, false);
        mViewPager = (JumiaCatalogViewPager) view.findViewById(R.id.viewpager_products_list);
        pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.products_list_titles);
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
        // Initialize the catalog model with fresh data
        initPageModel();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "FILTER ON RESUME");

        // http://www.jumia.co.ke:80/mobapi/womens-casual-shoes/

        title = getArguments().getString(ConstantsIntentExtra.CONTENT_TITLE);

        ((BaseActivity) getActivity()).setTitle(title);

        productsURL = getArguments().getString(ConstantsIntentExtra.CONTENT_URL);

        searchQuery = getArguments().getString(ConstantsIntentExtra.SEARCH_QUERY);

        navigationSource = getArguments().getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);

        navigationPath = getArguments().getString(ConstantsIntentExtra.NAVIGATION_PATH);

        // Save the current catalog data, used as a fall back
        saveCurrentCatalogDataForFilters();

        Log.i(TAG, "ON RESUME");
        AnalyticsGoogle.get().trackPage(R.string.gproductlist);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {

                    if (mSelectedPageIndex < 1) {
                        Log.i(TAG, "getCurrentCatalogPageModel lower :  mSelectedPageIndex is : "
                                + mSelectedPageIndex + " mLastSelectedPageIndex id : "
                                + mLastSelectedPageIndex);
                        // moving each page content one page to the right
                        updateCatalogPageModelIdexes(1);

                    } else if (mSelectedPageIndex > 1) {
                        Log.i(TAG, "getCurrentCatalogPageModel higher :  mSelectedPageIndex is : "
                                + mSelectedPageIndex + " mLastSelectedPageIndex id : "
                                + mLastSelectedPageIndex);
                        updateCatalogPageModelIdexes(-1);
                    }

                } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {

                }
            }

        });

        if (mCatalogPagerAdapter == null) {
            Log.d(TAG, "FILTER: ADAPTER IS NULL");
            mCatalogPagerAdapter = new CatalogPagerAdaper();

        } else {
            Log.d(TAG, "FILTER: ADAPTER IS NOT NULL");
            mCatalogPageModel[0].setTotalItemLable();
            mCatalogPageModel[0].notifyContentDataSetChanged();
        }

        mViewPager.setAdapter(mCatalogPagerAdapter);
        mViewPager.setCurrentItem(1);
        try {
            setLayoutSpec();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // Set catalog filters
        if (mCatalogFilter != null) {
            Log.i(TAG, "setFilterAction");
            setFilterAction();
        }

        AnalyticsGoogle.get().trackPage(R.string.gproductlist);
        getBaseActivity().setProcessShow(true);
        getBaseActivity().showContentContainer();

    }
    
    @Override
    public void onPause() {     
        super.onPause();
        Log.i(TAG, "onPause");
    }
    
    @Override
    public void onDestroy() {     
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    /*
     * ######## CATALOG FILTER ######## TODO : Add here more filter methods
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
        // Save filters
        mCatalogFilter = filters;
        // Restore the old state
        matchFilterStateWithOldState();
        // Save the current catalog data
        saveCurrentCatalogDataForFilters();
        // Set the button behavior
        setFilterAction();
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
     * @param featuredBox contains a list of featured products, a list of featured brands and error messages
     */
    public synchronized void onErrorSearchResult(FeaturedBox featuredBox) {
        // Get current view
        View view = getView();
        // Validate 
        if (featuredBox != null && view != null) {
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
                TextView textViewSearchTips= (TextView) view.findViewById(R.id.no_results_search_tips_text);

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
     * get spannableErrorMessage with <code>searchQuery</code> (text between " ") replaced by
     * original <code>searchQuery</code> and set in bold
     * 
     * @param errorMessage
     * @param searchQuery
     * @return <code>spannableErrorMessage</code> with original searchQuery set in bold, or
     *         <code>null</code> if processing wasn't successful
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
                if(TextUtils.isEmpty(searchQuery)) searchQuery = "";
                
                // create SpannableStringBuilder with searchQuery replaced by original
                SpannableStringBuilder spannableErrorMessage = new SpannableStringBuilder();
                spannableErrorMessage.append(errorMessage.substring(0, startIndex + startDelimiterLength));
                spannableErrorMessage.append(searchQuery);
                spannableErrorMessage.append(errorMessage.substring(lastEndIndex, errorMessage.length()));

                // set searchQuery on bold
                String newSearchQuery = startDelimiter + searchQuery + endDelimiter;
                startIndex = spannableErrorMessage.toString().indexOf(newSearchQuery);
                spannableErrorMessage.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIndex + startDelimiterLength, startIndex + searchQuery.length() + endDelimiterLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                return spannableErrorMessage;
            }
        }
        return null;
    }

    /**
     * get spannableSearchtips with first line set in bold
     * 
     * @param searchTips
     * @return <code>spannableSearchtips</code> with first line set in bold, or <code>null</code> if
     *         processing wasn't successful
     */
    private SpannableStringBuilder getSpannableSearchTips(String searchTips) { 
        // set first line bold, if searchTips has multiple lines
        if (searchTips.contains("\n")) {
            int endIndex = searchTips.indexOf("\n");
            if (endIndex > 0) {
                // check if Country is Nigeria
                SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                if(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, "en").contains("en_NG")){
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
    private void generateFeaturedProductsLayout(ArrayList<FeaturedItem> featuredProducts, int partialSize){
        View mLoadingFeaturedProducts = getView().findViewById(R.id.loading_featured_products);
        mFeaturedProductsViewPager = (ViewPager) getView().findViewById(R.id.featured_products_viewpager);
        // try to use portrait layout if there are less products than what the default layout would present
        if (featuredProducts.size() < partialSize) {
            partialSize = 3;
        }
        FeaturedItemsAdapter mFeaturedProductsAdapter = new FeaturedItemsAdapter(getBaseActivity(), featuredProducts, LayoutInflater.from(getActivity()), partialSize);
        mFeaturedProductsViewPager.setAdapter(mFeaturedProductsAdapter);
        mFeaturedProductsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedProducts.setVisibility(View.GONE);
    }

    /**
     * Fill adapter with featured brands
     * 
     * @param featuredBrandsList
     */
    private void generateFeaturedBrandsLayout(ArrayList<FeaturedItem> featuredBrandsList, int partialSize){
        View mLoadingFeaturedBrands = getView().findViewById(R.id.loading_featured_brands);
        mFeaturedBrandsViewPager = (ViewPager) getView().findViewById(R.id.featured_brands_viewpager);
        // try to use portrait layout if there are less brands than what the default layout would present
        if (featuredBrandsList.size() < partialSize) {
            partialSize = 3;
        }
        FeaturedItemsAdapter mFeaturedBrandsAdapter = new FeaturedItemsAdapter(getBaseActivity(), featuredBrandsList, LayoutInflater.from(getActivity()), partialSize);
        mFeaturedBrandsViewPager.setAdapter(mFeaturedBrandsAdapter);
        mFeaturedBrandsViewPager.setVisibility(View.VISIBLE);
        mLoadingFeaturedBrands.setVisibility(View.GONE);
    }

    private void showButtonsContainer() {
        mProductsButtonsContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Process the filter values
     * 
     * @param filterValues
     * @author sergiopereira
     */
    public void onSubmitFilterValues(ContentValues filterValues) {
        Log.d(TAG, "FILTER VALUES: " + filterValues.toString());
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
            productsURL = mSavedOldCatalogData[0];
            searchQuery = mSavedOldCatalogData[1];
            navigationPath = mSavedOldCatalogData[2];
            title = mSavedOldCatalogData[3];
        }
        
        // Contains the new search query (Brand filter)
        if (filterValues.containsKey(GetProductsHelper.SEARCH_QUERY)){
            searchQuery = filterValues.getAsString(GetProductsHelper.SEARCH_QUERY);
            mCatalogFilterValues.put(GetProductsHelper.SEARCH_QUERY, "");
        }
        
        // Set the filter button selected or not
        setFilterButtonState();
        // Error flag
        wasReceivedErrorEvent = false;
        
        // update totalUpdates
        totalUpdates++;
        Log.i(TAG, "Updating totalUpdates: " + totalUpdates);
        
        // Send new request with new filters
        // Update the current view pages
        getCurrentCatalogPageModel(mSelectedPageIndex).setVariables(productsURL, searchQuery,
                navigationPath, title, navigationSource, mCatalogFilterValues, showList, totalUpdates);
        getCurrentCatalogPageModel(mSelectedPageIndex - 1).setVariables(productsURL, searchQuery,
                navigationPath, title, navigationSource, mCatalogFilterValues, showList, totalUpdates);
        getCurrentCatalogPageModel(mSelectedPageIndex + 1).setVariables(productsURL, searchQuery,
                navigationPath, title, navigationSource, mCatalogFilterValues, showList, totalUpdates);
    }

    /**
     * Save the current data to create a fall back point in case some request filtered return error
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
     * Method used to restore the old filter state when is performed a query filtered by a new
     * category. Match the old selection with the new filter option.
     * 
     * @author sergiopereira
     */
    private void matchFilterStateWithOldState() {
        Log.i(TAG, "RESTORE THE OLD SELECTED STATE");
        // Validate the old filter state
        if (mOldCatalogFilterState != null)
            // Restore the filter if match with the old
            for (CatalogFilter newFilter : mCatalogFilter) {
                Log.i(TAG, "RESTORE FILTER: " + newFilter.getName());
                // Locate the old filter
                CatalogFilter oldFilter = locateFilter(mOldCatalogFilterState, newFilter.getId());
                // Validate old filter
                if (oldFilter != null) {
                    // Case generic filter
                    if (oldFilter.hasOptionSelected())
                        // Locate selected options and save
                        newFilter.setSelectedOption(locateOption(newFilter.getFilterOptions(),
                                oldFilter.getSelectedOption()));
                    // Case price filter
                    else if (oldFilter.hasRangeValues()) {
                        // Save the range
                        newFilter.setRangeValues(oldFilter.getMinRangeValue(),
                                oldFilter.getMaxRangeValue());
                    }
                }
            }
        else
            Log.i(TAG, "OLD SELECTED STATE IS NULL");
    }

    /**
     * Locate the current options in the old selected options and save the old value.
     * 
     * @param newOptions
     * @param oldOptions
     * @return The match of the selected options
     * @author sergiopereira
     */
    private SparseArray<CatalogFilterOption> locateOption(
            ArrayList<CatalogFilterOption> newOptions, SparseArray<CatalogFilterOption> oldOptions) {
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
    private void setFilterAction() {
        // Show buttons container
        showButtonsContainer();
        // Set listener
        mFilterButton.setVisibility(View.VISIBLE);
        mFilterButton.setOnClickListener(null);
        mFilterButton.setOnClickListener(this);
        pagerTabStrip.setPadding(
                0,
                0,
                getBaseActivity().getResources().getDimensionPixelSize(
                        R.dimen.catalog_button_filter_width), 0);
    }

    /**
     * Set the filter button state, to show as selected or not
     * 
     * @author sergiopereira
     */
    private void setFilterButtonState() {
        try {
            mFilterButton.setSelected((mCatalogFilterValues.size() == 0) ? false : true);
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
    public void onClick(View v) {
        // Get the view id
        int id = v.getId();
        // Validate the click
        if (id == R.id.products_list_filter_button && isNotShowing) {
            Log.d(TAG, "ON CLICK: FILTER BUTTON");
            isNotShowing = false;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(DialogFilterFragment.FILTER_TAG, mCatalogFilter);
            DialogFilterFragment newFragment = DialogFilterFragment.newInstance(bundle, this);
            newFragment.show(getBaseActivity().getSupportFragmentManager(), "dialog");
        } else if (id == R.id.products_switch_layout_button) {
        	Log.d(TAG, "ON CLICK: SWITCH LAYOUT BUTTON");

            showList = !showList;

            // Save current layout used
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_LIST_LAYOUT, showList);
            editor.apply();

            setSwitchLayoutButtonIcon();

            // update totalUpdates
            totalUpdates++;
            Log.i(TAG, "Updating totalUpdates: " + totalUpdates);

            // Update the current view page with the current list of products rendered on a
            // different layout without calling a new request
            getCurrentCatalogPageModel(mSelectedPageIndex).switchLayout(showList, totalUpdates);
        }
    }

    /*
     * ######### LAYOUT #########
     */

    /**
     * Set some layout parameters that aren't possible by xml
     * 
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void setLayoutSpec() throws NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        // Get text
        final TextView currTextView = (TextView) pagerTabStrip.getChildAt(TAB_CURR_ID);
        final TextView nextTextView = (TextView) pagerTabStrip.getChildAt(TAB_NEXT_ID);
        final TextView prevTextView = (TextView) pagerTabStrip.getChildAt(TAB_PREV_ID);

        // Set Color
        currTextView.setPadding(0, 0, 0, 1);

        // Calculate the measures
        final float density = this.getResources().getDisplayMetrics().density;
        int mIndicatorHeight = (int) (TAB_INDICATOR_HEIGHT * density + 0.5f);
        int mFullUnderlineHeight = (int) (TAB_UNDERLINE_HEIGHT * density + 0.5f);

        // Set the indicator height
        Field field;
        field = pagerTabStrip.getClass().getDeclaredField("mIndicatorHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mIndicatorHeight);
        // Set the underline height
        field = pagerTabStrip.getClass().getDeclaredField("mFullUnderlineHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mFullUnderlineHeight);
        // Set the color of indicator
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, 0, 0, mIndicatorHeight, getResources().getColor(
                TAB_STRIP_COLOR), getResources().getColor(
                TAB_STRIP_COLOR), Shader.TileMode.CLAMP));
        field = pagerTabStrip.getClass().getDeclaredField("mTabPaint");
        field.setAccessible(true);
        field.set(pagerTabStrip, paint);

    }

    private void setContent(int index) {
        final CatalogPageModel model = getCurrentCatalogPageModel(index);
        model.setRelativeLayout(model.getRelativeLayout());
    }

    private void initPageModel() {
        for (int i = 0; i < mCatalogPageModel.length; i++) {
            mCatalogPageModel[i] = new CatalogPageModel(i, getBaseActivity(), this);
            mCatalogPageModel[i].setTitle(mSortOptions.get(i));

        }
    }

    private CatalogPageModel getCurrentCatalogPageModel(int position) {
        for (int i = 0; i < mCatalogPageModel.length; i++) {
            if (mCatalogPageModel[i].getIndex() == position) {
                return mCatalogPageModel[i];
            }

        }

        return mCatalogPageModel[position];
    }

    private void updateCatalogPageModelIdexes(int val) {
        for (int i = 0; i < mCatalogPageModel.length; i++) {
            int index = mCatalogPageModel[i].getIndex();
            if (index + val < 0) {
                mCatalogPageModel[i].setIndex(6);
            } else if (index + val == 7) {
                mCatalogPageModel[i].setIndex(0);
            } else {
                mCatalogPageModel[i].setIndex(index + val);
            }

            Log.i(TAG, "updateCatalogPageModelIdexes " + mCatalogPageModel[i].getTitle() + " "
                    + mCatalogPageModel[i].getIndex());
        }
        // setContent(PAGE_LEFT);
        // setContent(PAGE_MIDDLE);
        // setContent(PAGE_RIGHT);
        mCatalogPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(PAGE_MIDDLE, false);
    }

    private class CatalogPagerAdaper extends PagerAdapter {

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            // we only need three pages
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getCurrentCatalogPageModel(position).getTitle().toUpperCase();
        }

        private CatalogPageModel getCurrentCatalogPageModel(int position) {
            for (int i = 0; i < mCatalogPageModel.length; i++) {
                if (mCatalogPageModel[i].getIndex() == position) {
                    // check if new page to display has same settings as this
                    CatalogPageModel currentCatalogPageModel = mCatalogPageModel[i];
                    int currentCatalogPageModelTotalUpdates = currentCatalogPageModel.getTotalUpdates();
                    if (totalUpdates > currentCatalogPageModelTotalUpdates) {
                        // verify if currentCatalogPageModel is initialized
                        if (currentCatalogPageModel.getGridView() != null) {
                            Log.i(TAG, "Updating variables! " + totalUpdates + " > " + currentCatalogPageModelTotalUpdates + " updates; page title: " + currentCatalogPageModel.getTitle());
                            currentCatalogPageModel.setVariables(productsURL, searchQuery, navigationPath, 
                                    title, navigationSource, mCatalogFilterValues, showList, totalUpdates);
                        }
                    }
                    return currentCatalogPageModel;
                }
            }

            return mCatalogPageModel[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final CatalogPageModel currentPage = getCurrentCatalogPageModel(position);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Log.i(TAG, "instantiateItem");
                    boolean isTabletInLandscape = BaseActivity.isTabletInLandscape(getBaseActivity()); 
                    if (currentPage.getRelativeLayout() == null || isTabletInLandscape != currentPage.isLandScape()) {
                        RelativeLayout mRelativeLayout = (RelativeLayout) mInflater.inflate(R.layout.products, null);
                        currentPage.setRelativeLayout(mRelativeLayout);
                        currentPage.setTextViewSpnf((org.holoeverywhere.widget.TextView) currentPage.getRelativeLayout().findViewById(R.id.search_products_not_found));
                        currentPage.setButtonRavb((Button) currentPage.getRelativeLayout().findViewById(R.id.retry_alert_view_button));
                        currentPage.setRelativeLayoutPc((RelativeLayout) currentPage.getRelativeLayout().findViewById(R.id.products_content));
                        currentPage.setLinearLayoutLm((LinearLayout) currentPage.getRelativeLayout().findViewById(R.id.loadmore));

                        currentPage.setGridView((GridView) currentPage.getRelativeLayout().findViewById(R.id.middle_productslist_list), isTabletInLandscape);

                        currentPage.setLinearLayoutLb((LinearLayout) currentPage.getRelativeLayout().findViewById(R.id.loading_view_pager));
                        currentPage.setRelativeLayoutPt((RelativeLayout) currentPage.getRelativeLayout().findViewById(R.id.products_tip));
                        // initialize view, setting variables to adjust the layout and filtering details
                        currentPage.setVariables(productsURL, searchQuery, navigationPath, title,
                                navigationSource, mCatalogFilterValues, showList, totalUpdates);
                    }

                }
            }).run();

            container.addView(currentPage.getRelativeLayout());
            return currentPage.getRelativeLayout();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == (View) obj;
        }
    }

}
