package com.mobile.utils.deeplink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.preferences.ShopPreferences;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.catalog.CatalogSort;
import com.mobile.utils.location.LocationHelper;
import com.mobile.view.R;
import com.mobile.view.fragments.CampaignsFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Class used to manage the deep link process
 *
 * @author sergiopereira
 */
public class DeepLinkManager {

    public static final String TAG = DeepLinkManager.class.getSimpleName();

    public static final String DEEP_LINK_PAGE_INDICATION = "u";

    public static final String EXTRA_GCM_PAYLOAD = "com.ad4screen.sdk.extra.GCM_PAYLOAD";

    private static final int PATH_CC_POS = 0;
    private static final int PATH_VIEW_POS = 1;
    private static final int PATH_DATA_POS = 2;
    public static final int FROM_GCM = 0;
    public static final int FROM_URI = 1;
    public static final int FROM_UNKNOWN = -1;
    private static final int CC_SIZE = 2;
    private static final int MIN_SEGMENTS = 3;
    private static final String DEFAULT_TAG = "default";
    private static final String CATALOG_TAG = "c";
    private static final String CATALOG_RATING_TAG = "cbr";
    private static final String CATALOG_POPULARITY_TAG = "cp";
    private static final String CATALOG_NEW_TAG = "cin";
    private static final String CATALOG_PRICE_UP_TAG = "cpu";
    private static final String CATALOG_PRICE_DOWN_TAG = "cpd";
    private static final String CATALOG_NAME_TAG = "cn";
    private static final String CATALOG_BRAND_TAG = "cb";
    private static final String CART_TAG = "cart";
    private static final String PDV_TAG = "d";
    private static final String SEARCH_TERM_TAG = "s";
    private static final String ORDER_OVERVIEW_TAG = "o";
    private static final String CAMPAIGN_TAG = "camp";
    private static final String LOGIN_TAG = "l";
    private static final String REGISTER_TAG = "r";
    private static final String NEWSLETTER_TAG = "news";
    private static final String RECENTLY_VIEWED_TAG = "rv";
    private static final String RECENT_SEARCHES_TAG = "rc";
    private static final String FAVORITES_TAG = "w";
    private static final String SHOPS_IN_SHOP_TAG = "ss";
    public static final String FRAGMENT_TYPE_TAG = "fragment_type";
    public static final String PDV_SIZE_TAG = "size";
    public static final String COUNTRY_TAG = "country";

    /**
     * Load the external deep link.<br>
     * Get and set the country Load the deep view and create a bundle<br>
     * # Default case -> JUMIA://com.mobile.jumia.dev/eg/cart/<br>
     * # Other case   -> JUMIA://eg/cart/<br>
     * @author sergiopereira
     */
    public static Bundle loadDeepLink(Uri data) {
        // Validate deep link
        List<String> segments = isValidLink(data);
        // Get the tag view and return values
        return loadDeepViewTag(data, segments);
    }

    /**
     * Validate the deep link data<br>
     *  - Case from GCM: JUMIA://eg/cart/<br>
     *  - Case from URI: JUMIA://com.jumia.android/eg/cart<br>
     * @param data The Uri
     * @return A list of segments should be something like this [eg, cart]
     * @author sergiopereira
     */
    private static List<String> isValidLink(Uri data) {
        // Get host and path
        String host = data.getHost();
        List<String> segments = data.getPathSegments();
        Print.i(TAG, "DEEP LINK URI HOST: " + data.getHost() + " PATH: " + data.getPathSegments());
        // Get deep link origin
        int origin = validateDeepLinkOrigin(host);
        // Case empty
        if (CollectionUtils.isEmpty(segments)) {
            Print.w(TAG, "WARNING: DEEP LINK IS EMPTY");
            // Validate if theres an host and add the default tag in order to redirect to Home screen if
            // the deeplink comes with format: android-app://com.jumia.android.dev/jumia/ke
            if(!TextUtils.isEmpty(host)){
                Print.w(TAG, "ADD DEFAULT TAG");
                ArrayList<String> arrayList = new ArrayList<>(segments);
                arrayList.add(DEFAULT_TAG);
                segments = arrayList;
            } else {
                return segments;
            }

        }
        // Case from URI: JUMIA://com.mobile.jumia.dev/eg/cart
         if(origin == FROM_URI) {
            // Add country code
            ArrayList<String> arrayList = new ArrayList<>(segments);
            arrayList.add(PATH_CC_POS, host);
            segments = arrayList;
            Print.i(TAG, "DEEP LINK FROM URI: " + segments.toString());
        }
        // Case from GCM: JUMIA://eg/cart/
        else {
            Print.i(TAG, "DEEP LINK FROM GCM: " + segments.toString());
        }
        // Return segments
        return segments;
    }

    /**
     * specifies if the deep link is FROM_URI or FROM_GCM
     */
    private static int validateDeepLinkOrigin(String host){
        // Get deep link origin
        return  !TextUtils.isEmpty(host) && host.length() == CC_SIZE ? FROM_URI : FROM_GCM;
    }

    /**
     * Load the deep link view to create the respective bundle for that view.
     *
     * @param data The URI.
     * @param segments The list of segments from URI.
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle loadDeepViewTag(Uri data, List<String> segments) {
        Print.i(TAG, "DEEP LINK URI: " + data + " " + segments);
        //
        Bundle bundle;
        String country = "";
        try {
            // Default case
            String tag = DEFAULT_TAG;

            // Validate current URI size
            if (CollectionUtils.isNotEmpty(segments) && segments.size() >= 2) {
                tag = segments.get(PATH_VIEW_POS);
                country = segments.get(PATH_CC_POS);
            }
            // Get bundle
            switch (tag) {
                case CATALOG_TAG:
                    bundle = processCatalogLink(CatalogSort.POPULARITY, segments, data);
                    break;
                case CATALOG_RATING_TAG:
                    bundle = processCatalogLink(CatalogSort.BESTRATING, segments, data);
                    break;
                case CATALOG_POPULARITY_TAG:
                    bundle = processCatalogLink(CatalogSort.POPULARITY, segments, data);
                    break;
                case CATALOG_NEW_TAG:
                    bundle = processCatalogLink(CatalogSort.NEWIN, segments, data);
                    break;
                case CATALOG_PRICE_UP_TAG:
                    bundle = processCatalogLink(CatalogSort.PRICE_UP, segments, data);
                    break;
                case CATALOG_PRICE_DOWN_TAG:
                    bundle = processCatalogLink(CatalogSort.PRICE_DOWN, segments, data);
                    break;
                case CATALOG_NAME_TAG:
                    bundle = processCatalogLink(CatalogSort.NAME, segments, data);
                    break;
                case CATALOG_BRAND_TAG:
                    bundle = processCatalogLink(CatalogSort.BRAND, segments, data);
                    break;
                case CART_TAG:
                    bundle = processCartLink(segments);
                    break;
                case PDV_TAG:
                    bundle = processPdvLink(segments, data);
                    break;
                case LOGIN_TAG:
                    bundle = processLoginLink();
                    break;
                case REGISTER_TAG:
                    bundle = processRegisterLink();
                    break;
                case SEARCH_TERM_TAG:
                    bundle = processSearchTermLink(segments.get(PATH_DATA_POS));
                    break;
                case ORDER_OVERVIEW_TAG:
                    bundle = processOrderStatus(segments.get(PATH_DATA_POS));
                    break;
                case CAMPAIGN_TAG:
                    bundle = processCampaignLink(segments.get(PATH_DATA_POS));
                    break;
                case NEWSLETTER_TAG:
                    bundle = processNewsletterLink();
                    break;
                case RECENTLY_VIEWED_TAG:
                    bundle = processRecentViewedLink();
                    break;
                case RECENT_SEARCHES_TAG:
                    bundle = processRecenteSearchesLink();
                    break;
                case FAVORITES_TAG:
                    bundle = processFavoritesLink();
                    break;
                case SHOPS_IN_SHOP_TAG:
                    bundle = processShopsInShopLink(segments.get(PATH_DATA_POS));
                    break;
                default:
                    bundle = processHomeLink();
                    break;
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Print.w(TAG, "ON LOAD DATA FROM DEEP VIEW TAG", e);
            bundle = processHomeLink();
        }
        bundle = addOriginGroupType(data, bundle);
        bundle.putString(COUNTRY_TAG, country);
        return bundle;
    }

    /**
     *  method that adds the Deep link origin to all bundles
     */
    private static Bundle addOriginGroupType(Uri data, Bundle bundle){
        if(bundle != null && data != null){
            bundle.putInt(ConstantsIntentExtra.DEEP_LINK_ORIGIN, validateDeepLinkOrigin(data.getHost()));
            return bundle;
        } else {
            return new Bundle();
        }
    }

    /**
     * Method used to create a bundle for campaign view with the respective campaign id. JUMIA://com.jumia.android/ng/camp/deals-of-the-day
     *
     * @param campaignId The campaign id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCampaignLink(String campaignId) {
        Print.i(TAG, "DEEP LINK TO CAMPAIGN: " + campaignId);
        // Create bundle
        Bundle bundle = new Bundle();
        ArrayList<TeaserCampaign> teaserCampaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign(campaignId.replace("-", " "), campaignId);
        teaserCampaigns.add(campaign);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, teaserCampaigns);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CAMPAIGNS);
        return bundle;
    }

    /**
     * Method used to create a bundle for track order view with the order id. JUMIA://com.jumia.android/ng/o/1233
     *
     * @param orderId order id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processOrderStatus(String orderId) {
        Print.i(TAG, "DEEP LINK TO TRACK ORDER: " + orderId);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.ARG_1, orderId);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.ORDER_STATUS);
        return bundle;
    }

    /**
     * Method used to create a bundle for catalog view with the search query. <p>JUMIA://com.mobile.jumia.dev/ng/s/cart <p>key: u value: ng/s/cart
     *
     * @param query The search query.
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processSearchTermLink(String query) {
        Print.i(TAG, "DEEP LINK TO SEARCH: " + query);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, query);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, query);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CATALOG);
        return bundle;
    }

    /**
     * Method used to create a bundle for cart or headless cart view with the respective SKUs. JUMIA://com.jumia.android/ng/cart
     * JUMIA://com.jumia.android/ng/cart/sku1_sku2_sku3
     *
     * @param segments The list of segments from URI.
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCartLink(List<String> segments) {
        Print.i(TAG, "DEEP LINK TO CART");
        // Default link
        String simpleSkuArray;
        FragmentType fragmentType = FragmentType.SHOPPING_CART;
        Bundle bundle = new Bundle();
        // Validate if has multiple SKUs
        if (segments.size() > 2) {
            // Add SKUs for HEADLESS_CART
            simpleSkuArray = segments.get(PATH_DATA_POS);
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, simpleSkuArray);
            Print.i(TAG, "DEEP LINK TO CART WITH: " + simpleSkuArray + " " + fragmentType.toString());
        }
        // Create bundle for fragment
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(FRAGMENT_TYPE_TAG, fragmentType);
        return bundle;
    }


    /**
     * Method used to create a bundle for PDV view with the respective product SKU and size. JUMIA://ng/d/HO525HLAC8VKAFRAMZ?size=6.5
     *
     * @param segments The list of segments from URI.
     * @param data The URI.
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processPdvLink(List<String> segments, Uri data) {
        Print.i(TAG, "DEEP LINK TO PDV: " + data.toString());
        // Get SKU
        String sku = segments.get(PATH_DATA_POS);
        // Get simple
        String size = data.getQueryParameter(PDV_SIZE_TAG);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(GetProductHelper.SKU_TAG, sku);
        bundle.putString(PDV_SIZE_TAG, size);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.PRODUCT_DETAILS);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processLoginLink() {
        Print.i(TAG, "DEEP LINK TO LOGIN");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.LOGIN);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processRegisterLink() {
        Print.i(TAG, "DEEP LINK TO REGISTER");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.REGISTER);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processNewsletterLink() {
        Print.i(TAG, "DEEP LINK TO NEWSLETTER");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.EMAIL_NOTIFICATION);
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.LOGIN);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processRecentViewedLink() {
        Print.i(TAG, "DEEP LINK TO RECENT VIEWED");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.RECENTLY_VIEWED_LIST);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processRecenteSearchesLink() {
        Print.i(TAG, "DEEP LINK TO RECENT SEARCHES");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.RECENT_SEARCHES_LIST);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processFavoritesLink() {
        Print.i(TAG, "DEEP LINK TO FAVOURITES");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.WISH_LIST);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processHomeLink() {
        Print.i(TAG, "DEEP LINK TO HOME");
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.HOME);
        return bundle;
    }

    /**
     * Method used to create a bundle for Catalog view with the respective catalog value. JUMIA://com.jumia.android/eg/c/surprise-your-guests?q=AKOZ--225&price=11720-53620&color_family=Noir--Bleu&size=38--40
     *
     * @param segments The list of segments from URI.
     * @param data The URI.
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCatalogLink(CatalogSort page, List<String> segments, Uri data) {
        // Create bundle
        Bundle bundle = new Bundle();
        // case ng/c/womens-dresses&sort=price&dir=asc
        String deeplinkUrl = data.toString();
        if(segments.size() >= MIN_SEGMENTS){
            String catalogUrlKey = "?category=" + segments.get(PATH_DATA_POS);
            //String catalogUrlKey = deeplinkUrl.substring(deeplinkUrl.indexOf('?'));
            Print.i(TAG, "DEEP LINK TO CATALOG: " + catalogUrlKey);

            bundle.putString(ConstantsIntentExtra.CONTENT_URL, catalogUrlKey);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            bundle.putString(ConstantsIntentExtra.CATALOG_QUERIE, deeplinkUrl);
            bundle.putInt(ConstantsIntentExtra.CATALOG_SORT, page.ordinal());
            bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CATALOG);
        }

        return bundle;
    }

    /**
     * Method used to create a bundle for shops in shop view with the respective shop id.<br/>
     * JUMIA://com.jumia.android/ug/ss/lego_shop_en_UG
     *
     * @param innerShopId The shop id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processShopsInShopLink(String innerShopId) {
        Print.i(TAG, "DEEP LINK TO SHOPS IN SHOP: " + innerShopId);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, innerShopId.replaceAll("-", " "));
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, innerShopId);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.INNER_SHOP);
        return bundle;
    }

    /**
     * Validate if is a valid link <p># Default case -> JUMIA://com.mobile.jumia.dev/eg/ <p># Other case   -> JUMIA://eg/
     *
     * @param host The host from path
     * @param segments A list of segments
     * @return list of segments
     */
//    @Deprecated
//    private static List<String> isValidLink(String host, List<String> segments) {
//        Print.i(TAG, "DEEP LINK URI HOST: " + host);
//        Print.d(TAG, "DEEP LINK URI PATH: " + segments.toString());
//         Validate segments
//        if (segments.size() == 0) {
//            Print.w(TAG, "RECEIVED DEEP LINK WITHOUT SEGMENTS");
//
            // Case -> JUMIA://ng/
//            if (isSupportedCountryCode(host)) {
//                List<String> array = new ArrayList<>();
//                array.add(host);
//                return array;

                // Case -> JUMIA://XXXX/
//            } else {
//                return null;
//            }

            // Case -> JUMIA://eg/cart/
//        } else if (isSupportedCountryCode(host)) {
//            List<String> array = new ArrayList<>();
//            array.addAll(segments);
//            array.add(PATH_CC_POS, host);
//            return array;
//        }
//
        // Return default segments
        // Case -> JUMIA://com.mobile.jumia.dev/eg/cart
//        return segments;
//    }

    /**
     *
     * Function that test if there is already a selected country, and if not validates if theres any from deeplink
     * @return true or false if there is a valid country from deeplink
     */
    public static boolean validateCountryDeepLink(Context context,Intent intent,Handler callback ){
        String selectedCountryCode = ShopPreferences.getShopId(context);
        Print.e(TAG, "selectedCountryCode:" + selectedCountryCode);
        // Validate saved shop id
        if (selectedCountryCode == ShopPreferences.SHOP_NOT_SELECTED) {
            return checkDeepLink(context, intent, callback);
        } else {
            Print.e(TAG, "DEEP LINK CC IS THE SAME");
            return false;
        }
    }

    /**
     * validates if the country from the deeplink is a valid one, and set the configurations
     *
     * @return true or false if there is a valid country from deeplink
     */
    private static boolean checkDeepLink(Context context, Intent intent, Handler callback){
        Print.e(TAG, "checkDeepLink:");
        Bundle mDeepLinkBundle = DeepLinkManager.hasDeepLink(intent);
        Print.e(TAG, "checkDeepLink:"+mDeepLinkBundle);
        if(mDeepLinkBundle != null){
            String countryCode = mDeepLinkBundle.getString(DeepLinkManager.COUNTRY_TAG);
            Print.e(TAG, "countryCode:"+countryCode);
            if(!TextUtils.isEmpty(countryCode)){
                LocationHelper.getInstance().initializeLocationHelper(context, callback);
                if(LocationHelper.getInstance().isCountryAvailable(countryCode)){
                    Print.i(TAG, "MATCH COUNTRY FROM DEEPLINK: " + countryCode);
                    LocationHelper.getInstance().sendInitializeMessage();
                    return true;
                }
                return false;
            }
            return false;
        } else {
          return false;
        }

    }

    /**
     * Locate the shop id and save it for a respective country code
     *
     * @param countryCode The country code
     * @author sergiopereira
     */
//    @Deprecated
//    private static void locateCountryCode(Context context, String countryCode) {
//         Validate countries available
//        if (JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0) {
//            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
//        }
        // Get the supported countries
//        if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
//             Get the shop id for the country code
//            for (int i = 0; i < JumiaApplication.INSTANCE.countriesAvailable.size(); i++) {
//                String supportedCountry = JumiaApplication.INSTANCE.countriesAvailable.get(i).getCountryIso();
//                Print.d(TAG, "SUPPORTED COUNTRY: " + supportedCountry);
//                if (supportedCountry.equalsIgnoreCase(countryCode)) {
//                    Print.d(TAG, "MATCH SUPPORTED COUNTRY: SHOP ID " + i + " " + countryCode);
//                    ShopPreferences.setShopId(context, i);
//                    break;
//                }
//            }
//        }
//    }

    /**
     * Locate the shop id and save it for a respective country code
     *
     * @param countryCode The country code
     * @author sergiopereira
     */
//    @Deprecated
//    private static boolean isSupportedCountryCode(String countryCode) {
//        if (JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0) {
//            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
//        }
//        if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
//            // Get the shop id for the country code
//            for (CountryObject supportedCountry : JumiaApplication.INSTANCE.countriesAvailable) {
//                if (supportedCountry.getCountryIso().equalsIgnoreCase(countryCode)) {
//                    return true;
//                }
//            }
//        }

//        return false;
//    }

    /**
     * Get all query parameters from Uri
     *
     * @param uri The URI.
     * @return set of keys
     */
//    private static Set<String> getQueryParameterNames(Uri uri) {
//        String query = uri.getEncodedQuery();
//        if (query == null) {
//            return Collections.emptySet();
//        }
//        Set<String> names = new LinkedHashSet<>();
//        int start = 0;
//        do {
//            int next = query.indexOf('&', start);
//            int end = (next == -1) ? query.length() : next;
//            int separator = query.indexOf('=', start);
//            if (separator > end || separator == -1) {
//                separator = end;
//            }
//            String name = query.substring(start, separator);
//            names.add(Uri.decode(name));
            // Move start to end of name.
//            start = end + 1;
//        } while (start < query.length());
//        return Collections.unmodifiableSet(names);
//    }

    /*
     * ############ DEEP LINK VALIDATIONS ############
     */
    /**
     * Create a deep link bundle from deep link intent.<br>
     * - From initial choose country<br>
     * - From external uri<br>
     * - From notification<br>
     */
    public static Bundle hasDeepLink(Intent intent) {
        Print.i(TAG, "DEEP LINK RECEIVED INTENT: " + intent.toString());
        // Create bundle from initial CC intent
        Bundle bundle = hasInitialChooseCountry(intent);
        // Create bundle from external URI intent
        if(bundle == null) {
            bundle = hasDeepLinkFromURI(intent);
        }
        // Create bundle from GCM intent
        if(bundle == null) {
            bundle = hasDeepLinkFromGCM(intent);
        }
        return bundle;
    }

    /**
     * Validate deep link from External URI.
     *
     * @param intent The CC intent from Splash Screen
     * @return Bundle or null
     * @author sergiopereira
     */
    private static Bundle hasInitialChooseCountry(Intent intent) {
        Print.i(TAG, "DEEP LINK: FROM INITIAL CHOOSE COUNTRY");
        Bundle bundle = null;
        // Validate intent
        if (intent.hasExtra(ConstantsIntentExtra.FRAGMENT_TYPE)) {
            Print.i(TAG, "DEEP LINK: VALID INTENT");
            // Get extras from notifications
            bundle = new Bundle();
            bundle.putSerializable(DeepLinkManager.FRAGMENT_TYPE_TAG, FragmentType.CHOOSE_COUNTRY);
            bundle.putBoolean(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
        }
        Print.i(TAG, "DEEP LINK: INVALID INTENT");
        return bundle;
    }

    /**
     * Validate deep link from External URI.
     *
     * @param intent The URI intent
     * @return Bundle or null
     * @author sergiopereira
     */
    private static Bundle hasDeepLinkFromURI(Intent intent) {
        Bundle bundle = null;
        // Get intent action ACTION_VIEW
        String action = intent.getAction();
        // Get intent data
        Uri data = intent.getData();
        // ## DEEP LINK FROM EXTERNAL URIs ##
        if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_VIEW) && data != null) {
            // ## Google Analytics "General Campaign Measurement" ##
//            TrackerDelegator.trackGACampaign(JumiaApplication.INSTANCE.getApplicationContext(), constructUTMLink(data));
            bundle = loadDeepLink(data);
            Print.i(TAG, "DEEP LINK: RECEIVED FROM URI");
        }
        return bundle;
    }

    /**
     * builds an utm string if the if the deeplink via URI has utm parameters
     * defaults values needed to be clarified by marketing, postponed to next release
     * @param data
     * @return
     */
//    private static String constructUTMLink(Uri data){
//        String campaign = data.getQueryParameter("utm_campaign");
//        String source = data.getQueryParameter("utm_source");
//        String medium = data.getQueryParameter("utm_medium");
//        String utmLink = "";
//       if(TextUtils.isEmpty(campaign) && TextUtils.isEmpty(source) && TextUtils.isEmpty(medium)){
//           return "";
//       } else if (!TextUtils.isEmpty(campaign) && !TextUtils.isEmpty(source) && !TextUtils.isEmpty(medium)) {
//           return "utm_campaign="+campaign+"&utm_source="+source+"&utm_medium="+medium;
//       } else {
//
//           if(!TextUtils.isEmpty(campaign)){
//               utmLink = "utm_campaign="+campaign;
//               if(!TextUtils.isEmpty(source)){
//                   utmLink = utmLink + "&utm_source="+ source;
//               } else if(!TextUtils.isEmpty(medium)){
//                   utmLink = utmLink + "&utm_medium="+ medium;
//               }
//           } else {
//               if(!TextUtils.isEmpty(source)){
//                   utmLink = "utm_source="+source;
//                   if(!TextUtils.isEmpty(medium)){
//                       utmLink = utmLink+"&utm_medium="+medium;
//                   }
//               } else if(!TextUtils.isEmpty(medium)){
//                   utmLink = "utm_medium="+medium;
//               }
//           }
//          return utmLink;
//       }
//    }

    /**
     * Validate deep link from Push Notification.
     *
     * @param intent The GCM intent.
     * @return true or false
     * @author sergiopereira
     */
    private static Bundle hasDeepLinkFromGCM(Intent intent) {
        Print.i(TAG, "DEEP LINK: FROM GCM");
        Bundle bundle = null;
        // ## DEEP LINK FROM NOTIFICATION ##
        Bundle payload = intent.getBundleExtra(EXTRA_GCM_PAYLOAD);
        // Get Deep link
        if (null != payload) {
            // Get UTM
            String mUtm = payload.getString(ConstantsIntentExtra.UTM_STRING);
            // ## Google Analytics "General Campaign Measurement" ##
            TrackerDelegator.trackGACampaign(JumiaApplication.INSTANCE.getApplicationContext(), mUtm);
            Print.i(TAG, "UTM FROM GCM: " + mUtm);
            // Get value from deep link key
            String deepLink = payload.getString(DEEP_LINK_PAGE_INDICATION);
            Print.i(TAG, "DEEP LINK: GCM " + deepLink);
            // Validate deep link
            if (!TextUtils.isEmpty(deepLink)) {
                // Create uri from the value
                Uri data = Uri.parse(deepLink);
                Print.d(TAG, "DEEP LINK URI: " + data.toString() + " " + data.getPathSegments().toString());
                // Load deep link
                bundle = loadDeepLink(data);
                TrackerDelegator.trackPushNotification();
                Print.i(TAG, "DEEP LINK: RECEIVED FROM GCM");
            }
        }
        return bundle;
    }

}
