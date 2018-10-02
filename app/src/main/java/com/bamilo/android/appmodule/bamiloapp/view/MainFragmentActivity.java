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
import android.util.Log;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.factories.EmarsysEventFactory;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.DeepLinkManager;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.tracking.emarsys.EmarsysTracker;
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
import com.bamilo.android.appmodule.bamiloapp.view.fragments.FrontPageFragment;
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
import com.bamilo.android.appmodule.bamiloapp.view.fragments.SessionRegisterFragment;
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
import com.bamilo.android.appmodule.modernbamilo.user.RegisterModalBottomSheet;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;

import static com.bamilo.android.appmodule.bamiloapp.view.fragments.FilterMainFragment.FILTER_TAG;

/**
 * @author sergiopereira
 */
public class MainFragmentActivity extends BaseActivity /*implements PushEventListener */{

    private final static String TAG = MainFragmentActivity.class.getSimpleName();
    private EmarsysEventFactory.OpenAppEventSourceType mAppOpenSource;

    private BaseFragment fragment;
    //DROID-63 private NewBaseFragment newFragment;
    //DROID-63 private boolean isNewFragment = false;

    private FragmentType mCurrentFragmentType;

    private boolean isInMaintenance = false;

    /**
     * Constructor
     */
    public MainFragmentActivity() {
        super(NavigationAction.UNKNOWN, EnumSet.noneOf(MyMenuItem.class),
                IntConstants.ACTION_BAR_NO_TITLE);
    }

//    //Registration receiver
//    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver() {
//        @Override
//        public void onRegisterActionReceive(Context context, Intent intent) {
//            checkMessage(intent);
//        }
//    };
//
//    //Push message receiver
//    private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
//        @Override
//        protected void onMessageReceive(Intent intent) {
//            //JSON_DATA_KEY contains JSON payload of push notification.
//            checkMessage(intent);
//            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
//        }
//    };

//    //Registration of the receivers
//    public void registerReceivers() {
//        IntentFilter intentFilter = new IntentFilter(
//                getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
//        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE",
//                null);
//        registerReceiver(mBroadcastReceiver,
//                new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
//    }
//
//    public void unregisterReceivers() {
////Unregister receivers on pause
//        try {
//            unregisterReceiver(mReceiver);
//        } catch (Exception e) {
//// pass.
//        }
//        try {
//            unregisterReceiver(mBroadcastReceiver);
//        } catch (Exception e) {
////pass through
//        }
//    }

//    private void checkMessage(Intent intent) {
//        if (null != intent) {
//            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
//                showMessage("push message is " + intent.getExtras()
//                        .getString(PushManager.PUSH_RECEIVE_EVENT));
//                MainEventModel appOpenedEventModel = new MainEventModel(null, null, null,
//                        SimpleEventModel.NO_VALUE,
//                        MainEventModel.createAppOpenEventModelAttributes(
//                                EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_PUSH_NOTIFICATION
//                                        .toString()));
//                TrackerManager.trackEvent(getApplicationContext(), EventConstants.AppOpened,
//                        appOpenedEventModel);
//                mAppOpenSource = EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_PUSH_NOTIFICATION;
//            } else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
//                showMessage("register");
//            } else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
//                showMessage("unregister");
//            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
//                showMessage("register error");
//            } else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
//                showMessage("unregister error");
//            } else if (intent.hasExtra(PushManager.REGISTER_BROAD_CAST_ACTION)) {
//                showMessage("REGISTER_BROAD_CAST_ACTION");
//            }
//            resetIntentValues();
//        }
//    }

//    private void resetIntentValues() {
//        Intent mainAppIntent = getIntent();
//        if (mainAppIntent != null) {
//            if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
//                mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
//            } else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
//                mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
//            } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
//                mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
//            } else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
//                mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
//            } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
//                mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
//            }
//            setIntent(mainAppIntent);
//        }
//    }

    private void showMessage(String message) {
        Log.i("AndroidBash", message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
//        checkMessage(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.d(TAG, "ON CREATE");

        //Init Pushwoosh fragment
//        PushFragment.init(this);
//        registerReceivers();
//        PushManager pushManager = PushManager.getInstance(this);

//        try {
//            pushManager.onStartup(this);
//        } catch (Exception ignored) {
//        }
//
//        pushManager.registerForPushNotifications();
//        checkMessage(getIntent());

        if (checkIntentsFromPDV()) {
            return;
        }

        if (savedInstanceState == null) {
            Print.d(TAG, "################### SAVED INSTANCE IS NULL");
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
                MainEventModel appOpenedEventModel = new MainEventModel(null, null, null,
                        SimpleEventModel.NO_VALUE,
                        MainEventModel.createAppOpenEventModelAttributes(
                                EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DEEPLINK
                                        .toString()));
                TrackerManager.trackEvent(getApplicationContext(), EventConstants.AppOpened,
                        appOpenedEventModel);
                mAppOpenSource = EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DEEPLINK;
            }
        } else {
            mCurrentFragmentType = (FragmentType) savedInstanceState
                    .getSerializable(ConstantsIntentExtra.FRAGMENT_TYPE);
            Print.d(TAG, "################### SAVED INSTANCE ISN'T NULL: " + mCurrentFragmentType);
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
            } else {
                Print.d(TAG, "COULDN'T RECOVER BACK STACK");
            }
        }

        TrackerManager.addTracker(EmarsysTracker.getInstance());
//        TrackerManager.addTracker(PushWooshTracker.getInstance(this));
        // TODO: 8/28/18 farshid
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

    /*
     * (non-Javadoc)
     *
     * @see <com.bamilo.android.framework.components.com.bamilo.android.appmodule.bamiloapp.utils.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();

        Print.d(TAG, "ON RESUME");
//        registerReceivers();

        //Clear application badge number
//        PushManager.getInstance(BamiloApplication.INSTANCE).setBadgeNumber(0);

        if (mAppOpenSource
                != EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_PUSH_NOTIFICATION
                && mAppOpenSource
                != EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DEEPLINK) {
            MainEventModel appOpenedEventModel = new MainEventModel(null, null, null,
                    SimpleEventModel.NO_VALUE,
                    MainEventModel.createAppOpenEventModelAttributes(
                            EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_DIRECT
                                    .toString()));
            TrackerManager.trackEvent(getApplicationContext(), EventConstants.AppOpened,
                    appOpenedEventModel);
        }
        mAppOpenSource = EmarsysEventFactory.OpenAppEventSourceType.OPEN_APP_SOURCE_NONE;

        EmarsysTracker.getInstance().trackEventAppLogin(
                Integer.parseInt(getApplicationContext().getResources()
                        .getString(R.string.Emarsys_ContactFieldID)),
                BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see <com.bamilo.android.framework.components.com.bamilo.android.appmodule.bamiloapp.utils.BaseActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        //PushWooshTracker.openApp(,true);
//        unregisterReceivers();
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
     * @see <com.bamilo.android.framework.components.com.bamilo.android.appmodule.bamiloapp.utils.BaseActivity#onDestroy()
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
            String tag = getSupportFragmentManager()
                    .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                    .getName();
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
     * <com.bamilo.android.framework.components.com.bamilo.android.appmodule.bamiloapp.utils.BaseActivity#onSwitchFragment(com.mobile.view.fragments
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
                if (FragmentController.getInstance().hasEntry(FragmentType.HOME.toString())) {
                    popBackStack(FragmentType.HOME.toString());
                    return;
                }
                fragment = newFragmentInstance(FrontPageFragment.class, bundle);
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
                new RegisterModalBottomSheet().show(getSupportFragmentManager(), "register");
                return;
//                fragment = newFragmentInstance(NewSessionLoginMainFragment.class, bundle);

            case LOGIN_EMAIL:
                fragment = newFragmentInstance(SessionLoginEmailFragment.class, bundle);
                break;
            case REGISTER:
                fragment = newFragmentInstance(SessionRegisterFragment.class, bundle);
//                new RegisterModalBottomSheet().show(getSupportFragmentManager(), "register");
                break;
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
                Print.w(TAG, "INVALID FRAGMENT TYPE");
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

        Print.i(TAG, "ON SWITCH FRAGMENT: " + type);
        // Save the current state
        mCurrentFragmentType = type;

        fragmentManagerTransition(R.id.app_content, fragment, type, addToBackStack);

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

    /*
     * (non-Javadoc)
     *
     * @see <com.bamilo.android.framework.components.com.bamilo.android.appmodule.bamiloapp.utils.MyActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Print.i(TAG, "ON BACK PRESSED");
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
        String tag = getSupportFragmentManager()
                .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                .getName();
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

//    @Override
//    public void doOnRegistered(String registrationId) {
//        Log.i(TAG, "Registered for pushes: " + registrationId);
//    }
//
//    @Override
//    public void doOnRegisteredError(String errorId) {
//        Log.e(TAG, "Failed to register for pushes: " + errorId);
//    }
//
//    @Override
//    public void doOnMessageReceive(String message) {
//        Log.i(TAG, "Notification opened: " + message);
//    }
//
//    @Override
//    public void doOnUnregistered(final String message) {
//        Log.i(TAG, "Unregistered from pushes: " + message);
//    }
//
//    @Override
//    public void doOnUnregisteredError(String errorId) {
//        Log.e(TAG, "Failed to unregister from pushes: " + errorId);
//    }
}
