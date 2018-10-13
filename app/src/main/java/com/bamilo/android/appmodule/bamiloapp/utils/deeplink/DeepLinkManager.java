package com.bamilo.android.appmodule.bamiloapp.utils.deeplink;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.home.TeaserCampaign;
import com.bamilo.android.framework.service.rest.RestUrlUtils;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.preferences.ShopPreferences;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.CatalogSort;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.UICatalogUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.location.LocationHelper;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CampaignsFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Class used to manage the deep link process
 *
 * @author sergiopereira
 */
public class DeepLinkManager {

    private static final String TAG = DeepLinkManager.class.getSimpleName();

    public static final String DEEP_LINK_PAGE_INDICATION = ConstantsIntentExtra.DEEP_LINK_TAG;

    public static final String EXTRA_GCM_PAYLOAD = "com.ad4screen.sdk.extra.GCM_PAYLOAD";

    // ORIGIN
    public static final int FROM_GCM = 0;
    public static final int FROM_URI = 1;
    public static final int FROM_UNKNOWN = -1;
    // PATH POSITION
    private static final int PATH_CC_POS = 0;
    private static final int PATH_VIEW_POS = 1;
    private static final int PATH_DATA_POS = 2;
    // SIZES
    private static final int CC_SIZE = 2;
    private static final int CATALOG_MIN_SEGMENTS = 3;
    // LINK TYPES
    private static final String DEFAULT_TAG = "default";
    private static final String CATALOG_BRAND_TAG = "b";
    private static final String CATALOG_SELLER_TAG = "sc";
    private static final String CATALOG_TAG = "c";
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
    public static final String PDV_SIZE_TAG = "size";
    private static final String COUNTRY_TAG = "country";
    // CATALOG SORT TYPES
    private static final String SORT_RATING = "br";
    private static final String SORT_POPULARITY = "p";
    private static final String SORT_NEW = "in";
    private static final String SORT_PRICE_UP = "pu";
    private static final String SORT_PRICE_DOWN = "pd";
    private static final String SORT_NAME = "n";
    private static final String SORT_BRAND = "b";
    private static String query_ex ="";

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
        String path =data.getPath();
        query_ex = data.getEncodedQuery();
        List<String> segments = data.getPathSegments();
        // Get deep link origin
        int origin = validateDeepLinkOrigin(host);
        // Case empty
        if (CollectionUtils.isEmpty(segments)) {
            // Validate if theres an host and add the default tag in order to redirect to Home screen if
            // the deeplink comes with format: android-app://com.jumia.android.dev/jumia/ke
            if(!TextUtils.isEmpty(host)){
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
                case CATALOG_BRAND_TAG:
                    bundle = processCatalogBrandLink(segments.get(PATH_DATA_POS));
                    break;
                case CATALOG_TAG:
                case CATALOG_TAG + SORT_POPULARITY:
                    bundle = processCatalogCategoryLink(CatalogSort.POPULARITY, segments);
                    break;
                case CATALOG_TAG + SORT_RATING:
                    bundle = processCatalogCategoryLink(CatalogSort.BEST_RATING, segments);
                    break;
                case CATALOG_TAG + SORT_NEW:
                    bundle = processCatalogCategoryLink(CatalogSort.NEW_IN, segments);
                    break;
                case CATALOG_TAG + SORT_PRICE_UP:
                    bundle = processCatalogCategoryLink(CatalogSort.PRICE_UP, segments);
                    break;
                case CATALOG_TAG + SORT_PRICE_DOWN:
                    bundle = processCatalogCategoryLink(CatalogSort.PRICE_DOWN, segments);
                    break;
                case CATALOG_TAG + SORT_NAME:
                    bundle = processCatalogCategoryLink(CatalogSort.NAME, segments);
                    break;
                case CATALOG_TAG + SORT_BRAND:
                    bundle = processCatalogCategoryLink(CatalogSort.BRAND, segments);
                    break;
                case CATALOG_SELLER_TAG:
                case CATALOG_SELLER_TAG + SORT_POPULARITY:
                    bundle = processCatalogSellerLink(CatalogSort.POPULARITY, segments);
                    break;
                case CATALOG_SELLER_TAG + SORT_RATING:
                    bundle = processCatalogSellerLink(CatalogSort.BEST_RATING, segments);
                    break;
                case CATALOG_SELLER_TAG + SORT_NEW:
                    bundle = processCatalogSellerLink(CatalogSort.NEW_IN, segments);
                    break;
                case CATALOG_SELLER_TAG + SORT_PRICE_UP:
                    bundle = processCatalogSellerLink(CatalogSort.PRICE_UP, segments);
                    break;
                case CATALOG_SELLER_TAG + SORT_PRICE_DOWN:
                    bundle = processCatalogSellerLink(CatalogSort.PRICE_DOWN, segments);
                    break;
                case CATALOG_SELLER_TAG + SORT_NAME:
                    bundle = processCatalogSellerLink(CatalogSort.NAME, segments);
                    break;
                case CATALOG_SELLER_TAG + SORT_BRAND:
                    bundle = processCatalogSellerLink(CatalogSort.BRAND, segments);
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
                    //bundle = processOrderStatus(segments.get(PATH_DATA_POS));
                    bundle = processOrderStatus();
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
                    bundle = processRecentSearchesLink();
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
        // Create bundle
        Bundle bundle = new Bundle();
        ArrayList<TeaserCampaign> teaserCampaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign(campaignId.replace("-", " "), campaignId);
        teaserCampaigns.add(campaign);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, teaserCampaigns);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CAMPAIGNS);
        return bundle;
    }

    /**
     * Method used to create a bundle for track order view with the order id. JUMIA://com.jumia.android/ng/o/1233
     *
     * @param orderId order id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processOrderStatusById(String orderId) {
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.ARG_1, orderId);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.ORDER_STATUS);
        return bundle;
    }

    private static Bundle processOrderStatus() {
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.MY_ORDERS);
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
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DATA, null);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, query);
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, query);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CATALOG);
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
        // Default link
        String simpleSkuArray;
        FragmentType fragmentType = FragmentType.SHOPPING_CART;
        Bundle bundle = new Bundle();
        // Validate if has multiple SKUs
        if (segments.size() > 2) {
            // Add SKUs for HEADLESS_CART
            simpleSkuArray = segments.get(PATH_DATA_POS);
            bundle.putString(ConstantsIntentExtra.DATA, simpleSkuArray);
        }
        // Create bundle for fragment
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, fragmentType);
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
        // Get SKU
        String sku = segments.get(PATH_DATA_POS);
        // Get simple
        String size = data.getQueryParameter(PDV_SIZE_TAG);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
        bundle.putString(PDV_SIZE_TAG, size);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.PRODUCT_DETAILS);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processLoginLink() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.LOGIN);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processRegisterLink() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.REGISTER);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processNewsletterLink() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.EMAIL_NOTIFICATION);
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.LOGIN);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processRecentViewedLink() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.RECENTLY_VIEWED_LIST);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processRecentSearchesLink() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.RECENT_SEARCHES_LIST);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processFavoritesLink() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.WISH_LIST);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processHomeLink() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.HOME);
        return bundle;
    }

    /**
     * Class used to process the catalog category.
     * - "<cc>/c/<category_key>"<br>
     * - "<cc>/cbr/<category_key>"<br>
     * ...
     */
    private static Bundle processCatalogCategoryLink(CatalogSort page, List<String> segments) {
        return processCatalogLink(page, UICatalogUtils.getCategoryQueryParam(), segments);
    }

    /**
     * Class used to process the catalog seller.<br>
     * - "<cc>/sc/<seller_name>"<br>
     * - "<cc>/scbr/<seller_name>"<br>
     * ...
     */
    private static Bundle processCatalogSellerLink(CatalogSort page, List<String> segments) {
        return processCatalogLink(page, UICatalogUtils.getSellerQueryParam(), segments);
    }

    /**
     * Method used to create a bundle for Catalog view with the respective catalog value.<br>
     * JUMIA://com.jumia.android/eg/c/surprise-your-guests?q=AKOZ--225&price=11720-53620&color_family=Noir--Bleu&size=38--40
     * @param segments The list of segments from URI.
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCatalogLink(CatalogSort page, String key, List<String> segments) {
        // Create bundle
        Bundle bundle = new Bundle();
        if (segments.size() >= CATALOG_MIN_SEGMENTS) {
            String query = key + segments.get(PATH_DATA_POS);
            if (query_ex!=null)
            {
                ContentValues deepLinkValues = new ContentValues();
                if (TextUtils.isNotEmpty(query+"&"+query_ex)) {
                    deepLinkValues = RestUrlUtils.getQueryParameters(query+"&"+query_ex);
                }
                bundle.putParcelable(ConstantsIntentExtra.DATA, deepLinkValues);
                bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
                bundle.putInt(ConstantsIntentExtra.CATALOG_SORT, page.ordinal());
                bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CATALOG_DEEP_LINK);
            }
            else {
                ContentValues deepLinkValues = new ContentValues();
                if (TextUtils.isNotEmpty(query)) {
                    deepLinkValues = RestUrlUtils.getQueryParameters(query);
                }
                bundle.putParcelable(ConstantsIntentExtra.DATA, deepLinkValues);
                bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
                bundle.putInt(ConstantsIntentExtra.CATALOG_SORT, page.ordinal());
                bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CATALOG_DEEP_LINK);
            }


        }
        return bundle;
    }

    /**
     * Method used to create a bundle for Catalog view with the respective catalog brand value.<br>
     * @link "ng/b/<brand_key>"
     * @return {@link Bundle}
     */
    @NonNull
    private static Bundle processCatalogBrandLink(@NonNull String brand) {
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, brand);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CATALOG_BRAND);
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
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, innerShopId.replaceAll("-", " "));
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, innerShopId);
        bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.INNER_SHOP);
        return bundle;
    }

    /*
     * ############ DEEP LINK VALIDATIONS ############
     */

    /**
     *
     * Function that test if there is already a selected country, and if not validates if theres any from deeplink
     * @return true or false if there is a valid country from deeplink
     */
    public static boolean validateCountryDeepLink(Context context,Intent intent,Handler callback ){
        String selectedCountryCode = ShopPreferences.getShopId(context);
        // Validate saved shop id
        if (TextUtils.equals(selectedCountryCode, ShopPreferences.SHOP_NOT_SELECTED)) {
            return checkDeepLink(context, intent, callback);
        } else {
            return false;
        }
    }

    /**
     * validates if the country from the deeplink is a valid one, and set the configurations
     *
     * @return true or false if there is a valid country from deeplink
     */
    private static boolean checkDeepLink(Context context, Intent intent, Handler callback) {
        Bundle mDeepLinkBundle = DeepLinkManager.hasDeepLink(intent);
        if (mDeepLinkBundle != null) {
            String countryCode = mDeepLinkBundle.getString(DeepLinkManager.COUNTRY_TAG);
            if (!TextUtils.isEmpty(countryCode)) {
                LocationHelper.getInstance().initializeLocationHelper(context, callback);
                if (LocationHelper.getInstance().isCountryAvailable(countryCode)) {
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
     * Create a deep link bundle from deep link intent.<br>
     * - From initial choose country<br>
     * - From external uri<br>
     * - From notification<br>
     */
    public static Bundle hasDeepLink(Intent intent) {
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
        hasDeeplinkUTM(intent);
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
        Bundle bundle = null;
        // Validate intent
        if (intent.hasExtra(ConstantsIntentExtra.FRAGMENT_TYPE)) {
            // Get extras from notifications
            bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.CHOOSE_COUNTRY);
            bundle.putBoolean(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, true);
        }
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
        Uri uri = intent.getData();
        // ## DEEP LINK FROM EXTERNAL URIs ##
        if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_VIEW) && uri != null) {
            // ReAttribution
            TrackerDelegator.deepLinkReAttribution(uri);
            // Load deep link
            bundle = loadDeepLink(uri);
        }
        return bundle;
    }

    /**
     * Validate deep link from Push Notification.
     *
     * @param intent The GCM intent.
     * @return true or false
     * @author sergiopereira
     */
    private static Bundle hasDeepLinkFromGCM(Intent intent) {
        Bundle bundle = null;
        // ## DEEP LINK FROM NOTIFICATION ##
        Bundle payload = intent.getBundleExtra(EXTRA_GCM_PAYLOAD);
        // Get Deep link
        if (null != payload) {
            // Get UTM
            String mUtm = payload.getString(ConstantsIntentExtra.UTM_STRING);

            // ## Google Analytics "General Campaign Measurement" ##
            TrackerManager.setCampaignUrl(mUtm);
            // Get value from deep link key
            String deepLink = payload.getString(DEEP_LINK_PAGE_INDICATION);
            // Validate deep link
            if (TextUtils.isNotEmpty(deepLink)) {
                // Create uri from the value
                Uri uri = Uri.parse(deepLink);
                // ReAttribution
                TrackerDelegator.deepLinkReAttribution(uri);
                // Load deep link
                bundle = loadDeepLink(uri);
            }
        }
        return bundle;
    }

    private static void hasDeeplinkUTM(Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) return;

        TrackerManager.setCampaignUrl(uri.toString());
    }

    /**
     * Validate and process intent from notification
     * @return valid or invalid
     */
    public static boolean onSwitchToDeepLink(BaseActivity activity, Intent intent) {
        // Get deep link
        Bundle mDeepLinkBundle = DeepLinkManager.hasDeepLink(intent);
        // Validate deep link
        boolean isDeepLinkLaunch = launchValidDeepLink(activity, mDeepLinkBundle);
        // Track open app event for all tracker but Adjust
        TrackerDelegator.trackAppOpen(activity.getApplicationContext(), isDeepLinkLaunch);
        // return result
        return isDeepLinkLaunch;
    }

    /**
     * Validate and process intent from notification
     * @param bundle The deep link intent
     * @return valid or invalid
     */
    private static boolean launchValidDeepLink(BaseActivity activity, Bundle bundle) {
        if (bundle != null) {
            // Get fragment type
            FragmentType fragmentType = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.FRAGMENT_TYPE);
            //Print.d(TA G, "DEEP LINK FRAGMENT TYPE: " + fragmentType.toString());
            // Validate fragment type
            if (fragmentType != FragmentType.UNKNOWN) {
                // Restart back stack and fragment manager
                FragmentController.getInstance().popAllBackStack(activity);
                // Validate this step to maintain the base TAG
                activity.onSwitchFragment(FragmentType.HOME, bundle, FragmentController.ADD_TO_BACK_STACK);
                // Switch to fragment with respective bundle
                if(fragmentType != FragmentType.HOME) {
                    activity.onSwitchFragment(fragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);
                }
                return true;
            }
        }
        return false;
    }
}
