package com.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import com.a4s.sdk.plugins.annotations.UseA4S;
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
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.CampaignsFragment;
import com.mobile.view.fragments.CatalogFragment;
import com.mobile.view.fragments.CheckoutAddressesFragment;
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
import com.mobile.view.fragments.MyAccountAddressesFragment;
import com.mobile.view.fragments.MyAccountCreateAddressFragment;
import com.mobile.view.fragments.MyAccountEditAddressFragment;
import com.mobile.view.fragments.MyAccountFragment;
import com.mobile.view.fragments.MyAccountNewslettersFragment;
import com.mobile.view.fragments.MyAccountUserDataFragment;
import com.mobile.view.fragments.MyOrdersFragment;
import com.mobile.view.fragments.OrderStatusFragment;
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
import com.mobile.view.fragments.SessionLoginEmailFragment;
import com.mobile.view.fragments.SessionLoginMainFragment;
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
@UseA4S
public class MainFragmentActivity extends BaseActivity {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    private BaseFragment fragment;

    private FragmentType mCurrentFragmentType;

    private boolean isInMaintenance = false;

    /**
     * Constructor
     */
    public MainFragmentActivity() {
        super(NavigationAction.UNKNOWN,
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
                FragmentController.getInstance().validateCurrentState(this, backStackTypes, originalFragments, mCurrentFragmentType);
            } else {
                Print.d(TAG, "COULDN'T RECOVER BACK STACK");
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
        // Validate deep link
        DeepLinkManager.onSwitchToDeepLink(this, intent);
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
            case CATALOG_SELLER:
            case CATALOG_BRAND:
            case CATALOG_DEEPLINK:
            case CATALOG_CATEGORY:
            case CATALOG:
                // Default
                removeEntries = true;
                // Get indications to remove old entries or not
                if (CollectionUtils.containsKey(bundle, ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES)) {
                    removeEntries = bundle.getBoolean(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                    bundle.remove(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                }
                // Put the target type
                bundle.putSerializable(ConstantsIntentExtra.TARGET_TYPE, type);
                // Put the type
                type = FragmentType.CATALOG;
                // Create instance
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
            case ORDER_STATUS:
                fragment = OrderStatusFragment.getInstance(bundle);
                break;
            case CHOOSE_COUNTRY:
                fragment = ChooseCountryFragment.getInstance();
                break;
            case LOGIN:
                fragment = SessionLoginMainFragment.getInstance(bundle);
                break;
            case LOGIN_EMAIL:
                fragment = SessionLoginEmailFragment.getInstance(bundle);
                break;
            case REGISTER:
                fragment = SessionRegisterFragment.getInstance(bundle);
                break;
            case FORGOT_PASSWORD:
                fragment = SessionForgotPasswordFragment.getInstance();
                break;
            case CHECKOUT_MY_ADDRESSES:
                fragment = CheckoutAddressesFragment.newInstance();
                break;
            case CHECKOUT_CREATE_ADDRESS:
                fragment = CheckoutCreateAddressFragment.getInstance();
                break;
            case CHECKOUT_EDIT_ADDRESS:
                fragment = CheckoutEditAddressFragment.getInstance(bundle);
                break;
            case CHECKOUT_SHIPPING:
                fragment = CheckoutShippingMethodsFragment.getInstance();
                break;
            case CHECKOUT_PAYMENT:
                fragment = CheckoutPaymentMethodsFragment.getInstance();
                break;
            case CHECKOUT_FINISH:
                fragment = CheckoutFinishFragment.getInstance(bundle);
                break;
            case CHECKOUT_THANKS:
                fragment = CheckoutThanksFragment.getInstance(bundle);
                break;
            case CHECKOUT_EXTERNAL_PAYMENT:
                fragment = CheckoutExternalPaymentFragment.getInstance(bundle);
                break;
            case CAMPAIGNS:
                fragment = CampaignsFragment.newInstance(bundle);
                break;
            case EMAIL_NOTIFICATION:
                fragment = MyAccountNewslettersFragment.newInstance();
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
                fragment = ProductOffersFragmentNew.newInstance(bundle);
                break;
            case MY_ACCOUNT_MY_ADDRESSES:
                fragment = MyAccountAddressesFragment.newInstance();
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
            case COMBO_PAGE:
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
        // Clear search term
        if(type != FragmentType.CATALOG && type != FragmentType.FILTERS)
            JumiaApplication.INSTANCE.setSearchedTerm("");

        // Validate menu flag and pop entries until home
        if (removeEntries) {
            popBackStackEntriesUntilTag(FragmentType.HOME.toString());
        }

        Print.i(TAG, "ON SWITCH FRAGMENT: " + type);
        // Save the current state
        mCurrentFragmentType = type;

        // Transition
        fragmentManagerTransition(R.id.app_content, fragment, type, addToBackStack);
    }

    /**
     * Fragment communication
     */
    @Override
    public boolean communicateBetweenFragments(String tag, Bundle bundle) {
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
        fragment = getActiveFragment();

        // Clear search term
        if(fragment.getTag().equals(FragmentType.CATALOG.toString()))
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

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

}
