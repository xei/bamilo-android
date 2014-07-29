package pt.rocket.view;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.holoeverywhere.widget.Button;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.LogOut;
import pt.rocket.controllers.SearchDropDownAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.SearchSuggestion;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.tracking.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.framework.utils.WindowHelper;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.GetSearchSuggestionHelper;
import pt.rocket.helpers.cart.GetShoppingCartItemsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.MyProfileActionProvider;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.CustomToastView;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogProgressFragment;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.fragments.BaseFragment.KeyboardState;
import pt.rocket.view.fragments.NavigationFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.interfaces.SearchViewImeBackListener;
import com.actionbarsherlock.internal.ActionBarSherlockNative;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnCloseListener;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;
import com.bugsense.trace.BugSenseHandler;
import com.urbanairship.UAirship;

import de.akquinet.android.androlog.Log;

/**
 * 
 * All activities extend this activity, in order to access methods that are shared and used in all
 * activities.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Paulo Carvalho
 * 
 * @modified Sergio Pereira
 * @modified Manuel Silva
 * 
 * @version 2.0
 * 
 *          2012/06/19
 * 
 */
public abstract class BaseActivity extends SherlockFragmentActivity {
    
    private static final String TAG = LogTagHelper.create(BaseActivity.class);

    //private ShareActionProvider mShareActionProvider;
    
    private static final int SEARCH_EDIT_DELAY = 500;
    
    private static final int SEARCH_EDIT_SIZE = 2;
    
    private static final int TOAST_LENGTH_SHORT = 2000; // 2 seconds

    // REMOVED FINAL ATRIBUTE
    private NavigationAction action;

    protected View contentContainer;

    private Set<MyMenuItem> menuItems;

    private final int activityLayoutId;

    protected DialogFragment dialog;

    private DialogProgressFragment baseActivityProgressDialog;

    private DialogGenericFragment dialogLogout;

    private boolean backPressedOnce = false;

    private View logoView = null;
    
    public View mDrawerNavigation;
    /**
     * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
     * @Solution :
     *           http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this
     *           -action-after-onsaveinstancestate-h
     */
    private Intent mOnActivityResultIntent = null;

    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    private int drawable_state = DrawerLayout.STATE_IDLE;

    private static final Set<EventType> HANDLED_EVENTS = EnumSet.of(
            EventType.GET_SHOPPING_CART_ITEMS_EVENT,
            EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT,
            EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT,
            EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
            EventType.INITIALIZE,
            EventType.LOGOUT_EVENT);

    

    private final Set<EventType> allHandledEvents = EnumSet.copyOf(HANDLED_EVENTS);
    private final Set<EventType> contentEvents;

    private boolean isRegistered = false;

    private View warningView;
    private View warningVariationView;

    private final int titleResId;

    private final int contentLayoutId;

    private TextView tvActionCartCount;

    private MyProfileActionProvider myProfileActionProvider;

    private FragmentController fragmentController;

    private boolean initialCountry = false;

    @SuppressWarnings("unused")
    private Set<EventType> userEvents;
    
    private Menu currentMenu;
    
    private long beginInMillis;
    
    protected SearchView mSearchView;
    
    protected SearchAutoComplete mSearchAutoComplete;
    
    protected View mSearchButton;
    
    protected boolean isSearchComponentOpened = false;

    private ViewStub mMainFallBackStub;

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     * 
     * @param userEvents
     */
    public BaseActivity(NavigationAction action, Set<MyMenuItem> enabledMenuItems,
            Set<EventType> contentEvents, Set<EventType> userEvents, int titleResId,
            int contentLayoutId) {
        this(R.layout.main, action, enabledMenuItems, contentEvents, userEvents, titleResId,
                contentLayoutId);

    }

    public BaseActivity(int activityLayoutId, NavigationAction action,
            Set<MyMenuItem> enabledMenuItems, Set<EventType> contentEvents,
            Set<EventType> userEvents,
            int titleResId, int contentLayoutId) {
        this.activityLayoutId = activityLayoutId;
        this.contentEvents = contentEvents;
        this.userEvents = userEvents;
        this.allHandledEvents.addAll(contentEvents);
        this.allHandledEvents.addAll(userEvents);
        this.action = action != null ? action : NavigationAction.Unknown;
        this.menuItems = enabledMenuItems;
        this.titleResId = titleResId;
        this.contentLayoutId = contentLayoutId;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE");
        
        JumiaApplication.INSTANCE.doBindService();

        // Validate if is phone and force orientaion
        setOrientationForHandsetDevices();

        // Get fragment controller
        fragmentController = FragmentController.getInstance();

        ShopSelector.resetConfiguration(getBaseContext());

        // Set action bar
        setupActionBar();
        // Set content view
        setupContentViews();
        // Update the content view if initial country selection 
        updateContentViewsIfInitialCountrySelection();

        isRegistered = true;
        setAppContentLayout();
        setTitle(titleResId);
        BugSenseHandler.leaveBreadcrumb(TAG + " _onCreate");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        BugSenseHandler.leaveBreadcrumb(TAG + " _onNewIntent");
        ActivitiesWorkFlow.addStandardTransition(this);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        UAirship.shared().getAnalytics().activityStarted(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ON RESUME");
        
        if (!isRegistered) {
            // OLD FRAMEWORK
            /**
             * Register service callback
             */
            JumiaApplication.INSTANCE.registerFragmentCallback(mCallback);
            isRegistered = true;
        }

        // TODO : Comment for Samsung store
        //CheckVersion.run(getApplicationContext());

        /**
         * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
         * @Solution :
         *           http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform
         *           -this-action-after-onsaveinstancestate-h
         */
        if (mOnActivityResultIntent != null && getIntent().getExtras() != null) {
            initialCountry = getIntent().getExtras().getBoolean(
                    ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false);
            mOnActivityResultIntent = null;
        }

        if (!contentEvents.contains(EventType.GET_SHOPPING_CART_ITEMS_EVENT)
                && JumiaApplication.SHOP_ID != null
                && JumiaApplication.INSTANCE.getCart() == null) {
            triggerContentEventWithNoLoading(new GetShoppingCartItemsHelper(), null, mIResponseCallback);
        }

    }

    IResponseCallback mIResponseCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            handleErrorEvent(bundle);

        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            handleSuccessEvent(bundle);
        }
    };

   

    /**
     * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
     * @Solution :
     *           http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this
     *           -action-after-onsaveinstancestate-h
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
     * 
     * @see com.actionbarsherlock.app.SherlockActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        // Hide search component
        hideSearchComponent();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ON STOP");
        UAirship.shared().getAnalytics().activityStopped(this);
        JumiaApplication.INSTANCE.setLoggedIn(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JumiaApplication.INSTANCE.unRegisterFragmentCallback(mCallback);
        JumiaApplication.INSTANCE.setLoggedIn(false);
        isRegistered = false;
        
        // getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * #### ACTION BAR ####
     */

    /**
     * Method used to update the sliding menu and items on action bar. Called from BaseFragment
     * 
     * @param enabledMenuItems
     * @param action
     * @param titleResId
     * @author sergiopereira
     */
    public void updateBaseComponents(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId) {
        Log.i(TAG, "ON UPDATE BASE COMPONENTS");
        
        // Update options menu and search bar
        menuItems = enabledMenuItems;
        hideKeyboard();
        invalidateOptionsMenu();
        // Update the sliding menu
        this.action = action != null ? action : NavigationAction.Unknown;
        updateNavigationMenu();
        // Update the title of fragment

        if (setCheckoutHeader(titleResId));
        else if (titleResId == 0) {
            hideTitle();
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
        } else {
            setTitle(titleResId);
        }

    }

    public void updateActionForCountry(NavigationAction action) {
        this.action = action != null ? action : NavigationAction.Unknown;
        updateNavigationMenu();
    }

    public void setupActionBar() {
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set custom view
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        getSupportActionBar().setCustomView(R.layout.action_bar_logo_layout);
        logoView = getSupportActionBar().getCustomView().findViewById(R.id.ic_logo);
        logoView.setOnClickListener(onActionBarClickListener);
    }

    private void setupContentViews() {
        Log.d(TAG, "DRAWER: SETUP CONTENT VIEWS");
        setContentView(activityLayoutId);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerNavigation = findViewById(R.id.fragment_navigation);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                //                 getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                onClosed();
                getSupportActionBar().updateUpState(true);
            }

            public void onDrawerOpened(View drawerView) {
                //                 getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                onOpened();
                getSupportActionBar().updateUpState(false);
            }
            
            @Override
            public void onDrawerStateChanged(int newState) {
              //Log.d(TAG, "SEARCH: ON STATE CHANGED " + newState);
                drawable_state = newState;
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        contentContainer = (ViewGroup) findViewById(R.id.rocket_app_content);
        //loadingBarContainer = findViewById(R.id.loading_bar);
        //loadingBarView = (LoadingBarView) findViewById(R.id.loading_bar_view);
        warningView = findViewById(R.id.warning);
        warningVariationView = findViewById(R.id.warning_variations);
        warningView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showWarning(false);

            }
        });
        warningVariationView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showWarningVariation(false);

            }
        });
        //errorView = findViewById(R.id.alert_view);
                
        // Get the fallback stub
        mMainFallBackStub = (ViewStub) findViewById(R.id.main_fall_back_stub);
        
    }
    
    
    /**
     * Updated the action bar and the navigation for initial country selection
     * @author sergiopereira
     */
    private void updateContentViewsIfInitialCountrySelection(){
        /**
         * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
         * @Solution :
         *           http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform
         *           -this-action-after-onsaveinstancestate-h
         */
        if (getIntent().getExtras() != null) {
            initialCountry = getIntent().getExtras().getBoolean(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false);
            mOnActivityResultIntent = null;
        }

        /**
         * Set the action bar and navigation drawer for initialCountry
         * @author sergiopereira
         */
        if (initialCountry) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setCustomView(R.layout.action_bar_initial_logo_layout);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    /**
     * Toggle the navigation drawer
     */
    public void toggle() {
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation)) {
            mDrawerLayout.closeDrawer(mDrawerNavigation);
        } else {
            if (mDrawerLayout.getDrawerLockMode(mDrawerNavigation) != DrawerLayout.LOCK_MODE_LOCKED_OPEN) {
                mDrawerLayout.openDrawer(mDrawerNavigation);
            }
        }
    }
    
    public void closeDrawerIfOpen() {
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation)) {
            mDrawerLayout.closeDrawer(mDrawerNavigation);
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean result = super.dispatchKeyEvent(event);
        if (result) {
            return result;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            toggle();
            return true;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Log.i(TAG, "ON BACK PRESSED");
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation)
                && !(mDrawerLayout.getDrawerLockMode(mDrawerNavigation) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)
                ) {
            
            mDrawerLayout.closeDrawer(mDrawerNavigation);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and
     * onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "ON POST CREATE: DRAWER");
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        // Removed Categories TAB
        /*-// Validate if navigation is open (on orientation change)
        if(mDrawerLayout.isDrawerOpen(mDrawerNavigation)) showWizardNavigation();*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "ON ORIENTATION CHANGED: DRAWER");
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * ############### SLIDE MENU #################
     */

    /**
     * Update the sliding menu
     */
    public void updateNavigationMenu() {
        Log.d(TAG, "UPDATE SLIDE MENU");
        NavigationFragment slideMenuFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation);
        if (slideMenuFragment != null) slideMenuFragment.onUpdateMenu();
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

    /**
     * Listener used for custom view on action bar
     */
    OnClickListener onActionBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                toggle();
            } else if (!initialCountry && drawable_state == DrawerLayout.STATE_IDLE) {
                // Hide search component and keyboard
                hideSearchComponent();
                hideKeyboard();
                // Goto home
                onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    };
    

    /**
     * ############### ORIENTATION #################
     */

    public void setOrientationForHandsetDevices() {
        // Validate if is phone and force portrait orientaion
        if (!getResources().getBoolean(R.bool.isTablet)) {
            Log.i(TAG, "IS PHONE: FORCE PORTRAIT ORIENTATION");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Verifies if the current screen orientation is Landscape
     * 
     * @return true if yes, false otherwise
     */
    public static boolean isTabletInLandscape(Context context) {
        if (context == null) {
            return false;
        }
        if (context.getResources().getBoolean(R.bool.isTablet)
                && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        return false;
    }

    public static boolean showSearchOnActionBar(Context context) {
        return true;
        // return isTabletInLandscape(context)
    }

    /**
     * ############### OPTIONS MENU #################
     */

    /**
     * When a user selects an option of the menu that is on the action bar. The centralization of
     * this in this activity, prevents all the activities to have to handle this events
     * 
     * @param item
     *            The menu item that was pressed
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected(android.view.MenuItem
     * )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "ON OPTION ITEM SELECTED: " + item.getTitle());
        
        // Get item id
        int itemId = item.getItemId();
        
        // HOME
        if (itemId == android.R.id.home) {
            toggle();
            return true;
        
        // CART
        } else if (itemId == R.id.menu_basket) {
            Log.i(TAG, "code1state : "+mDrawerLayout.getDrawableState()[0]+" : idle : "+ DrawerLayout.STATE_IDLE+" : dragging :  "+DrawerLayout.STATE_DRAGGING+"  : settling : "+DrawerLayout.STATE_SETTLING);
            if(drawable_state == DrawerLayout.STATE_IDLE){
                closeDrawerIfOpen();
                onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
            return false;
            
        // DEFAULT:    
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.d(TAG, "ON OPTIONS MENU: CREATE");
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        
        // Save the current menu
        currentMenu = menu;

        /**
         * Setting Menu Options
         */
        for (MyMenuItem item : menuItems) {
            switch (item) {
            case SEARCH_VIEW:
                Log.i(TAG, "ON OPTIONS MENU: CREATE SEARCH VIEW");
                setActionBarSearch(menu);
                break;
            /*-case SHARE:
                menu.findItem(item.resId).setVisible(true);
                menu.findItem(item.resId).setEnabled(true);
                mShareActionProvider = (ShareActionProvider) menu.findItem(item.resId).getActionProvider();
                mShareActionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
                            @Override
                            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                                getApplicationContext().startActivity(intent);
                                TrackerDelegator.trackItemShared(getApplicationContext(), intent);
                                return true;
                            }
                        });
                setShareIntent(createShareIntent());
                break;*/
            case MY_PROFILE:
                MenuItem myProfile = menu.findItem(item.resId); //menu.findItem(item.resId);
                myProfile.setVisible(true);
                myProfile.setEnabled(true);
                setMyProfile(myProfile);
                break;
            default:
                menu.findItem(item.resId).setVisible(true);
                break;
            }
        }
        if(!initialCountry){
            tvActionCartCount = (TextView) menu.findItem(R.id.menu_basket).getActionView().findViewById(R.id.cart_count);
            tvActionCartCount.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu.performIdentifierAction(R.id.menu_basket, 0);
                }
            });
            updateCartInfoInActionBar();
            
        } else {
            menu.findItem(R.id.menu_basket).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * ############### SEARCH COMPONENT #################
     */

    /**
     * Method used to set the search bar in the Action bar.
     * 
     * @param menu
     * @author Andre Lopes
     */
    private void setActionBarSearch(Menu menu) {
        MenuItem mSearchMenuItem = menu.findItem(R.id.menu_search);
        // Get search views
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchAutoComplete = (SearchAutoComplete) mSearchView.findViewById(R.id.abs__search_src_text);
        mSearchButton = mSearchView.findViewById(R.id.abs__search_button);
        // Set the ime options
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // 
        mSearchMenuItem.setVisible(true);
        // Get the width of main content
        logoView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int logoViewWidth = logoView.getMeasuredWidth() + logoView.getPaddingRight();
        int mainContentWidth = WindowHelper.getWidth(getApplicationContext());
        int genericIconWidth = getResources().getDimensionPixelSize(R.dimen.item_height_normal);
        // Calculate the search width
        int searchComponentWidth = mainContentWidth - logoViewWidth - genericIconWidth;
        Log.d(TAG, "SEARCH WIDTH SIZE: " + searchComponentWidth);
        // Set measures
        mSearchView.setMaxWidth(searchComponentWidth);
        mSearchAutoComplete.setDropDownWidth(searchComponentWidth);
        // Set hint
        mSearchView.setQueryHint(getString(R.string.action_label_search_hint));
        mSearchAutoComplete.setHintTextColor(getResources().getColor(R.color.grey_middlelight));
        // Set search
        setActionBarSearchBehavior();
    }
    
    /**
     * Set the search component
     */
    public void setActionBarSearchBehavior() {
        Log.d(TAG, "SEARCH MODE: NEW BEHAVIOUR");
        if(mSearchAutoComplete == null) {
            Log.w(TAG, "SEARCH COMPONENT IS NULL");
            return;
        }
        
        mSearchAutoComplete.dismissDropDown();
        mSearchAutoComplete.setFocusable(false);
        mSearchAutoComplete.setFocusableInTouchMode(true);
        mSearchView.setOnClickListener(null);

        /*
         * On item click listener
         */
        mSearchAutoComplete.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Log.d(TAG, "SEARCH: CLICKED ITEM " + position);
                SearchSuggestion selectedSuggestion = (SearchSuggestion) adapter.getItemAtPosition(position);
                String text = selectedSuggestion.getResult();
                mSearchAutoComplete.setText("");
                mSearchAutoComplete.dismissDropDown();
                GetSearchSuggestionHelper.saveSearchQuery(text);
                showSearchCategory(text);
            }
        });
        
//        /*
//         * Listener for right drawable
//         */
//        mSearchAutoComplete.setOnTouchListener(new RightDrawableOnTouchListener(mSearchAutoComplete) {
//            @Override
//            public boolean onDrawableTouch(final MotionEvent event) {
//                String searchTerm = mSearchAutoComplete.getText().toString();
//                Log.d(TAG, "SEARCH: ON RIGHT DRAWABLE TOUCH: " + searchTerm);
//                if ( TextUtils.isEmpty( searchTerm )) return false;
//                GetSearchSuggestionHelper.saveSearchQuery(searchTerm);
//                executeSearchRequest(searchTerm);
//                return true;
//            }
//            
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(findViewById(R.id.main_fallback_content) != null && findViewById(R.id.main_fallback_content).getVisibility() == View.VISIBLE){
//                    return true;
//                }
//                Log.d(TAG, "SEARCH: ON TOUCH: " + event.getAction());
//                // Close navigation menu
//                if (mDrawerLayout != null  && mDrawerLayout.isDrawerOpen(mDrawerNavigation)) 
//                    mDrawerLayout.closeDrawer(mDrawerNavigation);
//                // Force show drop down
//                if (TextUtils.isEmpty(mSearchAutoComplete.getText()) && event.getAction() == MotionEvent.ACTION_DOWN)
//                    mSearchAutoComplete.showDropDown();
//                // Sent to supper
//                return super.onTouch(v, event);
//            }
//            
//        });
        
        /*
         * Clear and add text listener
         */
        // mSearchAutoComplete.clearTextChangedListeners();
        mSearchAutoComplete.addTextChangedListener(new TextWatcher() {
            private Handler handle = new Handler();
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                handle.removeCallbacks(run);
                if (s.length() >= SEARCH_EDIT_SIZE && isSearchComponentOpened) handle.postDelayed(run, SEARCH_EDIT_DELAY);
            }
        });
        
        /*
         * Hide the search component on back pressed
         */
        mSearchView.setOnImeBackListener(new SearchViewImeBackListener() {
            @Override
            public void onImeBackPressed() {
                Log.d(TAG, "SEARCH ON IME PRESSED BACK");
                hideSearchComponent();
            }
        });
        
        /*
         * Hide the search component on click on close button
         */
        mSearchView.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "SEARCH ON CLOSE PRESSED");
                isSearchComponentOpened = false;
                hideSearchComponent();
                return true;
            }
        });
        
        /*
         * Show the search component on click on search button
         */
        mSearchView.setOnSearchClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SEARCH ON CLICK VIEW");
                // Validate menu state
                if(drawable_state == DrawerLayout.STATE_IDLE) closeDrawerIfOpen();
                isSearchComponentOpened = true;
                setItemsVisibility(false);
            }
        });
        
        /*
         * Editor 
         */
        mSearchAutoComplete.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(android.widget.TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO){
                    String searchTerm = mSearchAutoComplete.getText().toString();
                    Log.d(TAG, "SEARCH COMPONENT: ON IME ACTION " + searchTerm);
                    if (TextUtils.isEmpty(searchTerm)) return false;
                    mSearchAutoComplete.setText("");
                    GetSearchSuggestionHelper.saveSearchQuery(searchTerm);
                    showSearchCategory(searchTerm);
                    return true;
                }
                return false;
            }
        });
        
//        /*
//         * On focus to show the last recent searches
//         */
//        mSearchAutoComplete.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d(TAG, "SEARCH: ON FOCUS " + hasFocus);
//                // Get the recent searches
//                String searchTerm = ((SearchAutoComplete) v).getText().toString();
//                if(hasFocus && TextUtils.isEmpty(searchTerm) && searchTerm.length() >= SEARCH_EDIT_SIZE) {
//                    Log.d(TAG, "GET AUTO SUGGESTION FOR FOCUS");
//                    new Handler().postDelayed(run, SEARCH_EDIT_DELAY);
//                }
//            }
//        });
    }
    
    /**
     * Execute search
     * @param searchText
     * @author sergiopereira
     */
    protected void showSearchCategory(String searchText) {
        Log.d(TAG, "SEARCH COMPONENT: GOTO PROD LIST");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, searchText);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, searchText);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
//    /**
//     * Clean search component
//     * @author sergiopereira
//     */
//    public void cleanSearchConponent() {
//        Log.d(TAG, "CLEAN SEARCH COMPONENT");
//        //mSearchAutoComplete.removeTextChangedListener(listener);
//        //mSearchHandle.removeCallbacks(run);
//        
//        if(mSearchAutoComplete != null) mSearchAutoComplete.setText("");
//        
//    }

    /**
     * set all menu items visibility to <code>visible</code>
     * 
     * @param visible
     */
    protected void setItemsVisibility(boolean visible) {
        for (MyMenuItem item : menuItems) {
            switch (item) {
            case SEARCH_VIEW:
                break;
            default:
                currentMenu.findItem(item.resId).setVisible(visible);
                break;
            }
        }
        // set visibility for menu_basket
        currentMenu.findItem(R.id.menu_basket).setVisible(visible);
    }

    /**
     * Hide the search component
     * @param menu
     * @author sergiopereira
     */
    protected void hideSearchComponent(){
        Log.d(TAG, "SEARCH COMPONENT: HIDE");
        try {
            // Validate if exist search icon and bar
            if(menuItems.contains(MyMenuItem.SEARCH_VIEW)) {
                // Hide search bar
                mSearchView.onActionViewCollapsed();
                // Clean autocomplete
                mSearchAutoComplete.setText("");
                // show hidden items
                setItemsVisibility(true);
                // Forced the ime option on collapse
                mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON HIDE SEARCH COMPONENT", e);
        }
    }
    
    /**
     * Hide only the search bar, used by ChangeCountryFragment
     * @author sergiopereira
     * @param enumSet 
     */
    public void hideActionBarItemsForChangeCountry(EnumSet<MyMenuItem> enumSet){
        // Validate if the current menu options contains the search bar item
    	// TODO : Validate if is necessary the setIconified
        if (menuItems.contains(MyMenuItem.SEARCH_VIEW)) {
            // Hide search bar
            mSearchView.setIconified(true);
        }
        this.menuItems = enumSet;
        this.action = NavigationAction.Country;
        updateActionForCountry(NavigationAction.Country);
        invalidateOptionsMenu();
    }
    
//    /**
//     * Remove search focus
//     * @author sergiopereira
//     */
//    private void searchComponentDismissFocus(){
//        Log.d(TAG, "SEARCH: DISMISS FOCUS");
//    }
    
    /**
     * ############### SEARCH TRIGGER #################
     */ 
    
    /**
     * Runnable to get suggestions
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
     * @author sergiopereira
     */
    private void getSuggestions(){
        beginInMillis = System.currentTimeMillis();
        String text = mSearchAutoComplete.getText().toString();
        Log.d(TAG, "SEARCH COMPONENT: GET SUG FOR " + text);
        
        Bundle bundle = new Bundle();
        bundle.putString(GetSearchSuggestionHelper.SEACH_PARAM, text);
        JumiaApplication.INSTANCE.sendRequest(new GetSearchSuggestionHelper(), bundle, new IResponseCallback() {
            
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
      * @param event
      * @author sergiopereira
      */
     private void processErrorSearchEvent(Bundle bundle){
         Log.d(TAG, "SEARCH COMPONENT: ON ERROR");
         // Get query
         String requestQuery = bundle.getString(GetSearchSuggestionHelper.SEACH_PARAM);
         Log.d(TAG, "RECEIVED SEARCH ERROR EVENT: " + requestQuery);
         // Validate current search component
         if (mSearchAutoComplete != null && !mSearchAutoComplete.getText().toString().equals(requestQuery)) {
             Log.w(TAG, "SEARCH ERROR: WAS DISCARTED FOR QUERY " + requestQuery);
             return;
         }
         if(!currentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId).isVisible()) return;
         // Hide dropdown
         mSearchAutoComplete.dismissDropDown();
     }
     
     /**
      * Process success search event
      * @param event
      * @author sergiopereira
      */
     private void processSuccessSearchEvent(Bundle bundle){
         Log.d(TAG, "SEARCH COMPONENT: ON SUCCESS");
         // Get suggestions
         List<SearchSuggestion> sug = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
         // Get query
         String requestQuery = bundle.getString(GetSearchSuggestionHelper.SEACH_PARAM);
         Log.d(TAG, "RECEIVED SEARCH EVENT: " + sug.size() + " " + requestQuery );
         
         // Validate current objects
         if(menuItems == null && currentMenu == null && mSearchAutoComplete == null) return;
         // Validate current menu items
         if(menuItems!= null && !menuItems.contains(MyMenuItem.SEARCH_VIEW)) return;
         if(!currentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId).isVisible()) return;
         // Validate current search
         if(mSearchAutoComplete.getText().length() < SEARCH_EDIT_SIZE || !mSearchAutoComplete.getText().toString().equals(requestQuery)){
             Log.w(TAG, "SEARCH: DISCARTED DATA FOR QUERY " + requestQuery);
             return;
         }
         // Show suggestions
         Log.i(TAG, "SEARCH: SHOW DATA FOR QUERY " + requestQuery);
         AnalyticsGoogle.get().trackLoadTiming(R.string.gsearchsuggestions, beginInMillis);
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

    public static int maskRequestCodeId(int code) {
        return code & 0x0000FFFF;
    }

    /**
     * Displays the number of items that are currently on the shopping cart as well as its value.
     * This information is displayed on the navigation list
     * 
     * @param value
     *            The current value of the shopping cart
     * @param quantity
     *            The number of items that currently the shopping cart holds
     */
    public void updateCartInfo() {
        Log.d(TAG, "ON UPDATE CART INFO");
        if (JumiaApplication.INSTANCE.getCart() != null) {
            Log.d(TAG, "updateCartInfo value = " + JumiaApplication.INSTANCE.getCart().getCartValue()
                            + " quantity = "
                            + JumiaApplication.INSTANCE.getCart().getCartCount());
        }
        updateCartInfoInActionBar();
        updateCartInfoInNavigation();
    }

    public void updateCartInfoInActionBar() {
        Log.d(TAG, "ON UPDATE CART IN ACTION BAR");
        if (tvActionCartCount == null) {
            Log.w(TAG, "updateCartInfoInActionBar: cant find quantity in actionbar");
            return;
        }

        final String quantity = JumiaApplication.INSTANCE.getCart() == null 
                                ? "?"
                                : JumiaApplication.INSTANCE.getCart().getCartCount() > 0 
                                    ? String.valueOf(JumiaApplication.INSTANCE.getCart().getCartCount()) 
                                    : "";
           
        tvActionCartCount.post(new Runnable() {
            @Override
            public void run() {
                tvActionCartCount.setText(quantity);
            }
        });

    }

    private void updateCartInfoInNavigation() {
        Log.d(TAG, "ON UPDATE CART IN NAVIGATION");
        NavigationFragment navigation = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation);
        if (navigation != null) navigation.onUpdateCart();
        else Log.w(TAG, "updateCartInfoInNavigation: navigation container empty - doing nothing");
    }

//    private void updateTotalFavourites() {
//        /* -if (textViewFavouritesCount == null) {
//            Log.w(TAG, "updateFavouritesCountInActionBar: cant find FavouritesCount in actionbar");
//            return;
//        }
//
//        textViewFavouritesCount.post(new Runnable() {
//            @Override
//            public void run() {
//                textViewFavouritesCount.setText("5");
//            }
//        });*/
//    }

    /**
     * Create the share intent to be used to store the needed information
     * @return The created intent
     */
    public Intent createShareIntent() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        //sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        // Get product
        CompleteProduct prod = JumiaApplication.INSTANCE.getCurrentProduct();
        // Validate
        if (null != prod) {
            // For tracking when sharing
            sharingIntent.putExtra(getString(R.string.mixprop_sharelocation), getString(R.string.mixprop_sharelocationproduct));
            sharingIntent.putExtra(getString(R.string.mixprop_sharecategory), prod.getCategories().size() > 0 ? prod.getCategories().get(0) : "");
            sharingIntent.putExtra(getString(R.string.mixprop_sharename), prod.getName());
            sharingIntent.putExtra(getString(R.string.mixprop_sharebrand), prod.getBrand());
            sharingIntent.putExtra(getString(R.string.mixprop_shareprice), prod.getPrice());
            sharingIntent.putExtra(RestConstants.JSON_SKU_TAG, prod.getSku());
            String msg = getString(R.string.share_checkout_this_product) + "\n" + prod.getUrl().replace("/mobapi", "");
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
     */
    private void setMyProfile(MenuItem myProfile) {
        if (myProfile != null) {
            myProfileActionProvider = (MyProfileActionProvider) myProfile.getActionProvider();
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

                    closeDrawerIfOpen();

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

                        dialogLogout = DialogGenericFragment.newInstance(false, true, false,
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
                        onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    }
                    break;
                case Favourite:
                    // FAVOURITES
                    onSwitchFragment(FragmentType.FAVOURITE_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    break;
                case RecentSearch:
                    // RECENT SEARCHES
                    onSwitchFragment(FragmentType.RECENTSEARCHES_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    break;
                case RecentlyView:
                    // RECENTLY VIEWED
                    onSwitchFragment(FragmentType.RECENTLYVIEWED_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    break;
                case MyAccount:
                    // MY ACCOUNT
                    onSwitchFragment(FragmentType.MY_ACCOUNT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    break;
                case TrackOrder:
                    // TRACK ORDER
                    onSwitchFragment(FragmentType.TRACK_ORDER, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
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

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#setTitle(java.lang.CharSequence)
     */
    @Override
    public void setTitle(CharSequence title) {
        TextView titleView = (TextView) findViewById(R.id.titleProducts);
        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
        View header_title = findViewById(R.id.header_title);
        subtitleView.setVisibility(View.GONE);
        if (header_title == null)
            return;

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            header_title.setVisibility(View.VISIBLE);            
        } else if (TextUtils.isEmpty(title)) {
            header_title.setVisibility(View.GONE);
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
        View header_title = findViewById(R.id.header_title);

        if (titleView == null)
            return;
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(subtitle)) {
            Log.d(TAG, "------------->>>>>>>>>>>>>> SET TITLE ->" + title + "; " + subtitle);
            // Set text and force measure
            subtitleView.setText(subtitle);
            // Set title
            titleView.setText(title);
            // Set visibility
            header_title.setVisibility(View.VISIBLE);
            subtitleView.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(title)) {
            header_title.setVisibility(View.GONE);
        }
    }

    public void hideTitle() {
        findViewById(R.id.header_title).setVisibility(View.GONE);
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
     * Don't show loading if we are using fragments, no need to redraw all the layout...
     * 
     * @param event
     */

    private final void triggerContentEventWithNoLoading(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }
//
//    private final void triggerContentEvent(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
//        showLoading(false);
//        sendRequest(helper, args, responseCallback);
//    }

//    protected final void triggerContentEventProgress(final BaseHelper helper, Bundle args,
//            final IResponseCallback responseCallback) {
//        showProgress();
//        sendRequest(helper, args, responseCallback);
//    }

//    private final void showLoadingInfo() {
//        Log.d(TAG, "Showing loading info");
//        if (loadingBarContainer != null) {
//            loadingBarContainer.setVisibility(View.VISIBLE);
//        } else {
//            Log.w(TAG, "Did not find loading bar container, check layout!");
//        }
//        if (loadingBarView != null) {
//            loadingBarView.startRendering();
//        }
//    }

//    /**
//     * Hides the loading screen that appears on the front of the activity while it waits for the
//     * data to arrive from the server
//     */
//    private void hideLoadingInfo() {
//        Log.d(TAG, "Hiding loading info");
//        if (loadingBarView != null) {
//            loadingBarView.stopRendering();
//        }
//        if (loadingBarContainer != null) {
//            loadingBarContainer.setVisibility(View.GONE);
//        }
//    }
//
//    private static void setVisibility(View view, boolean show) {
//        if (view != null) {
//            view.setVisibility(show ? View.VISIBLE : View.GONE);
//        }
//    }

//    public void showError(final BaseHelper helper, final Bundle bundle,
//            final IResponseCallback responseCallback) {
//        showError(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                showWarning(false);
//                triggerContentEvent(helper, bundle, responseCallback);
//            }
//        }, false);
//    }

//    public void showLoading(boolean fromCheckout) {
//        setVisibility(errorView, false);
//        if (!fromCheckout) {
//            setVisibility(contentContainer, false);
//        }
//
//        showLoadingInfo();
//    }

//    protected void showError(OnClickListener clickListener, boolean fromCheckout) {
//        Log.d(TAG, "Showing error view");
//        hideLoadingInfo();
//        if (!fromCheckout) {
//            setVisibility(contentContainer, false);
//        }
//        setVisibility(errorView, true);
//        errorView.setOnClickListener(clickListener);
//    }

//    public final void showContentContainer() { // XXX
//        if (processShow) {
//            Log.d(TAG, "Showing the content container");
//            hideLoadingInfo();
//            dismissProgress();
//            setVisibility(errorView, false);
//            setVisibility(contentContainer, true);
//        }
//    }

    public final void showWarning(boolean show) {
        Log.d(TAG, "Showing warning: " + show);
        if (warningView != null) warningView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showWarningVariation(boolean show) {
        if (warningVariationView != null) warningVariationView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setAppContentLayout() {
        if (contentLayoutId == 0) {
            return;
        }
        ViewStub stub = (ViewStub) findViewById(R.id.stub_app_content);
        stub.setLayoutResource(contentLayoutId);
        contentContainer = stub.inflate();
    }

    public final void showProgress() {
        
        if (baseActivityProgressDialog != null) {
            return;
        }
        
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                baseActivityProgressDialog = DialogProgressFragment.newInstance();
                baseActivityProgressDialog.show(getSupportFragmentManager(), null);
            }
        });        
        
    }

    public final void dismissProgress() {
        if (baseActivityProgressDialog != null) {
            baseActivityProgressDialog.dismiss();
            baseActivityProgressDialog = null;
        }
    }

    public void onOpened() {
        Log.d(TAG, "onOpened");
        // Hide search component and hide keyboard
        hideSearchComponent();
        hideKeyboard();
        // Update cart
        AnalyticsGoogle.get().trackPage(R.string.gnavigation);
        // Removed Categories TAB
        /*-// Validate
        showWizardNavigation();*/
    }

    public void onClosed() {
        Log.d(TAG, "onClosed");
    }

    /**
     * Service Stuff
     */

    public void unbindDrawables(View view) {

        try {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            else if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                if (view instanceof AdapterView<?>)
                    return;
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "LOW MEM");
        System.gc();
    }

    public void hideKeyboard() {
        // Log.d( TAG , "hideKeyboard" );
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = mDrawerLayout;
        if (v == null)
            v = getWindow().getCurrentFocus();

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showKeyboard() {
        // Log.d( TAG, "showKeyboard" );
        Log.i(TAG, "code1here showKeyboard");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0);
        // use the above as the method below does not always work
        // imm.showSoftInput(getSlidingMenu().getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
    }

//    /**
//     * Use this variable to have a more precise control on when to show the content container. The
//     * content will show by default after finishing the event request.
//     * 
//     * @param b
//     */
//    public void setProcessShow(boolean b) {
//        processShow = b;
//    }

    public void finishFromAdapter() {
        finish();
    }

    /**
     * Method called then the activity is connected to the service
     */
    protected void onServiceActivation() {

    }

    /**
     * Handles a successful event and reflects necessary changes on the UI.
     * 
     * @param event
     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
     */

    public void handleSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        
        switch (eventType) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            updateCartInfo();
            break;
        case LOGOUT_EVENT:
            Log.i(TAG, "LOGOUT EVENT");
            onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            JumiaApplication.INSTANCE.setCart(null);
            updateNavigationMenu();
            dismissProgress();
            break;
        case LOGIN_EVENT:
            JumiaApplication.INSTANCE.setLoggedIn(true);
            triggerContentEventWithNoLoading(new GetShoppingCartItemsHelper(), null, mIResponseCallback);
            break;
        }
    }

    /**
     * Handles a failed event and shows dialogs to the user.
     * 
     * @param event
     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
     */
    @SuppressWarnings("unchecked")
    public boolean handleErrorEvent(final Bundle bundle) {
        final EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        
        if (eventType == EventType.LOGIN_EVENT){
            JumiaApplication.INSTANCE.setLoggedIn(false);
            JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
            updateNavigationMenu();
        }
        
        if( !bundle.getBoolean(Constants.BUNDLE_PRIORITY_KEY) ){
            return false;
        }
        
        HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
        if (errorCode == null) {
            return false;
        }
        if (errorCode.isNetworkError()) {
            switch (errorCode) {
            case NO_NETWORK:
                //showContentContainer();

                // Remove dialog if exist
                if (dialog != null) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                dialog = DialogGenericFragment.createNoNetworkDialog(this,
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                JumiaApplication.INSTANCE.sendRequest(
                                        JumiaApplication.INSTANCE.getRequestsRetryHelperList().get(
                                                eventType),
                                        JumiaApplication.INSTANCE.getRequestsRetryBundleList().get(
                                                eventType), JumiaApplication.INSTANCE
                                                .getRequestsResponseList().get(eventType));
                                dialog.dismiss();
                            }
                        }, false);
                try {
                    dialog.show(getSupportFragmentManager(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case SERVER_IN_MAINTENANCE:
                setLayoutMaintenance(eventType);
                return true;
            case REQUEST_ERROR:
                List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                String dialogMsg = "";
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
                }
                if (validateMessages != null) {
                    for (String message : validateMessages) {
                        dialogMsg += message + "\n";
                    }
                } else {
                    for (Entry<String, ? extends List<String>> entry : errorMessages.entrySet()) {
                        dialogMsg += entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                    }
                }
                if (dialogMsg.equals("")) {
                    dialogMsg = getString(R.string.validation_errortext);
                }
                //showContentContainer();
                dialog = DialogGenericFragment.newInstance(
                        true, true, false, getString(R.string.validation_title),
                        dialogMsg, getResources().getString(R.string.ok_label), "",
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int id = v.getId();
                                if (id == R.id.button1) {
                                    dialog.dismiss();
                                }

                            }

                        });

                dialog.show(getSupportFragmentManager(), null);
                return true;
            default:
                //showContentContainer();

                // Remove dialog if exist
                if (dialog != null) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                dialog = DialogGenericFragment.createNoNetworkDialog(this,
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                JumiaApplication.INSTANCE.sendRequest(
                                        JumiaApplication.INSTANCE.getRequestsRetryHelperList().get(
                                                eventType),
                                        JumiaApplication.INSTANCE.getRequestsRetryBundleList().get(
                                                eventType), JumiaApplication.INSTANCE
                                                .getRequestsResponseList().get(eventType));
                                dialog.dismiss();
                            }
                        }, false);
                try {
                    dialog.show(getSupportFragmentManager(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

        }
        return false;

    }


    /**
     * @return the action
     */
    public NavigationAction getAction() {
        return action;
    }

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
     * This method should be implemented by fragment activity to manage the work flow for fragments.
     * Each fragment should call this method.
     * 
     * @param search
     * @param addToBackStack
     * @author sergiopereira
     */
    public abstract void onSwitchFragment(FragmentType search, Bundle bundle, Boolean addToBackStack);

    /**
     * Method used to switch fragment on UI with/without back stack support
     * 
     * @param fragment
     * @param addToBackStack
     * @author sergiopereira
     */
    public void fragmentManagerTransition(int container, Fragment fragment, String tag,
            Boolean addToBackStack) {
        // showContentContainer(false);
        fragmentController.startTransition(this, container, fragment, tag, addToBackStack);
    }

    /**
     * Method used to perform a back stack using fragments
     * 
     * @author sergiopereira
     */
    public void fragmentManagerBackPressed() {
        //showContentContainer();
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
     * @param inclusive
     * @author sergiopereira
     */
    public void popBackStackUntilTag(String tag) {
        fragmentController.popAllEntriesUntil(this, tag);
    }

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     * 
     * @param userEvents
     * @author manuelsilva
     * 
     */
    public interface OnActivityFragmentInteraction {
        public void sendValuesToFragment(int identifier, Object values);

        public void sendPositionToFragment(int position);

        public void sendListener(int identifier, OnClickListener clickListener);

        public boolean allowBackPressed();
    }

    public void onFragmentSelected(FragmentType fragmentIdentifier) {
    }

    public void onFragmentElementSelected(int position) {
    }

    public void sendClickListenerToActivity(OnClickListener clickListener) {
    }

    public void sendValuesToActivity(int identifier, Object values) {
    }

    /**
     * Confirm backPress to exit application
     * 
     */
    public Boolean exitApplication(final FragmentController fragC) {

        dialog = DialogGenericFragment.newInstance(false, false, true,
                null, // no title
                getString(R.string.logout_text_question), getString(R.string.no_label),
                getString(R.string.yes_label), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        int id = v.getId();
                        if (id == R.id.button1) {
                            // fragC.popLastEntry();
                        } else if (id == R.id.button2) {
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
     * @see <a
     *      href="http://stackoverflow.com/questions/7965135/what-is-the-duration-of-a-toast-length-long-and-length-short">Toast
     *      duration</a> <br>
     *      Toast.LENGTH_LONG is 3500 seconds. <br>
     *      Toast.LENGTH_SHORT is 2000 seconds.
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
        CustomToastView.makeText(this, getString(R.string.exit_press_back_again),
                Toast.LENGTH_SHORT).show();
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
        // bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+ " size is : "
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
    public void hideMainFallBackView(){
        if(mMainFallBackStub != null) mMainFallBackStub.setVisibility(View.GONE);
    }
    

    /**
     * Sets Maintenance page
     */
    private void setLayoutMaintenance(final EventType eventType) {
        // Inflate maintenance
        mMainFallBackStub.setVisibility(View.VISIBLE);
        // Get retry button
        Button retry = (Button) findViewById(R.id.main_fallback_retry);
        retry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {;
                mMainFallBackStub.setVisibility(View.GONE);
                String result = JumiaApplication.INSTANCE.sendRequest(
                        JumiaApplication.INSTANCE.getRequestsRetryHelperList().get(eventType),
                        JumiaApplication.INSTANCE.getRequestsRetryBundleList().get(eventType),
                        JumiaApplication.INSTANCE.getRequestsResponseList().get(eventType));

                if (result.equalsIgnoreCase("") || result == null) {
                    onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                }
            }
        });
        
        ImageView mapImageView = (ImageView) findViewById(R.id.main_fallback_country_map);
        SharedPreferences sharedPrefs = this.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        RocketImageLoader.instance.loadImage(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_MAP_FLAG, ""), mapImageView); 
        
        String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, "");
       
        TextView fallbackBest = (TextView) findViewById(R.id.main_fallback_best);
        fallbackBest.setText(R.string.fallback_best);
        if (country.split(" ").length == 1) {
            TextView tView = (TextView) findViewById(R.id.main_fallback_country);
            tView.setVisibility(View.VISIBLE);
            tView.setText(country.toUpperCase());
            TextView txView = (TextView) findViewById(R.id.main_fallback_options_bottom);
            txView.setVisibility(View.VISIBLE);
            txView.setText(country.toUpperCase());
            findViewById(R.id.main_fallback_country_double).setVisibility(View.GONE);
        } else {
            TextView tView = (TextView) findViewById(R.id.main_fallback_country_top);
            tView.setText(country.split(" ")[0].toUpperCase());
            TextView tViewBottom = (TextView) findViewById(R.id.main_fallback_country_bottom);
            tViewBottom.setText(country.split(" ")[1].toUpperCase());
            fallbackBest.setTextSize(11.88f);
            TextView txView = (TextView) findViewById(R.id.main_fallback_options_bottom);
            txView.setVisibility(View.VISIBLE);
            txView.setText(country.toUpperCase());
            findViewById(R.id.main_fallback_country_double).setVisibility(View.VISIBLE);
            findViewById(R.id.main_fallback_country).setVisibility(View.GONE);

        }
        
        TextView mTextViewBT = (TextView) findViewById(R.id.main_fallback_country_bottom_text);
        mTextViewBT.setText(R.string.fallback_maintenance_text);
        
        TextView mTextViewBT2 = (TextView) findViewById(R.id.main_fallback_country_bottom_text2);
        mTextViewBT2.setText(R.string.fallback_maintenance_text_bottom);
        
        TextView mFallbackChoice = (TextView) findViewById(R.id.main_fallback_choice);
        mFallbackChoice.setText(R.string.fallback_choice);
        
        TextView mFallbackDoorstep = (TextView) findViewById(R.id.main_fallback_doorstep);
        mFallbackDoorstep.setText(R.string.fallback_doorstep);
        
        fallbackBest.setSelected(true);

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
            updateBaseComponentsInCheckout(visibility);
            hideOnlySteps();
            break;
        case ConstantsCheckout.CHECKOUT_NO_SET_HEADER:
            // Hide title and total
            hideTitle();
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
            break;
        default:
            visibility = View.GONE;
            result = false;
            updateBaseComponentsOutCheckout(visibility);
            break;
        }

        // Return value
        return result;
    }

    /**
     * Hide only the steps
     * 
     * @author sergiopereira
     */
    private void hideOnlySteps() {
        findViewById(R.id.checkout_header_main_step).setVisibility(View.INVISIBLE);
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
        // Put sliding menu normal behavior
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
        // Validate device and orientation
        if (showSearchOnActionBar(getApplicationContext())) {
            // Update with for main content
            findViewById(R.id.main_layout).getLayoutParams().width = LayoutParams.MATCH_PARENT;
        }
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
            unSelectStep(R.id.checkout_header_step_1, R.id.checkout_header_step_1_icon,
                    R.id.checkout_header_step_1_text);
            break;
        case ConstantsCheckout.CHECKOUT_BILLING:
            unSelectStep(R.id.checkout_header_step_2, R.id.checkout_header_step_2_icon,
                    R.id.checkout_header_step_2_text);
            break;
        case ConstantsCheckout.CHECKOUT_SHIPPING:
            unSelectStep(R.id.checkout_header_step_3, R.id.checkout_header_step_3_icon,
                    R.id.checkout_header_step_3_text);
            break;
        case ConstantsCheckout.CHECKOUT_PAYMENT:
            unSelectStep(R.id.checkout_header_step_4, R.id.checkout_header_step_4_icon,
                    R.id.checkout_header_step_4_text);
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
            selectStep(R.id.checkout_header_step_1, R.id.checkout_header_step_1_icon,
                    R.id.checkout_header_step_1_text);
            break;
        case ConstantsCheckout.CHECKOUT_BILLING:
            selectStep(R.id.checkout_header_step_2, R.id.checkout_header_step_2_icon,
                    R.id.checkout_header_step_2_text);
            break;
        case ConstantsCheckout.CHECKOUT_SHIPPING:
            selectStep(R.id.checkout_header_step_3, R.id.checkout_header_step_3_icon,
                    R.id.checkout_header_step_3_text);
            break;
        case ConstantsCheckout.CHECKOUT_PAYMENT:
            selectStep(R.id.checkout_header_step_4, R.id.checkout_header_step_4_icon,
                    R.id.checkout_header_step_4_text);
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
        findViewById(main).getLayoutParams().width = getResources().getDimensionPixelSize(
                R.dimen.checkout_header_step_selected_width);
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
        findViewById(main).getLayoutParams().width = getResources().getDimensionPixelSize(
                R.dimen.checkout_header_step_width);
        findViewById(icon).setSelected(false);
        findViewById(text).setVisibility(View.GONE);
    }

    /**
     * Checkout header click listener associated to each item on layout
     * 
     * @param view
     * @author sergiopereira
     */
    public void OnCheckoutHeaderClickListener(View view) {
        Log.i(TAG, "PROCESS CLICK ON CHECKOUT HEADER");
        int id = view.getId();
        // CHECKOUT_ABOUT_YOU
        if (id == R.id.checkout_header_step_1 && !view.isSelected()) {
            // Uncomment if you want click on about you step
            // removeAllCheckoutEntries();
            // onSwitchFragment(FragmentType.ABOUT_YOU, FragmentController.NO_BUNDLE,
            // FragmentController.ADD_TO_BACK_STACK);
        }
        // CHECKOUT_BILLING
        else if (id == R.id.checkout_header_step_2 && !view.isSelected()) {
            // Validate back stack
            if (FragmentController.getInstance().hasEntry(FragmentType.MY_ADDRESSES.toString()))
                FragmentController.getInstance().popAllEntriesUntil(this, FragmentType.MY_ADDRESSES.toString());
            else if (FragmentController.getInstance().hasEntry(FragmentType.CREATE_ADDRESS.toString())) {
                removeAllCheckoutEntries();
                onSwitchFragment(FragmentType.ABOUT_YOU, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
        // CHECKOUT_SHIPPING
        else if (id == R.id.checkout_header_step_3 && !view.isSelected()) {
            // Validate back stack
            if (FragmentController.getInstance().hasEntry(FragmentType.SHIPPING_METHODS.toString()))
                FragmentController.getInstance().popAllEntriesUntil(this, FragmentType.SHIPPING_METHODS.toString());
        }
        // CHECKOUT_PAYMENT IS THE LAST
    }

    /**
     * Remove all checkout entries to call the base of checkout
     * 
     * @author sergiopereira
     */
    private void removeAllCheckoutEntries() {
        FragmentController.getInstance().removeAllEntriesWithTag(
                FragmentType.PAYMENT_METHODS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(
                FragmentType.SHIPPING_METHODS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(
                FragmentType.MY_ADDRESSES.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(
                FragmentType.CREATE_ADDRESS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(
                FragmentType.EDIT_ADDRESS.toString());
        FragmentController.getInstance().removeAllEntriesWithTag(FragmentType.POLL.toString());
    }

    /**
     * Get the current menu options specified by current fragment
     * @return a set of menu items
     * @author sergiopereira 
     */
    public Set<MyMenuItem> getCurrentMenuOptions() {
        return menuItems;
    }

    /**
     * ##### WIZARDS #####
     */
    // Removed Categories TAB
    /*-private void showWizardNavigation() {
        WizardFactory.show(WizardType.NAVIGATION, getApplicationContext(), getSupportFragmentManager(), null);
    }*/
    
}

