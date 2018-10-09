package com.bamilo.android.framework.service.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bamilo.android.framework.service.Darwin;
import com.bamilo.android.framework.service.utils.security.ObscuredSharedPreferences;

import java.util.Map.Entry;
import java.util.Set;

public class CustomerUtils {
	
	private static final String TAG = CustomerUtils.class.getName();
	
	protected ObscuredSharedPreferences obscuredPreferences;
	
	public static final String INTERNAL_AUTO_LOGIN_FLAG = "__autologin_requested__";
	
	public static final String INTERNAL_PASSWORD_VALUE = "Alice_Module_Customer_Model_LoginForm[password]";
	
	public static final String INTERNAL_EMAIL_VALUE = " Alice_Module_Customer_Model_LoginForm[email] ";
	
	public static final String INTERNAL_SIGN_UP_FLAG = "signup";
	
    private static final String CRED_PREFS = "persistent_credentials";
    // Flag used for Accengage tracking
    public static final String USER_LOGGED_ONCE_FLAG = "user_logged_once";

    /**
     * Constructor
     */
	public CustomerUtils(Context ctx) {
		if(obscuredPreferences == null){
			obscuredPreferences = new MyObscuredPrefs(ctx, ctx.getSharedPreferences(CRED_PREFS, Context.MODE_PRIVATE));
		}
	}
	
	/**
	 * Get credentials
	 */
	public ContentValues getCredentials() {
		ContentValues cv = new ContentValues();
		try {
			for (Entry<String, ?> entry : obscuredPreferences.getAll().entrySet()) {
				if (entry.getValue() instanceof CharSequence) {
					cv.put(entry.getKey(), entry.getValue().toString());
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return cv;
	}

	/**
	 * Get email
	 * @return
	 */
	public String getEmail() {
		for (Entry<String, ?> entry : obscuredPreferences.getAll().entrySet()) {
			if (entry.getValue() instanceof CharSequence && entry.getKey().contains("email")) {
				return entry.getValue().toString();
			}
		}
		return null;
	}

	/**
	 * Has credentials.
	 * @return
	 */
	public boolean hasCredentials() {
		return obscuredPreferences.contains(INTERNAL_AUTO_LOGIN_FLAG);
	}

	/**
	 * Store credentials
	 * @param values
	 */
	public void storeCredentials(ContentValues values) {
		Editor editor = obscuredPreferences.edit();
		for (Entry<String, ?> entry : values.valueSet()) {
			if (entry.getKey() != null && entry.getValue() != null && entry.getValue().toString() != null) {
				editor.putString(entry.getKey(), entry.getValue().toString());
			} else {
			}
		}
		// Put logged flag
		editor.putBoolean(USER_LOGGED_ONCE_FLAG, true);
		// Commit
		editor.commit();
	}

	/**
	 * Clear customer credentials.
	 * @modified sergiopereira
	 */
	public void clearCredentials() {
		try {
			Editor editor = obscuredPreferences.edit();
			for (Entry<String, ?> entry : obscuredPreferences.getAll().entrySet()) {
				// Case flag logged once continue
				if(entry.getKey().equals(USER_LOGGED_ONCE_FLAG)) continue;
				// Case other remove
				else editor.remove(entry.getKey());
			}
			editor.commit();
		} catch (RuntimeException e) {
			obscuredPreferences.edit().clear().commit();
		}
	}
	
	/**
	 * Validate if user never logged in.<br>
	 * Used for Accengage tracking.
	 * @return true or false
	 * @author sergiopereira
	 */
	public boolean userNeverLoggedIn() {
		boolean loggedOnce = getCredentials().containsKey(USER_LOGGED_ONCE_FLAG);
		return !loggedOnce;
	}
	
	/**
	 * MyObscuredPrefs
	 */
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
			return Long.toHexString(0x9ad8aa75257645bL).toCharArray();
		}

	}

	/**
	 * returns the flag that shows if is possible to change password or not
	 * @return true or false
	 */
	public static boolean isChangePasswordHidden(Activity activity) {
		SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		return sharedPrefs.getBoolean(Darwin.KEY_CHANGE_PASSWORD, false);
	}

	/**
	 * set flag that controls if is possible to change password or not
	 */
	public static void setChangePasswordVisibility(Activity activity, boolean isHideChangePassword) {
		SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean(Darwin.KEY_CHANGE_PASSWORD, isHideChangePassword);
		editor.apply();
	}

}
