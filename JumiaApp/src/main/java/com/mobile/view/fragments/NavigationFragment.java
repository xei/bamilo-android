/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

/**
 * Class used to show the cart info and a navigation container, menu or categories
 * @author sergiopereira
 */
public class NavigationFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = NavigationFragment.class.getSimpleName();

    private LayoutInflater mInflater;

    private FragmentType mSavedStateType;

    /**
     * Constructor via bundle
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    /**
     * Empty constructor
     */
    public NavigationFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.navigation_fragment_main);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get inflater
        mInflater = LayoutInflater.from(getBaseActivity());

        mSavedStateType = savedInstanceState != null ? (FragmentType) savedInstanceState.getSerializable(TAG) : null;

    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

        if (mSavedStateType == null) {
            Print.d(TAG, "SAVED IS NULL");
            addListItems();
        }

    }

    private void addListItems() {
        Print.i(TAG,"ADD LIST ITEMS");
        Bundle args = new Bundle();
        args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
        onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL, args);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE");
        outState.putSerializable(TAG, FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }
    
    /**
     * ########### LAYOUT ###########  
     */
    
    /**
     * Create an Option for each item on array <code>navigation_items</code> and add it to Menu</br>
     * Add Categories header to Menu
     */
    private void addMenuItems() {
        try {
            mNavigationOptions.removeAllViews();
            // Add Home
            createGenericComponent(mNavigationOptions, R.drawable.selector_navigation_home, mHomeStringId, this);
            // Add Category
            createGenericComponent(mNavigationOptions, R.drawable.selector_navigation_categories, mCategoryStringId, null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method used to switch between the filter fragments
     * @param filterType
     * @param bundle 
     * @author sergiopereira
     */
    public void onSwitchChildFragment(FragmentType filterType, Bundle bundle) {
        Print.i(TAG, "ON SWITCH CHILD FRAG: " + filterType);
        switch (filterType) {
        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
            NavigationCategoryFragment navigationCategoryFragment = NavigationCategoryFragment.getInstance(bundle);
            fragmentChildManagerTransition(R.id.navigation_container_list, filterType, navigationCategoryFragment, false, true);
            break;
        default:
            Print.w(TAG, "ON SWITCH FILTER: UNKNOWN TYPE");
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
        // fragmentTransaction.commit();
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
    
    /**
     * ########### LISTENERS ###########  
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Print.d(TAG, "ON CLICK");
        int id = view.getId();

        Print.d(TAG, "ON CLICK NAVIGATION MENU ITEM: UNKNOWN");
//        switch (id) {
//            // Case unknown
//            default:
//                Print.d(TAG, "ON CLICK NAVIGATION MENU ITEM: UNKNOWN");
//                getBaseActivity().closeNavigationDrawer();
//                break;
//        }
        // Close

    }



}
