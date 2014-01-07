package pt.rocket.view;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.framework.utils.Utils;
import pt.rocket.framework.utils.WindowHelper;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.EventType;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.ServiceSingleton;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.CustomToastView;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogProgressFragment;
import pt.rocket.view.fragments.SlideMenuFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
<<<<<<< .mine
import android.os.RemoteException;
=======
import android.os.Handler;
>>>>>>> .r448799
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.internal.ActionBarSherlockNative;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.bugsense.trace.BugSenseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
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
public abstract class BaseActivity extends SlidingFragmentActivity implements OnOpenedListener, OnClosedListener {

    private ShareActionProvider mShareActionProvider;
    
    // private int navigationComponentsHashCode;
    private View navigationContainer;
    
    // REMOVED FINAL ATRIBUTE
    private NavigationAction action;

    protected View contentContainer;

    private Set<MyMenuItem> menuItems;

    private final int activityLayoutId;

    private View loadingBarContainer;

    private LoadingBarView loadingBarView;

    protected DialogFragment dialog;

    private DialogProgressFragment progressDialog;
    
    private Activity activity;
    
    private static final int TOAST_LENGTH_SHORT = 2000; // 2 seconds
    
    private boolean backPressedOnce = false;

    private IRemoteService mService;
    /**
     * The md5 registry
     */
    public HashMap<String, IResponseCallback> responseCallbacks;

    /**
     * Use this variable to have a more precise control on when to show the content container.
     */
    private boolean processShow = true;
    
    private static final Set<EventType> HANDLED_EVENTS = EnumSet.of(
            EventType.GET_SHOPPING_CART_ITEMS_EVENT,
            EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT,
            EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT,
            EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
            EventType.INITIALIZE,
            EventType.LOGOUT_EVENT);

    private static final String TAG = LogTagHelper.create(BaseActivity.class) + "Fragment";

    private final Set<EventType> allHandledEvents = EnumSet.copyOf(HANDLED_EVENTS);
    private final Set<EventType> contentEvents;

    private boolean isRegistered = false;

    private View warningView;
    private View warningVariationView;

    private View errorView;

    private final int titleResId;

    private final int contentLayoutId;

    private Set<EventType> userEvents;

    private TextView tvActionCartCount;

    private EditText searchComponent;

    private View searchOverlay;

	private FragmentController	fragmentController;
	
	private boolean initialCountry = false;
	

    
	/**
	 * Constructor used to initialize the navigation list component and the
	 * autocomplete handler
	 * @param userEvents
	 */
    public BaseActivity(NavigationAction action, Set<MyMenuItem> enabledMenuItems,
            Set<EventType> contentEvents, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        this(R.layout.main, action, enabledMenuItems, contentEvents, userEvents, titleResId, contentLayoutId);
        
    }

    public BaseActivity(int activityLayoutId, NavigationAction action,
            Set<MyMenuItem> enabledMenuItems, Set<EventType> contentEvents, Set<EventType> userEvents,
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
		responseCallbacks = new HashMap<String, IResponseCallback>();
		Log.d(TAG, "ON CREATE");
		
        // Validate if is phone and force orientaion
        setOrientationForHandsetDevices();
		
        // Get fragment controller
        fragmentController = FragmentController.getInstance();

		ShopSelector.resetConfiguration(getBaseContext());
		//OLD FRAMEWORK
//		EventManager.getSingleton().addResponseListener(this, allHandledEvents);
		setupActionBar();
		setupContentViews();
		
		if(getIntent().getExtras() != null){
		    initialCountry = getIntent().getExtras().getBoolean(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false);
		}
		// Set sliding menu
		setupNavigationMenu(initialCountry);
		
		isRegistered = true;
		setAppContentLayout();
        setTitle(titleResId);
        BugSenseHandler.leaveBreadcrumb(getTag() + " _onCreate");
	}
	
	@Override
	protected void onNewIntent( Intent intent ) {
	    super.onNewIntent(intent);
        BugSenseHandler.leaveBreadcrumb(getTag() + " _onNewIntent");
	    ActivitiesWorkFlow.addStandardTransition(this);
	}
	
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
		
		/**
		 * Register service callback
		 */
        mService = ServiceSingleton.getInstance().getService();
        
        try {
            mService.registerCallback(mCallback);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
		if (!isRegistered) {
		    // OLD FRAMEWORK
//		    EventManager.getSingleton().addResponseListener(this, allHandledEvents);
	        isRegistered = true;
		}
		
		CheckVersion.run(getApplicationContext());
		
		if(getIntent().getExtras() != null){
            initialCountry = getIntent().getExtras().getBoolean(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false);
        }
		
        // Validate if is in landscape and tablet and forcing menu
        if(isTabletInLandscape(this) && !initialCountry)
             showMenu();
        else
            showContent();
        
        if (!contentEvents.contains(EventType.GET_SHOPPING_CART_ITEMS_EVENT)) {
            EventManager.getSingleton().triggerRequestEvent(GetShoppingCartItemsEvent.GET_FROM_CACHE);
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
        // OLD FRAMEWORK
//        EventManager.getSingleton().removeResponseListener(this, allHandledEvents);
        isRegistered = false;
    }


    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ON STOP");
        UAirship.shared().getAnalytics().activityStopped(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * #### ACTION BAR ####
     */
    
    /**
     * Method used to update the sliding menu and items on action bar.
     * Called from BaseFragment
     * @param enabledMenuItems
     * @param action
     * @param titleResId
     * @author sergiopereira
     */
	
	public void updateBaseComponents(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int titleResId){
        // Update options menu and search bar
        menuItems = enabledMenuItems;
        if(action != NavigationAction.Country)
            findViewById(R.id.rocket_app_header_search).setVisibility(View.GONE);
        hideKeyboard();
        invalidateOptionsMenu();
        // Update the sliding menu
        this.action = action != null ? action : NavigationAction.Unknown;
        updateSlidingMenu();
        // Update the title of fragment
        if(titleResId == 0){
            hideTitle();
         
            findViewById(R.id.totalProducts).setVisibility(View.GONE);
            }
        else setTitle(titleResId);
    }
	
	public void updateActionForCountry(NavigationAction action){
	    this.action = action != null ? action : NavigationAction.Unknown;
	    updateSlidingMenu();
	}
    
    public void setupActionBar() {
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    
    private void setupContentViews() {
        setContentView(activityLayoutId);
        contentContainer = (ViewGroup) findViewById(R.id.rocket_app_content);
        loadingBarContainer = findViewById(R.id.loading_bar);
        loadingBarView = (LoadingBarView) findViewById(R.id.loading_bar_view);
        warningView = findViewById(R.id.warning);
        warningVariationView  = findViewById(R.id.warning_variations);
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
        errorView = findViewById(R.id.alert_view);
    }

    private String getTag() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean result = super.dispatchKeyEvent(event);
        if ( result ) {
            return result;   
        }
        
        if ( event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN ) {
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
		Log.i(getTag(), "onBackPressed");
		if (getSlidingMenu().isMenuShowing() && getSlidingMenu().isSlidingEnabled() && !isTabletInLandscape(this)) {
            showContent();
        } else {
          super.onBackPressed();
        }
	}

    /**
     * ############### SLIDE MENU #################
     */
	
	/**
	 * Method used to set the sliding menu with support for tablet
	 * @author sergiopereira
	 */
    private void setupNavigationMenu(boolean onChangeCountry) {
        // Set Behind Content View
        setBehindContentView(R.layout.navigation_container_fragments);
        // Customize sliding menu
        SlidingMenu sm = getSlidingMenu();
        // Set the SlidingMenu width with a percentage of the display width
        sm.setBehindWidth((int) (WindowHelper.getWidth(getApplicationContext()) * getResources().getFraction(R.dimen.navigation_menu_width, 1, 1)));
        sm.setShadowWidthRes(R.dimen.navigation_shadow_width);
        sm.setShadowDrawable(R.drawable.gradient_sidemenu);
        sm.setFadeDegree(0.35f);
        sm.setBackgroundColor(getResources().getColor(R.color.sidemenu_background));
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        Log.i(TAG, "codeW : "+onChangeCountry);
        // Validate current orientation and device
        if(isTabletInLandscape(this) && !onChangeCountry) {
            // Landscape mode
            slideMenuInLandscapeMode(sm);
        }else {
            // Portrait mode
            slideMenuInPortraitMode(sm);
        }
    }
    
    /**
     * Customize slide menu and action bar for landscape in tablet
     * @param sm
     * @author sergiopereira
     */
    private void slideMenuInLandscapeMode(SlidingMenu sm){
        Log.i(TAG, "SET SLIDE MENU: LANDSCAPE MODE");
        sm.setSlidingEnabled(false);
        sm.setOnOpenedListener(this);
        sm.setOnClosedListener(this);
        setSlidingActionBarEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);       
        getSupportActionBar().setLogo(R.drawable.logo_ic);
        
        
        // Get the width of main content
        int mainContentWidth = (int) (WindowHelper.getWidth(getApplicationContext()) * getResources().getFraction(R.dimen.navigation_menu_offset,1,1));
        findViewById(R.id.main_layout).getLayoutParams().width = mainContentWidth;
        
        // Show Menu
        sm.postDelayed(new Runnable() {
            @Override
            public void run() {
              showMenu();
            }
        }, 0);
    }
    
    /**
     * Customize slide menu and action bar for portrait
     * The same for phone or tablet
     * @param sm
     * @author sergiopereira
     */
    private void slideMenuInPortraitMode(SlidingMenu sm){
        Log.i(TAG, "SET SLIDE MENU: PORTRAIT MODE");
        // Set action bar
        setActionBarInPortraitMode();
        // Set listeners
        sm.setOnOpenedListener(this);
        sm.setOnClosedListener(this);
        // Show content
        sm.postDelayed(new Runnable() {
            @Override
            public void run() {
                showContent();
            }
        }, 0);
    }
    
    
    /**
     * Update the sliding menu
     */
    public void updateSlidingMenu(){
        SlideMenuFragment slideMenuFragment = (SlideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_slide_menu);
        if(slideMenuFragment != null)
            slideMenuFragment.onUpdate();
    }
    
    /**
     * Update the sliding menu
     */
    public void updateSlidingMenuCompletly(){
        SlideMenuFragment slideMenuFragment = (SlideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_slide_menu);
        if(slideMenuFragment != null)
            slideMenuFragment.onUpdate();
    }
    
    /**
     * Set the action bar for portrait orientation
     * Action bar with logo and custom view
     */
    private void setActionBarInPortraitMode(){
        // Set logo
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Set custom view
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_logo_layout);        
        getSupportActionBar().getCustomView().findViewById(R.id.ic_logo).setOnClickListener(onActionBarClickListener);
    }
    
    /**
     * Listener used for custom view on action bar
     */
    OnClickListener onActionBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!initialCountry){
                onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
                
        }
    };
    
    /**
     * ############### ORIENTATION #################
     */
    
    public void setOrientationForHandsetDevices(){
        // Validate if is phone and force portrait orientaion
        if(!getResources().getBoolean(R.bool.isTablet)) {
            Log.i(TAG, "IS PHONE: FORCE PORTRAIT ORIENTATION");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    
    /**
     * Verifies if the current screen orientation is Landscape
     * @return true if yes, false otherwise
     */
    public static boolean isTabletInLandscape(Context context){
        if (context.getResources().getBoolean(R.bool.isTablet) && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        return false;
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
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Log.d(getTag(), "onOptionsItemSelected: item id = " + itemId);
        if (itemId == android.R.id.home) {
            toggle();
            return true;
        } else if (itemId == R.id.menu_search) {
            onSwitchFragment(FragmentType.SEARCH, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            return false;
		} else if (itemId == R.id.menu_basket) {
			onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
			return false;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
    
    /*
     * (non-Javadoc)
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onCreateOptionsMenu(android.view.Menu)
     */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
	    Log.d(TAG, "ON OPTIONS MENU: CREATE");
	    getSupportMenuInflater().inflate(R.menu.main_menu, menu);
	    
        /**
         * Setting Menu Options
         */
        for (MyMenuItem item : menuItems) {
            switch (item) {
            case SEARCH_BAR:
                Log.i(TAG, "ON OPTIONS MENU: CREATE SEARCH BAR");
                setSearchBar(menu);
                break;
            case SHARE:
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
                break;
            case BUY_ALL:
            default:
                menu.findItem(item.resId).setVisible(true);
                break;
            }
        }
        
        tvActionCartCount = (TextView) menu.findItem(R.id.menu_basket).getActionView().findViewById(R.id.cart_count);
        tvActionCartCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(R.id.menu_basket, 0);
            }
        });
        
        return super.onCreateOptionsMenu(menu);
    }
	
    /**
     * ############### SEARCH BAR #################
     */
	
	/**
	 * Method used to set the search bar in/below the Action bar. 
	 * @param menu
	 * @author sergiopereira
	 */
	private void setSearchBar(Menu menu) {
	    // Validate the Sliding
        if(!isTabletInLandscape(this)) {
            // Show search below the action bar
            findViewById(R.id.rocket_app_header_search).setVisibility(View.VISIBLE);
            // Show the normal search
            searchComponent = (EditText) findViewById(R.id.search_component);
            searchOverlay = findViewById(R.id.search_overlay);
        } else {
            // Set search on action bar
            menu.findItem(R.id.menu_search_view).setVisible(true);
            // Set search 
            searchComponent = (EditText) menu.findItem(R.id.menu_search_view).getActionView().findViewById(R.id.search_component);
            searchOverlay = menu.findItem(R.id.menu_search_view).getActionView().findViewById(R.id.search_overlay);

            // Get the width of main content
            int mainContentWidth = (int) (WindowHelper.getWidth(getApplicationContext()) * getResources().getFraction(R.dimen.navigation_menu_offset,1,1));            
            // Get cart item and forcing measure
            View cartCount = menu.findItem(R.id.menu_basket).getActionView().findViewById(R.id.cart_count_layout);
            cartCount.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int cartCountWidth = cartCount.getMeasuredWidth() + cartCount.getPaddingRight();
            Log.d(TAG, "CART WIDTH SIZE: " + cartCountWidth);
            // Calculate the search width
            int searchComponentWidth = mainContentWidth - cartCountWidth;
            Log.d(TAG, "SEARCH WIDTH SIZE: " + searchComponentWidth);
            
            // Set width on search component
            searchComponent.getLayoutParams().width = searchComponentWidth;
            searchOverlay.getLayoutParams().width = searchComponentWidth;
        }
        // Set search 
        setSearchForwardBehaviour(searchComponent, searchOverlay);

        
        // XXX
//        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        int width;
//        int height;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//            display.getSize(size);
//            width = size.x;
//            height = size.y;
//       } else {
//           width = display.getWidth();
//           height = display.getHeight();
//       }
//       
//        getSlidingMenu().getWidth();
//        findViewById(R.id.abs__home).getLayoutParams().width = getSlidingMenu().getWidth();
//        findViewById(R.id.abs__home).getLayoutParams().width = (int) (width - getResources().getDimension(R.dimen.navigation_menu_offset));
        
	}
	
    /**
     * Set the forward behaviour on search bar
     * @param autoComplete
     * @param searchOverlay
     * @author sergiopereira
     */
    private void setSearchForwardBehaviour(EditText autoComplete, View searchOverlay){   
        Log.d(TAG, "SEARCH MODE: FORWARD BEHAVIOUR");
        autoComplete.setEnabled(false);
        autoComplete.setFocusable(false);
        autoComplete.setFocusableInTouchMode(false);
        searchOverlay.setVisibility(View.VISIBLE);
        searchOverlay.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSwitchFragment(FragmentType.SEARCH, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    }
                });
    }
    
    /**
     * Set the normal behaviour on search bar
     */
    public void setSearchNormalBehaviour(){
        Log.d(TAG, "SEARCH MODE: NORMAL BEHAVIOUR");
        if(searchComponent == null) {
            Log.w(TAG, "SEARCH COMPONENT IS NULL");
            return;
        }
        searchComponent.setEnabled(true);
        searchComponent.setFocusable(true);
        searchComponent.setFocusableInTouchMode(true);
        searchOverlay.setVisibility(View.GONE);
        searchOverlay.setOnClickListener(null);
    }
    
    public EditText getSearchComponent() {
        return searchComponent;
    }
    
    public void cleanSearchConponent() {
        if(searchComponent != null)
            searchComponent.setText("");
    }
    
    /**
     * #########################################
     */

    /**
     * Called to update the share intent
     * 
     * @param shareIntent
     *            the intent to be stored
     */
    public void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareHistoryFileName(null);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

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
    public void updateCartInfo(ShoppingCart cart) {
        if (cart != null) {
            Log.d(getTag(),
                    "updateCartInfo value = " + cart.getCartValue() + " quantity = "
                            + cart.getCartCount());
        }
        updateCartInfoInActionBar(cart);
        updateCartInfoInNavigation(cart);
    }
	
	public void updateCartInfoInActionBar(final ShoppingCart cart) {
        if (tvActionCartCount == null) {
            Log.w(getTag(), "updateCartInfoInActionBar: cant find quantity in actionbar");
            return;
        }
        
        final String quantity = cart == null ? "?" : cart.getCartCount() > 0 ? String.valueOf(cart.getCartCount()) : "";
        tvActionCartCount.post(new Runnable() {
            @Override
            public void run() {
                tvActionCartCount.setText(quantity);
            }
        });
        
    }

    private void updateCartInfoInNavigation(final ShoppingCart cart) {
        Log.d(getTag(), "updateCartInfoInNavigation");
        SlideMenuFragment slideMenu = (SlideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_slide_menu);
        if(slideMenu == null) {
            Log.w(getTag(), "updateCartInfoInNavigation: navigation container empty - doing nothing");
            return;
        }else{
            slideMenu.updateCartInfo(cart);
        }
    }

    /**
     * Create the share intent to be used to store the needed information
     * 
     * @return The created intent
     */
    public Intent createShareIntent() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));

        CompleteProduct prod = ServiceManager.SERVICES.get(ProductHelper.class).getCurrentProduct();
        
        if (null != prod) {
            //For tracking when sharing
            sharingIntent.putExtra(getString(R.string.mixprop_sharelocation), getString(R.string.mixprop_sharelocationproduct));
            sharingIntent.putExtra(getString(R.string.mixprop_sharecategory), prod.getCategories().size() > 0 ? prod.getCategories().get(0) : "");
            sharingIntent.putExtra(getString(R.string.mixprop_sharename), prod.getName());
            sharingIntent.putExtra(getString(R.string.mixprop_sharebrand), prod.getBrand());
            sharingIntent.putExtra(getString(R.string.mixprop_shareprice), prod.getPrice());
            
            String msg = getString(R.string.share_checkout_this_product) + "\n" + prod.getUrl().replace("/mobapi", "");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        }

        return sharingIntent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#setTitle(java.lang.CharSequence)
     */
    @Override
    public void setTitle(CharSequence title) {
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
        RelativeLayout header_title = (RelativeLayout) findViewById(R.id.header_title);
        subtitleView.setVisibility(View.GONE);
        if (header_title == null)
            return;
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            header_title.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(title)){
            header_title.setVisibility(View.GONE);
        }
    }
    
    /**
     * Method used to set the number of products
     * @param title
     * @param subtitle
     */
    public void setTitleAndSubTitle(CharSequence title,CharSequence subtitle) {
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView subtitleView = (TextView) findViewById(R.id.totalProducts);
        RelativeLayout header_title = (RelativeLayout) findViewById(R.id.header_title);
        
        if (titleView == null)
            return;
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(subtitle)) {
            titleView.setText(title);
            subtitleView.setText(subtitle);
            
            header_title.setVisibility(View.VISIBLE);
            subtitleView.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(title)){
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
	 * @param event
	 */
	public final void triggerContentEventWithNoLoading(RequestEvent event) {
        EventManager.getSingleton().triggerRequestEvent(event);
    }
	
	protected final void triggerContentEvent(RequestEvent event) {
	    showLoading(false);
	    EventManager.getSingleton().triggerRequestEvent(event);
	}
		
	protected final void triggerContentEvent(EventType type) {
	    triggerContentEvent(new RequestEvent(type));
	}
	
	protected final void triggerContentEventProgress(RequestEvent event) {
	    showProgress();
	    EventManager.getSingleton().triggerRequestEvent(event);
	}
	
	public final void showLoadingInfo() {
	    Log.d(getTag(), "Showing loading info");
		if (loadingBarContainer != null) {
			loadingBarContainer.setVisibility(View.VISIBLE);
		} else {
		    Log.w(getTag(), "Did not find loading bar container, check layout!");
		}
		if (loadingBarView != null) {
			loadingBarView.startRendering();
		}
	}
	
	/**
	 * Hides the loading screen that appears on the front of the activity while
	 * it waits for the data to arrive from the server
	 */
	public void hideLoadingInfo() {
	    Log.d(getTag(), "Hiding loading info");
		if (loadingBarView != null) {
			loadingBarView.stopRendering();
		}
		if (loadingBarContainer != null) {
			loadingBarContainer.setVisibility(View.GONE);
		}
	}
	
	private static void setVisibility(View view, boolean show) {
	    if(view != null) {
	        view.setVisibility(show ? View.VISIBLE : View.GONE);
	    }
	}
	
    public void showError(final RequestEvent event) {
        showError(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showWarning(false);
                triggerContentEvent(event);
            }
        }, false);
    }

    public void showLoading(boolean fromCheckout) {
        setVisibility(errorView, false);
        if(!fromCheckout){
            setVisibility(contentContainer, false);
        }
            
        showLoadingInfo();
    }
    
    protected void showError(OnClickListener clickListener, boolean fromCheckout) {
        Log.d(getTag(), "Showing error view");
        hideLoadingInfo();
        if(!fromCheckout){
            setVisibility(contentContainer, false);
        }
        setVisibility(errorView, true);
        errorView.setOnClickListener(clickListener);
    }

    public final void showContentContainer(boolean fromCheckout) {
        if(processShow){
            Log.d(getTag(), "Showing the content container");
            hideLoadingInfo();
            dismissProgress();
            setVisibility(errorView, false);
            if(!fromCheckout){
                setVisibility(contentContainer, true);
            }
        }
    }
		

    public final void showWarning(boolean show) {
        Log.d(getTag(), "Showing warning: " + show);
        if (warningView != null) {
            warningView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showWarningVariation(boolean show) {
        if (warningVariationView != null) {

            warningVariationView.setVisibility(show ? View.VISIBLE : View.GONE);
          
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

    public final void showProgress() {
        if (progressDialog != null) {
            return;
        }
        progressDialog = DialogProgressFragment.newInstance();
        progressDialog.show(getSupportFragmentManager(), null);
    }

    public final void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * This function round a number to a specific precision using a predefine rounding mode
     * 
     * @param unrounded
     *            The value to round
     * @param precision
     *            The number of decimal places we want
     * @param roundingMode
     *            The type of rounding we want done. Please refer to the java.math.BigDecimal class
     *            for more info
     * @return The number rounded according to the specifications we established
     */
    public static double roundValue(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    @Override
    public void onOpened() {
        Log.d(getTag(), "onOpened");
        if(!isTabletInLandscape(this))
            hideKeyboard();
        AnalyticsGoogle.get().trackPage(R.string.gnavigation);
    }

    @Override
    public void onClosed() {
        Log.d(getTag(), "onClosed");
    }

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

                ((ViewGroup) view).removeAllViews();
            }
        } catch (RuntimeException e) {
            Log.w(getTag(), "" + e);
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(getTag(), "LOW MEM");
        ImageLoader.getInstance().clearMemoryCache();
        System.gc();
    }    
    
    public void hideKeyboard() {
        // Log.d( getTag() , "hideKeyboard" );
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = getSlidingMenu();
        if (v == null)
            v = getWindow().getCurrentFocus();

        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard() {
        // Log.d( getTag(), "showKeyboard" );
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0);
        // use the above as the method below does not always work
        // imm.showSoftInput(getSlidingMenu().getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
    }
    
    /**
     * Use this variable to have a more precise control on when to show the content container.
     * The content will show by default after finishing the event request.
     * 
     * @param b
     */
    public void setProcessShow(boolean b){
        processShow = b;
    }
    
    public void finishFromAdapter(){
        finish();
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


    /**
     * Method called then the activity is connected to the service
     */
    protected void onServiceActivation() {

    }
    
    /**
     * Triggers the request for a new api call
     * 
     * @param helper
     *            of the api call
     * @param responseCallback
     * @return the md5 of the reponse
     */
    public String sendRequest(final BaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        Bundle bundle = helper.generateRequestBundle(args);
        String md5 = Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
        Log.d("TRACK", "sendRequest");
        responseCallbacks.put(md5, new IResponseCallback() {

            @Override
            public void onRequestComplete(Bundle bundle) {
                Log.d("TRACK", "onRequestComplete BaseActivity");
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.checkResponseForStatus(bundle);
                if (responseCallback != null) {
                    responseCallback.onRequestComplete(formatedBundle);
                }
            }

            @Override
            public void onRequestError(Bundle bundle) {
                Log.d("TRACK", "onRequestError  BaseActivity");
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.parseErrorBundle(bundle);
                if (responseCallback != null) {
                    responseCallback.onRequestError(formatedBundle);
                }
            }
        });

        try {
            mService.sendRequest(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return md5;
    }
    /**
     * ADAPTNEWFRAMEWORK
     */
//    public final void handleEvent(final ResponseEvent event) {
//        if (event.getSuccess()) {
//            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
//                boolean showContent = onSuccessEvent((ResponseResultEvent<?>) event);
//                if (showContent) {
//                    showContentContainer(false);
//                }
//                showWarning(event.warning != null);
//            }
//            handleSuccessEvent(event);
//
//        } else {
//            boolean needsErrorHandling = true;
//            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
//                needsErrorHandling = !onErrorEvent(event);
//            }
//            if (needsErrorHandling) {
//                handleErrorEvent(event);
//            }
//        }
//    }
    /**
     * ADAPTNEWFRAMEWORK
     */
//    /**
//     * Handles a successful event and reflects necessary changes on the UI.
//     * 
//     * @param event
//     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
//     */
//    @SuppressWarnings("unchecked")
//    private void handleSuccessEvent(ResponseEvent event) {
//        switch (event.getType()) {
//        case GET_SHOPPING_CART_ITEMS_EVENT:
//        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
//        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
//        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
//            updateCartInfo(((ResponseResultEvent<ShoppingCart>) event).result);
//            break;
//        case LOGOUT_EVENT:
//            Log.i(TAG, "LOGOUT EVENT");
//            onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//            updateSlidingMenu();
//            int trackRes;
//            if (event.getSuccess()) {
//                trackRes = R.string.glogoutsuccess;
//            }
//            else {
//                trackRes = R.string.glogoutfailed;
//            }
//            AnalyticsGoogle.get().trackAccount(trackRes, null);
//            break;
//        }
//    }
    /**
     * ADAPTNEWFRAMEWORK
     */
//    /**
//     * Handles a failed event and shows dialogs to the user.
//     * 
//     * @param event
//     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
//     */
//    public void handleErrorEvent(final ResponseEvent event) {
//        if (event.errorCode.isNetworkError()) {
//            if (event.type == EventType.GET_SHOPPING_CART_ITEMS_EVENT) {
//                updateCartInfo(null);
//            }
//            if (contentEvents.contains(event.type)) {
//                showError(event.request);
//            } else if (userEvents.contains(event.type)) {
//                showContentContainer(false);
//                dialog = DialogGenericFragment.createNoNetworkDialog(BaseActivity.this,
//                        new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                showLoadingInfo();
//                                EventManager.getSingleton().triggerRequestEvent(event.request);
//                                dialog.dismiss();
//                            }
//                        }, false);
//                dialog.show(getSupportFragmentManager(), null);
//            }
//            return;
//        } else if (event.type == EventType.GET_PROMOTIONS) {
//            /**
//             * No promotions available!
//             * Ignore error
//             */
//        } else if (event.errorCode == ErrorCode.REQUEST_ERROR) {
//            Map<String, ? extends List<String>> messages = event.errorMessages;
//            List<String> validateMessages = messages.get(RestConstants.JSON_VALIDATE_TAG);
//            String dialogMsg = "";
//            if (validateMessages == null || validateMessages.isEmpty()) {
//                validateMessages = messages.get(RestConstants.JSON_ERROR_TAG);
//            }
//            if (validateMessages != null) {
//                for (String message : validateMessages) {
//                    dialogMsg += message + "\n";
//                }
//            } else {
//                for (Entry<String, ? extends List<String>> entry : messages.entrySet()) {
//                    dialogMsg += entry.getKey() + ": " + entry.getValue().get(0) + "\n";
//                }
//            }
//            if (dialogMsg.equals("")) {
//                dialogMsg = getString(R.string.validation_errortext);
//            }
//            showContentContainer(false);
//            dialog = DialogGenericFragment.newInstance(
//                    true, true, false, getString(R.string.validation_title),
//                    dialogMsg, getResources().getString(R.string.ok_label), "",
//                    new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            int id = v.getId();
//                            if (id == R.id.button1) {
//                                dialog.dismiss();
//                            }
//
//                        }
//
//                    });
//
//            dialog.show(getSupportFragmentManager(), null);
//            return;
//        } else if (!event.getSuccess()) {
//            showContentContainer(false);
//            dialog = DialogGenericFragment.createServerErrorDialog(BaseActivity.this, new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    showLoadingInfo();
//                    event.request.metaData.putBoolean( IMetaData.MD_IGNORE_CACHE, IMetaData.TRUE);
//                    EventManager.getSingleton().triggerRequestEvent(event.request);
//                    dialog.dismiss();
//                }
//            }, false);
//            dialog.show(getSupportFragmentManager(), null);
//            return;
//        }
//        
//        /* TODO: finish to distinguish between errors
//         * else if (event.errorCode.isServerError()) {
//            dialog = DialogGeneric.createServerErrorDialog(MyActivity.this, new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    showLoadingInfo();
//                    EventManager.getSingleton().triggerRequestEvent(event.request);
//                    dialog.dismiss();
//                }
//            }, false);
//            dialog.show();
//            return;
//        } else if (event.errorCode.isClientError()) {
//            dialog = DialogGeneric.createClientErrorDialog( MyActivity.this, new OnClickListener() {
//                
//                @Override
//                public void onClick(View v) {
//                    showLoadingInfo();
//                    EventManager.getSingleton().triggerRequestEvent(event.request);
//                    dialog.dismiss();
//                }
//            }, false);
//            dialog.show();
//            return;
//        }
//        */
//    }
//	
//    @Override
//    public final boolean removeAfterHandlingEvent() {
//        return false;
//    }
//    
//    /**
//     * Handles a successful event in the concrete activity.
//     * 
//     * @param event
//     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
//     * @return Returns whether the content container should be shown.
//     */
//    protected abstract boolean onSuccessEvent(ResponseResultEvent<?> event);
//
//    /**
//     * Handles a failed event in the concrete activity. Override this if the concrete activity wants
//     * to handle a special error case.
//     * 
//     * @param event
//     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
//     * @return Whether the concrete activity handled the failed event and no further actions have to
//     *         be made.
//     */
//    protected boolean onErrorEvent(ResponseEvent event) {
//        return false;
//    }

    /**
     * @return the action
     */
    public NavigationAction getAction() {
        return action;
    }
    
    /**
     * Set action
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
    public void fragmentManagerTransition(int container, Fragment fragment, String tag, Boolean addToBackStack) {
        showContentContainer(false);
        fragmentController.startTransition(this, container, fragment, tag, addToBackStack);
    }
    
    /**
     * Method used to perform a back stack using fragments
     * @author sergiopereira
     */
    public void fragmentManagerBackPressed(){
        showContentContainer(false);
        fragmentController.fragmentBackPressed(this);
    }
    
    /**
     * Pop all back stack
     * @param tag
     * @author sergiopereira
     */
    protected void popAllBackStack(String tag){
        fragmentController.popAllBackStack(this, tag);
    }

    /**
     * Pop back stack until tag
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
    
    public void onFragmentSelected(FragmentType fragmentIdentifier){}
    
    public void onFragmentElementSelected(int position){}
    
    public void sendClickListenerToActivity(OnClickListener clickListener){}
    
    public void sendValuesToActivity(int identifier, Object values){}

// OLD FRAMEWORK
//    @Override
//    public String getMD5Hash() {
//        // TODO Auto-generated method stub
//        return null;
//    }
    
    /**
     * Confirm backPress to exit application
     *
     */
    public Boolean exitApplication(final FragmentController fragC){
        
     
        dialog = DialogGenericFragment.newInstance(false, false, true,
                null, // no title
                getString(R.string.logout_text_question), getString(R.string.no_label),
                getString(R.string.yes_label), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        int id = v.getId();
                        if (id == R.id.button1) {
                            //fragC.popLastEntry(); 
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
     * @author sergiopereira
     * @see <a href="http://stackoverflow.com/questions/7965135/what-is-the-duration-of-a-toast-length-long-and-length-short">Toast duration</a>
     * <br>Toast.LENGTH_LONG is 3500 seconds.
     * <br>Toast.LENGTH_SHORT is 2000 seconds.
     */
    public void doubleBackPressToExit(){
        Log.d(TAG, "DOUBLE BACK PRESSED TO EXIT: " + backPressedOnce);
        // If was pressed once
        if (backPressedOnce) {
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
    
}
