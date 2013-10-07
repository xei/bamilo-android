/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.SubCategoriesAdapter;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetCategoriesEvent;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.CategoriesFragmentActivity;
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

    private long beginRequestMillis;

    private String categoryUrl;

    private int categoryIndex;

    private int subCategoryIndex;

    private List<Category> parent;

    private ArrayList<Category> child;

    private Category currentCategory;

    private SubCategoriesAdapter subCatAdapter;

    /**
     * Get instance
     * 
     * @return
     */
    public static CategoriesFragment newInstance(String categoryUrl) {
        // return new CategoriesFragment();
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.categoryUrl = categoryUrl;
        return categoriesFragment;
        
    }

    /**
     * Empty constructor
     */
    public CategoriesFragment() {
        super(EnumSet.of(EventType.GET_CATEGORIES_EVENT), EnumSet.noneOf(EventType.class));
        this.setRetainInstance(true);
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
        beginRequestMillis = System.currentTimeMillis();
        
        categories = CategoriesFragmentActivity.currentCategories;
        categoryIndex = CategoriesFragmentActivity.selectedCategoryPosition;
        subCategoryIndex = CategoriesFragmentActivity.selectedSubCategoryPosition;

        //if(CategoriesFragmentActivity.currentFragment == FragmentType.CATEGORIES_LEVEL_1)
        //    categoryUrl = getActivity().getIntent().getStringExtra(ConstantsIntentExtra.CONTENT_URL);
        
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
//        if(CategoriesFragmentActivity.currentFragment == FragmentType.CATEGORIES_LEVEL_1) {
//            view = inflater.inflate(R.layout.categories, container, false);
//        }else{
            view = inflater.inflate(R.layout.categories_inner_container, container, false);
//        }
        
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
        ((BaseActivity) getActivity()).updateActivityHeader(NavigationAction.Categories, R.string.categories_title);
        
        if(categories != null && getView() != null){
            createList();
        } else {
            triggerContentEvent(new GetCategoriesEvent(categoryUrl));
        }
            
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
        EventManager.getSingleton().removeResponseListener(this, EnumSet.of(EventType.GET_CATEGORIES_EVENT));
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
        if(CategoriesFragmentActivity.currentFragment == FragmentType.CATEGORIES_LEVEL_1) {
            Category category = categories.get(position);
            if (!category.getHasChildren()) {
                showProducts(category);
            } else {
                CategoriesFragmentActivity.selectedCategoryPosition = position;
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CATEGORIES_LEVEL_2, true);
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

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onSuccessEvent(pt.rocket.framework.event.
     * ResponseResultEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        AnalyticsGoogle.get().trackLoadTiming(R.string.gcategories, beginRequestMillis);
        categories = (List<Category>) event.result;
        CategoriesFragmentActivity.currentCategories = categories;
        // Update accordion
        Log.d(TAG, "handleEvent: categories size = " + categories.size());
        createList();
        return true;
    }

    /**
     * Creates the list with all the categories
     */
    private void createList() {
        switch (CategoriesFragmentActivity.currentFragment) {
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
        //categoriesList.setExpanded(true);
        
        parent = categories;
        child = categories.get(categoryIndex).getChildren();
        currentCategory = parent.get(categoryIndex);
        
        Log.d(TAG, "setSubCategoryList: entering forward: categoryIndex = " + categoryIndex);
        
        String categoryTitle = currentCategory.getName();
        Log.d( TAG, "setSubCategoryList: currentCategory name = " + categoryTitle );
        getActivity().setTitle(categoryTitle);
        
        subCatAdapter = new SubCategoriesAdapter(getActivity(), child, categoryTitle);
        categoriesList.setAdapter(subCatAdapter);
        categoriesList.setOnItemClickListener(this);
    }
    
    /**
     * Category level 3
     */
    private void categoryLevel3() {
        categoriesList = (ListView) getView().findViewById(R.id.sub_categories_grid);
        //categoriesList.setExpanded(true);
        
        parent = categories.get(categoryIndex).getChildren();
        child = categories.get(categoryIndex).getChildren().get(subCategoryIndex).getChildren();
        currentCategory = parent.get(subCategoryIndex);
        
        Log.d(TAG, "setSubCategoryList: entering forward: categoryIndex = " + categoryIndex);
        
        String categoryTitle = currentCategory.getName();
        Log.d( TAG, "setSubCategoryList: currentCategory name = " + categoryTitle );
        getActivity().setTitle(categoryTitle);
        
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
        if(currentCategory != null && currentCategory.getChildren() !=null){
            Category selectedCategory = currentCategory.getChildren().get(pos);
            Log.d(TAG, "SELECTED CATEGORY: " + selectedCategory.getName());
            
            if (CategoriesFragmentActivity.currentFragment == FragmentType.CATEGORIES_LEVEL_2 && selectedCategory.getHasChildren()) {
                Log.d(TAG, "SELECTED CATEGORY HAS CHILDS: " + selectedCategory.getChildren().size());
                CategoriesFragmentActivity.selectedSubCategoryPosition = pos;
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CATEGORIES_LEVEL_3, true);
            } else {
                Log.v(TAG, "SELECTED CATEGORY IS EMPTY: SHOW PRODUCTS");
                showProducts(selectedCategory);
            }
        }
    } 
    
    /**
     * Show products
     * @param category
     */
    private void showProducts( Category category ) {


//        ActivitiesWorkFlow.productsActivity(getActivity(), category.getCategoryPath(), category.getName(), null, R.string.gcategory_prefix, category.getCategoryPath());

        ActivitiesWorkFlow.productsActivity(getActivity(), category.getApiUrl(), category.getName(), null, R.string.gcategory_prefix, category.getCategoryPath());
    }
    
}
