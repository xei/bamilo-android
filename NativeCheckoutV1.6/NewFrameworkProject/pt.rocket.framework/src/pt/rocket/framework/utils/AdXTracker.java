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

	public static void trackSale(Context context, String cartValue ) {
		if (!isEnabled)
			return;

		String currency = CurrencyFormatter.getCurrencyCode();
		Log.d( TAG, "trackSale: cartValue = " + cartValue + " currency = " + currency );
		AdXConnect.getAdXConnectEventInstance(context, context.getString( R.string.xsale), cartValue, currency);
	}
	
	public static void trackSale(Context context, String cartValue, String userId, String transactionId,  boolean isFirstCustomer ) {
		if (!isEnabled)
			return;
		String currency = CurrencyFormatter.getCurrencyCode();
		Log.d( TAG, "trackSale: cartValue = " + cartValue + " currency = " + currency );
		AdXConnect.getAdXConnectEventInstance(context, context.getString( R.string.xsale), cartValue, currency,transactionId+"-"+userId);
		if (isFirstCustomer) {
			Log.d(TAG, "trackSaleData: is first customer");
			AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xcustomer), userId, "");
		}
	}
	

//	public static void trackSaleData(Context context, String userId, String transactionId, boolean isFirstCustomer) {
//		if (!isEnabled)
//			return;
//
//		Log.d(TAG, "trackSaleData: userId = " + userId + " transactionId = " + transactionId);
//		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xsaledata), userId, transactionId);
//		if (isFirstCustomer) {
//			Log.d(TAG, "trackSaleData: is first customer");
//			AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xcustomer), userId, "");
//		}
//	}

	public static void launch(Context context) {
		if (!isEnabled)
			return;
		Log.d(TAG, "ADX: launch tracked event = " + context.getString(R.string.xlaunch));
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlaunch), "", "");
	}

	public static void login(Context context, String customerId) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		Log.d(TAG, "login tracked: event = " + context.getString(R.string.xlogin) + " customerId = " + customerId);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xlogin), customerId, "");
	}
	
	public static void facebookLogin(Context context, String customerId) {
		if (!isEnabled) {
			Log.d(TAG, "adx seems to be disabled - ignoring");
			return;
		}

		Log.d(TAG, "facebook login tracked: event = " + context.getString(R.string.xFBlogin) + " customerId = " + customerId);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xFBlogin), customerId, "");
	}

	public static void signup(Context context, String customerId) {
		if (!isEnabled)
			return;

		Log.d(TAG, "signup tracked: event = " + context.getString(R.string.xsignup) + " customerId = " + customerId);
		AdXConnect.getAdXConnectEventInstance(context, context.getString(R.string.xsignup), customerId, "");
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
