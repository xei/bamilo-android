package com.mobile.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.a4s.sdk.plugins.annotations.UseA4S;
import com.mobile.app.DebugActivity;
import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.libraries.emarsys.EmarsysMobileEngage;
import com.mobile.libraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.security.ObscuredSharedPreferences;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.emarsys.EmarsysTracker;
import com.mobile.utils.pushwoosh.PushWooshTracker;
import com.mobile.utils.pushwoosh.PushwooshCounter;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.CampaignsFragment;
import com.mobile.view.fragments.CatalogFragment;
import com.mobile.view.fragments.CheckoutAddressesFragment;
import com.mobile.view.fragments.CheckoutConfirmationFragment;
import com.mobile.view.fragments.CheckoutCreateAddressFragment;
import com.mobile.view.fragments.CheckoutEditAddressFragment;
import com.mobile.view.fragments.CheckoutExternalPaymentFragment;
import com.mobile.view.fragments.CheckoutFinishFragment;
import com.mobile.view.fragments.CheckoutPaymentMethodsFragment;
import com.mobile.view.fragments.CheckoutShippingMethodsFragment;
import com.mobile.view.fragments.CheckoutThanksFragment;
import com.mobile.view.fragments.ChooseCountryFragment;
import com.mobile.view.fragments.ComboFragment;
import com.mobile.view.fragments.FilterMainFragment;
import com.mobile.view.fragments.HomePageFragment;
import com.mobile.view.fragments.InnerShopFragment;
import com.mobile.view.fragments.MyAccountAboutFragment;
import com.mobile.view.fragments.MyAccountCreateAddressFragment;
import com.mobile.view.fragments.MyAccountEditAddressFragment;
import com.mobile.view.fragments.MyAccountFragment;
import com.mobile.view.fragments.MyAccountNewslettersFragment;
import com.mobile.view.fragments.MyAccountUserDataFragment;
import com.mobile.view.fragments.ProductDetailsFragment;
import com.mobile.view.fragments.ProductDetailsInfoFragment;
import com.mobile.view.fragments.ProductImageGalleryFragment;
import com.mobile.view.fragments.ProductOffersFragment;
import com.mobile.view.fragments.ProductSizeGuideFragment;
import com.mobile.view.fragments.RecentSearchFragment;
import com.mobile.view.fragments.RecentlyViewedFragment;
import com.mobile.view.fragments.ReviewFragment;
import com.mobile.view.fragments.ReviewWriteFragment;
import com.mobile.view.fragments.ReviewsFragment;
import com.mobile.view.fragments.SessionForgotPasswordFragment;
import com.mobile.view.fragments.SessionLoginEmailFragment;
import com.mobile.view.fragments.SessionLoginMainFragment;
import com.mobile.view.fragments.SessionRegisterFragment;
import com.mobile.view.fragments.ShoppingCartFragment;
import com.mobile.view.fragments.StaticPageFragment;
import com.mobile.view.fragments.StaticWebViewPageFragment;
import com.mobile.view.fragments.VariationsFragment;
import com.mobile.view.fragments.WishListFragment;
import com.mobile.view.fragments.order.MyOrdersFragment;
import com.mobile.view.fragments.order.OrderReturnCallFragment;
import com.mobile.view.fragments.order.OrderReturnConditionsFragment;
import com.mobile.view.fragments.order.OrderReturnStepsMain;
import com.mobile.view.fragments.order.OrderStatusFragment;
import com.mobile.view.newfragments.NewCheckoutAddressesFragment;
import com.mobile.view.newfragments.NewCheckoutPaymentMethodsFragment;
import com.mobile.view.newfragments.NewMyAccountAddressesFragment;
import com.mobile.view.newfragments.NewSessionLoginMainFragment;
import com.mobile.view.newfragments.NewShoppingCartFragment;
import com.mobile.view.newfragments.SubCategoryFilterFragment;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;
import com.pushwoosh.SendPushTagsCallBack;
import com.pushwoosh.fragment.PushEventListener;
import com.pushwoosh.fragment.PushFragment;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pushwoosh.BasePushMessageReceiver.JSON_DATA_KEY;

/**
 * @author sergiopereira
 */
@UseA4S
public class MainFragmentActivity extends DebugActivity implements PushEventListener {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    private BaseFragment fragment;
    //DROID-63 private NewBaseFragment newFragment;
    //DROID-63 private boolean isNewFragment = false;

    private FragmentType mCurrentFragmentType;

    private boolean isInMaintenance = false;

    /**
     * Constructor
     */
    public MainFragmentActivity() {
        super(NavigationAction.UNKNOWN, EnumSet.noneOf(MyMenuItem.class), IntConstants.ACTION_BAR_NO_TITLE);
    }

    //Registration receiver
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver() {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent) {
            checkMessage(intent);
        }
    };

    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
        //JSON_DATA_KEY contains JSON payload of push notification.
            checkMessage(intent);
            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    //Registration of the receivers
    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);
        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers() {
//Unregister receivers on pause
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
// pass.
        }
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
//pass through
        }
    }

    private void checkMessage(Intent intent) {
        if (null != intent) {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            } else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
                showMessage("register");
            } else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
                showMessage("unregister");
            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                showMessage("register error");
            } else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
                showMessage("unregister error");
            }
            else if(intent.hasExtra(PushManager.REGISTER_BROAD_CAST_ACTION)) {
                showMessage("REGISTER_BROAD_CAST_ACTION");
            }
            resetIntentValues();
        }
    }

    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();
        if (mainAppIntent != null) {
            if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
            } else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
                mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
            } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
                mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
            } else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
            } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
                mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
            }
            setIntent(mainAppIntent);
        }
    }

    private void showMessage(String message) {
        Log.i("AndroidBash", message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkMessage(intent);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.utils.MyActivity#onCreate(android.os.Bundle)
     */



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.d(TAG, "ON CREATE");
        // Enable Accengage rich push notifications

        //Init Pushwoosh fragment
        PushFragment.init(this);

        //Pushwoosh Begin Register receivers for push notifications
        registerReceivers();
//Create and start push manager
        PushManager pushManager = PushManager.getInstance(this);//Start push manager, this will count app open for Pushwoosh stats as well
        try {
            pushManager.onStartup(this);
        } catch (Exception e) {
//push notifications are not available or AndroidManifest.xml is not configured properly
        }
//Register for push!
        pushManager.registerForPushNotifications();
        checkMessage(getIntent());
 //PushwooshEnd in onCreate
        Ad4PushTracker.get().setPushNotificationLocked(false);

        //Emarsys
        EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
            @Override
            public void EmarsysMobileEngageResponse(boolean success) {
            }
        };
        EmarsysMobileEngage.getInstance(this).sendLogin(PushManager.getPushToken(this), emarsysMobileEngageResponse);
        // End of Emarsys

        // ON ORIENTATION CHANGE
        if (savedInstanceState == null) {
            Print.d(TAG, "################### SAVED INSTANCE IS NULL");
            // Initialize fragment controller
            FragmentController.getInstance().init();
            // Case invalid deep link goto HOME else goto deep link
            if (!DeepLinkManager.onSwitchToDeepLink(this, getIntent())) {
                onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }

        } else {
            mCurrentFragmentType = (FragmentType) savedInstanceState.getSerializable(ConstantsIntentExtra.FRAGMENT_TYPE);
            Print.d(TAG, "################### SAVED INSTANCE ISN'T NULL: " + mCurrentFragmentType);
            fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragmentType.toString());
            if (null != fragment) {
                fragment.setActivity(this);
            }
            // Get FC back stack from saved state and get fragments from FM
            ArrayList<String> backStackTypes = savedInstanceState.getStringArrayList(ConstantsIntentExtra.BACK_STACK);
            List<Fragment> originalFragments = this.getSupportFragmentManager().getFragments();
            if (!CollectionUtils.isEmpty(backStackTypes)) {
                FragmentController.getInstance().validateCurrentState(this, backStackTypes, originalFragments);
            } else {
                Print.d(TAG, "COULDN'T RECOVER BACK STACK");
            }
        }

        Fabric.with(this, new Crashlytics());

        /*
         * Used for on back pressed
         */
        Intent splashScreenParams = getIntent();
        if (splashScreenParams != null && splashScreenParams.getExtras() != null) {
            isInMaintenance = splashScreenParams.getExtras().getBoolean(ConstantsIntentExtra.IN_MAINTANCE, false);
        }
    }

    /*
     * (non-Javadoc)
     * For 4DS - http://wiki.accengage.com/android/doku.php?id=sub-classing-any-activity-type
     *
     * @see com.mobile.utils.BaseActivity#onNewIntent(android.content.Intent)
     */
/*    @Override
    protected void onNewIntent(Intent intent) {

        Print.d(TAG, "ON NEW INTENT");
       *//*
        // For AD4 - http://wiki.accengage.com/*//*android/doku.php?id=sub-classing-any-activity-type
        this.setIntent(intent);
        // Validate deep link
        DeepLinkManager.onSwitchToDeepLink(this, intent);
*//*
        super.onNewIntent(intent);

        //Check if we've got new intent with a push notification
        PushFragment.onNewIntent(this ,intent);
    }*/


    /*
     * (non-Javadoc)
     *
     * @see com.mobile.utils.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.d(TAG, "ON RESUME");
        registerReceivers();
        SendPushTagsCallBack callBack = new SendPushTagsCallBack() {
            @Override
            public void taskStarted() {

            }

            @Override
            public void onSentTagsSuccess(Map<String, String> map) {
                Print.d(TAG, "callback is" + map);
            }

            @Override
            public void onSentTagsError(Exception e) {

            }
        };

        PushWooshTracker.openApp(MainFragmentActivity.this, true);
        EmarsysTracker.openApp(true);

        PushwooshCounter.setAppOpenCount();
        HashMap<String, Object> open_count = new HashMap<>();
        open_count.put("AppOpenCount", PushwooshCounter.getAppOpenCount());
        PushManager.sendTags(MainFragmentActivity.this, open_count, callBack);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.utils.BaseActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        //PushWooshTracker.openApp(,true);
        unregisterReceivers();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.BaseActivity#onStop()
     */
    @Override
    protected void onStop() {
        Print.i(TAG, "ON STOP");
        super.onStop();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.utils.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.slidingmenu.lib.app.SlidingFragmentActivity#onSaveInstanceState(android
     * .os.Bundle)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.d(TAG, "ON SAVED INSTANCE STATE: " + mCurrentFragmentType);
        ArrayList<String> frags = new ArrayList<>();
        try {
            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            mCurrentFragmentType = FragmentType.getValue(tag);
            // Save the current back stack
            for (String entry : FragmentController.getInstance().returnAllEntries()) {
                frags.add(entry);
            }
        } catch (Exception e) {
            Print.w(TAG, "ERROR ON GET CURRENT FRAGMENT TYPE", e);
        }
        // Save the current fragment type on orientation change
        outState.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, mCurrentFragmentType);
        // Save the current back stack history
        outState.putStringArrayList(ConstantsIntentExtra.BACK_STACK, frags);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.utils.BaseActivity#onSwitchFragment(com.mobile.view.fragments
     * .FragmentType, android.os.Bundle, java.lang.Boolean)
     */
    @Override
    public void onSwitchFragment(FragmentType type, Bundle bundle, Boolean addToBackStack) {
        // Hide confirmation message
        mConfirmationCartMessageView.hideMessage();
        // Hide keyboard
        hideKeyboard();
        // Remove entries from back stack
        boolean removeEntries = false;
        //DROID-63 isNewFragment = false;
        // Validate fragment type
        switch (type) {
            case HOME:
                // Pop back stack until TEASERS
                if (FragmentController.getInstance().hasEntry(FragmentType.HOME.toString())) {
                    popBackStack(FragmentType.HOME.toString());
                    return;
                }
                fragment = newFragmentInstance(HomePageFragment.class, bundle);
                break;
            case CATALOG_SELLER:
            case CATALOG_BRAND:
            case CATALOG_DEEP_LINK:
            case CATALOG_CATEGORY:
            case CATALOG:
                // Default
                removeEntries = true;
                // Get indications to remove old entries or not
                if (CollectionUtils.containsKey(bundle, ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES)) {
                    removeEntries = bundle.getBoolean(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                    bundle.remove(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                }

                if (CollectionUtils.containsKey(bundle, ConstantsIntentExtra.SUB_CATEGORY_FILTER)) {
                    removeEntries = false;
                }

                // Put the target type
                bundle.putSerializable(ConstantsIntentExtra.TARGET_TYPE, type);
                // Create instance
                fragment = newFragmentInstance(CatalogFragment.class, bundle);
                // Put the type with unique identifier
                type = FragmentType.getUniqueIdentifier(FragmentType.CATALOG, fragment);
                break;
            case PRODUCT_DETAILS:
                // Create instance
                fragment = newFragmentInstance(ProductDetailsFragment.class, bundle);
                // Put the type with unique identifier
                type = FragmentType.getUniqueIdentifier(type, fragment);
                break;
            case PRODUCT_INFO:
                fragment = newFragmentInstance(ProductDetailsInfoFragment.class, bundle);
                break;
            case PRODUCT_GALLERY:
                fragment = newFragmentInstance(ProductImageGalleryFragment.class, bundle);
                break;
            case POPULARITY:
                fragment = newFragmentInstance(ReviewsFragment.class, bundle);
                break;
            case WRITE_REVIEW:
                fragment = newFragmentInstance(ReviewWriteFragment.class, bundle);
                break;
            case REVIEW:
                fragment = newFragmentInstance(ReviewFragment.class, bundle);
                break;
            case SHOPPING_CART:
                fragment = newFragmentInstance(NewShoppingCartFragment.class, bundle);
                break;
            case STATIC_PAGE:
                fragment = newFragmentInstance(StaticPageFragment.class, bundle);
                break;
            case STATIC_WEBVIEW_PAGE:
                fragment = newFragmentInstance(StaticWebViewPageFragment.class, bundle);
                break;
            case MY_ACCOUNT:
                removeEntries = true;
                fragment = newFragmentInstance(MyAccountFragment.class, bundle);
                break;
            case MY_USER_DATA:
                fragment = newFragmentInstance(MyAccountUserDataFragment.class, bundle);
                break;
            case MY_ORDERS:
                fragment = newFragmentInstance(MyOrdersFragment.class, bundle);
                break;
            case ORDER_STATUS:
                fragment = newFragmentInstance(OrderStatusFragment.class, bundle);
                break;
            case ORDER_RETURN_CONDITIONS:
                fragment = newFragmentInstance(OrderReturnConditionsFragment.class, bundle);
                break;
            case ORDER_RETURN_STEPS:
                fragment = newFragmentInstance(OrderReturnStepsMain.class, bundle);
                break;
            case CHOOSE_COUNTRY:
                fragment = newFragmentInstance(ChooseCountryFragment.class, bundle);
                break;
            case LOGIN:
                //fragment = newFragmentInstance(SessionLoginMainFragment.class, bundle);
                fragment = newFragmentInstance(NewSessionLoginMainFragment.class, bundle);
                break;
            case LOGIN_EMAIL:
                fragment = newFragmentInstance(SessionLoginEmailFragment.class, bundle);
                break;
            case REGISTER:
                fragment = newFragmentInstance(SessionRegisterFragment.class, bundle);
                break;
            case FORGOT_PASSWORD:
                fragment = newFragmentInstance(SessionForgotPasswordFragment.class, bundle);
                break;
            case CHECKOUT_MY_ADDRESSES:
                //DROID-63 isNewFragment = true;
                //DROID-63 newFragment = newNewFragmentInstance(NewCheckoutAddressesFragment.class, bundle);
                fragment = newFragmentInstance(NewCheckoutAddressesFragment.class, bundle);
                break;
            case CHECKOUT_CREATE_ADDRESS:
                fragment = newFragmentInstance(CheckoutCreateAddressFragment.class, bundle);
                break;
            case CHECKOUT_EDIT_ADDRESS:
                fragment = newFragmentInstance(CheckoutEditAddressFragment.class, bundle);
                break;
            case CHECKOUT_CONFIRMATION:
                fragment = newFragmentInstance(CheckoutConfirmationFragment.class, bundle);
                break;
            case CHECKOUT_SHIPPING:
                fragment = newFragmentInstance(CheckoutShippingMethodsFragment.class, bundle);
                break;
            case CHECKOUT_PAYMENT:
                fragment = newFragmentInstance(NewCheckoutPaymentMethodsFragment.class, bundle);
                break;
            case CHECKOUT_FINISH:
                fragment = newFragmentInstance(CheckoutFinishFragment.class, bundle);
                break;
            case CHECKOUT_THANKS:
                fragment = newFragmentInstance(CheckoutThanksFragment.class, bundle);
                break;
            case CHECKOUT_EXTERNAL_PAYMENT:
                fragment = newFragmentInstance(CheckoutExternalPaymentFragment.class, bundle);
                break;
            case CAMPAIGNS:
                fragment = newFragmentInstance(CampaignsFragment.class, bundle);
                break;
            case EMAIL_NOTIFICATION:
                fragment = newFragmentInstance(MyAccountNewslettersFragment.class, bundle);
                break;
            case WISH_LIST:
                removeEntries = true;
                fragment = newFragmentInstance(WishListFragment.class, bundle);
                break;
            case RECENT_SEARCHES_LIST:
                fragment = newFragmentInstance(RecentSearchFragment.class, bundle);
                break;
            case RECENTLY_VIEWED_LIST:
                removeEntries = true;
                fragment = newFragmentInstance(RecentlyViewedFragment.class, bundle);
                break;
            case PRODUCT_SIZE_GUIDE:
                fragment = newFragmentInstance(ProductSizeGuideFragment.class, bundle);
                break;
            case PRODUCT_OFFERS:
                fragment = newFragmentInstance(ProductOffersFragment.class, bundle);
                break;
            case MY_ACCOUNT_MY_ADDRESSES:
                //DROID-63 isNewFragment = true;
                //DROID-63 newFragment = newNewFragmentInstance(NewMyAccountAddressesFragment.class, bundle);
                fragment = newFragmentInstance(NewMyAccountAddressesFragment.class, bundle);
                break;
            case MY_ACCOUNT_CREATE_ADDRESS:
                fragment = newFragmentInstance(MyAccountCreateAddressFragment.class, bundle);
                break;
            case MY_ACCOUNT_EDIT_ADDRESS:
                fragment = newFragmentInstance(MyAccountEditAddressFragment.class, bundle);
                break;
            case INNER_SHOP:
                fragment = newFragmentInstance(InnerShopFragment.class, bundle);
                break;
            case COMBO_PAGE:
                fragment = newFragmentInstance(ComboFragment.class, bundle);
                break;
            case FILTERS:
                fragment = newFragmentInstance(FilterMainFragment.class, bundle);
                break;
            case Sub_CATEGORY_FILTERS:
                fragment = newFragmentInstance(SubCategoryFilterFragment.class, bundle);
                break;
            case VARIATIONS:
                fragment = newFragmentInstance(VariationsFragment.class, bundle);
                break;
            case ORDER_RETURN_CALL:
                fragment = newFragmentInstance(OrderReturnCallFragment.class, bundle);
                break;
            case ABOUT_US:
                removeEntries = true;
                fragment = newFragmentInstance(MyAccountAboutFragment.class, bundle);
                break;
            default:
                Print.w(TAG, "INVALID FRAGMENT TYPE");
                return;
        }
        // Clear search term
        if (type != FragmentType.CATALOG && type != FragmentType.FILTERS)
            JumiaApplication.INSTANCE.setSearchedTerm("");

        // Validate menu flag and pop entries until home
        if (removeEntries) {
            popBackStackEntriesUntilTag(FragmentType.HOME.toString());
        }

        Print.i(TAG, "ON SWITCH FRAGMENT: " + type);
        // Save the current state
        mCurrentFragmentType = type;

        fragmentManagerTransition(R.id.app_content, fragment, type, addToBackStack);

/* DROID-63
        // Transition
        if (!isNewFragment) {
            fragmentManagerTransition(R.id.app_content, fragment, type, addToBackStack);
        }
        else
        {
            fragmentManagerTransition(R.id.app_content, newFragment, type, addToBackStack);
        }
*/
    }

    /**
     * Create new fragment
     */
    private BaseFragment newFragmentInstance(@NonNull Class<? extends BaseFragment> fragmentClass, @Nullable Bundle arguments) {
        return BaseFragment.newInstance(getApplicationContext(), fragmentClass, arguments);
    }

/* DROID-63
    private  NewBaseFragment newNewFragmentInstance(@NonNull Class<? extends NewBaseFragment> fragmentClass, @Nullable Bundle arguments) {
        return NewBaseFragment.newInstance(getApplicationContext(), fragmentClass, arguments);
    }
*/

    /**
     * Fragment communication.<br>
     * The FragmentManager has some issues to get fragment with the same tag.<br>
     *
     * @author spereira
     */
    @Override
    public boolean communicateBetweenFragments(@Nullable String tag, @Nullable Bundle bundle) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            ((BaseFragment) fragment).notifyFragment(bundle);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.utils.MyActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Print.i(TAG, "ON BACK PRESSED");
        // This situation only occurs when user goes to Choose Country screen on maintenance page and presses back
        if (isInMaintenance()) {
            Intent newIntent = new Intent(this, SplashScreenActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
            finish();
        }
        // Case default
        else {
            onProcessBackPressed();
        }
    }

    /**
     * Process the back pressed
     */
    private void onProcessBackPressed() {
        Fragment frag = getActiveFragment();
        if (frag instanceof BaseFragment) {
            fragment = (BaseFragment) frag;

            // Clear search term
            if (fragment.getTag().equals(FragmentType.CATALOG.toString()))
                JumiaApplication.INSTANCE.setSearchedTerm("");

            // Case navigation opened
            if (mDrawerLayout.isDrawerOpen(mDrawerNavigation) && !(mDrawerLayout.getDrawerLockMode(mDrawerNavigation) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
                Print.i(TAG, "ON BACK PRESSED: NAV IS OPENED");
                mDrawerLayout.closeDrawer(mDrawerNavigation);
            }
            // Case fragment not allow back pressed
            else if (fragment == null || !fragment.allowBackPressed()) {
                Print.i(TAG, "NOT ALLOW BACK PRESSED: FRAGMENT");
                // Hide Keyboard
                hideKeyboard();
                // Back
                fragmentManagerBackPressed();
            }
            // Case fragment allow back pressed
            else {
                Print.i(TAG, "ALLOW BACK PRESSED: FRAGMENT");
            }
        }
/* DROID-63
        else
        {
            newFragment = (NewBaseFragment) frag;
            // Clear search term
            if (newFragment.getTag().equals(FragmentType.CATALOG.toString()))
                JumiaApplication.INSTANCE.setSearchedTerm("");

            // Case navigation opened
            if (mDrawerLayout.isDrawerOpen(mDrawerNavigation) && !(mDrawerLayout.getDrawerLockMode(mDrawerNavigation) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
                Print.i(TAG, "ON BACK PRESSED: NAV IS OPENED");
                mDrawerLayout.closeDrawer(mDrawerNavigation);
            }
            // Case fragment not allow back pressed
            else if (newFragment == null || !newFragment.allowBackPressed()) {
                Print.i(TAG, "NOT ALLOW BACK PRESSED: FRAGMENT");
                // Hide Keyboard
                hideKeyboard();
                // Back
                fragmentManagerBackPressed();
            }
            // Case fragment allow back pressed
            else {
                Print.i(TAG, "ALLOW BACK PRESSED: FRAGMENT");
            }
        }
*/
    }

    /**
     * Get the active fragment
     *
     * @return BaseFragment
     */
    public Fragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Print.i("BACKSTACK", "getBackStackEntryCount is 0");
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Print.i("BACKSTACK", "getActiveFragment:" + tag);
        return (Fragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * Pop back stack until fragment with tag.
     *
     * @param tag The fragment tag
     */
    protected void popBackStack(String tag) {
        // Pop back stack until tag
        popBackStackUntilTag(tag);
        // Get the current fragment
        fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

    @Override
    public void doOnRegistered(String registrationId) {
        Log.i(TAG, "Registered for pushes: " + registrationId);
    }

    @Override
    public void doOnRegisteredError(String errorId) {
        Log.e(TAG, "Failed to register for pushes: " + errorId);
    }

    @Override
    public void doOnMessageReceive(String message) {
        Log.i(TAG, "Notification opened: " + message);
    }

    @Override
    public void doOnUnregistered(final String message) {
        Log.i(TAG, "Unregistered from pushes: " + message);
    }

    @Override
    public void doOnUnregisteredError(String errorId) {
        Log.e(TAG, "Failed to unregister from pushes: " + errorId);
    }


}
