/**
 * 
 */
package pt.rocket.framework.tracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import pt.rocket.framework.R;
import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.utils.CurrencyFormatter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.analytics.Cart;
import com.ad4screen.sdk.analytics.Item;
import com.ad4screen.sdk.analytics.Lead;
import com.ad4screen.sdk.analytics.Purchase;

import de.akquinet.android.androlog.Log;

/**
 * @author nunocastro
 * 
 */
public class Ad4PushTracker {
    private final static String TAG = Ad4PushTracker.class.getSimpleName();

    private static boolean isEnabled = false;

    private static Ad4PushTracker sInstance;

    private static A4S mA4S;
    private static Context context;

    public static final String AD4PUSH_PREFERENCES = "Ad4PushPreferences";

    private static final String VIEW_STATE = "view";

    public static final String PURCHASE_NUMBER = "aggregatedNumberOfPurchases";
    private static final String PURCHASE_VALUE = "aggregatedValueOfPurchases";
    private static final String WISHLIST_NUMBER = "aggregatedNumberOfWishlistItems";

    private static final String STATUS_IN_APP = "statusInApp";
    private static final String SHARED_PRODUCT_COUNT = "shareCount";
    private static final String WISHLIST_STATUS = "wishlistStatus";
    // private static final String WISHLIST_VALUE = "wishlistValue";
    private static final String WISHLIST_DATE = "lastFavouritesProductDate";
    private static final String WISHLIST_PRODUCT = "lastFavouritesProduct";
    private static final String SHOP_COUNTRY = "shopCountry";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String ORDER_STATUS = "orderStatus";
    private static final String CHECKOUT_STARTED = "started";
    private static final String CHECKOUT_FINISHED = "done";
    // private static final String HAS_RATED_PRODUCT = "hasRatedProduct";
    private static final String USER_ID = "userID";
    private static final String USER_NAME = "firstName";
    private static final String USER_GENDER = "userGender";
    private static final String REGISTRATION = "registrationStatus";
    private static final String REGISTRATION_STARTED = "started";
    private static final String REGISTRATION_DONE = "done";
    private static final String LAST_SEARCH = "lastSearch";
    private static final String LAST_SEARCH_DATE = "lastSearchDate";
    private static final String PURCHASE_GRAND_TOTAL = "purchaseGrandTotal";
    private static final String CART_COUNTER = "cartStatus";
    private static final String CART_VALUE = "cartValue";
    private static final String AVERAGE_CART_VALUE = "avgCartValue";
    private static final String COUPON_STATUS = "couponStatus";
    private static final String LASTORDER_DATE = "lastOrderDate";
    private static final String FAVORITES_CART_COUNT = "lastMovedFromFavtoCart";
    private static final String PURCHASE_COUNTER_APP = "aggregatedNumberOfPurchase";
    private static final String MOST_VISITED_CATEGORY = "mostVisitedCategory";
    private static final String FILTER_BRAND_COUNT = "filterBrand";
    private static final String FILTER_COLOR_COUNT = "filterColor";
    private static final String FILTER_CATEGORY_COUNT = "filterCategory";
    private static final String FILTER_PRICE_COUNT = "filterPrice";
    private static final String CAMPAIGN_PAGEVIEW_COUNT = "campaignPageViewCount";

    private final String FILTER_BRANDS = "searchQuery";
    private final String FILTER_COLOR = "color_family";
    private final String FILTER_PRICE = "price";
    private final String FILTER_CATEGORIES = "productUrl";
    private final String FILTER_SIZE = "size";

    private static final String STATUS_PROSPECT = "Prospect";
    private static final String STATUS_CUSTOMER = "Customer";

    private static final int EVENT_LOGIN = 1001;
    private static final int EVENT_FACEBOOK_CONNECT = 1002;
    private static final int EVENT_FIRST_OPEN_APP = 1003;
    // private static final int EVENT_CREATE_LISTING_VERIFYED = 1004;
    private static final int EVENT_ADD_TO_WHISHLIST = 1005;

    private static final String HAS_OPENED_APP = "app_opened";

    // View States for In-App Messages
    private String HOME_VIEW = "HOME";
    private String CATEGORY_VIEW = "CATEGORY";
    // private String SUBCATEGORY_VIEW = "SUBCATEGORY";
    private String PRODUCT_VIEW = "PRODUCT";
    private String LOGIN_SIGNUP_VIEW = "ACCOUNT";
    private String FAVORITES_VIEW = "MYFAVORITES";
    private String CART_VIEW = "CART";
    // private String RATE_SELLER_VIEW = "RATESELLER";
    // private String RATE_BUYER_VIEW = "RATEBUYER";
    // private String INBOX_VIEW = "INBOX";
    // private String BUYNOW_VIEW = "BUYNOW";
    // private String SIGNUPP_ENDING_VIEW = "SIGNUPPENDING";
    // private String CREATE_LISTING_VIEW = "CREATELISTING";
    // private String CREATE_LISTING_DONE_VIEW = "CREATELISTINGDONE";

    HashMap<TrackingPage, String> screens;

    public static Ad4PushTracker get() {
        if (sInstance == null) {
            sInstance = new Ad4PushTracker();
        }
        return sInstance;
    }

    public static void startup(Context context) {
        Log.d(TAG, "Ad4PSUH Startup");

        sInstance = new Ad4PushTracker(context);
    }

    private Ad4PushTracker() {
        isEnabled = false;
    }

    private Ad4PushTracker(Context context) {

        isEnabled = context.getResources().getBoolean(R.bool.ad4push_enabled);

        Ad4PushTracker.context = context;

        screens = new HashMap<TrackingPage, String>();
        screens.put(TrackingPage.HOME, HOME_VIEW);
        screens.put(TrackingPage.PRODUCT_LIST, CATEGORY_VIEW);
        // screens.put(TrackingPage.SUB_CATEGORY, SUBCATEGORY_VIEW);
        screens.put(TrackingPage.PRODUCT_DETAIL, PRODUCT_VIEW);
        screens.put(TrackingPage.LOGIN_SIGNUP, LOGIN_SIGNUP_VIEW);
        screens.put(TrackingPage.FAVORITES, FAVORITES_VIEW);
        screens.put(TrackingPage.CART, CART_VIEW);

        // screens.put(TrackingScreen.RATINGS_SELLER, RATE_SELLER_VIEW);
        // screens.put(TrackingScreen.MESSAGES, INBOX_VIEW);
        // screens.put(TrackingScreen.CHECKOUT_START, BUYNOW_VIEW);
        // screens.put(TrackingScreen.USER_VALIDATION, SIGNUPP_ENDING_VIEW);
        // screens.put(TrackingScreen.LISTING_CREATION_PRODUCT_NAME,
        // CREATE_LISTING_VIEW);
        // screens.put(TrackinelgScreen.LISTING_CREATION_SUCCESS,
        // CREATE_LISTING_DONE_VIEW);
        
        // Initialize
        init(context);
    }
    
    /**
     * ####### BASE ####### 
     */
    
    
    private void init(Context context) {
        if (isEnabled) {
        	Log.d(TAG, "Ad4PSUH Startup -> INITITALIZED");
            mA4S = A4S.get(context);
            Bundle prefs = new Bundle();
            prefs.putString(USER_ID, "0");
            mA4S.updateUserPreferences(prefs);
        }
    }
    
    /**
     * 
     * @param activity
     */
    public static void startActivity(Activity activity) {
        if (null != mA4S && isEnabled) {
            Log.d(TAG, "Started Activity -> " + activity.getLocalClassName());
            mA4S.startActivity(activity);
        }
    }

    /**
     * 
     * @param activity
     */
    public static void stopActivity(Activity activity) {
        if (null != mA4S && isEnabled) {
            Log.d(TAG, "Stoped Activity -> " + activity.getLocalClassName());
            mA4S.stopActivity(activity);
        }
    }
    
    /**
     * Mark this activity to receive in app messages from Ad4Push service.
     * @param activity
     * @author sergiopereira
     */
    public static void startActivityForInAppMessages(Activity activity) {
    	if (null != mA4S && isEnabled) {
            Log.d(TAG, "ON START ACTIVITY ONLY FOR IN-APP MSG: " + activity.getLocalClassName());
            startActivity(activity);
            setPushNotificationLocked(true);
        } else Log.w(TAG, "WARNING: A4S IS NULL OR IS DISABLED"); 
    }
    
    /**
     * Lock or unlock the push notifications.
     * @param true/false
     * @author sergiopereira
     */
    public static void setPushNotificationLocked(boolean bool) {
    	if (null != mA4S && isEnabled) {
    		Log.d(TAG, "LOCK PUSH NOTIFICATIONS: " + bool);
        	mA4S.setPushNotificationLocked(bool);
    	}
    }
    
    /**
     * Lock or unlock the in-app messages. 
     * @param true/false
     * @author sergiopereira
     */
	public static void setInAppDisplayLocked(boolean bool) {
		if (null != mA4S && isEnabled) {
			Log.d(TAG, "LOCK IN APP MESSAGE: " + bool);
			mA4S.setInAppDisplayLocked(bool);
		}
    }
    
    

    private void trackView(String view) {
        if (null != mA4S && isEnabled) {
            Log.d(TAG, "View state tracked -> " + VIEW_STATE + "=" + view);
            mA4S.putState(VIEW_STATE, view);
        }
    }
    
    /*
     * ############## TRACKING ############## 
     */
    
    /**
     * Method used to set some info about device.
     * @param context
     * @author sergiopereira
     */
    private void setDeviceInfo(Bundle info) {
    	if (null != mA4S && isEnabled) {
    		Log.i(TAG, "SET DEVICE INFO: " + info.toString());
    		// Set info
    		mA4S.updateUserPreferences(info);
    	}
    }
    

    /**
     * First open
     * @param info
     */
    public void trackAppFirstOpen(Bundle info) {
        if (isEnabled) {
            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            boolean alreadyOpened = settings.getBoolean(HAS_OPENED_APP, false);

            if (!alreadyOpened) {
                String currentDateandTime = getCurrentDateTime();

                mA4S.trackEvent(EVENT_FIRST_OPEN_APP, "firstOpenDate=" + currentDateandTime);

                alreadyOpened = true;
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(HAS_OPENED_APP, alreadyOpened);
                editor.commit();
            }
        }
        
    	// Set some info
    	setDeviceInfo(info);
    }

    public void trackFacebookConnect(String customerId) {
        mA4S.trackEvent(EVENT_FACEBOOK_CONNECT, "loginUserID=" + customerId);
    }

    public void trackLogin(String customerId, String customerName) {
        // Log.d(TAG, "login tracked: event = " +
        // context.getString(R.string.xlogin) + " customerId = " + customerId);

        if (isEnabled) {
            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            String userStatus = settings.getString(STATUS_IN_APP, null);

            if (null == userStatus) {
                userStatus = STATUS_PROSPECT;
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(STATUS_IN_APP, userStatus);
                editor.commit();
            }

            Bundle prefs = new Bundle();
            prefs.putString(STATUS_IN_APP, userStatus);
            prefs.putString(USER_ID, customerId);
            prefs.putString(USER_NAME, customerName);
            mA4S.updateUserPreferences(prefs);

            mA4S.trackEvent(EVENT_LOGIN, "loginUserID=" + customerId);
        }
    }

    public void trackSignupStarted() {
        if (isEnabled) {
            Log.d(TAG, "trackSignupStarted");

            Bundle prefs = new Bundle();
            prefs.putString(REGISTRATION, REGISTRATION_STARTED);
            mA4S.updateUserPreferences(prefs);
        }
    }

    public void trackSignup(String customerId, String customerGender) {
        if (isEnabled) {
            Log.d(TAG, "trackSignup");
            Lead lead = new Lead("Registration done with customer ID", customerId);
            mA4S.trackLead(lead);

            Bundle prefs = new Bundle();
            prefs.putString(REGISTRATION, REGISTRATION_DONE);
            prefs.putString(USER_ID, customerId);
            prefs.putString(USER_GENDER, customerGender);
            mA4S.updateUserPreferences(prefs);
        }
    }

    // private void trackSignupVerifyed(String customerName) {
    // if (isEnabled) {
    // Log.d(TAG, "trackSignupVerifyed");
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(REGISTRATION, REGISTRATION_VERIFIED);
    // prefs.putString(USER_NAME, customerName);
    // mA4S.updateUserPreferences(prefs);
    // }
    // }

    public void trackCheckoutStarted() {
        if (isEnabled) {
            Log.d(TAG, "trackBuyNow started");
            Bundle prefs = new Bundle();
            prefs.putString(ORDER_STATUS, CHECKOUT_STARTED);
            mA4S.updateUserPreferences(prefs);
        }
    }

    public void trackCheckoutEnded(String transactionId, Double cartValue, Double average, int orderCount, String coupon, int favoriteCount) {
        String currency = CurrencyFormatter.getCurrencyCode();
        if (isEnabled) {
            Log.d(TAG, "trackBuyNowPurchase: cartValue = " + cartValue + " currency = " + currency);
            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            String userStatus = settings.getString(STATUS_IN_APP, null);
            int purchasesNumber = settings.getInt(PURCHASE_NUMBER, 0);
            double ordersSum = settings.getFloat(PURCHASE_VALUE, 0);

            ordersSum += cartValue;

            SharedPreferences.Editor editor = settings.edit();
            if (!STATUS_CUSTOMER.equals(userStatus)) {
                userStatus = STATUS_CUSTOMER;
                editor.putString(STATUS_IN_APP, userStatus);
            }

            editor.putInt(PURCHASE_NUMBER, ++purchasesNumber);
            editor.putFloat(PURCHASE_VALUE, (float) ordersSum);
            editor.commit();

            String currentDateandTime = getCurrentDateTime();

            Bundle prefs = new Bundle();
            prefs.putString(ORDER_STATUS, CHECKOUT_FINISHED);

            prefs.putString(LASTORDER_DATE, currentDateandTime);
            prefs.putInt(PURCHASE_COUNTER_APP, purchasesNumber);
            prefs.putInt(CART_COUNTER, orderCount);
            prefs.putDouble(CART_VALUE, cartValue);

            prefs.putDouble(PURCHASE_GRAND_TOTAL, ordersSum);
            prefs.putInt(FAVORITES_CART_COUNT, favoriteCount);
            prefs.putString(COUPON_STATUS, coupon);
            prefs.putDouble(AVERAGE_CART_VALUE, average);

            mA4S.updateUserPreferences(prefs);

            Purchase purchase = new Purchase(transactionId, CurrencyFormatter.getCurrencyCode(), cartValue);
            mA4S.trackPurchase(purchase);
        }
    }

    // private void trackBuyNowVerified() {
    // if (isEnabled) {
    // Log.d(TAG, "trackBuyNowVerified");
    // Bundle prefs = new Bundle();
    // prefs.putString(BUYNOW_STATUS, CHECKOUT_VERIFIED);
    // mA4S.updateUserPreferences(prefs);
    // }
    // }

    public void trackAddToFavorites(String productSKU) {
        if (isEnabled) {
            Log.d(TAG, "trackAddToWishlist: productSKU = " + productSKU);
            mA4S.trackEvent(EVENT_ADD_TO_WHISHLIST, "wishlisted=Y");

            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            int wishlistNumber = settings.getInt(WISHLIST_NUMBER, 0);

            SharedPreferences.Editor editor = settings.edit();

            editor.putInt(WISHLIST_NUMBER, ++wishlistNumber);
            editor.commit();

            String currentDateandTime = getCurrentDateTime();

            Bundle prefs = new Bundle();
            prefs.putInt(WISHLIST_STATUS, wishlistNumber);
            prefs.putString(WISHLIST_DATE, currentDateandTime);
            prefs.putString(WISHLIST_PRODUCT, productSKU);

            mA4S.updateUserPreferences(prefs);

            //mA4S.trackEvent(EVENT_ADD_TO_WHISHLIST, productSKU);
        }
    }

    public void trackRemoveFromWishlist(String productSKU) {
        if (isEnabled) {
            Log.d(TAG, "trackRemoveFromWishlist: itemcount " + productSKU);

            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            int wishlistNumber = settings.getInt(WISHLIST_NUMBER, 0);

            SharedPreferences.Editor editor = settings.edit();

            editor.putInt(WISHLIST_NUMBER, --wishlistNumber);
            editor.commit();

            Bundle prefs = new Bundle();
            prefs.putInt(WISHLIST_STATUS, wishlistNumber);
            mA4S.updateUserPreferences(prefs);
        }
    }

    public void trackAddToCart(String sku, double price, String name, String category) {
        if (isEnabled) {
            Log.d(TAG, "trackAddToCart: productSKU = " + sku);
            String currency = CurrencyFormatter.getCurrencyCode();

            Item productAdded = new Item(sku, name, category, currency, price, 1);
            Cart cart = new Cart("1", productAdded);
            mA4S.trackAddToCart(cart);
        }
    }

    public void trackSocialShare() {
        if (isEnabled) {
            Log.d(TAG, "trackSocialShare");

            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            int shareNumber = settings.getInt(SHARED_PRODUCT_COUNT, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(SHARED_PRODUCT_COUNT, ++shareNumber);
            editor.commit();

            Bundle prefs = new Bundle();
            prefs.putInt(SHARED_PRODUCT_COUNT, shareNumber);
            mA4S.updateUserPreferences(prefs);
        }
    }

    // private void trackRateAsBuyerStarted() {
    // if (isEnabled) {
    // Log.d(TAG, "trackRateProduct");
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(RATE_SELLER_STATUS_APP, RATE_STARTED);
    // mA4S.updateUserPreferences(prefs);
    // }
    // }
    //
    // private void trackRateAsBuyer(String seller) {
    // if (isEnabled) {
    // Log.d(TAG, "trackRateProduct");
    //
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // String currentDateandTime = sdf.format(new Date());
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(RATE_SELLER_STATUS_APP, RATE_DONE);
    // prefs.putString(LATEST_RATE_SELLER_DATE_APP, currentDateandTime);
    // prefs.putString(LATEST_RATE_SELLER_NAME_APP, seller);
    // mA4S.updateUserPreferences(prefs);
    // }
    //
    // }
    //
    // private void trackRateAsSellerStarted() {
    // if (isEnabled) {
    // Log.d(TAG, "trackRateProduct");
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(RATE_BUYER_STATUS_APP, RATE_STARTED);
    // mA4S.updateUserPreferences(prefs);
    // }
    // }
    //
    // private void trackRateAsSeller(String buyer) {
    // if (isEnabled) {
    // Log.d(TAG, "trackRateProduct");
    //
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // String currentDateandTime = sdf.format(new Date());
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(RATE_BUYER_STATUS_APP, RATE_DONE);
    // prefs.putString(RATE_BUYER_DATE_APP, currentDateandTime);
    // prefs.putString(RATE_BUYER_NAME_APP, buyer);
    // mA4S.updateUserPreferences(prefs);
    // }
    //
    // }

    public void trackCountryChange(String newCountryCode) {
        if (isEnabled) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getNetworkCountryIso();
            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                countryCode = tm.getSimCountryIso();
            }

            countryCode = countryCode.toUpperCase(context.getResources().getConfiguration().locale);
            Log.d(TAG, "trackCountryChange -> " + newCountryCode + "; Device Country -> " + countryCode);
            Bundle prefs = new Bundle();
            prefs.putString(SHOP_COUNTRY, newCountryCode);
            if (!countryCode.equals("")) {
                prefs.putString(COUNTRY_CODE, countryCode);
            }
            mA4S.updateUserPreferences(prefs);
        }
    }

    public void trackSearch(String searchTerm) {
        if (isEnabled) {
            String currentDateandTime = getCurrentDateTime();

            Log.d(TAG, "trackSearch -> " + searchTerm + "; " + currentDateandTime);

            Bundle prefs = new Bundle();
            prefs.putString(LAST_SEARCH, searchTerm);
            prefs.putString(LAST_SEARCH_DATE, currentDateandTime);
            mA4S.updateUserPreferences(prefs);
        }
    }

    // private void trackCreateListingStarted() {
    // if (isEnabled) {
    // Log.d(TAG, "trackCreateListingStarted");
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(CREATELISTINGSTARTED, "0");
    // prefs.putString(SELLER_STATUS, SELLER_STARTED);
    // mA4S.updateUserPreferences(prefs);
    //
    // mA4S.trackEvent(EVENT_CREATE_LISTING_STARTED, "0");
    // }
    // }

    // private void trackCreateListingEnded(String productSKU, int quantity) {
    // if (isEnabled) {
    // Log.d(TAG, "trackCreateListingEnded");
    //
    // SharedPreferences settings =
    // context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
    // int listingNumber = settings.getInt(TOTAL_LISTINGS_COUNT, 0);
    // int listingQuantity = settings.getInt(TOTAL_PRODUCT_QUANTITY, 0);
    //
    // listingQuantity +=quantity;
    //
    // SharedPreferences.Editor editor = settings.edit();
    // editor.putInt(TOTAL_LISTINGS_COUNT, ++listingNumber);
    // editor.putInt(TOTAL_PRODUCT_QUANTITY, ++listingQuantity);
    // editor.commit();
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(CREATELISTINGDONE, productSKU);
    // prefs.putString(SELLER_STATUS, SELLER_DONE);
    // prefs.putInt(TOTAL_LISTINGS_COUNT, listingNumber);
    // prefs.putInt(TOTAL_PRODUCT_QUANTITY, listingQuantity);
    //
    // mA4S.updateUserPreferences(prefs);
    //
    // mA4S.trackEvent(EVENT_CREATE_LISTING_DONE, "productid=" + productSKU);
    // }
    // }

    // private void trackCreateListingVerified(String productSKU) {
    // if (isEnabled) {
    // Log.d(TAG, "trackCreateListingEnded");
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(CREATELISTINGVERIFIED, productSKU);
    // prefs.putString(SELLER_STATUS, SELLER_VERIFIED);
    // mA4S.updateUserPreferences(prefs);
    //
    // mA4S.trackEvent(EVENT_CREATE_LISTING_VERIFYED, "productid=" +
    // productSKU);
    // }
    // }

    // private void trackContactSeller(String productSKU, String category) {
    // if (isEnabled) {
    // Log.d(TAG, "trackContactSeller");
    //
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // String currentDateandTime = sdf.format(new Date());
    //
    //
    // Bundle prefs = new Bundle();
    // prefs.putString(LAST_CONTACT_PRODUCT, productSKU);
    // prefs.putString(LAST_CONTACT_CATEGORY, category);
    // prefs.putString(LAST_CONTACT_SUBCATEGORY, category);
    // prefs.putString(LAST_CONTACT_DATE, currentDateandTime);
    //
    // mA4S.updateUserPreferences(prefs);
    // }
    // }

    public void trackCategorySelection() {
        if (isEnabled) {
            Log.d(TAG, "trackCategorySelection");

            try {
                Bundle prefs = new Bundle();
                prefs.putString(MOST_VISITED_CATEGORY, CategoriesTableHelper.getTopCategory());
                mA4S.updateUserPreferences(prefs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void trackCatalogFilter(ContentValues filters) {
        if (isEnabled) {
            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            int brandNumber = settings.getInt(FILTER_BRANDS, 0);
            int colorNumber = settings.getInt(FILTER_COLOR, 0);
            int priceNumber = settings.getInt(FILTER_PRICE, 0);
            int categoryNumber = settings.getInt(FILTER_CATEGORIES, 0);
            int sizeNumber = settings.getInt(FILTER_SIZE, 0);

            brandNumber += filters.containsKey(FILTER_BRANDS) ? 1 : 0;
            colorNumber += filters.containsKey(FILTER_COLOR) ? 1 : 0;
            priceNumber += filters.containsKey(FILTER_PRICE) ? 1 : 0;
            categoryNumber += filters.containsKey(FILTER_CATEGORIES) ? 1 : 0;
            sizeNumber += filters.containsKey(FILTER_SIZE) ? 1 : 0;

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(FILTER_BRANDS, brandNumber);
            editor.putInt(FILTER_COLOR, colorNumber);
            editor.putInt(FILTER_PRICE, priceNumber);
            editor.putInt(FILTER_CATEGORIES, categoryNumber);
            editor.putInt(FILTER_SIZE, sizeNumber);
            editor.commit();

            Bundle prefs = new Bundle();
            prefs.putInt(FILTER_BRAND_COUNT, brandNumber);
            prefs.putInt(FILTER_COLOR_COUNT, colorNumber);
            prefs.putInt(FILTER_CATEGORY_COUNT, categoryNumber);
            prefs.putInt(FILTER_PRICE_COUNT, priceNumber);

            mA4S.updateUserPreferences(prefs);
        }
    }

    public void trackCampaignsView() {
        if (isEnabled) {
            SharedPreferences settings = context.getSharedPreferences(AD4PUSH_PREFERENCES, 0);
            int campaignNumber = settings.getInt(CAMPAIGN_PAGEVIEW_COUNT, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(CAMPAIGN_PAGEVIEW_COUNT, ++campaignNumber);
            editor.commit();

            Bundle prefs = new Bundle();
            prefs.putInt(CAMPAIGN_PAGEVIEW_COUNT, campaignNumber);

            mA4S.updateUserPreferences(prefs);

        }
    }

    public void trackScreen(TrackingPage screen) {
        if (null != screens && screens.containsKey(screen)) {
            switch (screen) {
            case REGISTRATION:
                trackSignupStarted();
                break;

            default:
                trackView(screens.get(screen));
                break;
            }
        }

    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", context.getResources().getConfiguration().locale);
        return sdf.format(new Date());
    }
    
    
    


}
