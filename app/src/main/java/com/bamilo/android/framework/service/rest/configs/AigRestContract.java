package com.bamilo.android.framework.service.rest.configs;

import android.content.Context;
import android.content.SharedPreferences;

import com.bamilo.android.R;
import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.utils.DarwinRegex;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Interface Defines important constants to access the SQLite DB and the Rest Methods
 *
 * @author Jacob Zschunke
 */
public class AigRestContract {

    private final static String TAG = AigRestContract.class.getSimpleName();

    public final static Integer NO_CACHE = null;
    public final static int DEFAULT_CACHE_TIME = 1800;
    public final static int MAX_CACHE_TIME = 3600;

    public static String REST_BASE_PATH = null;
    public static String REQUEST_HOST = null;
    public static Boolean USE_ONLY_HTTPS = false;

    // Authentication
    public static String AUTHENTICATION_USER_AGENT;

    // AUTH CONSTANTS
    public static boolean USE_ONLY_HTTP = false;

    // COOKIE MANAGER
    public static String COOKIE_SHOP_DOMAIN;
    public static String COOKIE_SHOP_URI;

    //PREFERENCES
    public static String USER_LANGUAGE;

    private AigRestContract() {
        // ...
    }

    public static void init(Context context, String selectedId) {
        Print.i(TAG, "Initializing RestContract : " + selectedId);
        SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        setRestHost(sharedPrefs);
        setRestScheme(context, sharedPrefs);
        setRestBasePath(context, R.string.global_server_api_version);
        setCookieShopConfigs();
        setShopUserAgentAuthentication(sharedPrefs);
        setUserLanguageCode(sharedPrefs);
        Print.i(TAG, "Initializing RestContract with " + REQUEST_HOST + "/" + REST_BASE_PATH);
    }

    // NO_COUNTRIES_CONFIGS
    public static void init(Context context) {
        Print.i(TAG, "Initializing RestContract");
        SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        setRestHost(context, R.string.global_server_host);
        setRestScheme(context, sharedPrefs);
        setRestBasePath(context, R.string.global_server_restbase_path);
        setCookieShopConfigs();
        setShopUserAgentAuthentication(sharedPrefs);
        setUserLanguageCode(sharedPrefs);
        Print.i(TAG, "Initializing RestContract with " + REQUEST_HOST + "/" + REST_BASE_PATH);
    }

    // NO_COUNTRY_CONFIGS_AVAILABLE        KEY_SELECTED_COUNTRY_URL
    public static void init(Context context, String requestHost, String basePath) {
        Print.i(TAG, "Initializing RestContract");
        SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        setRestHost(requestHost);
        setRestScheme(context, sharedPrefs);
        setRestBasePath(context, R.string.global_server_api_version);
        setCookieShopConfigs();
        setShopUserAgentAuthentication(sharedPrefs);
        setUserLanguageCode(sharedPrefs);
        Print.i(TAG, "Initializing RestContract with " + REQUEST_HOST + "/" + REST_BASE_PATH);
    }

    /*
     * ######### URI #########
	 */

    private static void setRestHost(SharedPreferences sharedPrefs) {
        setRestHost(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_URL, null));
    }

    private static void setRestHost(Context context, int stringId) {
        setRestHost(context.getResources().getString(stringId));
    }

    private static void setRestHost(String requestHost) {
        Print.i(TAG, "REQUEST HOST :" + REQUEST_HOST);
        REQUEST_HOST = requestHost;
        if (TextUtils.isEmpty(REQUEST_HOST)) {
            throw new RuntimeException("The rest host has to be set and not being empty!");
        }
    }

    private static void setRestScheme(Context context, SharedPreferences sharedPrefs) {
        USE_ONLY_HTTPS = sharedPrefs.getBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, false);
        USE_ONLY_HTTP = context.getResources().getBoolean(R.bool.is_force_http);
    }

    private static void setRestBasePath(Context context, int stringId) {
        REST_BASE_PATH = context.getResources().getString(stringId);
        if (TextUtils.isEmpty(REST_BASE_PATH)) {
            throw new RuntimeException("The rest base path has to be set and not being empty!");
        }
    }

    private static void setUserLanguageCode(SharedPreferences sharedPrefs) {
        USER_LANGUAGE = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, ShopSelector.getCountryCode());
    }

    /*
     * ######### CREDENTIALS #########
	 */

    /**
     * Set the user agent authentication to access dev servers
     */
    private static void setShopUserAgentAuthentication(SharedPreferences sharedPrefs) {
        AUTHENTICATION_USER_AGENT = sharedPrefs.getString(Darwin.KEY_COUNTRY_USER_AGENT_AUTH_KEY, "");
    }

	/*
     * ######### COOKIE #########
	 */

    /**
     * Set the cookie domain based in the host.
     */
    private static void setCookieShopConfigs() {
        if (!TextUtils.isEmpty(AigRestContract.REQUEST_HOST)) {
            COOKIE_SHOP_DOMAIN = AigRestContract.REQUEST_HOST.replaceFirst(DarwinRegex.COOKIE_DOMAIN, ".");
            COOKIE_SHOP_URI = (USE_ONLY_HTTPS ? "https" : "http") + "://" + REQUEST_HOST;
        }
    }

    /**
     * Get the shop domain for cookie
     *
     * @return String
     */
    public static String getShopDomain() {
        Print.i(TAG, "COOKIE SHOP DOMAIN: " + COOKIE_SHOP_DOMAIN);
        return COOKIE_SHOP_DOMAIN;
    }

    /**
     * Get the URI base for cookie
     *
     * @return String
     */
    public static String getShopUri() {
        Print.i(TAG, "COOKIE SHOP URI: " + COOKIE_SHOP_URI);
        return COOKIE_SHOP_URI;
    }

    /**
     * TODO: FOR TESTS
     */
    public static URL buildCompleteUrl(String apiServicePath) throws MalformedURLException {
        URL url = new URL("https", REQUEST_HOST + "/" + REST_BASE_PATH, apiServicePath);
        Print.i(TAG, "CREATED URI: " + url);
        return url;
    }

}
