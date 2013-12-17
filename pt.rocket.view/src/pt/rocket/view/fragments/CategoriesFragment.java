/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.List;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.SubCategoriesAdapter;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.FragmentCommunicator;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.MainFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CategoriesFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = LogTagHelper.create(CategoriesFragment.class);

    private List<Category> categories;

    private CategoriesAdapter mainCatAdapter;

    private ListView categoriesList;

    private int categoryIndex;

    private int subCategoryIndex;

    private List<Category> parent;

    private ArrayList<Category> child;

    private Category currentCategory;

    private SubCategoriesAdapter subCatAdapter;
    
    private FragmentType currentFragment = FragmentType.CATEGORIES_LEVEL_1;
    
    private boolean isParent = false;

    /**
     * Get instance
     * 
     * @return
     */
    public static CategoriesFragment newInstance(String categoryUrl) {
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        return categoriesFragment;
    }
    
    /**
     * 
     * @param bundle
     * @return
     */
    public static CategoriesFragment getInstance(Bundle bundle) {
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        // Get data
        if(bundle != null) {
            categoriesFragment.currentFragment = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL);
            if(categoriesFragment.currentFragment == null){
                categoriesFragment.currentFragment = FragmentType.CATEGORIES_LEVEL_1;
            }
                
            categoriesFragment.isParent = bundle.getBoolean(CategoriesContainerFragment.CATEGORY_PARENT);
            categoriesFragment.categoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
            categoriesFragment.subCategoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
        }
        return categoriesFragment;
    }

    /**
     * Empty constructor
     */
    public CategoriesFragment() {
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
        View view;
        view = inflater.inflate(R.layout.categories_inner_container, container, false);
        
        return view;
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
        
        createList();
//        ((BaseActivity) getActivity()).updateActivityHeader(NavigationAction.Categories, R.string.categories_title);
        
        
            
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

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     * android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // LEVEL 1
        if(currentFragment == FragmentType.CATEGORIES_LEVEL_1) {
            Log.i(TAG, "code1 requested level 1");
            Category category = categories.get(position);
            if (!category.getHasChildren()) {
                showProducts(category);
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean(CategoriesContainerFragment.UPDATE_CHILD, true);
                bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, position);
                bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, 0);
                bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
                
                FragmentCommunicator.getInstance().notifyTarget(this, bundle, 0);
                //((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CATEGORIES_LEVEL_2, bundle, true);
            }
        } // LEVEL 2 or 3
        else {
            if (position == 0) {
                showProducts(currentCategory);
            } else {
                requestSubcategory(position - 1);
            }
        }
    }

    

    /**
     * Creates the list with all the categories
     */
    private void createList() {
        if(categories == null){
            categories = MainFragmentActivity.currentCategories;
        }
        
        if(categories == null)
            return;
        // Validate the current level
        switch (currentFragment) {
        case CATEGORIES_LEVEL_1:
            Log.d(TAG, "CATEGORIES LEVEL 1");
            categoryLevel1();
            break;
        case CATEGORIES_LEVEL_2:
            Log.d(TAG, "CATEGORIES LEVEL 2");
            categoryLevel2();
            break;
        case CATEGORIES_LEVEL_3:
            Log.d(TAG, "CATEGORIES LEVEL 3");
            categoryLevel3();
            break;
        }
    }
    
    /**
     * Category level 1
     */
    private void categoryLevel1() {
        if(getView() == null){
            getActivity().finish();
            return;
        }
        categoriesList = (ListView) getView().findViewById(R.id.sub_categories_grid);
        //categoriesList.setExpanded(true);
        
        mainCatAdapter = new CategoriesAdapter(getActivity(), categories);
        categoriesList.setAdapter(mainCatAdapter);
        categoriesList.setOnItemClickListener(this);
    }
    
    /**
     * Category level 2
     */
    private void categoryLevel2() {
        categoriesList = (ListView) getView().findViewById(R.id.sub_categories_grid);
        
        parent = categories;
        child = categories.get(categoryIndex).getChildren();
        currentCategory = parent.get(categoryIndex);
        
        Log.d(TAG, "setSubCategoryList: entering forward: categoryIndex = " + categoryIndex);
        
        String categoryTitle = currentCategory.getName();
        Log.d( TAG, "setSubCategoryList: currentCategory name = " + categoryTitle );
        getActivity().setTitle(categoryTitle);
        if(isParent){
            TrackerDelegator.trackCategoryView(getActivity(), categoryTitle, 1);
        }
            
        subCatAdapter = new SubCategoriesAdapter(getActivity(), child, categoryTitle);
        categoriesList.setAdapter(subCatAdapter);
        categoriesList.setOnItemClickListener(this);
    }
    
    /**
     * Category level 3
     */
    private void categoryLevel3() {
        categoriesList = (ListView) getView().findViewById(R.id.sub_categories_grid);
        
        parent = categories.get(categoryIndex).getChildren();
        child = categories.get(categoryIndex).getChildren().get(subCategoryIndex).getChildren();
        currentCategory = parent.get(subCategoryIndex);
        
        Log.d(TAG, "setSubCategoryList: entering forward: categoryIndex = " + categoryIndex);
        
        String categoryTitle = currentCategory.getName();
        Log.d( TAG, "setSubCategoryList: currentCategory name = " + categoryTitle );
        getActivity().setTitle(categories.get(categoryIndex).getName() + " / "+categoryTitle);
        if(isParent){
            TrackerDelegator.trackCategoryView(getActivity(), categoryTitle, 1);
        }
        subCatAdapter = new SubCategoriesAdapter(getActivity(), child, categoryTitle);
        categoriesList.setAdapter(subCatAdapter);
        categoriesList.setOnItemClickListener(this);
        
    }
    
    /**
     * Request sub category
     * @param pos
     */
    private void requestSubcategory(int pos) {
        // This condition verifies if we are in the root of the category chosen
        Category selectedCategory = currentCategory.getChildren().get(pos);
        Log.d(TAG, "SELECTED CATEGORY: " + selectedCategory.getName());
        
        // Validate if exist level 3
        if (currentFragment == FragmentType.CATEGORIES_LEVEL_2 && selectedCategory.getHasChildren()) {
            Log.d(TAG, "SELECTED CATEGORY HAS CHILDS: " + selectedCategory.getChildren().size());
            // Switch to level 3 
            Bundle bundle = new Bundle();
            if(isParent){
                bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, categoryIndex);
                bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, pos);
                bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_3);
                bundle.putBoolean(CategoriesContainerFragment.UPDATE_CHILD, true);
            } else {
                bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, categoryIndex);
                bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, pos);
                bundle.putSerializable(CategoriesContainerFragment.PARENT_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
                bundle.putSerializable(CategoriesContainerFragment.CHILD_LEVEL, FragmentType.CATEGORIES_LEVEL_3);
                bundle.putBoolean(CategoriesContainerFragment.UPDATE_BOTH, true);
            }
            FragmentCommunicator.getInstance().notifyTarget(this, bundle, 0);
        } else {
            Log.v(TAG, "SELECTED CATEGORY IS EMPTY: SHOW PRODUCTS");
            showProducts(selectedCategory);
        }
    } 
    
    /**
     * Show products
     * @param category
     */
    private void showProducts( Category category ) {
       
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean(CategoriesContainerFragment.REMOVE_FRAGMENTS, true);
        bundle2.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
        bundle2.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
        bundle2.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        bundle2.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        bundle2.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
        
        FragmentCommunicator.getInstance().notifyTarget(this, bundle2, 0);
    }

    @Override
    public void notifyFragment(Bundle bundle) {
        
        if(bundle.containsKey(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX))
            subCategoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
        else
            subCategoryIndex = 0;
        
        if(bundle.containsKey(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX))
            categoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
        
        if(bundle.containsKey(ConstantsIntentExtra.CATEGORY_LEVEL))
            currentFragment = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL);
        
        Log.i(TAG, "subCategoryIndex : "+subCategoryIndex + " categoryIndex : "+categoryIndex);
        
        categories = MainFragmentActivity.currentCategories;
        
        if(categories != null && getView() != null){
            createList();
        } else {
           Bundle args = new Bundle();
           args.putBoolean(CategoriesContainerFragment.GET_CATEGORIES, true);
           FragmentCommunicator.getInstance().notifyTarget(this, args, 0);
        }
        
        
    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }
    
}
