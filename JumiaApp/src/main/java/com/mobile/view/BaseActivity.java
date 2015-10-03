package com.mobile.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.SearchAutoComplete;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.SearchDropDownAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.search.GetSearchSuggestionsHelper;
import com.mobile.helpers.session.GetLoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.CheckVersion;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.MyProfileActionProvider;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.CustomToastView;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogProgressFragment;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.BaseFragment.KeyboardState;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * All activities extend this activity, in order to access methods that are shared and used in all activities.
 * <p/>
 * <br>
 * <p/>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited <br> Proprietary and confidential.
 *
 * @author Paulo Carvalho
 * @version 2.0
 *          <p/>
 *          2012/06/19
 * @modified Sergio Pereira
 * @modified Manuel Silva
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    // private ShareActionProvider mShareActionProvider;

    private static final int SEARCH_EDIT_DELAY = 500;

    private static final int SEARCH_EDIT_SIZE = 2;

    private static final int TOAST_LENGTH_SHORT = 2000; // 2 seconds

    // REMOVED FINAL ATTRIBUTE
    private NavigationAction action;

    //protected View contentContainer;

    private Set<MyMenuItem> menuItems;

    private final int activityLayoutId;

    protected DialogFragment dialog;

    private DialogProgressFragment baseActivityProgressDialog;

    private DialogGenericFragment dialogLogout;

    private boolean backPressedOnce = false;

    public View mDrawerNavigation;
    /**
     * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
     * @Solution : http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this -action-after-onsaveinstancestate-h
     */
    private Intent mOnActivityResultIntent = null;

    public DrawerLayout mDrawerLayout;

    public ActionBarDrawerToggle mDrawerToggle;

    private final int titleResId;

    //private final int contentLayoutId;

    private TextView mActionCartCount;

    private MyProfileActionProvider myProfileActionProvider;

    private FragmentController fragmentController;

    private boolean initialCountry = false;

    private Menu mCurrentMenu;

    private long beginInMillis;

    protected SearchView mSearchView;

    protected SearchAutoComplete mSearchAutoComplete;

    protected boolean isSearchComponentOpened = false;

    private ActionBar mSupportActionBar;

    private boolean isBackButtonEnabled = false;

    private long mLaunchTime;

    public MenuItem mSearchMenuItem;

    public WarningFactory warningFactory;

    public static KeyboardState currentAdjustState;

    private TabLayout mTabLayout;

    private AppBarLayout mAppBarLayout;

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     */
    public BaseActivity(NavigationAction action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        this(R.layout.main, action, enabledMenuItems, userEvents, titleResId, contentLayoutId);
    }

    /**
     * Constructor
     */
    public BaseActivity(int activityLayoutId, NavigationAction action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        this.activityLayoutId = activityLayoutId;
        //this.userEvents = userEvents;
        this.action = action != null ? action : NavigationAction.Unknown;
        this.menuItems = enabledMenuItems;
        this.titleResId = titleResId;
        //this.contentLayoutId = contentLayoutId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.d(TAG, "ON CREATE");
        /*
         * In case of rotation the activity is restarted and the locale too.<br>
         * These method forces the right locale used before the rotation.
         * @author spereira
         */
        ShopSelector.setLocaleOnOrientationChanged(getApplicationContext());
        // In case app is killed in background needs to restore font type
        HoloFontLoader.initFont(getResources().getBoolean(R.bool.is_shop_specific));
        // Get fragment controller
        fragmentController = FragmentController.getInstance();
        // Set content
        setContentView(activityLayoutId);
        // Set action bar
        setupAppBarLayout();
        // Set navigation
        setupDrawerNavigation();
        // Set content view
        setupContentViews();
        // Update the content view if initial country selection
        updateContentViewsIfInitialCountrySelection();
        // Set main layout
        //setAppContentLayout();
        // Set title in AB or TitleBar
        setTitle(titleResId);
        // For tracking
        mLaunchTime = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onNewIntent(android.content.Intent )
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Print.i(TAG, "ON NEW INTENT");
        ActivitiesWorkFlow.addStandardTransition(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");

        // Disabled for Samsung and Blackberry (check_version_enabled)
        CheckVersion.run(getApplicationContext());

        /**
         * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
         * @Solution : http://stackoverflow.com/questions/7575921/illegalstateexception
         *           -can-not-perform -this-action-after-onsaveinstancestate-h
         */
        if (mOnActivityResultIntent != null && getIntent().getExtras() != null) {
            initialCountry = getIntent().getExtras().getBoolean(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false);
            mOnActivityResultIntent = null;
        }

        // Get the cart and perform auto login
        recoverUserDataFromBackground();
        AdjustTracker.onResume();

        TrackerDelegator.trackAppOpenAdjust(getApplicationContext(), mLaunchTime);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        /**
         * Validate current version to show the upgrade dialog.
         * Disabled for Samsung and Blackberry (check_version_enabled).
         */
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(this);
        }
    }

    /**
     * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
     * @Solution : http://stackoverflow.com/questions/7575921/illegalstateexception -can-not-perform-this -action-after-onsaveinstancestate-h
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            mOnActivityResultIntent = data;
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        // Hide search component
        hideSearchComponent();
        // Dispatch saved hits
        AnalyticsGoogle.get().dispatchHits();

        AdjustTracker.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
        JumiaApplication.INSTANCE.setLoggedIn(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
        JumiaApplication.INSTANCE.setLoggedIn(false);
        // Tracking
        TrackerDelegator.trackCloseApp();
    }

    /**
     * Using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Print.i(TAG, "ON POST CREATE: DRAWER");
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v7.app.ActionBarActivity#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Print.i(TAG, "ON CONFIGURATION CHANGED");
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * ############## ACTION BAR ##############
     */

    /**
     * Method used to update the sliding menu and items on action bar. Called from BaseFragment
     */
    public void updateBaseComponents(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int actionBarTitleResId, int checkoutStep) {
        Print.i(TAG, "ON UPDATE BASE COMPONENTS");
        // Update the app bar layout
        setAppBarLayout(action);
        // Update options menu and search bar
        menuItems = enabledMenuItems;
        hideKeyboard();
        invalidateOptionsMenu();
        // Update the sliding menu
        this.action = action != null ? action : NavigationAction.Unknown;
        // Select step on Checkout
        setCheckoutHeader(checkoutStep);
        // Set actionbarTitle
        setActionTitle(actionBarTitleResId);
    }

    /**
     *
     */
    public void updateActionForCountry(NavigationAction action) {
        this.action = action != null ? action : NavigationAction.Unknown;
    }

    /**
     * Set the Action bar style
     *
     * @modified sergiopereira
     */
    public void setupAppBarLayout() {
        // Get tab layout
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        // Get tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSupportActionBar = getSupportActionBar();
        if(mSupportActionBar != null) {
            mSupportActionBar.setDisplayHomeAsUpEnabled(true);
            mSupportActionBar.setHomeButtonEnabled(true);
            mSupportActionBar.setDisplayShowTitleEnabled(true);
            mSupportActionBar.setElevation(0);
        }
        // Set tab layout
        setupTabBarLayout();
    }

    public void setupTabBarLayout() {
        // Get tab layout
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setCustomView(R.layout.tab_home);
        mTabLayout.addTab(tab);
        TabLayout.Tab tab2 = mTabLayout.newTab();
        tab2.setCustomView(R.layout.tab_saved);
        mTabLayout.addTab(tab2);
        TabLayout.Tab tab3 = mTabLayout.newTab();
        tab3.setCustomView(R.layout.tab_cart);
        mTabLayout.addTab(tab3);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0 && action != NavigationAction.Home) {
                    onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                }
                else if (pos == 1 && action != NavigationAction.Saved) {
                    onSwitchFragment(FragmentType.WISH_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                }
                else if (pos == 2 && action != NavigationAction.Basket) {
                    onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        updateTabCartInfo();

    }

    private void setAppBarLayout(NavigationAction action) {
        try {
            // Case others
            if (action != NavigationAction.Basket && action != NavigationAction.Saved && action != NavigationAction.Home) {
                mTabLayout.setVisibility(View.GONE);
            }
            else {
                mTabLayout.setVisibility(View.VISIBLE);
                // Case Home
                if (action == NavigationAction.Home) {
                    mTabLayout.getTabAt(0).select();
                }
                // Case Basket
                else if (action == NavigationAction.Saved) {
                    mTabLayout.getTabAt(1).select();
                }
                // Case Basket
                else {
                    mTabLayout.getTabAt(2).select();
                }
            }
            // Expand the app bar layout
            mAppBarLayout.setExpanded(true, true);
        } catch (NullPointerException e) {
            // ...
        }
    }

    /**
     * Method used to add a bottom margin with tool bar size.<br>
     * Because the Coordinator Layout first build the without tool bar size.<br>
     * And after add the tool bar and translate the view to below.
     */
    public void setViewWithoutNestedScrollView(View view, NavigationAction action) {
        // Case others
        if (action != NavigationAction.Basket &&
                action != NavigationAction.Saved &&
                action != NavigationAction.Home &&
                action != NavigationAction.Catalog &&
                view != null) {
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.bottomMargin += actionBarHeight + 5; // 5 is little fix
            }
        }

    }

    /**
     * Set Action bar title
     */
    private void setActionTitle(int actionBarTitleResId) {
        // Case hide all
        if (actionBarTitleResId == IntConstants.ACTION_BAR_NO_TITLE) {
            hideTitle();
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
            hideActionBarTitle();
        }
        // Case #specific_shop
        else if (getResources().getBoolean(R.bool.is_shop_specific) || ShopSelector.isRtlShop()) {
            // Show the application name in the action bar
            setActionBarTitle(R.string.app_name);
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
            hideTitle();
        } else {
            hideTitle();
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
            setActionBarTitle(actionBarTitleResId);
        }
    }
    
    /*
     * ############## NAVIGATION ##############
     */

    /**
     * Set the navigation drawer.
     *
     * @modified sergiopereira
     */
    private void setupDrawerNavigation() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerNavigation = findViewById(R.id.fragment_navigation);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }

        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Close the navigation drawer if open.
     *
     * @modified sergiopereira
     */
    public void closeNavigationDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation)) {
            mDrawerLayout.closeDrawer(mDrawerNavigation);
        }
    }
    
    /*
     * ############## CONTENT VIEWS ##############
     */

    /**
     *
     */
    private void setupContentViews() {
        Print.d(TAG, "DRAWER: SETUP CONTENT VIEWS");
        // Get the application horizontalListView
        //contentContainer = findViewById(R.id.rocket_app_content);
        // Warning layout
        try {
            warningFactory = new WarningFactory(findViewById(R.id.warning));
        } catch(IllegalStateException ex){
            Print.e(TAG, ex.getLocalizedMessage(), ex);
        }
    }
    
    /*
     * ############## INITIAL COUNTRY SELECTION ##############
     */

    /**
     * Updated the action bar and the navigation for initial country selection
     *
     * @author sergiopereira
     */
    private void updateContentViewsIfInitialCountrySelection() {
        /**
         * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
         * @Solution : http://stackoverflow.com/questions/7575921/illegalstateexception
         *           -can-not-perform -this-action-after-onsaveinstancestate-h
         */
        if (getIntent().getExtras() != null) {
            initialCountry = getIntent().getExtras().getBoolean(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false);
            mOnActivityResultIntent = null;
        }

        /**
         * Set the action bar and navigation drawer for initialCountry
         *
         * @author sergiopereira
         */
        if (initialCountry) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mSupportActionBar.setDisplayHomeAsUpEnabled(false);
            mSupportActionBar.setDisplayShowCustomEnabled(true);
            mSupportActionBar.setCustomView(R.layout.action_bar_initial_logo_layout);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mSupportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Method used to validate if is to show the initial country selection or is in maintenance.<br> Used in {@link com.mobile.view.fragments.HomePageFragment#onCreate(Bundle)}.
     *
     * @return true or false
     * @author sergiopereira
     */
    public boolean isInitialCountry() {
        return initialCountry;
    }

    /*
     * ############### OPTIONS MENU #################
     */

    /**
     * When a user selects an option of the menu that is on the action bar. The centralization of this in this activity, prevents all the activities to have to
     * handle this events
     *
     * @param item The menu item that was pressed
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
     * (android.view.MenuItem )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Print.d(TAG, "ON OPTION ITEM SELECTED: " + item.getTitle());
        // Get item id
        int itemId = item.getItemId();
        // CASE BACK ARROW
        if (itemId == android.R.id.home && isBackButtonEnabled) {
            onBackPressed();
            return true;
        }
        // CASE HOME (BURGUER)
        else if (mDrawerToggle.onOptionsItemSelected(item)) {
            // Toggle between opened and closed drawer
            return true;
        }
        // CASE CART ACTION
        else if (itemId == R.id.menu_basket) {
            // Goto cart
            onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            return true;
        }
        // DEFAULT:
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Print.d(TAG, "ON OPTIONS MENU: CREATE");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Save the current menu
        mCurrentMenu = menu;
        // Flag used to show action bar as default
        int showActionBar = View.VISIBLE;
        // Flag to show home or back button
        isBackButtonEnabled = false;
        // Setting Menu Options
        for (MyMenuItem item : menuItems) {
            switch (item) {
                case HIDE_AB:
                    showActionBar = View.GONE;
                    break;
                case UP_BUTTON_BACK:
                    isBackButtonEnabled = true;
                    break;
                case SEARCH_VIEW:
                    setActionSearch(menu);
                    break;
                case BASKET:
                    setActionCart(menu);
                    break;
                case MY_PROFILE:
                    setActionProfile(menu);
                    break;
                default:
                    menu.findItem(item.resId).setVisible(true);
                    break;
            }
        }

        // Set AB UP button
        setActionBarUpButton();
        // Set AB visibility
        setActionBarVisibility(showActionBar);
        // Return current menu
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * ############### ACTION BAR MENU ITEMS #################
     */

    /**
     * Change actionBar visibility if necessary
     */
    public void setActionBarVisibility(int showActionBar) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Print.w(TAG, "WARNING: AB IS NULL");
            return;
        }
        // Validate flag
        if (showActionBar == View.VISIBLE) {
            if (!actionBar.isShowing()) {
                actionBar.show();
            }
        } else if (showActionBar == View.GONE) {
            actionBar.hide();
        } else {
            Print.w(TAG, "WARNING: INVALID FLAG, USE VISIBLE/INVISIBLE FROM View.");
        }
    }

//    /**
//     * Change actionBar visibility if necessary and executes runnable
//     */
//    public void setActionBarVisibility(int showActionBar, Runnable onChangeRunnable, int onChangePostDelay) {
//        boolean actionBarVisible = getSupportActionBar().isShowing();
//        setActionBarVisibility(showActionBar);
//
//        if (getSupportActionBar().isShowing() != actionBarVisible) {
//            new Handler().postDelayed(onChangeRunnable, onChangePostDelay);
//        }
//    }

    /**
     * Set the up button in ActionBar
     *
     * @author sergiopereira
     */
    private void setActionBarUpButton() {
        if (isBackButtonEnabled) {
            Print.i(TAG, "SHOW UP BUTTON");
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        } else {
            Print.i(TAG, "NO SHOW UP BUTTON");
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
    }


    /**
     * Set the share menu item
     *
     * @modified sergiopereira
     */
    @SuppressWarnings("unused")
    private void setActionShare() {
        //menu.findItem(MyMenuItem.BASKET.resId).setVisible(true);
        //menu.findItem(item.resId).setEnabled(true);
        //mShareActionProvider = (ShareActionProvider) menu.findItem(item.resId).getActionProvider();
        //mShareActionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
        //            @Override
        //            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        //                getApplicationContext().startActivity(intent);
        //                TrackerDelegator.trackItemShared(getApplicationContext(), intent);
        //                return true;
        //            }
        //        });
        //mShareActionProvider.setShareHistoryFileName(null);
        //mShareActionProvider.setShareIntent(shareIntent);
        //setShareIntent(createShareIntent());
    }

    /**
     * Set the cart menu item
     */
    private void setActionCart(final Menu menu) {
        MenuItem basket = menu.findItem(MyMenuItem.BASKET.resId);
        // Validate country
        if (!initialCountry) {
            basket.setVisible(true);
            basket.setEnabled(true);
            View actionCartView = MenuItemCompat.getActionView(basket);
            mActionCartCount = (TextView) actionCartView.findViewById(R.id.action_cart_count);
            View actionCartImage = actionCartView.findViewById(R.id.action_cart_image);
            actionCartImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu.performIdentifierAction(MyMenuItem.BASKET.resId, 0);
                }
            });
            updateCartInfoInActionBar();
        } else {
            basket.setVisible(false);
        }
    }
    
    
    /*
     * ############### SEARCH COMPONENT #################
     */

    /**
     * Method used to set the search bar in the Action bar.
     * @author Andre Lopes
     * @modified sergiopereira
     */
    private void setActionSearch(Menu menu) {
        Print.i(TAG, "ON OPTIONS MENU: CREATE SEARCH VIEW");
        // Get search menu item
        mSearchMenuItem = menu.findItem(R.id.menu_search);
        // Get search action view
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchView.setQueryHint(getString(R.string.action_label_search_hint, getString(R.string.app_name_placeholder)));
        // Get edit text
        mSearchAutoComplete = (SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        //#RTL
        if (ShopSelector.isRtl()) {
            mSearchAutoComplete.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        }
        // Set font
        HoloFontLoader.applyDefaultFont(mSearchView);
        HoloFontLoader.applyDefaultFont(mSearchAutoComplete);
        // Set the ime options
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // Set text color for old android versions
        mSearchAutoComplete.setTextColor(getResources().getColor(R.color.search_edit_color));
        mSearchAutoComplete.setHintTextColor(getResources().getColor(R.color.search_hint_color));
        // Initial state
        MenuItemCompat.collapseActionView(mSearchMenuItem);
        // Calculate the max width to fill action bar
        setSearchWidthToFillOnExpand();
        // Set search
        setActionBarSearchBehavior(mSearchMenuItem);
        // Set visibility
        mSearchMenuItem.setVisible(true);
    }


    private void setSearchWidthToFillOnExpand() {
        // Get the width of main content
        // logoView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        // int logoViewWidth = logoView.getMeasuredWidth() + logoView.getPaddingRight();
        int mainContentWidth = DeviceInfoHelper.getWidth(getApplicationContext());
        int genericIconWidth = getResources().getDimensionPixelSize(R.dimen.item_height_normal);
        // Calculate the search width
        int searchComponentWidth = mainContentWidth - genericIconWidth;
        Print.d(TAG, "SEARCH WIDTH SIZE: " + searchComponentWidth);
        // Set measures
        mSearchView.setMaxWidth(searchComponentWidth);
        mSearchAutoComplete.setDropDownWidth(searchComponentWidth);
    }

    /**
     * Set the search component
     */
    public void setActionBarSearchBehavior(final MenuItem mSearchMenuItem) {
        Print.d(TAG, "SEARCH MODE: NEW BEHAVIOUR");
        if (mSearchAutoComplete == null) {
            Print.w(TAG, "SEARCH COMPONENT IS NULL");
            return;
        }
        
        /*
         * Set on item click listener
         */
        mSearchAutoComplete.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Print.d(TAG, "SEARCH: CLICKED ITEM " + position);
                // Get suggestion
                Suggestion selectedSuggestion = (Suggestion) adapter.getItemAtPosition(position);
                // Get text suggestion
                String text = selectedSuggestion.getResult();
                // Clean edit text
                mSearchAutoComplete.setText("");
                mSearchAutoComplete.dismissDropDown();
                // Collapse search view
                MenuItemCompat.collapseActionView(mSearchMenuItem);
                // Save query
                GetSearchSuggestionsHelper.saveSearchQuery(text);
                // Show query
                showSearchCategory(text);
                if (JumiaApplication.INSTANCE != null) {
                    JumiaApplication.INSTANCE.trackSearch = true;
                }
            }
        });

        /*
         * Clear and add text listener
         */
        // mSearchAutoComplete.clearTextChangedListeners();
        mSearchAutoComplete.addTextChangedListener(new TextWatcher() {
            private Handler handle = new Handler();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                handle.removeCallbacks(run);
                if (s.length() >= SEARCH_EDIT_SIZE && isSearchComponentOpened) {
                    handle.postDelayed(run, SEARCH_EDIT_DELAY);
                }
            }
        });

        /*
         * Set IME action listener
         */
        mSearchAutoComplete.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(android.widget.TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO) {
                    String searchTerm = textView.getText().toString();
                    Print.d(TAG, "SEARCH COMPONENT: ON IME ACTION " + searchTerm);
                    if (TextUtils.isEmpty(searchTerm)) {
                        return false;
                    }
                    // Clean edit text
                    textView.setText("");
                    // Collapse search view
                    MenuItemCompat.collapseActionView(mSearchMenuItem);
                    // Save query
                    GetSearchSuggestionsHelper.saveSearchQuery(searchTerm);
                    // Show query
                    showSearchCategory(searchTerm);
                    return true;
                }
                return false;
            }
        });

        /*
         * Set expand listener
         */
        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Print.d(TAG, "SEARCH ON EXPAND");
                closeNavigationDrawer();
                isSearchComponentOpened = true;
                setActionMenuItemsVisibility(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Print.d(TAG, "SEARCH ON COLLAPSE");
                isSearchComponentOpened = false;
                setActionMenuItemsVisibility(true);
                return true;
            }
        });
        
        /*
         * Set focus listener, for back pressed with IME
         */
        mSearchAutoComplete.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MenuItemCompat.collapseActionView(mSearchMenuItem);
                    setActionMenuItemsVisibility(true);
                }
            }
        });

    }

    /**
     * Execute search
     * @author sergiopereira
     */
    protected void showSearchCategory(String searchText) {
        Print.d(TAG, "SEARCH COMPONENT: GOTO PROD LIST");
        // Tracking
        JumiaApplication.INSTANCE.trackSearchCategory = true;
        TrackerDelegator.trackSearchSuggestions(searchText);
        // Data
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, searchText);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, searchText);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
        onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * set all menu items visibility to <code>visible</code>
     */
    protected void setActionMenuItemsVisibility(boolean visible) {
        for (MyMenuItem item : menuItems) {
            if (item != MyMenuItem.SEARCH_VIEW && item.resId != -1) {
                MenuItem view = mCurrentMenu.findItem(item.resId);
                if (view != null) {
                    view.setVisible(visible);
                }
            }
        }
    }

    /**
     * Hide the search component
     *
     * @author sergiopereira
     */
    public void hideSearchComponent() {
        Print.d(TAG, "SEARCH COMPONENT: HIDE");
        try {
            // Validate if exist search icon and bar
            if (menuItems.contains(MyMenuItem.SEARCH_VIEW)) {
                // Hide search bar
                MenuItemCompat.collapseActionView(mSearchMenuItem);
                // Clean autocomplete
                mSearchAutoComplete.setText("");
                // Show hidden items
                setActionMenuItemsVisibility(true);
                // Forced the IME option on collapse
                mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            }
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING NPE ON HIDE SEARCH COMPONENT");
        }
    }

    /**
     * Hide only the search bar, used by ChangeCountryFragment
     * @author sergiopereira
     */
    public void hideActionBarItemsForChangeCountry(EnumSet<MyMenuItem> enumSet) {
        this.menuItems = enumSet;
        this.action = NavigationAction.Country;
        updateActionForCountry(NavigationAction.Country);
        invalidateOptionsMenu();
    }

    /*
     * ############### SEARCH TRIGGER #################
     */

    /**
     * Runnable to get suggestions
     *
     * @author sergiopereira
     */
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            Print.i(TAG, "SEARCH: RUN GET SUGGESTIONS: " + mSearchAutoComplete.getText().toString());
            getSuggestions();
        }
    };

    /**
     * Get suggestions and recent queries
     *
     * @author sergiopereira
     */
    private void getSuggestions() {
        beginInMillis = System.currentTimeMillis();
        String text = mSearchAutoComplete.getText().toString();
        Print.d(TAG, "SEARCH COMPONENT: GET SUG FOR " + text);

        Bundle bundle = new Bundle();
        bundle.putString(GetSearchSuggestionsHelper.SEACH_PARAM, text);
        JumiaApplication.INSTANCE.sendRequest(new GetSearchSuggestionsHelper(), bundle,
                new IResponseCallback() {

                    @Override
                    public void onRequestError(Bundle bundle) {
                        processErrorSearchEvent(bundle);
                    }

                    @Override
                    public void onRequestComplete(Bundle bundle) {
                        processSuccessSearchEvent(bundle);
                    }
                });
    }

    /**
     * ############### SEARCH RESPONSES #################
     */

    /**
     * Process the search error event
     * @author sergiopereira
     */
    private void processErrorSearchEvent(Bundle bundle) {
        Print.d(TAG, "SEARCH COMPONENT: ON ERROR");
        // Get query
        String requestQuery = bundle.getString(GetSearchSuggestionsHelper.SEACH_PARAM);
        Print.d(TAG, "RECEIVED SEARCH ERROR EVENT: " + requestQuery);
        // Validate current search component
        if (mSearchAutoComplete != null && !mSearchAutoComplete.getText().toString().equals(requestQuery)) {
            Print.w(TAG, "SEARCH ERROR: WAS DISCARTED FOR QUERY " + requestQuery);
            return;
        }
        if (!mCurrentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId).isVisible()) {
            return;
        }
        // Hide dropdown
        mSearchAutoComplete.dismissDropDown();
        
        /*-- // Show no network dialog
        if(!NetworkConnectivity.isConnected(getApplicationContext())) {
            if(dialog != null) dialog.dismissAllowingStateLoss();
            // Show
            dialog = DialogGenericFragment.createNoNetworkDialog(this, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismissAllowingStateLoss();
                    if(mSearchAutoComplete != null) getSuggestions();
                }
            }, 
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismissAllowingStateLoss();
                }
            }, false);
            dialog.show(getSupportFragmentManager(), null);
        }
         */
    }

    /**
     * Process success search event
     * @author sergiopereira
     */
    private void processSuccessSearchEvent(Bundle bundle) {
        Print.d(TAG, "SEARCH COMPONENT: ON SUCCESS");
        // Get suggestions
        List<Suggestion> sug = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        // Get query
        String requestQuery = bundle.getString(GetSearchSuggestionsHelper.SEACH_PARAM);
        Print.d(TAG, "RECEIVED SEARCH EVENT: " + sug + " " + requestQuery);

        // Validate current objects
        if (menuItems == null || mCurrentMenu == null || mSearchAutoComplete == null) {
            return;
        }
        // Validate current menu items
        if (!menuItems.contains(MyMenuItem.SEARCH_VIEW)) {
            return;
        }
        MenuItem searchMenuItem = mCurrentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId);
        if (searchMenuItem != null && !searchMenuItem.isVisible()) {
            return;
        }
        // Validate current search
        if (mSearchAutoComplete.getText().length() < SEARCH_EDIT_SIZE
                || !mSearchAutoComplete.getText().toString().equals(requestQuery)) {
            Print.w(TAG, "SEARCH: DISCARDED DATA FOR QUERY " + requestQuery);
            return;
        }
        // Show suggestions
        Print.i(TAG, "SEARCH: SHOW DATA FOR QUERY " + requestQuery);
        Bundle params = new Bundle();
        params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gsearchsuggestions);
        params.putLong(TrackerDelegator.START_TIME_KEY, beginInMillis);
        TrackerDelegator.trackLoadTiming(params);
        SearchDropDownAdapter searchSuggestionsAdapter = new SearchDropDownAdapter(getApplicationContext(), sug, requestQuery);
        mSearchAutoComplete.setAdapter(searchSuggestionsAdapter);
        mSearchAutoComplete.showDropDown();
    }

    /**
     * #################### SHARE #####################
     */

    /**
     * Called to update the share intent
     *
     * @param shareIntent
     *            the intent to be stored
     */
    /*-public void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareHistoryFileName(null);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }*/

    /**
     * Displays the number of items that are currently on the shopping cart as well as its value. This information is displayed on the navigation list
     */
    public void updateCartInfo() {
        Print.d(TAG, "ON UPDATE CART INFO");
//        if (JumiaApplication.INSTANCE.getCart() != null) {
//            Log.d(TAG, "updateCartInfo value = "
//                    + JumiaApplication.INSTANCE.getCart().getCartValue() + " quantity = "
//                    + JumiaApplication.INSTANCE.getCart().getCartCount());
//        }
        updateCartInfoInActionBar();
        updateTabCartInfo();
    }

    public void updateCartInfoInActionBar() {
        Print.d(TAG, "ON UPDATE CART IN ACTION BAR");
        if (mActionCartCount == null) {
            Print.w(TAG, "updateCartInfoInActionBar: cant find quantity in actionbar");
            return;
        }

        PurchaseEntity currentCart = JumiaApplication.INSTANCE.getCart();
        // Show 0 while the cart is not updated
        final String quantity = currentCart == null ? "0" : String.valueOf(currentCart.getCartCount());

        mActionCartCount.post(new Runnable() {
            @Override
            public void run() {
                mActionCartCount.setText(quantity);
            }
        });

    }

    private void updateTabCartInfo() {
        // Update the cart tab
        try {
            // Show 0 while the cart is not updated
            String quantity = JumiaApplication.INSTANCE.getCart() == null ? "0" : String.valueOf(JumiaApplication.INSTANCE.getCart().getCartCount());
            //noinspection ConstantConditions
            ((TextView) mTabLayout.getTabAt(2).getCustomView().findViewById(R.id.tab_cart_number_products)).setText(quantity);
        } catch (NullPointerException e) {
            // ...
        }
    }


    /**
     * Create the share intent to be used to store the needed information
     *
     * @return The created intent
     */
    public Intent createShareIntent(String extraSubject, String extraText) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, extraSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        return sharingIntent;
    }

    /**
     * ################# MY PROFILE #################
     */

    /**
     * Method used to set the myProfile Menu
     *
     * @author Andre Lopes
     * @modified sergiopereira
     */
    private void setActionProfile(Menu menu) {
        MenuItem myProfile = menu.findItem(MyMenuItem.MY_PROFILE.resId);
        // Validate
        if (myProfile != null) {
            myProfile.setVisible(true);
            myProfile.setEnabled(true);
            myProfileActionProvider = (MyProfileActionProvider) MenuItemCompat.getActionProvider(myProfile);
            myProfileActionProvider.setFragmentNavigationAction(action);
            myProfileActionProvider.setAdapterOnClickListener(myProfileClickListener);
        }
    }

    OnClickListener myProfileClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean hideMyProfile = true;

            NavigationAction navAction = (NavigationAction) v.getTag(R.id.nav_action);
            if (navAction != null && getAction() != navAction) {
                switch (navAction) {
                    case MyProfile:
                        // MY PROFILE
                        hideMyProfile = false;
                        // Close Drawer
                        closeNavigationDrawer();
                        // Validate provider
                        if (myProfileActionProvider != null) {
                            myProfileActionProvider.showSpinner();
                        }
                        break;
                    case Home:
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_HOME);
                        onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case LoginOut:
                        // SIGN IN
                        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
                            FragmentManager fm = getSupportFragmentManager();
                            dialogLogout = DialogGenericFragment.newInstance(true, false,
                                    getString(R.string.logout_title),
                                    getString(R.string.logout_text_question),
                                    getString(R.string.no_label), getString(R.string.yes_label),
                                    new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.button2) {
                                                LogOut.performLogOut(new WeakReference<Activity>(BaseActivity.this));
                                            }
                                            dialogLogout.dismiss();
                                        }
                                    });
                            dialogLogout.show(fm, null);
                        } else {
                            TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_SIGN_IN);
                            onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        }
                        break;
                    case Saved:
                        // FAVOURITES
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_FAVORITE);
                        // Validate customer is logged in
                        if (!JumiaApplication.isCustomerLoggedIn()) {
                            // Goto Login and next WishList
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.WISH_LIST);
                            onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
                        } else {
                            onSwitchFragment(FragmentType.WISH_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        }
                        break;
                    case RecentSearches:
                        // RECENT SEARCHES
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENT_SEARCHES);
                        onSwitchFragment(FragmentType.RECENT_SEARCHES_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case RecentlyViewed:
                        // RECENTLY VIEWED
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENTLY_VIEW);
                        onSwitchFragment(FragmentType.RECENTLY_VIEWED_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case MyAccount:
                        // MY ACCOUNT
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_MY_ACCOUNT);
                        onSwitchFragment(FragmentType.MY_ACCOUNT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case MyOrders:
                        // TRACK ORDER
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_TRACK_ORDER);
                        onSwitchFragment(FragmentType.MY_ORDERS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case Country:
                        onSwitchFragment(FragmentType.CHOOSE_COUNTRY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    default:
                        Print.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
                        break;
                }
            } else {
                Print.d(TAG, "selected navAction is already being shown");
            }

            // only hide dropdown for Spinner if hideMyProfile flag is activated
            if (hideMyProfile && myProfileActionProvider != null) {
                myProfileActionProvider.dismissSpinner();
            }
        }
    };

    /**
     * @return the action
     */
    public NavigationAction getAction() {
        return action;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#setTitle(java.lang.CharSequence)
     */
    @Override
    public void setTitle(CharSequence title) {
        TextView titleView = (TextView) findViewById(R.id.titleProducts);
        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
        View headerTitle = findViewById(R.id.header_title);
        subtitleView.setVisibility(View.GONE);
        if (headerTitle == null) {
            return;
        }

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            headerTitle.setVisibility(View.VISIBLE);
        } else {
            headerTitle.setVisibility(View.GONE);
        }
    }

//    /**
//     * Method used to set the number of products
//     */
//    public void setTitleAndSubTitle(CharSequence title, CharSequence subtitle) {
//        TextView titleView = (TextView) findViewById(R.id.titleProducts);
//        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
//        View headerTitle = findViewById(R.id.header_title);
//
//        if (titleView == null) {
//            return;
//        }
//        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(subtitle)) {
//            Print.d(TAG, "------------->>>>>>>>>>>>>> SET TITLE ->" + title + "; " + subtitle);
//            // Set text and force measure
//            subtitleView.setText(subtitle);
//            // Set title
//            titleView.setText(title);
//            // Set visibility
//            headerTitle.setVisibility(View.VISIBLE);
//            subtitleView.setVisibility(View.VISIBLE);
//        } else if (TextUtils.isEmpty(title)) {
//            headerTitle.setVisibility(View.GONE);
//        }
//    }

//    /**
//     * Method used to set the number of products
//     */
//    public void setSubTitle(CharSequence subtitle) {
//        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
//        View headerTitle = findViewById(R.id.header_title);
//
//        if (subtitleView == null) {
//            return;
//        }
//        if (!TextUtils.isEmpty(subtitle)) {
//            // Set text and force measure
//            subtitleView.setText(subtitle);
//
//            headerTitle.setVisibility(View.VISIBLE);
//            subtitleView.setVisibility(View.VISIBLE);
//        } else if (TextUtils.isEmpty(subtitle)) {
//            headerTitle.setVisibility(View.GONE);
//        }
//    }

    public void hideTitle() {
        findViewById(R.id.header_title).setVisibility(View.GONE);
    }

//    /**
//     * get the category tree title
//     *
//     * @return subtitle
//     */
//    public String getCategoriesTitle() {
//        TextView titleView = (TextView) findViewById(R.id.titleProducts);
//        if (!TextUtils.isEmpty(titleView.getText().toString())) {
//            return titleView.getText().toString();
//        } else {
//            return "";
//        }
//    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#setTitle(int)
     */
    @Override
    public void setTitle(int titleId) {
        if (titleId != 0) {
            setTitle(getString(titleId));
        }
    }

    /**
     * Show and set title on actionbar
     */
    public void setActionBarTitle(@StringRes int actionBarTitleResId) {
        //logoTextView.setVisibility(View.VISIBLE);
        //logoTextView.setText(getString(actionBarTitleResId));
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setTitle(getString(actionBarTitleResId));
        mSupportActionBar.setTitle(getString(actionBarTitleResId));
    }

    public void setActionBarTitle(@NonNull String title) {
        mSupportActionBar.setTitle(title);
    }

    /**
     * Hide title on actionbar
     */
    public void hideActionBarTitle() {
        //logoTextView.setVisibility(View.GONE);
        //getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        mSupportActionBar.setTitle("");
    }

//    private void setAppContentLayout() {
//        if (contentLayoutId == 0) {
//            return;
//        }
//        ViewStub stub = (ViewStub) findViewById(R.id.stub_app_content);
//        stub.setLayoutResource(contentLayoutId);
//        contentContainer = stub.inflate();
//    }

    /**
     * Show progress.
     *
     * @modified sergiopereira
     */
    public final void showProgress() {
        // Validate current progress dialog
        if (baseActivityProgressDialog != null) {
            return;
        }
        // FIXME: New implementation
        // Try fix android.view.WindowManager$BadTokenException: Unable to add window -- token
        // android.os.BinderProxy@42128698 is not valid; is your activity running?
        if (!isFinishing()) {
            baseActivityProgressDialog = DialogProgressFragment.newInstance();
            baseActivityProgressDialog.show(getSupportFragmentManager(), null);
        }
    }

    public final void dismissProgress() {
        if (baseActivityProgressDialog != null) {
            baseActivityProgressDialog.dismissAllowingStateLoss();
            baseActivityProgressDialog = null;
        }
    }

//    /**
//     * Service Stuff
//     */
//
//    public void unbindDrawables(View view) {
//
//        try {
//            if (view.getBackground() != null) {
//                view.getBackground().setCallback(null);
//            } else if (view instanceof ViewGroup) {
//                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                    unbindDrawables(((ViewGroup) view).getChildAt(i));
//                }
//                if (view instanceof AdapterView<?>) {
//                    return;
//                }
//                try {
//                    ((ViewGroup) view).removeAllViews();
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        } catch (RuntimeException e) {
//            Print.w(TAG, "" + e);
//        }
//
//    }

    public void hideKeyboard() {
        Print.i(TAG, "HIDE KEYBOARD");
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = mDrawerLayout;
        if (v == null) {
            v = getWindow().getCurrentFocus();
        }
        if (v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

//    public void showKeyboard() {
//        // Log.d( TAG, "showKeyboard" );
//        Print.i(TAG, "code1here showKeyboard");
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0);
//        // use the above as the method below does not always work
//        // imm.showSoftInput(getSlidingMenu().getCurrentFocus(),
//        // InputMethodManager.SHOW_IMPLICIT);
//    }

    public void onLogOut() {
        /*
         * NOTE: Others sign out methods are performed in {@link LogOut}.
         */
        // Logout Facebook
        FacebookHelper.facebookLogout();
        // Track logout
        TrackerDelegator.trackLogoutSuccessful();
        // Goto Home
        onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        // Hide progress
        dismissProgress();
    }

    /**
     * Set action
     */
    public void setAction(NavigationAction action) {
        this.action = action;
    }

    /**
     * ############### FRAGMENTS #################
     */

    /**
     * This method should be implemented by fragment activity to manage the work flow for fragments. Each fragment should call this method.
     */
    public abstract void onSwitchFragment(FragmentType type, Bundle bundle, Boolean addToBackStack);

    /**
     * Method used to switch fragment on UI with/without back stack support
     */
    public void fragmentManagerTransition(int container, Fragment fragment, FragmentType fragmentType, Boolean addToBackStack) {
        fragmentController.startTransition(this, container, fragment, fragmentType, addToBackStack);
    }

    /**
     * Method used to perform a back stack using fragments
     *
     * @author sergiopereira
     */
    public void fragmentManagerBackPressed() {
        fragmentController.fragmentBackPressed(this);
    }

    /**
     * Pop back stack until tag, FC and FM are affected.
     *
     * @param tag - The fragment tag
     * @author sergiopereira
     */
    public boolean popBackStackUntilTag(String tag) {
        if (fragmentController.hasEntry(tag)) {
            fragmentController.popAllEntriesUntil(this, tag);
            return true;
        }
        return false;
    }

    /**
     * Pop back stack entries until tag.
     *
     * @param tag - The fragment tag
     * @author sergiopereira
     */
    public void popBackStackEntriesUntilTag(String tag) {
        if (fragmentController.hasEntry(tag)) {
            fragmentController.removeEntriesUntilTag(tag);
        }
    }

    /**
     * Method used to control the double back pressed
     *
     * @author sergiopereira
     * @see <a href="http://stackoverflow.com/questions/7965135/what-is-the-duration-of-a-toast-length-long-and-length-short">Toast duration</a> <br>
     * Toast.LENGTH_LONG is 3500 seconds. <br> Toast.LENGTH_SHORT is 2000 seconds.
     */
    public void doubleBackPressToExit() {
        Print.d(TAG, "DOUBLE BACK PRESSED TO EXIT: " + backPressedOnce);
        // If was pressed once
        if (backPressedOnce) {
            fragmentController.popLastEntry();
            finish();
            return;
        }
        // First time show toast
        this.backPressedOnce = true;
        CustomToastView.makeText(this, getString(R.string.exit_press_back_again), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, TOAST_LENGTH_SHORT);
    }

    public void restartAppFlow() {
        // Clear Ad4Push prefs
        Ad4PushTracker.clearAllSavedData(this);
        // Show splash screen
        ActivitiesWorkFlow.splashActivityNewTask(this);
        // Finish MainFragmentActivity
        finish();
    }

    /*
     * ########## CHECKOUT HEADER ##########
     */

    /**
     * Set the current checkout step otherwise return false
     */
    public boolean setCheckoutHeader(int checkoutStep) {
        Print.d(TAG, "SET CHECKOUT HEADER STEP ID: " + checkoutStep);

        int visibility = View.VISIBLE;
        boolean result = true;
        switch (checkoutStep) {
            case ConstantsCheckout.CHECKOUT_ABOUT_YOU:
                selectCheckoutStep(ConstantsCheckout.CHECKOUT_ABOUT_YOU);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_BILLING);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_SHIPPING);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_PAYMENT);
                updateBaseComponentsInCheckout(visibility);
                break;
            case ConstantsCheckout.CHECKOUT_BILLING:
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_ABOUT_YOU);
                selectCheckoutStep(ConstantsCheckout.CHECKOUT_BILLING);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_SHIPPING);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_PAYMENT);
                updateBaseComponentsInCheckout(visibility);
                break;
            case ConstantsCheckout.CHECKOUT_SHIPPING:
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_ABOUT_YOU);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_BILLING);
                selectCheckoutStep(ConstantsCheckout.CHECKOUT_SHIPPING);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_PAYMENT);
                updateBaseComponentsInCheckout(visibility);
                break;
            case ConstantsCheckout.CHECKOUT_PAYMENT:
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_ABOUT_YOU);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_BILLING);
                unSelectCheckoutStep(ConstantsCheckout.CHECKOUT_SHIPPING);
                selectCheckoutStep(ConstantsCheckout.CHECKOUT_PAYMENT);
                updateBaseComponentsInCheckout(visibility);
                break;
            case ConstantsCheckout.CHECKOUT_ORDER:
            case ConstantsCheckout.CHECKOUT_THANKS:
                visibility = View.GONE;
                updateBaseComponentsInCheckout(visibility);
                break;
            case ConstantsCheckout.CHECKOUT_NO_SET_HEADER:
                // Hide title and total
                hideTitle();
                findViewById(R.id.totalProducts).setVisibility(View.GONE);
                break;
            case ConstantsCheckout.NO_CHECKOUT:
                visibility = View.GONE;
                result = false;
                updateBaseComponentsOutCheckout(visibility);
                break;
            default:
                Print.e(TAG, "checkoutStep unknown");
                visibility = View.GONE;
                result = false;
                updateBaseComponentsOutCheckout(visibility);
                break;
        }
        // Return value
        return result;
    }

    /**
     * Update the base components out checkout
     */
    private void updateBaseComponentsOutCheckout(int visibility) {
        Print.d(TAG, "SET BASE FOR NON CHECKOUT: HIDE");
        // Set header visibility
        findViewById(R.id.checkout_header_main_step).setVisibility(visibility);
        findViewById(R.id.checkout_header).setVisibility(visibility);
    }

    /**
     * Update the base components in checkout
     */
    private void updateBaseComponentsInCheckout(int visibility) {
        Print.d(TAG, "SET BASE FOR CHECKOUT: SHOW");
        // Set header visibility
        findViewById(R.id.checkout_header_main_step).setVisibility(visibility);
        findViewById(R.id.checkout_header).setVisibility(visibility);
        // Hide title and prod
        hideTitle();
        findViewById(R.id.totalProducts).setVisibility(View.GONE);
    }

    /**
     * Unselect the a checkout step
     */
    private void unSelectCheckoutStep(int step) {
        switch (step) {
            case ConstantsCheckout.CHECKOUT_ABOUT_YOU:
                unSelectStep(R.id.checkout_header_step_1, R.id.checkout_header_step_1_icon, R.id.checkout_header_step_1_text);
                break;
            case ConstantsCheckout.CHECKOUT_BILLING:
                unSelectStep(R.id.checkout_header_step_2, R.id.checkout_header_step_2_icon, R.id.checkout_header_step_2_text);
                break;
            case ConstantsCheckout.CHECKOUT_SHIPPING:
                unSelectStep(R.id.checkout_header_step_3, R.id.checkout_header_step_3_icon, R.id.checkout_header_step_3_text);
                break;
            case ConstantsCheckout.CHECKOUT_PAYMENT:
                unSelectStep(R.id.checkout_header_step_4, R.id.checkout_header_step_4_icon, R.id.checkout_header_step_4_text);
                break;
            default:
                break;
        }
    }

    /**
     * Set the selected checkout step
     */
    private void selectCheckoutStep(int step) {
        switch (step) {
            case ConstantsCheckout.CHECKOUT_ABOUT_YOU:
                selectStep(R.id.checkout_header_step_1, R.id.checkout_header_step_1_icon, R.id.checkout_header_step_1_text);
                break;
            case ConstantsCheckout.CHECKOUT_BILLING:
                selectStep(R.id.checkout_header_step_2, R.id.checkout_header_step_2_icon, R.id.checkout_header_step_2_text);
                break;
            case ConstantsCheckout.CHECKOUT_SHIPPING:
                selectStep(R.id.checkout_header_step_3, R.id.checkout_header_step_3_icon, R.id.checkout_header_step_3_text);
                break;
            case ConstantsCheckout.CHECKOUT_PAYMENT:
                selectStep(R.id.checkout_header_step_4, R.id.checkout_header_step_4_icon, R.id.checkout_header_step_4_text);
                break;
            default:
                break;
        }
    }

    /**
     * Set a step selected
     */
    private void selectStep(int main, int icon, int text) {
        findViewById(main).setSelected(true);
        findViewById(main).getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.checkout_header_step_selected_width);
        findViewById(icon).setSelected(true);
        findViewById(text).setVisibility(View.VISIBLE);
    }

    /**
     * Set a step unselected
     */
    private void unSelectStep(int main, int icon, int text) {
        findViewById(main).setSelected(false);
        findViewById(main).getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.checkout_header_step_width);
        findViewById(icon).setSelected(false);
        findViewById(text).setVisibility(View.GONE);
    }

    /**
     * Checkout header click listener associated to each item on layout
     */
    public void onCheckoutHeaderClickListener(View view) {
        Print.i(TAG, "PROCESS CLICK ON CHECKOUT HEADER");
        int id = view.getId();
        /*
        // CHECKOUT_ABOUT_YOU
        if (id == R.id.checkout_header_step_1 && !view.isSelected()) {
            // Uncomment if you want click on about you step
            // removeCheckoutEntries();
            // onSwitchFragment(FragmentType.ABOUT_YOU,
            // FragmentController.NO_BUNDLE,
            // FragmentController.ADD_TO_BACK_STACK);
        }
        else
        */
        // CHECKOUT_BILLING
        if (id == R.id.checkout_header_step_2 && !view.isSelected()) {
            // Validate back stack
            if(!popBackStackUntilTag(FragmentType.MY_ADDRESSES.toString()) && fragmentController.hasEntry(FragmentType.CREATE_ADDRESS.toString())){
                removeAllNativeCheckoutFromBackStack();
                onSwitchFragment(FragmentType.ABOUT_YOU, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }

        }
        // CHECKOUT_SHIPPING
        else if (id == R.id.checkout_header_step_3 && !view.isSelected()) {
            // Validate back stack
            popBackStackUntilTag(FragmentType.SHIPPING_METHODS.toString());
        }
        // CHECKOUT_PAYMENT IS THE LAST
    }

    /**
     * Method used to remove all native checkout entries from the back stack on the Fragment Controller
     * Note: This method must be updated in case of adding more screens to native checkout.
     * @author ricardosoares
     */
    public void removeAllNativeCheckoutFromBackStack(){
        // Remove all native checkout tags
        FragmentController.getInstance().removeAllEntriesWithTag(CheckoutStepManager.getAllNativeCheckout());
    }

    /*
     * ##### REQUESTS TO RECOVER #####
     */

    /**
     * Recover the user data when comes from background.
     *
     * @author sergiopereira
     */
    private void recoverUserDataFromBackground() {
        Print.i(TAG, "ON TRIGGER: INITIALIZE USER DATA");
        // Validate the user credentials
        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() && !JumiaApplication.isCustomerLoggedIn()) {
            triggerAutoLogin();
        } else {
            // Track auto login failed if hasn't saved credentials
            TrackerDelegator.trackLoginFailed(TrackerDelegator.IS_AUTO_LOGIN, GTMValues.LOGIN, GTMValues.EMAILAUTH);
        }
        // Validate the user credentials
        if (JumiaApplication.SHOP_ID != null && JumiaApplication.INSTANCE.getCart() == null) {
            triggerGetShoppingCartItemsHelper();
        }
    }

    /**
     * Get cart
     */
    public void triggerGetShoppingCartItemsHelper() {
        Print.i(TAG, "TRIGGER SHOPPING CART ITEMS");
        Bundle bundle = new Bundle();
        //bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, false);
        JumiaApplication.INSTANCE.sendRequest(new GetShoppingCartItemsHelper(), bundle, new IResponseCallback() {
            @Override
            public void onRequestError(Bundle bundle) {
                Print.i(TAG, "ON REQUEST ERROR: CART");
                //...
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                Print.i(TAG, "ON REQUEST COMPLETE: CART");
                updateCartInfo();
            }
        });
    }

    /**
     * Auto login
     */
    private void triggerAutoLogin() {
        Print.i(TAG, "ON TRIGGER: AUTO LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        JumiaApplication.INSTANCE.sendRequest(new GetLoginHelper(), bundle, new IResponseCallback() {
            @Override
            public void onRequestError(Bundle bundle) {
                Print.i(TAG, "ON REQUEST ERROR: AUTO LOGIN");
                JumiaApplication.INSTANCE.setLoggedIn(false);
                JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                Print.i(TAG, "ON REQUEST COMPLETE: AUTO LOGIN");
                // Set logged in
                JumiaApplication.INSTANCE.setLoggedIn(true);
                // Get customer
                Customer customer = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                // Get origin
                ContentValues credentialValues = JumiaApplication.INSTANCE.getCustomerUtils().getCredentials();
                boolean isFBLogin = credentialValues.getAsBoolean(CustomerUtils.INTERNAL_FACEBOOK_FLAG);
                // Track
                Bundle params = new Bundle();
                params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
                params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, TrackerDelegator.IS_AUTO_LOGIN);
                params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, isFBLogin);
                params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.HOME);
                TrackerDelegator.trackLoginSuccessful(params);
                trackPageAdjust();
            }
        });
    }

    /**
     * Track Page only for adjust
     */
    private void trackPageAdjust() {
        Bundle bundle = new Bundle();
        bundle.putLong(AdjustTracker.BEGIN_TIME, mLaunchTime);
        TrackerDelegator.trackPageForAdjust(TrackingPage.HOME, bundle);
    }

    public boolean communicateBetweenFragments(String tag, Bundle bundle){
        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null){
            ((BaseFragment)fragment).notifyFragment(bundle);
            return true;
        }
        return false;
    }

//    /**
//     * Shows server overload page
//     */
//    public void showOverLoadView(){
//
//        Intent intent = new Intent(getApplicationContext(), OverLoadErrorActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        //if(getSupportFragmentManager() != null){
//        //    OverlayDialogFragment.getInstance(R.layout.kickout_page).show(getSupportFragmentManager(),null);
//        //}
//    }
}