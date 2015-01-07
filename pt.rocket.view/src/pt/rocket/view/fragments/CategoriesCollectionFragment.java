package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show categories with support for multi levels.
 * @author sergiopereira
 */
public class CategoriesCollectionFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(CategoriesCollectionFragment.class);
    
    private static CategoriesCollectionFragment sCategoriesFragment;
    
    private static int BACK_STACK_EMPTY = 0;

    /**
     * Get instance of CategoryColletionFragment
     * @param bundle
     * @return CategoryColletionFragment
     * @author sergiopereira
     */
    public static CategoriesCollectionFragment getInstance(Bundle bundle) {
        sCategoriesFragment = new CategoriesCollectionFragment();        
        return sCategoriesFragment;
    }

    /**
     * Empty constructor
     */
    public CategoriesCollectionFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Categories,
                R.layout.categories_collection_fragment,
                R.string.categories_title,
                KeyboardState.ADJUST_CONTENT);
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
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Validate saved state
        if (getChildFragmentManager().getBackStackEntryCount() == BACK_STACK_EMPTY) {
            Log.d(TAG, "SAVED IS NULL");
            // Switch content
            Bundle args = new Bundle();
            args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
            onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL, args);
        } else {
            Log.d(TAG, "SAVED STACK SIZE: " + getChildFragmentManager().getBackStackEntryCount());
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
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        Log.i(TAG, "ON ALLOW BACK PRESSED");
        // Case multi level
        if(getChildFragmentManager().getBackStackEntryCount() > 1) {
            goToParentCategory();
            return true;
        }
        // Case first level
        else return false;
    }
    
    
    /**
     * Method used to switch between the filter fragments
     * @param filterType
     * @param bundle 
     * @author sergiopereira
     */
    public void onSwitchChildFragment(FragmentType filterType, Bundle bundle) {
        Log.i(TAG, "ON SWITCH CHILD FRAG: " + filterType);
        switch (filterType) {
        case NAVIGATION_CATEGORIES_SUB_LEVEL:
            // No tag fragment on back stack
            filterType = null; 
        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
            CategoriesPageFragment fragment = CategoriesPageFragment.getInstance(bundle);
            fragmentChildManagerTransition(R.id.categories_fragments_container, filterType, fragment, false, true);
            break;
        default:
            Log.w(TAG, "ON SWITCH FILTER: UNKNOWN TYPE");
            break;
        }
    }
    
    /**
     * Method used to associate the container and fragment 
     * @param container
     * @param fragment
     * @param filterType 
     * @param animated
     * @author sergiopereira
     */
    public void fragmentChildManagerTransition(int container, FragmentType filterType, Fragment fragment, boolean animated, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Fragment tag
        String tag = filterType != null ? filterType.toString() : null;
        // Animations
        if (animated)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment, tag);
        // Back stack
        if (addToBackStack)
            fragmentTransaction.addToBackStack(tag);
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Goto back until type
     * @param type
     * @author sergiopereira
     */
    public void goToBackUntil(FragmentType type){
        getChildFragmentManager().popBackStackImmediate(type.toString(), 0);
    }
    
    /**
     * Pop the back stack
     * @author sergiopereira
     */
    public void goToParentCategory(){
        getChildFragmentManager().popBackStack();
    }
    
}
