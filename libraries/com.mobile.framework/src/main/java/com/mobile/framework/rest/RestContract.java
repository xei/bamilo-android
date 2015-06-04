package com.mobile.framework.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mobile.framework.Darwin;
import com.mobile.framework.R;
import com.mobile.framework.output.Print;

/**
 * The Interface Defines important constants to access the SQLite DB and the Rest Methods
 *
 * @author Jacob Zschunke
 */
public class RestContract {

	private final static String TAG = RestContract.class.getSimpleName();

	private RestContract() {

	}

	public final static Integer NO_CACHE = null;
	//public final static int MIN_CACHE_TIME = 0;
	public final static int DEFAULT_CACHE_TIME = 1800;
	public final static int MAX_CACHE_TIME = 3600;

	// Table Rows
	public static final String _ID = "id";
	public static final String _URI = "uri";
	public static final String _STATE = "state";
	public static final String _RESULT_CODE = "resultcode";
	public static final String _PAYLOAD = "payload";
	public static final String _TIMESTAMP = "timestamp";

	public static String REST_BASE_PATH = null;
	public static String REQUEST_HOST = null;
	public static Boolean USE_ONLY_HTTPS = false;

	// Authentication
	public static Boolean USE_AUTHENTICATION = null;
	public static String AUTHENTICATION_USER = null;
	public static String AUTHENTICATION_PASS = null;

	// AUTH CONSTANTS										//false
	public static boolean RUNNING_TESTS = false;
	public static boolean USE_ONLY_HTTP = false;
	public static final boolean USE_AUTHENTICATION_TEST = true;
	public static final String AUTHENTICATION_USER_TEST = "rocket";
	public static final String AUTHENTICATION_PASS_TEST = "rock4me";

	public static final String REST_PARAM_RATING =                      "rating";
	public static final String REST_PARAM_PAGE =                        "page";
	public static final String REST_PARAM_SELLER_RATING =               "seller_rating";

	// private static Context context;

	public static void init(Context context, String selectedId) {
		Print.i(TAG, "code1configs initializing RestContract : " + selectedId);
		SharedPreferences sharedPrefs = context.getSharedPreferences(Darwin.SHARED_PREFERENCES, Context.MODE_PRIVATE);

		REQUEST_HOST = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_URL, null);
		Print.i(TAG, "code1configs REQUEST_HOST : " + REQUEST_HOST);
		if (TextUtils.isEmpty(REQUEST_HOST)) {
			throw new RuntimeException("The rest host has to be set and not beeing empty!");
		}

		USE_ONLY_HTTPS = sharedPrefs.getBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, false);
		// 
		USE_ONLY_HTTP  = context.getResources().getBoolean(R.bool.is_force_http);

		REST_BASE_PATH = context.getResources().getString(R.string.global_server_api_version);
		if (TextUtils.isEmpty(REST_BASE_PATH)) {
			throw new RuntimeException("The rest base path has to be set and not beeing empty!");
		}
		Print.d(TAG, "Initilizing RestContract with " + REQUEST_HOST + "/" + REST_BASE_PATH);
		AUTHENTICATION_USER = context.getResources().getString(R.string.global_server_user);
		AUTHENTICATION_PASS = context.getResources().getString(R.string.global_server_password);
		USE_AUTHENTICATION = context.getResources().getBoolean(R.bool.rest_host_auth_use_it);
	}

	// NO_COUNTRIES_CONFIGS
	public static void init(Context context) {
		Print.i(TAG, "initializing RestContract");

		REQUEST_HOST = context.getResources().getString(R.string.global_server_host);
		if (TextUtils.isEmpty(REQUEST_HOST)) {
			throw new RuntimeException("The rest host has to be set and not beeing empty!");
		}

		REST_BASE_PATH = context.getResources().getString(R.string.global_server_restbase_path);
		if (TextUtils.isEmpty(REST_BASE_PATH)) {
			throw new RuntimeException("The rest base path has to be set and not beeing empty!");
		}
		Print.d(TAG, "Initilizing RestContract with " + REQUEST_HOST + "/" + REST_BASE_PATH);
		AUTHENTICATION_USER = context.getResources().getString(R.string.global_server_user);
		AUTHENTICATION_PASS = context.getResources().getString(R.string.global_server_password);
		USE_AUTHENTICATION = context.getResources().getBoolean(R.bool.rest_host_auth_use_it);
	}

	// NO_COUNTRY_CONFIGS_AVAILABLE        KEY_SELECTED_COUNTRY_URL
	public static void init(Context context, String requestHost, String basePath) {
		Print.i(TAG, "initializing RestContract");

		REQUEST_HOST = requestHost;
		if (TextUtils.isEmpty(REQUEST_HOST)) {
			throw new RuntimeException("The rest host has to be set and not beeing empty!");
		}

		REST_BASE_PATH = context.getResources().getString(R.string.global_server_api_version);
		if (TextUtils.isEmpty(REST_BASE_PATH)) {
			throw new RuntimeException("The rest base path has to be set and not beeing empty!");
		}
		Print.d(TAG, "Initilizing RestContract with " + REQUEST_HOST + "/" + REST_BASE_PATH);
		AUTHENTICATION_USER = context.getResources().getString(R.string.global_server_user);
		AUTHENTICATION_PASS = context.getResources().getString(R.string.global_server_password);
		USE_AUTHENTICATION = context.getResources().getBoolean(R.bool.rest_host_auth_use_it);
	}
}
