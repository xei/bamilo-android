package com.bamilo.android.appmodule.bamiloapp.view;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.ActivitiesWorkFlow;
import com.bamilo.android.appmodule.bamiloapp.controllers.LogOut;
import com.bamilo.android.appmodule.bamiloapp.controllers.SearchDropDownAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.GetShoppingCartItemsHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.search.GetSearchSuggestionsHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.search.SearchSuggestionClient;
import com.bamilo.android.appmodule.bamiloapp.helpers.search.SuggestionsStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.interfaces.OnProductViewHolderClickListener;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckVersion;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.ConfigurationWrapper;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.MyProfileActionProvider;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TopViewAutoHideUtil;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.CustomToastView;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogGenericFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogProgressFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.tracking.emarsys.EmarsysTracker;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ConfirmationCartMessageView;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.FixedDrawerDrawable;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UITabLayoutUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment.KeyboardState;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.DrawerFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.OldProductDetailsFragment;
import com.bamilo.android.appmodule.modernbamilo.customview.XeiTextView;
import android.widget.TextView;

import com.bamilo.android.appmodule.modernbamilo.util.typography.TypeFaceHelper;
import com.bamilo.android.framework.components.recycler.HorizontalSpaceItemDecoration;
import com.bamilo.android.framework.service.database.SearchRecentQueriesTableHelper;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.objects.search.Suggestion;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.tracking.gtm.GTMValues;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import me.toptas.fancyshowcase.FancyShowCaseView;

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
 * <p/>
 * 2012/06/19
 * @modified Sergio Pereira
 * @modified Manuel Silva
 */
public abstract class BaseActivity extends BaseTrackerActivity implements TabLayout.OnTabSelectedListener, OnProductViewHolderClickListener {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int RESULT_SPEECH = 1993;

    private static final int SEARCH_EDIT_DELAY = 500;
    private static final int SEARCH_EDIT_SIZE = 2;
    private static final int TOAST_LENGTH_SHORT = 2000; // 2 seconds
    private static final String HAMBURGER_ICON_ITEM_TRACKING_SHOWCASE = "hamburger_icon_item_tracking_showcase";
    private static final int SHOWCASE_DELAY = 500;

    @KeyboardState
    public static int sCurrentAdjustState;
    private final int activityLayoutId;
    private final int titleResId;
    public View mDrawerNavigation;
    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public MenuItem mSearchMenuItem;
    private WarningFactory warningFactory;
    protected DialogFragment dialog;
    protected CustomSearchActionView mSearchView;
    //    protected EditText mSearchAutoComplete;
    protected RecyclerView mSearchListView;
    protected FrameLayout mSearchOverlay;
    protected boolean isSearchComponentOpened = false;
    @NavigationAction.Type
    private int action;
    private Set<MyMenuItem> menuItems;
    private DialogProgressFragment baseActivityProgressDialog;
    private DialogGenericFragment dialogLogout;
    private boolean backPressedOnce = false;
    /**
     * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
     * @Solution : http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this -action-after-onsaveinstancestate-h
     */
    private Intent mOnActivityResultIntent = null;
    private XeiTextView mActionCartCount, mActionCartIndicatorCount;
    private MyProfileActionProvider myProfileActionProvider;
    private FragmentController mFragmentController;
    private boolean initialCountry = false;
    private Menu mCurrentMenu;
    //DROID-10
    private long mGABeginInMillis;

    private ActionBar mSupportActionBar;
    private Toolbar toolbar;
    private boolean isBackButtonEnabled = false;
    private boolean isCloseButtonEnabled = false;
    private TabLayout mExtraTabLayout;
    private float mExtraTabLayoutHeight = -1;
    private AppBarLayout mAppBarLayout;
    public ConfirmationCartMessageView mConfirmationCartMessageView;

    public DrawerFragment mDrawerFragment;
    private boolean searchBarEnabled = false;
    private boolean voiceTyping = false;
    private boolean actionBarAutoHideEnabled = false;
    private TopViewAutoHideUtil searchBarAutoHide;
    private TopViewAutoHideUtil actionBarAutoHide;

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     */
    public BaseActivity(@NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, int titleResId) {
        this(R.layout.main, action, enabledMenuItems, titleResId);
    }

    /**
     * Constructor
     */
    private BaseActivity(int activityLayoutId, @NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, int titleResId) {
        this.activityLayoutId = activityLayoutId;
        this.action = action;
        this.menuItems = enabledMenuItems;
        this.titleResId = titleResId;
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

    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.attachBaseContext(ConfigurationWrapper.wrapLocale(newBase, new Locale("fa", "ir")));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        ScrollerCompat nestedScrollerCompat = ScrollerCompat.create(this);
        /*
         * In case of rotation the activity is restarted and the locale too.<br>
         * These method forces the right locale used before the rotation.
         * @author spereira
         */
        ShopSelector.setLocaleOnOrientationChanged(getApplicationContext());
        // In case app is killed in background needs to restore font type

        // TODO: 8/28/18 farshid
//        HoloFontLoader.initFont(getResources().getBoolean(R.bool.is_shop_specific));
        // Get fragment controller
        mFragmentController = FragmentController.getInstance();
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
        // Set title in AB or TitleBar
        setTitle(titleResId);


        // perform showcase on drawer hamburger icon
        View actionView = getToolbarNavigationIcon(toolbar);
        if (actionView != null) {
            actionView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isBackButtonEnabled || isCloseButtonEnabled) {
                        onBackPressed();
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    if (FancyShowCaseView.isVisible(BaseActivity.this)) {
                        FancyShowCaseView.hideCurrent(BaseActivity.this);
                    }
                }
            });
            ShowcasePerformer.createSimpleCircleShowcase(this, HAMBURGER_ICON_ITEM_TRACKING_SHOWCASE,
                    actionView, getString(R.string.showcase_hamburger_icon_item_tracking), getString(R.string.showcase_got_it), SHOWCASE_DELAY)
                    .show();
        }

    }

    public static View getToolbarNavigationIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getNavigationContentDescription());
        String contentDescription = !hadContentDescription ? toolbar.getNavigationContentDescription().toString() : "navigationIcon";
        toolbar.setNavigationContentDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setNavigationContentDescription ensures its existence
        View navIcon = null;
        if (potentialViews.size() > 0) {
            navIcon = potentialViews.get(0); //navigation icon is ImageButton
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setNavigationContentDescription(null);
        return navIcon;
    }

    /**
     * Using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Print.i(TAG, "ON POST CREATE: DRAWER");
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

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
        updateCartInfo();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // Show the upgrade dialog
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Print.i(TAG, "ON CONFIGURATION CHANGED");
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        // Hide search component
        hideSearchComponent();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     * @FIX: IllegalStateException: Can not perform this action after onSaveInstanceState
     * @Solution : http://stackoverflow.com/questions/7575921/illegalstateexception -can-not-perform-this -action-after-onsaveinstancestate-h
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                mSearchView.setText(text.get(0));

                SimpleEventModel sem = new SimpleEventModel();
                sem.category = CategoryConstants.SEARCH_RESULTS;
                sem.action = EventActionKeys.VOICE_SEARCH;
                sem.label = text.get(0);
                sem.value = SimpleEventModel.NO_VALUE;
                TrackerManager.trackEvent(this, EventConstants.SearchBarSearched, sem);

                fireSearch(text.get(0));
            }
            voiceTyping = false;
        } else if (data != null) {
            mOnActivityResultIntent = data;
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
     * ############## ACTION BAR ##############
     */

    /**
     * Method used to update the sliding menu and items on action bar. Called from BaseFragment
     */
    public void updateBaseComponents(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int newNavAction, int actionBarTitleResId, int checkoutStep) {
        Print.i(TAG, "ON UPDATE BASE COMPONENTS");
        // Update options menu and search bar
        this.menuItems = enabledMenuItems;
        // Update the current nav action
        int oldNavAction = this.action;
        this.action = newNavAction;
        // Update the app bar layout
        setAppBarLayout(oldNavAction, newNavAction);
        // Update Options Menu
        invalidateOptionsMenu();
        // Set actionbarTitle
        setActionTitle(actionBarTitleResId);
    }

    /*
     * ############## NAVIGATION ##############
     */

    public void setupAppBarLayout() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.viewToobarElevationMock).setVisibility(View.VISIBLE);
        }
        // Get tab layout
        mAppBarLayout = findViewById(R.id.app_bar);
        // Get tool bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance);
        setSupportActionBar(toolbar);
        mSupportActionBar = getSupportActionBar();
        if (mSupportActionBar != null) {
            mSupportActionBar.setDisplayHomeAsUpEnabled(true);
            mSupportActionBar.setHomeButtonEnabled(true);
            mSupportActionBar.setDisplayShowTitleEnabled(true);
            mSupportActionBar.setElevation(0);
            mSupportActionBar.setLogo(R.drawable.logo_nav_bar);
        }
    }

    public void enableSearchBar(final boolean autoHide, final View... views) {
        searchBarEnabled = true;
        final View searchBar = findViewById(R.id.searchBar);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) searchBar.getLayoutParams();
        params.setMargins(params.leftMargin, 0, params.rightMargin, params.bottomMargin);
        searchBar.setLayoutParams(params);
        searchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchMenuItem.expandActionView();
            }
        });
        searchBar.setVisibility(View.VISIBLE);
        searchBar.post(new Runnable() {
            @Override
            public void run() {
                if (views != null) {
                    for (View v : views) {
                        v.setPadding(v.getPaddingLeft(), (int) getResources().getDimension(R.dimen.search_bar_height),
                                v.getPaddingRight(), v.getPaddingBottom());
                    }
                }
                ViewGroup.MarginLayoutParams warningParams =
                        (ViewGroup.MarginLayoutParams) findViewById(R.id.warning).getLayoutParams();
                warningParams.topMargin = searchBar.getHeight();
                findViewById(R.id.warning).setLayoutParams(warningParams);

                if (autoHide && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    searchBarAutoHide = new TopViewAutoHideUtil(-searchBar.getHeight(), 0, searchBar);
                    searchBarAutoHide.setOnViewShowHideListener(new TopViewAutoHideUtil.OnViewShowHideListener() {
                        @Override
                        public void onViewHid() {
                            if (searchBarEnabled) {
                                mSearchMenuItem.setVisible(true);
                            }
                        }

                        @Override
                        public void onViewShowed() {
                            if (searchBarEnabled) {
                                mSearchMenuItem.setVisible(false);
                            }
                        }
                    });
                }
            }
        });
    }

    public void onSearchBarScrolled(int dy) {
        if (searchBarEnabled && searchBarAutoHide != null) {
            searchBarAutoHide.onScroll(-dy);
        }
    }

    public void syncSearchBarState(int scrollAmount) {
        if (searchBarEnabled && searchBarAutoHide != null) {
            searchBarAutoHide.syncState(scrollAmount);
        }
    }

    public void disableSearchBar() {
        searchBarEnabled = false;
        findViewById(R.id.searchBar).setVisibility(View.GONE);
        searchBarAutoHide = null;
        ViewGroup.MarginLayoutParams warningParams =
                (ViewGroup.MarginLayoutParams) findViewById(R.id.warning).getLayoutParams();
        warningParams.topMargin = 0;
        findViewById(R.id.warning).setLayoutParams(warningParams);
    }

    public boolean enableActionbarAutoHide(final View... views) {
        actionBarAutoHideEnabled = false;
        if (!searchBarEnabled) {
            actionBarAutoHideEnabled = true;
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = 0;
            toolbar.setLayoutParams(params);

            toolbar.post(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.rlScrollableContent).setPadding(0, 0, 0, 0);
                    if (views != null) {
                        for (View v : views) {
                            v.setPadding(v.getPaddingLeft(), toolbar.getHeight(),
                                    v.getPaddingRight(), v.getPaddingBottom());
                        }
                    }
                    ViewGroup.MarginLayoutParams warningParams =
                            (ViewGroup.MarginLayoutParams) findViewById(R.id.warning).getLayoutParams();
                    warningParams.topMargin = toolbar.getHeight();
                    findViewById(R.id.warning).setLayoutParams(warningParams);
                    actionBarAutoHide = new TopViewAutoHideUtil(-toolbar.getHeight(), 0, toolbar);
                }
            });
        }
        return actionBarAutoHideEnabled;
    }

    public void disableActionbarAutoHide() {
        actionBarAutoHideEnabled = false;
        actionBarAutoHide = null;
        findViewById(R.id.rlScrollableContent).setPadding(0, toolbar.getHeight(), 0, 0);
        ViewGroup.MarginLayoutParams warningParams =
                (ViewGroup.MarginLayoutParams) findViewById(R.id.warning).getLayoutParams();
        warningParams.topMargin = 0;
        findViewById(R.id.warning).setLayoutParams(warningParams);
    }

    /*
     * ############## CONTENT VIEWS ##############
     */

    private void setAppBarLayout(@NavigationAction.Type int oldNavAction, @NavigationAction.Type int newNavAction) {
        try {
            // Case enable/disable actionbar auto-hide
            if (!UITabLayoutUtils.isNavigationActionbarAutoHide(newNavAction)) {
                /*AppBarLayout.LayoutParams params =
                        (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                params.setScrollFlags(0);
                toolbar.setLayoutParams(params);*/
                actionBarAutoHideEnabled = false;
            } else if (!UITabLayoutUtils.isNavigationActionbarAutoHide(oldNavAction)) {
                /*AppBarLayout.LayoutParams params =
                        (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                toolbar.setLayoutParams(params);*/
                actionBarAutoHideEnabled = true;
            }

            // Case action without tab layout
            if (!UITabLayoutUtils.isNavigationActionWithTabLayout(newNavAction)) {
                mAppBarLayout.setExpanded(true, true);
            }
            // Case action with tab layout
            else {
                // Case from other tab
                if (!UITabLayoutUtils.isNavigationActionWithTabLayout(oldNavAction)) {
                    mAppBarLayout.setExpanded(true, true);
                }
                //noinspection ConstantConditions
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

    public void setupDrawerNavigation() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerNavigation = findViewById(R.id.fragment_navigation);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
                if (mDrawerFragment != null) {
                    mDrawerFragment.performShowcase(BaseActivity.this, R.string.drawer_order_tracking);
                }
            }
        };
        mDrawerToggle.setDrawerArrowDrawable(new FixedDrawerDrawable(this, R.drawable.ic_action_hamburger));
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_action_selector);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (mDrawerFragment != null) {
            mDrawerFragment.CreateDrawer();
        }
    }

    public void updateCartDrawerItem() {
        if (mDrawerFragment != null) {
            mDrawerFragment.CreateDrawer();
        }
        /*DrawerFragment navigationCategoryFragment = new DrawerFragment();
        NavigationCategoryFragment.getInstance(bundle);
        fragmentChildManagerTransition(R.id.navigation_container_list, filterType, navigationCategoryFragment, false, true);
        PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
        if (cart != null && cart.getCartCount() != 0) {
            mainDrawer.updateBadge(drawer_identifier_cart, new StringHolder(cart.getCartCount() + ""));
        }*/
    }

    /*
     * ############### OPTIONS MENU #################
     */

    public void closeNavigationDrawer() {
        if (FancyShowCaseView.isVisible(BaseActivity.this)) {
            FancyShowCaseView.hideCurrent(this);
        }
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.closeDrawer(mDrawerNavigation);
                }
            });
        }
    }

    private void setupContentViews() {
        Print.d(TAG, "DRAWER: SETUP CONTENT VIEWS");
        // Warning layout
        try {
            warningFactory = new WarningFactory(findViewById(R.id.warning));
            //view for configurable confirmation message when adding to carte, in case of hasCartPopup = true
            mConfirmationCartMessageView = new ConfirmationCartMessageView(findViewById(R.id.configurableCartView), this);
        } catch (IllegalStateException ex) {
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

    /**
     * Method used to validate if is to show the initial country selection or is in maintenance.<br>
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
        isCloseButtonEnabled = false;
        // Setting Menu Options
        for (MyMenuItem item : menuItems) {
            switch (item) {
                case HIDE_AB:
                    showActionBar = View.GONE;
                    break;
                case CLOSE_BUTTON:
                    isCloseButtonEnabled = true;
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
                case BASKET_INDICATOR:
                    setActionCartIndicator(menu);
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
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // ...
    }

    /**
     * Set the up button in ActionBar
     *
     * @author sergiopereira
     */
    private void setActionBarUpButton() {
        if (isCloseButtonEnabled) {
            Print.i(TAG, "SHOW CLOSE BUTTON");
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_action_close_white);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else if (isBackButtonEnabled) {
            Print.i(TAG, "SHOW UP BUTTON");
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_action_selector);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            Print.i(TAG, "NO SHOW UP BUTTON");
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        // TODO: 7/29/2017 Cleanup
        mDrawerToggle.syncState();
    }

    @Override
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
            mActionCartCount = actionCartView.findViewById(R.id.action_cart_count);
            mActionCartCount.setVisibility(View.INVISIBLE);
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

    /**
     * Set the cart-indicator menu item
     */
    private void setActionCartIndicator(final Menu menu) {
        MenuItem basket = menu.findItem(MyMenuItem.BASKET_INDICATOR.resId);
        // Validate country
        if (!initialCountry) {
            basket.setVisible(true);
            basket.setEnabled(false);
            View actionCartView = MenuItemCompat.getActionView(basket);
            actionCartView.findViewById(R.id.action_cart_image).setEnabled(false);
            mActionCartIndicatorCount = actionCartView.findViewById(R.id.action_cart_count);
            mActionCartIndicatorCount.setVisibility(View.INVISIBLE);
            updateCartIndicatorInfoInActionBar();
        } else {
            basket.setVisible(false);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        UITabLayoutUtils.tabSelected(this, tab, action);
    }

    /*
     * ############### SEARCH COMPONENT #################
     */

    /**
     * Method used to set the search bar in the Action bar.
     *
     * @author Andre Lopes
     * @modified sergiopereira
     */
    private void setActionSearch(Menu menu) {
        Print.i(TAG, "ON OPTIONS MENU: CREATE SEARCH VIEW");
        // Get search menu item
        mSearchMenuItem = menu.findItem(R.id.menu_search);
        // Get search action view
        mSearchView = (CustomSearchActionView) MenuItemCompat.getActionView(mSearchMenuItem);
        setSearchWidthToFillOnExpand();
        mSearchView.setVoiceSearchClickListener(new CustomSearchActionView.VoiceSearchClickListener() {
            @Override
            public void onVoiceSearchClicked(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("fa"));
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint));

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    voiceTyping = true;
                } catch (ActivityNotFoundException a) {
                    mSearchView.setVoiceSearchEnabled(false);
                }
            }
        });
        // Get edit text
        mSearchOverlay = findViewById(R.id.search_overlay);
        mSearchListView = mSearchOverlay.findViewById(R.id.search_overlay_listview);
        mSearchListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mSearchListView.addItemDecoration(new HorizontalSpaceItemDecoration(getApplicationContext(), R.drawable._gen_divider_horizontal_black_400));
        // Set font
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(mSearchView);
        // Initial state
        MenuItemCompat.collapseActionView(mSearchMenuItem);
        // Set search
        setActionBarSearchBehavior(mSearchMenuItem);
        // Set visibility
        if (searchBarEnabled) {
            mSearchMenuItem.setVisible(false);
        } else {
            mSearchMenuItem.setVisible(true);
        }
    }

    private void setSearchWidthToFillOnExpand() {
        // Get the width of main content
        final int mainContentWidth = DeviceInfoHelper.getWidth(getApplicationContext());
        mSearchView.setMaxWidth(mainContentWidth);
    }

    /**
     * Set the search component
     */
    public void setActionBarSearchBehavior(final MenuItem mSearchMenuItem) {
        Print.d(TAG, "SEARCH MODE: NEW BEHAVIOUR");
        if (mSearchView == null) {
            Print.w(TAG, "SEARCH COMPONENT IS NULL");
            return;
        }

        /*
         * Clear and add text listener
         */
        mSearchView.addTextChangedListener(new TextWatcher() {
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
                } else if (TextUtils.isEmpty(s) && isSearchComponentOpened) {
                    getSuggestions();
                }
            }
        });

        /*
         * Set IME action listener
         */
        mSearchView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(android.widget.TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO) {
                    String searchTerm = textView.getText().toString();
                    Print.d(TAG, "SEARCH COMPONENT: ON IME ACTION " + searchTerm);
                    if (TextUtils.isEmpty(searchTerm)) {
                        getSuggestions();
                        return false;
                    }

                    SimpleEventModel sem = new SimpleEventModel();
                    sem.category = CategoryConstants.SEARCH_RESULTS;
                    sem.action = EventActionKeys.SEARCH_BAR_SEARCH;
                    sem.label = searchTerm;
                    sem.value = SimpleEventModel.NO_VALUE;
                    TrackerManager.trackEvent(BaseActivity.this, EventConstants.SearchBarSearched, sem);

                    fireSearch(searchTerm);
                    return true;
                }
                return false;
            }
        });

        /*
         * Set expand listener
         */
        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new OnActionExpandListener() {
            private final Handler handle = new Handler();
            public int restoreSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (mExtraTabLayout != null) {
                    mExtraTabLayout.setVisibility(View.GONE);
                    View scrollContainer = findViewById(R.id.rlScrollableContent);
                    scrollContainer.setPadding(scrollContainer.getPaddingLeft(),
                            toolbar.getHeight(),
                            scrollContainer.getPaddingRight(), scrollContainer.getPaddingBottom());
                }
                toolbar.setBackgroundColor(Color.WHITE);
                findViewById(R.id.searchBar).setVisibility(View.GONE);
                Print.d(TAG, "SEARCH ON EXPAND");
                closeNavigationDrawer();
                isSearchComponentOpened = true;
                setActionMenuItemsVisibility(false);
                setAppBarLayout(action, NavigationAction.UNKNOWN);
                mSearchOverlay.setVisibility(View.VISIBLE);
                restoreSoftInputMode = getWindow().getAttributes().softInputMode;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                // Re-set the searched text if it exists
                mSearchView.post(new Runnable() {
                    @Override
                    public void run() {
                        String searchedTerm = BamiloApplication.INSTANCE.getSearchedTerm();
                        mSearchView.setFocus((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                        if (TextUtils.isNotEmpty(searchedTerm)) {
                            mSearchView.setText(searchedTerm);
                            mSearchView.setSelection(searchedTerm.length());
                        } else {
                            handle.removeCallbacks(run);
                            handle.postDelayed(run, SEARCH_EDIT_DELAY);
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (mExtraTabLayout != null) {
                    mExtraTabLayout.setVisibility(View.VISIBLE);
                    setScrollContentPadding(findViewById(R.id.rlScrollableContent));
                }
                isSearchComponentOpened = false;
                toolbar.setBackgroundColor(ContextCompat.getColor(BaseActivity.this, R.color.appBar));
                if (searchBarEnabled) {
                    findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
                }
                Print.d(TAG, "SEARCH ON COLLAPSE");
                setActionMenuItemsVisibility(true);
                setAppBarLayout(NavigationAction.UNKNOWN, action);
                mSearchOverlay.setVisibility(View.GONE);
                getWindow().setSoftInputMode(restoreSoftInputMode);
                hideKeyboard();
                return true;
            }
        });

        /*
         * Set focus listener, for back pressed with IME
         */
        mSearchView.setOnFieldFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Print.i(TAG, "SEARCH ON FOCUS CHANGE");
                    if (!voiceTyping && isSearchComponentOpened) {
                        MenuItemCompat.collapseActionView(mSearchMenuItem);
                        setActionMenuItemsVisibility(true);
                    }
                }
            }
        });

    }

    private void fireSearch(String searchTerm) {
        //Save searched text
        BamiloApplication.INSTANCE.setSearchedTerm(searchTerm);
        // Collapse search view
        MenuItemCompat.collapseActionView(mSearchMenuItem);
        Suggestion suggestion = new Suggestion();
        suggestion.setQuery(searchTerm);
        suggestion.setResult(searchTerm);
        suggestion.setTarget(searchTerm);
        suggestion.setType(Suggestion.SUGGESTION_OTHER);
        // Save query
        GetSearchSuggestionsHelper.saveSearchQuery(suggestion);
        // Show query
        showSearchOther(suggestion);
    }

    /**
     * Runnable to get suggestions
     *
     * @author sergiopereira
     */
    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            Print.i(TAG, "SEARCH: RUN GET SUGGESTIONS: " + mSearchView.getText());
            getSuggestions();
        }
    };

    /**
     * Execute search
     *
     * @author sergiopereira
     */
    public void showSearchCategory(final Suggestion suggestion) {
        Print.d(TAG, "SEARCH COMPONENT: GOTO PROD LIST " + suggestion.getResult());
        // Tracking
        SimpleEventModel sem = new SimpleEventModel();
        sem.category = CategoryConstants.SEARCH_RESULTS;
        sem.action = EventActionKeys.SEARCH_BAR_SEARCH;
        sem.label = suggestion.getResult();
        sem.value = SimpleEventModel.NO_VALUE;
        TrackerManager.trackEvent(BaseActivity.this, EventConstants.SearchSuggestions, sem);


        // Case mob api
        @TargetLink.Type String link = suggestion.getTarget();
        boolean result = new TargetLink(getWeakBaseActivity(), link).addTitle(suggestion.getResult()).run();
        // Case algolia
        if (!result) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.DATA, null);
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, suggestion.getResult());
            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, suggestion.getQuery());
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, suggestion.getTarget());
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
            onSwitchFragment(FragmentType.CATALOG_CATEGORY, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Execute search
     */
    public void showSearchOther(final Suggestion suggestion) {
        Print.d(TAG, "SEARCH COMPONENT: GOTO CATALOG " + suggestion.getResult());
        // Tracking
        SimpleEventModel sem = new SimpleEventModel();
        sem.category = CategoryConstants.SEARCH_RESULTS;
        sem.action = EventActionKeys.SEARCH_BAR_SEARCH;
        sem.label = suggestion.getResult();
        sem.value = SimpleEventModel.NO_VALUE;
        TrackerManager.trackEvent(BaseActivity.this, EventConstants.SearchSuggestions, sem);

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DATA, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, suggestion.getResult());
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, suggestion.getResult());
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
        onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Execute search for product
     */
    public void showSearchProduct(Suggestion suggestion) {
        Print.d(TAG, "SEARCH COMPONENT: GOTO PROD VIEW " + suggestion.getResult());
        SimpleEventModel sem = new SimpleEventModel();
        sem.category = CategoryConstants.SEARCH_RESULTS;
        sem.action = EventActionKeys.SEARCH_BAR_SEARCH;
        sem.label = suggestion.getResult();
        sem.value = SimpleEventModel.NO_VALUE;
        TrackerManager.trackEvent(BaseActivity.this, EventConstants.SearchSuggestions, sem);

        // Case mob api
        @TargetLink.Type String link = suggestion.getTarget();
        boolean result = new TargetLink(getWeakBaseActivity(), link).addTitle(suggestion.getResult()).run();
        // Case algolia
        if (!result) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, suggestion.getTarget());
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Execute search for shop in shop
     */
    public void showSearchShopsInShop(final Suggestion suggestion) {
        Print.d(TAG, "SEARCH COMPONENT: GOTO SHOP IN SHOP " + suggestion.getResult());
        SimpleEventModel sem = new SimpleEventModel();
        sem.category = CategoryConstants.SEARCH_RESULTS;
        sem.action = EventActionKeys.SEARCH_BAR_SEARCH;
        sem.label = suggestion.getResult();
        sem.value = SimpleEventModel.NO_VALUE;
        TrackerManager.trackEvent(BaseActivity.this, EventConstants.SearchSuggestions, sem);

        // Case mob api
        @TargetLink.Type String link = suggestion.getTarget();
        boolean result = new TargetLink(getWeakBaseActivity(), link).addTitle(suggestion.getResult()).run();
        Print.d(TAG, "SEARCH COMPONENT: mainDrawer " + result);
        // Case algolia
        if (!result) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, suggestion.getResult());
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, suggestion.getTarget());
            onSwitchFragment(FragmentType.INNER_SHOP, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
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
            if (!voiceTyping && isSearchComponentOpened) {
                if (menuItems.contains(MyMenuItem.SEARCH_VIEW) && mSearchMenuItem != null) {
                    // Hide search bar
                    MenuItemCompat.collapseActionView(mSearchMenuItem);

                    // Show hidden items
                    setActionMenuItemsVisibility(true);
                }
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
        long beginInMillis = System.currentTimeMillis();
        mGABeginInMillis = System.currentTimeMillis();
        final String text = mSearchView.getText();
        Print.d(TAG, "SEARCH COMPONENT: GET SUG FOR " + text);
        SearchSuggestionClient mSearchSuggestionClient = new SearchSuggestionClient();
        mSearchSuggestionClient.getSuggestions(getApplicationContext(), new IResponseCallback() {
            @Override
            public void onRequestComplete(final BaseResponse baseResponse) {
                processSuccessSearchEvent(baseResponse);
            }

            @Override
            public void onRequestError(final BaseResponse baseResponse) {
                processErrorSearchEvent(baseResponse);
            }
        }, text, CountryPersistentConfigs.isUseAlgolia(getApplicationContext()));
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
     * Process the search messageItem event
     *
     * @author sergiopereira
     */
    private void processErrorSearchEvent(BaseResponse baseResponse) {
        Print.d(TAG, "SEARCH COMPONENT: ON ERROR");

        SuggestionsStruct suggestionsStruct = (SuggestionsStruct) baseResponse.getContentData();

        // Get query
        String requestQuery = suggestionsStruct.getSearchParam();
        Print.d(TAG, "RECEIVED SEARCH ERROR EVENT: " + requestQuery);
        // Validate current search component
        if (mSearchView != null && !mSearchView.getText().equals(requestQuery)) {
            Print.w(TAG, "SEARCH ERROR: WAS DISCARDED FOR QUERY " + requestQuery);
            return;
        }
        if (!mCurrentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId).isVisible()) {
            return;
        }
        // Hide dropdown
        if (suggestionsStruct.size() == 0) {
//            mSearchAutoComplete.dismissDropDown();
        } else {
            //show dropdown with recent queries
            SearchDropDownAdapter searchSuggestionsAdapter = new SearchDropDownAdapter(getApplicationContext(), suggestionsStruct);
            searchSuggestionsAdapter.setOnViewHolderClickListener(this);
            mSearchListView.setAdapter(searchSuggestionsAdapter);
        }
    }

    /**
     * Process successes search event
     *
     * @author sergiopereira
     */
    private void processSuccessSearchEvent(BaseResponse baseResponse) {
        Print.d(TAG, "SEARCH COMPONENT: ON SUCCESS");
        // Get suggestions
        SuggestionsStruct suggestionsStruct = (SuggestionsStruct) baseResponse.getContentData();
        // Get query
        String requestQuery = suggestionsStruct.getSearchParam();
        Print.d(TAG, "RECEIVED SEARCH EVENT: " + suggestionsStruct.size() + " " + requestQuery);

        // Validate current objects
        if (menuItems == null || mCurrentMenu == null || mSearchView == null) {
            return;
        }
        // Validate current menu items
        if (!menuItems.contains(MyMenuItem.SEARCH_VIEW)) {
            return;
        }
        MenuItem searchMenuItem = mCurrentMenu.findItem(MyMenuItem.SEARCH_VIEW.resId);
        if (searchMenuItem != null && !(searchMenuItem.isVisible() || searchBarEnabled)) {
            return;
        }
        // Validate current search
        if (!TextUtils.equals(mSearchView.getText(), requestQuery)) {
            Print.w(TAG, "SEARCH: DISCARDED DATA FOR QUERY " + requestQuery);
            return;
        }
        BaseScreenModel screenModel = new BaseScreenModel(getString(R.string.gaSearchSuggestions), getString(R.string.gaScreen), requestQuery, mGABeginInMillis);
        TrackerManager.trackScreenTiming(this, screenModel);
        SearchDropDownAdapter searchSuggestionsAdapter = new SearchDropDownAdapter(getApplicationContext(), suggestionsStruct);
        searchSuggestionsAdapter.setOnViewHolderClickListener(this);
        mSearchListView.setAdapter(searchSuggestionsAdapter);

    }

    /**
     * Displays the number of items that are currently on the shopping cart as well as its value. This information is displayed on the navigation list
     */
    public void updateCartInfo() {
        Print.d(TAG, "ON UPDATE CART INFO");
        updateCartInfoInActionBar();
        updateCartIndicatorInfoInActionBar();

        updateCartDrawerItem();
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

        PurchaseEntity currentCart = BamiloApplication.INSTANCE.getCart();
        // Show 0 while the cart is not updated
        final int quantity = currentCart == null ? 0 : currentCart.getCartCount();

        mActionCartCount.post(new Runnable() {
            @Override
            public void run() {
                if (quantity > 0) {
                    mActionCartCount.setVisibility(View.VISIBLE);
                    mActionCartCount.setText(String.valueOf(quantity));
                } else {
                    mActionCartCount.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public void updateCartIndicatorInfoInActionBar() {
        Print.d(TAG, "ON UPDATE CART IN ACTION BAR");
        if (mActionCartIndicatorCount == null) {
            Print.w(TAG, "updateCartInfoInActionBar: cant find quantity in actionbar");
            return;
        }

        PurchaseEntity currentCart = BamiloApplication.INSTANCE.getCart();
        // Show 0 while the cart is not updated
        final int quantity = currentCart == null ? 0 : currentCart.getCartCount();

        mActionCartIndicatorCount.post(new Runnable() {
            @Override
            public void run() {
                if (quantity > 0) {
                    mActionCartIndicatorCount.setVisibility(View.VISIBLE);
                    mActionCartIndicatorCount.setText(String.valueOf(quantity));
                } else {
                    mActionCartIndicatorCount.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * Create the share intent to be used to store the needed information
     *
     * @return The created intent
     */
    public Intent createShareIntent(String extraSubject, String extraText) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
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
            // commented next line because options menu is the same in all pages
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
//                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_HOME);
                        onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.CATEGORIES:
                        onSwitchFragment(FragmentType.CATEGORIES, null, true);
                        break;
                    case NavigationAction.LOGIN_OUT:
                        // SIGN IN
                        if (BamiloApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
                            dialogLogout = DialogGenericFragment.newInstance(true, false,
                                    getString(R.string.logout_title),
                                    getString(R.string.logout_text_question),
                                    getString(R.string.no_label),
                                    getString(R.string.yes_label),
                                    new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.button2) {
                                                LogOut.perform(getWeakBaseActivity(), null);
                                            }
                                            dialogLogout.dismiss();
                                        }
                                    });
                            dialogLogout.show(getSupportFragmentManager(), null);
                        } else {
//                            TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_SIGN_IN);
                            onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        }
                        break;
                    case NavigationAction.SAVED:
                        // FAVOURITES
//                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_FAVORITE);
                        // Validate customer is logged in
                        if (!BamiloApplication.isCustomerLoggedIn()) {
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
//                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENT_SEARCHES);
                        onSwitchFragment(FragmentType.RECENT_SEARCHES_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.RECENTLY_VIEWED:
                        // RECENTLY VIEWED
//                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_RECENTLY_VIEW);
                        onSwitchFragment(FragmentType.RECENTLY_VIEWED_LIST, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.MY_ACCOUNT:
                        // MY ACCOUNT
//                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_MY_ACCOUNT);
                        onSwitchFragment(FragmentType.MY_ACCOUNT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.MY_ORDERS:
                        // TRACK ORDER
//                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_TRACK_ORDER);
                        onSwitchFragment(FragmentType.MY_ORDERS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.COUNTRY:
                        onSwitchFragment(FragmentType.CHOOSE_COUNTRY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.FAQ:
                        // FAQ
                        @TargetLink.Type String link = TargetLink.SHOP_IN_SHOP.concat("::help-android");
                        new TargetLink(getWeakBaseActivity(), link)
                                .addTitle(R.string.faq)
                                .setOrigin(TeaserGroupType.MAIN_TEASERS)
                                //.addAppendListener(this)
                                //.addCampaignListener(this)
                                .retainBackStackEntries()
                                .enableWarningErrorMessage()
                                .run();
                        //TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_FAQ);
                        //onSwitchFragment(FragmentType.STATIC_PAGE, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                        break;
                    case NavigationAction.ABOUT:
                        // MY ABOUT
//                        TrackerDelegator.trackOverflowMenu(TrackingEvent.AB_MENU_MY_ACCOUNT);
                        onSwitchFragment(FragmentType.ABOUT_US, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
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
     * Show and set title on actionbar
     */
    public void setActionBarTitle(@StringRes int actionBarTitleResId) {
        mSupportActionBar.setLogo(null);
        mSupportActionBar.setTitle(getString(actionBarTitleResId));

        ViewGroup customView = toolbar;
        for(int i = 0 ; i < customView.getChildCount() ; i++) {
            if (customView.getChildAt(i) instanceof TextView) {
                ((TextView)customView.getChildAt(i)).setTypeface(TypeFaceHelper.getInstance(this).getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_REGULAR));
            }
        }
    }

    public void setActionBarTitle(@NonNull String title) {
        mSupportActionBar.setLogo(null);
        mSupportActionBar.setTitle(title);

        ViewGroup customView = toolbar;
        for(int i = 0 ; i < customView.getChildCount() ; i++) {
            if (customView.getChildAt(i) instanceof TextView) {
                ((TextView)customView.getChildAt(i)).setTypeface(TypeFaceHelper.getInstance(this).getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_REGULAR));
            }
        }
    }

    /**
     * Hide title on actionbar
     */
    public void hideActionBarTitle() {
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
            baseActivityProgressDialog.setCancelable(false);
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
            e.printStackTrace();
        }
    }

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

    /**
     * NOTE: Others sign out methods are performed in {@link LogOut}.
     */
    public void onLogOut() {
        OldProductDetailsFragment.clearSelectedRegionCityId();
        SearchRecentQueriesTableHelper.deleteAllRecentQueries();
        mSearchListView.setAdapter(null);

        // Global Tracker
        MainEventModel authEventModel =
                new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGOUT, null, SimpleEventModel.NO_VALUE,
                        MainEventModel.createAuthEventModelAttributes(null, null, true));
        TrackerManager.trackEvent(this, EventConstants.Logout, authEventModel);

        //Mobile Engage API
        //https://help.emarsys.com/hc/en-us/articles/115002410625
        //App Logout
        EmarsysTracker.getInstance().trackEventAppLogout();

        // Track logout
        TrackerDelegator.trackLogoutSuccessful();
        // Goto Home
        onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        // Hide progress
        dismissProgress();
        // Inform user
        showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.logout_success));

        setupDrawerNavigation();
        mDrawerToggle.syncState();
    }

    /**
     * ############### FRAGMENTS #################
     */

    /**
     * This method should be implemented by fragment activity to manage the work flow for fragments. Each fragment should call this method.
     */
    public abstract void onSwitchFragment(FragmentType type, Bundle bundle, Boolean addToBackStack);

    /**
     * This method should be implemented by fragment activity to manage the communications between fragments. Each fragment should call this method.
     */
    public abstract boolean communicateBetweenFragments(@Nullable String tag, @Nullable Bundle bundle);

    /**
     * Method used to switch fragment on UI with/without back stack support
     */
    public void fragmentManagerTransition(int container, Fragment fragment, FragmentType fragmentType, Boolean addToBackStack) {
        mFragmentController.startTransition(this, container, fragment, fragmentType, addToBackStack);
    }

    /**
     * Method used to perform a back stack using fragments
     *
     * @author sergiopereira
     */
    public void fragmentManagerBackPressed() {
        mFragmentController.fragmentBackPressed(this);
    }

    /**
     * Pop back stack until tag, FC and FM are affected.
     *
     * @param tag - The fragment tag
     * @author sergiopereira
     */
    public boolean popBackStackUntilTag(String tag) {
        if (mFragmentController.hasEntry(tag)) {
            mFragmentController.popAllEntriesUntil(this, tag);
            return true;
        }
        return false;
    }

    /**
     * Pop back stack entries until tag, only FC is affected.
     *
     * @param tag - The fragment tag
     * @author sergiopereira
     */
    public void popBackStackEntriesUntilTag(String tag) {
        if (mFragmentController.hasEntry(tag)) {
            mFragmentController.removeEntriesUntilTag(tag);
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
            mFragmentController.popLastEntry();
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
        // Show splash screen
        ActivitiesWorkFlow.splashActivityNewTask(this);
        // Finish MainFragmentActivity
        finish();
    }

    /*
     * ########## CHECKOUT ##########
     */

    /**
     * Set the current checkout step otherwise return false
     */
    /*public void setCheckoutHeader(@ConstantsCheckout.CheckoutType int checkoutStep) {
        Print.i(TAG, "SET CHECKOUT HEADER STEP ID: " + checkoutStep);
        switch (checkoutStep) {
            case ConstantsCheckout.CHECKOUT_ABOUT_YOU:
            case ConstantsCheckout.CHECKOUT_BILLING:
            case ConstantsCheckout.CHECKOUT_CONFIRMATION:
            case ConstantsCheckout.CHECKOUT_SHIPPING:
            case ConstantsCheckout.CHECKOUT_PAYMENT:
                //377-7 selectCheckoutStep(checkoutStep);
                updateBaseComponentsInCheckout(View.GONE);
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

    *//**
     * Update the base components out checkout
     *//*
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

    *//**
     * Update the base components in checkout
     *//*
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

    *//**
     * Set the selected checkout step
     *//*
    private void selectCheckoutStep(int step) {
        TabLayout.Tab tab = mCheckoutTabLayout.getTabAt(step);
        if(tab != null) {
            tab.select();
        }
    }

    *//**
     * Checkout header click listener associated to each item on layout
     *//*
    public void onCheckoutHeaderClickListener(int step) {
        Print.i(TAG, "PROCESS CLICK ON CHECKOUT HEADER " + step);
        FragmentType fragmentType = ConstantsCheckout.getFragmentType(step);

        if (fragmentType != FragmentType.UNKNOWN && mCheckoutTabLayout.getSelectedTabPosition() > step) {
            if (FragmentController.getInstance().hasEntry(fragmentType.toString())) {
                selectCheckoutStep(step);
            } else {
                onSwitchFragment(fragmentType, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    }

    *//**
     * When user changes checkout step.
     * @param step - selected position on header.
     *//*
    public void onCheckoutHeaderSelectedListener(int step) {
        // CASE TAB_CHECKOUT_ABOUT_YOU - step == 0 - click is never allowed
        // CASE TAB_CHECKOUT_BILLING
        if (step == ConstantsCheckout.CHECKOUT_BILLING) {
            String last = FragmentController.getInstance().getLastEntry();
            // Validate last entry to support create and edit address (rotation)
            if(!TextUtils.equals(last, FragmentType.CHECKOUT_CREATE_ADDRESS.toString()) &&
                    !TextUtils.equals(last, FragmentType.CHECKOUT_EDIT_ADDRESS.toString())) {
                popBackStackUntilTag(FragmentType.CHECKOUT_MY_ADDRESSES.toString());
            }
        }
        // CASE TAB_CHECKOUT_SHIPPING
        else if (step == ConstantsCheckout.CHECKOUT_SHIPPING ) {
            popBackStackUntilTag(FragmentType.CHECKOUT_SHIPPING.toString());
        }
        // CASE TAB_CHECKOUT_PAYMENT IS THE LAST  - step == 3 - click is never allowed
    }*/

    /**
     * Method used to remove all native checkout entries from the back stack on the Fragment Controller
     * Note: This method must be updated in case of adding more screens to native checkout.
     *
     * @author ricardosoares
     */
    public void removeAllNativeCheckoutFromBackStack() {
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
        if (BamiloApplication.INSTANCE.getCustomerUtils().hasCredentials() && !BamiloApplication.isCustomerLoggedIn()) {
            triggerAutoLogin();
        } else {
            // Track auto login failed if hasn't saved credentials
            MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_FAILED,
                    Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                    MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
            TrackerManager.trackEvent(BaseActivity.this, EventConstants.Login, authEventModel);
        }
        // Validate the user credentials
        if (BamiloApplication.SHOP_ID != null && BamiloApplication.INSTANCE.getCart() == null) {
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
        BamiloApplication.INSTANCE.sendRequest(new GetShoppingCartItemsHelper(), bundle, new IResponseCallback() {
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
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, BamiloApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        BamiloApplication.INSTANCE.sendRequest(new LoginHelper(this), bundle, new IResponseCallback() {
            @Override
            public void onRequestError(BaseResponse baseResponse) {
                Print.i(TAG, "ON REQUEST ERROR: AUTO LOGIN");
                BamiloApplication.INSTANCE.getCustomerUtils().clearCredentials();
            }

            @Override
            public void onRequestComplete(BaseResponse baseResponse) {
                Print.i(TAG, "ON REQUEST COMPLETE: AUTO LOGIN");
                // Get customer
                Customer customer = ((CheckoutStepLogin) ((NextStepStruct) baseResponse.getMetadata().getData()).getCheckoutStepObject()).getCustomer();
                // Get origin
                ContentValues credentialValues = BamiloApplication.INSTANCE.getCustomerUtils().getCredentials();

                // Global Tracker
                MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_SUCCESS,
                        Constants.LOGIN_METHOD_EMAIL, customer.getId(),
                        MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, EmailHelper.getHost(customer.getEmail()),
                                true));
                TrackerManager.trackEvent(BaseActivity.this, EventConstants.Login, authEventModel);

                EmarsysTracker.getInstance().trackEventAppLogin(Integer.parseInt(BaseActivity.this.getResources().getString(R.string.Emarsys_ContactFieldID)), BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);

                // Track
                Bundle params = new Bundle();
                params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
                params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, TrackerDelegator.IS_AUTO_LOGIN);
                params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.HOME);
                TrackerDelegator.trackLoginSuccessful(params);
                setupDrawerNavigation();
            }
        });
    }

    public void showWarning(@WarningFactory.WarningErrorType final int warningFact) {
        warningFactory.showWarning(warningFact);
    }

    public void showWarningMessage(@WarningFactory.WarningErrorType final int warningFact, final String message) {
        warningFactory.showWarning(warningFact, message);
    }

    public void hideWarningMessage() {
        warningFactory.hideWarning();
    }

    /**
     * Create a BaseActivity weak reference.
     */
    public WeakReference<BaseActivity> getWeakBaseActivity() {
        return new WeakReference<>(this);
    }

    public TabLayout getExtraTabLayout() {
        if (mExtraTabLayout == null) {
            injectExtraTabLayout();
        }
        return mExtraTabLayout;
    }

    public void setUpExtraTabLayout(ViewPager viewPager) {
        if (mExtraTabLayout == null) {
            injectExtraTabLayout();
        }
        mExtraTabLayout.setupWithViewPager(viewPager);
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(mExtraTabLayout);
    }

    public void setUpExtraTabLayout(ViewPager viewPager, float height) {
        this.mExtraTabLayoutHeight = height;
        if (mExtraTabLayout == null) {
            injectExtraTabLayout();
        }
        mExtraTabLayout.setupWithViewPager(viewPager);
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(mExtraTabLayout);
    }

    private void injectExtraTabLayout() {
        mExtraTabLayout = (TabLayout) getLayoutInflater().inflate(R.layout.extra_tab_layout, mAppBarLayout, false);
        if (mExtraTabLayoutHeight == -1) {
            mExtraTabLayoutHeight = getResources().getDimension(R.dimen.tab_layout_height);
        }
        ViewGroup tabLayoutContainer = findViewById(R.id.llExtraTabLayoutContainer);
        tabLayoutContainer.addView(mExtraTabLayout);
        setScrollContentPadding(findViewById(R.id.rlScrollableContent));
    }

    public void onFragmentViewDestroyed(Boolean isNestedFragment) {
        disableSearchBar();
        disableActionbarAutoHide();
        if (!isNestedFragment) {
            if (mExtraTabLayout != null) {
                ViewGroup tabLayoutContainer = findViewById(R.id.llExtraTabLayoutContainer);
                tabLayoutContainer.removeView(mExtraTabLayout);
                mExtraTabLayout = null;
                mExtraTabLayoutHeight = -1;
                final View scrollContainer = findViewById(R.id.rlScrollableContent);
                mAppBarLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollContainer.setPadding(scrollContainer.getPaddingLeft(),
                                toolbar.getHeight(),
                                scrollContainer.getPaddingRight(), scrollContainer.getPaddingBottom());
                    }
                });
            }
        }
    }


    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.OnViewHolderClickListener#onViewHolderClick(android.support.v7.widget.RecyclerView.Adapter, android.view.View, int)
     */
    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
        // Get suggestion
        Suggestion selectedSuggestion = ((SearchDropDownAdapter) adapter).getItem(position);
        // Get text suggestion
        String text = selectedSuggestion.getResult();
        //Save searched text
        BamiloApplication.INSTANCE.setSearchedTerm(text);
        // Collapse search view
        MenuItemCompat.collapseActionView(mSearchMenuItem);
        // Save query
        GetSearchSuggestionsHelper.saveSearchQuery(selectedSuggestion);

        switch (selectedSuggestion.getType()) {
            case Suggestion.SUGGESTION_PRODUCT:
                showSearchProduct(selectedSuggestion);
                break;
            case Suggestion.SUGGESTION_SHOP_IN_SHOP:
                showSearchShopsInShop(selectedSuggestion);
                break;
            case Suggestion.SUGGESTION_CATEGORY:
                showSearchCategory(selectedSuggestion);
                break;
            case Suggestion.SUGGESTION_OTHER:
                showSearchOther(selectedSuggestion);
                break;
        }

    }

    @Override
    public void onHeaderClick(String target, String title) {

    }

    @Override
    public void onViewHolderItemClick(View view, RecyclerView.Adapter<?> adapter, int position) {

    }

    private void setScrollContentPadding(final View scrollContainer) {
//        Assert.assertNotNull(scrollContainer);

        scrollContainer.post(new Runnable() {
            @Override
            public void run() {
                scrollContainer.setPadding(scrollContainer.getPaddingLeft(),
                        scrollContainer.getPaddingTop() + (int) mExtraTabLayoutHeight,
                        scrollContainer.getPaddingRight(), scrollContainer.getPaddingBottom());
            }
        });
    }

}
