package pt.rocket.framework.utils;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import oak.ObscuredSharedPreferences;

public class JumiaPrefs extends ObscuredSharedPreferences {

	public JumiaPrefs(Context context, SharedPreferences delegate) {
		super(context, delegate);
	}

	@Override
	public Set<String> getStringSet(String arg0, Set<String> arg1) {
		return null;
	}

	@Override
	protected char[] getSpecialCode() {
		return "secretpassword".toCharArray();
	}

}
