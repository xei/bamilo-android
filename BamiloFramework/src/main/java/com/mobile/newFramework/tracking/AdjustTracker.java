package com.mobile.newFramework.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.mobile.framework.R;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.checkout.PurchaseItem;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerGender;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.gtm.GTMKeys;
import com.mobile.newFramework.tracking.gtm.GTMManager;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nunocastro
 */
public class AdjustTracker extends AbcBaseTracker {

    private final static String TAG = AdjustTracker.class.getSimpleName();

    public static final String BEGIN_TIME = "beginTime";
    public static final String USER_ID = "userId";
    public static final String CURRENCY_ISO = "currencyIso";
    public static final String COUNTRY_ISO = "countryIso";
    public static final String CUSTOMER = "customer";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TRANSACTION_ITEM_SKUS = "transactionItemSkus";
    public static final String TRANSACTION_VALUE = "transactionValue";
    public static final String PRODUCT = "product";
    public static final String PRODUCT_SKU = "productSku";
    public static final String VALUE = "value";
    public static final String IS_GUEST_CUSTOMER = "isGuestCustomer";
    public static final String IS_FIRST_CUSTOMER = "isFirstCustomer";
    public static final String SEARCH_TERM = "searchTerm";
    public static final String CART = "cart";
    public static final String DEVICE = "device";
    public static final String CATEGORY = "category";
    public static final String MAIN_CATEGORY = "main_category";
    public static final String CATEGORY_ID = "categoryId";
    public static final String BRAND_ID = "brand_id";
    public static final String TREE = "tree";
    public final static String ADJUST_PREFERENCES = "AdjustPreferences";
    public final static String PURCHASE_NUMBER = "aggregatedNumberOfPurchases";

    private static class AdjustKeys {
        private static final String SHOP_COUNTRY = "shop_country";
        private static final String APP_VERSION = "app_version";
        private static final String DISPLAY_SIZE = "display_size";
        private static final String USER_ID = "user_id";
        private static final String DURATION = "duration";
        private static final String DEVICE = "device";
        private static final String DEVICE_MANUFACTURER = "device_manufacturer";
        private static final String DEVICE_MODEL = "device_model";
        private static final String TRANSACTION_ID = "transaction_id";
        private static final String CURRENCY_CODE = "currency_code";
        private static final String CURRENCY = "currency";
        private static final String PRICE = RestConstants.PRICE;
        private static final String CATEGORY_ID = "category_id";
        private static final String QUANTITY = RestConstants.QUANTITY;
        private static final String GENDER = RestConstants.GENDER;
        private static final String SKU = RestConstants.SKU;
        private static final String SKUS = "skus";
        private static final String PRODUCTS = RestConstants.PRODUCTS;
        private static final String PRODUCT = RestConstants.PRODUCT;
        private static final String KEYWORDS = "keywords";
        private static final String NEW_CUSTOMER = "new_customer";
        private static final String APP_PRE_INSTALL = Constants.INFO_PRE_INSTALL;
        private static final String DEVICE_SIM_OPERATOR = Constants.INFO_SIM_OPERATOR;
        // New Adjust Facebook Audience keys
        private static final String FB_VALUE_TO_SUM = "_valueToSum";
        private static final String FB_CONTENT_ID = "fb_content_id";
        private static final String FB_CONTENT_TYPE = "fb_content_type";
        private static final String FB_CURRENCY = "fb_currency";
        private static final String FB_CONTENT_CATEGORY = "content_category";
    }

    private static final String TABLET = "Tablet";
    private static final String PHONE = "Phone";

    private static boolean isEnabled = false;

    private static AdjustTracker sInstance;

    public final static String ADJUST_FIRST_TIME_KEY = "adjust_first_time";

    private static Context mContext;

    private static final String EURO_CURRENCY = "EUR";

    private static final String PRODUCT_CONTENT_TYPE = "product";

    private static final String PRICE_DECIMAL_FORMAT = "0.00";

    public static AdjustTracker get() {
        if (sInstance == null) {
            sInstance = new AdjustTracker();
        }
        return sInstance;
    }

    public static void startup(Context context) {
        Print.d(TAG, "Adjust Startup");
        sInstance = new AdjustTracker(context);
    }

    public AdjustTracker() {
        isEnabled = false;
        if (mContext != null) {
            isEnabled = mContext.getResources().getBoolean(R.bool.adjust_enabled);
        }
    }

    public AdjustTracker(Context context) {
        super();
        isEnabled = context.getResources().getBoolean(R.bool.adjust_enabled);
        mContext = context;
        if (isEnabled) {
            initAdjustInstance();
        }

    }

    private void initAdjustInstance() {
        // Where the 2nd parameter is update should be set to
        // true if the App has been run before and false if it has
        // never been run before. Setting this correctly will allow
        // us to distinguish which installs are current users
        // updating to include the Ad-X SDK from new organic
        // downloads. The third parameter sets the logging level for
        // debugging. Set to zero for production version of the App.
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(ADJUST_FIRST_TIME_KEY, false);
        editor.apply();
        Print.i(TAG, "ADJUST is APP_LAUNCH " + Adjust.isEnabled());
    }

    /**
     * initialized Adjust tracker
     */
    public static void initializeAdjust(final Context context) {
        // Get adjust environment and log level
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        LogLevel logLevel = LogLevel.INFO;
        // Case dev
        if (!context.getResources().getBoolean(R.bool.adjust_is_production_env)) {
            environment = AdjustConfig.ENVIRONMENT_SANDBOX;
            //logLevel = LogLevel.VERBOSE;
        }
        initializeAdjust(context, environment, logLevel);
    }

    /**
     * Initialized Adjust tracker
     */
    private static void initializeAdjust(final Context context, String environment, LogLevel logLevel) {
        // Create adjust config
        AdjustConfig config = new AdjustConfig(context, getAppToken(context), environment);
        config.setLogLevel(logLevel);
        // Set pre install default tracker
        if (!TextUtils.isEmpty(context.getString(R.string.adjust_default_tracker))) {
            config.setDefaultTracker(context.getString(R.string.adjust_default_tracker));
        }
        // Set listener
        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                AdjustTracker.saveReAttributionInfoForGTM(context, attribution.adgroup, attribution.network, attribution.campaign, attribution.creative);
            }
        });
        // Create adjust using configs
        Adjust.onCreate(config);
    }

    /**
     * Get adjust app token
     */
    private static String getAppToken(Context context) {
        return context.getString(R.string.adjust_app_token);
    }

    /*
     * ######### BASE TRACKER #########
     */

    @Override
    public String getId(@NonNull Context context) {
        return getAppToken(context);
    }

    @Override
    public void debugMode(@NonNull Context context, boolean enable) {
        if (enable) {
            Print.w(TAG, "WARNING: DEBUG MODE IS ENABLE");
            initializeAdjust(context, AdjustConfig.ENVIRONMENT_SANDBOX, LogLevel.VERBOSE);
        } else {
            Print.w(TAG, "WARNING: DEBUG MODE IS DISABLE");
            initializeAdjust(context, AdjustConfig.ENVIRONMENT_SANDBOX, LogLevel.INFO);
        }
    }

    /*
     * ######### TRACKER #########
     */

    public static void onResume() {
        if (Adjust.isEnabled()) {
            Adjust.onResume();
        }

    }

    public static void onPause() {
        if (Adjust.isEnabled()) {
            Adjust.onPause();
        }

    }

    public void trackScreen(TrackingPage screen, @NonNull Bundle bundle) {
        if (!isEnabled) {
            return;
        }
        Print.i(TAG, " Tracked Screen --> " + screen);
        switch (screen) {
            case HOME:
                //didn't used the baseParameters because some of the parameters aren't used on this event ,eg (SHOP_COUNTRY)
                AdjustEvent eventHomeScreen = new AdjustEvent(mContext.getString(R.string.adjust_token_home));
                addUserIdParameters(eventHomeScreen, (Customer) bundle.getParcelable(CUSTOMER), bundle.getString(USER_ID));
                addAppVersionParameters(eventHomeScreen);
                addDisplayParameters(eventHomeScreen);
                addDeviceManufacturerParameters(eventHomeScreen);
                addDeviceModelParameters(eventHomeScreen);
                addDurationParameters(eventHomeScreen, bundle);
                addCustomerGenderParameters(eventHomeScreen, (Customer) bundle.getParcelable(CUSTOMER));
                Adjust.trackEvent(eventHomeScreen);
                break;

            case PRODUCT_DETAIL_LOADED:
                AdjustEvent eventPDVScreen = new AdjustEvent(mContext.getString(R.string.adjust_token_view_product));
                addBaseParameters(eventPDVScreen, bundle);
                addCustomerGenderParameters(eventPDVScreen, (Customer) bundle.getParcelable(CUSTOMER));

                ProductComplete product = bundle.getParcelable(PRODUCT);
                if (product != null) {
                    eventPDVScreen.addCallbackParameter(AdjustKeys.PRODUCT, product.getSku());
                    eventPDVScreen.addPartnerParameter(AdjustKeys.PRODUCT, product.getSku());
                    eventPDVScreen.addCallbackParameter(AdjustKeys.CATEGORY_ID, product.getCategoryId());
                    eventPDVScreen.addPartnerParameter(AdjustKeys.CATEGORY_ID, product.getCategoryId());
                    if (product.getBrandId() != 0) {
                        eventPDVScreen.addCallbackParameter(BRAND_ID, String.valueOf(product.getBrandId()));
                        eventPDVScreen.addPartnerParameter(BRAND_ID, String.valueOf(product.getBrandId()));
                    }

                    // FB - View Product
                    AdjustEvent eventPDVScreenFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_view_product));
                    // format sku as array
                    String fbSkuList = convertParameterToStringArray(product.getSku());
                    //format price with 2 c.d.
                    String formattedPrice = formatPriceForTracking(product.getPriceForTracking(), PRICE_DECIMAL_FORMAT);
                    eventPDVScreenFB = getFBTrackerBaseParameters(eventPDVScreenFB, bundle);
                    eventPDVScreenFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                    eventPDVScreenFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                    eventPDVScreenFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, fbSkuList);
                    eventPDVScreenFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, fbSkuList);
                    // Tracking event
                    Adjust.trackEvent(eventPDVScreenFB);
                }
                // Tracking event
                Adjust.trackEvent(eventPDVScreen);
                break;

            case PRODUCT_LIST_SORTED:  //View Listing
                Print.d("ADJUST", "PRODUCT_LIST_SORTED");
                AdjustEvent eventCatalogSorted = new AdjustEvent(mContext.getString(R.string.adjust_token_view_listing));
                addBaseParameters(eventCatalogSorted, bundle);

                addCustomerGenderParameters(eventCatalogSorted, (Customer) bundle.getParcelable(CUSTOMER));


                if (bundle.containsKey(AdjustKeys.CATEGORY_ID)) {
                    // Add category ID
                    eventCatalogSorted.addCallbackParameter(AdjustKeys.CATEGORY_ID, bundle.getString(AdjustKeys.CATEGORY_ID));
                    eventCatalogSorted.addPartnerParameter(AdjustKeys.CATEGORY_ID, bundle.getString(AdjustKeys.CATEGORY_ID));
                }
                if (bundle.containsKey(BRAND_ID)) {
                    // Add brand ID
                    eventCatalogSorted.addCallbackParameter(BRAND_ID, bundle.getString(BRAND_ID));
                    eventCatalogSorted.addPartnerParameter(BRAND_ID, bundle.getString(BRAND_ID));
                }


                ArrayList<ProductRegular> skus = bundle.getParcelableArrayList(TRANSACTION_ITEM_SKUS);
                StringBuilder sbSkus;
                sbSkus = new StringBuilder();
                if (skus.size() > 0) {
                    sbSkus.append("[");
                    final int skusLimit = 3;
                    int skusCount = 0;

                    for (ProductRegular sku : skus) {
                        sbSkus.append(sku.getSku()).append(",");
                        skusCount++;
                        if (skusLimit <= skusCount) {
                            break;
                        }
                    }
                    sbSkus.setLength(sbSkus.length() - 1);
                    sbSkus.append("]");
                    eventCatalogSorted.addCallbackParameter(AdjustKeys.PRODUCTS, sbSkus.toString());
                    eventCatalogSorted.addPartnerParameter(AdjustKeys.PRODUCTS, sbSkus.toString());
                    Adjust.trackEvent(eventCatalogSorted);
                }
                //FB - View Listing
                AdjustEvent eventCatalogSortedFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_view_listing));

                eventCatalogSortedFB.addCallbackParameter(AdjustKeys.APP_VERSION, getAppVersion());
                eventCatalogSortedFB.addPartnerParameter(AdjustKeys.APP_VERSION, getAppVersion());
                eventCatalogSortedFB.addCallbackParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
                eventCatalogSortedFB.addPartnerParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
                eventCatalogSortedFB.addCallbackParameter(AdjustKeys.FB_CONTENT_CATEGORY, bundle.getString(MAIN_CATEGORY));
                eventCatalogSortedFB.addPartnerParameter(AdjustKeys.FB_CONTENT_CATEGORY, bundle.getString(MAIN_CATEGORY));

                Adjust.trackEvent(eventCatalogSortedFB);
                break;

            case CART_LOADED:
                AdjustEvent eventCartLoaded = new AdjustEvent(mContext.getString(R.string.adjust_token_view_cart));
                addBaseParameters(eventCartLoaded, bundle);

                addCustomerGenderParameters(eventCartLoaded, (Customer) bundle.getParcelable(CUSTOMER));

                Adjust.trackEvent(eventCartLoaded);
                break;

            default:

                break;
        }
    }

    public static void saveReAttributionInfoForGTM(@Nullable Context context, String adGroup, String network, String campaign, String creative) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(GTMKeys.INSTALLNETWORK, network);
            editor.putString(GTMKeys.INSTALLADGROUP, adGroup);
            editor.putString(GTMKeys.INSTALLCAMPAIGN, campaign);
            editor.putString(GTMKeys.INSTALLCREATIVE, creative);
            editor.apply();
        }
    }


    /**
     * Return a string representing an array of string parameters  - only for facebook tracking purposes
     */
    private String convertParameterToStringArray(String parameter) {
        return "[\"" + parameter + "\"]";
    }


    /**
     * Return a string representing an array of string parameters provided in a List parameter - only for facebook tracking purposes
     */
    private String convertListParameterToStringArray(List<String> parametersList) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(parametersList)) {
            sb.append("[");
            for (String sku : parametersList) {
                sb.append("\"").append(sku).append("\"").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }
        return sb.toString();
    }


    /**
     * Returns a string price formated with format
     */
    public String formatPriceForTracking(double priceForTracking, String format) {
        return new DecimalFormat(format).format(priceForTracking);

    }


    public void trackEvent(TrackingEvent eventTracked, Bundle bundle) {
        if (!isEnabled) {
            return;
        }
        Print.i(TAG, " Tracked Event --> " + eventTracked);

        switch (eventTracked) {

            case APP_OPEN:
                Print.i(TAG, "APP_OPEN:" + Adjust.isEnabled());
                AdjustEvent eventAppOpen = new AdjustEvent(mContext.getString(R.string.adjust_token_launch));
                addAppVersionParameters(eventAppOpen);
                addDisplayParameters(eventAppOpen);
                addDurationParameters(eventAppOpen, bundle);
                addDeviceManufacturerParameters(eventAppOpen);
                addDeviceModelParameters(eventAppOpen);
                addDeviceTypeParameters(eventAppOpen, bundle);
                addPreInstallParameters(eventAppOpen, bundle);
                addSimOperatorParameters(eventAppOpen, bundle);
                Adjust.trackEvent(eventAppOpen);
                break;

            case LOGIN_SUCCESS:
                AdjustEvent eventLogin = new AdjustEvent(mContext.getString(R.string.adjust_token_log_in));
                addBaseParameters(eventLogin, bundle);
                Adjust.trackEvent(eventLogin);
                break;

            case LOGOUT_SUCCESS:
                AdjustEvent eventLogout = new AdjustEvent(mContext.getString(R.string.adjust_token_log_out));
                addBaseParameters(eventLogout, bundle);
                Adjust.trackEvent(eventLogout);
                break;

            case SIGNUP_SUCCESS:
                AdjustEvent eventSignUp = new AdjustEvent(mContext.getString(R.string.adjust_token_sign_up));
                addBaseParameters(eventSignUp, bundle);
                Adjust.trackEvent(eventSignUp);
                break;

            case CHECKOUT_FINISHED: // Sale
                try {

                    String order = bundle.getString(TRANSACTION_ID);
                    String country = bundle.getString(AdjustTracker.COUNTRY_ISO, NOT_AVAILABLE);

                    Print.d(TAG, " TRACK REVENEU --> " + bundle.getDouble(TRANSACTION_VALUE));
                    increaseTransactionCount();

                    if (bundle.getBoolean(IS_FIRST_CUSTOMER)) {
                        AdjustEvent eventFirstCustomer = new AdjustEvent(mContext.getString(R.string.adjust_token_customer));
                        addBaseParameters(eventFirstCustomer, bundle);
                        Adjust.trackEvent(eventFirstCustomer);
                    }

                    //convert list to an array in a string
                    String skuList = convertListParameterToStringArray(bundle.getStringArrayList(TRANSACTION_ITEM_SKUS));
                    //format price with 2 c.d.
                    String formattedPrice = formatPriceForTracking(bundle.getDouble(TRANSACTION_VALUE), PRICE_DECIMAL_FORMAT);

                    // Track Revenue (Sale or Guest Sale)
                    String eventString = bundle.getBoolean(IS_GUEST_CUSTOMER) ? mContext.getString(R.string.adjust_token_guest_sale) : mContext.getString(R.string.adjust_token_sale);
                    AdjustEvent eventRevenue = new AdjustEvent(eventString);
                    addBaseParameters(eventRevenue, bundle);
                    eventRevenue.addCallbackParameter(AdjustKeys.SKUS, skuList);
                    eventRevenue.addPartnerParameter(AdjustKeys.SKUS, skuList);
                    eventRevenue.addCallbackParameter(AdjustKeys.TRANSACTION_ID, order);
                    eventRevenue.addPartnerParameter(AdjustKeys.TRANSACTION_ID, order);
                    eventRevenue.setRevenue(bundle.getDouble(TRANSACTION_VALUE), EURO_CURRENCY);
                    Adjust.trackEvent(eventRevenue);

                    AdjustEvent eventTransaction = new AdjustEvent(mContext.getString(R.string.adjust_token_transaction_confirmation));
                    addBaseParameters(eventTransaction, bundle);
                    eventTransaction.addCallbackParameter(AdjustKeys.TRANSACTION_ID, order);
                    eventTransaction.addPartnerParameter(AdjustKeys.TRANSACTION_ID, order);
                    eventTransaction.addCallbackParameter(AdjustKeys.NEW_CUSTOMER, String.valueOf(bundle.getBoolean(IS_GUEST_CUSTOMER)));
                    eventTransaction.addPartnerParameter(AdjustKeys.NEW_CUSTOMER, String.valueOf(bundle.getBoolean(IS_GUEST_CUSTOMER)));


                    addCustomerGenderParameters(eventTransaction, (Customer) bundle.getParcelable(CUSTOMER));

                    ArrayList<PurchaseItem> cartItems = bundle.getParcelableArrayList(CART);
                    JSONObject json;
                    int productCount = 0;
                    String countString = "";
                    for (PurchaseItem item : cartItems) {

                        json = new JSONObject();
                        try {
                            json.put(AdjustKeys.SKU, item.sku);
                            json.put(AdjustKeys.CURRENCY, EURO_CURRENCY);
                            json.put(AdjustKeys.QUANTITY, item.quantity);
                            json.put(AdjustKeys.PRICE, item.getPriceForTracking());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        eventTransaction.addCallbackParameter(AdjustKeys.PRODUCT + countString, json.toString());
                        eventTransaction.addPartnerParameter(AdjustKeys.PRODUCT + countString, json.toString());
                        countString = String.valueOf(++productCount);

                    }
                    Adjust.trackEvent(eventTransaction);

                    AdjustEvent eventTransactionFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_transaction_confirmation));

                    eventTransactionFB = getFBTrackerBaseParameters(eventTransactionFB, bundle);
                    eventTransactionFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                    eventTransactionFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                    eventTransactionFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, skuList);
                    eventTransactionFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, skuList);
                    Adjust.trackEvent(eventTransactionFB);

                    // Cake-Adjust integration
                    trackCakePurchaseIntegration(order, country.toUpperCase(), cartItems);

                } catch (Exception e) {
                    // ADJUST INTERNAL CRASH FATAL EXCEPTION: Adjust java.util.ConcurrentModificationException
                    Print.w("WARNING: ON TRACKING CHECKOUT FINISHED");
                }
                break;

            case ADD_TO_CART:
                AdjustEvent eventAddToCart = new AdjustEvent(mContext.getString(R.string.adjust_token_add_to_cart));
                addBaseParameters(eventAddToCart, bundle);
                addProductSkuParameters(eventAddToCart, bundle);
                addPriceParameters(eventAddToCart, bundle);
                Adjust.trackEvent(eventAddToCart);

                AdjustEvent eventAddToCartFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_add_to_cart));
                //convert string to array string
                String skuArray = convertParameterToStringArray(bundle.getString(PRODUCT_SKU));
                // format price to 2 c.d.
                String formattedPrice = formatPriceForTracking(bundle.getDouble(VALUE), PRICE_DECIMAL_FORMAT);

                eventAddToCartFB = getFBTrackerBaseParameters(eventAddToCartFB, bundle);
                eventAddToCartFB.addCallbackParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                eventAddToCartFB.addPartnerParameter(AdjustKeys.FB_VALUE_TO_SUM, formattedPrice);
                eventAddToCartFB.addCallbackParameter(AdjustKeys.FB_CONTENT_ID, skuArray);
                eventAddToCartFB.addPartnerParameter(AdjustKeys.FB_CONTENT_ID, skuArray);
                Adjust.trackEvent(eventAddToCartFB);

                break;

            case REMOVE_FROM_CART:
                AdjustEvent eventRemoveFromCart = new AdjustEvent(mContext.getString(R.string.adjust_token_remove_from_cart));
                addBaseParameters(eventRemoveFromCart, bundle);
                addProductSkuParameters(eventRemoveFromCart, bundle);
                addPriceParameters(eventRemoveFromCart, bundle);
                Adjust.trackEvent(eventRemoveFromCart);
                break;

            case ADD_TO_WISHLIST:
                AdjustEvent eventAddToWishlist = new AdjustEvent(mContext.getString(R.string.adjust_token_add_to_wishlist));
                addBaseParameters(eventAddToWishlist, bundle);
                addProductSkuParameters(eventAddToWishlist, bundle);
                addPriceParameters(eventAddToWishlist, bundle);
                Adjust.trackEvent(eventAddToWishlist);
                break;

            case REMOVE_FROM_WISHLIST:
                AdjustEvent eventRemoveFromWishlist = new AdjustEvent(mContext.getString(R.string.adjust_token_remove_from_wishlist));
                addBaseParameters(eventRemoveFromWishlist, bundle);
                addProductSkuParameters(eventRemoveFromWishlist, bundle);
                addPriceParameters(eventRemoveFromWishlist, bundle);
                Adjust.trackEvent(eventRemoveFromWishlist);
                break;

            case SHARE:
                AdjustEvent eventShare = new AdjustEvent(mContext.getString(R.string.adjust_token_social_share));
                addBaseParameters(eventShare, bundle);
                addProductSkuParameters(eventShare, bundle);
                Adjust.trackEvent(eventShare);
                break;

            case CALL:
                AdjustEvent eventCall = new AdjustEvent(mContext.getString(R.string.adjust_token_call));
                addBaseParameters(eventCall, bundle);
                Adjust.trackEvent(eventCall);
                break;

            case ADD_REVIEW:
                AdjustEvent eventReview = new AdjustEvent(mContext.getString(R.string.adjust_token_product_rate));
                addBaseParameters(eventReview, bundle);
                addProductSkuParameters(eventReview, bundle);
                Adjust.trackEvent(eventReview);
                break;

            case LOGIN_FB_SUCCESS:
                AdjustEvent eventLoginFB = new AdjustEvent(mContext.getString(R.string.adjust_token_fb_connect));
                addBaseParameters(eventLoginFB, bundle);
                Adjust.trackEvent(eventLoginFB);
                break;
            case SEARCH:
                AdjustEvent eventSearch = new AdjustEvent(mContext.getString(R.string.adjust_token_search));
                addBaseParameters(eventSearch, bundle);
                eventSearch.addCallbackParameter(AdjustKeys.KEYWORDS, bundle.getString(SEARCH_TERM));
                eventSearch.addPartnerParameter(AdjustKeys.KEYWORDS, bundle.getString(SEARCH_TERM));

                addCustomerGenderParameters(eventSearch, (Customer) bundle.getParcelable(CUSTOMER));

                Adjust.trackEvent(eventSearch);
                break;
            default:
                break;
        }

    }

    public boolean enabled() {
        return true;
    }


    private void addBaseParameters(AdjustEvent event, Bundle bundle) {
        addCountryParameters(event, bundle);
        addUserIdParameters(event, (Customer) bundle.getParcelable(CUSTOMER), bundle.getString(USER_ID));
        addAppVersionParameters(event);
        addDisplayParameters(event);
        addDeviceManufacturerParameters(event);
        addDeviceModelParameters(event);
    }

    private AdjustEvent addCustomerGenderParameters(@NonNull AdjustEvent event, @Nullable Customer customer) {
        if (customer != null) {
            String gender = getGender(customer);
            if (!TextUtils.equals(gender, CustomerGender.UNKNOWN.name())) {
                event.addCallbackParameter(AdjustKeys.GENDER, gender);
                event.addPartnerParameter(AdjustKeys.GENDER, gender);
            }
        }
        return event;
    }

    private void addUserIdParameters(@NonNull AdjustEvent event, @Nullable Customer customer, @Nullable String id) {
        if (TextUtils.isNotEmpty(id)) {
            event.addCallbackParameter(AdjustKeys.USER_ID, id);
            event.addPartnerParameter(AdjustKeys.USER_ID, id);
        } else if (customer != null) {
            event.addCallbackParameter(AdjustKeys.USER_ID, customer.getIdAsString());
            event.addPartnerParameter(AdjustKeys.USER_ID, customer.getIdAsString());
        }
    }

    private void addAppVersionParameters(@NonNull AdjustEvent event) {
        event.addCallbackParameter(AdjustKeys.APP_VERSION, getAppVersion());
        event.addPartnerParameter(AdjustKeys.APP_VERSION, getAppVersion());
    }

    private void addDisplayParameters(@NonNull AdjustEvent event) {
        event.addCallbackParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
        event.addPartnerParameter(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
    }

    private void addDeviceManufacturerParameters(@NonNull AdjustEvent event) {
        event.addCallbackParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        event.addPartnerParameter(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
    }

    private void addDeviceModelParameters(@NonNull AdjustEvent event) {
        event.addCallbackParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);
        event.addPartnerParameter(AdjustKeys.DEVICE_MODEL, Build.MODEL);
    }

    private void addDeviceTypeParameters(@NonNull AdjustEvent event, @Nullable Bundle bundle) {
        if (bundle != null) {
            event.addCallbackParameter(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE)));
            event.addPartnerParameter(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE)));
        }
    }

    private void addDurationParameters(@NonNull AdjustEvent event, @Nullable Bundle bundle) {
        if (bundle != null && bundle.containsKey(BEGIN_TIME)) {
            event.addCallbackParameter(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
            event.addPartnerParameter(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
        }
    }

    private void addCountryParameters(@NonNull AdjustEvent event, @Nullable Bundle bundle) {
        if (bundle != null && bundle.containsKey(COUNTRY_ISO)) {
            event.addCallbackParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
            event.addPartnerParameter(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
        }
    }

    private void addPreInstallParameters(@NonNull AdjustEvent event, @Nullable Bundle bundle) {
        if (bundle != null) {
            event.addCallbackParameter(AdjustKeys.APP_PRE_INSTALL, String.valueOf(bundle.getBoolean(Constants.INFO_PRE_INSTALL)));
            event.addPartnerParameter(AdjustKeys.APP_PRE_INSTALL, String.valueOf(bundle.getBoolean(Constants.INFO_PRE_INSTALL)));
        }
    }

    private void addSimOperatorParameters(@NonNull AdjustEvent event, @Nullable Bundle bundle) {
        if (bundle != null && TextUtils.isNotEmpty(bundle.getString(Constants.INFO_SIM_OPERATOR))) {
            event.addCallbackParameter(AdjustKeys.DEVICE_SIM_OPERATOR, bundle.getString(Constants.INFO_SIM_OPERATOR));
            event.addPartnerParameter(AdjustKeys.DEVICE_SIM_OPERATOR, bundle.getString(Constants.INFO_SIM_OPERATOR));
        }
    }

    private void addProductSkuParameters(@NonNull AdjustEvent event, @Nullable Bundle bundle) {
        if (bundle != null) {
            event.addCallbackParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
            event.addPartnerParameter(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
        }
    }

    private void addPriceParameters(@NonNull AdjustEvent event, @Nullable Bundle bundle) {
        if (bundle != null && bundle.getDouble(VALUE) > 0d) {
            event.addCallbackParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
            event.addPartnerParameter(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
            event.addCallbackParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
            event.addPartnerParameter(AdjustKeys.CURRENCY_CODE, EURO_CURRENCY);
        }
    }

    /**
     * sets the SHOP_COUNTRY, APP_VERSION, FB_CONTENT_TYPE, FB_CURRENCY fields for the specific event.
     */

    private AdjustEvent getFBTrackerBaseParameters(AdjustEvent event, Bundle bundle) {
        addCountryParameters(event, bundle);
        addAppVersionParameters(event);
        event.addCallbackParameter(AdjustKeys.FB_CONTENT_TYPE, PRODUCT_CONTENT_TYPE);
        event.addPartnerParameter(AdjustKeys.FB_CONTENT_TYPE, PRODUCT_CONTENT_TYPE);
        event.addCallbackParameter(AdjustKeys.FB_CURRENCY, EURO_CURRENCY);
        event.addPartnerParameter(AdjustKeys.FB_CURRENCY, EURO_CURRENCY);

        return event;
    }

    private String getDuration(long begin) {
        return String.valueOf(System.currentTimeMillis() - begin);
    }

    private String getGender(Customer customer) {
        return customer != null ? customer.getGender() : "n.a";
    }

    private String getAppVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }

        if (pInfo == null) {
            return NOT_AVAILABLE;
        } else {
            return pInfo.versionName;
        }
    }

    private Float getScreenSizeInches() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return (float) Math.round(screenInches * 10) / 10;
    }

    private String getDeviceType(boolean isTablet) {
        return isTablet ? TABLET : PHONE;
    }

    private void increaseTransactionCount() {
        SharedPreferences settings = mContext.getSharedPreferences(ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        int purchasesNumber = settings.getInt(PURCHASE_NUMBER, 0);
        purchasesNumber = purchasesNumber + 1;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PURCHASE_NUMBER, purchasesNumber);
        editor.apply();
    }

    public static void resetTransactionCount(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AdjustTracker.ADJUST_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(AdjustTracker.PURCHASE_NUMBER, 0);
        editor.apply();
    }

    /**
     * Method used to set re attributions parameters from deep link.<br>
     * Example: - "cc/d?adjust_tracker=abc123&adjust_campaign=campaign01&adjust_adgroup=adgroup01&adjust_creative=creative01"<br>
     *
     * @see <a href="https://docs.adjust.com/en/deeplinking/#manually-appending-attribution-data-to-a-deep-link">Manually attribution to deep link</a><br>
     * <a href="https://github.com/adjust/android_sdk#13-set-up-deep-link-reattributions">Set up deep link reattributions</a>
     */
    public static void deepLinkReAttribution(@NonNull Uri uri) {
        Adjust.appWillOpenUrl(uri);
    }

    /**
     * Track the cake purchase, NAFAMZ-17210.<br>
     * - Not available for all.
     */
    private void trackCakePurchaseIntegration(String order, String country, ArrayList<PurchaseItem> items) {
        // Get token not available for all
        String token = mContext.getString(R.string.adjust_token_cake_integration);
        // Tracking
        if (CollectionUtils.isNotEmpty(items) && TextUtils.isNotEmpty(token)) {
            // Create event
            AdjustEvent event = new AdjustEvent(token);
            // Values
            String skus = "", qts = "", prcs = "", dcts = "";
            double sum = 0;
            boolean first = true;
            for (PurchaseItem item : items) {
                String spt = first ? "" : SEPARATOR_CARET;
                first = false;
                skus += spt + item.sku.split(SEPARATOR_HYPHEN)[CONFIG_SKU_POS];
                qts += spt + item.quantity;
                prcs += spt + item.price;
                dcts += spt + 0;
                sum += item.quantity * item.price;
            }
            // List of SKUs (sku_config)
            String key = mContext.getString(R.string.adjust_key_ecsk);
            event.addCallbackParameter(key, skus);
            event.addPartnerParameter(key, skus);
            // List of Quantities
            key = mContext.getString(R.string.adjust_key_ecqu);
            event.addCallbackParameter(key, qts);
            event.addPartnerParameter(key, qts);
            // List of Prices
            key = mContext.getString(R.string.adjust_key_ecpr);
            event.addCallbackParameter(key, prcs);
            event.addPartnerParameter(key, prcs);
            // List of Discounts
            key = mContext.getString(R.string.adjust_key_ecld);
            event.addCallbackParameter(key, dcts);
            event.addPartnerParameter(key, dcts);
            // Add Country Code
            key = mContext.getString(R.string.adjust_key_ecco);
            event.addCallbackParameter(key, country);
            event.addPartnerParameter(key, country);
            // Sub total without tax
            key = mContext.getString(R.string.adjust_key_ect);
            event.addCallbackParameter(key, String.valueOf(sum));
            event.addPartnerParameter(key, String.valueOf(sum));
            // Sub total 2
            key = mContext.getString(R.string.adjust_key_ecst);
            event.addCallbackParameter(key, String.valueOf(sum));
            event.addPartnerParameter(key, String.valueOf(sum));
            // Order id
            key = mContext.getString(R.string.adjust_key_t);
            event.addCallbackParameter(key, order);
            event.addPartnerParameter(key, order);
            // Track
            Adjust.trackEvent(event);
        }
    }

}
