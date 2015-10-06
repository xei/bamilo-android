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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

/**
 * Class used to show the cart info and a navigation container, menu or categories
 * @author sergiopereira
 */
public class NavigationFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = NavigationFragment.class.getSimpleName();

    private LinearLayout mNavigationOptions;

    private RelativeLayout mCategoryBack;

    private LayoutInflater mInflater;

    private FragmentType mSavedStateType;

    private int mHomeStringId = R.string.home_label;

    private int mCategoryStringId = R.string.categories_label;

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
        mCategoryBack = (RelativeLayout) view.findViewById(R.id.categories_back_navigation);
        ((TextView)mCategoryBack.findViewById(R.id.text)).setText(getString(R.string.back_label));
        mCategoryBack.setOnClickListener(this);
        // Get container
        mNavigationOptions = (LinearLayout) view.findViewById(R.id.navigation_options_container);
        // Check if mNavigationContainer is being reconstructed
        if (mNavigationOptions.getChildCount() <= 0){
            addMenuItems();
        }

        if (mSavedStateType == null) {
            Print.d(TAG, "SAVED IS NULL");
            addListItems();
        }

    }

    private void addListItems() {
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
     * create generic component to add to navigation drawer
     * @param parent
     * @param component
     * @param iconRes
     * @param text
     * @param listener
     * @return
     */
    private void createGenericComponent(ViewGroup parent, int iconRes, int stringId, OnClickListener listener) {
        View navComponent = mInflater.inflate(R.layout.navigation_generic_component, parent, false);
        TextView tVSearch = (TextView) navComponent.findViewById(R.id.component_text);
        String text = getString(stringId);
        tVSearch.setText(text);
        tVSearch.setContentDescription("calabash_" + text);
        
        // RTL VALIDATION
        if(ShopSelector.isRtl()){
            tVSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0);
        } else {
            tVSearch.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        }
        // Remove selector from category item
        if(stringId == mCategoryStringId){
            tVSearch.setBackgroundResource(0);
        }
        tVSearch.setOnClickListener(listener);
        tVSearch.setTag(stringId);
        parent.addView(navComponent);
    }

    /**
     * Method used to update the navigation menu
     * @author sergiopereira
     */
    public void onUpdateMenu(NavigationAction page) {
        Print.i(TAG, "ON UPDATE NAVIGATION MENU");
        // Update items
        if (!isOnStoppingProcess)
            updateNavigationItems(page);
    }

    /**
     * Method used to update the navigation menu
     * @author sergiopereira
     */
    public void onUpdateCategorySelected(String categoryId) {
        Print.i(TAG, "ON UPDATE NAVIGATION MENU");
        // Update items
//        if (!isOnStoppingProcess)
//            setNavigationCategorySelection(categoryId);
    }
    
    /**
     * Updated generic items
     * 
     * @author sergiopereira
     */
    private void updateNavigationItems(NavigationAction page) {

        switch (page){
            case Home:
                Print.i(TAG, "ON UPDATE NAVIGATION MENU: HOME");
                if(mNavigationOptions != null){
                    mNavigationOptions.findViewWithTag(R.string.home_label).setSelected(true);
//                    clearNavigationCategorySelection();
                }
                break;
            default:
                Print.i(TAG, "ON UPDATE NAVIGATION MENU: UNKNOWN");
                if(mNavigationOptions != null){
                    mNavigationOptions.findViewWithTag(R.string.home_label).setSelected(false);
//                    clearNavigationCategorySelection();
                }
                break;
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
        case NAVIGATION_CATEGORIES_SUB_LEVEL:
            // No tag fragment on back stack
            filterType = null; 
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
//        // Animations
//        if (animated)
//            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
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

        switch (id) {
            // Case Home
            case R.id.component_text:
                int tag = (int) view.getTag();
                if(tag == R.string.home_label){
                    Print.d(TAG, "ON CLICK NAVIGATION MENU ITEM: HOME");
                    getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    getBaseActivity().closeNavigationDrawer();
                } else {
                    Print.d(TAG, "ON CLICK NAVIGATION MENU ITEM: CATALOG");
                }
                break;
            // Case Back button
            case R.id.categories_back_navigation:
                // should I clear the selected category?
//                clearNavigationCategorySelection();
                goToParentCategory();
                break;
            // Case unknown
            default:
                Print.d(TAG, "ON CLICK NAVIGATION MENU ITEM: UNKNOWN");
                getBaseActivity().closeNavigationDrawer();
                break;
        }
        // Close

    }

    /**
     * control back button visibility
     */
    public void setBackButtonVisibility(int visibility){
        if(mCategoryBack == null){
            mCategoryBack = (RelativeLayout) getView().findViewById(R.id.categories_back_navigation);
        }
        mCategoryBack.setVisibility(visibility);
    }

//    /**
//     * Clear selected Category
//     */
//    public void clearNavigationCategorySelection(){
//        if(navigationCategoryFragment != null){
//            navigationCategoryFragment.clearSelectedCategory();
//        }
//    }
//
//    /**
//     * Set selected Category
//     */
//    public void setNavigationCategorySelection(String categoryId){
//        if(navigationCategoryFragment != null){
//            navigationCategoryFragment.setSelectedCategory(categoryId);
//        }
//    }


}
