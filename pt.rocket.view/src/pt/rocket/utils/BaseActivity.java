package pt.rocket.utils;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.IMetaData;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetShoppingCartItemsEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogProgressFragment;
import pt.rocket.view.R;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.SlideMenuFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.RobotoActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

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
public abstract class BaseActivity extends SlidingFragmentActivity implements OnOpenedListener, OnClosedListener, ResponseListener, OnFragmentActivityInteraction {

    private ShareActionProvider mShareActionProvider;
    
    // private int navigationComponentsHashCode;
    private View navigationContainer;
    
    // REMOVED FINAL ATRIBUTE
    private NavigationAction action;

    protected View contentContainer;

    private final Set<MyMenuItem> menuItems;

    private final int activityLayoutId;

    private View loadingBarContainer;

    private LoadingBarView loadingBarView;

    protected DialogFragment dialog;

    private DialogProgressFragment progressDialog;

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

    private static final String TAG = LogTagHelper.create(BaseActivity.class);

    private final Set<EventType> allHandledEvents = EnumSet.copyOf(HANDLED_EVENTS);
    private final Set<EventType> contentEvents;

    private boolean isRegistered = false;

    private View warningView;

    private View errorView;

    private final int titleResId;

    private final int contentLayoutId;

    private Set<EventType> userEvents;

    private TextView tvActionCartCount;


    
	/**
	 * Constructor used to initialize the navigation list component and the
	 * autocomplete handler
	 * @param userEvents TODO
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
        menuItems = enabledMenuItems;
        this.titleResId = titleResId;
        this.contentLayoutId = contentLayoutId;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(getTag(), "onCreate");
		ShopSelector.resetConfiguration(getBaseContext());
		EventManager.getSingleton().addResponseListener(this, allHandledEvents);
		setupActionBar();
		setupContentViews();
		setupNavigationMenu();
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
	
	@Override
	protected void onStop() {
	    super.onStop();
	    UAirship.shared().getAnalytics().activityStopped(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.d(getTag(), "onResume");
		if (!isRegistered) {
		    EventManager.getSingleton().addResponseListener(this, allHandledEvents);
	        isRegistered = true;
		}
        CheckVersion.run(getApplicationContext());
        
        // Slide Menu Fragment
        //attachSlidingMenu();
        showContent();
        
        supportInvalidateOptionsMenu();
        if (!contentEvents.contains(EventType.GET_SHOPPING_CART_ITEMS_EVENT)) {
            EventManager.getSingleton().triggerRequestEvent(
                    GetShoppingCartItemsEvent.GET_FROM_CACHE);
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
        EventManager.getSingleton().removeResponseListener(this, allHandledEvents);
        isRegistered = false;
        System.gc();
    }


    
    
    /**
     * #### ACTION BAR ####
     */
    
    public void updateActivityHeader(NavigationAction action, int titleResId){
        this.action = action != null ? action : NavigationAction.Unknown;
        setTitle(titleResId);
    }
    
    public void setupActionBar() {
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    
    private void setupContentViews() {
        System.gc();
        setContentView(activityLayoutId);
        
        // Slide Menu Fragment
        setBehindContentView(R.layout.navigation_container_fragments);

        contentContainer = (ViewGroup) findViewById(R.id.rocket_app_content);
        loadingBarContainer = findViewById(R.id.loading_bar);
        loadingBarView = (LoadingBarView) findViewById(R.id.loading_bar_view);
        warningView = findViewById(R.id.warning);
        warningView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showWarning(false);
                
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
		if (getSlidingMenu().isMenuShowing()) {
            showContent();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_right);
        }
	}

    private void setupNavigationMenu() {
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.navigation_shadow_width);
        sm.setBehindOffsetRes(R.dimen.navigation_menu_offset);
        sm.setShadowDrawable(R.drawable.gradient_sidemenu);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        sm.setBackgroundColor(getResources().getColor(R.color.sidemenu_background));
        sm.setOnOpenedListener(this);
        sm.setOnClosedListener(this);
    }


    /**
     * When a user selects an option of the menu that is on the action bar. The centralization of
     * this in this activity, prevents all the activities to have to handle this events
     * 
     * @param item
     *            The menu item that was pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Log.d(getTag(), "onOptionsItemSelected: item id = " + itemId);
        if (itemId == android.R.id.home) {
            toggle();
            return true;
        } else if (itemId == R.id.menu_search) {
            ActivitiesWorkFlow.searchActivity(this);
            return false;
		} else if (itemId == R.id.menu_basket) {
			ActivitiesWorkFlow.shoppingCartActivity(this);
			return false;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
	    System.gc();   
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		
        tvActionCartCount = (TextView) menu.findItem(R.id.menu_basket).getActionView()
                .findViewById(R.id.cart_count);
        tvActionCartCount.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(R.id.menu_basket, 0);
            }
        });
        /**
         * Setting Menu Options
         */

        for (MyMenuItem item : menuItems) {
            switch (item) {
            case SHARE:
                menu.findItem(item.resId).setVisible(true);
                menu.findItem(item.resId).setEnabled(true);
                mShareActionProvider = (ShareActionProvider) menu.findItem(
                        item.resId).getActionProvider();
                mShareActionProvider
                        .setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {

                            @Override
                            public boolean onShareTargetSelected(
                                    ShareActionProvider source, Intent intent) {
                                getApplicationContext().startActivity(intent);
                                
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

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called to update the share intent
     * 
     * @param shareIntent
     *            the intent to be stored
     */
    protected void setShareIntent(Intent shareIntent) {
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
	
	private void updateCartInfoInActionBar(final ShoppingCart cart) {
        if (tvActionCartCount == null) {
            Log.w(getTag(), "updateCartInfoInActionBar: cant find quantity in actionbar");
            return;
        }
        final String quantity = cart == null ? "?" : cart.getCartCount() > 0 ? String.valueOf(cart
                .getCartCount()) : "";
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

        CompleteProduct prod = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
        
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
        if (titleView == null)
            return;
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(title))
            titleView.setVisibility(View.GONE);
        else if (TextUtils.isEmpty(titleView.getText()))
            titleView.setVisibility(View.GONE);
        else
            titleView.setVisibility(View.VISIBLE);
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
	protected final void triggerContentEventWithNoLoading(RequestEvent event) {
        EventManager.getSingleton().triggerRequestEvent(event);
    }
	
	protected final void triggerContentEvent(RequestEvent event) {
	    showLoading();
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
	private void hideLoadingInfo() {
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
	
	public void showLoading() {
        setVisibility(errorView, false);
        setVisibility(contentContainer, false);
        showLoadingInfo();
    }

    public void showError(final RequestEvent event) {
        showError(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showWarning(false);
                triggerContentEvent(event);
            }
        });
    }

    protected void showError(OnClickListener clickListener) {
        Log.d(getTag(), "Showing error view");
        hideLoadingInfo();
        setVisibility(contentContainer, false);
        setVisibility(errorView, true);
        errorView.setOnClickListener(clickListener);
    }

    public final void showContentContainer() {
        if(processShow){
            Log.d(getTag(), "Showing the content container");
            hideLoadingInfo();
            dismissProgress();
            setVisibility(errorView, false);
            setVisibility(contentContainer, true);    
        }
    }
		

    public final void showWarning(boolean show) {
        Log.d(getTag(), "Showing warning: " + show);
        if (warningView != null) {
            warningView.setVisibility(show ? View.VISIBLE : View.GONE);
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

    @Override
    public final void handleEvent(final ResponseEvent event) {
        if (event.getSuccess()) {
            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
                boolean showContent = onSuccessEvent((ResponseResultEvent<?>) event);
                if (showContent) {
                    showContentContainer();
                }
                showWarning(event.warning != null);
            }
            handleSuccessEvent(event);

        } else {
            boolean needsErrorHandling = true;
            if (contentEvents.contains(event.type) || userEvents.contains(event.type)) {
                needsErrorHandling = !onErrorEvent(event);
            }
            if (needsErrorHandling) {
                handleErrorEvent(event);
            }
        }
    }

    /**
     * Handles a successful event and reflects necessary changes on the UI.
     * 
     * @param event
     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
     */
    @SuppressWarnings("unchecked")
    private void handleSuccessEvent(ResponseEvent event) {
        switch (event.getType()) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            updateCartInfo(((ResponseResultEvent<ShoppingCart>) event).result);
            break;
        case LOGOUT_EVENT:
            Log.d(TAG, "LOGOUT EVENT");
            finish();
            ActivitiesWorkFlow.homePageActivity(this);

            int trackRes;
            if (event.getSuccess()) {
                trackRes = R.string.glogoutsuccess;
            }
            else {
                trackRes = R.string.glogoutfailed;
            }
            AnalyticsGoogle.get().trackAccount(trackRes, null);
            break;
        }
    }

    /**
     * Handles a failed event and shows dialogs to the user.
     * 
     * @param event
     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
     */
    private void handleErrorEvent(final ResponseEvent event) {
        if (event.errorCode.isNetworkError()) {
            if (event.type == EventType.GET_SHOPPING_CART_ITEMS_EVENT) {
                updateCartInfo(null);
            }
            if (contentEvents.contains(event.type)) {
                showError(event.request);
            } else if (userEvents.contains(event.type)) {
                showContentContainer();
                dialog = DialogGenericFragment.createNoNetworkDialog(BaseActivity.this,
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                showLoadingInfo();
                                EventManager.getSingleton().triggerRequestEvent(event.request);
                                dialog.dismiss();
                            }
                        }, false);
                dialog.show(getSupportFragmentManager(), null);
            }
            return;
        } else if (event.errorCode == ErrorCode.REQUEST_ERROR) {
            Map<String, ? extends List<String>> messages = event.errorMessages;
            List<String> validateMessages = messages.get(RestConstants.JSON_VALIDATE_TAG);
            String dialogMsg = "";
            if (validateMessages == null || validateMessages.isEmpty()) {
                validateMessages = messages.get(RestConstants.JSON_ERROR_TAG);
            }
            if (validateMessages != null) {
                for (String message : validateMessages) {
                    dialogMsg += message + "\n";
                }
            } else {
                for (Entry<String, ? extends List<String>> entry : messages.entrySet()) {
                    dialogMsg += entry.getKey() + ": " + entry.getValue().get(0) + "\n";
                }
            }
            if (dialogMsg.equals("")) {
                dialogMsg = getString(R.string.validation_errortext);
            }
            showContentContainer();
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
            return;
        } else if (!event.getSuccess()) {
            showContentContainer();
            dialog = DialogGenericFragment.createServerErrorDialog(BaseActivity.this, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showLoadingInfo();
                    event.request.metaData.putBoolean( IMetaData.MD_IGNORE_CACHE, IMetaData.TRUE);
                    EventManager.getSingleton().triggerRequestEvent(event.request);
                    dialog.dismiss();
                }
            }, false);
            dialog.show(getSupportFragmentManager(), null);
            return;
        }
        
        /* TODO: finish to distinguish between errors
         * else if (event.errorCode.isServerError()) {
            dialog = DialogGeneric.createServerErrorDialog(MyActivity.this, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showLoadingInfo();
                    EventManager.getSingleton().triggerRequestEvent(event.request);
                    dialog.dismiss();
                }
            }, false);
            dialog.show();
            return;
        } else if (event.errorCode.isClientError()) {
            dialog = DialogGeneric.createClientErrorDialog( MyActivity.this, new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    showLoadingInfo();
                    EventManager.getSingleton().triggerRequestEvent(event.request);
                    dialog.dismiss();
                }
            }, false);
            dialog.show();
            return;
        }
        */
    }
	
    @Override
    public final boolean removeAfterHandlingEvent() {
        return false;
    }
    
    /**
     * Handles a successful event in the concrete activity.
     * 
     * @param event
     *            The successful event with {@link ResponseEvent#getSuccess()} == <code>true</code>
     * @return Returns whether the content container should be shown.
     */
    protected abstract boolean onSuccessEvent(ResponseResultEvent<?> event);

    /**
     * Handles a failed event in the concrete activity. Override this if the concrete activity wants
     * to handle a special error case.
     * 
     * @param event
     *            The failed event with {@link ResponseEvent#getSuccess()} == <code>false</code>
     * @return Whether the concrete activity handled the failed event and no further actions have to
     *         be made.
     */
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
    }

    /**
     * @return the action
     */
    public NavigationAction getAction() {
        return action;
    }

	    /**
     * #### FRAGMENTS ####
     */

    /**
     * This method should be implemented by fragment activity to manage the work flow for fragments.
     * Each fragment should call this method.
     * 
     * @param type
     * @param addToBackStack
     * @author sergiopereira
     */
    public abstract void onSwitchFragment(FragmentType type, Boolean addToBackStack);

    /**
     * Method used to switch fragment on UI with/without back stack support
     * 
     * @param fragment
     * @param addToBackStack
     * @author sergiopereira
     */
    protected void fragmentManagerTransition(int container, Fragment fragment, Boolean addToBackStack, Boolean animated) {
        fragment.setRetainInstance(true);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Animations
        if(animated)
            fragmentTransaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out, R.anim.pop_in, R.anim.pop_out);
//          fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment);
        // BackStack
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        // Commit
        fragmentTransaction.commit();
    }
   
    /**
     * Method used to perform a back stack using fragments
     * @author sergiopereira
     */
    protected void fragmentManagerBackPressed(){
        int backStackSize = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "BackStack SIZE: " + backStackSize);
        if (backStackSize == 1)
            finish();
        else
            getSupportFragmentManager().popBackStack();
    }
    
    public void onFragmentSelected(FragmentType fragmentIdentifier){}
    
    public void onFragmentElementSelected(int position){}
    
    public void sendClickListenerToActivity(OnClickListener clickListener){}
    
    public void sendValuesToActivity(int identifier, Object values){}



}
