/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetCategoriesHelper;
import pt.rocket.helpers.GetSearchCategoryHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.FragmentCommunicator;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CategoriesContainerFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(CategoriesContainerFragment.class);

    private static final String USED_CACHED_CATEGORIES = "used_cached";
    
    private CategoriesAdapter mainCatAdapter;

    private ListView categoriesList;

    private long mBeginRequestMillis;

    private String categoryUrl;

    private int categoryIndex;

    private int subCategoryIndex;
    
    private FragmentType currentFragment = FragmentType.CATEGORIES_LEVEL_1;
    private FragmentType childCurrentFragment = FragmentType.CATEGORIES_LEVEL_2;
    
    public static String CATEGORY_PARENT ="category_parent";
    public static String UPDATE_CHILD ="update_child";
    public static String UPDATE_BOTH ="update_both";
    public static String CHILD_LEVEL ="child_level";
    public static String PARENT_LEVEL ="parent_level";
    public static String GET_CATEGORIES ="get_categories";
    public static String PORTRAIT_MODE ="portrait_mode";
    public static String REMOVE_FRAGMENTS ="remove_fragments";
    
    private static Fragment mCategoriesFragment;

    private static Fragment mChildCategoriesFragment;
    
    RelativeLayout backLevelButton;
    private static CategoriesContainerFragment categoriesFragment;
    
    SharedPreferences sharedPrefs;
    int selectedCategory;
    int selectedSubCategory;

    private String mDeepLinkCategoryId;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static CategoriesContainerFragment newInstance(String categoryUrl) {
        if(categoriesFragment == null)
            categoriesFragment = new CategoriesContainerFragment();
        categoriesFragment.categoryUrl = categoryUrl;
        return categoriesFragment;
    }
    
    /**
     * 
     * @param bundle
     * @return
     */
    public static CategoriesContainerFragment getInstance(Bundle bundle) {
        
        if(categoriesFragment == null)
            categoriesFragment = new CategoriesContainerFragment();
        // Get data
        if(bundle != null) {
            if(bundle.containsKey(ConstantsIntentExtra.CATEGORY_LEVEL)){
                categoriesFragment.currentFragment = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL);
                
                if(categoriesFragment.currentFragment == null){
                    categoriesFragment.currentFragment = FragmentType.CATEGORIES_LEVEL_1;
                    categoriesFragment.childCurrentFragment = FragmentType.CATEGORIES_LEVEL_2;
                }
                
            }
            
            if(bundle.containsKey(ConstantsIntentExtra.CATEGORY_URL)){
                categoriesFragment.categoryUrl = bundle.getString(ConstantsIntentExtra.CATEGORY_URL);
            }
                
            if(bundle.containsKey(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX)){
                categoriesFragment.categoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
                categoriesFragment.selectedCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
            }
                
            if(bundle.containsKey(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX)){
                categoriesFragment.subCategoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
                categoriesFragment.selectedSubCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
            }
            
            // Clean the deep link mode
            if(categoriesFragment.mDeepLinkCategoryId != null) {
                Log.d(TAG, "IS ON DEEP LINK MODE");
                categoriesFragment.mDeepLinkCategoryId = null;
                categoriesFragment.clean();
            }
            
            // Validate if is in deep mode
            if(bundle.containsKey(ConstantsIntentExtra.CATEGORY_ID)) {
                categoriesFragment.mDeepLinkCategoryId = bundle.getString(ConstantsIntentExtra.CATEGORY_ID);
                Log.i(TAG, "DEEP LINK: CATEGORY ID " + categoriesFragment.mDeepLinkCategoryId);
                categoriesFragment.clean();
            }
                
        }
        return categoriesFragment;
    }

    /**
     * Empty constructor
     */
    public CategoriesContainerFragment() {
        super(EnumSet.of(EventType.GET_CATEGORIES_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW), 
                NavigationAction.Categories, 
                0, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        sharedPrefs = getBaseActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
        if(JumiaApplication.INSTANCE.currentCategories != null && getView() != null){
//            Log.i(TAG, "ON currentCategories != null");
            if(((BaseActivity) getActivity()).isTabletInLandscape(getBaseActivity())){
//                Log.i(TAG, "ON createFragmentsForLandscape != null");
                createFragmentsForLandscape();
            } else { 
//                Log.i(TAG, "ON createFragment != null");
                createFragment();
            }
            
        } else if(getView() != null) {
            mBeginRequestMillis = System.currentTimeMillis();
//            Log.i(TAG, "ON trigger(categoryUrl); "+categoryUrl);
            
            // Validate the received data
            if(mDeepLinkCategoryId != null)
                triggerSearchCategory(mDeepLinkCategoryId);
            else 
                trigger(categoryUrl);
            
        } else {
//            Log.i(TAG, "ON tonBackPressed");
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
        Log.i(TAG, "ON PAUSE");
        // if any fragment is active, remove it.
        if(mCategoriesFragment != null || mChildCategoriesFragment != null)
            removeOldFragments();
        FragmentCommunicator.getInstance().destroyInstance();
        super.onPause();
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
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        // Clean the deep link mode
        if(mDeepLinkCategoryId != null) {
            mDeepLinkCategoryId = null;
            clean();
        }
    }
    
    private FragmentType getFragmentType(String type){
       if(FragmentType.CATEGORIES_LEVEL_1.toString().equalsIgnoreCase(type)) {
           return FragmentType.CATEGORIES_LEVEL_1;
       } else if(FragmentType.CATEGORIES_LEVEL_2.toString().equalsIgnoreCase(type)) {
           return FragmentType.CATEGORIES_LEVEL_2;
       } else if(FragmentType.UNKNOWN.toString().equalsIgnoreCase(type)) {
           return FragmentType.UNKNOWN;
       } else {
           return FragmentType.CATEGORIES_LEVEL_3;
       }
        
    }
    
    
    @Override
    public boolean allowBackPressed() {
        if(getActivity() == null){
            return false;
        }
        if(!((BaseActivity) getActivity()).isTabletInLandscape(getBaseActivity())){
            if(currentFragment == FragmentType.CATEGORIES_LEVEL_3){
                currentFragment = FragmentType.CATEGORIES_LEVEL_2;
                childCurrentFragment = FragmentType.UNKNOWN;
                Bundle bundleParent = new Bundle(); 
                bundleParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
                FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleParent, 1);
                saveState();
                return true;
            } else if(currentFragment == FragmentType.CATEGORIES_LEVEL_2){
                currentFragment = FragmentType.CATEGORIES_LEVEL_1;
                childCurrentFragment = FragmentType.UNKNOWN;
                Bundle bundleParent = new Bundle(); 
                bundleParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
                FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleParent, 1);
                saveState();
                return true;
            }
            return false;
        }
        
        if(backLevelButton != null && backLevelButton.getVisibility() == View.VISIBLE){
            Bundle bundleParent = new Bundle(); 
            bundleParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
            FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleParent, 1);
            currentFragment = FragmentType.CATEGORIES_LEVEL_1;
            childCurrentFragment = FragmentType.CATEGORIES_LEVEL_2;
            Bundle bundleChild = new Bundle();
            bundleChild.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
            FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleChild, 2);
            
            updateBackLevelButtonVisibility(false);
            saveState();
            return true;
        } 
        return false;
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        Log.i(TAG, "onSuccessEvent");
        if(getBaseActivity() != null){
            getBaseActivity().handleSuccessEvent(bundle);
        } else {
            return true;
        }
        
        // Validate if fragment is on the screen
    
        if(!bundle.getBoolean(USED_CACHED_CATEGORIES, false)){
            AnalyticsGoogle.get().trackLoadTiming(R.string.gcategories, mBeginRequestMillis);
        } else {
            Log.i(TAG, "code1 received categories from database "+JumiaApplication.currentCategories.size());
        }
        JumiaApplication.currentCategories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        
        if(JumiaApplication.currentCategories != null && getView() != null){
//            Log.d(TAG, "code1 received categories size = " + JumiaApplication.INSTANCE.currentCategories.size());
            if(getBaseActivity().isTabletInLandscape(getBaseActivity())){
//                Log.d(TAG, "code1 going to create fragment createFragmentsForLandscape");
                createFragmentsForLandscape();
            } else {
//                Log.d(TAG, "code1 going to create fragment");
                createFragment();
            }
        }
  
        return true;
    }

    
    public void onErrorEvent(Bundle bundle) {
        mBeginRequestMillis = System.currentTimeMillis();
        if(!isVisible()){
            return;
        }
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return;
        }
    }

    /**
     * Creates the list with all the categories
     */
    private void createFragment() {
        ((BaseActivity) getActivity()).setTitle(R.string.categories_title);
        Bundle args = new Bundle();
        
        if(sharedPrefs != null){
            currentFragment = getFragmentType(sharedPrefs.getString(ConstantsSharedPrefs.KEY_CURRENT_FRAGMENT, FragmentType.CATEGORIES_LEVEL_1.toString()));
            childCurrentFragment = getFragmentType(sharedPrefs.getString(ConstantsSharedPrefs.KEY_CHILD_CURRENT_FRAGMENT, FragmentType.UNKNOWN.toString()));
            selectedCategory = sharedPrefs.getInt(ConstantsSharedPrefs.KEY_CATEGORY_SELECTED, categoryIndex);
            selectedSubCategory = sharedPrefs.getInt(ConstantsSharedPrefs.KEY_SUB_CATEGORY_SELECTED, subCategoryIndex);
//            Log.i(TAG, "code1categories createFragments sharedPrefs: currentFragment "+currentFragment+" childCurrentFragment "+childCurrentFragment+ " selectedCategory: "+selectedCategory+" selectedSubCategory : "+selectedSubCategory);
        } else {
            currentFragment = FragmentType.CATEGORIES_LEVEL_1;
            childCurrentFragment = FragmentType.UNKNOWN;
            selectedCategory = categoryIndex;
            selectedSubCategory = subCategoryIndex;
//            Log.i(TAG, "code1categories createFragments : currentFragment "+currentFragment+" childCurrentFragment "+childCurrentFragment+ " selectedCategory: "+selectedCategory+" selectedSubCategory : "+selectedSubCategory);
        }
        
        args.putString(ConstantsIntentExtra.CATEGORY_URL, categoryUrl);
        args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, currentFragment);
        args.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, selectedCategory);
        args.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, selectedSubCategory);
        args.putBoolean(CATEGORY_PARENT, true);
        mCategoriesFragment = CategoriesFragment.getInstance(args);
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.categories_fragments_container, mCategoriesFragment);
        ft.commit();
        FragmentCommunicator.getInstance().startFragmentsCallBacks(this, mCategoriesFragment);
        
        getBaseActivity().showContentContainer();
        saveState();
    }
    
    /**
     * Creates the list with all the categories
     */
    private void createFragmentsForLandscape() {
        Log.i(TAG, "createFragmentsForLandscape : ");
        FragmentCommunicator.getInstance().destroyInstance();
        startButtonListener();
        if(sharedPrefs != null){
            currentFragment = getFragmentType(sharedPrefs.getString(ConstantsSharedPrefs.KEY_CURRENT_FRAGMENT, FragmentType.CATEGORIES_LEVEL_1.toString()));
            childCurrentFragment = getFragmentType(sharedPrefs.getString(ConstantsSharedPrefs.KEY_CHILD_CURRENT_FRAGMENT, FragmentType.CATEGORIES_LEVEL_2.toString()));
            if(childCurrentFragment == FragmentType.UNKNOWN){
                childCurrentFragment = currentFragment;
            }
            if(currentFragment == childCurrentFragment){
                if(childCurrentFragment == FragmentType.CATEGORIES_LEVEL_3){
                    currentFragment = FragmentType.CATEGORIES_LEVEL_2;
                } else if(currentFragment  == FragmentType.CATEGORIES_LEVEL_2) {
                    childCurrentFragment = FragmentType.CATEGORIES_LEVEL_3;
                } else {
                    childCurrentFragment = FragmentType.CATEGORIES_LEVEL_2;
                }
            }
            selectedCategory = sharedPrefs.getInt(ConstantsSharedPrefs.KEY_CATEGORY_SELECTED, 0);
            selectedSubCategory = sharedPrefs.getInt(ConstantsSharedPrefs.KEY_SUB_CATEGORY_SELECTED, 0);
//            Log.i(TAG, "code1categories createFragmentsForLandscape sharedPrefs: currentFragment "+currentFragment+" childCurrentFragment "+childCurrentFragment+ " selectedCategory: "+selectedCategory+" selectedSubCategory : "+selectedSubCategory);
        } else {
            currentFragment = FragmentType.CATEGORIES_LEVEL_1;
            childCurrentFragment = FragmentType.CATEGORIES_LEVEL_2;
            selectedCategory = 0;
            selectedSubCategory = 0;
//            Log.i(TAG, "code1categories createFragmentsForLandscape : currentFragment "+currentFragment+" childCurrentFragment "+childCurrentFragment+ " selectedCategory: "+selectedCategory+" selectedSubCategory : "+selectedSubCategory);
        }
        
        if(childCurrentFragment == FragmentType.CATEGORIES_LEVEL_3){
            updateBackLevelButtonVisibility(true);
        }
        
        
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.CATEGORY_URL, categoryUrl);
        args.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, selectedCategory);
        args.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, selectedSubCategory);
        args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, currentFragment);
        args.putBoolean(CATEGORY_PARENT, true);
        mCategoriesFragment = CategoriesFragment.getInstance(args);
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.categories_fragments_container, mCategoriesFragment);
        
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, selectedCategory);
        bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, selectedSubCategory);
        bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, childCurrentFragment);
        bundle.putBoolean(CATEGORY_PARENT, false);
        mChildCategoriesFragment = CategoriesFragment.getInstance(bundle);
        ft.replace(R.id.categories_child_fragments_container, mChildCategoriesFragment);
        ft.commit();
        
        FragmentCommunicator.getInstance().startFragmentsCallBacks(this, mCategoriesFragment, mChildCategoriesFragment);
        
        getBaseActivity().showContentContainer();
        saveState();
    }
    
    private void startButtonListener(){
        if(getView() != null){
            backLevelButton = (RelativeLayout) getView().findViewById(R.id.back_level_button);    
        }
        
        // Validate if is present
        if(backLevelButton == null) {
            Log.w(TAG, "THE BACK LEVEL BUTTON IS NOT PRESENT");
            return;
        }
        
        backLevelButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Bundle bundleParent = new Bundle(); 
                bundleParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
                FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleParent, 1);
                
                Bundle bundleChild = new Bundle();
                bundleChild.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
                FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleChild, 2);
                
                updateBackLevelButtonVisibility(false);
            }
        });
        
    }
    
    private void updateBackLevelButtonVisibility(boolean show){
        if(show){
            backLevelButton.setVisibility(View.VISIBLE);
        } else {
            backLevelButton.setVisibility(View.GONE);
        }
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
        FragmentCommunicator.getInstance().destroyInstance();
        fm = null;
        ft = null;
        
    }
    
    private void removeFragmentsAndOpenProducts(Bundle bundle){
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
        
        BaseActivity activity = (BaseActivity) getActivity();
        if ( null == activity ) {
            activity = mainActivity;
        }
        
        activity.onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, true);
    }
    
    
    private void updateFragment(Bundle bundle){
        bundle.putBoolean(PORTRAIT_MODE, true);
        currentFragment = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL);
        selectedCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
        selectedSubCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
        FragmentCommunicator.getInstance().notifyTarget(this, bundle, 1);
        saveState();
    }
    
    private void updateChild(Bundle bundle){
        selectedCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
        selectedSubCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
        FragmentCommunicator.getInstance().notifyTarget(this, bundle, 2);
        saveState();
    }
    
    private void updateBoth(Bundle bundle){
        selectedCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
        selectedSubCategory = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
        Bundle bParent = new Bundle();
        currentFragment = (FragmentType) bundle.getSerializable(PARENT_LEVEL);
        bParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, currentFragment);
        bParent.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, selectedCategory);
        bParent.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, selectedSubCategory);
        FragmentCommunicator.getInstance().notifyTarget(this, bParent, 1);
        
        childCurrentFragment = (FragmentType) bundle.getSerializable(CHILD_LEVEL);
        Bundle bChild = new Bundle();
        bChild.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, childCurrentFragment);
        bChild.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, selectedCategory);
        bChild.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, selectedSubCategory);
        FragmentCommunicator.getInstance().notifyTarget(this, bChild, 2);
        
        if((FragmentType) bundle.getSerializable(CHILD_LEVEL) == FragmentType.CATEGORIES_LEVEL_3){
            updateBackLevelButtonVisibility(true);
        }
        
        saveState();
    }
    
    public void saveState(){
        Editor eDitor = sharedPrefs.edit();
        eDitor.putInt(ConstantsSharedPrefs.KEY_CATEGORY_SELECTED, selectedCategory);
        eDitor.putInt(ConstantsSharedPrefs.KEY_SUB_CATEGORY_SELECTED, selectedSubCategory);
        eDitor.putString(ConstantsSharedPrefs.KEY_CURRENT_FRAGMENT, currentFragment.toString());
        eDitor.putString(ConstantsSharedPrefs.KEY_CHILD_CURRENT_FRAGMENT, childCurrentFragment.toString());
        eDitor.commit();
    }
    
    /**
     * Clean the current state for deep link
     * @author sergiopereira
     */
    private void clean(){
        Log.d(TAG, "DEEP LINK: CLEAN");
        // Clean current categories on Jumia application
        JumiaApplication.currentCategories = null;
        // Clean current saved data
        sharedPrefs = getBaseActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor eDitor = sharedPrefs.edit();
        eDitor.remove(ConstantsSharedPrefs.KEY_CATEGORY_SELECTED);
        eDitor.remove(ConstantsSharedPrefs.KEY_SUB_CATEGORY_SELECTED);
        eDitor.remove(ConstantsSharedPrefs.KEY_CURRENT_FRAGMENT);
        eDitor.remove(ConstantsSharedPrefs.KEY_CHILD_CURRENT_FRAGMENT);
        eDitor.commit();
        mCategoriesFragment = null;
        mChildCategoriesFragment = null;
        currentFragment = FragmentType.CATEGORIES_LEVEL_1;
        childCurrentFragment = FragmentType.CATEGORIES_LEVEL_2;
        backLevelButton = null;
        subCategoryIndex = 0;
        selectedSubCategory = 0;
    }
    
    @Override
    public void notifyFragment(Bundle bundle) {
        
        if(bundle.containsKey(REMOVE_FRAGMENTS)){
            removeFragmentsAndOpenProducts(bundle);
            
            return;
        }
        
        if(bundle.containsKey(GET_CATEGORIES)){
            mBeginRequestMillis = System.currentTimeMillis();
            
            /**
             * TRIGGERS
             * @author sergiopereira
             */
            trigger(categoryUrl);
            //triggerContentEvent(new GetCategoriesEvent(categoryUrl));
            
            return;
        }
        
//        Log.i(TAG, "CATEGORY_LEVEL : "+(FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL));
//        Log.i(TAG, "SELECTED_SUB_CATEGORY_INDEX : "+bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX));
        if(!((BaseActivity) getActivity()).isTabletInLandscape(getBaseActivity())){
            updateFragment(bundle);
        } else if(bundle.containsKey(UPDATE_CHILD)){
            updateChild(bundle);
        } else if(bundle.containsKey(UPDATE_BOTH)){
            updateBoth(bundle);
        }
    }
    
    
    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void trigger(String categoryUrl){
        Bundle bundle = new Bundle();
        bundle.putString(GetCategoriesHelper.CATEGORY_URL, categoryUrl);
        
        // TODO : Validate this
        //JumiaApplication.currentCategories = CategoriesTableHelper.getCategories();
        
        if(JumiaApplication.currentCategories != null && JumiaApplication.currentCategories.size() > 0){
            bundle.putBoolean(USED_CACHED_CATEGORIES, true);
            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_CATEGORIES_EVENT);
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.currentCategories); 
            onSuccessEvent(bundle);
        } else {
            triggerContentEvent(new GetCategoriesHelper(), bundle, mCallBack);    
        }
    }
    
    /**
     * Trigger to get the category via id.
     * The GetSearchCategoryHelper event is the same that the GetCategoriesHelper.
     * @author sergiopereira
     */
    private void triggerSearchCategory(String categoryId){
        Log.i(TAG, "DEEP LINK: TRIGGER GET CATEGORY " + categoryId);
        Bundle bundle = new Bundle();
        bundle.putString(GetSearchCategoryHelper.CATEGORY_TAG, categoryId);
        triggerContentEvent(new GetSearchCategoryHelper(), bundle, mCallBack);
    }
    
    /**
     * CALLBACK
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            handleErrorEvent(bundle);
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
            
        }
    };
    
}
