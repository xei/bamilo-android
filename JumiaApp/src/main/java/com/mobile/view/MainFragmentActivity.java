/**
 * 
 */
package com.mobile.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import com.ad4screen.sdk.Tag;
import com.mobile.app.JumiaApplication;
import com.mobile.constants.BundleConstants;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.tracking.Ad4PushTracker;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.utils.EventType;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.PreferenceListFragment.OnPreferenceAttachedListener;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.CampaignsFragment;
import com.mobile.view.fragments.CatalogFragment;
import com.mobile.view.fragments.CategoriesCollectionFragment;
import com.mobile.view.fragments.CheckoutAboutYouFragment;
import com.mobile.view.fragments.CheckoutCreateAddressFragment;
import com.mobile.view.fragments.CheckoutEditAddressFragment;
import com.mobile.view.fragments.CheckoutExternalPaymentFragment;
import com.mobile.view.fragments.CheckoutMyAddressesFragment;
import com.mobile.view.fragments.CheckoutMyOrderFragment;
import com.mobile.view.fragments.CheckoutPaymentMethodsFragment;
import com.mobile.view.fragments.CheckoutPollAnswerFragment;
import com.mobile.view.fragments.CheckoutShippingMethodsFragment;
import com.mobile.view.fragments.CheckoutThanksFragment;
import com.mobile.view.fragments.CheckoutWebFragment;
import com.mobile.view.fragments.ChooseCountryFragment;
import com.mobile.view.fragments.FavouritesFragment;
import com.mobile.view.fragments.HomeFragment;
import com.mobile.view.fragments.MyAccountCreateAddressFragment;
import com.mobile.view.fragments.MyAccountEditAddressFragment;
import com.mobile.view.fragments.MyAccountEmailNotificationFragment;
import com.mobile.view.fragments.MyAccountFragment;
import com.mobile.view.fragments.MyAccountMyAddressesFragment;
import com.mobile.view.fragments.MyAccountUserDataFragment;
import com.mobile.view.fragments.MyOrdersFragment;
import com.mobile.view.fragments.ProductDetailsDescriptionFragment;
import com.mobile.view.fragments.ProductDetailsFragment;
import com.mobile.view.fragments.ProductImageGalleryFragment;
import com.mobile.view.fragments.ProductOffersFragment;
import com.mobile.view.fragments.ProductSizeGuideFragment;
import com.mobile.view.fragments.RecentSearchFragment;
import com.mobile.view.fragments.RecentlyViewedFragment;
import com.mobile.view.fragments.ReviewFragment;
import com.mobile.view.fragments.ReviewWriteFragment;
import com.mobile.view.fragments.ReviewsFragment;
import com.mobile.view.fragments.SessionForgotPasswordFragment;
import com.mobile.view.fragments.SessionLoginFragment;
import com.mobile.view.fragments.SessionRegisterFragment;
import com.mobile.view.fragments.SessionTermsFragment;
import com.mobile.view.fragments.ShoppingCartFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
@Tag(name = "MainActivity")
public class MainFragmentActivity extends BaseActivity implements OnPreferenceAttachedListener {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    private BaseFragment fragment;

    private FragmentType currentFragmentType;

    private boolean wasReceivedNotification = false;

    private boolean isInMaintenance = false;

    /**
     * Constructor
     */
    public MainFragmentActivity() {
        super(NavigationAction.Unknown,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.noneOf(EventType.class),
                0,
                R.layout.main_fragment_activity_layout);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE");
        
        // Parse deep link from service
        parseDeeplinkIntent(getIntent());
        
        // ON ORIENTATION CHANGE
        if (savedInstanceState == null) {
            Log.d(TAG, "################### SAVED INSTANCE IS NULL");
            // Initialize fragment controller
            FragmentController.getInstance().init();
            // Validate intent
            if (!isValidNotification(getIntent()))
                onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        } else {
            currentFragmentType = (FragmentType) savedInstanceState.getSerializable(ConstantsIntentExtra.FRAGMENT_TYPE);

            Log.d(TAG, "################### SAVED INSTANCE ISN'T NULL: " + currentFragmentType.toString());
            fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(currentFragmentType.toString());
            if (null != fragment) {
                fragment.setActivity(this);
            }
            
            // Get FC Backstack from saved state and get fragments from FM
            ArrayList<String> backstackTypes = (ArrayList<String>) savedInstanceState.getStringArrayList(ConstantsIntentExtra.BACK_STACK);
            List<Fragment> originalFragments = this.getSupportFragmentManager().getFragments();
            if (backstackTypes != null && backstackTypes.size() > 0)
                FragmentController.getInstance().validateCurrentState(this, backstackTypes, originalFragments, currentFragmentType);
            
        }

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
     * 
     * @see com.mobile.utils.BaseActivity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "ON NEW INTENT");
        super.onNewIntent(intent);
        // For AD4 - http://wiki.accengage.com/android/doku.php?id=sub-classing-any-activity-type
        this.setIntent(intent);
        // Parse deep link from splash screen
        parseDeeplinkIntent(intent);
        // Validate deep link
        isValidNotification(intent);
    }

    /**
     * Validate and process intent from notification
     * 
     * @param intent
     * @return
     */
    private boolean isValidNotification(Intent intent) {
        Log.d(TAG, "VALIDATE INTENT FROM NOTIFICATION");
        // Validate intent
        if (intent.hasExtra(ConstantsIntentExtra.FRAGMENT_TYPE)) {
            Log.d(TAG, "VALID INTENT");
            // Get extras from notifications
            FragmentType fragmentType = (FragmentType) intent.getSerializableExtra(ConstantsIntentExtra.FRAGMENT_TYPE);
            Bundle bundle = intent.getBundleExtra(ConstantsIntentExtra.FRAGMENT_BUNDLE);
            // Validate this step to maintain the base TAG
            onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            // Switch to fragment with respective bundle
            onSwitchFragment(fragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);
            // Set flag
            wasReceivedNotification = true;
            // Return result
            return true;
        }
        Log.d(TAG, "INVALID INTENT");
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.utils.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ON RESUME");

        // AD4Push activity tracking for in-app messages
        //Ad4PushTracker.startActivityForInAppMessages(this);
        Ad4PushTracker.get().startActivity(this);
        //Ad4PushTracker.setPushNotificationLocked(true);
        AdjustTracker.onResume(this);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.utils.BaseActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.BaseActivity#onStop()
     */
    @Override
    protected void onStop() {
        Log.i(TAG, "ON STOP");
        super.onStop();
        // AD4Push activity tracking for in-app messages
        Ad4PushTracker.get().stopActivity(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.utils.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        
        JumiaApplication.INSTANCE.setLoggedIn(false);

        //
        if (wasReceivedNotification) {
            wasReceivedNotification = false;
            getIntent().removeExtra(ConstantsIntentExtra.FRAGMENT_TYPE);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.slidingmenu.lib.app.SlidingFragmentActivity#onSaveInstanceState(android
     * .os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "ON SAVED INSTANCE STATE: " + currentFragmentType.toString());
        ArrayList<String> frags = new ArrayList<String>();
        try {
            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            currentFragmentType = FragmentType.valueOf(tag);
            // Save the current back stack
            Iterator<String> iterator =  FragmentController.getInstance().returnAllEntries().iterator();
            while (iterator.hasNext()) {
                frags.add(iterator.next());
            }
            
        } catch (Exception e) {
            Log.w(TAG, "ERROR ON GET CURRENT FRAGMENT TYPE", e);
        }
        // Save the current fragment type on orientation change
        outState.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, currentFragmentType);
        // Save the current backstack history
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
        showWarningVariation(false);
        // 
        hideKeyboard();
        // Validate fragment type
        switch (type) {
        case HOME:
            // Pop back stack until TEASERS
            if (FragmentController.getInstance().hasEntry(FragmentType.HOME.toString())) {
                popBackStack(FragmentType.HOME.toString());
                return;
            }
            fragment = HomeFragment.newInstance();
            break;
        case CATEGORIES:
            fragment = CategoriesCollectionFragment.getInstance(bundle);
            break;
        case CATALOG:
            fragment = CatalogFragment.getInstance(bundle);
            break;
        case PRODUCT_DETAILS:
            fragment = ProductDetailsFragment.getInstance(bundle);
            break;
        case PRODUCT_DESCRIPTION:
            fragment = ProductDetailsDescriptionFragment.getInstance(bundle);
            break;
        case PRODUCT_GALLERY:
            fragment = ProductImageGalleryFragment.getInstance(bundle);
            break;
        case POPULARITY:
            fragment = ReviewsFragment.getInstance(bundle);
            break;
        case WRITE_REVIEW:
            fragment = ReviewWriteFragment.getInstance(bundle);
            break;
        case REVIEW:
            fragment = ReviewFragment.getInstance();
            break;
        case SHOPPING_CART:
            fragment = ShoppingCartFragment.getInstance();
            break;
        case CHECKOUT_BASKET:
            fragment = CheckoutWebFragment.getInstance();
            break;
        case REGISTER:
            fragment = SessionRegisterFragment.getInstance(bundle);
            break;
        case FORGOT_PASSWORD:
            fragment = SessionForgotPasswordFragment.getInstance();
            break;
        case TERMS:
            fragment = SessionTermsFragment.getInstance(bundle);
            break;
        case MY_ACCOUNT:
            fragment = MyAccountFragment.getInstance();
            break;
        case MY_USER_DATA:
            fragment = MyAccountUserDataFragment.getInstance();
            break;
        case MY_ORDERS:
            fragment = MyOrdersFragment.getInstance(bundle);
            break;
        case CHOOSE_COUNTRY:
            fragment = ChooseCountryFragment.getInstance();
            break;
        case LOGIN:
            fragment = SessionLoginFragment.getInstance(bundle);
            break;
        case ABOUT_YOU:
            fragment = CheckoutAboutYouFragment.getInstance(bundle);
            break;
        case POLL:
            fragment = CheckoutPollAnswerFragment.getInstance(bundle);
            break;
        case MY_ADDRESSES:
            fragment = CheckoutMyAddressesFragment.getInstance(bundle);
            break;
        case CREATE_ADDRESS:
            fragment = CheckoutCreateAddressFragment.getInstance(bundle);
            break;
        case EDIT_ADDRESS:
            fragment = CheckoutEditAddressFragment.getInstance(bundle);
            break;
        case SHIPPING_METHODS:
            fragment = CheckoutShippingMethodsFragment.getInstance(bundle);
            break;
        case PAYMENT_METHODS:
            fragment = CheckoutPaymentMethodsFragment.getInstance(bundle);
            break;
        case MY_ORDER:
            fragment = CheckoutMyOrderFragment.getInstance(bundle);
            break;
        case CHECKOUT_THANKS:
            fragment = CheckoutThanksFragment.getInstance(bundle);
            break;
        case CHECKOUT_EXTERNAL_PAYMENT:
            fragment = CheckoutExternalPaymentFragment.getInstance();
            break;
        case CAMPAIGNS:
            fragment = CampaignsFragment.newInstance(bundle);
            break;
        case EMAIL_NOTIFICATION:
            fragment = MyAccountEmailNotificationFragment.newInstance(bundle);
            break;
        case FAVORITE_LIST:
            fragment = FavouritesFragment.getInstance();
            break;
        case RECENTSEARCHES_LIST:
            fragment = RecentSearchFragment.newInstance();
            break;
        case RECENTLYVIEWED_LIST:
            fragment = RecentlyViewedFragment.getInstance();
            break;
        case PRODUCT_SIZE_GUIDE:
            fragment = ProductSizeGuideFragment.newInstance(bundle);
            break;
        case PRODUCT_OFFERS:
            fragment = ProductOffersFragment.newInstance(bundle);
            break;
        case MY_ACCOUNT_MY_ADDRESSES:
            fragment = MyAccountMyAddressesFragment.newInstance();
            break;
        case MY_ACCOUNT_CREATE_ADDRESS:
            fragment = MyAccountCreateAddressFragment.newInstance();
            break;
        case MY_ACCOUNT_EDIT_ADDRESS:
            fragment = MyAccountEditAddressFragment.newInstance(bundle);
            break;
        default:
            Log.w(TAG, "INVALIDE FRAGMENT TYPE");
            return;
        }

        try {
            fragment.setArguments(null);
            fragment.setArguments(bundle);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "ON SWITCH FRAGMENT: " + type.toString());
        // Save the current state
        currentFragmentType = type;
        // Transition
        fragmentManagerTransition(R.id.rocket_app_content, fragment, type.toString(), addToBackStack);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.utils.MyActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Log.i(TAG, "ON BACK PRESSED");

        /*-
         * This situation only occurs when user goes to Choose Country screen on maintenance page and presses back
         */
        if (isInMaintenance) {
            Intent newIntent = new Intent(this, SplashScreenActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
            finish();
            return;
        }
        
        /*-
         * Default
         */
        fragment = getActiveFragment();
        // Case navigation opened
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation) && !(mDrawerLayout.getDrawerLockMode(mDrawerNavigation) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            Log.i(TAG, "ON BACK PRESSED: NAV IS OPENED");
            mDrawerLayout.closeDrawer(mDrawerNavigation);
        }
        // Case fragment not allow back pressed
        else if (fragment == null || !fragment.allowBackPressed()) {
            Log.i(TAG, "NOT ALLOW BACK PRESSED: FRAGMENT");
            fragmentManagerBackPressed();
        // Case fragment allow back pressed    
        } else {
            Log.i(TAG, "ALLOW BACK PRESSED: FRAGMENT");
        }
    }

    /**
     * Get the active fragment
     * @return
     */
    public BaseFragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Log.i("BACKSTACK","getBackStackEntryCount is 0" );
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Log.i("BACKSTACK","getActiveFragment:"+tag);
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * Pop back stack
     * 
     * @param tag
     */
    public void popBackStack(String tag) {
        // Pop back stack until tag
        popBackStackUntilTag(tag);
        // Get the current fragment
        fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    // ####################### MY ACCOUNT FRAGMENT #######################
    @Override
    public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
    }

    // ####################### DEEP LINK #######################
    /**
     * Parse the deep link
     * @author nunocastro
     */
    private void parseDeeplinkIntent(Intent intent) {
        Bundle mBundle = intent.getExtras();
        Uri data = intent.getData();
          
        Log.i(TAG, "PARSE DEEP LINK - Bundle -> " + (null != mBundle ? mBundle.keySet().toString() : "null"));
        Log.i(TAG, "PARSE DEEP LINK - data -> " + data);
        
        if (null != mBundle) {        
            Bundle payload = intent.getBundleExtra(BundleConstants.EXTRA_GCM_PAYLOAD);
            if (null != payload) {
                Intent newIntent = new Intent(this, SplashScreenActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);                
                newIntent.putExtra(BundleConstants.EXTRA_GCM_PAYLOAD, payload);
                
                startActivity(newIntent);
                finish();
            }
        }
    }
    
    public boolean isInMaintenance(){
        return isInMaintenance;
    }
    
}
