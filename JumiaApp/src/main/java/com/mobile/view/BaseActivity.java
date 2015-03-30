package com.mobile.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.SearchAutoComplete;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.mobile.framework.database.FavouriteTableHelper;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.objects.SearchSuggestion;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.service.IRemoteServiceCallback;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.AnalyticsGoogle;
import com.mobile.framework.tracking.TrackingEvent;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.tracking.gtm.GTMValues;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CustomerUtils;
import com.mobile.framework.utils.DeviceInfoHelper;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.framework.utils.ShopSelector;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.search.GetSearchSuggestionHelper;
import com.mobile.helpers.session.GetLoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.CheckVersion;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.MyProfileActionProvider;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.CustomToastView;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogProgressFragment;
import com.mobile.utils.maintenance.MaintenancePage;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.fragments.BaseFragment.KeyboardState;
import com.mobile.view.fragments.HomeFragment;
import com.mobile.view.fragments.NavigationFragment;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import de.akquinet.android.androlog.Log;

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
public abstract class BaseActivity extends ActionBarActivity {

    private static final String TAG = LogTagHelper.create(BaseActivity.class);

    // private ShareActionProvider mShareActionProvider;

    private static final int SEARCH_EDIT_DELAY = 500;

    private static final int SEARCH_EDIT_SIZE = 2;

    private static final int TOAST_LENGTH_SHORT = 2000; // 2 seconds

    private static final int WARNING_LENGTH = 4000;

    // REMOVED FINAL ATRIBUTE
    private NavigationAction action;

    protected View contentContainer;

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

    private boolean isRegistered = false;

    private View mWarningBar;

    private final int titleResId;

    private final int contentLayoutId;

    private TextView mActionCartCount;

    private MyProfileActionProvider myProfileActionProvider;

    private FragmentController fragmentController;

    private boolean initialCountry = false;

    @SuppressWarnings("unused")
    private Set<EventType> userEvents;

    private Menu mCurrentMenu;

    private long beginInMillis;

    protected SearchView mSearchView;

    protected SearchAutoComplete mSearchAutoComplete;

    protected boolean isSearchComponentOpened = false;

    private ViewStub mMainFallBackStub;

    private ActionBar mSupportActionBar;

    private boolean isBackButtonEnabled = false;

    private long mLaunchTime;

    public MenuItem mSearchMenuItem;

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     *
     * @param action
     * @param enabledMenuItems
     * @param userEvents
     * @param titleResId
     * @param contentLayoutId
     */
    public BaseActivity(NavigationAction action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        this(R.layout.main, action, enabledMenuItems, userEvents, titleResId, contentLayoutId);
    }

    /**
     * @param activityLayoutId
     * @param action
     * @param enabledMenuItems
     * @param userEvents
     * @param titleResId
     * @param contentLayoutId
     */
    public BaseActivity(int activityLayoutId, NavigationAction action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        this.activityLayoutId = activityLayoutId;
        this.userEvents = userEvents;
        this.action = action != null ? action : NavigationAction.Unknown;
        this.menuItems = enabledMenuItems;
        this.titleResId = titleResId;
        this.contentLayoutId = contentLayoutId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE");
        /*
         * In case of rotation the activity is restarted and the locale too.<br>
         * These method forces the right locale used before the rotation.
         * @author spereira
         */
        ShopSelector.setLocaleOnOrientationChanged(getApplicationContext());
        // Bind service
        JumiaApplication.INSTANCE.doBindService();
        /*
         * In case app is killed in background needs to restore font type
         */
        if (getApplicationContext().getResources().getBoolean(R.bool.is_shop_specific)) {
            HoloFontLoader.initFont(true);
        } else {
            HoloFontLoader.initFont(false);
        }
        // Get fragment controller
        fragmentController = FragmentController.getInstance();
        // Set content
        setContentView(activityLayoutId);
        // Set action bar
        setupActionBar();
        // Set navigation
        setupDrawerNavigation();
        // Set content view
        setupContentViews();
        // Update the content view if initial country selection
        updateContentViewsIfInitialCountrySelection();

        isRegistered = true;
        // Set main layout
        setAppContentLayout();
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
        Log.i(TAG, "ON NEW INTENT");
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
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");

        if (!isRegistered) {
            // OLD FRAMEWORK
            /**
             * Register service callback
             */
            JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
            isRegistered = true;
        }

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

        AdjustTracker.onResume(this);

        TrackerDelegator.trackAppOpenAdjust(getApplicationContext(), mLaunchTime);
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
        Log.i(TAG, "ON PAUSE");
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
        Log.i(TAG, "ON STOP");
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
        Log.i(TAG, "ON DESTROY");
        JumiaApplication.INSTANCE.unRegisterFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.setLoggedIn(false);
        isRegistered = false;
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
        Log.i(TAG, "ON POST CREATE: DRAWER");
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
        Log.i(TAG, "ON CONFIGURATION CHANGED");
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * ############## ACTION BAR ##############
     */

    /**
     * Method used to update the sliding menu and items on action bar. Called from BaseFragment
     *
     * @param enabledMenuItems
     * @param action
     * @param actionBarTitleResId
     * @param checkoutStep
     * @author sergiopereira
     * @modified Andre Lopes
     */
    public void updateBaseComponents(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int actionBarTitleResId, int checkoutStep) {
        Log.i(TAG, "ON UPDATE BASE COMPONENTS");
        // Update options menu and search bar
        menuItems = enabledMenuItems;
        hideKeyboard();
        invalidateOptionsMenu();
        // Update the sliding menu
        this.action = action != null ? action : NavigationAction.Unknown;
        updateNavigationMenu();
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
        updateNavigationMenu();
    }

    /**
     * Set the Action bar style
     *
     * @modified sergiopereira
     */
    public void setupActionBar() {
        mSupportActionBar = getSupportActionBar();
        mSupportActionBar.setDisplayHomeAsUpEnabled(true);
        mSupportActionBar.setHomeButtonEnabled(true);
        mSupportActionBar.setDisplayShowTitleEnabled(true);
    }

    /**
     * Set Action bar title
     *
     * @param actionBarTitleResId
     */
    private void setActionTitle(int actionBarTitleResId) {
        // Case hide all
        if (actionBarTitleResId == 0) {
            hideTitle();
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
            hideActionBarTitle();
        }
        // Case #specific_shop
        else if (getResources().getBoolean(R.bool.is_daraz_specific) ||
                getResources().getBoolean(R.bool.is_shop_specific) ||
                getResources().getBoolean(R.bool.is_bamilo_specific)) {
            // Show the application name in the action bar
            setActionBarTitle(R.string.app_name);
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
            setTitle(actionBarTitleResId);
        }
        // Case Jumia
        else {
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
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.drawable.ic_drawer){

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
        Log.d(TAG, "DRAWER: SETUP CONTENT VIEWS");
        // Get the application container
        contentContainer = findViewById(R.id.rocket_app_content);
        // Warning layout
        mWarningBar = findViewById(R.id.warning);
        mWarningBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWarningVariation(false);
            }
        });
        // Get the fallback stub
        mMainFallBackStub = (ViewStub) findViewById(R.id.main_fall_back_stub);
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
     * Method used to validate if is to show the initial country selection or is in maintenance.<br> Used in {@link HomeFragment#onCreate(Bundle)}.
     *
     * @return true or false
     * @author sergiopereira
     */
    public boolean isInitialCountry() {
        return initialCountry;
    }

    /*
     * ############### NAVIGATION MENU #################
     */

    /**
     * Update the sliding menu
     */
    public void updateNavigationMenu() {
        Log.d(TAG, "UPDATE SLIDE MENU");
        NavigationFragment slideMenuFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation);
        if (slideMenuFragment != null) {
            slideMenuFragment.onUpdateMenu();
        }
    }

    /**
     * Update the sliding menu
     */
    public void updateSlidingMenuCompletly() {
        NavigationFragment slideMenuFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation);
        if (slideMenuFragment != null) {
            slideMenuFragment.onUpdateCart();
            slideMenuFragment.onUpdateMenu();
        }
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
        Log.d(TAG, "ON OPTION ITEM SELECTED: " + item.getTitle());
        // Get item id
        int itemId = item.getItemId();
        // CASE BACK ARROW
        if (itemId == android.R.id.home && isBackButtonEnabled) {
            onBackPressed();
            return true;
        }
        // CASE HOME 
        else if (mDrawerToggle.onOptionsItemSelected(item)) {
            // Toggle between opened and closed drawer
            return true;
        }
        // CART
        else if (itemId == R.id.menu_basket) {
            // Close drawer
            closeNavigationDrawer();
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
        Log.d(TAG, "ON OPTIONS MENU: CREATE");
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
     *
     * @param showActionBar
     * @author andre
     * @modified sergiopereira
     */
    public void setActionBarVisibility(int showActionBar) {
        // Get current visibility
        boolean actionBarVisible = getSupportActionBar().isShowing();
        // Validate flag
        switch (showActionBar) {
            case View.VISIBLE:
                if (!actionBarVisible) {
                    getSupportActionBar().show();
                }
                break;
            case View.GONE:
                getSupportActionBar().hide();
                break;
            default:
                Log.w(TAG, "WARNING: INVALID FLAG, USE VISIBLE/INVISIBLE FROM View.");
                break;
        }
    }

    /**
     * Change actionBar visibility if necessary and executes runnable
     *
     * @param showActionBar
     * @param onChangeRunnable
     * @param onChangePostDelay
     */
    public void setActionBarVisibility(int showActionBar, Runnable onChangeRunnable, int onChangePostDelay) {
        boolean actionBarVisible = getSupportActionBar().isShowing();
        setActionBarVisibility(showActionBar);

        if (getSupportActionBar().isShowing() != actionBarVisible) {
            new Handler().postDelayed(onChangeRunnable, onChangePostDelay);
        }
    }

    /**
     * Set the up button in ActionBar
     *
     * @author sergiopereira
     */
    private void setActionBarUpButton() {
        if (isBackButtonEnabled) {
            Log.i(TAG, "SHOW UP BUTTON");
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        } else {
            Log.i(TAG, "NO SHOW UP BUTTON");
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
     *
     * @param menu
     * @modified sergiopereira
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
     *
     * @param menu
     * @author Andre Lopes
     * @modified sergiopereira
     */
    private void setActionSearch(Menu menu) {
        Log.i(TAG, "ON OPTIONS MENU: CREATE SEARCH VIEW");
        // Get search menu item
        mSearchMenuItem = menu.findItem(R.id.menu_search);
        // Get search action view
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchView.setQueryHint(getString(R.string.action_label_search_hint, getString(R.string.app_name_placeholder)));
        // Get edit text
        mSearchAutoComplete = (SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        //#RTL
        if (getResources().getBoolean(R.bool.is_bamilo_specific)) {
            mSearchAutoComplete.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        }
        // Set font
        HoloFontLoader.applyDefaultFont(mSearchView);
        HoloFontLoader.applyDefaultFont(mSearchAutoComplete);
        // Set the ime options
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // Set text color for old android versions
        mSearchAutoComplete.setTextColor(getResources().getColor(R.color.grey_middle));
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
        Log.d(TAG, "SEARCH WIDTH SIZE: " + searchComponentWidth);
        // Set measures
        mSearchView.setMaxWidth(searchComponentWidth);
        mSearchAutoComplete.setDropDownWidth(searchComponentWidth);
    }

    /**
     * Set the search component
     *
     * @param mSearchMenuItem
     */
    public void setActionBarSearchBehavior(final MenuItem mSearchMenuItem) {
        Log.d(TAG, "SEARCH MODE: NEW BEHAVIOUR");
        if (mSearchAutoComplete == null) {
            Log.w(TAG, "SEARCH COMPONENT IS NULL");
            return;
        }
        
        /*
         * Set on item click listener
         */
        mSearchAutoComplete.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Log.d(TAG, "SEARCH: CLICKED ITEM " + position);
                // Get suggestion
                SearchSuggestion selectedSuggestion = (SearchSuggestion) adapter.getItemAtPosition(position);
                // Get text suggestion
                String text = selectedSuggestion.getResult();
                // Clean edit text
                mSearchAutoComplete.setText("");
                mSearchAutoComplete.dismissDropDown();
                // Collapse search view
                MenuItemCompat.collapseActionView(mSearchMenuItem);
                // Save query
                GetSearchSuggestionHelper.saveSearchQuery(text);
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
                    Log.d(TAG, "SEARCH COMPONENT: ON IME ACTION " + searchTerm);
                    if (TextUtils.isEmpty(searchTerm)) {
                        return false;
                    }
                    // Clean edit text
                    textView.setText("");
                    // Collapse search view
                    MenuItemCompat.collapseActionView(mSearchMenuItem);
                    // Save query
                    GetSearchSuggestionHelper.saveSearchQuery(searchTerm);
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
                Log.d(TAG, "SEARCH ON EXPAND");
                closeNavigationDrawer();
                isSearchComponentOpened = true;
                setItemsVisibility(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "SEARCH ON COLLAPSE");
                isSearchComponentOpened = false;
                setItemsVisibility(true);
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
                    setItemsVisibility(true);
                }
            }
        });

    }

    /**
     * Execute search
     *
     * @param searchText
     * @author sergiopereira
     */
    protected void showSearchCategory(String searchText) {
        Log.d(TAG, "SEARCH COMPONENT: GOTO PROD LIST");
        // Tracking
        JumiaApplication.INSTANCE.trackSearchCategory = true;
        TrackerDelegator.trackSearchSuggestions(searchText);
        // Data
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, searchText);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, searchText);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * set all menu items visibility to <code>visible</code>
     *
     * @param visible
     */
    protected void setItemsVisibility(boolean visible) {
        for (MyMenuItem item : menuItems) {
            if (item != MyMenuItem.SEARCH_VIEW && item.resId != -1) {
                mCurrentMenu.findItem(item.resId).setVisible(visible);
            }
        }
    }

    /**
     * Hide the search component
     *
     * @author sergiopereira
     */
    public void hideSearchComponent() {
        Log.d(TAG, "SEARCH COMPONENT: HIDE");
        try {
            // Validate if exist search icon and bar
            if (menuItems.contains(MyMenuItem.SEARCH_VIEW)) {
                // Hide search bar
                MenuItemCompat.collapseActionView(mSearchMenuItem);
                // Clean autocomplete
                mSearchAutoComplete.setText("");
                // Show hidden items
                setItemsVisibility(true);
                // Forced the IME option on collapse
                mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON HIDE SEARCH COMPONENT");
        }
    }

    /**
     * Hide only the search bar, used by ChangeCountryFragment
     *
     * @param enumSet
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
            Log.i(TAG, "SEARCH: RUN GET SUGGESTIONS: " + mSearchAutoComplete.getText().toString());
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
        Log.d(TAG, "SEARCH COMPONENT: GET SUG FOR " + text);

        Bundle bundle = new Bundle();
        bundle.putString(GetSearchSuggestionHelper.SEACH_PARAM, text);
        JumiaApplication.INSTANCE.sendRequest(new GetSearchSuggestionHelper(), bundle,
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
     *
     * @param bundle
     * @author sergiopereira
     */
    private void processErrorSearchEvent(Bundle bundle) {
        Log.d(TAG, "SEARCH COMPONENT: ON ERROR");
        // Get query
        String requestQuery = bundle.getString(GetSearchSuggestionHelper.SEACH_PARAM);
        Log.d(TAG, "RECEIVED SEARCH ERROR EVENT: " + requestQuery);
        // Validate current search component
        if (mSearchAutoComplete != null && !mSearchAutoComplete.getText().toString().equals(requestQuery)) {
            Log.w(TAG, "SEARCH ERROR: WAS DISCARTED FOR QUERY " + requestQuery);
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
     *
     * @param bundle
     * @author sergiopereira
     */
    private void processSuccessSearchEvent(Bundle bundle) {
        Log.d(TAG, "SEARCH COMPONENT: ON SUCCESS");
        // Get suggestions
        List<SearchSuggestion> sug = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        // Get query
        String requestQuery = bundle.getString(GetSearchSuggestionHelper.SEACH_PARAM);
        Log.d(TAG, "RECEIVED SEARCH EVENT: " + sug.size() + " " + requestQuery);

        // Validate current objects
        if (menuItems == null && mCurrentMenu == null && mSearchAutoComplete == null) {
            return;
        }
        // Validate current menu items
        if (menuItems != null && !menuItems.contains(MyMenuItem.SEARCH_VIEW)) {
            return;
        }
        if (!mCurrentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId).isVisible()) {
            return;
        }
        // Validate current search
        if (mSearchAutoComplete.getText().length() < SEARCH_EDIT_SIZE
                || !mSearchAutoComplete.getText().toString().equals(requestQuery)) {
            Log.w(TAG, "SEARCH: DISCARTED DATA FOR QUERY " + requestQuery);
            return;
        }
        // Show suggestions
        Log.i(TAG, "SEARCH: SHOW DATA FOR QUERY " + requestQuery);
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
        Log.d(TAG, "ON UPDATE CART INFO");
        if (JumiaApplication.INSTANCE.getCart() != null) {
            Log.d(TAG, "updateCartInfo value = "
                    + JumiaApplication.INSTANCE.getCart().getCartValue() + " quantity = "
                    + JumiaApplication.INSTANCE.getCart().getCartCount());
        }
        updateCartInfoInActionBar();
        updateCartInfoInNavigation();
    }

    public void updateCartInfoInActionBar() {
        Log.d(TAG, "ON UPDATE CART IN ACTION BAR");
        if (mActionCartCount == null) {
            Log.w(TAG, "updateCartInfoInActionBar: cant find quantity in actionbar");
            return;
        }

        ShoppingCart currentCart = JumiaApplication.INSTANCE.getCart();
        // Show 0 while the cart is not updated
        final String quantity = currentCart == null ? "0" : String.valueOf(currentCart.getCartCount());

        mActionCartCount.post(new Runnable() {
            @Override
            public void run() {
                mActionCartCount.setText(quantity);
            }
        });
    }

    private void updateCartInfoInNavigation() {
        Log.d(TAG, "ON UPDATE CART IN NAVIGATION");
        NavigationFragment navigation = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation);
        if (navigation != null) {
            navigation.onUpdateCart();
        } else {
            Log.w(TAG, "updateCartInfoInNavigation: navigation container empty - doing nothing");
        }
    }

    /**
     * Create the share intent to be used to store the needed information
     *
     * @return The created intent
     */
    public Intent createShareIntent() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        // sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject, getString(R.string.app_name_placeholder)));
        // Get product
        CompleteProduct prod = JumiaApplication.INSTANCE.getCurrentProduct();
        // Validate
        if (null != prod) {
            // For tracking when sharing
            sharingIntent.putExtra(RestConstants.JSON_SKU_TAG, prod.getSku());
            String apiVersion = getString(R.string.jumia_global_api_version) + "/";
            String msg = getString(R.string.share_checkout_this_product) + "\n" + prod.getUrl().replace(apiVersion, "");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        }
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
        myProfile.setVisible(true);
        myProfile.setEnabled(true);
        if (myProfile != null) {
            myProfileActionProvider = (MyProfileActionProvider) MenuItemCompat.getActionProvider(myProfile);
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
                            int totalFavourites = FavouriteTableHelper.getTotalFavourites();
                            myProfileActionProvider.setTotalFavourites(totalFavourites);
                        }
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
                    case Favorite:
                        // FAVOURITES
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_FAVORITE);
                        onSwitchFragment(FragmentType.FAVORITE_LIST, FragmentController.NO_BUNDLE,
                                FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case RecentSearch:
                        // RECENT SEARCHES
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENT_SEARCHES);
                        onSwitchFragment(FragmentType.RECENT_SEARCHES_LIST,
                                FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case RecentlyView:
                        // RECENTLY VIEWED
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENTLY_VIEW);
                        onSwitchFragment(FragmentType.RECENTLY_VIEWED_LIST,
                                FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case MyAccount:
                        // MY ACCOUNT
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_MY_ACCOUNT);
                        onSwitchFragment(FragmentType.MY_ACCOUNT, FragmentController.NO_BUNDLE,
                                FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case MyOrders:
                        // TRACK ORDER
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_TRACK_ORDER);
                        onSwitchFragment(FragmentType.MY_ORDERS, FragmentController.NO_BUNDLE,
                                FragmentController.ADD_TO_BACK_STACK);
                        break;
                    default:
                        Log.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
                        break;
                }
            } else {
                Log.d(TAG, "selected navAction is already being shown");
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
            titleView.setText(title + " ");
            headerTitle.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(title)) {
            headerTitle.setVisibility(View.GONE);
        }
    }

    /**
     * Method used to set the number of products
     *
     * @param title
     * @param subtitle
     */
    public void setTitleAndSubTitle(CharSequence title, CharSequence subtitle) {
        TextView titleView = (TextView) findViewById(R.id.titleProducts);
        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
        View headerTitle = findViewById(R.id.header_title);

        if (titleView == null) {
            return;
        }
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(subtitle)) {
            Log.d(TAG, "------------->>>>>>>>>>>>>> SET TITLE ->" + title + "; " + subtitle);
            // Set text and force measure
            subtitleView.setText(subtitle);
            // Set title
            titleView.setText(title);
            // Set visibility
            headerTitle.setVisibility(View.VISIBLE);
            subtitleView.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(title)) {
            headerTitle.setVisibility(View.GONE);
        }
    }

    /**
     * Method used to set the number of products
     *
     * @param subtitle
     */
    public void setSubTitle(CharSequence subtitle) {
        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
        View headerTitle = findViewById(R.id.header_title);

        if (subtitleView == null) {
            return;
        }
        if (!TextUtils.isEmpty(subtitle)) {
            // Set text and force measure
            subtitleView.setText(subtitle);

            headerTitle.setVisibility(View.VISIBLE);
            subtitleView.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(subtitle)) {
            headerTitle.setVisibility(View.GONE);
        }
    }

    public void hideTitle() {
        findViewById(R.id.header_title).setVisibility(View.GONE);
    }

    /**
     * get the category tree title
     *
     * @return subtitle
     */
    public String getCategoriesTitle() {
        TextView titleView = (TextView) findViewById(R.id.titleProducts);
        if (!TextUtils.isEmpty(titleView.getText().toString())) {
            return titleView.getText().toString();
        } else {
            return "";
        }
    }

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
     *
     * @param actionBarTitleResId
     */
    public void setActionBarTitle(int actionBarTitleResId) {
//        logoTextView.setVisibility(View.VISIBLE);
//        logoTextView.setText(getString(actionBarTitleResId));
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setTitle(getString(actionBarTitleResId));
        mSupportActionBar.setTitle(getString(actionBarTitleResId));
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

    /**
     * ################# WARNING BAR #################
     */

    /**
     * Show or hide warning message with image
     */
    public final void showWarning(boolean show) {
        if(mWarningBar != null){
            mWarningBar.clearAnimation();
            if (show) {
                findViewById(R.id.warning_image).setVisibility(View.VISIBLE);
            }
            mWarningBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Show warning message with image and animation
     */
    public void showWarning(int message) {
        if (mWarningBar != null && mWarningBar.getVisibility() != View.VISIBLE) {
            ((TextView) findViewById(R.id.warning_text)).setText(message);
            findViewById(R.id.warning_image).setVisibility(View.VISIBLE);
            UIUtils.animateFadeInAndOut(this, mWarningBar, WARNING_LENGTH);
        }
    }

    /**
     * Show warning message without image
     */
    public void showWarningNoImage(int message) {
        if(mWarningBar != null){
            mWarningBar.clearAnimation();
            ((TextView) findViewById(R.id.warning_text)).setText(message);
            findViewById(R.id.warning_image).setVisibility(View.GONE);
            mWarningBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show warning variations message
     */
    public void showWarningVariation(boolean show) {
        if(mWarningBar != null){
            mWarningBar.clearAnimation();
            if(show){
                ((TextView) findViewById(R.id.warning_text)).setText(R.string.product_variance_choose_error);
                findViewById(R.id.warning_image).setVisibility(View.GONE);
            }
            mWarningBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void setAppContentLayout() {
        if (contentLayoutId == 0) {
            return;
        }
        ViewStub stub = (ViewStub) findViewById(R.id.stub_app_content);
        stub.setLayoutResource(contentLayoutId);
        contentContainer = stub.inflate();
    }

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

    /**
     * Service Stuff
     */

    public void unbindDrawables(View view) {

        try {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            } else if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                if (view instanceof AdapterView<?>) {
                    return;
                }
                try {
                    ((ViewGroup) view).removeAllViews();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
        } catch (RuntimeException e) {
            Log.w(TAG, "" + e);
        }

    }

    public void hideKeyboard() {
        Log.i(TAG, "HIDE KEYBOARD");
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = mDrawerLayout;
        if (v == null) {
            v = getWindow().getCurrentFocus();
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showKeyboard() {
        // Log.d( TAG, "showKeyboard" );
        Log.i(TAG, "code1here showKeyboard");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0);
        // use the above as the method below does not always work
        // imm.showSoftInput(getSlidingMenu().getCurrentFocus(),
        // InputMethodManager.SHOW_IMPLICIT);
    }

    public void onLogOut() {
        /*
         * NOTE: Others sign out methods are performed in {@link LogOut}.
         */
        // Track logout
        TrackerDelegator.trackLogoutSuccessful();
        // Goto Home
        onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        // Hide progress
        dismissProgress();
    }

    /**
     * Handles a successful event and reflects necessary changes on the UI.
     *
     * @param event
     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
     */

//    public void handleSuccessEvent(Bundle bundle) {
//        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//
//        switch (eventType) {
//        case GET_SHOPPING_CART_ITEMS_EVENT:
//        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
//        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
//        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
//            updateCartInfo();
//            break;
//        case LOGOUT_EVENT:
//            Log.i(TAG, "LOGOUT EVENT");
//            /*
//             * NOTE: Others sign out methods are performed in {@link LogOut}.
//             */
//            // Track logout
//            TrackerDelegator.trackLogoutSuccessful();
//            // Goto Home
//            onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//            // Hide progress
//            dismissProgress();
//            break;
//        case LOGIN_EVENT:
//            JumiaApplication.INSTANCE.setLoggedIn(true);
//            Bundle b = new Bundle();
//            b.putBoolean(Constants.BUNDLE_PRIORITY_KEY, false);
//            triggerContentEventWithNoLoading(new GetShoppingCartItemsHelper(), b, mIResponseCallback);
//            break;
//        default:
//            break;
//        }
//    }

    /**
     * Handles a failed event and shows dialogs to the user.
     *
     * @param event
     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
     */
//    @SuppressWarnings("unchecked")
//    public boolean handleErrorEvent(final Bundle bundle) {
//
//        Log.i(TAG, "ON HANDLE ERROR EVENT");
//
//        final EventType eventType = (EventType) bundle
//                .getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
//
//        if (eventType == EventType.LOGIN_EVENT) {
//            JumiaApplication.INSTANCE.setLoggedIn(false);
//            JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
//            updateNavigationMenu();
//        }
//
//        if (!bundle.getBoolean(Constants.BUNDLE_PRIORITY_KEY)) {
//            return false;
//        }
//
//        HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
//                .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
//        if (errorCode == null) {
//            return false;
//        }
//        if (errorCode.isNetworkError()) {
//            switch (errorCode) {
//            case SSL:
//            case IO:
//            case CONNECT_ERROR:
//            case TIME_OUT:
//            case HTTP_STATUS:
//            case NO_NETWORK:
//                createNoNetworkDialog(eventType);
//                return true;
//            case SERVER_IN_MAINTENANCE:
//                setLayoutMaintenance(eventType);
//                return true;
//            case REQUEST_ERROR:
//                List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
//                String dialogMsg = "";
//                if (validateMessages == null || validateMessages.isEmpty()) {
//                    validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
//                }
//                if (validateMessages != null) {
//                    for (String message : validateMessages) {
//                        dialogMsg += message + "\n";
//                    }
//                } else {
//                    for (Entry<String, ? extends List<String>> entry : errorMessages.entrySet()) {
//                        dialogMsg += entry.getKey() + ": " + entry.getValue().get(0) + "\n";
//                    }
//                }
//                if (dialogMsg.equals("")) {
//                    dialogMsg = getString(R.string.validation_errortext);
//                }
//                // showContentContainer();
//                dialog = DialogGenericFragment.newInstance(true, true, false,
//                        getString(R.string.validation_title), dialogMsg,
//                        getResources().getString(R.string.ok_label), "", new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                int id = v.getId();
//                                if (id == R.id.button1) {
//                                    dialog.dismiss();
//                                }
//                            }
//                        });
//                
//                dialog.show(getSupportFragmentManager(), null);
//                return true;
//            default:
//                createNoNetworkDialog(eventType);
//                return true;
//            }
//
//        }
//        return false;
//
//    }

//    private void createNoNetworkDialog(final EventType eventType) {
//        // Remove dialog if exist
//        if (dialog != null) {
//            try {
//                dialog.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        dialog = DialogGenericFragment.createNoNetworkDialog(this,
//                new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        JumiaApplication.INSTANCE.sendRequest(JumiaApplication.INSTANCE
//                                .getRequestsRetryHelperList().get(eventType),
//                                JumiaApplication.INSTANCE.getRequestsRetryBundleList().get(eventType),
//                                JumiaApplication.INSTANCE.getRequestsResponseList().get(eventType));
//                        if (dialog != null) dialog.dismiss();
//                        dialog = null;
//                    }
//                },
//                new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        if (dialog != null) dialog.dismiss();
//                        dialog = null;
//                    }
//                },
//                false);
//
//        try {
//            dialog.show(getSupportFragmentManager(), null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * Set action
     *
     * @param action
     */
    public void setAction(NavigationAction action) {
        this.action = action;
    }

    /**
     * ############### FRAGMENTS #################
     */

    /**
     * This method should be implemented by fragment activity to manage the work flow for fragments. Each fragment should call this method.
     *
     * @param type
     * @param bundle
     * @param addToBackStack
     * @author sergiopereira
     */
    public abstract void onSwitchFragment(FragmentType type, Bundle bundle, Boolean addToBackStack);

    /**
     * Method used to switch fragment on UI with/without back stack support
     *
     * @param fragment
     * @param addToBackStack
     * @author sergiopereira
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
     * Pop all back stack
     *
     * @param tag
     * @author sergiopereira
     */
    protected void popAllBackStack(String tag) {
        fragmentController.popAllBackStack(this, tag);
    }

    /**
     * Pop back stack until tag
     *
     * @param tag
     * @author sergiopereira
     */
    public boolean popBackStackUntilTag(String tag) {
        if (fragmentController.hasEntry(FragmentType.HOME.toString())) {
            fragmentController.popAllEntriesUntil(this, tag);
            return true;
        }
        return false;
    }


    /**
     * Confirm backPress to exit application
     */
    public Boolean exitApplication() {

        dialog = DialogGenericFragment.newInstance(false,
                true,
                null, // no
                // title
                getString(R.string.logout_text_question), getString(R.string.no_label),
                getString(R.string.yes_label), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismissAllowingStateLoss();
                        int id = v.getId();
                        /*
                        if (id == R.id.button1) {
                            // fragC.popLastEntry();
                        } else
                         */
                        if (id == R.id.button2) {
                            finish();
                        }

                    }
                });
        dialog.show(getSupportFragmentManager(), null);
        return false;
    }

    /**
     * Method used to control the double back pressed
     *
     * @author sergiopereira
     * @see <a href="http://stackoverflow.com/questions/7965135/what-is-the-duration-of-a-toast-length-long-and-length-short">Toast duration</a> <br>
     * Toast.LENGTH_LONG is 3500 seconds. <br> Toast.LENGTH_SHORT is 2000 seconds.
     */
    public void doubleBackPressToExit() {
        Log.d(TAG, "DOUBLE BACK PRESSED TO EXIT: " + backPressedOnce);
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

    /**
     * Requests and Callbacks methods
     */

    /**
     * Callback which deals with the IRemoteServiceCallback
     */
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {

        @Override
        public void getError(Bundle response) throws RemoteException {
            Log.i(TAG, "Set target to handle error");
            handleError(response);
        }

        @Override
        public void getResponse(Bundle response) throws RemoteException {
            handleResponse(response);
        }
    };

    public static KeyboardState currentAdjustState;

    /**
     * Handles correct responses
     *
     * @param bundle
     */
    private void handleResponse(Bundle bundle) {

        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        // Log.i(TAG, "code1removing callback from request type : "+
        // bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+
        // " size is : "
        // +JumiaApplication.INSTANCE.responseCallbacks.size());
        // Log.i(TAG, "code1removing callback with id : "+ id);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
            // Log.i(TAG, "code1removing removed callback with id : "+ id);
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestComplete(bundle);
        }
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);
    }

    /**
     * Handles error responses
     *
     * @param bundle
     */
    private void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        // Log.i(TAG, "code1removing callback from request type : "+
        // bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY));
        // Log.i(TAG, "code1removing callback with id : "+ id);
        if (JumiaApplication.INSTANCE.responseCallbacks.containsKey(id)) {
            // Log.i(TAG, "code1removing removed callback with id : "+ id);
            JumiaApplication.INSTANCE.responseCallbacks.get(id).onRequestError(bundle);
        }
        JumiaApplication.INSTANCE.responseCallbacks.remove(id);
    }

    /**
     * ################ MAIN MAINTENANCE PAGE ################
     */

    /**
     * Hide the main fall back view with retry button
     */
    public void hideLayoutMaintenance() {
        if (mMainFallBackStub != null) {
            mMainFallBackStub.setVisibility(View.GONE);
        }
    }

    /**
     * Sets Maintenance page
     */
    public void setLayoutMaintenance(final EventType eventType, OnClickListener onClickListener, boolean showChooseCountry) {
        // Inflate maintenance
        mMainFallBackStub.setVisibility(View.VISIBLE);

        // Case BAMILO
        if (getResources().getBoolean(R.bool.is_bamilo_specific)) {
            MaintenancePage.setMaintenancePageBamilo(this, eventType, onClickListener);
        }
        // Case JUMIA
        else {
            // Set content
            if (showChooseCountry) {
                MaintenancePage.setMaintenancePageWithChooseCountry(this, eventType, onClickListener);
            } else {
                MaintenancePage.setMaintenancePageBaseActivity(this, onClickListener);
            }
        }
    }

    /**
     * ########## CHECKOUT HEADER ##########
     */

    /**
     * Set the current checkout step otherwise return false
     *
     * @param checkoutStep
     * @return true/false
     */
    public boolean setCheckoutHeader(int checkoutStep) {
        Log.d(TAG, "SET CHECKOUT HEADER STEP ID: " + checkoutStep);

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
                Log.e(TAG, "checkoutStep unknown");
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
     *
     * @param visibility
     * @author sergiopereira
     */
    private void updateBaseComponentsOutCheckout(int visibility) {
        Log.d(TAG, "SET BASE FOR NON CHECKOUT: HIDE");
        // Set header visibility
        findViewById(R.id.checkout_header_main_step).setVisibility(visibility);
        findViewById(R.id.checkout_header).setVisibility(visibility);
    }

    /**
     * Update the base components in checkout
     *
     * @param visibility
     * @author sergiopereira
     */
    private void updateBaseComponentsInCheckout(int visibility) {
        Log.d(TAG, "SET BASE FOR CHECKOUT: SHOW");
        // Set header visibility
        findViewById(R.id.checkout_header_main_step).setVisibility(visibility);
        findViewById(R.id.checkout_header).setVisibility(visibility);
        // Hide title and prod
        hideTitle();
        findViewById(R.id.totalProducts).setVisibility(View.GONE);
    }

    /**
     * Unselect the a checkout step
     *
     * @param step
     * @author sergiopereira
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
     *
     * @param step
     * @author sergiopereira
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
     *
     * @param main
     * @param icon
     * @param text
     * @author sergiopereira
     */
    private void selectStep(int main, int icon, int text) {
        findViewById(main).setSelected(true);
        findViewById(main).getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.checkout_header_step_selected_width);
        findViewById(icon).setSelected(true);
        findViewById(text).setVisibility(View.VISIBLE);
    }

    /**
     * Set a step unselected
     *
     * @param main
     * @param icon
     * @param text
     * @author sergiopereira
     */
    private void unSelectStep(int main, int icon, int text) {
        findViewById(main).setSelected(false);
        findViewById(main).getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.checkout_header_step_width);
        findViewById(icon).setSelected(false);
        findViewById(text).setVisibility(View.GONE);
    }

    /**
     * Checkout header click listener associated to each item on layout
     *
     * @param view
     * @author sergiopereira
     */
    public void onCheckoutHeaderClickListener(View view) {
        Log.i(TAG, "PROCESS CLICK ON CHECKOUT HEADER");
        int id = view.getId();
        /*
        // CHECKOUT_ABOUT_YOU
        if (id == R.id.checkout_header_step_1 && !view.isSelected()) {
            // Uncomment if you want click on about you step
            // removeAllCheckoutEntries();
            // onSwitchFragment(FragmentType.ABOUT_YOU,
            // FragmentController.NO_BUNDLE,
            // FragmentController.ADD_TO_BACK_STACK);
        }
        else
        */
        // CHECKOUT_BILLING
        if (id == R.id.checkout_header_step_2 && !view.isSelected()) {
            // Validate back stack

            if(!popBackStackUntilTag(FragmentType.MY_ADDRESSES.toString()) &&
                    fragmentController.hasEntry(FragmentType.CREATE_ADDRESS.toString())){
                removeAllCheckoutEntries();
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
     * Remove all checkout entries to call the base of checkout
     *
     * @author sergiopereira
     */
    private void removeAllCheckoutEntries() {
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.PAYMENT_METHODS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.SHIPPING_METHODS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.MY_ADDRESSES.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.CREATE_ADDRESS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.EDIT_ADDRESS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.POLL.toString());
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
        Log.i(TAG, "ON TRIGGER: INITIALIZE USER DATA");
        // Validate the user credentials
        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() && JumiaApplication.CUSTOMER == null) {
            triggerAutoLogin();
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
        Log.i(TAG, "TRIGGER SHOPPING CART ITEMS");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, false);
        JumiaApplication.INSTANCE.sendRequest(new GetShoppingCartItemsHelper(), bundle, new IResponseCallback() {
            @Override
            public void onRequestError(Bundle bundle) {
                Log.i(TAG, "ON REQUEST ERROR: CART");
                //...
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                Log.i(TAG, "ON REQUEST COMPLETE: CART");
                updateCartInfo();
            }
        });
    }

    /**
     * Auto login
     */
    private void triggerAutoLogin() {
        Log.i(TAG, "ON TRIGGER: AUTO LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        JumiaApplication.INSTANCE.sendRequest(new GetLoginHelper(), bundle, new IResponseCallback() {
            @Override
            public void onRequestError(Bundle bundle) {
                Log.i(TAG, "ON REQUEST ERROR: AUTO LOGIN");
                JumiaApplication.INSTANCE.setLoggedIn(false);
                JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
                updateNavigationMenu();
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                Log.i(TAG, "ON REQUEST COMPLETE: AUTO LOGIN");
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
        bundle.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
        bundle.putLong(AdjustTracker.BEGIN_TIME, mLaunchTime);
        bundle.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
        if (JumiaApplication.CUSTOMER != null) {
            bundle.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER);
        }
        TrackerDelegator.trackPageForAdjust(TrackingPage.HOME, bundle);
    }

    /**
     * ##### WIZARDS #####
     */

}