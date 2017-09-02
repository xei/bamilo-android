package com.mobile.preferences;


import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mobile.service.rest.cookies.ISessionCookie;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.security.ObscuredSharedPreferences;

import java.net.URISyntaxException;
import java.util.Map;

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
public class PersistentSessionStore extends CustomerUtils {

    private static final String TAG = PersistentSessionStore.class.getSimpleName();

    public static final String SESSION_COOKIE_TAG = "session_cookie";

    public static final String SESSION_EMAIL_TAG = "email";

    private String country;

    private ContentValues values;

    private ISessionCookie cookieStore;

    /**
     * Constructor
     */
    public PersistentSessionStore(Context ctx, String country, ISessionCookie cookieStore) {
        super(ctx);
        this.country = country;
        this.cookieStore = cookieStore;
        this.values = getValues();

        Object cookieObj = values.get(SESSION_COOKIE_TAG);
        if(cookieObj instanceof String) {
            storeCookie((String) cookieObj);
        }
    }

    @Override
    public ContentValues getCredentials() {
        return getValues();
    }

    @Override
    public boolean hasCredentials() {
        return getValues().containsKey(INTERNAL_AUTO_LOGIN_FLAG);
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

    @Override
    public String getEmail() {
        ContentValues values = getValues();
        for(Map.Entry<String, Object> entry : values.valueSet()){
            if(entry.getValue() instanceof CharSequence && entry.getKey().contains(SESSION_EMAIL_TAG)){
                return entry.getValue().toString();
            }
        }
        return null;
    }

    /*
     * ############# PERSISTENT SESSION #############
     */

    /**
     * Store session
     */
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
            try {
                // Add the saved cookie
                cookieStore.addEncodedSessionCookie(cookie);
                // Remove the saved cookie
                values.remove(SESSION_COOKIE_TAG);
            } catch (NullPointerException | URISyntaxException e) {
                Print.w(TAG, "WARNING: EXCEPTION ON ADD ENCODED COOKIE", e);
                clearCredentials();
            }
        }
    }

    /**
     * Get session from obscured preferences
     * @return ContentValues
     */
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
            String cookie = cookieStore.getEncodedSessionCookie();
            this.values.put(SESSION_COOKIE_TAG, cookie);
        }
        storeCredentials(this.values);
    }
}
