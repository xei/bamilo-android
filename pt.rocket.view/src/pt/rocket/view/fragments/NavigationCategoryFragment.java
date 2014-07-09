/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.SubCategoriesAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.helpers.GetCategoriesHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * Classm used to shoe the categories in the navigation container
 * @author sergiopereira
 */
public class NavigationCategoryFragment extends BaseFragment implements OnItemClickListener, OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(NavigationCategoryFragment.class);
    
    private static final int HEADER_FOR_BACK_POSITION = 0;
    
    private static final int HEADER_FOR_ALL_POSITION = 1;
    
    private static final int NUMBER_OF_HEADERS = 2;
    
    private static final int ROOT_CATEGORIES_LEVEL = 0;

    private ListView mCategoryList;

    private Category currentCategory;
    
    private CategoriesAdapter mCategoryAdapter;

    private SubCategoriesAdapter mSubCategoryAdapter;

    private LayoutInflater mInflater;

    private View mLoadingView;

    private View mRetryView;

    private ArrayList<Integer> mTreePath;

    // private String mParentCategoryName;

    /**
     * Create a new instance and save the bundle data
     * @param bundle
     * @return NavigationCategoryFragment
     * @author sergiopereira
     */
    public static NavigationCategoryFragment getInstance(Bundle bundle) {
        NavigationCategoryFragment categoriesFragment = new NavigationCategoryFragment();
        // Get data
        if(bundle != null) {
            Log.i(TAG, "ON GET INSTANCE: SAVE DATA");
            categoriesFragment.mTreePath = bundle.getIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH);
            // categoriesFragment.mParentCategoryName = bundle.getString(ConstantsIntentExtra.CATEGORY_PARENT_NAME);
            // Validate path
            if(categoriesFragment.mTreePath == null) categoriesFragment.mTreePath = new ArrayList<Integer>();
        }
        return categoriesFragment;
    }

    /**
     * Empty constructor
     */
    public NavigationCategoryFragment() {
        super(IS_NESTED_FRAGMENT);
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
        // Validate saved state
        if(savedInstanceState != null) {
            Log.i(TAG, "ON LOAD SAVED STATE");
            mTreePath = savedInstanceState.getIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH);
            // mParentCategoryName = savedInstanceState.getString(ConstantsIntentExtra.CATEGORY_PARENT_NAME);
        }
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
        this.mInflater = inflater;
        return inflater.inflate(R.layout.navigation_fragment_categories, container, false);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get category list view
        mCategoryList = (ListView) getView().findViewById(R.id.sub_categories_grid);
        // Get loading view
        mLoadingView = view.findViewById(R.id.loading_bar);
        // Get retry view
        mRetryView = view.findViewById(R.id.campaign_retry);
        view.findViewById(R.id.campaign_retry_button).setOnClickListener(this);
        // Validate the cache
        if (JumiaApplication.currentCategories != null) showCategoryList();
        else if(!TextUtils.isEmpty(ShopSelector.getShopId())) triggerGetCategories();
        else Log.w(TAG, "APPLICATION IS ON BIND PROCESS");
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
        outState.putIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH, mTreePath);
        // outState.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, mParentCategoryName);
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
        Log.i(TAG, "ON DESTROY");
       
    }
    
    /**
     * ######## LAYOUT ########
     */
    
    /**
     * Show the category list for current level
     * @author sergiopereira
     */
    private void showCategoryList() {
        // Get level
        int mTreeLevel = mTreePath.size();
        Log.i(TAG, "SHOW CATEGORY IN LEVEL: " + mTreeLevel);
        
        // Case root
        if(mTreeLevel == ROOT_CATEGORIES_LEVEL) showRootCategories();
        // Case branch
        else if(mTreeLevel > ROOT_CATEGORIES_LEVEL) showSubCategory();
        // Case unknown
        else showRetry();
        
        // Show content
        showContent();
    }
        
    /**
     * Show the root categories without headers
     * @author sergiopereira
     */
    private void showRootCategories() {
        Log.i(TAG, "ON SHOW ROOT CATEGORIES");
        mCategoryAdapter = new CategoriesAdapter(getActivity(), JumiaApplication.currentCategories);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setOnItemClickListener(this);
    }
    
    /**
     * Show the nested categories with respective headers
     * @author sergiopereira
     */
    private void showSubCategory() {
        Log.i(TAG, "ON SHOW NESTED CATEGORIES");
        try {
            // Get data
            currentCategory = findCategoryByPath();
            ArrayList<Category> child = currentCategory.getChildren();
            String categoryName = currentCategory.getName();
            // Create and add the header for back
            // Use always word BACK
            View headerForBack = createHeader(R.layout.category_inner_top_back, getString(R.string.back)); 
            mCategoryList.addHeaderView(headerForBack);
            // Set Adapter
            mSubCategoryAdapter = new SubCategoriesAdapter(getActivity(), child, categoryName);
            mCategoryList.setAdapter(mSubCategoryAdapter);
            // Set listener
            mCategoryList.setOnItemClickListener(this);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
            goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
        }
    }
    
    /**
     * Method used to find the current category with respective path
     * @return Category or null
     * @author sergiopereira
     */
    private Category findCategoryByPath() {
        Log.i(TAG, "ON FIND CURRENT CATEGORY");
        // Get root categories
        ArrayList<Category> subCategories = JumiaApplication.currentCategories;
        // Current category
        Category category =  null;
        // For each parent position
        for (Integer nodePosition : mTreePath) {
            // Get category to return
            category = subCategories.get(nodePosition);
            // Get childs to find
            subCategories = category.getChildren();
        }
        return category;
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
     * Show only the loading view
     * @author sergiopereira
     */
    private void showLoading(){
        mCategoryList.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mRetryView.setVisibility(View.GONE);
    }
    
    /**
     * Show only the content view
     * @author sergiopereira
     */
    private void showContent() {
        mCategoryList.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mRetryView.setVisibility(View.GONE);
    }
    
    /**
     * Show only the retry view
     * @author sergiopereira
     */
    private void showRetry() {
        mCategoryList.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mRetryView.setVisibility(View.VISIBLE);
    }
    
    /**
     * ####### TRIGGERS ####### 
     */
    /**
     * Trigger to get categories
     * @author sergiopereira
     */
    private void triggerGetCategories(){
        Log.i(TAG, "TRIGGER: GET CATEGORIES");
        // Show loading 
        showLoading();
        // Get categories
        Bundle bundle = new Bundle();
        bundle.putString(GetCategoriesHelper.CATEGORY_URL, null);
        triggerContentEventWithNoLoading(new GetCategoriesHelper(), bundle, this);    
    }
    
    /**
     * ####### LISTENERS ####### 
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case retry
        if (id == R.id.campaign_retry_button) onClickRetryButton();
        // Case Unknown
        else Log.w(TAG, "WARNING: UNKNOWN BUTTON");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     * android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "ON ITEM CLICKED: " + position);
        // Validate tree level
        int mTreeLevel = mTreePath.size();
        Log.d(TAG, "TREE LEVEL: " + mTreeLevel);
        // Validate the level
        switch (mTreeLevel) {
        case ROOT_CATEGORIES_LEVEL:
            onClickRootCategory(position);
            break;
        default:
            onClickNestedCategory(position);
            break;
        }
    }
    
    /**
     * Process the click on a root category
     * @param position
     * @author sergiopereira
     */
    private void onClickRootCategory(int position) {
        try {
            // Validate item
            Category category = JumiaApplication.currentCategories.get(position);
            // Show product list
            if (!category.getHasChildren()) showProductList(category);
            // Show sub level
            else showNestedCategories(getString(R.string.categories), position);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON CLICK ROOT CATEGORY POS: " + position);   
        }
    }
    
    /**
     * Process the click on a nested category
     * @param position
     * @author sergiopereira
     */
    private void onClickNestedCategory(int position) {
        try {
            switch (position) {
            case HEADER_FOR_BACK_POSITION:
                // First header goes to parent
                goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL);
                break;
            case HEADER_FOR_ALL_POSITION:
                // Second header goes to all
                showProductList(currentCategory);
                break;
            default:
                // Get real position
                int pos = position - NUMBER_OF_HEADERS;
                // Validate item goes to product list or a sub level
                Category selectedCategory = currentCategory.getChildren().get(pos);
                // Show product list
                if (!selectedCategory.getHasChildren()) showProductList(selectedCategory);
                // Show sub level
                else showNestedCategories(currentCategory.getName(), pos);
                break;
            }
        } catch (Exception e) {
            Log.w(TAG, "WARNING NPE ON CLICK NESTED CATEGORY POS: " + position);
        }
    }
    
    /**
     * Show nested categories with title name
     * @param name
     * @param position
     * @author sergiopereira
     */
    @SuppressWarnings("unchecked")
    private void showNestedCategories(String name, int position){
        ArrayList<Integer> newTreePath = (ArrayList<Integer>) mTreePath.clone();
        newTreePath.add(position);
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH, newTreePath);
        bundle.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, name);
        ((NavigationFragment) getParentFragment()).onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL, bundle);
    }
    
    /**
     * Show product list
     * @param category
     * @author sergiopereira
     */
    private void showProductList(Category category) {
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean(CategoriesContainerFragment.REMOVE_FRAGMENTS, true);
        bundle2.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
        bundle2.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
        bundle2.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        bundle2.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        bundle2.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
        // Remove entries until Home
        FragmentController.getInstance().removeEntriesUntilTag(FragmentType.HOME.toString());
        // Goto Catalog
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle2, FragmentController.ADD_TO_BACK_STACK);
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
     * Process the click on retry
     * @author sergiopereira
     */
    private void onClickRetryButton(){
        Log.d(TAG, "ON CLICK RETRY");
        triggerGetCategories();
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
        // Save categories
        JumiaApplication.currentCategories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);; 
        // Validate fragment state
        if(isOnStoppingProcess) return;
        // Validate saved categories
        if(JumiaApplication.currentCategories != null) showCategoryList();
        else showRetry();
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON ERROR EVENT");
        // Show retry
        showRetry();
    }
    
}
