package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by Arash Hassanpour on 4/12/2017.
 */

public class AndroidStorage implements com.emarsys.predict.Storage {

    private SharedPreferences sharedPref;

    public AndroidStorage(Context context) {
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void put(String key, Object value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        editor.apply();
    }

    @Override
    public Object get(String key) {
        return sharedPref.getString(key, null);
    }
}
