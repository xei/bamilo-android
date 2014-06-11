/**
 * 
 */
package pt.rocket.view.fragments;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show the cart info and a navigation container, menu or categories
 * @author sergiopereira
 */
public class NavigationFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = LogTagHelper.create(NavigationFragment.class);
    
    private static NavigationFragment sNavigationFragment;
    
    private View mTabMenu;

    private View mTabCategories;

    private View mCartView;

    private TextView mCartCount;

    private TextView mCartElements;

    private TextView mCartVat;

    private View mCartEmpty;

    private FragmentType mSavedStateType;
    
    /**
     * Constructor via bundle
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static NavigationFragment newInstance(Bundle bundle) {
        sNavigationFragment = new NavigationFragment();
        return sNavigationFragment;
    }

    /**
     * Empty constructor
     */
    public NavigationFragment() {
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
        // Get the pre selected tab
        mSavedStateType = savedInstanceState != null ? (FragmentType) savedInstanceState.getSerializable(TAG) : null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.navigation_fragment_main, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Set cart
        mCartView = view.findViewById(R.id.nav_basket);
        mCartCount = (TextView) view.findViewById(R.id.nav_cart_count);
        mCartElements = (TextView) view.findViewById(R.id.nav_basket_elements);
        mCartVat = (TextView) view.findViewById(R.id.nav_basket_vat);
        mCartEmpty = view.findViewById(R.id.nav_basket_empty);
        // Set tabs
        mTabMenu = view.findViewById(R.id.navigation_tabs_button_menu);
        mTabCategories = view.findViewById(R.id.navigation_tabs_button_categories);
        // Set listeners
        mCartView.setOnClickListener(this);
        mTabMenu.setOnClickListener(this);
        mTabCategories.setOnClickListener(this);
        // Set cart
        onUpdateCart();
        // Show default content
        mTabMenu.callOnClick();
        // Validate pre selected tab (onSaveInstanceState)
        onLoadSavedState(mSavedStateType);
    }
    
    
    /**
     * Load and show the saved state
     * @param mPreSelectedTab
     */
    private void onLoadSavedState(FragmentType mPreSelectedTab) {
        if(mSavedStateType == null) return;
        // Validate type
        switch (mPreSelectedTab) {
        case NAVIGATION_MENU:
            Log.i(TAG, "ON LOAD SAVED STATE: NAVIGATION_MENU");
            mTabMenu.callOnClick();
            break;
        case NAVIGATION_CATEGORIES_LEVEL_1:
            Log.i(TAG, "ON LOAD SAVED STATE: NAVIGATION_CATEGORIES_LEVEL_1");
            mTabCategories.callOnClick();
            break;
        default:
            Log.w(TAG, "WARNING ON LOAD UNKNOWN SAVED STATE");
            break;
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
        else if (mTabCategories.isSelected()) outState.putSerializable(TAG, FragmentType.NAVIGATION_CATEGORIES_LEVEL_1);
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
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
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
    
    /**
     * ########### LAYOUT ###########  
     */
    
    /**
     * Method used to update the navigation menu
     * @author sergiopereira
     */
    public void onUpdateMenu() {
        Log.i(TAG, "ON UPDATE NAVIGATION MENU");
        try {
            NavigationMenuFragment navMenu = (NavigationMenuFragment) getChildFragmentManager().getFragments().get(0);
            navMenu.onUpdate();
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON UPDATE NAVIGATION MENU");
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "WARNING: IOE ON UPDATE NAVIGATION MENU");
        }
    }
  
    /**
     * Method used to update the cart info
     * @author sergiopereira
     */
    public synchronized void onUpdateCart() {
        Log.d(TAG, "ON UPDATE CART");
        // Validate current cart and current state
        if (JumiaApplication.INSTANCE.getCart() == null || isOnStoppingProcess) return;
        // Get the current cart
        ShoppingCart currentCart = JumiaApplication.INSTANCE.getCart();
        // Get cart value
        String value = currentCart != null ? currentCart.getCartValue() : "";
        // Get cart quantity
        String quantity = currentCart == null ? "?" : currentCart.getCartCount() == 0 ? "" : "" + currentCart.getCartCount();
        // Set layout
        setCartLayout(value, quantity);
    }
    
    /**
     * Show the cart values
     * @param value
     * @param quantity
     * @author sergiopereira
     */
    private void setCartLayout(String value, String quantity){
        // Validate quantity
        if (!TextUtils.isEmpty(quantity)) {
            Log.d(TAG, "CART IS NOT EMPTY: " + value);
            mCartCount.setText(quantity);
            mCartElements.setText(CurrencyFormatter.formatCurrency(value));
            mCartElements.setVisibility(View.VISIBLE);
            mCartVat.setVisibility(View.VISIBLE);
            mCartEmpty.setVisibility(View.INVISIBLE);
        } else {
            Log.d(TAG, "CART IS EMPTY");
            mCartCount.setText(quantity);
            mCartElements.setVisibility(View.INVISIBLE);
            mCartVat.setVisibility(View.INVISIBLE);
            mCartEmpty.setVisibility(View.VISIBLE);
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
            // Check back stack
            if(getChildFragmentManager().findFragmentByTag(FragmentType.NAVIGATION_MENU.toString()) != null)
                goToBackUntil(FragmentType.NAVIGATION_MENU);
            else {
                NavigationMenuFragment slideMenuFragment = NavigationMenuFragment.getInstance();
                fragmentChildManagerTransition(R.id.navigation_container, filterType, slideMenuFragment, false, true);
            }
            break;
        case NAVIGATION_CATEGORIES_LEVEL_1:
        case NAVIGATION_CATEGORIES_LEVEL_2:
        case NAVIGATION_CATEGORIES_LEVEL_3:
            NavigationCategoryFragment fragment = NavigationCategoryFragment.getInstance(bundle);
            fragmentChildManagerTransition(R.id.navigation_container, filterType, fragment, true, true);
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
        
        //FragmentManager.enableDebugLogging(true);
        
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
        // Case button menu
        if (id == R.id.nav_basket) onClickCart();
        // Case button menu
        else if (id == R.id.navigation_tabs_button_menu) onClickMenu();
        // Case button categories
        else if (id == R.id.navigation_tabs_button_categories) onClickCategories();
        // Case unknown
        else Log.d(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on cart
     * @author sergiopereira
     */
    private void onClickCart(){
        try {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            getBaseActivity().toggle();   
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON CLICK CART");
        }
    }
    
    /**
     * Process the click on menu tab
     * @author sergiopereira
     */
    private void onClickMenu() {
        Log.d(TAG, "ON CLICK: TAB MENU");
        // Validate state
        if(mTabMenu.isSelected()) return;
        // Update state
        mTabMenu.setSelected(true);
        mTabCategories.setSelected(false);
        // Switch content
        onSwitchChildFragment(FragmentType.NAVIGATION_MENU, null);
    }
    
    /**
     * Process the click on categories menu
     * @param view
     * @author sergiopereira
     */
    private void onClickCategories() {
        Log.d(TAG, "ON CLICK: TAB CAT");
        // Validate state
        if(mTabCategories.isSelected()) return;
        // Update state
        mTabCategories.setSelected(true);
        mTabCategories.setPressed(true);
        mTabMenu.setSelected(false);
        // Switch content
        Bundle args = new Bundle();
        args.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.NAVIGATION_CATEGORIES_LEVEL_1);
        onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_LEVEL_1, args);
    }
    
    
    /**
     * ########### DIALOGS ###########  
     */    

  
    
}
