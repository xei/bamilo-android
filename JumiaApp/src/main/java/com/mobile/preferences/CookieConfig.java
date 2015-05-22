package com.mobile.preferences;


import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mobile.framework.rest.ICurrentCookie;
import com.mobile.framework.utils.CustomerUtils;

import java.util.Map;

import oak.ObscuredSharedPreferences;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author Ricardo Soares
 * @version 1.0
 * @date 2015/04/22
 */
public class CookieConfig extends CustomerUtils{

    public static final String CURRENT_COOKIE = "current_cookie";

    private String country;

    private ContentValues values;

    private ICurrentCookie cookieStore;

    /**
     * Constructor
     *
     * @param ctx
     * @param country
     * @param cookieStore
     */
    public CookieConfig(Context ctx, String country, ICurrentCookie cookieStore) {
        super(ctx);
        this.country = country;
        this.cookieStore = cookieStore;
        this.values = getValues();

        Object cookieObj = values.get(CURRENT_COOKIE);
        if(cookieObj instanceof String) {
            storeCookie((String) values.get(CURRENT_COOKIE));
        }
    }

    @Override
    public ContentValues getCredentials() {
        return getValues();
    }

    @Override
    public String getEmail() {
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
        if(values.size() > 0) {
            this.values.putAll(values);

            Gson gson = new Gson();
            String json = gson.toJson(this.values);

            ObscuredSharedPreferences.Editor editor = obscuredPreferences.edit();

            editor.putString(country, json);
            editor.putBoolean(USER_LOGGED_ONCE_FLAG, true);

            editor.commit();
        }
    }

    /**
     * Store the cookie on cookieStore.
     *
     * @param cookie Encoded cookie.
     */
    protected void storeCookie(String cookie){
        if(!TextUtils.isEmpty(cookie) && cookieStore != null){
            cookieStore.addCookie(cookie);
        }
    }

    @Override
    public boolean hasCredentials() {
        return getValues().containsKey(INTERNAL_AUTOLOGIN_FLAG);
    }

    @Override
    public void clearCredentials() {
        ObscuredSharedPreferences.Editor editor = obscuredPreferences.edit();
        editor.remove(country).commit();
        values.clear();
    }

    @Override
    public boolean userNeverLoggedIn() {
        return !obscuredPreferences.contains(USER_LOGGED_ONCE_FLAG);
    }

    private ContentValues getValues() {
        if(values == null) {
            Gson gson = new Gson();
            String json = obscuredPreferences.getString(country, null);
            this.values = (json != null) ? gson.fromJson(json, ContentValues.class) : new ContentValues();
        }
        return values;
    }

    /**
     * Save on preferences the credentials and cookie.
     */
    public void save(){
        if(cookieStore != null) {
            String cookie = cookieStore.getCurrentCookie();
            this.values.put(CURRENT_COOKIE, cookie);
        }
        storeCredentials(this.values);
    }
}
