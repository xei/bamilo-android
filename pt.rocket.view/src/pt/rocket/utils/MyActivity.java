package pt.rocket.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.IRemoteServiceCallback;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.NavigationListHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.EventType;
import pt.rocket.pojo.NavigationListComponent;

import pt.rocket.view.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.RobotoActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.internal.ActionBarSherlockNative;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;



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
 * 
 * @version 2.0
 * 
 *          2012/06/19
 * 
 */
public abstract class MyActivity extends RobotoActivity implements OnOpenedListener,
        OnClosedListener {

    private static String TAG = MyActivity.class.getSimpleName();
    
    private ShareActionProvider mShareActionProvider;

    private static RocketNavigationListGenerator navigationGenerator;
    // private int navigationComponentsHashCode;
    private View navigationContainer;
    private final NavigationAction action;

    protected View contentContainer;

    private final Set<MyMenuItem> menuItems;

    private final int activityLayoutId;

    private static View loadingBarContainer;

    private static ProgressBar loadingBarView;

    protected Dialog dialog;

    private DialogProgress progressDialog;

  

    private boolean isRegistered = false;

    private View warningView;

    private View errorView;

    private final int titleResId;

    private final int contentLayoutId;

    private Set<EventType> userEvents;

    private TextView tvActionCartCount;

    private int SHARE_REQUEST = 3;

    private Activity activity;
    
    private Context context;

    public static HashMap<String, IResponseCallback> responseCallbacks;
    private static IRemoteService mService;
    
    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     * 
     * @param userEvents
     *            TODO
     */
    public MyActivity(NavigationAction action, Set<MyMenuItem> enabledMenuItems,
            Set<EventType> contentEvents, Set<EventType> userEvents, int titleResId,
            int contentLayoutId) {
        this(R.layout.main, action, enabledMenuItems, contentEvents, userEvents, titleResId,
                contentLayoutId);

    }

    public MyActivity(int activityLayoutId, NavigationAction action,
            Set<MyMenuItem> enabledMenuItems, Set<EventType> contentEvents,
            Set<EventType> userEvents,
            int titleResId, int contentLayoutId) {
        responseCallbacks = ServiceSingleton.getInstance().getResponseCallbacks();
        this.activityLayoutId = activityLayoutId;
//        this.contentEvents = contentEvents;
        this.userEvents = userEvents;
        this.action = action != null ? action : NavigationAction.Unknown;
        menuItems = enabledMenuItems;
        this.titleResId = titleResId;
        this.contentLayoutId = contentLayoutId;
    }


    
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
     * Handles correct responses
     * 
     * @param bundle
     */
    private void handleResponse(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (responseCallbacks.containsKey(id)) {
            responseCallbacks.get(id).onRequestComplete(bundle);
        }
        responseCallbacks.remove(id);
    }

    /**
     * Handles error responses
     * 
     * @param bundle
     */
    public static void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (responseCallbacks.containsKey(id)) {
            responseCallbacks.get(id).onRequestError(bundle);
        }
        responseCallbacks.remove(id);
    }
    
    public static String sendRequest(final BaseHelper helper, final IResponseCallback responseCallback) {
        showLoadingInfo();
        Bundle bundle = helper.generateRequestBundle();
        String md5 = Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
        Log.d("TRACK", "sendRequest");
        if(responseCallbacks==null)
            responseCallbacks = ServiceSingleton.getInstance().getResponseCallbacks();
        responseCallbacks.put(md5, new IResponseCallback() {

            @Override
            public void onRequestComplete(Bundle bundle) {
                Log.d(TAG, "onRequestComplete BaseActivity");
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.checkResponseForStatus(bundle);
                if (responseCallback != null) {
                    responseCallback.onRequestComplete(formatedBundle);
                }
                hideLoadingInfo();
            }

            @Override
            public void onRequestError(Bundle bundle) {
                Log.d(TAG, "onRequestError  BaseActivity");
                // We have to parse this bundle to the final one
                Bundle formatedBundle = (Bundle) helper.parseErrorBundle(bundle);
                if (responseCallback != null) {
                    responseCallback.onRequestError(formatedBundle);
                }
                hideLoadingInfo();
            }
        });

        try {
            if(mService==null)
                mService = ServiceSingleton.getInstance().getService();
            mService.sendRequest(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return md5;
    }

    


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getTag(), "!!!!!!!!!!!!onCreate!!!!!!!!!!!!!!!!!!!!!!!!!");
//        ShopSelector.resetConfiguration(getBaseContext());
        setupActionBar();
        setupContentViews();
        setupNavigationMenu();
        isRegistered = true;
        setAppContentLayout();
        setTitle(titleResId);
        initLocalytics();
        activity = this;
        context = activity.getApplicationContext();
    }

    private void initLocalytics() {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ActivitiesWorkFlow.addStandardTransition(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        
    }

    @Override
    protected void onStop() {
        super.onStop();
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

            isRegistered = true;
        }
        mService = ServiceSingleton.getInstance().getService();
        
        try {
            mService.registerCallback(mCallback);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        CheckVersion.run(getApplicationContext());
        
        showContent();
        supportInvalidateOptionsMenu();


    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockActivity#onPause()
     */
    @Override
    public void onPause() {

        super.onPause();
        isRegistered = false;
    }

    private void setupActionBar() {
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupContentViews() {
        setContentView(activityLayoutId);
        setBehindContentView(R.layout.navigation_container);
        contentContainer = (ViewGroup) findViewById(R.id.rocket_app_content);
        loadingBarContainer = findViewById(R.id.loading_bar);
        loadingBarView = (ProgressBar) findViewById(R.id.loading_bar_view);
        warningView = findViewById(R.id.warning);
        errorView = findViewById(R.id.alert_view);
    }

    private String getTag() {
        return this.getClass().getSimpleName();
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


            sendRequest(new NavigationListHelper(), new IResponseCallback() {
                
                @Override
                public void onRequestError(Bundle bundle) {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void onRequestComplete(Bundle bundle) {
                    // TODO Auto-generated method stub
                    Log.i(TAG,"Received Nav List");                  
                    ArrayList<NavigationListComponent> navList = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
                    navigationGenerator = new RocketNavigationListGenerator(getApplicationContext(), navList);
                    attachSlidingMenu();
                }
            });
            
        
    }

    private void attachSlidingMenu() {
        navigationContainer = navigationGenerator.getNavigation(this);
        getSlidingMenu().setMenu(navigationContainer);
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
      //      ActivitiesWorkFlow.searchActivity(this);
            return false;
        } else if (itemId == R.id.menu_basket) {
     //       ActivitiesWorkFlow.shoppingCartActivity(this);
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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

                                String component = getShareProviderName(context, intent);
                                Log.i("SHARE"," component => "+component);
                                onSharingMediumSelected(component);
                                getApplicationContext().startActivity(intent);
                                return true;
                            }

                        });
                break;
            case BUY_ALL:
            default:
                menu.findItem(item.resId).setVisible(true);
                break;
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    // note: this tracks when user selects a sharing medium from the list - not if this is actually
    // sent
    protected void onSharingMediumSelected(String sharingComponent) {
    }
    
    private static String getShareProviderName( Context context, Intent intent ) {
        String name = "";       
        final PackageManager pm = context.getPackageManager();
        ActivityInfo ai = intent.resolveActivityInfo(pm, 0);

        if (ai != null) {            
                if (ai.labelRes != 0) {
                    Resources res;
                    try {
                        res = pm.getResourcesForApplication(ai.applicationInfo);
                        name = res.getString(ai.labelRes);
                    } catch (NameNotFoundException e) {                     
                        e.printStackTrace();
                        name = ai.applicationInfo.loadLabel(pm).toString();
                    }
                } else {
                    name = ai.applicationInfo.loadLabel(pm).toString();
                }
        }       
        
        return name;
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


    private final static void showLoadingInfo() {
        Log.d(TAG, "Showing loading info");
        if (loadingBarContainer != null) {
            loadingBarContainer.setVisibility(View.VISIBLE);
        } else {
            Log.w(TAG, "Did not find loading bar container, check layout!");
        }
        if (loadingBarView != null) {
            // loadingBarView.startRendering();
        }
    }

    /**
     * Hides the loading screen that appears on the front of the activity while it waits for the
     * data to arrive from the server
     */
    private static void hideLoadingInfo() {
        // Log.d(getTag(), "Hiding loading info");
        if (loadingBarView != null) {
            // loadingBarView.stopRendering();
        }
        if (loadingBarContainer != null) {
            loadingBarContainer.setVisibility(View.GONE);
        }
    }

    private static void setVisibility(View view, boolean show) {
        if (view != null) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected void showLoading() {
        setVisibility(errorView, false);
        setVisibility(contentContainer, false);
        showLoadingInfo();
    }

   

    protected void showError(OnClickListener clickListener) {
        // Log.d(getTag(), "Showing error view");
        hideLoadingInfo();
        setVisibility(contentContainer, false);
        setVisibility(errorView, true);
        errorView.setOnClickListener(clickListener);
    }

    protected final void showContentContainer() {
        Log.d(getTag(), "Showing the content container");
        hideLoadingInfo();
        dismissProgress();
        setVisibility(errorView, false);
        setVisibility(contentContainer, true);
    }

    protected final void showWarning(boolean show) {
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

    protected final void showProgress() {
        if (progressDialog != null) {
            return;
        }
        progressDialog = new DialogProgress(this);
        progressDialog.show();
    }

    protected final void dismissProgress() {
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
        Log.e("??LowMemPhotoDetails", "LOW MEM");
        System.gc();
    }

    protected void hideKeyboard() {
        // Log.d( getTag() , "hideKeyboard" );
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = getSlidingMenu();
        if (v == null)
            v = getWindow().getCurrentFocus();

        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void showKeyboard() {
        // Log.d( getTag(), "showKeyboard" );
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, 0);
        // use the above as the method below does not always work
        // imm.showSoftInput(getSlidingMenu().getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
    }

 


    /**
     * @return the action
     */
    public NavigationAction getAction() {
        return action;
    }
}
