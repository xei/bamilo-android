/**
 *
 */
package com.mobile.view;

import android.content.Intent;
import android.os.Bundle;
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
import com.mobile.view.fragments.CheckoutShippingMethodsFragment;
import com.mobile.view.fragments.CheckoutThanksFragment;
import com.mobile.view.fragments.CheckoutWebFragment;
import com.mobile.view.fragments.ChooseCountryFragment;
import com.mobile.view.fragments.FavouritesFragment;
import com.mobile.view.fragments.HomeFragment;
import com.mobile.view.fragments.InnerShopFragment;
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
import com.mobile.view.fragments.WriteSellerReviewFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 */
@Tag(name = "MainActivity")
public class MainFragmentActivity extends BaseActivity implements OnPreferenceAttachedListener {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    private BaseFragment fragment;

    private FragmentType mCurrentFragmentType;

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

        /**
         * CASE APP IN QUICK LAUNCHER:
         * - parse the deep link intent from notification service
         * - start splash screen case valid intent
         */
        onParseValidDeepLinkIntent(getIntent());

        // ON ORIENTATION CHANGE
        if (savedInstanceState == null) {
            Log.d(TAG, "################### SAVED INSTANCE IS NULL");
            // Initialize fragment controller
            FragmentController.getInstance().init();
            // Validate intent
            if (!isValidDeepLinkNotification(getIntent())) {
                onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        } else {
            mCurrentFragmentType = (FragmentType) savedInstanceState.getSerializable(ConstantsIntentExtra.FRAGMENT_TYPE);

            Log.d(TAG, "################### SAVED INSTANCE ISN'T NULL: " + mCurrentFragmentType.toString());
            fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragmentType.toString());
            if (null != fragment) {
                fragment.setActivity(this);
            }

            // Get FC back stack from saved state and get fragments from FM
            ArrayList<String> backStackTypes = savedInstanceState.getStringArrayList(ConstantsIntentExtra.BACK_STACK);
            List<Fragment> originalFragments = this.getSupportFragmentManager().getFragments();
            if (backStackTypes != null && backStackTypes.size() > 0) {
                FragmentController.getInstance().validateCurrentState(this, backStackTypes, originalFragments, mCurrentFragmentType);
            }
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
        onParseValidDeepLinkIntent(intent);
    }

    /**
     * Validate and process intent from notification
     *
     * @param intent The deep link intent
     * @return valid or invalid
     */
    private boolean isValidDeepLinkNotification(Intent intent) {
        Log.i(TAG, "DEEP LINK: VALIDATE INTENT FROM NOTIFICATION");
        // Validate intent
        if (intent.hasExtra(ConstantsIntentExtra.FRAGMENT_TYPE)) {
            Log.i(TAG, "DEEP LINK: VALID INTENT");
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
        Log.i(TAG, "DEEP LINK: INVALID INTENT");
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
        //
        Ad4PushTracker.get().startActivity(this);
        //
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
        //
        Ad4PushTracker.get().stopActivity(this);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.BaseActivity#onStop()
     */
    @Override
    protected void onStop() {
        Log.i(TAG, "ON STOP");
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
        Log.d(TAG, "ON SAVED INSTANCE STATE: " + mCurrentFragmentType.toString());
        ArrayList<String> frags = new ArrayList<>();
        try {
            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            mCurrentFragmentType = FragmentType.valueOf(tag);
            // Save the current back stack
            for (String entry : FragmentController.getInstance().returnAllEntries()) {
                frags.add(entry);
            }
        } catch (Exception e) {
            Log.w(TAG, "ERROR ON GET CURRENT FRAGMENT TYPE", e);
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
        //
        showWarningVariation(false);
        // 
        hideKeyboard();
        // Validate fragment type
        switch (type) {
            case HOME:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                // Pop back stack until TEASERS
                if (FragmentController.getInstance().hasEntry(FragmentType.HOME.toString())) {
                    popBackStack(FragmentType.HOME.toString());
                    return;
                }
                fragment = HomeFragment.newInstance();
                break;
            case CATEGORIES:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
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
                fragment = ReviewFragment.getInstance(bundle);
                break;
            case SHOPPING_CART:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = ShoppingCartFragment.getInstance(bundle);
                break;
            case CHECKOUT_BASKET:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutWebFragment.getInstance();
                break;
            case REGISTER:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = SessionRegisterFragment.getInstance(bundle);
                break;
            case FORGOT_PASSWORD:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = SessionForgotPasswordFragment.getInstance();
                break;
            case TERMS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = SessionTermsFragment.getInstance(bundle);
                break;
            case MY_ACCOUNT:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = MyAccountFragment.getInstance();
                break;
            case MY_USER_DATA:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = MyAccountUserDataFragment.getInstance();
                break;
            case MY_ORDERS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = MyOrdersFragment.getInstance(bundle);
                break;
            case CHOOSE_COUNTRY:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = ChooseCountryFragment.getInstance();
                break;
            case LOGIN:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = SessionLoginFragment.getInstance(bundle);
                break;
            case ABOUT_YOU:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutAboutYouFragment.getInstance();
                break;
            case MY_ADDRESSES:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutMyAddressesFragment.getInstance();
                break;
            case CREATE_ADDRESS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutCreateAddressFragment.getInstance();
                break;
            case EDIT_ADDRESS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutEditAddressFragment.getInstance(bundle);
                break;
            case SHIPPING_METHODS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutShippingMethodsFragment.getInstance();
                break;
            case PAYMENT_METHODS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutPaymentMethodsFragment.getInstance();
                break;
            case MY_ORDER:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutMyOrderFragment.getInstance(bundle);
                break;
            case CHECKOUT_THANKS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutThanksFragment.getInstance(bundle);
                break;
            case CHECKOUT_EXTERNAL_PAYMENT:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = CheckoutExternalPaymentFragment.getInstance();
                break;
            case CAMPAIGNS:
                fragment = CampaignsFragment.newInstance(bundle);
                break;
            case EMAIL_NOTIFICATION:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = MyAccountEmailNotificationFragment.newInstance();
                break;
            case FAVORITE_LIST:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = FavouritesFragment.getInstance();
                break;
            case RECENT_SEARCHES_LIST:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = RecentSearchFragment.newInstance();
                break;
            case RECENTLY_VIEWED_LIST:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = RecentlyViewedFragment.getInstance();
                break;
            case PRODUCT_SIZE_GUIDE:
                fragment = ProductSizeGuideFragment.newInstance(bundle);
                break;
            case PRODUCT_OFFERS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = ProductOffersFragment.newInstance(bundle);
                break;
            case MY_ACCOUNT_MY_ADDRESSES:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = MyAccountMyAddressesFragment.newInstance();
                break;
            case MY_ACCOUNT_CREATE_ADDRESS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = MyAccountCreateAddressFragment.newInstance();
                break;
            case MY_ACCOUNT_EDIT_ADDRESS:
                JumiaApplication.INSTANCE.setIsFromBanner(false);
                fragment = MyAccountEditAddressFragment.newInstance(bundle);
                break;
            case INNER_SHOP:
                fragment = InnerShopFragment.getInstance(bundle);
                break;
            case WRITE_REVIEW_SELLER:
                fragment = WriteSellerReviewFragment.getInstance(bundle);
                break;
            default:
                Log.w(TAG, "INVALID FRAGMENT TYPE");
                return;
        }

        Log.i(TAG, "ON SWITCH FRAGMENT: " + type);
        // Save the current state
        mCurrentFragmentType = type;

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
        }
        // Case fragment allow back pressed
        else {
            Log.i(TAG, "ALLOW BACK PRESSED: FRAGMENT");
        }
    }

    /**
     * Get the active fragment
     *
     * @return BaseFragment
     */
    public BaseFragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Log.i("BACKSTACK", "getBackStackEntryCount is 0");
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Log.i("BACKSTACK", "getActiveFragment:" + tag);
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * Pop back stack until fragment with tag.
     *
     * @param tag The fragment tag
     */
    public void popBackStack(String tag) {
        // Pop back stack until tag
        popBackStackUntilTag(tag);
        // Get the current fragment
        fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    // ####################### MY ACCOUNT FRAGMENT #######################
    @Override
    public void onPreferenceAttached() {
    }

    // ####################### DEEP LINK #######################

    /**
     * Parse a valid deep link and redirect to Splash.
     *
     * @author nunocastro
     * @modified sergiopereira
     */
    private void onParseValidDeepLinkIntent(Intent intent) {
        Log.i(TAG, "ON PARSE DEEP LINK INTENT");
        Bundle mBundle = intent.getExtras();
        Log.i(TAG, "DEEP LINK - Bundle -> " + mBundle);
        if (null != mBundle) {
            Bundle payload = intent.getBundleExtra(BundleConstants.EXTRA_GCM_PAYLOAD);
            if (null != payload) {
                Log.i(TAG, "DEEP LINK: START SPLASH ACTIVITY");
                Intent newIntent = new Intent(this, SplashScreenActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.putExtra(BundleConstants.EXTRA_GCM_PAYLOAD, payload);
                startActivity(newIntent);
                finish();
            }
        }
    }

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

}
