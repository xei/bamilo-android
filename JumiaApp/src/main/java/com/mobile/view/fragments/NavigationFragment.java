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

import com.mobile.components.NavigationListComponent;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show the cart info and a navigation container, menu or categories
 * @author sergiopereira
 */
public class NavigationFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = LogTagHelper.create(NavigationFragment.class);
    
    private static final int TAB_MENU = 0;
    
    private static final int TAB_CATEGORIES = 1;
    
    private View mTabMenu;

    private View mTabCategories;

    private FragmentType mSavedStateType;

    private ViewGroup mNavigationContainer;

    private LayoutInflater mInflater;
    
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
        // Get inflater
        mInflater = LayoutInflater.from(getBaseActivity());
        // Get the pre selected tab
        mSavedStateType = savedInstanceState != null ? (FragmentType) savedInstanceState.getSerializable(TAG) : null;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Set tabs
        mTabMenu = view.findViewById(R.id.navigation_tabs_button_menu);
        mTabCategories = view.findViewById(R.id.navigation_tabs_button_categories);
        // Set listeners
        mTabMenu.setOnClickListener(this);
        mTabCategories.setOnClickListener(this);
        // Get container
        mNavigationContainer = (ViewGroup) view.findViewById(R.id.slide_menu_scrollable_container);
        // Check if mNavigationContainer is being reconstructed
        if (mNavigationContainer.getChildCount() <= 1) addMenuItems();
        // Validate saved state
        if (mSavedStateType == null) {
            Log.d(TAG, "SAVED IS NULL");
            onClick(mTabCategories);
        } else {
            Log.d(TAG, "SAVED STACK SIZE: " + getChildFragmentManager().getBackStackEntryCount());
            // Validate pre selected tab (onSaveInstanceState)
            onLoadSavedState(mSavedStateType);
        }
    }

    /**
     * Load and show the saved state
     * @param mPreSelectedTab
     * @author sergiopereira
     */
    private void onLoadSavedState(FragmentType mPreSelectedTab) {
        // Validate type
        switch (mPreSelectedTab) {
        case NAVIGATION_MENU:
            Log.i(TAG, "ON LOAD SAVED STATE: NAVIGATION_MENU");
            //setSelectedTab(TAB_MENU);
            break;
        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
            Log.i(TAG, "ON LOAD SAVED STATE: NAVIGATION_CATEGORIES_ROOT_LEVEL");
            setSelectedTab(TAB_CATEGORIES);
            break;
        default:
            Log.w(TAG, "WARNING ON LOAD UNKNOWN SAVED STATE");
            break;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
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
        // Case Menu
        if(mTabMenu.isSelected()) outState.putSerializable(TAG, FragmentType.NAVIGATION_MENU);
        // Case Categories
        else if (mTabCategories.isSelected()) outState.putSerializable(TAG, FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
        // Case Unknown
        else Log.w(TAG, "WARNING UNKNOWN TAB SELECTED");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }
    
    /**
     * ########### LAYOUT ###########  
     */
    
    /**
     * Create an Option for each item on array <code>navigation_items</code> and add it to Menu</br>
     * Add Categories header to Menu
     */
    private void addMenuItems() {
        ArrayList<NavigationListComponent> components = new ArrayList<>();
        // Get Navigation items from arrays.xml
        String[] navigationItems = getResources().getStringArray(R.array.navigation_items);
        for (String item : navigationItems) {
            NavigationListComponent component = new NavigationListComponent();
            component.setElementUrl(item);
            components.add(component);
        }
        // Fill container
        fillNavigationContainer(components);
        // Add Categories Header
        mNavigationContainer.addView(createCategoriesHeader());
    }

    /**
     * 
     * @param components
     */
    private void fillNavigationContainer(ArrayList<NavigationListComponent> components) {
        Log.d(TAG, "FILL NAVIGATION CONTAINER");
        try {
            mNavigationContainer.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // Scrollable container
        if (components != null) {
            for (NavigationListComponent component : components) {
                // Others
                View actionElementLayout = getActionElementLayout(component, mNavigationContainer);
                if (actionElementLayout != null) {
                    mNavigationContainer.addView(actionElementLayout);
                }
            }
        }
    }

    /**
     * Retrieves the layout element associated with a given action of the navigation list
     *
     * @return The layout of the navigation list element
     */
    public View getActionElementLayout(NavigationListComponent component, ViewGroup parent) {
        View layout = null;
        String elementUrl = component.getElementUrl();
        if (elementUrl == null) {
            elementUrl = "";
        }
        String[] nav = elementUrl.split("/");
        NavigationAction action = NavigationAction.byAction(nav[nav.length - 1].trim());

        switch (action) {
        case Home:
            layout = createGenericComponent(parent, R.drawable.selector_navigation_home, R.string.home_label, this);
            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
            break;
        case Categories:
            layout = createCategoriesHeader();
            break;
        default:
            layout = mInflater.inflate(R.layout.navigation_generic_component, parent, false);
            TextView tVd = (TextView) layout.findViewById(R.id.component_text);
            tVd.setText(component.getElementText());
            break;
        }
        if (layout != null) {
            layout.setTag(R.id.nav_action, action);
            setActionSelected(layout);
        }
        return layout;
    }

    /**
     * 
     * @param view
     */
    private void setActionSelected(View view) {
        try {
            NavigationAction action = getBaseActivity().getAction();
            Log.i(TAG, "SELECTED ACTION: " + action);
            if (!view.isSelected() && action == view.getTag(R.id.nav_action)) view.setSelected(true);
            else view.setSelected(false);
        } catch (NullPointerException e) {
            Log.w(TAG, "ON SET ACTION SELECTED: NULL POINTER EXCEPTION");
            e.printStackTrace();
            view.setSelected(false);
        }
    }

    /**
     * 
     * @return
     */
    private View createCategoriesHeader() {
        View navComponent = mInflater.inflate(R.layout.navigation_categories_component, mNavigationContainer, false);
        TextView tVSearch = (TextView) navComponent.findViewById(R.id.component_text);
        String text = getString(R.string.categories_label);
        tVSearch.setText(text.toUpperCase());
        tVSearch.setContentDescription("calabash_" + text);
        navComponent.setOnClickListener(null);
        navComponent.setTag(R.id.nav_action, NavigationAction.Categories);
        return navComponent;
    }

    /* 
     * @param parent
     * @param component
     * @param iconRes
     * @param text
     * @param listener
     * @return
     */
    private View createGenericComponent(ViewGroup parent, int iconRes, int stringId, OnClickListener listener) {
        View navComponent = mInflater.inflate(R.layout.navigation_generic_component, parent, false);
        TextView tVSearch = (TextView) navComponent.findViewById(R.id.component_text);
        String text = getString(stringId);
        tVSearch.setText(text);
        tVSearch.setContentDescription("calabash_" + text);
        
        // RTL VALIDATION
        if(getResources().getBoolean(R.bool.is_bamilo_specific)){
            tVSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0);
        } else {
            tVSearch.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        }
        
        tVSearch.setOnClickListener(listener);
        return navComponent;
    }

    /**
     * Method used to update the navigation menu
     * @author sergiopereira
     */
    public void onUpdateMenu() {
        Log.i(TAG, "ON UPDATE NAVIGATION MENU");
//        try {
//            Fragment navMenu = getChildFragmentManager().getFragments().get(0);
//            if (navMenu instanceof NavigationMenuFragment) ((NavigationMenuFragment) navMenu).onUpdate();
//        } catch (NullPointerException e) {
//            Log.w(TAG, "WARNING: NPE ON UPDATE NAVIGATION MENU");
//        } catch (IndexOutOfBoundsException e) {
//            Log.w(TAG, "WARNING: IOE ON UPDATE NAVIGATION MENU");
//        }

        // Update items
        if (!isOnStoppingProcess)
            updateNavigationItems();
    }
    
    
    /**
     * Updated generic items
     * 
     * @author sergiopereira
     */
    private void updateNavigationItems() {
        try {
            // For each child validate the selected item
            int count = mNavigationContainer.getChildCount();
            for (int i = 0; i < count; i++) updateItem(mNavigationContainer.getChildAt(i));
        } catch (NullPointerException e) {
            Log.w(TAG, "ON UPDATE NAVIGATION: NULL POINTER EXCEPTION");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "ON UPDATE NAVIGATION: INDEX OUT OF BOUNDS EXCEPTION");
            e.printStackTrace();
        }
    }
    
    /**
     * Update item
     * 
     * @param view
     */
    private void updateItem(View view) {
        NavigationAction navAction = (NavigationAction) view.getTag(R.id.nav_action);
        Log.d(TAG, "UPDATE NAV: " + navAction.toString());
        switch (navAction) {
        case Home:
            // ...
        case Country:
            // ...
        default:
            break;
        }
        // Set selected
        setActionSelected(view);
    }
    
    /**
     * Set selected tag with respective tag
     * @param tab
     * @author sergiopereira
     */
    private void setSelectedTab(int tab) {
        switch (tab) {
        case TAB_MENU:
            Log.i(TAG, "ON SELECT TAB: TAB_MENU");
            mTabMenu.setSelected(true);
            mTabCategories.setSelected(false);
            break;
        case TAB_CATEGORIES:
            Log.i(TAG, "ON SELECT TAB: TAB_CATEGORIES");
            mTabMenu.setSelected(false);
            mTabCategories.setSelected(true);
            break;
        default:
            Log.w(TAG, "WARINING: ON SELECT UNKNOWN TAB");
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
        Log.i(TAG, "ON SWITCH CHILD FRAG: " + filterType);
        switch (filterType) {
        case NAVIGATION_MENU:
//            // Check back stack
//            if(getChildFragmentManager().findFragmentByTag(FragmentType.NAVIGATION_MENU.toString()) != null)
//                goToBackUntil(FragmentType.NAVIGATION_MENU);
//            else {
//                NavigationMenuFragment slideMenuFragment = NavigationMenuFragment.getInstance();
//                fragmentChildManagerTransition(R.id.navigation_container, filterType, slideMenuFragment, false, true);
//            }
            break;
        case NAVIGATION_CATEGORIES_SUB_LEVEL:
            // No tag fragment on back stack
            filterType = null; 
        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
            NavigationCategoryFragment fragment = NavigationCategoryFragment.getInstance(bundle);
            fragmentChildManagerTransition(R.id.navigation_container, filterType, fragment, false, true);
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
        Log.d(TAG, "ON CLICK");
        //Get view id 
        int id = view.getId();
        /*-// Case button menu
        else if (id == R.id.navigation_tabs_button_menu) onClickMenu();*/
        // Case button categories
        if (id == R.id.navigation_tabs_button_categories) onClickCategories();
        // Case NavigationAction
        else if (onClickNavigationAction(view));
        // Case unknown
        else Log.d(TAG, "ON CLICK: UNKNOWN VIEW");
    }

    /**
     * OnClick to process NavigationAction items
     * 
     * @param v
     * @return
     */
    public boolean onClickNavigationAction(View v) {
        NavigationAction navAction = (NavigationAction) v.getTag(R.id.nav_action);
        if (navAction != null && getBaseActivity().getAction() != navAction) {
            switch (navAction) {
            // Case Home
            case Home:
                Log.d(TAG, "ON CLICK NAVIGATION MENU ITEM: HOME");
                getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,FragmentController.ADD_TO_BACK_STACK);
                break;
            // Case unknown
            default:
                Log.d(TAG, "ON CLICK NAVIGATION MENU ITEM: UNKNOWN");
                return false;
            }
            // Close 
            getBaseActivity().closeNavigationDrawer();
        } else {
            Log.d(TAG, "ON CLICK NAVIGATION MENU ITEM: NOT HANDLE " + navAction);
            return false;
        }
        return true;
    }
    
    /**
     * Process the click on menu tab
     * @author sergiopereira
     */
    /*-private void onClickMenu() {
        Log.d(TAG, "ON CLICK: TAB MENU");
        // Validate state
        if(mTabMenu.isSelected()) return;
        // Update state
        setSelectedTab(TAB_MENU);
        // Switch content
        onSwitchChildFragment(FragmentType.NAVIGATION_MENU, null);
    }*/
    
    /**
     * Process the click on categories menu
     *
     * @author sergiopereira
     */
    private void onClickCategories() {
        Log.d(TAG, "ON CLICK: TAB CAT");
        // Validate state
        if(mTabCategories.isSelected()) return;
        // Update state
        setSelectedTab(TAB_CATEGORIES);
        // Switch content
        Bundle args = new Bundle();
        args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
        onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL, args);
    }

}
