package pt.rocket.framework.utils;

import pt.rocket.framework.R;
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

	public static void trackSale(Context context, String cartValue, String shop ) {
		if (!isEnabled)
			return;

		String currency = CurrencyFormatter.getCurrencyCode();
		Log.d( TAG, "trackSale: cartValue = " + cartValue + " currency = " + currency );
		cartValue = CurrencyFormatter.getCleanValue(cartValue);
		AdXConnect.getAdXConnectEventInstance(context, context.getString( R.string.xsale), cartValue, shop+"_"+currency);
	}
	
	public static void trackSale(Context context, String cartValue, String userId, String transactionId,  boolean isFirstCustomer, String shop ) {
		if (!isEnabled)
			return;
		
		cartValue = CurrencyFormatter.getCleanValue(cartValue);
		String currency = CurrencyFormatter.getCurrencyCode();
		Log.d( TAG, "trackSale: cartValue = " + cartValue + " currency = " + currency );
		AdXConnect.getAdXConnectEventInstance(context, context.getString( R.string.xsale), cartValue, currency,shop+"_"+userId+"_"+transactionId);
		if (isFirstCustomer) {
			Log.d(TAG, "trackSaleData: is first customer");
			AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xcustomer), shop+"_"+userId, "");
		}
	}
	
//
//	public static void trackAddToCart(Context context, CompleteProduct product, ProductSimple simple, Double price, String location, String country) {
//		if (!isEnabled) {
//			Log.d(TAG, "adx seems to be disabled - ignoring");
//			return;
//		}
//
//		Log.d(TAG, "xaddtocart tracked: event = " + context.getString(R.string.xaddtocart) + " customerId = " + customerId+" country = "+country);
//		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xaddtocart), product.getPrice(), );
//	}
	
	public static void trackCheckoutStep(Context context, String email, int step) {
		if (!isEnabled)
			return;

		Log.d(TAG, "trackCheckoutStep: email = " + email + " step = " + context.getString(step));
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xnativecheckout), email+"_"+context.getString(step), "");
	}
	
	public static void trackSignUp(Context context, String email, String shop) {
		if (!isEnabled)
			return;

		Log.d(TAG, "trackSignUp: email = " + email + " step = " + context.getString(R.string.xcustomersignup));
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xcustomersignup), "", "", shop+"_0_"+email);
	}

	public static void trackPaymentMethod(Context context, String email, String payment) {
		if (!isEnabled)
			return;

		Log.d(TAG, "trackPaymentMethod: email = " + email + " payment = " + payment);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xpaymentmethod), email+"_"+payment, "");
	}
	
	public static void trackNativeCheckoutError(Context context, String email, String error) {
		if (!isEnabled)
			return;

		Log.d(TAG, "trackNativeCheckoutError: email = " + email + " error = " + error);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xnativecheckouterror), email+"_"+error, "");
	}
	
	public static void launch(Context context) {
		if (!isEnabled)
			return;
		Log.d(TAG, "ADX: launch tracked event = " + context.getString(R.string.xlaunch));
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlaunch), "", "");
	}

	public static void login(Context context, String customerId, String country) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		Log.d(TAG, "login tracked: event = " + context.getString(R.string.xlogin) + " customerId = " + customerId+" country = "+country);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlogin), "", "", country+"_"+customerId);
	}
	
	public static void logout(Context context, String customerId, String country) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		Log.d(TAG, "logout tracked: event = " + context.getString(R.string.xlogout) + " customerId = " + customerId+" country = "+country);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlogout), "", "", country+"_"+customerId);
	}
	
	public static void facebookLogin(Context context, String customerId, String country) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		Log.d(TAG, "facebook login tracked: event = " + context.getString(R.string.xFBlogin) + " customerId = " + customerId+" country = "+country);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xFBlogin), "", "", country+"_"+customerId);
	}

	public static void signup(Context context, String customerId, String country) {
		if (!isEnabled)
			return;

		Log.d(TAG, "signup tracked: event = " + context.getString(R.string.xsignup) + " customerId = " + customerId+" country = "+country);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xsignup), "", "", country+"_"+customerId);
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
	
}
