/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.List;

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
import pt.rocket.helpers.GetCategoriesHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
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

    private ListView mCategoryList;

    private int mCategoryIndex;

    private int mSubCategoryIndex;

    private List<Category> parent;

    private ArrayList<Category> child;

    private Category currentCategory;
    
    private CategoriesAdapter mCategoryAdapter;

    private SubCategoriesAdapter mSubCategoryAdapter;
    
    private FragmentType mCurrentCategoryLevel = FragmentType.NAVIGATION_CATEGORIES_LEVEL_1;

    private LayoutInflater mInflater;

    private View mLoadingView;

    private View mRetryView;
    
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
            // Get and validate level
            categoriesFragment.mCurrentCategoryLevel = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL);
            if(categoriesFragment.mCurrentCategoryLevel == null) categoriesFragment.mCurrentCategoryLevel = FragmentType.NAVIGATION_CATEGORIES_LEVEL_1;
            // Get cat id and sub cat id
            categoriesFragment.mCategoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
            categoriesFragment.mSubCategoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
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
            mCurrentCategoryLevel = (FragmentType) savedInstanceState.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL);
            mCategoryIndex = savedInstanceState.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
            mSubCategoryIndex = savedInstanceState.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
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
        // Validate categories on cache
        if (JumiaApplication.currentCategories != null){
            showCategoryList();
        } else { // if (mCurrentCategoryLevel == FragmentType.NAVIGATION_CATEGORIES_LEVEL_1) {
            triggerGetCategories();
        } 
//        else {
//            Log.w(TAG, "WARNING CATEGORIES IS EMPTY IN : " + mCurrentCategoryLevel.toString());
//            goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_LEVEL_1);
//        }
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
        outState.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, mCurrentCategoryLevel);
        outState.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, mCategoryIndex);
        outState.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, mSubCategoryIndex);
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
        Log.i(TAG, "SHOW CATEGORIES");
        // Validate the current level
        switch (mCurrentCategoryLevel) {
        case NAVIGATION_CATEGORIES_LEVEL_1:
            Log.d(TAG, "CATEGORIES LEVEL 1");
            showCategoryLevel1();
            break;
        case NAVIGATION_CATEGORIES_LEVEL_2:
            Log.d(TAG, "CATEGORIES LEVEL 2");
            showCategoryLevel2();
            break;
        case NAVIGATION_CATEGORIES_LEVEL_3:
            Log.d(TAG, "CATEGORIES LEVEL 3");
            showCategoryLevel3();
            break;
        default:
            Log.w(TAG, "WARNING ON SHOW CATEGORY UNKNOWN LEVEL");
            // Show retry
            showRetry();
            break;
        }
        // Show content
        showContent();
    }
    
    /**
     * Show category level 1
     * @author sergiopereira
     */
    private void showCategoryLevel1() {
        mCategoryAdapter = new CategoriesAdapter(getActivity(), JumiaApplication.currentCategories);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setOnItemClickListener(this);
    }
    
    /**
     * Show category level 2
     * @author sergiopereira
     */
    private void showCategoryLevel2() {
        Log.i(TAG, "ON SHOW CAT LEVEL 2");
        //  Validate categories
        ArrayList<Category> mCategories = JumiaApplication.currentCategories;
        if( mCategories == null || mCategories.size() <= 0 || mCategoryIndex >= mCategories.size()) return;
        // Get data
        parent = mCategories;
        child = mCategories.get(mCategoryIndex).getChildren();
        currentCategory = parent.get(mCategoryIndex);
        String categoryName = currentCategory.getName();
        //Log.i(TAG, "CATEGORY: " + " " + categoryName);
        // Get header layout
        View headerForBack = createHeader(R.layout.category_inner_back, getString(R.string.categories)); 
        mCategoryList.addHeaderView(headerForBack);
        // Create and set adapter
        mSubCategoryAdapter = new SubCategoriesAdapter(getActivity(), child, categoryName);
        mCategoryList.setAdapter(mSubCategoryAdapter);
        // Set listener
        mCategoryList.setOnItemClickListener(this);
    }
    
    /**
     * Show category level 3
     * @author sergiopereira
     */
    private void showCategoryLevel3() {
        Log.i(TAG, "ON SHOW CAT LEVEL 3");
        // Valdiate categories
        ArrayList<Category> mCategories = JumiaApplication.currentCategories;
        if( mCategories == null || mCategories.size() <= 0 || mCategoryIndex >= mCategories.size()) return;
        // Get data
        parent = mCategories.get(mCategoryIndex).getChildren();
        child = mCategories.get(mCategoryIndex).getChildren().get(mSubCategoryIndex).getChildren();
        currentCategory = parent.get(mSubCategoryIndex);
        String categoryName = currentCategory.getName();
        //Log.i(TAG, "CATEGORY: " + " " + categoryName);
        // Create and add the header for back
        View headerForBack = createHeader(R.layout.category_inner_back, mCategories.get(mCategoryIndex).getName()); 
        mCategoryList.addHeaderView(headerForBack);
        // Set Adapter
        mSubCategoryAdapter = new SubCategoriesAdapter(getActivity(), child, categoryName);
        mCategoryList.setAdapter(mSubCategoryAdapter);
        // Set listener
        mCategoryList.setOnItemClickListener(this);
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
        switch (mCurrentCategoryLevel) {
        case NAVIGATION_CATEGORIES_LEVEL_1:
            // Validate item
            Category category = JumiaApplication.currentCategories.get(position);
            // Show product list
            if (!category.getHasChildren()) showProductList(category);
            // Show level 2
            else switchToCategoryLevel2(position);
            break;
        case NAVIGATION_CATEGORIES_LEVEL_2:
        case NAVIGATION_CATEGORIES_LEVEL_3:
            // First header goes to parent
            if (position == HEADER_FOR_BACK_POSITION) goToParentCategoryFromType(mCurrentCategoryLevel);
            // Second header goes to all
            else if (position == HEADER_FOR_ALL_POSITION) showProductList(currentCategory);
            // Validate item goes to product list or level 3
            else switchToSubCategoryOrShowProd(position - NUMBER_OF_HEADERS);
            break;
        default:
            Log.w(TAG, "WARNING: ITEM CLICKED ON UNKNOWN LEVEL");
            break;
        }
    }
    
    /**
     * Switch to categories level 2
     * @param position
     * @author sergiopereira
     */
    private void switchToCategoryLevel2(int position){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CategoriesContainerFragment.UPDATE_CHILD, true);
        bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, position);
        bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, 0);
        bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.NAVIGATION_CATEGORIES_LEVEL_2);
        ((NavigationFragment) getParentFragment()).onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_LEVEL_2, bundle);
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
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle2, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Switch to sub category or product list
     * @param pos
     * @author sergiopereira
     */
    private void switchToSubCategoryOrShowProd(int pos) {
        // This condition verifies if we are in the root of the category chosen
        Category selectedCategory = currentCategory.getChildren().get(pos);
        Log.d(TAG, "SELECTED CATEGORY: " + selectedCategory.getName());
        // Validate if exist level 3
        if (mCurrentCategoryLevel == FragmentType.NAVIGATION_CATEGORIES_LEVEL_2 && selectedCategory.getHasChildren()) {
            Log.d(TAG, "SELECTED CATEGORY HAS CHILDS: " + selectedCategory.getChildren().size());
            // Switch to level 3 
            Bundle bundle = new Bundle();
            bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, mCategoryIndex);
            bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, pos);
            bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.NAVIGATION_CATEGORIES_LEVEL_3);
            bundle.putBoolean(CategoriesContainerFragment.UPDATE_CHILD, true);
            ((NavigationFragment) getParentFragment()).onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_LEVEL_3, bundle);
        } else {
            Log.v(TAG, "SELECTED CATEGORY IS EMPTY: SHOW PRODUCTS");
            showProductList(selectedCategory);
        }
    }
    
    /**
     * Pop the back stack until the parent from current level type
     * @param type
     * @author sergiopereira
     */
    private void goToParentCategoryFromType(FragmentType type){
        Log.i(TAG, "GOTO PARENT LEVEL FROM: " + type.toString());
        switch (type) {
        case NAVIGATION_CATEGORIES_LEVEL_1:
        case NAVIGATION_CATEGORIES_LEVEL_2:
            ((NavigationFragment) getParentFragment()).goToBackUntil(FragmentType.NAVIGATION_CATEGORIES_LEVEL_1);
            break;
        case NAVIGATION_CATEGORIES_LEVEL_3:
            ((NavigationFragment) getParentFragment()).goToBackUntil(FragmentType.NAVIGATION_CATEGORIES_LEVEL_2);
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
