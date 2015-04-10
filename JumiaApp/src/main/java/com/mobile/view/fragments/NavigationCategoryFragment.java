/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.CategoriesAdapter;
import com.mobile.controllers.SubCategoriesAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.database.CategoriesTableHelper;
import com.mobile.framework.objects.Category;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.framework.utils.ShopSelector;
import com.mobile.helpers.categories.GetCategoriesPerLevelsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.view.MainFragmentActivity;
import com.mobile.view.R;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Class used to shoe the categories in the navigation container
 * @author sergiopereira
 */
public class NavigationCategoryFragment extends BaseFragment implements OnItemClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(NavigationCategoryFragment.class);
    
    private static final int HEADER_FOR_BACK_POSITION = 0;
    
    private static final int HEADER_FOR_ALL_POSITION = 1;
    
    private static final String ROOT_CATEGORIES = null;

    private ListView mCategoryList;

    private Category currentCategory;

    private LayoutInflater mInflater;
    
    private String mCategoryKey;

    private ArrayList<Category>  mCategories;

    /**
     * Create a new instance and save the bundle data
     * @param bundle
     * @return NavigationCategoryFragment
     * @author sergiopereira
     */
    public static NavigationCategoryFragment getInstance(Bundle bundle) {
        NavigationCategoryFragment categoriesFragment = new NavigationCategoryFragment();
        categoriesFragment.setArguments(bundle);
        return categoriesFragment;
    }

    /**
     * Empty constructor as a nested fragment
     */
    public NavigationCategoryFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.navigation_fragment_categories);
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
        Bundle bundle = savedInstanceState == null ? getArguments() : savedInstanceState;
        // Get data
        if(bundle != null) {
            Log.i(TAG, "ON LOAD SAVED STATE");
            mCategoryKey = bundle.getString(ConstantsIntentExtra.CATEGORY_ID);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get inflater
        mInflater = LayoutInflater.from(getBaseActivity());
        // Get category list view
        mCategoryList = (ListView) view.findViewById(R.id.nav_sub_categories_grid);
        
        // Validation to show content
        // Case cache
        if (mCategories != null && mCategories.size() > 0){ 
            showCategoryList(mCategories);
        }
        // Case empty
        else if(!TextUtils.isEmpty(ShopSelector.getShopId())) {
            if(getBaseActivity() instanceof MainFragmentActivity && !((MainFragmentActivity) getBaseActivity()).isInMaintenance())
                triggerGetCategories(mCategoryKey);
        }
        // Case recover from background
        else {
            Log.w(TAG, "APPLICATION IS ON BIND PROCESS");
            showRetry();
        }
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
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
        Log.i(TAG, "ON DESTROY");
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
        Log.i(TAG, "ON SHOW ROOT CATEGORIES");
        CategoriesAdapter mCategoryAdapter = new CategoriesAdapter(getBaseActivity(), categories);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setOnItemClickListener(this);
    }
    
    /**
     * Show the nested categories with respective headers
     * @author sergiopereira
     */
    private void showSubCategory(Category category) {
        Log.i(TAG, "ON SHOW NESTED CATEGORIES");
        try {
            // Get data
            this.currentCategory = category;
            ArrayList<Category> child = category.getChildren();
            String categoryName = category.getName();
            // Create and add the header for back
            // Use always word BACK
            View headerForBack = createHeader(R.layout.category_inner_top_back, getString(R.string.back_label)); 
            mCategoryList.addHeaderView(headerForBack);
            // Set Adapter
            SubCategoriesAdapter mSubCategoryAdapter = new SubCategoriesAdapter(getBaseActivity(), child, categoryName);
            mCategoryList.setAdapter(mSubCategoryAdapter);
            // Set listener
            mCategoryList.setOnItemClickListener(this);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
            showRetry();
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
        showFragmentErrorRetry();
    }
    
    /**
     * ####### TRIGGERS ####### 
     */
    /**
     * Trigger to get categories with pagination
     * @param categoryKey
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
     * ####### LISTENERS ####### 
     */
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        // Get categories from unexpected error
        triggerGetCategories(mCategoryKey);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onRetryRequest(com.mobile.framework.utils.EventType)

    @Override
    protected void onRetryRequest(EventType eventType) {
        //super.onRetryRequest(eventType);
        // Get categories from no network
        triggerGetCategories(mCategoryKey);
    }
    */
    
    /*
     * (non-Javadoc)
     * 
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     * android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "ON ITEM CLICKED: " + position);
        // Case root
        if(mCategoryKey == ROOT_CATEGORIES) onClickRootCategory(parent, position);
        // Case branch or leaf
        else onClickNestedCategory(parent, position);
    }
    
    /**
     * Process the click on a root category
     * @param position
     * @author sergiopereira
     */
    private void onClickRootCategory(AdapterView<?> parent, int position) {
        try {
            Category category = (Category) parent.getAdapter().getItem(position);
            // Show product list
            if (!category.hasChildren()) gotoCatalog(category);
            // Show sub level
            else gotoSubCategory(category.getUrlKey());
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK ROOT CATEGORY");   
        }
    }
    
    /**
     * Process the click on a nested category
     * @param position
     * @author sergiopereira
     */
    private void onClickNestedCategory(AdapterView<?> parent, int position) {
        try {
            switch (position) {
            case HEADER_FOR_BACK_POSITION:
                // First header goes to parent
                gotoParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL);
                break;
            case HEADER_FOR_ALL_POSITION:
                // Second header goes to all
                gotoCatalog(currentCategory);
                break;
            default:
                // Validate item goes to product list or a sub level
                Category selectedCategory = (Category) parent.getAdapter().getItem(position);
                // Show product list
                if (!selectedCategory.hasChildren()) gotoCatalog(selectedCategory);
                // Show sub level
                else gotoSubCategory(selectedCategory.getUrlKey());
                break;
            }
        } catch (Exception e) {
            Log.w(TAG, "WARNING NPE ON CLICK NESTED CATEGORY POS: " + position);
        }
    }
    
    /**
     * Show nested categories
     * @param categoryKey key
     * @author sergiopereira
     */
    private void gotoSubCategory(String categoryKey){
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CATEGORY_ID, categoryKey);
        ((NavigationFragment) getParentFragment()).onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL, bundle);
    }
    
    /**
     * Show product list
     * @param category
     * @author sergiopereira
     */
    private void gotoCatalog(Category category) {
        // Update counter for tracking
        CategoriesTableHelper.updateCategoryCounter(category.getId(), category.getName());
        // Close navigation
        getBaseActivity().closeNavigationDrawer();
        // Create bundle for catalog 
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
        // Goto Catalog
        getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Pop the back stack until the parent from current level type
     * @param type
     * @author sergiopereira
     */
    private void gotoParentCategoryFromType(FragmentType type){
        Log.i(TAG, "GOTO PARENT LEVEL FROM: " + type.toString());
        switch (type) {
        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
            ((NavigationFragment) getParentFragment()).goToBackUntil(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
            break;
        case NAVIGATION_CATEGORIES_SUB_LEVEL:
            ((NavigationFragment) getParentFragment()).goToParentCategory();
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
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS EVENT");
        // Validate fragment state
        if (isOnStoppingProcess) return;
        // Get categories
        mCategories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        // Show categories
        showCategoryList(mCategories);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON ERROR EVENT");
        // Validate fragment state
        if (isOnStoppingProcess) return;
        /*
        // Generic errors
        if(super.handleErrorEvent(bundle)){
            ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
            if(errorCode == ErrorCode.SSL){
                getBaseActivity().closeNavigationDrawer();
                hideFragmentRootViews();
            }
            return;
        }
        */
        // Show retry
        showRetry();
    }

    @Override
    protected void showFragmentNoNetworkRetry() {
        super.showFragmentNoNetworkRetry();
        try{
            ((TextView)getView().findViewById(R.id.no_connection_label)).setTextSize( TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.no_connection_label_small_size) );
            ((TextView)getView().findViewById(R.id.no_connection_details_label)).setTextSize( TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.no_connection_label_details_small_size) );
        }catch(NullPointerException e){
            Log.w(TAG, "WARNING NPE ON SHOW RETRY LAYOUT");
        }
    }

}
