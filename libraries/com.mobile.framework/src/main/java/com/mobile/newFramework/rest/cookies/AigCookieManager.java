package com.mobile.newFramework.rest.cookies;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mobile.newFramework.rest.configs.AigRestContract;

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
 * Class used to manage cookies.
 */
public class AigCookieManager extends CookieManager implements ISessionCookie {

    private static final String TAG = AigCookieManager.class.getSimpleName();

    private static final String PERSISTENT_COOKIES_FILE = "persistent_cookies";

    private static final String COOKIE_PREFS_TAG = "cookie_jumia";

    private static final String PHP_SESSION_ID_TAG = "PHPSESSID";

    private SharedPreferences mCookiePrefs;

    private HttpCookie mCurrentCookie;

    /**
     * Constructor
     */
    public AigCookieManager(Context context) {
        // Load session cookie from prefs
        mCurrentCookie = loadSessionCookie(context);
    }

    /**
     * Override to save session cookie.
     */
    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        super.put(uri, responseHeaders);
        // Validate cookies and save the session cookie
        saveSessionCookie();
    }

    /**
     * Save the session cookie into shared preferences.
     * @author spereira
     */
    public synchronized void saveSessionCookie() {
        // Get shop domain
        String shop = AigRestContract.getShopDomain();
        // Get cookies not expired
        List<HttpCookie> cookies = getCookieStore().getCookies();
        for (HttpCookie cookie : cookies) {
            if(cookie.getDomain().contains(shop) && cookie.getName().contains(PHP_SESSION_ID_TAG)) {
                store(cookie);
                return;
            }
        }
    }

    /**
     * Persist the session cookie.
     */
    private void store(HttpCookie cookie) {
        // Validate the cookie and the current
        if(mCurrentCookie == null || !mCurrentCookie.equals(cookie)) {
            Log.i(TAG, "STORED COOKIE: " + cookie.getDomain() + " "  + cookie.getName());
            mCurrentCookie = cookie;
            SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
            String str = encodeCookie(new AigPersistentHttpCookie(cookie));
            prefsWriter.putString(COOKIE_PREFS_TAG, str);
            prefsWriter.apply();
        }
    }

    /**
     * Load the session cookie from shared preferences.
     */
    private HttpCookie loadSessionCookie(Context context) {
        // Get shop domain
        String shop = AigRestContract.getShopDomain();
        // Get preferences
        mCookiePrefs = context.getSharedPreferences(PERSISTENT_COOKIES_FILE, Context.MODE_PRIVATE);
        // Get stored encoded cookie
        String encodedCookie = mCookiePrefs.getString(COOKIE_PREFS_TAG, null);
        // Decode
        HttpCookie cookie = decodeCookie(encodedCookie);
        // Save
        if (cookie != null && cookie.getDomain().contains(shop) && cookie.getName().contains(PHP_SESSION_ID_TAG)) {
            Log.i(TAG, "LOADED COOKIE FROM PERFS: " + cookie.getDomain() + " " + cookie.getName());
            getCookieStore().add(URI.create(cookie.getDomain()), cookie);
        } else if(cookie != null && !cookie.getDomain().contains(shop) ){
            Log.i(TAG, "LOADED COOKIE FROM PERFS: NO MATCH " + shop + " != " + cookie.getDomain());
        } else {
            Log.i(TAG, "LOADED COOKIE FROM PERFS: IS EMPTY");
        }
        return cookie;
    }

    /**
     * Encode a serialize cookie using Base64 into a String.
     * @return String or null
     */
    private String encodeCookie(AigPersistentHttpCookie cookie) {
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
     * @return Cookie or null
     * @author spereira
     */
    private HttpCookie decodeCookie(String encodedCookieString) {
        HttpCookie cookie = null;
        if(!TextUtils.isEmpty(encodedCookieString)) {
            byte[] bytes = Base64.decode(encodedCookieString, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                cookie = ((AigPersistentHttpCookie) objectInputStream.readObject()).getCookie();
            } catch (IOException | ClassNotFoundException e) {
                Log.d(TAG, "Exception in decodeCookie", e);
            }
        }
        return cookie;
    }

    /**
     * Get all cookies
     */
    public List<HttpCookie> getCookies() {
        return getCookieStore().getCookies();
    }

    /**
     * Remove all cookies
     */
    public void removeAll() {
        // Remove all cookies
        getCookieStore().removeAll();
        // Clear cookies from persistent store
        SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        // Remove current cookie
        mCurrentCookie = null;
    }

    /*
     * ####### ICookieManager #######
     */

    /**
     * Add an encoded session cookie
     */
    @Override
    public void addEncodedSessionCookie(String encodedCookie) throws NullPointerException, URISyntaxException {
        HttpCookie cookie = decodeCookie(encodedCookie);
        getCookieStore().add(new URI(cookie.getDomain()), cookie);
        Log.i(TAG, "STORE SESSION FROM PERSISTENT COOKIE: " + cookie.getDomain() + " " + cookie.getName());
    }

    /**
     * Get the current session cookie
     * @return Encoded session cookie or null
     */
    @Override
    public String getEncodedSessionCookie() {
        return mCurrentCookie != null ? encodeCookie(new AigPersistentHttpCookie(mCurrentCookie)) : null;
    }
}
