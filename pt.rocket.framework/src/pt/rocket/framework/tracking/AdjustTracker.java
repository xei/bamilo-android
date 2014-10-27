/**
 * 
 */
package pt.rocket.framework.tracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.R;
import pt.rocket.framework.objects.AddableToCart;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.CustomerGender;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.adjust.sdk.Adjust;

import de.akquinet.android.androlog.Log;

/**
 * @author nunocastro
 *
 */
public class AdjustTracker {
    
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
    public static final String REGION = "region";
    public static final String CITY = "city";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "categoryId";
    public static final String TREE = "tree";
    public static final String FAVORITES = "favorites";
    public static final String PRODUCT_SIZE = "size";
    
    protected static class AdjustConstants {
        public static final String NO_DATA = "";
        public static final String NO_CURRENCY = "";
        public static final String FIXED_PRICE = "FixedPrice";
        public static final String NOT_AVAILABLE = "n.a.";

        public static final String FILTERS_BRAND = "brand";
        public static final String FILTERS_COLOR = "color";
        public static final String FILTERS_PRICE_RANGE = "price_range";
        public static final String FILTERS_CATEGORY = "category";
    }

    protected static class AdjustKeys {
        public static final String SHOP_COUNTRY = "shop_country";
        public static final String APP_VERSION = "app_version";
        public static final String DISPLAY_SIZE = "display_size";

        public static final String USER_ID = "user_id";
        public static final String DURATION = "duration";
        public static final String DEVICE = "device";
        public static final String DEVICE_MANUFACTURER = "device_manufacturer";
        public static final String DEVICE_MODEL = "device_model";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String LISTING_ID = "listingID";
        public static final String AVG_RATING_SELLER = "average_rating_seller";
        public static final String TRUSTED_SELLER = "trusted_seller";
        public static final String OFFERING_TYPE = "offering_type";
        public static final String SELLER_ID = "seller_id";
        public static final String CURRENCY_CODE = "currency_code";
        public static final String PRICE = "price";
        public static final String RATE_GIVEN = "rategiven";
        public static final String COMMENT = "comment";
        public static final String CATEGORY_TREE = "tree";
        public static final String CATEGORY = "category";
        public static final String CATEGORY_ID = "category_id";
        public static final String SUBCATEGORY = "subcategory";
        public static final String CONDITION = "condition";
        public static final String QUANTITY = "quantity";
        public static final String PRODUCT_TYPE = "product_type";
        public static final String PRODUCT_IMAGE = "product_image";
        public static final String PRODUCT_NAME = "product_name";
        public static final String SALE_DURATION = "sale_duration";
        public static final String PAYMENT_OPTIONS = "payment_options";
        public static final String SHIPPING_OPTIONS = "shipping_options";
        public static final String SHIPMENT_TIME = "shipment_time";
        public static final String SHIPMENT_FEES = "shipment_fees";
        public static final String DESTINATION = "destination";
        public static final String SEARCH_PHRASE = "search_phrase";
        public static final String SEARCH_RESULTS = "number_of_search_results";
        public static final String GENDER = "gender";
        public static final String SKU = "sku";
        public static final String SKUS = "skus";
        public static final String PRODUCTS = "products";
        public static final String PRODUCT = "product";
        public static final String KEYWORDS = "keywords";
        public static final String NEW_CUSTOMER = "new_customer";   
        public static final String AMOUNT_TRANSACTIONS = "amount_transactions";
        public static final String AMOUNT_SESSIONS = "amount_sessions";
        public static final String REGION = "region";
        public static final String CITY = "city";
        public static final String DISCOUNT = "discount";
        public static final String BRAND = "brand";
        public static final String QUERY = "query";
        public static final String SIZE = "size";
        public static final String COLOUR = "colour";
        public static final String TOTAL_WISHLIST = "total_wishlist";
        public static final String WISHLIST_CURRENCY_CODE = "currency_code1";
        public static final String TOTAL_CART = "total_cart";
        public static final String CART_CURRENCY_CODE = "cart_currency_code";
        public static final String TOTAL_TRANSACTION = "total_transaction"; 
        public static final String TRANSACTION_CURRENCY = "transaction_currency";
        public static final String VARIATION = "variation";
        public static final String APP_PRE_INSTALL = Constants.INFO_PRE_INSTALL;
        public static final String INFO_BRAND = Constants.INFO_BRAND;
        public static final String DEVICE_SIM_OPERATOR = Constants.INFO_SIM_OPERATOR;
                
    }

    protected static class AdjustEvents {
        public static final String LAUNCH = "Launch";
        public static final String LOGIN = "Login";
        public static final String LOGOUT = "Logout";
        public static final String SIGNUP = "Signup";
        public static final String SIGNUPNEWSLETTER = "SignupNewsletter";
        public static final String CUSTOMER = "Customer";
        public static final String SALE = "Sale";
        public static final String FBCONNECTLOGIN = "FBConnectLogin";
        public static final String FBCONNECTSIGNUP = "FBConnectSignup";
        public static final String ADDTOWISHLIST = "AddtoWishlist";
        public static final String REMOVEFROMWISHLIST = "RemoveFromWishlist";
        public static final String FILTERS = "PersonalisedFeed";
        public static final String SHARE = "SocialShare";
        public static final String ADDREVIEWASBUYER = "RateSeller";
        public static final String ADDREVIEWASSELLER = "RateBuyer";
        public static final String LISTINGADDTITLE = "CreateListingStarted";
        public static final String LISTINGCREATION = "CreateListing";
        public static final String MESSAGESUBMITTOSELLER = "MessageToSeller";
        public static final String MESSAGESUBMIT = "Message";
        public static final String SEARCH = "Search";
        public static final String PERSONALISEDFEED = "PersonalisedFeed";
        public static final String CALL = "Call";        

    }
    
    private String TABLET = "Tablet";
    private String PHONE = "Phone";

//    private static Context context;
    
    private final String TRACKING_PREFS = "tracking_prefs";
    private final String SESSION_COUNTER = "sessionCounter";
    
    private static boolean isEnabled = false;
    
    private static AdjustTracker sInstance;
    
    private static final String NOT_AVAILABLE = "n.a.";

    public final static String ENCODING_SCHEME = "UTF-8";

    public final static String ADJUST_FIRST_TIME_KEY = "adjust_first_time";
    
    private static double ADJUST_CENT_VALUE = 100d;

    private Context mContext;

    public static AdjustTracker get() {
        if (sInstance == null) {
            sInstance = new AdjustTracker();
        }
        return sInstance;
    }
    
    public static void startup(Context context) {
        Log.d(TAG, "Adjust Startup");

        sInstance = new AdjustTracker(context);
    }    
    
    public AdjustTracker() {
        isEnabled = false;
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
        // Where the 2nd parameter isupdate should be set to
        // true if the App has been run before and false if it has
        // never been run before. Setting this correctly will allow
        // us to distinguish which installs are current users
        // updating to include the Ad-X SDK from new organic
        // downloads. The third parameter sets the logging level for
        // debugging. Set to zero for production version of the App.
//        boolean isupdate;
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        boolean firstTimeAdjust = sharedPrefs.getBoolean(ADJUST_FIRST_TIME_KEY, true);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(ADJUST_FIRST_TIME_KEY, false);
        editor.commit();

//        if (firstTimeAdjust) {
//            isupdate = false;
//        } else {
//            isupdate = true;
//        }
        
        Log.i(TAG, "ADJUST is APP_LAUNCH " + Adjust.isEnabled());
    }
    
    public static void onResume(Activity activity) {
        Adjust.onResume(activity);
    }

    public static void onPause(){
        Adjust.onPause();
    }
    
    public void trackScreen(TrackingPage screen, Bundle bundle) {
        if (!isEnabled) {
            return;
        }
        
        Log.i(TAG, " Tracked Screen --> " + screen);
        
        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String> fbParameters;
        
        switch (screen) {
        case HOME:
            parameters = getBaseParameters(parameters, bundle);
            parameters.remove(AdjustKeys.SHOP_COUNTRY);
            parameters.put(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                parameters.put(AdjustKeys.GENDER, getGender(customer));
            }   
            
            Adjust.trackEvent(mContext.getString(R.string.adjust_token_home), parameters);
            
            //FB - View Homescreen
            parameters = new HashMap<String, String>();
            parameters = getFBBaseParameters(parameters, bundle);
            Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_home), parameters);
            
            break;

        case PRODUCT_DETAIL_LOADED:
            parameters = getBaseParameters(parameters, bundle);
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                parameters.put(AdjustKeys.GENDER, getGender(customer));
            }   
            CompleteProduct prod = bundle.getParcelable(PRODUCT);
            parameters.put(AdjustKeys.SKU, prod.getSku());
            
            Adjust.trackEvent(mContext.getString(R.string.adjust_token_view_product), parameters);
            
            // FB - View Product
            parameters = new HashMap<String, String>();
            parameters = getFBBaseParameters(parameters, bundle);           
            parameters.put(AdjustKeys.SKU, prod.getSku()); 
            parameters.put(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO));           
            parameters.put(AdjustKeys.DISCOUNT, prod.hasDiscount() ? "y" : "n"); 
            parameters.put(AdjustKeys.BRAND, prod.getBrand()); 
            parameters.put(AdjustKeys.PRICE, prod.hasDiscount() ? prod.getSpecialPrice() : prod.getPrice());
            if( null != prod.getAttributes() && !TextUtils.isEmpty(prod.getAttributes().get("color"))){
            	 parameters.put(AdjustKeys.COLOUR, prod.getAttributes().get("color"));
            }
            if (bundle.containsKey(AdjustTracker.PRODUCT_SIZE) && !TextUtils.isEmpty(bundle.getString(AdjustTracker.PRODUCT_SIZE))){
            	parameters.put(AdjustKeys.SIZE,bundle.getString(AdjustTracker.PRODUCT_SIZE)); 
            }
            if (bundle.containsKey(TREE) && !TextUtils.isEmpty(bundle.getString(TREE))){
                parameters.put(AdjustKeys.CATEGORY_TREE, bundle.getString(TREE));
            }           
            Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_view_product), parameters);
            
            break;
            
        case PRODUCT_LIST_SORTED:  //View Listing
            Log.d("ADJUST","PRODUCT_LIST_SORTED");
            parameters = getBaseParameters(parameters, bundle);         
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                parameters.put(AdjustKeys.GENDER, getGender(customer));
            }
            ArrayList<String> skus = bundle.getStringArrayList(TRANSACTION_ITEM_SKUS);
            StringBuilder sbSkus;
            sbSkus = new StringBuilder();
            if (skus.size() > 0) {
                sbSkus.append("[");
                final int skusLimit = 3;
                int skusCount = 0;
                for (String sku : skus) {
                    skusCount++;
//                    sku = "\""+sku+"\"";
                    sbSkus.append(sku).append(",");
                    if (skusLimit <= skusCount) {
                        break;
                    }
                }
                sbSkus.setLength(sbSkus.length() - 1);
                sbSkus.append("]");
                parameters.put(AdjustKeys.PRODUCTS, sbSkus.toString());
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_view_listing), parameters);
            }
            //FB - View Listing
            fbParameters = new HashMap<String, String>();
            fbParameters = getFBBaseParameters(fbParameters, bundle);
            fbParameters.put(AdjustKeys.CATEGORY, bundle.getString(CATEGORY)); 
            fbParameters.put(AdjustKeys.SKUS, sbSkus.toString());
            if (bundle.containsKey(CATEGORY_ID) && !TextUtils.isEmpty(bundle.getString(CATEGORY_ID))){
                fbParameters.put(AdjustKeys.CATEGORY_ID, bundle.getString(CATEGORY_ID));
            }
            if (bundle.containsKey(TREE) && !TextUtils.isEmpty(bundle.getString(TREE))){
            	fbParameters.put(AdjustKeys.CATEGORY_TREE, bundle.getString(TREE));
            }
   
            Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_view_listing), fbParameters);
            break;
            
        case CART_LOADED:
            parameters = getBaseParameters(parameters, bundle);
            fbParameters = new HashMap<String, String>();
            
            if (bundle.getParcelable(CUSTOMER) != null) {
                Customer customer = bundle.getParcelable(CUSTOMER);
                parameters.put(AdjustKeys.GENDER, getGender(customer));
            }   
  
            ShoppingCart cart = bundle.getParcelable(CART);
            ShoppingCartItem item;
            JSONObject json;
            
            int productCount = 0;
            String countString = "";
            Boolean hasDiscount = false;                
            Double price = 0.0;
            for (String key : cart.getCartItems().keySet()) {
                item = cart.getCartItems().get(key);
                
                json = new JSONObject();
                try {
                    json.put(AdjustKeys.SKU, item.getConfigSKU());
                    json.put(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO));
                    json.put(AdjustKeys.QUANTITY, item.getQuantity());
                    json.put(AdjustKeys.PRICE, item.getSpecialPriceVal());
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }               
                parameters.put(AdjustKeys.PRODUCT + countString, json.toString());

                countString = String.valueOf(++productCount);

                //FB - View Cart
                fbParameters = getFBBaseParameters(fbParameters, bundle);

                hasDiscount = item.getSpecialPriceVal() != item.getPriceVal();
                price = (hasDiscount ? item.getSpecialPriceVal() : item.getPriceVal());

                fbParameters.put(AdjustKeys.SKU, item.getConfigSKU() );
                fbParameters.put(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO)); 
                fbParameters.put(AdjustKeys.QUANTITY, String.valueOf(item.getQuantity())); 
                fbParameters.put(AdjustKeys.DISCOUNT, hasDiscount ? "y" : "n"); 


                fbParameters.put(AdjustKeys.PRICE, price.toString());
                
                fbParameters.put(AdjustKeys.CART_CURRENCY_CODE, bundle.getString(CURRENCY_ISO));             
                fbParameters.put(AdjustKeys.TOTAL_CART, cart.getCartValue());
                
//              fbParameters.put(AdjustKeys.SIZE, item.getVariation());
//              fbParameters.put(AdjustKeys.COLOUR, item.getVariation());
//              fbParameters.put(AdjustKeys.VARIATION, item.getVariation());            
//                parameters.put(AdjustKeys.BRAND, item.getBrand());               
          
                
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_view_cart), fbParameters);
            }
    
            Adjust.trackEvent(mContext.getString(R.string.adjust_token_view_cart), parameters);
            break;
        
        default:
            
            break;
        }
    }

    public void trackEvent(Context context, TrackingEvent event, Bundle bundle) {

        Map<String, String> parameters = new HashMap<String, String>();
        
        Log.i(TAG, " Tracked Event --> " + event);
        
        switch (event) {

//        case APP_LAUNCH:
//            Log.i(TAG, "code1adjust is APP_LAUNCH " + Adjust.isEnabled());
//            break;

        case APP_OPEN:
            Log.i(TAG, "APP_OPEN:" + Adjust.isEnabled());
            if (isEnabled) {
                Log.i(TAG, "code1adjust is APP_OPEN " + Adjust.isEnabled());
                parameters.put(AdjustKeys.APP_VERSION, getAppVersion());
                parameters.put(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
                parameters.put(AdjustKeys.DURATION, getDuration(bundle.getLong(BEGIN_TIME)));
                parameters.put(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
                parameters.put(AdjustKeys.DEVICE_MODEL, Build.MODEL);
                parameters.put(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE, false)));
                parameters.put(AdjustKeys.APP_PRE_INSTALL, String.valueOf(bundle.getBoolean(Constants.INFO_PRE_INSTALL)));
                parameters.put(AdjustKeys.DEVICE_SIM_OPERATOR, bundle.getString(Constants.INFO_SIM_OPERATOR));
                
                Log.i(TAG, "code1adjust is APP_OPEN values ##### " + parameters.toString());
    
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_launch), parameters);
            }
            break;
            
        case LOGIN_SUCCESS:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_log_in), parameters);                
            }
            break;
            
        case LOGOUT_SUCCESS:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_log_out), parameters);
            }
            break;
            
        case SIGNUP_SUCCESS:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_sign_up), parameters);
            }

//            /**
//             * Track Signup Newsletter
//             */
//            if (bundle.containsKey(TrackingManager.REGISTRATION_SIGNUP_NEWSLETTER)) {
//                if (bundle.getBoolean(TrackingManager.REGISTRATION_SIGNUP_NEWSLETTER)) {
//                    Log.i(TAG, "code1reg tracker : " + bundle.getBoolean(TrackingManager.REGISTRATION_SIGNUP_NEWSLETTER));
//                    Adjust.trackEvent(mContext.getString(R.string.adjust_token_signup_newsletter), parameters);
//                }
//            }

            break;
            
        case CHECKOUT_FINISHED: // Sale       
            if (isEnabled) {
                Log.d(TAG, " TRACK REVENEU --> " + bundle.getDouble(TRANSACTION_VALUE));
                
                parameters = getBaseParameters(parameters, bundle);                
                Map<String, String> fbParameters = new HashMap<String, String>();
                Map<String, String> transParameters;
                
                if (bundle.getBoolean(IS_FIRST_CUSTOMER)) {
                    Adjust.trackEvent(mContext.getString(R.string.adjust_token_customer), parameters);
                }
                
                parameters.put(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));
                
                ArrayList<String> skus = bundle.getStringArrayList(TRANSACTION_ITEM_SKUS);
                StringBuilder sbSkus = new StringBuilder();
                sbSkus.append("[");
                for (String sku: skus) {
                    sbSkus.append(sku).append(",");
                }
                sbSkus.deleteCharAt(sbSkus.length()-1);
                sbSkus.append("]");            
                parameters.put(AdjustKeys.SKUS, sbSkus.toString());
                
                String eventString = bundle.getBoolean(IS_GUEST_CUSTOMER) ? mContext.getString(R.string.adjust_token_guest_sale) : mContext.getString(R.string.adjust_token_sale);
                
                // Track Revenue (Sale or Gues Sale)
//                convertToEuro(baseActivity, bundle.getDouble(TRANSACTION_VALUE), parameters, eventString, true, bundle.getString(CURRENCY_ISO));
                double finalValue = bundle.getDouble(TRANSACTION_VALUE)*ADJUST_CENT_VALUE;
                Adjust.trackRevenue(finalValue, eventString, parameters);
                
                // Trigger also the TRANSACTION_CONFIRMATION event
                transParameters = new HashMap<String, String>(parameters);
                transParameters.remove(AdjustKeys.SKUS);
                transParameters.put(AdjustKeys.NEW_CUSTOMER, String.valueOf(bundle.getBoolean(IS_GUEST_CUSTOMER))); 
                if (bundle.getParcelable(CUSTOMER) != null) {
                    Customer customer = bundle.getParcelable(CUSTOMER);
                    transParameters.put(AdjustKeys.GENDER, getGender(customer));
                }
                
                ArrayList<PurchaseItem> cartItems = bundle.getParcelableArrayList(CART);
                JSONObject json;
                int productCount = 0;
                String countString = "";
                for (PurchaseItem item : cartItems) {
                    
                    json = new JSONObject();
                    try {
                        json.put(AdjustKeys.SKU, item.sku);
                        json.put(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO));
                        json.put(AdjustKeys.QUANTITY, item.quantity);
                        json.put(AdjustKeys.PRICE, item.paidpriceAsDouble);
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    transParameters.put(AdjustKeys.PRODUCT + countString, json.toString());
                    
                    
                    countString = String.valueOf(++productCount);
                    
                    //FB - Transaction
                    fbParameters = getFBBaseParameters(fbParameters, bundle);

                    fbParameters.put(AdjustKeys.SKU, item.sku );
                    fbParameters.put(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO)); 
                    fbParameters.put(AdjustKeys.QUANTITY, item.quantity);
                    if(item.getPriceForTracking() > 0d){
                    	fbParameters.put(AdjustKeys.PRICE, String.valueOf(item.getPriceForTracking()));
                        fbParameters.put(AdjustKeys.CURRENCY_CODE, "EUR"); 
                    }
//                      fbParameters.put(AdjustKeys.DISCOUNT + countString, hasDiscount ? "true" : "false"); 
//                      fbParameters.put(AdjustKeys.BRAND + countString, item.get);
//                      fbParameters.put(AdjustKeys.SIZE + countString, item.);
                    
                    fbParameters.put(AdjustKeys.TRANSACTION_ID, bundle.getString(TRANSACTION_ID));
                //    fbParameters.put(AdjustKeys.TRANSACTION_CURRENCY, bundle.getString(CURRENCY_ISO));
                    fbParameters.put(AdjustKeys.TOTAL_TRANSACTION, String.valueOf(bundle.getDouble(TRANSACTION_VALUE)));
                    
                    Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_transaction_confirmation), fbParameters);
                }
                
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_transaction_confirmation), transParameters);
                
            }
            break;
            
        case ADD_TO_CART:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                parameters.put(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                
                if(bundle.getDouble(VALUE) > 0d){
                	parameters.put(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    parameters.put(AdjustKeys.CURRENCY_CODE, "EUR");
                }
              Adjust.trackEvent(mContext.getString(R.string.adjust_token_add_to_cart), parameters);
            
            }
            break;

        case REMOVE_FROM_CART:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                parameters.put(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));

                if(bundle.getDouble(VALUE) > 0d){
                	parameters.put(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    parameters.put(AdjustKeys.CURRENCY_CODE, "EUR");
                }
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_remove_from_cart), parameters);
            }
            break;          
            
        case ADD_TO_WISHLIST:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                parameters.put(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                
                if(bundle.getDouble(VALUE) > 0d){
                	parameters.put(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    parameters.put(AdjustKeys.CURRENCY_CODE, "EUR");
                }
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_add_to_wishlist), parameters);
            }
            break;

        case REMOVE_FROM_WISHLIST:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                parameters.put(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
    
                if(bundle.getDouble(VALUE) > 0d){
                	parameters.put(AdjustKeys.PRICE, String.valueOf(bundle.getDouble(VALUE)));
                    parameters.put(AdjustKeys.CURRENCY_CODE, "EUR");
                }
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_remove_from_wishlist), parameters);            
            }
            break;
            
        case SHARE:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                parameters.put(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_social_share), parameters);
            }
            break;

        case CALL:
            parameters = getBaseParameters(parameters, bundle);
            Adjust.trackEvent(mContext.getString(R.string.adjust_token_call), parameters);
            break;

        case ADD_REVIEW:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                parameters.put(AdjustKeys.SKU, bundle.getString(PRODUCT_SKU));
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_product_rate), parameters);
            }
            break;            
            
        case LOGIN_FB_SUCCESS:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_connect), parameters);
            }
            break;
            
        case SEARCH:
            if (isEnabled) {
                parameters = getBaseParameters(parameters, bundle);
                parameters.put(AdjustKeys.KEYWORDS, bundle.getString(SEARCH_TERM));
                if (bundle.getParcelable(CUSTOMER) != null) {
                    Customer customer = bundle.getParcelable(CUSTOMER);
                    parameters.put(AdjustKeys.GENDER, getGender(customer));
                }       
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_search), parameters);
                
                //FB - Search
                Map<String, String> fbParameters = new HashMap<String, String>();
                fbParameters = getFBBaseParameters(fbParameters, bundle);
            
                fbParameters.put(AdjustKeys.CATEGORY, bundle.getString(CATEGORY)); 
                if (bundle.containsKey(CATEGORY_ID) && !TextUtils.isEmpty(bundle.getString(CATEGORY_ID))){
                    fbParameters.put(AdjustKeys.CATEGORY_ID, bundle.getString(CATEGORY_ID));
                }
                
                fbParameters.put(AdjustKeys.QUERY, bundle.getString(SEARCH_TERM)); 
                Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_search), fbParameters);
            }
            break;
            
        case VIEW_WISHLIST:
            //FB - View Wishlist
            if (isEnabled) {
                parameters = new HashMap<String, String>();
                parameters = getFBBaseParameters(parameters, bundle);
                HashMap<String, String> fbParams = new HashMap<String, String>(); 
                
                ArrayList<AddableToCart> favourites = bundle.getParcelableArrayList(FAVORITES);
            
                Boolean hasDiscount = false;
                Double price;
                Double WishlistTotal = 0.0;
                if (null != favourites) {
                    for (AddableToCart fav : favourites) {
                        hasDiscount = fav.getSpecialPriceDouble() != fav.getPriceAsDouble();
                        price = (hasDiscount ? fav.getSpecialPriceDouble() : fav.getPriceAsDouble());
                        WishlistTotal += price;
                    }
                }
                
//                parameters.put(AdjustKeys.WISHLIST_CURRENCY_CODE, bundle.getString(CURRENCY_ISO));
                parameters.put(AdjustKeys.TOTAL_WISHLIST, WishlistTotal.toString());
                
                if (null != favourites) {
                    String priceValue;
                    for (AddableToCart fav : favourites) {
                        fbParams = new HashMap<String, String>(parameters);
                        hasDiscount = fav.getSpecialPriceDouble() != fav.getPriceAsDouble();
                        priceValue = (hasDiscount ? fav.getSpecialPrice() : fav.getPrice());
                        parameters.put(AdjustKeys.BRAND, fav.getBrand()); 
//                        if( null != fav.getAttributes() && !TextUtils.isEmpty(fav.getAttributes().get("color"))){
//                        	 parameters.put(AdjustKeys.COLOUR, fav.getAttributes().get("color"));
//                        }
//                        fbParams.put(AdjustKeys.SKU + countString, fav.getSku());
                        fbParams.put(AdjustKeys.SKU, fav.getSku());
                        fbParams.put(AdjustKeys.CURRENCY_CODE, bundle.getString(CURRENCY_ISO)); 
    //                  fbParams.put(AdjustKeys.QUANTITY, "1"); 
                        fbParams.put(AdjustKeys.DISCOUNT, hasDiscount ? "y" : "n"); 
                        fbParams.put(AdjustKeys.BRAND, fav.getBrand());
                        if (fav.hasSimples() && AddableToCart.NO_SIMPLE_SELECTED != fav.getSelectedSimple()) {                                              
                            fbParams.put(AdjustKeys.SIZE, fav.getSelectedSimpleValue());
                        }
                        fbParams.put(AdjustKeys.PRICE, priceValue);
                        
                        Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_view_wishlist), fbParams);
                    }
                } else {
                    Adjust.trackEvent(mContext.getString(R.string.adjust_token_fb_view_wishlist), parameters);
                }
            }           
            break;
            
        default:
            break;
        }

    }

    public boolean enabled() {
        return true;
    }

    public void trackTiming(TrackingPage screen, Bundle bundle) {

    }

    private Map<String, String> getBaseParameters(Map<String, String> parameters, Bundle bundle) {        
        parameters.put(AdjustKeys.SHOP_COUNTRY, bundle.getString(COUNTRY_ISO));
        if (bundle.containsKey(USER_ID) && !bundle.getString(USER_ID).equals("")) {
            parameters.put(AdjustKeys.USER_ID, bundle.getString(USER_ID));
        } else if (bundle.getParcelable(CUSTOMER) != null) {
            Customer customer = bundle.getParcelable(CUSTOMER);
            parameters.put(AdjustKeys.USER_ID, customer.getIdAsString());
        }
        parameters.put(AdjustKeys.APP_VERSION, getAppVersion());
        parameters.put(AdjustKeys.DISPLAY_SIZE, getScreenSizeInches().toString());
        parameters.put(AdjustKeys.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        parameters.put(AdjustKeys.DEVICE_MODEL, Build.MODEL);
        
        return parameters;
    }

    private Map<String, String> getFBBaseParameters(Map<String, String> parameters, Bundle bundle) {
        parameters = getBaseParameters(parameters, bundle);
        parameters.put(AdjustKeys.DEVICE, getDeviceType(bundle.getBoolean(DEVICE, false)));
        Address address = getAddressFromLocation();
        if (null != address) {
            parameters.put(AdjustKeys.REGION, address.getAdminArea()); 
            parameters.put(AdjustKeys.CITY, address.getLocality()); 
        }
        
        if (bundle.getParcelable(CUSTOMER) != null) {
            Customer customer = bundle.getParcelable(CUSTOMER);
            parameters.put(AdjustKeys.GENDER, getGender(customer));
        }       
 
        parameters.put(AdjustKeys.AMOUNT_TRANSACTIONS, getTransationCount()); 
        parameters.put(AdjustKeys.AMOUNT_SESSIONS, getSessionsCount());         
        
        return parameters;
    }
    
    private String getDuration(long begin) {

        long current = System.currentTimeMillis();
        long result = current - begin;

        return String.valueOf(result);
    }
    
    private String getGender(Customer customer){
        String gender = "";
        if (customer.getGender() != CustomerGender.Female && customer.getGender() != CustomerGender.Male) {
            gender = CustomerGender.UNKNOWN.name();
        } else {
            gender = customer.getGender().name();
        }
        
        return gender;        
    }

    private String getAppVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
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

    /**
     * gets the number of sessions in the device
     * 
     * @param context
     * @return
     */
    private String getSessionsCount() {
        SharedPreferences settings = mContext.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE);
        String sessionCount = String.valueOf(settings.getInt(SESSION_COUNTER, 0));
        return sessionCount;
    }    
    
    private String getTransationCount() {
        SharedPreferences settings = mContext.getSharedPreferences(Ad4PushTracker.AD4PUSH_PREFERENCES, Context.MODE_PRIVATE);
        int purchasesNumber = settings.getInt(Ad4PushTracker.PURCHASE_NUMBER, 0);
        
        return String.valueOf(purchasesNumber);
    }    

    public Address getAddressFromLocation() {
        Address currAddressLocation = null;
        // From geo location
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // From last known geo location
        currAddressLocation = getAddressFromLastKnownLocation(locationManager); 
     
        return currAddressLocation;
    }
    
    private Address getAddressFromLastKnownLocation(LocationManager locationManager) {
        Address address = null;
        try {
            String bestProvider = getBestLocationProvider(locationManager);
            if(bestProvider != null) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
                double lat = lastKnownLocation.getLatitude();
                double lng = lastKnownLocation.getLongitude();
                address = getAddressFromGeoCode(lat, lng);
            }
            
        } catch (Exception e) {
            Log.w(TAG, "GET ADDRESS EXCEPTION: " + e.getMessage());
        }
        
        return address;
        
    }
    
    private Address getAddressFromGeoCode(double lat, double lng) {
        Address geoAddress = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (0 < addresses.size()) {
                geoAddress = addresses.get(0);
            }
        } catch (Exception e) {
            Log.w(TAG, "GET ADDRESS EXCEPTION: " + e.getMessage());
        }
        
        return geoAddress;
    }
    
    /**
     * Get the best location provider GPS or Network 
     * @param locationManager
     * @return String
     * @author sergiopereira
     */
    private String getBestLocationProvider(LocationManager locationManager){
        // Get the best provider
        String bestProvider = null;
        // Validate if GPS is enabled
        if(checkConnection() && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            bestProvider = LocationManager.NETWORK_PROVIDER;
        }    
        // Validate if GPS disabled, connection and Network provider
        if(bestProvider == null && checkConnection() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            bestProvider = LocationManager.GPS_PROVIDER;
        }
        
        // Return provider
        Log.i(TAG, "SELECTED PROVIDER: " + bestProvider);
        return bestProvider;
    }
    
    /**
     * Check the connection
     * @return true or false
     * @author sergiopereira
     */
    private boolean checkConnection() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    
    
}
