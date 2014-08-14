/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.SubCategoriesAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.helpers.categories.GetCategoriesPerLevelsHelper;
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
 * Classm used to shoe the categories in the navigation container
 * @author sergiopereira
 */
public class NavigationCategoryFragment extends BaseFragment implements OnItemClickListener, OnClickListener, IResponseCallback {

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
        // Get data
        if(bundle != null) {
            Log.i(TAG, "ON GET INSTANCE: SAVE DATA");
            categoriesFragment.mCategoryKey = bundle.getString(ConstantsIntentExtra.CATEGORY_ID);
        }
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
        // Validate saved state
        if(savedInstanceState != null) {
            Log.i(TAG, "ON LOAD SAVED STATE");
            mCategoryKey = savedInstanceState.getString(ConstantsIntentExtra.CATEGORY_ID);
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
        mCategoryList = (ListView) getView().findViewById(R.id.nav_sub_categories_grid);
        
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
        if (null != getActivity()) {
            CategoriesAdapter mCategoryAdapter = new CategoriesAdapter(getActivity(), categories);
            mCategoryList.setAdapter(mCategoryAdapter);
            mCategoryList.setOnItemClickListener(this);
        }
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
        showFragmentRetry((OnClickListener) this);
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
     * @param category key
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
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
        // Remove entries until Home
        FragmentController.getInstance().removeEntriesUntilTag(FragmentType.HOME.toString());
        // Goto Catalog
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);
        // Tracking category
        trackCategory(category.getName());
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
     * Process the click on retry
     * @author sergiopereira
     */
    private void onClickRetryButton(){
        Log.d(TAG, "ON CLICK RETRY");
        triggerGetCategories(mCategoryKey);
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
        if (isOnStoppingProcess) return;
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
        // Show retry
        showRetry();
    }
    
    /**
     * ####### TRACKING ####### 
     */
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
    
}
