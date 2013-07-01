package pt.rocket.framework.utils;

import java.util.HashMap;
import java.util.Map;

import pt.rocket.framework.R;
import pt.rocket.framework.objects.Customer;
import android.content.Context;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;

import de.akquinet.android.androlog.Log;

public class FlurryTracker {
	private final static String TAG = LogTagHelper.create(FlurryTracker.class);

	private static FlurryTracker sInstance;

	private static Context mContext;
	private String mCurrentKey;

	private boolean isEnabled;
	private int mShopId;
	private boolean isStarted;

	private String mTrackerString;

	public static void startup(Context context, int shopId) {
		sInstance = new FlurryTracker(context, shopId);
	}

	public static FlurryTracker get() {
		if (sInstance == null) {
			sInstance = new FlurryTracker();
		}
		return sInstance;
	}

	private FlurryTracker(Context context, int shopId) {
		mContext = context;
		isEnabled = mContext.getResources().getBoolean(R.bool.flurry_enable);
		if (!isEnabled)
			return;

		mShopId = shopId;
		boolean testMode = mContext.getResources().getBoolean(R.bool.f_testmode);

		if (testMode)
			mCurrentKey = mContext.getResources().getStringArray(R.array.fkey_testkeys)[mShopId];
		else
			mCurrentKey = mContext.getResources().getStringArray(R.array.fkey_livekeys)[mShopId];
		
		updateSession();
	}

	private FlurryTracker() {
		isEnabled = false;
	}
	
	private void updateSession() {
		if (TextUtils.isEmpty(mCurrentKey)) {
			Log.w("No flurry key for shop_id " + mShopId + " and current keyset found. Cant enable flurry. Sorry.");
			return;
		}
		switchTracker(mCurrentKey);
	}

	private void switchTracker(String trackerString) {
		if (!isEnabled)
			return;

		if (isStarted) {
			FlurryAgent.onEndSession(mContext);
		}
		mTrackerString = trackerString;

		Log.d(TAG, "switchTracker: switching");
		FlurryAgent.onStartSession(mContext, trackerString);
		Log.d( TAG, "switchTracker: key = " + trackerString + " shopId = " + mShopId );
		FlurryAgent.setLogEnabled(false);
		isStarted = true;
	}

	public void begin(Context context) {
		if (!isEnabled)
			return;

		Log.d( TAG, "beginSession: key = " + mTrackerString);
		FlurryAgent.onStartSession(context, mTrackerString);
		isStarted = true;
	}

	public void end(Context context) {
		if (!isEnabled)
			return;

		Log.d( TAG, "endSession: key = " + mTrackerString);
		FlurryAgent.onEndSession(context);
		isStarted = false;
	}

	public void signUp(Customer customer) {
		if (!isEnabled)
			return;

		Map<String, String> map = new HashMap<String, String>();
		map.put(mContext.getString(R.string.fuserid), customer.getEmail());
		map.put(mContext.getString(R.string.fcustomerid), customer.getIdAsString());

		Log.d(TAG, "signUp for " + customer.getIdAsString());
		FlurryAgent.logEvent(mContext.getString(R.string.fsignup), map);
	}

	public void signIn(Customer customer, boolean justSignup, boolean wasAutologin) {
		if (!isEnabled)
			return;

		Map<String, String> map = new HashMap<String, String>();
		map.put(mContext.getString(R.string.fuserid), customer.getEmail());
		map.put(mContext.getString(R.string.fcustomerid), customer.getIdAsString());

		if (justSignup) {
			Log.d(TAG, "signUp for " + customer.getIdAsString() + " - has just signed up");
			FlurryAgent.logEvent(mContext.getString(R.string.fsigninjust), map);
		} else if ( wasAutologin ) {
			Log.d(TAG, "signUp for " + customer.getIdAsString() + " - was auto signin");
			FlurryAgent.logEvent(mContext.getString(R.string.fsigninauto), map);
		} else {
			Log.d(TAG, "signUp for " + customer.getIdAsString() + "was normal signin");
			FlurryAgent.logEvent(mContext.getString(R.string.fsignup), map);
		}		
		
	}
	
	public void trackError( String errorId, String message, String errorClass ) {
		FlurryAgent.onError( errorId, message, errorClass);
	}

	public void purchase(String orderId, String customerId, String value) {
		if (!isEnabled)
			return;

		Map<String, String> m = new HashMap<String, String>();
		m.put(mContext.getString(R.string.fcustomerid), customerId);
		m.put(mContext.getString(R.string.forderid), orderId);
		m.put(mContext.getString(R.string.fvalue), value);
		m.put(mContext.getString(R.string.fcurrency), CurrencyFormatter.getCurrencyCode());
		Log.d(TAG, "purchase for customerId = " + customerId + " value = " + value);
		FlurryAgent.logEvent(mContext.getString(R.string.fpurchase), m);
	}

}
