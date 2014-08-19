/**
 * 
 */
package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.BundleConstants;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.tracking.Ad4PushTracker;
import pt.rocket.framework.utils.EventType;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.PreferenceListFragment.OnPreferenceAttachedListener;
import pt.rocket.view.fragments.BaseFragment;
import pt.rocket.view.fragments.CampaignsFragment;
import pt.rocket.view.fragments.CatalogFragment;
import pt.rocket.view.fragments.CategoriesColletionFragment;
import pt.rocket.view.fragments.ChangeCountryFragment;
import pt.rocket.view.fragments.CheckoutAboutYouFragment;
import pt.rocket.view.fragments.CheckoutCreateAddressFragment;
import pt.rocket.view.fragments.CheckoutEditAddressFragment;
import pt.rocket.view.fragments.CheckoutExternalPaymentFragment;
import pt.rocket.view.fragments.CheckoutMyAddressesFragment;
import pt.rocket.view.fragments.CheckoutMyOrderFragment;
import pt.rocket.view.fragments.CheckoutPaymentMethodsFragment;
import pt.rocket.view.fragments.CheckoutPollAnswerFragment;
import pt.rocket.view.fragments.CheckoutShippingMethodsFragment;
import pt.rocket.view.fragments.CheckoutThanksFragment;
import pt.rocket.view.fragments.CheckoutWebFragment;
import pt.rocket.view.fragments.ChooseCountryFragment;
import pt.rocket.view.fragments.FavouritesFragment;
import pt.rocket.view.fragments.HeadlessAddToCartFragment;
import pt.rocket.view.fragments.HomeFragment;
import pt.rocket.view.fragments.MyAccountEmailNotificationFragment;
import pt.rocket.view.fragments.MyAccountFragment;
import pt.rocket.view.fragments.MyAccountUserDataFragment;
import pt.rocket.view.fragments.PopularityFragment;
import pt.rocket.view.fragments.ProductDetailsDescriptionFragment;
import pt.rocket.view.fragments.ProductDetailsFragment;
import pt.rocket.view.fragments.ProductImageGalleryFragment;
import pt.rocket.view.fragments.RecentSearchFragment;
import pt.rocket.view.fragments.RecentlyViewedFragment;
import pt.rocket.view.fragments.ReviewFragment;
import pt.rocket.view.fragments.SessionForgotPasswordFragment;
import pt.rocket.view.fragments.SessionLoginFragment;
import pt.rocket.view.fragments.SessionRegisterFragment;
import pt.rocket.view.fragments.SessionTermsFragment;
import pt.rocket.view.fragments.ShoppingCartFragment;
import pt.rocket.view.fragments.TrackOrderFragment;
import pt.rocket.view.fragments.WriteReviewFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.widget.DrawerLayout;

import com.ad4screen.sdk.Tag;

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

    /**
     * Constructor
     */
    public MainFragmentActivity() {
        super(NavigationAction.Unknown,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                0, R.layout.teasers_main_fragment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
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
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.BaseActivity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "ON NEW INTENT");
        super.onNewIntent(intent);
        // Parse deep link from splashscreen
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
     * @see pt.rocket.utils.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");

        // AD4Push activity tracking for in-app messages
        Ad4PushTracker.startActivity(this);        
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.BaseActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
//        JumiaApplication.COMPONENTS.get(UrbanAirshipComponent.class).setUserPushSettings();

        // AD4Push activity tracking for in-app messages
        Ad4PushTracker.stopActivity(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.BaseActivity#onDestroy()
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
        try {
            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            currentFragmentType = FragmentType.valueOf(tag);
        } catch (Exception e) {
            Log.w(TAG, "ERROR ON GET CURRENT FRAGMENT TYPE", e);
        }
        // Save the current fragment type on orientation change
        outState.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, currentFragmentType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.utils.BaseActivity#onSwitchFragment(pt.rocket.view.fragments
     * .FragmentType, android.os.Bundle, java.lang.Boolean)
     */
    @Override
    public void onSwitchFragment(FragmentType type, Bundle bundle, Boolean addToBackStack) {
        showWarningVariation(false);

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
            fragment = CategoriesColletionFragment.getInstance(bundle);
            break;
        case PRODUCT_LIST:
            fragment = CatalogFragment.getInstance();
            break;
        case PRODUCT_DETAILS:
            fragment = ProductDetailsFragment.getInstance(bundle);
            break;
        case PRODUCT_DESCRIPTION:
            fragment = ProductDetailsDescriptionFragment.getInstance();
            break;
        case PRODUCT_GALLERY:
            fragment = ProductImageGalleryFragment.getInstance(bundle);
            break;
        case POPULARITY:
            fragment = PopularityFragment.getInstance();
            break;
        case WRITE_REVIEW:
            fragment = WriteReviewFragment.getInstance();
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
        case TRACK_ORDER:
            fragment = TrackOrderFragment.getInstance();
            break;
        case CHANGE_COUNTRY:
            fragment = ChangeCountryFragment.getInstance();
            break;
        case CHOOSE_COUNTRY:
            fragment = ChooseCountryFragment.getInstance();
            break;
        case HEADLESS_CART:
            fragment = HeadlessAddToCartFragment.getInstance();
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
            fragment = CheckoutThanksFragment.getInstance();
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
        case FAVOURITE_LIST:
            fragment = FavouritesFragment.getInstance();
            break;
        case RECENTSEARCHES_LIST:
            fragment = RecentSearchFragment.newInstance();
            break;
        case RECENTLYVIEWED_LIST:
            fragment = RecentlyViewedFragment.getInstance();
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
        fragmentManagerTransition(R.id.main_fragment_container, fragment, type.toString(), addToBackStack);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Log.i(TAG, "ON BACK PRESSED");
        fragment = getActiveFragment();
        if (mDrawerLayout.isDrawerOpen(mDrawerNavigation) && !(mDrawerLayout.getDrawerLockMode(mDrawerNavigation) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {

            mDrawerLayout.closeDrawer(mDrawerNavigation);
        } else if (fragment == null || !fragment.allowBackPressed()) {
            Log.i(TAG, "NOT ALLOW BACK PRESSED: FRAGMENT");
            fragmentManagerBackPressed();
        } else {
            Log.i(TAG, "ALLOW BACK PRESSED: FRAGMENT");
        }
    }

    public BaseFragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * Pop back stack
     * 
     * @param tag
     * @param isInclusive
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
    
}
