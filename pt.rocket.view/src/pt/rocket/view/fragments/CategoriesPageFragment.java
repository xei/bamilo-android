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
import pt.rocket.helpers.categories.GetCategoriesHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.TrackerDelegator;
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
    
    private static final int ROOT_CATEGORIES_LEVEL = 0;
    
    private static final int NO_LANDSCAPE_SELECETD_CATEGORY = 0;
    
    private static final int CLICK_FROM_DEFAULT_CONTAINER = 0;
    
    private static final int CLICK_FROM_LANDSCAPE_CONTAINER = 1;

    private ListView mCategoryList;

    private Category currentCategory;
    
    private CategoriesAdapter mCategoryAdapter;

    private SubCategoriesAdapter mSubCategoryAdapter;

    private LayoutInflater mInflater;

    private ArrayList<Integer> mTreePath;

    private String mParentCategoryName;

    private ListView mLandscapeCategoryChildrenList;

    private int mLandscapeParentPosition;

    private String mTitleCategory;
    
    /**
     * Create a new instance and save the bundle data
     * @param bundle
     * @return NavigationCategoryFragment
     * @author sergiopereira
     */
    public static CategoriesPageFragment getInstance(Bundle bundle) {
        CategoriesPageFragment categoriesFragment = new CategoriesPageFragment();
        // Get data
        if(bundle != null) {
            Log.i(TAG, "ON GET INSTANCE: SAVE BUNDLE DATA");
            // Sava data
            categoriesFragment.mTreePath = bundle.getIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH);
            categoriesFragment.mParentCategoryName = bundle.getString(ConstantsIntentExtra.CATEGORY_PARENT_NAME);
            categoriesFragment.mLandscapeParentPosition = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
            categoriesFragment.mTitleCategory = bundle.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
            // Validate path
            if(categoriesFragment.mTreePath == null) categoriesFragment.mTreePath = new ArrayList<Integer>();
        }
        return categoriesFragment;
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
        // Validate saved state
        if(savedInstanceState != null) {
            mTreePath = savedInstanceState.getIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH);
            mParentCategoryName = savedInstanceState.getString(ConstantsIntentExtra.CATEGORY_PARENT_NAME);
            mLandscapeParentPosition = savedInstanceState.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
            mTitleCategory = savedInstanceState.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
            Log.i(TAG, "ON LOAD SAVED STATE: " + mParentCategoryName + " " + mLandscapeParentPosition);
        } else {
            Log.i(TAG, "ON CURRENT STATE: " + mParentCategoryName + " " + mLandscapeParentPosition);
        }
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
        mCategoryList = (ListView) getView().findViewById(R.id.sub_categories_grid);
        // Get child category list view (landscape)
        mLandscapeCategoryChildrenList = (ListView) getView().findViewById(R.id.sub_categories_grid_right); 
        // Validate the cache
        if (JumiaApplication.currentCategories != null) 
        	showCategoryList();
        else if(!TextUtils.isEmpty(ShopSelector.getShopId())) 
        	triggerGetCategories();
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
        outState.putIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH, mTreePath);
        outState.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, mParentCategoryName);
        outState.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, mLandscapeParentPosition);
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
        showFragmentContentContainer();
    }
        
    /**
     * Show the root categories without headers
     * @author sergiopereira
     */
    private void showRootCategories() {
        Log.i(TAG, "ON SHOW ROOT CATEGORIES");
        // Container
        mCategoryAdapter = new CategoriesAdapter(getActivity(), JumiaApplication.currentCategories);
        mCategoryList.setTag(CLICK_FROM_DEFAULT_CONTAINER);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setOnItemClickListener(this);
        // Validate and fill the landscape container
        if(isPresentLandscapeContainer()) showChildrenInLandscape(JumiaApplication.currentCategories.get(mLandscapeParentPosition), mLandscapeParentPosition);
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
            // Set Adapter
            mSubCategoryAdapter = new SubCategoriesAdapter(getBaseActivity(), child, categoryName);
            mCategoryList.setTag(CLICK_FROM_DEFAULT_CONTAINER);
            mCategoryList.setAdapter(mSubCategoryAdapter);
            // Set listener
            mCategoryList.setOnItemClickListener(this);
            // Validate and fill the landscape container
            if(isPresentLandscapeContainer()) showChildrenInLandscape(child.get(mLandscapeParentPosition), mLandscapeParentPosition);
            
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
            goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING IOE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
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
        // Log
        if(category == null) Log.i(TAG, "NOT FOUND CURRENT CATEGORY");
        // Return category
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
     * Show only the retry view
     * @author sergiopereira
     */
    private void showRetry() {
        showFragmentRetry((OnClickListener) this);
    }
    
    /**
     * Return the tree level
     * @return int
     * @author sergiopereira
     */
    private int getTreeCategoryLevel(){
        return mTreePath != null ? mTreePath.size() : 0;
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
    private void showChildrenInLandscape(Category category, int parentPos) {
        mLandscapeParentPosition = parentPos;
        Log.d(TAG, "SHOW PARENT POSITION: " + mLandscapeParentPosition);
        // Create and add the header for back
        if(mLandscapeCategoryChildrenList.getHeaderViewsCount() == 0 && getTreeCategoryLevel() != ROOT_CATEGORIES_LEVEL) {
            View headerForBack = createHeader(R.layout.category_inner_top_back, getString(R.string.back_label));
            mLandscapeCategoryChildrenList.addHeaderView(headerForBack);
        }
        // Set Adapter
        SubCategoriesAdapter adapter = new SubCategoriesAdapter(getBaseActivity(), category.getChildren(), category.getName());
        mLandscapeCategoryChildrenList.setAdapter(adapter);
        mLandscapeCategoryChildrenList.setTag(CLICK_FROM_LANDSCAPE_CONTAINER);
        mLandscapeCategoryChildrenList.setOnItemClickListener((OnItemClickListener) this);
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
        // Get categories
        Bundle bundle = new Bundle();
        bundle.putString(GetCategoriesHelper.CATEGORY_URL, null);
        triggerContentEvent(new GetCategoriesHelper(), bundle, this);    
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
        if (id == R.id.fragment_root_retry_button) onClickRetryButton();
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
        Log.d(TAG, "ON ITEM CLICKED: " + position + " TAG:" + parent.getTag());
        try {
            // Get tag
            int from = (Integer) parent.getTag();
            // Validation
            switch (from) {
            case CLICK_FROM_DEFAULT_CONTAINER:
                Log.i(TAG, "CLICK_FROM_DEFAULT_CONTAINER");
                // Case root level
                if(getTreeCategoryLevel() == ROOT_CATEGORIES_LEVEL) onClickRootCategory(position);
                // Case other level
                else onClickNestedCategory(position);
                break;
            case CLICK_FROM_LANDSCAPE_CONTAINER:
                Log.i(TAG, "CLICK_FROM_LANDSCAPE_CONTAINER");
                // Case root level, landscape without back button
                if(getTreeCategoryLevel() == ROOT_CATEGORIES_LEVEL) onClickSubCategoryWithoutBack(parent, position);
                // Case other level, landscape with back button
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
     */
    private void onClickRootCategory(int position) {
        try {
            // Validate item
            Category category = JumiaApplication.currentCategories.get(position);
            // Show product list
            if (!category.getHasChildren()) gotoCatalog(category);
            // Show sub level in landscape container
            else if (isPresentLandscapeContainer()) showChildrenInLandscape(category, position);
            // Show sub level
            else gotoNestedCategories(category.getName(), position, NO_LANDSCAPE_SELECETD_CATEGORY);
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
    private void onClickNestedCategory(int position) {
        try {
            switch (position) {
            case HEADER_FOR_ALL_POSITION:
                // Second header goes to all
                gotoCatalog(currentCategory);
                break;
            default:
                // Get real position
                int pos = position - NUMBER_OF_HEADERS;
                // Validate item goes to product list or a sub level
                Category selectedCategory = currentCategory.getChildren().get(pos);
                // Show product list
                if (!selectedCategory.getHasChildren()) gotoCatalog(selectedCategory);
                // Show sub level in landscape container
                else if (isPresentLandscapeContainer()) showChildrenInLandscape(selectedCategory, pos);
                // Show sub level
                else gotoNestedCategories(selectedCategory.getName(), pos , NO_LANDSCAPE_SELECETD_CATEGORY);
                break;
            }
        } catch (Exception e) {
            Log.w(TAG, "WARNING NPE ON CLICK NESTED CATEGORY POS: " + position);
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
        switch (position) {
        case HEADER_FOR_ALL_POSITION:
            // Second header goes to all
            gotoCatalog(JumiaApplication.currentCategories.get(mLandscapeParentPosition));
            break;
        default:
            // Get real position
            int pos = position - NUMBER_OF_HEADERS;
            // Validate item goes to product list or a sub level
            Category selectedCategory = (Category) parent.getAdapter().getItem(position);
            // Show product list
            if (!selectedCategory.getHasChildren()) gotoCatalog(selectedCategory);
            // Show sub level
            else gotoNestedCategories(selectedCategory.getName(), mLandscapeParentPosition, pos);
            break;
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
        switch (position) {
        case HEADER_FOR_BACK_POSITION_LANDSCAPE:
            // First header goes to parent
            goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL);
            break;
        case HEADER_FOR_ALL_POSITION_LANDSCAPE:
            // Second header goes to all
            gotoCatalog(currentCategory.getChildren().get(mLandscapeParentPosition));
            break;
        default:
            // Get real position
            int pos = position - NUMBER_OF_HEADERS_LANDSCAPE;
            // Validate item goes to product list or a sub level
            Category selectedCategory = (Category) parent.getAdapter().getItem(position);
            // Show product list
            if (!selectedCategory.getHasChildren()) gotoCatalog(selectedCategory);
            // Show sub level
            else gotoNestedCategories(selectedCategory.getName(), mLandscapeParentPosition, pos);
            break;
        }
    }
    
    /**
     * Show nested categories with title name
     * @param name
     * @param selectedCategoryPosition
     * @param landscapeCategoryPosition
     * @author sergiopereira 
     */
    @SuppressWarnings("unchecked")
    private void gotoNestedCategories(String name, int selectedCategoryPosition, int landscapeCategoryPosition){
        ArrayList<Integer> newTreePath = (ArrayList<Integer>) mTreePath.clone();
        // Add selected item
        newTreePath.add(selectedCategoryPosition);
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ConstantsIntentExtra.CATEGORY_TREE_PATH, newTreePath);
        bundle.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, name);
        bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, landscapeCategoryPosition);
        String treeName = (TextUtils.isEmpty(mTitleCategory))? name : mTitleCategory + "/" + name;
        bundle.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME,  treeName);
        // Switch
        ((CategoriesColletionFragment) getParentFragment()).onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL, bundle);
    }
    
    /**
     * Show product list
     * @param category
     * @author sergiopereira
     */
    private void gotoCatalog(Category category) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
        // Goto Catalog
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);
        // Tracking category
        trackCategory(category.getName());
    }
    
    /**
     * Tracking category
     * @param name
     */
    private void trackCategory(String name){
        Bundle params = new Bundle();
        params.putString(TrackerDelegator.CATEGORY_KEY, name);
        params.putInt(TrackerDelegator.PAGE_NUMBER_KEY, 1);
        params.putString(TrackerDelegator.LOCATION_KEY, getString(R.string.gnavigation));
        TrackerDelegator.trackCategoryView(params);
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
            ((CategoriesColletionFragment) getParentFragment()).goToBackUntil(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
            break;
        case NAVIGATION_CATEGORIES_SUB_LEVEL:
            ((CategoriesColletionFragment) getParentFragment()).goToParentCategory();
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
        JumiaApplication.currentCategories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
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
        // Validate fragment state
        if(isOnStoppingProcess) return;
        // Generic errors
        if(getBaseActivity().handleErrorEvent(bundle)) return;
        // Show retry
        showRetry();
    }
    
}
