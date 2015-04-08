package com.mobile.utils.deeplink;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.database.CountriesConfigsTableHelper;
import com.mobile.framework.objects.CountryObject;
import com.mobile.framework.objects.TeaserCampaign;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.campaign.GetCampaignHelper;
import com.mobile.helpers.search.GetSearchProductHelper;
import com.mobile.preferences.ShopPreferences;
import com.mobile.utils.catalog.CatalogSort;
import com.mobile.view.R;
import com.mobile.view.fragments.CampaignsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.akquinet.android.androlog.Log;


/**
 * Class used to manage the deep link process
 *
 * @author sergiopereira
 */
public class DeepLinkManager {

    public static final String TAG = DeepLinkManager.class.getSimpleName();

    private static final int PATH_CC_POS = 0;

    private static final int PATH_VIEW_POS = 1;

    private static final int PATH_DATA_POS = 2;

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
    private static final String CATEGORY_TAG = "n";
    private static final String SEARCH_TERM_TAG = "s";
    private static final String ORDER_OVERVIEW_TAG = "o";
    private static final String CAMPAIGN_TAG = "camp";
    private static final String LOGIN_TAG = "l";
    private static final String REGISTER_TAG = "r";
    private static final String NEWSLETTER_TAG = "news";
    private static final String RECENTLY_VIEWED_TAG = "rv";
    private static final String RECENT_SEARCHES_TAG = "rc";
    private static final String FAVORITES_TAG = "w";

    public static final String FRAGMENT_TYPE_TAG = "fragment_type";

    public static final String PDV_SIZE_TAG = "size";

    /**
     * Load the external deep link. Get and set the country Load the deep view and create a bundle <p># Default case -> JUMIA://com.mobile.jumia.dev/eg/cart/
     * <p># Other case   -> JUMIA://eg/cart/
     *
     * @author sergiopereira
     */
    public static Bundle loadExternalDeepLink(Context context, Uri data) {
        // Decode path
        List<String> segments = isValidLink(context, data.getHost(), data.getPathSegments());
        if (segments == null) {
            return null;
        }
        // Get the country code
        loadCountryCode(context, segments.get(PATH_CC_POS));
        // Get the tag view and return values
        return loadDeepViewTag(segments, data);
    }

    /**
     * Validate if is a valid link <p># Default case -> JUMIA://com.mobile.jumia.dev/eg/ <p># Other case   -> JUMIA://eg/
     *
     * @param context
     * @param host
     * @param segments
     * @return list of segments
     */
    private static List<String> isValidLink(Context context, String host, List<String> segments) {
        Log.i(TAG, "DEEP LINK URI HOST: " + host);
        Log.d(TAG, "DEEP LINK URI PATH: " + segments.toString());
        // Validate segments
        if (segments.size() == 0) {
            Log.w(TAG, "RECEIVED DEEP LINK WITHOUT SEGMENTS");

            // Case -> JUMIA://ng/
            if (isSupporedCountryCode(host)) {
                List<String> array = new ArrayList<>();
                array.add(host);
                return array;

                // Case -> JUMIA://XXXX/
            } else {
                return null;
            }

            // Case -> JUMIA://eg/cart/
        } else if (isSupporedCountryCode(host)) {
            List<String> array = new ArrayList<>();
            array.addAll(segments);
            array.add(PATH_CC_POS, host);
            return array;
        }

        // Return default segments
        // Case -> JUMIA://com.mobile.jumia.dev/eg/cart
        return segments;
    }

    /**
     * Load the deep link view to create the respective bundle for that view
     *
     * @param segments
     * @param data
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle loadDeepViewTag(List<String> segments, Uri data) {
        // 
        Bundle bundle = null;
        try {
            // Validate current URI size
            if (segments != null && segments.size() > 1) {
                // Get the tag
                String tag = segments.get(PATH_VIEW_POS);
                // Catalog
                if (tag.equalsIgnoreCase(CATALOG_TAG)) {
                    bundle = processCatalogLink(CatalogSort.POPULARITY, segments, data);
                }
                // Catalog - Rating
                else if (tag.equalsIgnoreCase(CATALOG_RATING_TAG)) {
                    bundle = processCatalogLink(CatalogSort.BESTRATING, segments, data);
                }
                // Catalog - Popularity
                else if (tag.equalsIgnoreCase(CATALOG_POPULARITY_TAG)) {
                    bundle = processCatalogLink(CatalogSort.POPULARITY, segments, data);
                }
                // Catalog - New In
                else if (tag.equalsIgnoreCase(CATALOG_NEW_TAG)) {
                    bundle = processCatalogLink(CatalogSort.NEWIN, segments, data);
                }
                // Catalog - Price Up
                else if (tag.equalsIgnoreCase(CATALOG_PRICE_UP_TAG)) {
                    bundle = processCatalogLink(CatalogSort.PRICE_UP, segments, data);
                }
                // Catalog - Price Down
                else if (tag.equalsIgnoreCase(CATALOG_PRICE_DOWN_TAG)) {
                    bundle = processCatalogLink(CatalogSort.PRICE_DOWN, segments, data);
                }
                // Catalog - Name
                else if (tag.equalsIgnoreCase(CATALOG_NAME_TAG)) {
                    bundle = processCatalogLink(CatalogSort.NAME, segments, data);
                }
                // Catalog - Brand
                else if (tag.equalsIgnoreCase(CATALOG_BRAND_TAG)) {
                    bundle = processCatalogLink(CatalogSort.BRAND, segments, data);
                }
                // Cart
                else if (tag.equalsIgnoreCase(CART_TAG)) {
                    bundle = processCartLink(segments);
                }
                // Product Details
                else if (tag.equalsIgnoreCase(PDV_TAG)) {
                    bundle = processPdvLink(segments, data);
                }
                // Login
                else if (tag.equalsIgnoreCase(LOGIN_TAG)) {
                    bundle = processLoginLink();
                }
                // Register
                else if (tag.equalsIgnoreCase(REGISTER_TAG)) {
                    bundle = processRegisterLink();
                }
                // Category
                else if (tag.equalsIgnoreCase(CATEGORY_TAG)) {
                    bundle = processCategoryLink(segments.get(PATH_DATA_POS));
                }
                // Search term
                else if (tag.equalsIgnoreCase(SEARCH_TERM_TAG)) {
                    bundle = processSearchTermLink(segments.get(PATH_DATA_POS));
                }
                // Order overview
                else if (tag.equalsIgnoreCase(ORDER_OVERVIEW_TAG)) {
                    bundle = processTrackOrderLink(segments.get(PATH_DATA_POS));
                }
                // Campaign
                else if (tag.equalsIgnoreCase(CAMPAIGN_TAG)) {
                    bundle = processCampaignLink(segments.get(PATH_DATA_POS));
                }
                // Newsleter
                else if (tag.equalsIgnoreCase(NEWSLETTER_TAG)) {
                    bundle = processNewsletterLink();
                }
                // Recently Viewed
                else if (tag.equalsIgnoreCase(RECENTLY_VIEWED_TAG)) {
                    bundle = processRecentViewedLink();
                }
                // Recent Searches
                else if (tag.equalsIgnoreCase(RECENT_SEARCHES_TAG)) {
                    bundle = processRecenteSearchesLink();
                }
                // Favorites
                else if (tag.equalsIgnoreCase(FAVORITES_TAG)) {
                    bundle = processFavoritesLink();
                }

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
     * Method used to create a bundle for campaign view with the respective campaign id. JUMIA://com.jumia.android/ng/cam/deals-of-the-day
     *
     * @param campaign id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCampaignLink(String campaignId) {
        Log.i(TAG, "DEEP LINK TO CAMPAIGN: " + campaignId);
        // Create bundle
        Bundle bundle = new Bundle();
        ArrayList<TeaserCampaign> teaserCampaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign();
        campaign.setTitle(campaignId.replace("-", " "));
        campaign.setUrl(EventType.GET_CAMPAIGN_EVENT.action + "?" + GetCampaignHelper.CAMPAIGN_TAG + "=" + campaignId);
        teaserCampaigns.add(campaign);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, teaserCampaigns);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CAMPAIGNS);
        return bundle;
    }


    /**
     * Method used to create a bundle for category view with the respective category id. JUMIA://com.jumia.android/ng/n/5121
     *
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
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CATEGORIES);
        return bundle;
    }

    /**
     * Method used to create a bundle for track order view with the order id. JUMIA://com.jumia.android/ng/o/1233
     *
     * @param order id
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processTrackOrderLink(String orderId) {
        Log.i(TAG, "DEEP LINK TO TRACK ORDER: " + orderId);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, orderId);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.MY_ORDERS);
        return bundle;
    }

    /**
     * Method used to create a bundle for catalog view with the search query. <p>JUMIA://com.mobile.jumia.dev/ng/s/cart <p>key: u value: ng/s/cart
     *
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
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CATALOG);
        return bundle;
    }

    /**
     * Method used to create a bundle for cart or headless cart view with the respective SKUs. JUMIA://com.jumia.android/ng/cart
     * JUMIA://com.jumia.android/ng/cart/sku1_sku2_sku3
     *
     * @param segments
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCartLink(List<String> segments) {
        Log.i(TAG, "DEEP LINK TO CART");
        // Default link 
        String simpleSkuArray;
        FragmentType fragmentType = FragmentType.SHOPPING_CART;
        Bundle bundle = new Bundle();
        // Validate if has multiple SKUs
        if (segments.size() > 2) {
            // Add SKUs for HEADLESS_CART
            simpleSkuArray = segments.get(PATH_DATA_POS);
//            fragmentType = FragmentType.HEADLESS_CART;
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, simpleSkuArray);
            Log.i(TAG, "DEEP LINK TO CART WITH: " + simpleSkuArray + " " + fragmentType.toString());
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
     *
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processLoginLink() {
        Log.i(TAG, "DEEP LINK TO LOGIN");
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
        Log.i(TAG, "DEEP LINK TO REGISTER");
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
        Log.i(TAG, "DEEP LINK TO NEWSLETTER");
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
        Log.i(TAG, "DEEP LINK TO RECENT VIEWED");
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
        Log.i(TAG, "DEEP LINK TO RECENT SEARCHES");
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
        Log.i(TAG, "DEEP LINK TO FAVOURITES");
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.DEEP_LINK_TAG, TAG);
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.FAVORITE_LIST);
        return bundle;
    }

    /**
     * Method used to create a bundle for Home
     *
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
     * Method used to create a bundle for Catalog view with the respective catalog value. JUMIA://com.jumia.android/eg/c/surprise-your-guests?q=AKOZ--225&price=11720-53620&color_family=Noir--Bleu&size=38--40
     *
     * @param segments
     * @param data
     * @return {@link Bundle}
     * @author sergiopereira
     */
    private static Bundle processCatalogLink(CatalogSort page, List<String> segments, Uri data) {
        // Get catalog 
        String catalogUrlKey = segments.get(PATH_DATA_POS);
        // Get filters
        Set<String> filters = getQueryParameterNames(data);
        // Get all params
        if (filters.size() > 0) {
            catalogUrlKey += "?";
            for (String key : filters) {
                catalogUrlKey += key + "=" + data.getQueryParameter(key) + "&";
            }
        }
        // Log
        Log.i(TAG, "DEEP LINK TO CATALOG: " + catalogUrlKey);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, "https:/" + catalogUrlKey);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gpush_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putInt(ConstantsIntentExtra.CATALOG_SORT, page.ordinal());
        bundle.putSerializable(FRAGMENT_TYPE_TAG, FragmentType.CATALOG);
        return bundle;
    }

//    /**
//     * Get the adx id value and add it to the received bundle
//     * @param deepLinkBundle
//     * @param data
//     * @author sergiopereira
//     */
//    private static void getAdxValues(Bundle deepLinkBundle, Uri data){
//        // Validate current bundle
//        if(deepLinkBundle == null || data == null) return;
//        try {
//            // Get the adx id
//            String adxIdValue = data.getQueryParameter(ADX_ID_TAG);
//            // Add to bundle
//            deepLinkBundle.putString(ADX_ID_TAG, adxIdValue);
//        } catch (UnsupportedOperationException e) {
//            Log.w(TAG, "ON GET ADX VALUE FROM: " + data.toString(), e);
//            deepLinkBundle.putString(ADX_ID_TAG, null);
//        }
//    }

    /**
     * Load the country and set
     *
     * @param context
     * @param countryCode
     * @author sergiopereira
     */
    private static void loadCountryCode(Context context, String countryCode) {
        Log.d(TAG, "DEEP LINK URI PATH: " + countryCode);
        // Get current country code
        String selectedCountryCode = ShopPreferences.getShopId(context);
        // Validate saved shop id
        if (selectedCountryCode == ShopPreferences.SHOP_NOT_SELECTED || !selectedCountryCode.equalsIgnoreCase(countryCode)) {
            locateCountryCode(context, countryCode);
        } else {
            Log.i(TAG, "DEEP LINK CC IS THE SAME");
        }
    }

    /**
     * Locate the shop id and save it for a respective country code
     *
     * @param countryCode
     * @author sergiopereira
     */
    private static void locateCountryCode(Context context, String countryCode) {
        // Valdiate countries available
        if (JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0) {
            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        }
        // Get the supported countries
        if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
            // Get the shop id for the country code
            for (int i = 0; i < JumiaApplication.INSTANCE.countriesAvailable.size(); i++) {
                String supportedCountry = JumiaApplication.INSTANCE.countriesAvailable.get(i).getCountryIso();
                Log.d(TAG, "SUPPORTED COUNTRY: " + supportedCountry);
                if (supportedCountry.equalsIgnoreCase(countryCode)) {
                    Log.d(TAG, "MATCH SUPPORTED COUNTRY: SHOP ID " + i + " " + countryCode);
                    ShopPreferences.setShopId(context, i);
                    break;
                }
            }
        }
    }

    /**
     * Locate the shop id and save it for a respective country code
     *
     * @param countryCode
     * @author sergiopereira
     */
    private static boolean isSupporedCountryCode(String countryCode) {
        if (JumiaApplication.INSTANCE.countriesAvailable == null || JumiaApplication.INSTANCE.countriesAvailable.size() == 0) {
            JumiaApplication.INSTANCE.countriesAvailable = CountriesConfigsTableHelper.getCountriesList();
        }
        if (JumiaApplication.INSTANCE.countriesAvailable != null && JumiaApplication.INSTANCE.countriesAvailable.size() > 0) {
            // Get the shop id for the country code 
            for (CountryObject supportedCountry : JumiaApplication.INSTANCE.countriesAvailable) {
                if (supportedCountry.getCountryIso().equalsIgnoreCase(countryCode)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get all query parameters from Uri
     *
     * @param uri
     * @return set of keys
     */
    private static Set<String> getQueryParameterNames(Uri uri) {
        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }

}
