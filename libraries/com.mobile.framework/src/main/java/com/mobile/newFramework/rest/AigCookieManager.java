package com.mobile.newFramework.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mobile.framework.rest.RestContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import de.akquinet.android.androlog.Log;
import oak.Base64;

/**
 * Created by spereira on 6/3/15.
 */
public class AigCookieManager extends CookieManager {

    private static final String TAG = AigCookieManager.class.getSimpleName();

    private static final String PERSISTENT_COOKIES_FILE = "persistent_cookies";

    private static final String COOKIE_PREFS_TAG = "cookie_jumia";

    private static final String PHPSESSID_TAG = "PHPSESSID";

    private SharedPreferences mCookiePrefs;

    public AigCookieManager(Context context) {
        Log.i(TAG, "CONSTRUCTOR");
        loadSessionCookie(context);
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        super.put(uri, responseHeaders);
        Log.i(TAG, "PUT URI AND RESPONSE HEADERS");
        saveSessionCookie();
    }

    public List<HttpCookie> getCookies() {
        return getCookieStore().getCookies();
    }

    public void removeAll() {
        // Remove all cookies
        getCookieStore().removeAll();
        // Clear cookies from persistent store
        SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
    }

    /**
     * Save the session cookie into shared preferences.
     * @author spereira
     */
    public synchronized void saveSessionCookie() {
        Log.i(TAG, "SAVE SESSION COOKIE IN PREFS");
        // Save cookie into persistent store
        List<HttpCookie> cookies = getCookieStore().getCookies();
        for (HttpCookie cookie : cookies) {
            if(cookie.getName().contains(PHPSESSID_TAG) && cookie.getDomain().equals(".jumia.com.ng")) {
                Log.i(TAG, "ON PERSIST COOKIE SESSION : " + RestContract.REQUEST_HOST);
                Log.i(TAG, "DOMAIN: " + cookie.getDomain() + " NAME: "  + cookie.getName());
                SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
                String str = encodeCookie(new PersistentCookieNew(cookie));
                prefsWriter.putString(COOKIE_PREFS_TAG, str);
                prefsWriter.apply();
                return;
            }
        }
        Log.w(TAG, "WARNING: NO PERSIST COOKIE SESSION");
    }

    /**
     * Load the session cookie from shared preferences.
     * @param context
     * @author spereira
     */
    private void loadSessionCookie(Context context) {
        Log.i(TAG, "LOAD SESSION COOKIE FROM PREFS");
        // Get preferences
        mCookiePrefs = context.getSharedPreferences(PERSISTENT_COOKIES_FILE, Context.MODE_PRIVATE);
        // Get stored encoded cookie
        String encodedCookie = mCookiePrefs.getString(COOKIE_PREFS_TAG, null);
        // Create cookie
        if(!TextUtils.isEmpty(encodedCookie)) {
            // Get
            HttpCookie cookie = decodeCookie(encodedCookie);
            // Save
            if(cookie != null) {
                Log.i(TAG, "DOMAIN: " + cookie.getDomain() + " NAME: "  + cookie.getName());
                try {
                    getCookieStore().add(new URI(cookie.getDomain()), cookie);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Encode a serialize cookie using Base64 into a String.
     * @param cookie
     * @return String or null
     */
    private String encodeCookie(PersistentCookieNew cookie) {
        if (cookie == null) return null;
        Log.i(TAG, "ON ENCODE COOKIE: " + cookie.toString());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(TAG, "IOException in encodeCookie", e);
            return null;
        }
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Create a cookie from cookie string Base64.
     * @param cookieString
     * @return Cookie or null
     * @author spereira
     */
    private HttpCookie decodeCookie(String cookieString) {
        Log.i(TAG, "ON DECODE COOKIE: " + cookieString);
        byte[] bytes = Base64.decode(cookieString, Base64.DEFAULT);
        Log.i(TAG, "ON DECODE COOKIE: " + new String(bytes));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        HttpCookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((PersistentCookieNew) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            Log.d(TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "ClassNotFoundException in decodeCookie", e);
        }
        return cookie;
    }



}
