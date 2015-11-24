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
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.search.GetSearchSuggestionsHelper;
import com.mobile.helpers.session.LoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.pojo.BaseResponse;
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
import com.mobile.utils.ui.ConfirmationCartMessageView;
import com.mobile.utils.ui.TabLayoutUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.BaseFragment.KeyboardState;

import java.lang.ref.WeakReference;
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
public abstract class BaseActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    // private ShareActionProvider mShareActionProvider;

    private static final int SEARCH_EDIT_DELAY = 500;

    private static final int SEARCH_EDIT_SIZE = 2;

    private static final int TOAST_LENGTH_SHORT = 2000; // 2 seconds

    @KeyboardState
    public static int currentAdjustState;

    //protected View contentContainer;
    private final int activityLayoutId;
    private final int titleResId;
    public View mDrawerNavigation;
    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public MenuItem mSearchMenuItem;
    private WarningFactory warningFactory;
    protected DialogFragment dialog;
    protected SearchView mSearchView;
    protected SearchAutoComplete mSearchAutoComplete;
    protected boolean isSearchComponentOpened = false;

    //private final int contentLayoutId;
    // REMOVED FINAL ATTRIBUTE
    @NavigationAction.Type private int action;
    private Set<MyMenuItem> menuItems;
    private DialogProgressFragment baseActivityProgressDialog;
    private DialogGenericFragment dialogLogout;
    private boolean backPressedOnce = false;
    /**
     * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
     * @Solution : http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this -action-after-onsaveinstancestate-h
     */
    private Intent mOnActivityResultIntent = null;
    private TextView mActionCartCount;
    private MyProfileActionProvider myProfileActionProvider;
    private FragmentController fragmentController;
    /**
     * Handles changes on Checkout tabs.
     */
    final TabLayout.OnTabSelectedListener mCheckoutOnTabSelectedListener = new android.support.design.widget.TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(android.support.design.widget.TabLayout.Tab tab) {
            onCheckoutHeaderSelectedListener(tab.getPosition());
        }

        @Override
        public void onTabUnselected(android.support.design.widget.TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(android.support.design.widget.TabLayout.Tab tab) {

        }
    };
    private boolean initialCountry = false;
    private Menu mCurrentMenu;
    private long beginInMillis;
    /**
     * Runnable to get suggestions
     *
     * @author sergiopereira
     */
    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            Print.i(TAG, "SEARCH: RUN GET SUGGESTIONS: " + mSearchAutoComplete.getText());
            getSuggestions();
        }
    };
    private ActionBar mSupportActionBar;
    private boolean isBackButtonEnabled = false;
    private long mLaunchTime;
    private TabLayout mTabLayout;
    OnClickListener myProfileClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean hideMyProfile = true;
            @NavigationAction.Type
            int navAction = (int) v.getTag(R.id.nav_action);
            if (getAction() != navAction) {
                switch (navAction) {
                    case NavigationAction.MY_PROFILE:
                        // MY PROFILE
                        hideMyProfile = false;
                        // Close Drawer
                        closeNavigationDrawer();
                        // Validate provider
                        if (myProfileActionProvider != null) {
                            myProfileActionProvider.showSpinner();
                        }
                        break;
                    case NavigationAction.HOME:
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_HOME);
                        onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.LOGIN_OUT:
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
                                                LogOut.perform(new WeakReference<Activity>(BaseActivity.this));
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
                    case NavigationAction.SAVED:
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
                    case NavigationAction.RECENT_SEARCHES:
                        // RECENT SEARCHES
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENT_SEARCHES);
                        onSwitchFragment(FragmentType.RECENT_SEARCHES_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.RECENTLY_VIEWED:
                        // RECENTLY VIEWED
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENTLY_VIEW);
                        onSwitchFragment(FragmentType.RECENTLY_VIEWED_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.MY_ACCOUNT:
                        // MY ACCOUNT
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_MY_ACCOUNT);
                        onSwitchFragment(FragmentType.MY_ACCOUNT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.MY_ORDERS:
                        // TRACK ORDER
                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_TRACK_ORDER);
                        onSwitchFragment(FragmentType.MY_ORDERS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.COUNTRY:
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
    private TabLayout mCheckoutTabLayout;
    /**
     * Handles clicks on Checkout Header
     * Verifies if click is available for this header position, if so, will select position and then mCheckoutOnTabSelectedListener will handle next steps.
     */
    final android.view.View.OnClickListener mCheckoutOnClickListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            onCheckoutHeaderClickListener((int) v.getTag());
        }
    };
    private AppBarLayout mAppBarLayout;

    public ConfirmationCartMessageView mConfirmationCartMessageView;

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     */
    public BaseActivity(@NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        this(R.layout.main, action, enabledMenuItems, userEvents, titleResId, contentLayoutId);
    }

    /**
     * Constructor
     */
    public BaseActivity(int activityLayoutId, @NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        this.activityLayoutId = activityLayoutId;
        //this.userEvents = userEvents;
        this.action = action;
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
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
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
        // Tracking
        TrackerDelegator.trackCloseApp();
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
     * ############## ACTION BAR ##############
     */

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Check version, disabled for Samsung (check_version_enabled)
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
         * Validate current version to show the upgrade dialog, disabled for Samsung (check_version_enabled).
         */
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(this);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.triggerContentEventProgressapp.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /**
     * Method used to update the sliding menu and items on action bar. Called from BaseFragment
     */
    public void updateBaseComponents(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int newNavAction, int actionBarTitleResId, int checkoutStep) {
        Print.i(TAG, "ON UPDATE BASE COMPONENTS");
        // Update the app bar layout
        setAppBarLayout(this.action, newNavAction);
        // Update options menu and search bar
        menuItems = enabledMenuItems;
        hideKeyboard();
        invalidateOptionsMenu();
        // Update the sliding menu
        this.action = newNavAction;
        // Select step on Checkout
        setCheckoutHeader(checkoutStep);
        // Set actionbarTitle
        setActionTitle(actionBarTitleResId);
    }

    /*
     * ############## NAVIGATION ##############
     */

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
            mSupportActionBar.setLogo(R.drawable.logo_nav_bar);
        }
        // Set tab layout
        setupTabBarLayout();
    }

    public void setupTabBarLayout() {
        // Get tab layout
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mCheckoutTabLayout = (TabLayout) findViewById(R.id.checkout_tabs);
        mCheckoutTabLayout.setVisibility(android.view.View.INVISIBLE);
        TabLayoutUtils.fillTabLayout(mTabLayout, this);
        TabLayoutUtils.updateTabCartInfo(mTabLayout);
        // Checkout Tab
        TabLayoutUtils.fillCheckoutTabLayout(mCheckoutTabLayout, mCheckoutOnTabSelectedListener, mCheckoutOnClickListener);
        mCheckoutTabLayout.setOnTabSelectedListener(mCheckoutOnTabSelectedListener);


    }
    
    /*
     * ############## CONTENT VIEWS ##############
     */

    private void setAppBarLayout(@NavigationAction.Type int oldNavAction, @NavigationAction.Type int newNavAction) {
        try {
            // Case action without tab layout
            if (!TabLayoutUtils.isNavigationActionWithTabLayout(newNavAction)) {
                mTabLayout.setVisibility(View.GONE);
                mAppBarLayout.setExpanded(true, true);
            }
            // Case action with tab layout
            else {
                // Case from other tab
                if (!TabLayoutUtils.isNavigationActionWithTabLayout(oldNavAction)) {
                    mTabLayout.setVisibility(View.VISIBLE);
                    mAppBarLayout.setExpanded(true, true);
                }
                //noinspection ConstantConditions
                mTabLayout.getTabAt(TabLayoutUtils.getTabPosition(newNavAction)).select();
            }
        } catch (NullPointerException e) {
            // ...
        }
    }
    
    /*
     * ############## INITIAL COUNTRY SELECTION ##############
     */

    /**
     * Set Action bar title
     */
    private void setActionTitle(int actionBarTitleResId) {
        // Case hide all
        if (actionBarTitleResId == IntConstants.ACTION_BAR_NO_TITLE) {
            hideActionBarTitle();
        }
        // Case to set title
        else {
            setActionBarTitle(actionBarTitleResId);
        }
    }

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

    /*
     * ############### OPTIONS MENU #################
     */

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

    /**
     *
     */
    private void setupContentViews() {
        Print.d(TAG, "DRAWER: SETUP CONTENT VIEWS");
        // Get the application horizontalListView
        // Warning layout
        try {
            warningFactory = new WarningFactory(findViewById(R.id.warning));
            //view for configurable confirmation message when adding to carte, in case of hasCartPopup = true
            mConfirmationCartMessageView = new ConfirmationCartMessageView(findViewById(R.id.configurableCartView),this);
        } catch(IllegalStateException ex){
            Print.e(TAG, ex.getLocalizedMessage(), ex);
        }
    }

    /*
     * ############### ACTION BAR MENU ITEMS #################
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
     * Method used to validate if is to show the initial country selection or is in maintenance.<br> Used in {@link com.mobile.view.fragments.HomePageFragment#onCreate(Bundle)}.
     *
     * @return true or false
     * @author sergiopereira
     */
    public boolean isInitialCountry() {
        return initialCountry;
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
     * ########### TAB LAYOUT LISTENER ###########
     */

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
    }    @Override
         public void onTabUnselected(TabLayout.Tab tab) {
        // ...
    }

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
    }    @Override
         public void onTabReselected(TabLayout.Tab tab) {
        // ...
    }
    
    
    /*
     * ############### SEARCH COMPONENT #################
     */

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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        TabLayoutUtils.tabSelected(this, tab, action);
    }

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
            private final Handler handle = new Handler();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

    /*
     * ############### SEARCH TRIGGER #################
     */

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
     * ############### SEARCH RESPONSES #################
     */

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
                    public void onRequestComplete(BaseResponse baseResponse) {
                        processSuccessSearchEvent(baseResponse);
                    }

                    @Override
                    public void onRequestError(BaseResponse baseResponse) {
                        processErrorSearchEvent(baseResponse);
                    }
                });
    }

    /**
     * #################### SHARE #####################
     */

//    /**
//     * Called to update the share intent
//     *
//     * @param shareIntent
//     *            the intent to be stored
//     */
//    /*-public void setShareIntent(Intent shareIntent) {
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareHistoryFileName(null);
//            mShareActionProvider.setShareIntent(shareIntent);
//        }
//    }*/

    /**
     * Process the search error event
     *
     * @author sergiopereira
     */
    private void processErrorSearchEvent(BaseResponse baseResponse) {
        Print.d(TAG, "SEARCH COMPONENT: ON ERROR");

        GetSearchSuggestionsHelper.SuggestionsStruct suggestionsStruct = (GetSearchSuggestionsHelper.SuggestionsStruct)baseResponse.getMetadata().getData();

        // Get query
        String requestQuery = suggestionsStruct.getSearchParam();
        Print.d(TAG, "RECEIVED SEARCH ERROR EVENT: " + requestQuery);
        // Validate current search component
        if (mSearchAutoComplete != null && !mSearchAutoComplete.getText().toString().equals(requestQuery)) {
            Print.w(TAG, "SEARCH ERROR: WAS DISCARDED FOR QUERY " + requestQuery);
            return;
        }
        if (!mCurrentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId).isVisible()) {
            return;
        }
        // Hide dropdown
        mSearchAutoComplete.dismissDropDown();
    }

    /**
     * Process success search event
     *
     * @author sergiopereira
     */
    private void processSuccessSearchEvent(BaseResponse baseResponse) {
        Print.d(TAG, "SEARCH COMPONENT: ON SUCCESS");
        // Get suggestions
        GetSearchSuggestionsHelper.SuggestionsStruct suggestionsStruct = (GetSearchSuggestionsHelper.SuggestionsStruct)baseResponse.getMetadata().getData();
        // Get query
        String requestQuery = suggestionsStruct.getSearchParam();
        Print.d(TAG, "RECEIVED SEARCH EVENT: " + suggestionsStruct.size() + " " + requestQuery);

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
        SearchDropDownAdapter searchSuggestionsAdapter = new SearchDropDownAdapter(getApplicationContext(), suggestionsStruct, requestQuery);
        mSearchAutoComplete.setAdapter(searchSuggestionsAdapter);
        mSearchAutoComplete.showDropDown();
    }

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
        TabLayoutUtils.updateTabCartInfo(mTabLayout);
    }

    /**
     * ################# MY PROFILE #################
     */

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

    /**
     * @return the action
     */
    @NavigationAction.Type
    public int getAction() {
        return action;
    }

    /**
     * Set action
     */
    public void setAction(@NavigationAction.Type int action) {
        this.action = action;
    }

    /**
     * Show and set title on actionbar
     */
    public void setActionBarTitle(@StringRes int actionBarTitleResId) {
        //logoTextView.setVisibility(View.VISIBLE);
        //logoTextView.setText(getString(actionBarTitleResId));
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setTitle(getString(actionBarTitleResId));
        mSupportActionBar.setLogo(null);
        mSupportActionBar.setTitle(getString(actionBarTitleResId));
    }

    public void setActionBarTitle(@NonNull String title) {
        mSupportActionBar.setLogo(null);
        mSupportActionBar.setTitle(title);
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
     * Hide title on actionbar
     */
    public void hideActionBarTitle() {
        //logoTextView.setVisibility(View.GONE);
        //getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        mSupportActionBar.setLogo(R.drawable.logo_nav_bar);
        mSupportActionBar.setTitle("");
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
        try {
            if (baseActivityProgressDialog != null) {
                baseActivityProgressDialog.dismissAllowingStateLoss();
                baseActivityProgressDialog = null;
            }
        } catch (IllegalStateException e) {
            // ...
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
        showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.logout_success));
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
    public void setCheckoutHeader(@ConstantsCheckout.CheckoutType int checkoutStep) {
        Print.i(TAG, "SET CHECKOUT HEADER STEP ID: " + checkoutStep);
        switch (checkoutStep) {
            case ConstantsCheckout.CHECKOUT_ABOUT_YOU:
            case ConstantsCheckout.CHECKOUT_BILLING:
            case ConstantsCheckout.CHECKOUT_SHIPPING:
            case ConstantsCheckout.CHECKOUT_PAYMENT:
                selectCheckoutStep(checkoutStep);
                updateBaseComponentsInCheckout(View.VISIBLE);
                break;
            case ConstantsCheckout.CHECKOUT_ORDER:
            case ConstantsCheckout.CHECKOUT_THANKS:
                updateBaseComponentsInCheckout(View.GONE);
                break;
            case ConstantsCheckout.NO_CHECKOUT:
            default:
                updateBaseComponentsOutCheckout(View.GONE);
                break;
        }
    }

    /**
     * Update the base components out checkout
     */
    private void updateBaseComponentsOutCheckout(final int visibility) {
        Print.d(TAG, "SET BASE FOR NON CHECKOUT: HIDE");
        // Set header visibility
        mCheckoutTabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCheckoutTabLayout.setVisibility(visibility);
            }
        }, 5);
    }

    /**
     * Update the base components in checkout
     */
    private void updateBaseComponentsInCheckout(int visibility) {
        Print.d(TAG, "SET BASE FOR CHECKOUT: SHOW");
        mCheckoutTabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(android.view.View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mCheckoutTabLayout.removeOnLayoutChangeListener(this);
            }

        });
        // Set header visibility
        mCheckoutTabLayout.setVisibility(visibility);
    }

    /**
     * Set the selected checkout step
     */
    private void selectCheckoutStep(int step) {
        TabLayout.Tab tab = mCheckoutTabLayout.getTabAt(step);
        if(tab != null) {
            tab.select();
        }
    }

    /**
     * Checkout header click listener associated to each item on layout
     */
    public void onCheckoutHeaderClickListener(int step) {
        Print.i(TAG, "PROCESS CLICK ON CHECKOUT HEADER " + step);
        // CHECKOUT_ABOUT_YOU - step == 0 - click is never allowed

        // CHECKOUT_BILLING  - step == 1
        // If selected tab is CHECKOUT_SHIPPING or CHECKOUT_PAYMENT, allow click
        if (step == ConstantsCheckout.CHECKOUT_BILLING && mCheckoutTabLayout.getSelectedTabPosition() > ConstantsCheckout.CHECKOUT_BILLING) {
            selectCheckoutStep(step);
        }
        // CHECKOUT_SHIPPING  - step == 2
        // If selected tab is the CHECKOUT_PAYMENT, allow click
        else if (step == ConstantsCheckout.CHECKOUT_SHIPPING && mCheckoutTabLayout.getSelectedTabPosition() > ConstantsCheckout.CHECKOUT_SHIPPING) {
            selectCheckoutStep(step);
        }
        // CHECKOUT_PAYMENT IS THE LAST  - step == 3 - click is never allowed
    }

    /**
     * When user changes checkout step.
     * @param step - selected position on header.
     */
    public void onCheckoutHeaderSelectedListener(int step) {
        // CASE TAB_CHECKOUT_ABOUT_YOU - step == 0 - click is never allowed
        // CASE TAB_CHECKOUT_BILLING
        if (step == ConstantsCheckout.CHECKOUT_BILLING) {
            popBackStackUntilTag(FragmentType.MY_ADDRESSES.toString());
        }
        // CASE TAB_CHECKOUT_SHIPPING
        else if (step == ConstantsCheckout.CHECKOUT_SHIPPING ) {
            popBackStackUntilTag(FragmentType.SHIPPING_METHODS.toString());
        }
        // CASE TAB_CHECKOUT_PAYMENT IS THE LAST  - step == 3 - click is never allowed
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
            public void onRequestError(BaseResponse baseResponse) {
                Print.i(TAG, "ON REQUEST ERROR: CART");
                //...
            }

            @Override
            public void onRequestComplete(BaseResponse baseResponse) {
                Print.i(TAG, "ON REQUEST COMPLETE: CART");
                updateCartInfo();
            }
        });
    }

    /*
     * ##### REQUESTS TO RECOVER #####
     */

    /**
     * Auto login
     */
    private void triggerAutoLogin() {
        Print.i(TAG, "ON TRIGGER: AUTO LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        JumiaApplication.INSTANCE.sendRequest(new LoginHelper(), bundle, new IResponseCallback() {
            @Override
            public void onRequestError(BaseResponse baseResponse) {
                Print.i(TAG, "ON REQUEST ERROR: AUTO LOGIN");
                JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
            }

            @Override
            public void onRequestComplete(BaseResponse baseResponse) {
                Print.i(TAG, "ON REQUEST COMPLETE: AUTO LOGIN");
                // Get customer
                Customer customer = ((CheckoutStepLogin)((NextStepStruct)baseResponse.getMetadata().getData()).getCheckoutStepObject()).getCustomer();
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

    public void showWarning(@WarningFactory.WarningErrorType final int warningFact){
        warningFactory.showWarning(warningFact);
    }

    public void showWarningMessage(@WarningFactory.WarningErrorType final int warningFact, final String message){

        warningFactory.showWarning(warningFact, message);
    }

    public void hideWarningMessage(){
        warningFactory.hideWarning();
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