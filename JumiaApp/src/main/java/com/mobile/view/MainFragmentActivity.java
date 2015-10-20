package com.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import com.ad4screen.sdk.Tag;
import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.PreferenceListFragment.OnPreferenceAttachedListener;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.CampaignsFragment;
import com.mobile.view.fragments.CatalogFragment;
import com.mobile.view.fragments.CheckoutAboutYouFragment;
import com.mobile.view.fragments.CheckoutCreateAddressFragment;
import com.mobile.view.fragments.CheckoutEditAddressFragment;
import com.mobile.view.fragments.CheckoutExternalPaymentFragment;
import com.mobile.view.fragments.CheckoutMyAddressesFragment;
import com.mobile.view.fragments.CheckoutMyOrderFragment;
import com.mobile.view.fragments.CheckoutPaymentMethodsFragment;
import com.mobile.view.fragments.CheckoutShippingMethodsFragment;
import com.mobile.view.fragments.CheckoutThanksFragment;
import com.mobile.view.fragments.ChooseCountryFragment;
import com.mobile.view.fragments.ComboFragment;
import com.mobile.view.fragments.FilterMainFragment;
import com.mobile.view.fragments.HomePageFragment;
import com.mobile.view.fragments.InnerShopFragment;
import com.mobile.view.fragments.MyAccountCreateAddressFragment;
import com.mobile.view.fragments.MyAccountEditAddressFragment;
import com.mobile.view.fragments.MyAccountEmailNotificationFragment;
import com.mobile.view.fragments.MyAccountFragment;
import com.mobile.view.fragments.MyAccountMyAddressesFragment;
import com.mobile.view.fragments.MyAccountUserDataFragment;
import com.mobile.view.fragments.MyAddressesSessionLogin;
import com.mobile.view.fragments.MyOrdersFragment;
import com.mobile.view.fragments.ProductDetailsFragment;
import com.mobile.view.fragments.ProductDetailsInfoFragment;
import com.mobile.view.fragments.ProductImageGalleryFragment;
import com.mobile.view.fragments.ProductOffersFragmentNew;
import com.mobile.view.fragments.ProductSizeGuideFragment;
import com.mobile.view.fragments.RecentSearchFragment;
import com.mobile.view.fragments.RecentlyViewedFragment;
import com.mobile.view.fragments.ReviewFragment;
import com.mobile.view.fragments.ReviewWriteFragment;
import com.mobile.view.fragments.ReviewsFragment;
import com.mobile.view.fragments.SessionForgotPasswordFragment;
import com.mobile.view.fragments.SessionLoginFragment;
import com.mobile.view.fragments.SessionRegisterFragment;
import com.mobile.view.fragments.ShoppingCartFragment;
import com.mobile.view.fragments.StaticPageFragment;
import com.mobile.view.fragments.VariationsFragment;
import com.mobile.view.fragments.WishListFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 */
@Tag(name = "MainActivity")
public class MainFragmentActivity extends BaseActivity implements OnPreferenceAttachedListener {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    private BaseFragment fragment;

    private FragmentType mCurrentFragmentType;

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
        Print.d(TAG, "ON CREATE");
        // Enable Accengage rich push notifications
        Ad4PushTracker.get().setPushNotificationLocked(false);
        // ON ORIENTATION CHANGE
        if (savedInstanceState == null) {
            Print.d(TAG, "################### SAVED INSTANCE IS NULL");
            // Initialize fragment controller
            FragmentController.getInstance().init();
            // Get deep link
            Bundle mDeepLinkBundle = DeepLinkManager.hasDeepLink(getIntent());
            // Validate deep link
            boolean isDeepLinkLaunch = isValidDeepLinkNotification(mDeepLinkBundle);
            // Track open app event for all tracker but Adjust
            TrackerDelegator.trackAppOpen(getApplicationContext(), isDeepLinkLaunch);
            // Invalid deep link
            if (!isDeepLinkLaunch) {
                onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            } else {
                // Adjust reattribution
                TrackerDelegator.deeplinkReattribution(getIntent());
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
                FragmentController.getInstance().validateCurrentState(this, backStackTypes, originalFragments, mCurrentFragmentType);
            } else {
                Print.d(TAG, "COULDN'T RECOVER BACKSTACK");
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
     * For 4DS - http://wiki.accengage.com/android/doku.php?id=sub-classing-any-activity-type
     * 
     * @see com.mobile.utils.BaseActivity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Print.d(TAG, "ON NEW INTENT");
        // For AD4 - http://wiki.accengage.com/android/doku.php?id=sub-classing-any-activity-type
        this.setIntent(intent);
        // Get deep link
        Bundle mDeepLinkBundle = DeepLinkManager.hasDeepLink(intent);
        // Validate deep link
        boolean isDeepLinkLaunch = isValidDeepLinkNotification(mDeepLinkBundle);
        //track open app event for all tracker but Adjust
        TrackerDelegator.trackAppOpen(getApplicationContext(), isDeepLinkLaunch);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.utils.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.d(TAG, "ON RESUME");
        //
        Ad4PushTracker.get().startActivity(this);
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
        //
        Ad4PushTracker.get().stopActivity(this);
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
        JumiaApplication.INSTANCE.setLoggedIn(false);
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
        Print.d(TAG, "ON SAVED INSTANCE STATE: " + mCurrentFragmentType.toString());
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
        //
        warningFactory.hideWarning();
        // 
        hideKeyboard();

        boolean removeEntries = false;

        // Validate fragment type
        switch (type) {
            case HOME:
                // Pop back stack until TEASERS
                if (FragmentController.getInstance().hasEntry(FragmentType.HOME.toString())) {
                    popBackStack(FragmentType.HOME.toString());
                    return;
                }
                fragment = HomePageFragment.newInstance(bundle);
                break;
            case CATALOG:
                if(CollectionUtils.containsKey(bundle, ConstantsIntentExtra.REMOVE_ENTRIES)){
                    removeEntries = bundle.getBoolean(ConstantsIntentExtra.REMOVE_ENTRIES);
                    bundle.remove(ConstantsIntentExtra.REMOVE_ENTRIES);
                } else {
                    removeEntries = true;
                }
                fragment = CatalogFragment.getInstance(bundle);
                break;
            case PRODUCT_DETAILS:
                fragment = ProductDetailsFragment.getInstance(bundle);
                type.setId(fragment.hashCode());
                break;
            case PRODUCT_INFO:
                fragment = ProductDetailsInfoFragment.getInstance(bundle);
                break;
            case PRODUCT_GALLERY:
                fragment = ProductImageGalleryFragment.getInstance(bundle);
                break;
            case POPULARITY:
            //    fragment = ReviewsFragment.getInstance(bundle);
                fragment = ReviewsFragment.getInstance(bundle);
                break;
            case WRITE_REVIEW:
                fragment = ReviewWriteFragment.getInstance(bundle);
                break;
            case REVIEW:
                fragment = ReviewFragment.getInstance(bundle);
                break;
            case SHOPPING_CART:
                fragment = ShoppingCartFragment.getInstance(bundle);
                break;
            case REGISTER:
                fragment = SessionRegisterFragment.getInstance(bundle);
                break;
            case FORGOT_PASSWORD:
                fragment = SessionForgotPasswordFragment.getInstance();
                break;
            case STATIC_PAGE:
                fragment = StaticPageFragment.getInstance(bundle);
                break;
            case MY_ACCOUNT:
                removeEntries = true;
                fragment = MyAccountFragment.getInstance();
                break;
            case MY_USER_DATA:
                fragment = MyAccountUserDataFragment.getInstance();
                break;
            case MY_ORDERS:
                fragment = MyOrdersFragment.getInstance();
                break;
            case CHOOSE_COUNTRY:
                fragment = ChooseCountryFragment.getInstance();
                break;
            case LOGIN:
                fragment = SessionLoginFragment.getInstance(bundle);
                break;
            case MY_ADDRESSES_LOGIN:
                fragment = MyAddressesSessionLogin.getInstance(bundle);
                break;
            case ABOUT_YOU:
                fragment = CheckoutAboutYouFragment.getInstance();
                break;
            case MY_ADDRESSES:
                fragment = CheckoutMyAddressesFragment.getInstance();
                break;
            case CREATE_ADDRESS:
                fragment = CheckoutCreateAddressFragment.getInstance();
                break;
            case EDIT_ADDRESS:
                fragment = CheckoutEditAddressFragment.getInstance(bundle);
                break;
            case SHIPPING_METHODS:
                fragment = CheckoutShippingMethodsFragment.getInstance();
                break;
            case PAYMENT_METHODS:
                fragment = CheckoutPaymentMethodsFragment.getInstance();
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
                fragment = MyAccountEmailNotificationFragment.newInstance();
                break;
            case WISH_LIST:
                removeEntries = true;
                fragment = WishListFragment.getInstance();
                break;
            case RECENT_SEARCHES_LIST:
                fragment = RecentSearchFragment.newInstance();
                break;
            case RECENTLY_VIEWED_LIST:
                removeEntries = true;
                fragment = RecentlyViewedFragment.getInstance();
                break;
            case PRODUCT_SIZE_GUIDE:
                fragment = ProductSizeGuideFragment.newInstance(bundle);
                break;
            case PRODUCT_OFFERS:
              //  fragment = ProductOffersFragment.newInstance(bundle);
                fragment = ProductOffersFragmentNew.newInstance(bundle);
                break;
            case MY_ACCOUNT_MY_ADDRESSES:
                fragment = MyAccountMyAddressesFragment.newInstance();
                break;
            case MY_ACCOUNT_CREATE_ADDRESS:
                fragment = MyAccountCreateAddressFragment.newInstance(bundle);
                break;
            case MY_ACCOUNT_EDIT_ADDRESS:
                fragment = MyAccountEditAddressFragment.newInstance(bundle);
                break;
            case INNER_SHOP:
                fragment = InnerShopFragment.getInstance(bundle);
                break;
//            case WRITE_REVIEW_SELLER:
//                fragment = WriteSellerReviewFragment.getInstance(bundle);
//                break;
            case COMBOPAGE:
                fragment = ComboFragment.getInstance(bundle);
                break;
            case FILTERS:
                fragment = FilterMainFragment.getInstance(bundle);
                break;
            case VARIATIONS:
                fragment = VariationsFragment.getInstance(bundle);
                break;
            default:
                Print.w(TAG, "INVALID FRAGMENT TYPE");
                return;
        }


        // Validate menu flag and pop entries until home
        if (removeEntries) {
            popBackStackEntriesUntilTag(FragmentType.HOME.toString());
        }

        Print.i(TAG, "ON SWITCH FRAGMENT: " + type);
        // Save the current state
        mCurrentFragmentType = type;

        // Transition
        fragmentManagerTransition(R.id.rocket_app_content, fragment, type, addToBackStack);
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
        fragment = getActiveFragment();
        // Case navigation opened
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation) && !(mDrawerLayout.getDrawerLockMode(mDrawerNavigation) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            Print.i(TAG, "ON BACK PRESSED: NAV IS OPENED");
            mDrawerLayout.closeDrawer(mDrawerNavigation);
        }
        // Case fragment not allow back pressed
        else if (fragment == null || !fragment.allowBackPressed()) {
            Print.i(TAG, "NOT ALLOW BACK PRESSED: FRAGMENT");
            fragmentManagerBackPressed();
        }
        // Case fragment allow back pressed
        else {
            Print.i(TAG, "ALLOW BACK PRESSED: FRAGMENT");
        }
    }

    /**
     * Get the active fragment
     *
     * @return BaseFragment
     */
    public BaseFragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Print.i("BACKSTACK", "getBackStackEntryCount is 0");
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Print.i("BACKSTACK", "getActiveFragment:" + tag);
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
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

    // ####################### MY ACCOUNT FRAGMENT #######################
    @Override
    public void onPreferenceAttached() {
    }

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

    // ####################### DEEP LINK #######################

    /**
     * Validate and process intent from notification
     *
     * @param bundle The deep link intent
     * @return valid or invalid
     */
    private boolean isValidDeepLinkNotification(Bundle bundle) {
        Print.i(TAG, "DEEP LINK: VALIDATE INTENT FROM NOTIFICATION");
        if (bundle != null) {
            // Get fragment type
            FragmentType fragmentType = (FragmentType) bundle.getSerializable(DeepLinkManager.FRAGMENT_TYPE_TAG);
            //Print.d(TAG, "DEEP LINK FRAGMENT TYPE: " + fragmentType.toString());
            // Validate fragment type
            if (fragmentType != FragmentType.UNKNOWN) {
                // Restart back stack and fragment manager
                FragmentController.getInstance().popAllBackStack(this);
                // Validate this step to maintain the base TAG
                onSwitchFragment(FragmentType.HOME, bundle, FragmentController.ADD_TO_BACK_STACK);
                // Switch to fragment with respective bundle
                if(fragmentType != FragmentType.HOME) {
                    onSwitchFragment(fragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);
                }
                return true;
            }
        }
        Print.i(TAG, "DEEP LINK: INVALID INTENT");
        return false;
    }

}
