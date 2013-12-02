/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.SubCategoriesAdapter;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetCategoriesEvent;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.FragmentCommunicator;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import pt.rocket.view.MainFragmentActivity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CategoriesContainerFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(CategoriesContainerFragment.class);
    
    private CategoriesAdapter mainCatAdapter;

    private ListView categoriesList;

    private long beginRequestMillis;

    private String categoryUrl;

    private int categoryIndex;

    private int subCategoryIndex;
    
    public FragmentType currentFragment = FragmentType.CATEGORIES_LEVEL_1;
    
    public static String CATEGORY_PARENT ="category_parent";
    public static String UPDATE_CHILD ="update_child";
    public static String UPDATE_BOTH ="update_both";
    public static String CHILD_LEVEL ="child_level";
    public static String PARENT_LEVEL ="parent_level";
    public static String GET_CATEGORIES ="get_categories";
    
    private static Fragment mCategoriesFragment;

    private static Fragment mChildCategoriesFragment;
    /**
     * Get instance
     * 
     * @return
     */
    public static CategoriesContainerFragment newInstance(String categoryUrl) {
        CategoriesContainerFragment categoriesFragment = new CategoriesContainerFragment();
        categoriesFragment.categoryUrl = categoryUrl;
        return categoriesFragment;
    }
    
    /**
     * 
     * @param bundle
     * @return
     */
    public static CategoriesContainerFragment getInstance(Bundle bundle) {
        // return new CategoriesFragment();
        CategoriesContainerFragment categoriesFragment = new CategoriesContainerFragment();
        // Get data
        if(bundle != null) {
            categoriesFragment.currentFragment = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL);
            if(categoriesFragment.currentFragment == null)
                categoriesFragment.currentFragment = FragmentType.CATEGORIES_LEVEL_1;
            categoriesFragment.categoryUrl = bundle.getString(ConstantsIntentExtra.CATEGORY_URL);
            categoriesFragment.categoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
            categoriesFragment.subCategoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
        }
        return categoriesFragment;
    }

    /**
     * Empty constructor
     */
    public CategoriesContainerFragment() {
        super(EnumSet.of(EventType.GET_CATEGORIES_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH), 
                NavigationAction.Categories, 
                R.string.categories_title);
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
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
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
        view = inflater.inflate(R.layout.categories_fragments, container, false);
        
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
        
        if(MainFragmentActivity.currentCategories != null && getView() != null){
            if(((BaseActivity) getActivity()).isTabletInLandscape()){
                createFragmentsForLandscape();
            } else {
                createFragment();
            }
        } else if(getView() != null) {
            triggerContentEventWithNoLoading(new GetCategoriesEvent(categoryUrl));
        } else {
            ((BaseActivity) getActivity()).onBackPressed();
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
        removeOldFragments();
        FragmentCommunicator.getInstance().destroyInstance();
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
    
    
    @Override
    public boolean allowBackPressed() {
        
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onSuccessEvent(pt.rocket.framework.event.
     * ResponseResultEvent)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // Validate if fragment is on the screen
        if(isVisible()) {
            AnalyticsGoogle.get().trackLoadTiming(R.string.gcategories, beginRequestMillis);
            MainFragmentActivity.currentCategories = (List<Category>) event.result;
            
            if(MainFragmentActivity.currentCategories != null && getView() != null){
                Log.d(TAG, "handleEvent: categories size = " + MainFragmentActivity.currentCategories.size());
                if(((BaseActivity) getActivity()).isTabletInLandscape()){
                    createFragmentsForLandscape();
                } else {
                    createFragment();
                }
            }
        }
        return true;
    }

    /**
     * Creates the list with all the categories
     */
    private void createFragment() {
        Bundle args = new Bundle();
        currentFragment = FragmentType.CATEGORIES_LEVEL_1;
        args.putString(ConstantsIntentExtra.CATEGORY_URL, categoryUrl);
        args.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, categoryIndex);
        args.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, subCategoryIndex);
        args.putBoolean(CATEGORY_PARENT, true);
        mCategoriesFragment = CategoriesFragment.getInstance(args);
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.categories_fragments_container, mCategoriesFragment);
        ft.commit();
    }
    
    /**
     * Creates the list with all the categories
     */
    private void createFragmentsForLandscape() {
       
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.CATEGORY_URL, categoryUrl);
        args.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, categoryIndex);
        args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
        args.putBoolean(CATEGORY_PARENT, true);
        mCategoriesFragment = CategoriesFragment.getInstance(args);
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.categories_fragments_container, mCategoriesFragment);
        
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, 0);
        args.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, subCategoryIndex);
        bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
        bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
        bundle.putBoolean(CATEGORY_PARENT, false);
        mChildCategoriesFragment = CategoriesFragment.getInstance(bundle);
        ft.replace(R.id.categories_child_fragments_container, mChildCategoriesFragment);
        ft.commit();
        
        FragmentCommunicator.getInstance().startFragmentsCallBacks(this, mCategoriesFragment, mChildCategoriesFragment);
        
    }
    
    private void removeOldFragments(){
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();    
        if(mCategoriesFragment != null){
            ft.remove(mCategoriesFragment);
        }
        
        if(mChildCategoriesFragment != null){
            ft.remove(mChildCategoriesFragment);
        }
        ft.commit();
        mCategoriesFragment = null;
        mChildCategoriesFragment = null;
        fm = null;
        ft = null;
        
    }
    
    private void updateChild(Bundle bundle){
        FragmentCommunicator.getInstance().notifyTarget(this, bundle, 2);
    }
    
    private void updateBoth(Bundle bundle){

        Bundle bParent = new Bundle();
        bParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, (FragmentType) bundle.getSerializable(PARENT_LEVEL));
        bParent.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX));
        bParent.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX));
        FragmentCommunicator.getInstance().notifyTarget(this, bParent, 1);
        
        Bundle bChild = new Bundle();
        bChild.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, (FragmentType) bundle.getSerializable(CHILD_LEVEL));
        bChild.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX));
        bChild.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX));
        FragmentCommunicator.getInstance().notifyTarget(this, bChild, 2);
    }
    
    @Override
    public void notifyFragment(Bundle bundle) {
        
        if(bundle.containsKey(GET_CATEGORIES)){
            triggerContentEventWithNoLoading(new GetCategoriesEvent(categoryUrl));
            return;
        }
        
        Log.i(TAG, "CATEGORY_LEVEL : "+(FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL));
        Log.i(TAG, "SELECTED_SUB_CATEGORY_INDEX : "+bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX));
        
        if(bundle.containsKey(UPDATE_CHILD)){
            updateChild(bundle);
        } else if(bundle.containsKey(UPDATE_BOTH)){
            updateBoth(bundle);
        }
    }
    
}
