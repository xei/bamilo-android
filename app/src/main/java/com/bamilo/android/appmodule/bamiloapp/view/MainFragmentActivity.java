package com.bamilo.android.appmodule.bamiloapp.view;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.factories.EmarsysEventFactory;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.DeepLinkManager;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.tracking.ga.GATracker;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CampaignsFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CatalogFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ChangePhoneNumberFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CheckoutConfirmationFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CheckoutCreateAddressFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CheckoutEditAddressFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CheckoutExternalPaymentFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CheckoutFinishFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CheckoutShippingMethodsFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CheckoutThanksFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ChooseCountryFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ComboFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.EditProfileFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.FilterMainFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.HomeFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.InnerShopFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ItemTrackingFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.MobileVerificationFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.MyAccountAboutFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.MyAccountCreateAddressFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.MyAccountEditAddressFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.MyAccountFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.MyAccountNewslettersFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.NavigationCategoryFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.OrderCancellationFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.OrderCancellationSuccessFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ProductDetailsInfoFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ProductImageGalleryFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ProductOffersFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ProductSizeGuideFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.RecentSearchFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.RecentlyViewedFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ReviewFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ReviewWriteFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ReviewsFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.SessionForgotPasswordFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.SessionLoginEmailFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.StaticPageFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.StaticWebViewPageFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.VariationsFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.WishListFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.order.MyOrdersFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.order.OrderReturnCallFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.order.OrderReturnConditionsFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.order.OrderReturnStepsMain;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.NewCheckoutAddressesFragment;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.NewCheckoutPaymentMethodsFragment;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.NewMyAccountAddressesFragment;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.NewShoppingCartFragment;
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.ProductDetailActivity;
import com.bamilo.android.appmodule.bamiloapp.view.relatedproducts.RecommendProductsFragment;
import com.bamilo.android.appmodule.bamiloapp.view.subcategory.SubCategoryFilterFragment;
import com.bamilo.android.appmodule.modernbamilo.authentication.login.LoginDialogBottomSheet;
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.AuthenticationRepo;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents;
import com.bamilo.android.appmodule.modernbamilo.user.RegisterModalBottomSheet;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;

import static com.bamilo.android.appmodule.bamiloapp.view.fragments.FilterMainFragment.FILTER_TAG;

/**
 * @author sergiopereira
 */
public class MainFragmentActivity extends BaseActivity {

    private final static String TAG = MainFragmentActivity.class.getSimpleName();
    private EmarsysEventFactory.OpenAppEventSourceType mAppOpenSource;

    private BaseFragment fragment;

    private FragmentType mCurrentFragmentType;

    private boolean isInMaintenance = false;

    /**
     * Constructor
     */
    public MainFragmentActivity() {
        super(NavigationAction.UNKNOWN, EnumSet.noneOf(MyMenuItem.class),
                IntConstants.ACTION_BAR_NO_TITLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkIntentsFromPDV()) {
            return;
        }

        if (savedInstanceState == null) {
            // Initialize fragment controller
            FragmentController.getInstance().init();

            // Case invalid deep link goto HOME else goto deep link
            if (!DeepLinkManager.onSwitchToDeepLink(this, getIntent())) {
                Bundle args = getIntent().getExtras();
                if (args != null && args.containsKey(ConstantsIntentExtra.ORDER_NUMBER)) {
                    onSwitchFragment(FragmentType.ORDER_STATUS, args, true);
                } else {
                    onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
                            FragmentController.ADD_TO_BACK_STACK);
                }
            } else {
//                MainEventModel appOpenedEventModel = new MainEventModel(null, null, null,
//                        SimpleEventModel.NO_VALUE,
//                        MainEventModel.createAppOpenEventModelAttributes(
//                                EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DEEPLINK
//                                        .toString()));
//                TrackerManager.trackEvent(getApplicationContext(), EventConstants.AppOpened,
//                        appOpenedEventModel);
                EventTracker.INSTANCE.openApp(TrackingEvents.OpenAppType.DEEP_LINK);
                mAppOpenSource = EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DEEPLINK;
            }
        } else {
            mCurrentFragmentType = (FragmentType) savedInstanceState
                    .getSerializable(ConstantsIntentExtra.FRAGMENT_TYPE);
            fragment = (BaseFragment) getSupportFragmentManager()
                    .findFragmentByTag(mCurrentFragmentType.toString());
            if (null != fragment) {
                fragment.setActivity(this);
            }
            // Get FC back stack from saved state and get fragments from FM
            ArrayList<String> backStackTypes = savedInstanceState
                    .getStringArrayList(ConstantsIntentExtra.BACK_STACK);
            List<Fragment> originalFragments = this.getSupportFragmentManager().getFragments();
            if (!CollectionUtils.isEmpty(backStackTypes)) {
                FragmentController.getInstance()
                        .validateCurrentState(this, backStackTypes, originalFragments);
            }
        }

//        TrackerManager.addTracker(EmarsysTracker.getInstance());
        TrackerManager.addTracker(GATracker.getInstance());

        /*
         * Used for on back pressed
         */
        Intent splashScreenParams = getIntent();
        if (splashScreenParams != null && splashScreenParams.getExtras() != null) {
            isInMaintenance = splashScreenParams.getExtras()
                    .getBoolean(ConstantsIntentExtra.IN_MAINTANCE, false);
        }
    }

    private boolean checkIntentsFromPDV() {
        Intent intent = getIntent();

        if (intent == null) {
            return false;
        }

        if (intent.getSerializableExtra(ConstantsIntentExtra.FRAGMENT_TYPE)
                == FragmentType.MORE_RELATED_PRODUCTS) {
            onSwitchFragment(FragmentType.MORE_RELATED_PRODUCTS, null, true);
            return true;
        }

        if (intent.getSerializableExtra(ConstantsIntentExtra.FRAGMENT_TYPE)
                == FragmentType.SHOPPING_CART) {
            onSwitchFragment(FragmentType.SHOPPING_CART, null, true);
            return true;
        }

        if (!TextUtils.isEmpty(intent.getStringExtra("bread_crumb_target"))) {
            new TargetLink(getWeakBaseActivity(), intent.getStringExtra("bread_crumb_target"))
                    .addTitle(intent.getStringExtra("bread_crumb_title"))
                    .retainBackStackEntries()
                    .enableWarningErrorMessage()
                    .run();

            return true;
        }

        if (!TextUtils.isEmpty(intent.getStringExtra("seller_target"))) {
            @TargetLink.Type String target = intent.getStringExtra("seller_target");
            new TargetLink(getWeakBaseActivity(), target)
                    .enableWarningErrorMessage()
                    .retainBackStackEntries()
                    .run();

            return true;
        }

        Bundle loginBundle = intent.getBundleExtra("pdv_login_bundle");
        if (loginBundle != null && loginBundle
                .containsKey(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API)) {
            onSwitchFragment(FragmentType.LOGIN, intent.getBundleExtra("pdv_login_bundle"),
                    true);
            return true;
        }

        Bundle brandBundle = intent.getBundleExtra("pdv_brand_bundle");
        if (brandBundle != null) {
            onSwitchFragment(FragmentType.CATALOG_BRAND, intent.getBundleExtra("pdv_brand_bundle"),
                    true);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAppOpenSource
                != EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_PUSH_NOTIFICATION
                && mAppOpenSource
                != EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DEEPLINK) {
//            MainEventModel appOpenedEventModel = new MainEventModel(null, null, null,
//                    SimpleEventModel.NO_VALUE,
//                    MainEventModel.createAppOpenEventModelAttributes(
//                            EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DIRECT
//                                    .toString()));
//            TrackerManager.trackEvent(getApplicationContext(), EventConstants.AppOpened,
//                    appOpenedEventModel);
            EventTracker.INSTANCE.openApp(TrackingEvents.OpenAppType.LAUNCHER);
        }
        mAppOpenSource = EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_NONE;

//        EmarsysTracker.getInstance().trackEventAppLogin(
//                Integer.parseInt(getApplicationContext().getResources()
//                        .getString(R.string.Emarsys_ContactFieldID)),
//                BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);

        String userId = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : "UNKNOWN";
        EventTracker.INSTANCE.login(userId, TrackingEvents.LoginType.LOGIN_WITH_EMAIL, true);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> frags = new ArrayList<>();
        try {
            String tag = getSupportFragmentManager()
                    .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                    .getName();
            mCurrentFragmentType = FragmentType.getValue(tag);
            // Save the current back stack
            frags.addAll(FragmentController.getInstance().returnAllEntries());
        } catch (Exception ignored) {
        }
        // Save the current fragment type on orientation change
        outState.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, mCurrentFragmentType);
        // Save the current back stack history
        outState.putStringArrayList(ConstantsIntentExtra.BACK_STACK, frags);
    }

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
                if (FragmentController.getInstance().hasEntry(FragmentType.HOME.toString())) {
                    popBackStack(FragmentType.HOME.toString());
                    return;
                }
                fragment = newFragmentInstance(HomeFragment.class, bundle);
                break;
            case CATALOG_SELLER:
            case CATALOG_BRAND:
            case CATALOG_DEEP_LINK:
            case CATALOG_CATEGORY:
            case CATALOG_NOFILTER:
            case CATALOG:
                // Default
                removeEntries = false;
                // Get indications to remove old entries or not
                if (CollectionUtils
                        .containsKey(bundle, ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES)) {
                    removeEntries = bundle
                            .getBoolean(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                    bundle.remove(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                }

                // Put the target type
                bundle.putSerializable(ConstantsIntentExtra.TARGET_TYPE, type);
                // Create instance
                fragment = newFragmentInstance(CatalogFragment.class, bundle);
                ContentValues filterValues = bundle.getParcelable(FILTER_TAG);
                if (filterValues != null) {
                    removeEntries = filterValues.keySet().size() == 0;
                }
                // Put the type with unique identifier
                type = FragmentType.getUniqueIdentifier(FragmentType.CATALOG, fragment);
                break;
            case CATALOG_FILTER:
                removeEntries = false;
                // Get indications to remove old entries or not
                if (CollectionUtils
                        .containsKey(bundle, ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES)) {
                    removeEntries = bundle
                            .getBoolean(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                    bundle.remove(ConstantsIntentExtra.REMOVE_OLD_BACK_STACK_ENTRIES);
                }
                bundle.putSerializable(ConstantsIntentExtra.TARGET_TYPE, type);
                // Create instance
                fragment = newFragmentInstance(CatalogFragment.class, bundle);
                // Put the type with unique identifier
                type = FragmentType.getUniqueIdentifier(FragmentType.CATALOG, fragment);
                break;
            case PRODUCT_DETAILS:
                // Create instance
                ProductDetailActivity
                        .start(this, bundle.getString(ConstantsIntentExtra.CONTENT_ID));
                return;
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
                removeEntries = false;
                fragment = newFragmentInstance(MyAccountFragment.class, bundle);
                break;
            case MY_USER_DATA:
                fragment = newFragmentInstance(EditProfileFragment.class, bundle);
                break;
            case MY_ORDERS:
                fragment = newFragmentInstance(MyOrdersFragment.class, bundle);
                break;
            case ORDER_STATUS:
                fragment = newFragmentInstance(ItemTrackingFragment.class, bundle);
                break;
            case ORDER_CANCELLATION:
                fragment = newFragmentInstance(OrderCancellationFragment.class, bundle);
                break;
            case ORDER_CANCELLATION_SUCCESS:
                fragment = newFragmentInstance(OrderCancellationSuccessFragment.class, bundle);
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
                triggerAutoLogin(bundle);
//                new LoginDialogBottomSheet().show(getSupportFragmentManager(), "login");
//                fragment = newFragmentInstance(NewSessionLoginMainFragment.class, bundle);
                return;
            case LOGIN_EMAIL:
                fragment = newFragmentInstance(SessionLoginEmailFragment.class, bundle);
                break;
            case REGISTER:
                new RegisterModalBottomSheet().show(getSupportFragmentManager(), "register");
                return;
            case MOBILE_VERIFICATION:
                fragment = newFragmentInstance(MobileVerificationFragment.class, bundle);
                break;
            case CHANGE_PHONE_NUMBER_FRAGMENT:
                fragment = newFragmentInstance(ChangePhoneNumberFragment.class, bundle);
                break;
            case FORGOT_PASSWORD:
                fragment = newFragmentInstance(SessionForgotPasswordFragment.class, bundle);
                break;
            case CHECKOUT_MY_ADDRESSES:
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
                removeEntries = false;
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
            case CATEGORIES:
                fragment = newFragmentInstance(NavigationCategoryFragment.class, bundle);
                break;
            case MORE_RELATED_PRODUCTS:
                fragment = newFragmentInstance(RecommendProductsFragment.class, bundle);
                break;
            default:
                return;
        }
        // Clear search term
        if (type != FragmentType.CATALOG && type != FragmentType.FILTERS) {
            BamiloApplication.INSTANCE.setSearchedTerm("");
        }

        // Validate menu flag and pop entries until home
        if (removeEntries) {
            popBackStackEntriesUntilTag(FragmentType.HOME.toString());
        }

        // Save the current state
        mCurrentFragmentType = type;

        fragmentManagerTransition(R.id.app_content, fragment, type, addToBackStack);

    }

    private void triggerAutoLogin(Bundle bundle) {
        if (!BamiloApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
            LoginDialogBottomSheet.Companion.show(getSupportFragmentManager(), bundle, null);
            return;
        }

        showProgress();
        AuthenticationRepo.INSTANCE.autoLogin(this, new IResponseCallback() {
            @Override
            public void onRequestComplete(BaseResponse baseResponse) {
                dismissProgress();
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse
                        .getContentData();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();

                if (nextStepFromApi != FragmentType.UNKNOWN) {

                    FragmentType mParentFragmentType = (FragmentType) bundle
                            .getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
                    FragmentType mNextStepFromParent = (FragmentType) bundle
                            .getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
                    boolean isInCheckoutProcess = bundle
                            .getBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API);

                    CheckoutStepManager
                            .validateLoggedNextStep(MainFragmentActivity.this,
                                    isInCheckoutProcess,
                                    mParentFragmentType, mNextStepFromParent,
                                    nextStepFromApi,
                                    bundle);
                } else {
                    setupDrawerNavigation();
                }
            }

            @Override
            public void onRequestError(BaseResponse baseResponse) {
                dismissProgress();
                LoginDialogBottomSheet.Companion.show(getSupportFragmentManager(), bundle, null);
            }
        });
    }

    /**
     * Create new fragment
     */
    private BaseFragment newFragmentInstance(@NonNull Class<? extends BaseFragment> fragmentClass,
            @Nullable Bundle arguments) {
        return BaseFragment.newInstance(getApplicationContext(), fragmentClass, arguments);
    }

    /**
     * Fragment communication.<br> The FragmentManager has some issues to get fragment with the same
     * tag.<br>
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

    @Override
    public void onBackPressed() {
        if (FancyShowCaseView.isVisible(this)) {
            FancyShowCaseView.hideCurrent(this);
            return;
        }
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
            if (fragment.getTag().equals(FragmentType.CATALOG.toString())) {
                BamiloApplication.INSTANCE.setSearchedTerm("");
            }

            // Case navigation opened
            if (mDrawerLayout.isDrawerOpen(mDrawerNavigation) && !(
                    mDrawerLayout.getDrawerLockMode(mDrawerNavigation)
                            == DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
                mDrawerLayout.closeDrawer(mDrawerNavigation);
            }
            // Case fragment not allow back pressed
            else if (fragment == null || !fragment.allowBackPressed()) {
                // Hide Keyboard
                hideKeyboard();
                // Back
                fragmentManagerBackPressed();
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
            return null;
        }
        String tag = getSupportFragmentManager()
                .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                .getName();
        return getSupportFragmentManager().findFragmentByTag(tag);
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
