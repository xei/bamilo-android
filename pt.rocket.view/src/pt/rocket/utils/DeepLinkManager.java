package pt.rocket.utils;

import java.util.ArrayList;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.database.CountriesConfigsTableHelper;
import pt.rocket.framework.objects.CountryObject;
import pt.rocket.framework.objects.TeaserCampaign;
import pt.rocket.helpers.search.GetSearchProductHelper;
import pt.rocket.preferences.ShopPreferences;
import pt.rocket.view.R;
import pt.rocket.view.fragments.CampaignsFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


/**
 * Class used to manage the deep link process
 * @author sergiopereira
 */
public class DeepLinkManager {
    
    public static final String TAG = DeepLinkManager.class.getSimpleName();
    
    private static final int PATH_CC_POS = 0;
    
    private static final int PATH_VIEW_POS = 1;
    
    private static final int PATH_DATA_POS = 2;
    
    private static final String CATALOG_TAG = "c";
    
    private static final String CART_TAG = "cart";
    
    private static final String PDV_TAG = "d";
    
    private static final String CATEGORY_TAG = "n";
    
    private static final String SEARCH_TERM_TAG = "s";
    
    private static final String ORDER_OVERVIEW_TAG = "o";
    
    private static final String CAMPAIGN_TAG = "cp";
    
    private static final String LOGIN_TAG = "l";
    
    private static final String REGISTER_TAG = "r";
    
    public static final String ADX_ID_TAG = "ADXID";
    
    public static final String FRAGMENT_TYPE_TAG = "fragment_type";
    
    public static final String PDV_SIZE_TAG = "size";
    
    /**
     * Load the external deep link.
     * Get and set the country
     * Load the deep view and create a bundle
     * Get and add the ADX value to the bundle
     * <p># Default case -> JUMIA://pt.rocket.jumia.dev/eg/cart/
     * <p># Other case   -> JUMIA://eg/cart/
     * @param intent
     * @author sergiopereira
     */
    public static Bundle loadExternalDeepLink(Context context, Uri data){
        // Decode path
        List<String> segments = isValidLink(context, data.getHost(), data.getPathSegments());
        if(segments == null) return null;
        Log.d(TAG, "DEEP LINK SEGMENTS: " + segments.toString());
        // Get the country code
        loadCountryCode(context, segments.get(PATH_CC_POS));
        // Get the tag view
        Bundle deepLinkBundle = loadDeepViewTag(context, segments, data);
        // Get ADX parameters from URI are set on selectActivity
        getAdxValues(deepLinkBundle, data);
        // Return ADX values
        return deepLinkBundle;
    }
    
    /**
     * Validate if is a valid link
     * <p># Default case -> JUMIA://pt.rocket.jumia.dev/eg/
     * <p># Other case   -> JUMIA://eg/
     * @param context
     * @param host
     * @param segments
     * @return list of segments
     */
    private static List<String> isValidLink(Context context, String host, List<String> segments){
        Log.i(TAG, "DEEP LINK URI HOST: " + host);
        Log.d(TAG, "DEEP LINK URI PATH: " + segments.toString());
        // Validate segments
        if(segments.size() == 0) {
            Log.w(TAG, "RECEIVED DEEP LINK WITHOUT SEGMENTS");
           
            // Case -> JUMIA://ng/
            if(isSupporedCountryCode(context, host)) { 
                List<String> array = new ArrayList<String>(); 
                array.add(host);
                return array;
            
            // Case -> JUMIA://XXXX/
            } else return null;
            
        // Case -> JUMIA://eg/cart/ 
        } else if(isSupporedCountryCode(context, host)) {
            List<String> array = new ArrayList<String>();
            array.addAll(segments);
            array.add(PATH_CC_POS, host);
            return array;
        }
        
        // Return default segments
        // Case -> JUMIA://pt.rocket.jumia.dev/eg/cart
        return segments;
    }

    /**
     * Load the deep link view to create the respective bundle for that view
     * @param context
     * @param segments
     * @return {@link Bundle}
     * @author sergiopereira
     * @param data 
     */
    private static Bundle loadDeepViewTag(Context context, List<String> segments, Uri data){
        // 
        Bundle bundle = null;
        try {
            // Validate current URI size
            if(segments != null && segments.size() > 1){
                // Get the tag
                String tag = segments.get(PATH_VIEW_POS);
                // Catalog
                if(tag.equalsIgnoreCase(CATALOG_TAG))
                    bundle = processCatalogLink(segments.get(PATH_DATA_POS));
                // Cart
                else if(tag.equalsIgnoreCase(CART_TAG))
                    bundle = processCartLink(segments);
                // Product Details
                else if(tag.equalsIgnoreCase(PDV_TAG))
                    bundle = processPdvLink(segments, data);
                // Login
                else if(tag.equalsIgnoreCase(LOGIN_TAG))
                    bundle = processLoginLink();
                // Register
                else if(tag.equalsIgnoreCase(REGISTER_TAG))
                    bundle = processRegisterLink();
                // Category
                else if(tag.equalsIgnoreCase(CATEGORY_TAG))
                    bundle = processCategoryLink(segments.get(PATH_DATA_POS));
                // Search term
                else if(tag.equalsIgnoreCase(SEARCH_TERM_TAG))
                    bundle = processSearchTermLink(segments.get(PATH_DATA_POS));
                // Order overview
                else if(tag.equalsIgnoreCase(ORDER_OVERVIEW_TAG))
                    bundle = processTrackOrderLink(segments.get(PATH_DATA_POS));
                // Campaign
                else if(tag.equalsIgnoreCase(CAMPAIGN_TAG))
                    bundle = processCampaignLink(segments.get(PATH_DATA_POS));
            } else {
                // Home
                bundle = processHomeLink();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "ON LOAD DATA FROM DEEP VIEW TAG: " + segments.toString(), e);
        } catch (NullPointerException e) {
            Log.w(TAG, "ON LOAD THE DEEP VIEW TAG", e);
        }
        return bundle;
    }
    
    
    /**
     * Method used to create a bundle for campaign view with the respective campaign id.
     * JUMIA://com.jumia.android/ng/cp/deals-of-the-day?ADXID=SOMEADXID
     * @param campaign id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCampaignLink(String campaignId) {
        Log.i(TAG, "DEEP LINK TO CAMPAIGN: " + campaignId);
        // Create bundle
        Bundle bundle = new Bundle();
        ArrayList<TeaserCampaign> teaserCampaigns = new ArrayList<TeaserCampaign>();
        TeaserCampaign campaign = new TeaserCampaign();
        campaign.setTitle(campaignId);
        campaign.setUrl(campaignId);
        teaserCampaigns.add(campaign);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, teaserCampaigns);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CAMPAIGNS);
        return bundle;
    }
    
    
    /**
     * Method used to create a bundle for category view with the respective category id.
     * JUMIA://com.jumia.android/ng/n/5121?ADXID=SOMEADXID
     * @param category id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCategoryLink(String categoryId) {
        Log.i(TAG, "DEEP LINK TO CATEGORY: " + categoryId);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CATEGORY_URL, null);
        bundle.putString(ConstantsIntentExtra.CATEGORY_ID, categoryId);
        bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CATEGORIES_LEVEL_1);
        return bundle;
    }
    
    /**
     * Method used to create a bundle for track order view with the order id.
     * JUMIA://com.jumia.android/ng/o/1233?ADXID=XXXX
     * @param order id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processTrackOrderLink(String orderId) {
        Log.i(TAG, "DEEP LINK TO TRACK ORDER: " + orderId);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, orderId);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.TRACK_ORDER);
        return bundle;
    }
    
    /**
     * Method used to create a bundle for catalog view with the search query.
     * <p>JUMIA://pt.rocket.jumia.dev/ng/s/cart?ADXID=web1253325978
     * <p>key: u value: ng/s/cart
     * @param query
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processSearchTermLink(String query) {
        Log.i(TAG, "DEEP LINK TO SEARCH: " + query);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, query);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, query);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.PRODUCT_LIST);
        return bundle;
    }
    
    /**
     * Method used to create a bundle for cart or headless cart view with the respective SKUs.
     * JUMIA://com.jumia.android/ng/cart?ADXID=web1253325978
     * JUMIA://com.jumia.android/ng/cart/sku1_sku2_sku3?ADXID=web1253325978
     * @param segments
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCartLink(List<String> segments) {
        Log.i(TAG, "DEEP LINK TO CART");
        // Default link 
        String simpleSkuArray = null;
        FragmentType fragmentType = FragmentType.SHOPPING_CART;
        // Validate if has multiple SKUs
        if(segments.size() > 2) {
            // Add SKUs for HEADLESS_CART
            simpleSkuArray = segments.get(PATH_DATA_POS);
            fragmentType = FragmentType.HEADLESS_CART;
            Log.i(TAG, "DEEP LINK TO CART WITH: " + simpleSkuArray + " " + fragmentType.toString());
        }
        // Create bundle for fragment
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, simpleSkuArray);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(FRAGMENT_TYPE_TAG, fragmentType);
        return bundle;
    }
    
    
    /**
     * Method used to create a bundle for PDV view with the respective product SKU and size.
     * JUMIA://ng/d/HO525HLAC8VKAFRAMZ?size=6.5
     * @param segments
     * @param data 
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processPdvLink(List<String> segments, Uri data) {
        Log.i(TAG, "DEEP LINK TO PDV: " + data.toString());
        // Get SKU
        String sku = segments.get(PATH_DATA_POS);
        // Get simple
        String size = data.getQueryParameter(PDV_SIZE_TAG);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(GetSearchProductHelper.SKU_TAG, sku);
        bundle.putString(PDV_SIZE_TAG, size);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.PRODUCT_DETAILS);
        return bundle;
    }
    
    /**
     * Method used to create a bundle for Home
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processLoginLink() {
        Log.i(TAG, "DEEP LINK TO HOME");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.LOGIN);
        return bundle;
    }
    
    /**
     * Method used to create a bundle for Home
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processRegisterLink() {
        Log.i(TAG, "DEEP LINK TO HOME");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.REGISTER);
        return bundle;
    }
    
    
    /**
     * Method used to create a bundle for Home
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processHomeLink() {
        Log.i(TAG, "DEEP LINK TO HOME");
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.HOME);
        return bundle;
    }
    
    /**
     * Method used to create a bundle for Catalog view with the respective catalog value. 
     * JUMIA://com.jumia.android/eg/c/surprise-your-guests?ADXID=XXXX
     * @param catalog
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCatalogLink(String catalog) {
        Log.i(TAG, "DEEP LINK TO CATALOG: " + catalog);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, "https:/" + catalog);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.PRODUCT_LIST);
        return bundle;
    }
    
    /**
     * Get the adx id value and add it to the received bundle
     * @param deepLinkBundle
     * @param data
     * @author sergiopereira
     */
    private static void getAdxValues(Bundle deepLinkBundle, Uri data){
        // Validate current bundle
        if(deepLinkBundle == null || data == null) return;
        try {
            // Get the adx id
            String adxIdValue = data.getQueryParameter(ADX_ID_TAG);
            // Add to bundle
            deepLinkBundle.putString(ADX_ID_TAG, adxIdValue);
        } catch (UnsupportedOperationException e) {
            Log.w(TAG, "ON GET ADX VALUE FROM: " + data.toString(), e);
            deepLinkBundle.putString(ADX_ID_TAG, null);
        }
    }

    
    /**
     * Load the country and set
     * @param context
     * @param countryCode
     * @author sergiopereira
     */
    private static void loadCountryCode(Context context, String countryCode){
        Log.d(TAG, "DEEP LINK URI PATH: " + countryCode);
        // Get current country code
        String selectedCountryCode = ShopPreferences.getShopId(context);
        // Validate saved shop id
        if(selectedCountryCode == ShopPreferences.SHOP_NOT_SELECTED || !selectedCountryCode.equalsIgnoreCase(countryCode))
            locateCountryCode(context, countryCode);
        else
            Log.i(TAG, "DEEP LINK CC IS THE SAME");
    }

    /**
     * Locate the shop id and save it for a respective country code
     * @param countryCode
     * @author sergiopereira
     */
    private static void locateCountryCode(Context context, String countryCode) {
        // Valdiate countries available
        if(JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0 )
            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        // Get the supported countries
        if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0 ){
         // Get the shop id for the country code 
            for (int i = 0; i < JumiaApplication.INSTANCE.countriesAvailable.size(); i++) { 
                String supportedCountry = JumiaApplication.INSTANCE.countriesAvailable.get(i).getCountryIso();
                Log.d(TAG, "SUPPORTED COUNTRY: " + supportedCountry);
                if (supportedCountry.equalsIgnoreCase(countryCode)){
                    Log.d(TAG, "MATCH SUPPORTED COUNTRY: SHOP ID " + i + " " + countryCode);
                    ShopPreferences.setShopId(context, i);
                    break;
                }
            }
        }
    }
    
    /**
     * Locate the shop id and save it for a respective country code
     * @param countryCode
     * @author sergiopereira
     */
    private static boolean isSupporedCountryCode(Context context, String countryCode) {
        if(JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0 ){
            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        }
        if(JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0 ){
            // Get the shop id for the country code 
            for (CountryObject supportedCountry : JumiaApplication.INSTANCE.countriesAvailable) if (supportedCountry.getCountryIso().equalsIgnoreCase(countryCode)) return true;            
        }

        return false;
    }

}
