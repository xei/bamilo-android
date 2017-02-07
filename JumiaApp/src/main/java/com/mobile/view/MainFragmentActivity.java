package com.mobile.view;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import com.a4s.sdk.plugins.annotations.UseA4S;
import com.mobile.app.DebugActivity;
import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.utils.CollectionUtils;
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
import com.mobile.view.fragments.MyAccountAboutFragment;
import com.mobile.view.fragments.MyAccountAddressesFragment;
import com.mobile.view.fragments.MyAccountCreateAddressFragment;
import com.mobile.view.fragments.MyAccountEditAddressFragment;
import com.mobile.view.fragments.MyAccountFragment;
import com.mobile.view.fragments.MyAccountNewslettersFragment;
import com.mobile.view.fragments.MyAccountUserDataFragment;
import com.mobile.view.fragments.MyNewAccountFragment;
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
import com.mobile.view.newfragments.NewBaseFragment;
import com.mobile.view.newfragments.NewCheckoutAddressesFragment;
import com.mobile.view.newfragments.NewMyAccountAddressesFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 */
@UseA4S
public class MainFragmentActivity extends DebugActivity {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    private BaseFragment fragment;
    private NewBaseFragment newFragment;
    private boolean isNewFragment = false;

    private FragmentType mCurrentFragmentType;

    private boolean isInMaintenance = false;

    /**
     * Constructor
     */
    public MainFragmentActivity() {
        super(NavigationAction.UNKNOWN, EnumSet.noneOf(MyMenuItem.class), IntConstants.ACTION_BAR_NO_TITLE);
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
                FragmentController.getInstance().validateCurrentState(this, backStackTypes, originalFragments);
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
        isNewFragment = false;
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
                fragment = newFragmentInstance(ShoppingCartFragment.class, bundle);
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
            case MY_NEW_ACCOUNT:
                removeEntries = true;
                fragment = newFragmentInstance(MyNewAccountFragment.class, bundle);
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
                fragment = newFragmentInstance(SessionLoginMainFragment.class, bundle);
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
                isNewFragment = true;
                newFragment = newNewFragmentInstance(NewCheckoutAddressesFragment.class, bundle);
               // fragment = newFragmentInstance(CheckoutAddressesFragment.class, bundle);
                break;
            case CHECKOUT_CREATE_ADDRESS:
                fragment = newFragmentInstance(CheckoutCreateAddressFragment.class, bundle);
                break;
            case CHECKOUT_EDIT_ADDRESS:
                fragment = newFragmentInstance(CheckoutEditAddressFragment.class, bundle);
                break;
            case CHECKOUT_SHIPPING:
                fragment = newFragmentInstance(CheckoutShippingMethodsFragment.class, bundle);
                break;
            case CHECKOUT_PAYMENT:
                fragment = newFragmentInstance(CheckoutPaymentMethodsFragment.class, bundle);
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
                isNewFragment = true;
                newFragment = newNewFragmentInstance(NewMyAccountAddressesFragment.class, bundle);
                //fragment = newFragmentInstance(MyAccountAddressesFragment.class, bundle);
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
        if (!isNewFragment) {
            fragmentManagerTransition(R.id.app_content, fragment, type, addToBackStack);
        }
        else
        {
            fragmentManagerTransition(R.id.app_content, newFragment, type, addToBackStack);
        }
    }

    /**
     * Create new fragment
     */
    private  BaseFragment newFragmentInstance(@NonNull Class<? extends BaseFragment> fragmentClass, @Nullable Bundle arguments) {
        return BaseFragment.newInstance(getApplicationContext(), fragmentClass, arguments);
    }

    private  NewBaseFragment newNewFragmentInstance(@NonNull Class<? extends NewBaseFragment> fragmentClass, @Nullable Bundle arguments) {
        return NewBaseFragment.newInstance(getApplicationContext(), fragmentClass, arguments);
    }

    /**
     * Fragment communication.<br>
     * The FragmentManager has some issues to get fragment with the same tag.<br>
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



}
