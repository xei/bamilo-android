package pt.rocket.view.fragments;

import java.util.ArrayList;

import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.SubCategoriesAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.helpers.categories.GetCategoriesPerLevelsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * Class used to shoe the categories in the main container
 * @author sergiopereira
 */
public class CategoriesPageFragment extends BaseFragment implements OnItemClickListener, OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CategoriesPageFragment.class);
    
    private static final int NUMBER_OF_HEADERS = 1;
    
    private static final int HEADER_FOR_ALL_POSITION = 0;
    
    private static final int NUMBER_OF_HEADERS_LANDSCAPE = 2;
    
    private static final int HEADER_FOR_ALL_POSITION_LANDSCAPE = 1;
    
    private static final int HEADER_FOR_BACK_POSITION_LANDSCAPE = 0;
    
    private static final int NO_SELECETD_LANDSCAPE_CATEGORY = 0;
    
    private static final int CLICK_FROM_DEFAULT_CONTAINER = 0;
    
    private static final int CLICK_FROM_LANDSCAPE_CONTAINER = 1;

    private static final String ROOT_CATEGORIES = null;

    private static CategoriesPageFragment sCategoriesFragment;

    private ListView mCategoryList;

    private LayoutInflater mInflater;

    private String mParentCategoryName;

    private ListView mLandscapeCategoryChildrenList;

    private int mSelectedParentPosition;

    private String mTitleCategory;

    private View mLandscapeLoading;

    private String mCategoryKey;

    private ArrayList<Category> mCategories;

    private Category mCurrentSubCategory;
    
    private String treeName = "";
    
    /**
     * Create a new instance and save the bundle data
     * @param bundle
     * @return NavigationCategoryFragment
     * @author sergiopereira
     */
    public static CategoriesPageFragment getInstance(Bundle bundle) {
        sCategoriesFragment = new CategoriesPageFragment();
        sCategoriesFragment.setArguments(bundle);
        return sCategoriesFragment;
    }

    /**
     * Empty constructor
     */
    public CategoriesPageFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.categories_page_fragment);
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
        // Get data from saved instance or from arguments
        Bundle bundle = (savedInstanceState != null) ? savedInstanceState : getArguments();
        // Get data
        if(bundle != null) {
            Log.i(TAG, "ON GET DATA FROM BUNDLE");
            // Sava data
            mCategoryKey = bundle.getString(ConstantsIntentExtra.CATEGORY_ID);
            mParentCategoryName = bundle.getString(ConstantsIntentExtra.CATEGORY_PARENT_NAME);
            mSelectedParentPosition = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
            mTitleCategory = bundle.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
            Log.i(TAG, "ON LOAD SAVED STATE: " + mParentCategoryName + " " + mSelectedParentPosition);
        } 
        // Case bundle null
        else Log.w(TAG, "WARNING: SOMETHING IS WRONG BUNDLE IS NULL ON CREATE");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get inflater
        mInflater = LayoutInflater.from(getBaseActivity());
        // Get category list view
        mCategoryList = (ListView) view.findViewById(R.id.sub_categories_grid);
        // Get child category list view (landscape)
        mLandscapeCategoryChildrenList = (ListView) view.findViewById(R.id.sub_categories_grid_right);
        // Get loading (landscape)
        mLandscapeLoading = view.findViewById(R.id.loading_bar);
        
        // Validation to show content
        // Case cache
        if (mCategories != null && mCategories.size() > 0) showCategoryList(mCategories);
        // Case empty
        else if(!TextUtils.isEmpty(ShopSelector.getShopId())) triggerGetCategories(mCategoryKey);
        // Case recover from background
        else { Log.w(TAG, "APPLICATION IS ON BIND PROCESS"); showRetry(); }
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

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // Set title
        getBaseActivity().setTitle(TextUtils.isEmpty(mTitleCategory) ? getString(R.string.categories_title) : mTitleCategory);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE");
        outState.putString(ConstantsIntentExtra.CATEGORY_ID, mCategoryKey);
        outState.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, mParentCategoryName);
        outState.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, mSelectedParentPosition);
        outState.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME, mTitleCategory);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW"); 
    }
    
    /**
     * ######## LAYOUT ########
     */
    /**
     * Show the category list for current level
     * @author sergiopereira
     */
    private void showCategoryList(ArrayList<Category> categories) {
        // Case root
        if(mCategoryKey == ROOT_CATEGORIES) showRootCategories(categories);
        // Case branch
        else if(categories != null && categories.size() > 0) showSubCategory(categories.get(0));
        // Case error
        else showRetry();
        // Show content
        showFragmentContentContainer();
    }
    
    /**
     * Show the root categories without headers
     * @author sergiopereira
     */
    private void showRootCategories(ArrayList<Category> categories) {
        Log.i(TAG, "ON SHOW ROOT CATEGORIES: " + mSelectedParentPosition);
        // Container
        CategoriesAdapter mCategoryAdapter = new CategoriesAdapter(getBaseActivity(), categories);
        mCategoryList.setTag(CLICK_FROM_DEFAULT_CONTAINER);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setOnItemClickListener(this);
        // Validate and fill the landscape container
        if(isPresentLandscapeContainer()) triggerGetCategoriesLandscape(categories.get(mSelectedParentPosition).getUrlKey());
    }
    
    /**
     * Show the nested categories with respective headers
     * @author sergiopereira
     */
    private void showSubCategory(Category category) {
        Log.i(TAG, "ON SHOW NESTED CATEGORIES");
        try {
            // Get data
            mCurrentSubCategory = category;
            ArrayList<Category> child = category.getChildren();
            String categoryName = category.getName();
            // Set adapter, tag and listener
            SubCategoriesAdapter mSubCategoryAdapter = new SubCategoriesAdapter(getBaseActivity(), child, categoryName);
            mCategoryList.setTag(CLICK_FROM_DEFAULT_CONTAINER);
            mCategoryList.setAdapter(mSubCategoryAdapter);
            mCategoryList.setOnItemClickListener(this);
            // Validate and fill the landscape container
            if(isPresentLandscapeContainer()) triggerGetCategoriesLandscape(child.get(mSelectedParentPosition).getUrlKey());
            
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
            //goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING IOE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
            //goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
        }
    }
    
    /**
     * Create a header using a layout with R.id.text
     * @param layout
     * @param text
     * @return View
     * @author sergiopereira
     */
    private View createHeader(int layout, String text){
        View headerForAll = mInflater.inflate(layout, null);
        ((TextView) headerForAll.findViewById(R.id.text)).setText(text);
        return headerForAll;
    }
        
    /**
     * Show only the retry view
     * @author sergiopereira
     */
    private void showRetry() {
        showFragmentRetry((OnClickListener) this);
    }
    
    /**
     * ############# LANDSCAPE LAYOUT ############# 
     */
    
    /**
     * Method used to validate if the landscape container is present or not
     * @return true/false
     * @author sergiopereira
     */
    private boolean isPresentLandscapeContainer(){
        return mLandscapeCategoryChildrenList != null ? true : false;
    }

    /**
     * Method used to fill the landscape container with the sub categories from selected category in the default container.
     * @param category
     * @param parent position
     * @author sergiopereira
     */
    private void showChildrenInLandscape(Category category) {
        // Create and add the header for back
        if(mLandscapeCategoryChildrenList.getHeaderViewsCount() == 0 && mCategoryKey != ROOT_CATEGORIES) {
            View headerForBack = createHeader(R.layout.category_inner_top_back, getString(R.string.back_label));
            mLandscapeCategoryChildrenList.addHeaderView(headerForBack);
        }
        // Set adapter, tag and listener
        SubCategoriesAdapter adapter = new SubCategoriesAdapter(getBaseActivity(), category.getChildren(), category.getName());
        mLandscapeCategoryChildrenList.setAdapter(adapter);
        mLandscapeCategoryChildrenList.setTag(CLICK_FROM_LANDSCAPE_CONTAINER);
        mLandscapeCategoryChildrenList.setOnItemClickListener((OnItemClickListener) this);
    }
    
    /**
     * Show the landscape loading
     * @author sergiopereira
     */
    private void showLandscapeLoading() {
        // Show inner loading
        showLoadingInfo(mLandscapeLoading);
    }
    
    /**
     * Hide the landscape loading
     * @author sergiopereira
     */
    private void hideLandscapeLoading() {
        // Hide inner loading
        hideLoadingInfo(mLandscapeLoading);
    }
    
    /**
     * ####### TRIGGERS ####### 
     */
    /**
     * Trigger to get categories
     * @author sergiopereira
     */    
    private void triggerGetCategories(String categoryKey) {
        Log.i(TAG, "GET CATEGORY PER LEVEL: " + categoryKey);
        // Create bundle 
        Bundle bundle = new Bundle();
        // Get per levels
        bundle.putString(GetCategoriesPerLevelsHelper.PAGINATE_KEY, GetCategoriesPerLevelsHelper.PAGINATE_ENABLE);
        // Get category
        if(!TextUtils.isEmpty(categoryKey)) bundle.putString(GetCategoriesPerLevelsHelper.CATEGORY_KEY, categoryKey);
        // Trigger
        triggerContentEvent(new GetCategoriesPerLevelsHelper(), bundle, this);
    }
    
    /**
     * Trigger to get sub categories for landscape container.
     * @param category url key
     * @author sergiopereira
     */
    private void triggerGetCategoriesLandscape(String categoryKey) {
        Log.i(TAG, "GET CATEGORY PER LEVEL: " + categoryKey);
        // Show inner loading
        showLandscapeLoading();
        // Create bundle 
        Bundle bundle = new Bundle();
        // Get category per levels
        bundle.putString(GetCategoriesPerLevelsHelper.PAGINATE_KEY, GetCategoriesPerLevelsHelper.PAGINATE_ENABLE);
        bundle.putString(GetCategoriesPerLevelsHelper.CATEGORY_KEY, categoryKey);
        // Trigger
        triggerContentEventWithNoLoading(new GetCategoriesPerLevelsHelper(), bundle, new IResponseCallback() {
            
            @Override
            public void onRequestError(Bundle bundle) {
                // Validate fragment state
                if(isOnStoppingProcess) return;
                // Hide laoding
                hideLandscapeLoading();
            }
            
            @Override
            public void onRequestComplete(Bundle bundle) {
                // Validate fragment state
                if(isOnStoppingProcess) return;
                // Get categories
                ArrayList<Category> categories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
                // Show categories and hide loading
                showChildrenInLandscape((hasContent(categories)) ? categories.get(0) : new Category());
                hideLandscapeLoading();                
            }
        });
    }
    
    /**
     * ####### LISTENERS ####### 
     */
//    /*
//     * (non-Javadoc)
//     * @see android.view.View.OnClickListener#onClick(android.view.View)
//     */
//    @Override
//    public void onClick(View view) {
//        // Get view id
//        int id = view.getId();
//        // Case retry
//        if (id == R.id.fragment_root_retry_button) onClickRetryButton();
//        // Case Unknown
//        else Log.w(TAG, "WARNING: UNKNOWN BUTTON");
//    }
//    
//    /**
//     * Process the click on retry
//     * @author sergiopereira
//     */
//    private void onClickRetryButton(){
//        Log.d(TAG, "ON CLICK RETRY");
//        triggerGetCategories(mCategoryKey);
//    }
    

    protected void onRetryRequest(EventType eventType){
        Log.d(TAG, "ON CLICK RETRY");
        triggerGetCategories(mCategoryKey);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     * android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "ON ITEM CLICKED: " + position + " TAG:" + parent.getTag());
        try {
            // Get tag
            int from = (Integer) parent.getTag();
            // Validation
            switch (from) {
            case CLICK_FROM_DEFAULT_CONTAINER:
                Log.i(TAG, "CLICK_FROM_DEFAULT_CONTAINER");
                // Case root
                if(mCategoryKey == ROOT_CATEGORIES) onClickRootCategory(parent, position);
                // Case branch or leaf
                else onClickNestedCategory(parent, position);
                break;
            case CLICK_FROM_LANDSCAPE_CONTAINER:
                Log.i(TAG, "CLICK_FROM_LANDSCAPE_CONTAINER");
                // Case root level from landscape without back button
                if(mCategoryKey == ROOT_CATEGORIES) onClickSubCategoryWithoutBack(parent, position);
                // Case other level from landscape with back button
                else onClickSubCategoryWithBack(parent, position);
                break;
            default:
                Log.w(TAG, "WARNING: UNKNOWN CLICK");
                break;
            }            
        } catch (ClassCastException e) {
            Log.w(TAG, "WARNING CCE ON CLICK ITEM, THE TAG MUST BE AN INTEGER", e);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON CLICK ITEM, THE TAG IS NULL", e);
        }
    }
    
    /**
     * Process the click on a root category.
     * In landscape shows the sub categories in the right container.
     * @param position
     * @author sergiopereira
     * @param parent 
     */
    private void onClickRootCategory(AdapterView<?> parent, int position) {
        try {
            // Validate item
            mSelectedParentPosition = position;
            // Get category
            Category category = (Category) parent.getAdapter().getItem(position); 
            // Show product list
            if (!category.hasChildren()) gotoCatalog(category);
            // Show sub level in landscape container
            else if (isPresentLandscapeContainer()) triggerGetCategoriesLandscape(category.getUrlKey());
            // Show sub level
            else gotoNestedCategories(category.getName(), category.getUrlKey(), NO_SELECETD_LANDSCAPE_CATEGORY);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON CLICK ROOT CATEGORY POS: " + position);   
        }
    }
    
    /**
     * Process the click on a nested category.<br>
     * In landscape shows the sub categories in the right container.
     * @param position
     * @author sergiopereira
     */
    private void onClickNestedCategory(AdapterView<?> parent, int position) {
        try {
            // Validate position
            switch (position) {
            case HEADER_FOR_ALL_POSITION:
                // Second header goes to all
                gotoCatalog(mCurrentSubCategory);
                break;
            default:
                // Get real position
                mSelectedParentPosition = position - NUMBER_OF_HEADERS;
                // Validate item goes to product list or a sub level
                Category selectedCategory = (Category) parent.getAdapter().getItem(position);
                // Show product list
                if (!selectedCategory.hasChildren()) gotoCatalog(selectedCategory);
                // Show sub level in landscape container
                else if (isPresentLandscapeContainer()) triggerGetCategoriesLandscape(selectedCategory.getUrlKey());
                // Show sub level
                else gotoNestedCategories(selectedCategory.getName(), selectedCategory.getUrlKey() , NO_SELECETD_LANDSCAPE_CATEGORY);
                break;
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON CLICK NESTED CATEGORY POS: " + position, e);
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING IOE ON CLICK NESTED CATEGORY", e);
        }
    }
    

    /**
     * Proccess the click on a category in landscape container.<br>
     * The behavior it's like {@link #onClickNestedCategory(int)}.
     * @param parent
     * @param position
     * @author sergiopereira
     */
    private void onClickSubCategoryWithoutBack(AdapterView<?> parent, int position) {
        try {
            // Validate position
            switch (position) {
            case HEADER_FOR_ALL_POSITION:
                // Second header goes to all
                gotoCatalog(mCategories.get(mSelectedParentPosition));
                break;
            default:
                // Get real position
                int pos = position - NUMBER_OF_HEADERS;
                // Validate item goes to product list or a sub level
                Category selectedCategory = (Category) parent.getAdapter().getItem(position);
                // Show product list
                if (!selectedCategory.hasChildren()) gotoCatalog(selectedCategory);
                // Show sub level
                else gotoNestedCategories(selectedCategory.getParent().getName(), selectedCategory.getParent().getUrlKey(), pos);
                break;
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK NESTED CATEGORY IN LANDSCAPE POS: " + position, e);
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING IOE ON CLICK NESTED CATEGORY IN LANDSCAPE", e);
        }
    }
    
    /**
     * Proccess the click on a category in landscape container.<br>
     * This layout contains two header, BACK and ALL.
     * @param parent
     * @param position
     * @author sergiopereira
     */
    private void onClickSubCategoryWithBack(AdapterView<?> parent, int position) {
        try {
            // Validate position
            switch (position) {
            case HEADER_FOR_BACK_POSITION_LANDSCAPE:
                // First header goes to parent
                goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL);
                break;
            case HEADER_FOR_ALL_POSITION_LANDSCAPE:
                // Second header goes to all
                gotoCatalog(mCurrentSubCategory.getChildren().get(mSelectedParentPosition));
                break;
            default:
                // Get real position
                int pos = position - NUMBER_OF_HEADERS_LANDSCAPE;
                // Validate item goes to product list or a sub level
                Category selectedCategory = (Category) parent.getAdapter().getItem(position);
                // Show product list
                if (!selectedCategory.hasChildren()) gotoCatalog(selectedCategory);
                // Show sub level
                else gotoNestedCategories(selectedCategory.getParent().getName(), selectedCategory.getParent().getUrlKey(), pos);
                break;
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK NESTED CATEGORY IN LANDSCAPE POS: " + position, e);
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING IOE ON CLICK NESTED CATEGORY IN LANDSCAPE", e);
        }
    }
    
    /**
     * Show nested categories with title name
     * @param name
     * @param selectedCategoryPosition
     * @param landscapeCategoryPosition
     * @author sergiopereira 
     */
    private void gotoNestedCategories(String name, String key, int landscapeCategoryPosition){
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CATEGORY_ID, key);
        bundle.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, name);
        bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, landscapeCategoryPosition);
        treeName = (TextUtils.isEmpty(mTitleCategory))? name : mTitleCategory + "/" + name;
        bundle.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME,  treeName);
        // Switch
        ((CategoriesCollectionFragment) getParentFragment()).onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL, bundle);
    }
    
    /**
     * Show product list
     * @param category
     * @author sergiopereira
     */
    private void gotoCatalog(Category category) {
        
        // Update category counter for tracking
        CategoriesTableHelper.updateCategoryCounter(category.getId(), category.getName());
        // Tracking category
        //trackCategory(category.getName());
        
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
        bundle.putString(ConstantsIntentExtra.CATEGORY_ID, category.getId());
        String categoryTree = "";
        if(!TextUtils.isEmpty(getBaseActivity().getCategoriesTitle())){
            categoryTree = getBaseActivity().getCategoriesTitle()+"/"+category.getName(); 
            categoryTree = categoryTree.replace("/", ",");
        }
        bundle.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME,  categoryTree);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
        // Goto Catalog
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Pop the back stack until the parent from current level type
     * @param type
     * @author sergiopereira
     */
    private void goToParentCategoryFromType(FragmentType type){
        Log.i(TAG, "GOTO PARENT LEVEL FROM: " + type.toString());
        switch (type) {
        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
            ((CategoriesCollectionFragment) getParentFragment()).goToBackUntil(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
            break;
        case NAVIGATION_CATEGORIES_SUB_LEVEL:
            ((CategoriesCollectionFragment) getParentFragment()).goToParentCategory();
            break;
        default:
            Log.w(TAG, "WARNING: ON GOTO PARENT UNKNOWN LEVEL");
            break;
        }
    }

    /**
     * ####### RESPONSE EVENTS ####### 
     */
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS EVENT");
        // Validate fragment state
        if(isOnStoppingProcess) return;
        // Get categories
        mCategories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        // Show categories
        showCategoryList(mCategories);
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON ERROR EVENT");
        // Validate fragment state
        if(isOnStoppingProcess) return;
//        // Generic errors
//        if(getBaseActivity().handleErrorEvent(bundle)) return;
//        // Show retry
//        showRetry();
        
        // Generic errors
        if(super.handleErrorEvent(bundle)) return;
    }
    
    /**
     * ####### TRACKING ####### 
     */
//    /**
//     * Tracking category
//     * @param name
//     */
//    private void trackCategory(String name){
//        Bundle params = new Bundle();
//        params.putString(TrackerDelegator.CATEGORY_KEY, name);
//        params.putInt(TrackerDelegator.PAGE_NUMBER_KEY, 1);
//        params.putSerializable(TrackerDelegator.LOCATION_KEY, TrackingEvent.CATALOG_FROM_CATEGORIES);
//        TrackerDelegator.trackCategoryView(params);
//    }
}
