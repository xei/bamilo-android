package pt.rocket.framework.utils;

import java.util.Set;
import java.util.Map.Entry;

import oak.ObscuredSharedPreferences;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import de.akquinet.android.androlog.Log;

public class CustomerUtils {
	private static final String TAG = CustomerUtils.class.getName();
	private ObscuredSharedPreferences obscuredPreferences;
	public static final String INTERNAL_AUTOLOGIN_FLAG = "__autologin_requested__";
    private static final String CRED_PREFS = "cred";

	public CustomerUtils(Context ctx) {
		if(obscuredPreferences == null){
			obscuredPreferences = new MyObscuredPrefs(ctx, ctx.getSharedPreferences(
                CRED_PREFS, Context.MODE_PRIVATE));
		}
	}
	
	public void storeLogin(ContentValues values) {
		storeCredentials(values);
	}
	

	public ContentValues getCredentials() {
		ContentValues cv = new ContentValues();
		try {
			for (Entry<String, ?> entry : obscuredPreferences.getAll()
					.entrySet()) {
				if (entry.getValue() instanceof CharSequence) {
					cv.put(entry.getKey(), entry.getValue().toString());
				}
			}
		} catch (RuntimeException e) {
			Log.e(TAG, "CUST.ACCOUNT : ERROR in ObscuredPrefs.");
			e.printStackTrace();
		}
		return cv;
	}

	public String getEmail() {
		for (Entry<String, ?> entry : obscuredPreferences.getAll().entrySet()) {
			if (entry.getValue() instanceof CharSequence
					&& entry.getKey().contains("email")) {
				return entry.getValue().toString();
			}
		}
		return null;
	}

	public boolean hasCredentials() {
		return obscuredPreferences.contains(INTERNAL_AUTOLOGIN_FLAG);
	}

	public void storeCredentials(ContentValues values) {
		Editor editor = obscuredPreferences.edit();
		for (Entry<String, ?> entry : values.valueSet()) {
			if (entry.getKey() != null && entry.getValue() != null
					&& entry.getValue().toString() != null) {
				editor.putString(entry.getKey(), entry.getValue().toString());
			} else {
				Log.e(TAG, "MISSING PARAMETERS FROM API!");
			}

		}
		editor.commit();
	}

	public void clearCredentials() {
		obscuredPreferences.edit().clear().commit();
	}
	
	
	private static class MyObscuredPrefs extends ObscuredSharedPreferences {

		public MyObscuredPrefs(Context context, SharedPreferences delegate) {
			super(context, delegate);
		}

		@Override
		public Set<String> getStringSet(String arg0, Set<String> arg1) {
			return null;
		}

		@Override
		protected char[] getSpecialCode() {
			return Long.toHexString(0x9ad8aa75257645bl).toCharArray();
		}

	}

}
