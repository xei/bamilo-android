package com.mobile.preferences;


import android.content.ContentValues;
import android.content.Context;

import com.mobile.framework.utils.CustomerUtils;
import com.newrelic.com.google.gson.Gson;

import java.util.Map;

import oak.ObscuredSharedPreferences;

/**
 * Created by rsoares on 4/21/15.
 */
public class CookieConfig extends CustomerUtils{
    private String country;

    private ContentValues values;

    /**
     * Constructor
     *
     * @param ctx
     */
    public CookieConfig(Context ctx, String country) {
        super(ctx);
        this.country = country;
        this.values = null;
    }

    @Override
    public ContentValues getCredentials() {
        return getValues();
//        return super.getCredentials();
    }

    @Override
    public String getEmail() {
//        return super.getEmail();
        ContentValues values = getValues();
        for(Map.Entry<String, Object> entry : values.valueSet()){
            if(entry.getValue() instanceof CharSequence && entry.getKey().contains("email")){
                return entry.getValue().toString();
            }
        }
        return null;
    }

    @Override
    public void storeCredentials(ContentValues values) {
//        super.storeCredentials(values);
        this.values = values;

        Gson gson = new Gson();
        String json = gson.toJson(values);

        ObscuredSharedPreferences.Editor editor = obscuredPreferences.edit();

        editor.putString(country, json);
        editor.putBoolean(USER_LOGGED_ONCE_FLAG, true);

        editor.commit();

    }

    @Override
    public boolean hasCredentials() {
//        return super.hasCredentials();
        return getValues().containsKey(INTERNAL_AUTOLOGIN_FLAG);
    }

    @Override
    public void clearCredentials() {
//        super.clearCredentials();
        ObscuredSharedPreferences.Editor editor = obscuredPreferences.edit();
        editor.remove(country).commit();
    }

    @Override
    public boolean userNeverLoggedIn() {
//        return super.userNeverLoggedIn();
        return !obscuredPreferences.contains(USER_LOGGED_ONCE_FLAG);
    }

    private ContentValues getValues() {
        if(values == null) {
//        ObscuredSharedPreferences.Editor editor = obscuredPreferences.edit();
            Gson gson = new Gson();
            String json = obscuredPreferences.getString(country, null);
            this.values = (json != null) ? gson.fromJson(json, ContentValues.class) : new ContentValues();
        }
        return values;
    }



}
