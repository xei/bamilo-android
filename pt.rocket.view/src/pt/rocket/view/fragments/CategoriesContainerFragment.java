/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.objects.ProductRatingPage;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetCategoriesHelper;
import pt.rocket.helpers.GetProductReviewsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.FragmentCommunicator;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.MainFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
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

    private static final String USED_CACHED_CATEGORIES = null;
    
    private CategoriesAdapter mainCatAdapter;

    private ListView categoriesList;

    private long mBeginRequestMillis;

    private String categoryUrl;

    private int categoryIndex;

    private int subCategoryIndex;
    
    private FragmentType currentFragment = FragmentType.CATEGORIES_LEVEL_1;
    
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
                }
                
            }
            
            if(bundle.containsKey(ConstantsIntentExtra.CATEGORY_URL)){
                categoriesFragment.categoryUrl = bundle.getString(ConstantsIntentExtra.CATEGORY_URL);
            }
                
            if(bundle.containsKey(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX)){
                categoriesFragment.categoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX);
            }
                
            if(bundle.containsKey(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX)){
                categoriesFragment.subCategoryIndex = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
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
                EnumSet.of(MyMenuItem.SEARCH), 
                NavigationAction.Categories, 
                0);
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
            mBeginRequestMillis = System.currentTimeMillis();
            
            /**
             * TRIGGERS
             * @author sergiopereira
             */
            trigger(categoryUrl);
            //triggerContentEvent(new GetCategoriesEvent(categoryUrl));
            
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
        Log.i(TAG, "ON DESTROY");
//        EventManager.getSingleton().removeResponseListener(this, EnumSet.of(EventType.GET_CATEGORIES_EVENT));
    }
    
    
    @Override
    public boolean allowBackPressed() {
        if(getActivity() == null){
            return false;
        }
        if(!((BaseActivity) getActivity()).isTabletInLandscape()){
            if(currentFragment == FragmentType.CATEGORIES_LEVEL_3){
                currentFragment = FragmentType.CATEGORIES_LEVEL_2;
                Bundle bundleParent = new Bundle(); 
                bundleParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
                FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleParent, 1);
                return true;
            } else if(currentFragment == FragmentType.CATEGORIES_LEVEL_2){
                currentFragment = FragmentType.CATEGORIES_LEVEL_1;
                Bundle bundleParent = new Bundle(); 
                bundleParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
                FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleParent, 1);
                return true;
            }
            return false;
        }
        
        if(backLevelButton != null && backLevelButton.getVisibility() == View.VISIBLE){
            Bundle bundleParent = new Bundle(); 
            bundleParent.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
            FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleParent, 1);
            
            Bundle bundleChild = new Bundle();
            bundleChild.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
            FragmentCommunicator.getInstance().notifyTarget(CategoriesContainerFragment.this, bundleChild, 2);
            
            updateBackLevelButtonVisibility(false);
            return true;
        } 
        return false;
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        getBaseActivity().handleSuccessEvent(bundle);
        Log.i(TAG, "code1 received categories");
        // Validate if fragment is on the screen
        if(isVisible()) {
            if(!bundle.getBoolean(USED_CACHED_CATEGORIES, false)){
                AnalyticsGoogle.get().trackLoadTiming(R.string.gcategories, mBeginRequestMillis);
            } else {
                Log.i(TAG, "code1 received categories from database"+MainFragmentActivity.currentCategories.size());
            }
            MainFragmentActivity.currentCategories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            
            if(MainFragmentActivity.currentCategories != null && getView() != null){
                Log.d(TAG, "code1 received categories size = " + MainFragmentActivity.currentCategories.size());
                if(getBaseActivity().isTabletInLandscape()){
                    Log.d(TAG, "code1 going to create fragment createFragmentsForLandscape");
                    createFragmentsForLandscape();
                } else {
                    Log.d(TAG, "code1 going to create fragment");
                    createFragment();
                }
            }
        }
        return true;
    }

    
    public void onErrorEvent(Bundle bundle) {
        mBeginRequestMillis = System.currentTimeMillis();
        getBaseActivity().handleErrorEvent(bundle);
    }

    /**
     * Creates the list with all the categories
     */
    private void createFragment() {
        ((BaseActivity) getActivity()).setTitle(R.string.categories_title);
        Bundle args = new Bundle();
        currentFragment = FragmentType.CATEGORIES_LEVEL_1;
        args.putString(ConstantsIntentExtra.CATEGORY_URL, categoryUrl);
        args.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, categoryIndex);
        args.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, subCategoryIndex);
        args.putBoolean(CATEGORY_PARENT, true);
        mCategoriesFragment = CategoriesFragment.getInstance(args);
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.categories_fragments_container, mCategoriesFragment);
        ft.commit();
        FragmentCommunicator.getInstance().startFragmentsCallBacks(this, mCategoriesFragment);
    }
    
    /**
     * Creates the list with all the categories
     */
    private void createFragmentsForLandscape() {
        FragmentCommunicator.getInstance().destroyInstance();
        startButtonListener();
        currentFragment = FragmentType.CATEGORIES_LEVEL_1;
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.CATEGORY_URL, categoryUrl);
        args.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, 0);
        args.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, 0);
        args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, currentFragment);
        args.putBoolean(CATEGORY_PARENT, true);
        mCategoriesFragment = CategoriesFragment.getInstance(args);
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.categories_fragments_container, mCategoriesFragment);
        
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.SELECTED_CATEGORY_INDEX, 0);
        bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, 0);
        bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_2);
        bundle.putBoolean(CATEGORY_PARENT, false);
        mChildCategoriesFragment = CategoriesFragment.getInstance(bundle);
        ft.replace(R.id.categories_child_fragments_container, mChildCategoriesFragment);
        ft.commit();
        
        FragmentCommunicator.getInstance().startFragmentsCallBacks(this, mCategoriesFragment, mChildCategoriesFragment);
        
    }
    
    private void startButtonListener(){
        if(getView() != null){
            backLevelButton = (RelativeLayout) getView().findViewById(R.id.back_level_button);    
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
        FragmentCommunicator.getInstance().notifyTarget(this, bundle, 1);
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
        
        if((FragmentType) bundle.getSerializable(CHILD_LEVEL) == FragmentType.CATEGORIES_LEVEL_3){
            updateBackLevelButtonVisibility(true);
        }
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
        
        Log.i(TAG, "CATEGORY_LEVEL : "+(FragmentType) bundle.getSerializable(ConstantsIntentExtra.CATEGORY_LEVEL));
        Log.i(TAG, "SELECTED_SUB_CATEGORY_INDEX : "+bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX));
        if(!((BaseActivity) getActivity()).isTabletInLandscape()){
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
        MainFragmentActivity.currentCategories = CategoriesTableHelper.getCategories();
        if(MainFragmentActivity.currentCategories != null && MainFragmentActivity.currentCategories.size() > 0){
            bundle.putBoolean(USED_CACHED_CATEGORIES, true);
            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, MainFragmentActivity.currentCategories); 
            onSuccessEvent(bundle);
        } else {
            triggerContentEvent(new GetCategoriesHelper(), bundle, mCallBack);    
        }
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
