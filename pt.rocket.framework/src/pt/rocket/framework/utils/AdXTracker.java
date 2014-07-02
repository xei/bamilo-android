package pt.rocket.framework.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.R;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.AdX.tag.AdXConnect;

import de.akquinet.android.androlog.Log;


public class AdXTracker {
	private final static String TAG = AdXTracker.class.getSimpleName();
	private static boolean isEnabled;
	private static boolean isStarted;

	@SuppressLint("NewApi")
	public static void startup(Context context) {
		isEnabled = context.getResources().getBoolean(R.bool.adx_enable);
		if (!isEnabled)
			return;

		if (isStarted) {
			Log.d(TAG, "startup: is already started - ignoring");
			return;
		}

		PackageInfo pInfo;
		boolean isUpdate = false;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (pInfo.firstInstallTime != pInfo.lastUpdateTime)
					isUpdate = true;
			} else {
				isUpdate = true;
			}
		} catch (NameNotFoundException e) {
			isUpdate = true;
		}
		
		AdXConnect.LOGLEVEL = 0;
		AdXConnect.DEBUG = false;
		Log.d(TAG, "startup: connect instance isUpdate = " + isUpdate);
		AdXConnect.getAdXConnectInstance(context, isUpdate, 0);
		isStarted = true;
	}
	
	public static void trackSale(Context context, String cartValue, String user_id, String transaction_id, ArrayList<String> skus, String app_version, String display_size, boolean isFirstCustomer, String shop_country, boolean guest ) {
		if (!isEnabled)
			return;
		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("transaction_id", transaction_id);
		values.put("display_size", display_size);
		
		JSONArray mJSONArray = new JSONArray();
		for (String sku : skus) {
			mJSONArray.put(sku);
		}
		values.put("skus", mJSONArray);
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		cartValue = CurrencyFormatter.getCleanValue(cartValue);
		String currency = CurrencyFormatter.getCurrencyCode();
		Log.d( TAG, "trackSale: cartValue = " + cartValue + " currency = " + currency );
		if(guest){
			AdXConnect.getAdXConnectEventInstance(context, context.getString( R.string.xguestsale), cartValue, currency,jsonEncoded);
		} else {
			AdXConnect.getAdXConnectEventInstance(context, context.getString( R.string.xsale), cartValue, currency,jsonEncoded);
		}
		if (isFirstCustomer) {
			Log.d(TAG, "trackSaleData: is first customer");
			AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xcustomer), "", "",jsonEncoded);
		}
	}
	

	public static void trackAddToCart(Context context, String cartValue, String user_id, String sku, String app_version, String display_size, String shop_country ) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}
		
		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("sku", sku);
		values.put("display_size", display_size);
	
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		String currency = CurrencyFormatter.getCurrencyCode();
		Log.d(TAG, "xaddtocart tracked: event = " + context.getString(R.string.xaddtocart) + " jsonEncoded = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xaddtocart), cartValue, currency,jsonEncoded);
	}
	
	public static void trackRemoveFromCart(Context context, String cartValue, String user_id, String sku, String app_version, String display_size, String shop_country ) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("sku", sku);
		values.put("display_size", display_size);
	
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		String currency = CurrencyFormatter.getCurrencyCode();
		
		Log.d(TAG, "xremovefromcart tracked: event = " + context.getString(R.string.xremovefromcart) + " jsonEncoded = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xremovefromcart), cartValue, currency,jsonEncoded);
	}
	
	public static void trackCheckoutStep(Context context, String shop_country, String user_id, String app_version, String display_size, int step) {
		if (!isEnabled)
			return;

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		values.put("checkout_step", context.getString(step));
		
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		
		Log.d(TAG, "xnativecheckout tracked: event =  " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xnativecheckout), "","", jsonEncoded);
	}
	
	public static void trackSignUp(Context context, String shop_country, String user_id, String app_version, String display_size) {
		if (!isEnabled)
			return;

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		Log.d(TAG, "trackSignUp: " + context.getString(R.string.xcustomersignup)+ " json = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xcustomersignup), "", "", jsonEncoded);
	}

	public static void trackPaymentMethod(Context context, String shop_country, String user_id, String app_version, String display_size, String payment) {
		if (!isEnabled)
			return;

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		values.put("payment_method", payment);
		
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		Log.d(TAG, "xpaymentmethod  = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xpaymentmethod), "","", jsonEncoded);
	}
	
	public static void trackNativeCheckoutError(Context context, String shop_country, String user_id, String app_version, String display_size, String error) {
		if (!isEnabled)
			return;
		
		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		values.put("error", error);
		
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		Log.d(TAG, "xnativecheckouterror = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xnativecheckouterror), "", "",jsonEncoded);
	}
	
	public static JSONObject generateJSONObject(LinkedHashMap<String,Object> values){
		JSONObject jsonObject = new JSONObject();
		Set<String> keys = values.keySet();
		for (String key : keys) {
			try {
				jsonObject.put(key, values.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return jsonObject;
		
	}
	
	public static void launch(Context context, String app_version, String display_size, String duration) {
		if (!isEnabled)
			return;
		Log.d(TAG, "ADX: launch tracked event = " + context.getString(R.string.xlaunch));
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		values.put("duration", duration);
		String jsonEncoded = "";
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		Log.d(TAG, "ADX: launch tracked event = " + context.getString(R.string.xlaunch)+" "+jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlaunch), "", "", jsonEncoded);
	}

	public static void login(Context context, String shop_country, String user_id, String app_version, String display_size) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}
		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		Log.d(TAG, "login tracked: event = " + context.getString(R.string.xlogin) + " customerId = " + user_id+" app_version = "+app_version+" display_size: "+display_size);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlogin), "", "", jsonEncoded);
	}
	
	public static void logout(Context context, String shop_country, String user_id, String app_version, String display_size) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}
		
		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		Log.d(TAG, "logout tracked: event = " + context.getString(R.string.xlogout) + " customerId = " + user_id+" app_version = "+app_version+" display_size: "+display_size);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlogout), "", "", jsonEncoded);
	}
	
	public static void facebookLogin(Context context, String shop_country, String user_id, String app_version, String display_size) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}
		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		Log.d(TAG, "facebook login tracked: event = " + context.getString(R.string.xFBlogin) + " customerId = " + user_id+" app_version = "+app_version+" display_size: "+display_size);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xFBlogin), "", "", jsonEncoded);
	}

	public static void signup(Context context, String shop_country, String user_id, String app_version, String display_size) {
		if (!isEnabled)
			return;

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
		
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		
		Log.d(TAG, "signup tracked: event = " + context.getString(R.string.xsignup) + " customerId = " + user_id+" app_version = "+app_version+" display_size: "+display_size);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xsignup), "", "", jsonEncoded);
	}
	
	
	public static void trackCall(Context context, String user_id, String app_version, String display_size, String shop_country ) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
	
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		String currency = CurrencyFormatter.getCurrencyCode();
		
		Log.d(TAG, "xcall tracked: event = " + context.getString(R.string.xcall) + " jsonEncoded = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xcall), "", "",jsonEncoded);
	}
	
	public static void trackShare(Context context, String sku, String user_id, String app_version, String display_size, String shop_country ) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("sku", sku);
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
	
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		String currency = CurrencyFormatter.getCurrencyCode();
		
		Log.d(TAG, "xsocialshare tracked: event = " + context.getString(R.string.xsocialshare) + " jsonEncoded = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xsocialshare), "", "",jsonEncoded);
	}
	
	public static void trackProductRate(Context context, String sku, ProductReviewCommentCreated review, String user_id, String app_version, String display_size, String shop_country ) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		String jsonEncoded = "";
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("sku", sku);
		values.put("comments",review.getComments());
		for ( Entry<String, Double> option : review.getRating().entrySet()) {
			values.put(option.getKey(),option.getValue());
		}
		
		values.put("shop_country", shop_country);
		values.put("user_id", user_id);
		values.put("app_version", app_version);
		values.put("display_size", display_size);
	
		try {
			jsonEncoded = URLEncoder.encode(generateJSONObject(values).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			jsonEncoded = "";
			e.printStackTrace();
		}
		String currency = CurrencyFormatter.getCurrencyCode();
		
		Log.d(TAG, "xproductrate tracked: event = " + context.getString(R.string.xproductrate) + " jsonEncoded = " + jsonEncoded);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xproductrate), "", "",jsonEncoded);
	}
	
	/**
	 * Method that adds the pre install event
	 * 
	 * @param context
	 * @param value
	 * <!-- AD-X: Hardcode Network and Referrer -->
	 * <meta-data android:name="NETWORK" android:value="@string/adx_network_def_value" />
	 * <meta-data android:name="REFERENCE" android:value="PREINSTALL" />
	 * @author sergiopereira
	 */
	public static void preInstall(Context context, String value) {
		Log.i(TAG, "PRE INSTALL ADX TRACKING: NETWORK " + value);
		Log.i(TAG, "PRE INSTALL ADX TRACKING: REFERENCE " + context.getString(R.string.xpreinstall));		
		// Get constants
		final String REFERRAL_URL = context.getString(R.string.xreferral); 			// InstallReferral
		final String RECEIVER_DONE = context.getString(R.string.xreceiver); 		// ReceiverDone
		final String ADX_PREFERENCES = context.getString(R.string.xpreferences); 	// AdXPrefrences
		// Set variables
		String network = value;
		String reference = context.getString(R.string.xpreinstall);
		// Get AdX shared preferences 
		SharedPreferences settings = context.getSharedPreferences(ADX_PREFERENCES, 0);
		SharedPreferences.Editor editor = settings.edit();
		// Set configuration
		String uri = "referrer=utm_source%3D"+network+"%26utm_medium%3Dcpc%26utm_campaign%3D"+reference;
		editor.putString(REFERRAL_URL, uri);
		editor.putString(RECEIVER_DONE, "done");
		editor.commit();
	}
	
	/**
	 * Add the deep link launch
	 * @param context
	 * @param value
	 * @author sergiopereira
	 */
	public static void deepLinkLaunch(Context context, String value) {
		if (!isEnabled || value == null)
			return;
		Log.i(TAG, "DEEP LINK ADX TRACKING: " + context.getString(R.string.xdeeplinklaunch) + " " + value);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xdeeplinklaunch), value, "");
	}
	
}
