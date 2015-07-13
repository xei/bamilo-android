package com.mobile.newFramework.rest.cookies;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mobile.newFramework.rest.configs.AigRestContract;
import com.mobile.newFramework.utils.security.Base64;

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

/**
 * Class used to manage cookies.
 * @author sergiopereira
 */
public class AigCookieManager extends CookieManager implements ISessionCookie {

    private static final String TAG = AigCookieManager.class.getSimpleName();

    private static final String PERSISTENT_COOKIES_FILE = "persistent_cookies";

    private static final String COOKIE_PREFIX_TAG = "cookie.";

    private static final String PHP_SESSION_ID_TAG = "PHPSESSID";

    private SharedPreferences mCookiePrefs;

    private HttpCookie mCurrentCookie;

    /**
     * Constructor
     */
    public AigCookieManager(Context context) {
        // Load session cookie from prefs
        loadSessionCookie(context);
    }

    /**
     * Get persistent cookie key.
     */
    private String getCookieKey() {
        return COOKIE_PREFIX_TAG + AigRestContract.getShopDomain();
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
//            Log.i(TAG, "TRY STORED COOKIE: " +
//                    cookie.getDomain() + " " +
//                    cookie.getName() + " " +
//                    cookie.getValue() + " " +
//                    cookie.hasExpired() + " " +
//                    cookie.getPath()+ " " +
//                    cookie.getMaxAge());

            if(!cookie.hasExpired() && cookie.getDomain().contains(shop) && cookie.getName().contains(PHP_SESSION_ID_TAG)) {
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
        if (mCurrentCookie == null || !mCurrentCookie.getValue().equals(cookie.getValue())) {
            mCurrentCookie = cookie;
            SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
            String str = encodeCookie(new AigPersistentHttpCookie(cookie));
            prefsWriter.putString(getCookieKey(), str);
            prefsWriter.apply();
            Log.i(TAG, "STORED COOKIE: " + cookie.getDomain() + " " + cookie.getName() + " " + cookie.getValue());
        }
    }

    /**
     * Load the session cookie from shared preferences.
     */
    private void loadSessionCookie(Context context) {
        // Get shop domain
        String shop = AigRestContract.getShopDomain();
        // Get preferences
        mCookiePrefs = context.getSharedPreferences(PERSISTENT_COOKIES_FILE, Context.MODE_PRIVATE);
        // Get stored encoded cookie
        String encodedCookie = mCookiePrefs.getString(getCookieKey(), null);
        // Decode
        HttpCookie cookie = decodeCookie(encodedCookie);
        // Save
        if (cookie != null && !cookie.hasExpired() && cookie.getDomain().contains(shop) && cookie.getName().contains(PHP_SESSION_ID_TAG)) {
            Log.i(TAG, "LOADED COOKIE FROM PERFS: " + cookie.getDomain() + " " + cookie.getName() + " " + cookie.getValue());
            getCookieStore().add(URI.create(cookie.getDomain()), cookie);
            mCurrentCookie = cookie;
        } else if(cookie != null && !cookie.hasExpired()){
            Log.i(TAG, "LOADED COOKIE FROM PERFS: EXPIRED COOKIE" + cookie.getDomain() + " " + cookie.getName());
        } else if(cookie != null && !cookie.getDomain().contains(shop) ){
            Log.i(TAG, "LOADED COOKIE FROM PERFS: NO MATCH " + shop + " != " + cookie.getDomain());
        } else {
            Log.i(TAG, "LOADED COOKIE FROM PERFS: IS EMPTY");
        }
    }

    /**
     * Encode a serialize cookie using Base64 into a String.
     * @return String or null
     */
    private String encodeCookie(AigPersistentHttpCookie cookie) {
        if (cookie == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.w(TAG, "WARNING: EXCEPTION IN ENCODE COOKIE", e);
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
                Log.w(TAG, "WARNING: EXCEPTION IN DECODE COOKIE", e);
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
        if (cookie != null && !cookie.hasExpired()) {
            getCookieStore().add(new URI(cookie.getDomain()), cookie);
            Log.i(TAG, "STORED SESSION FROM PERSISTENT COOKIE: " + cookie.getDomain() + " " + cookie.getName() + " " + cookie.getValue());
        } else {
            throw new NullPointerException("INVALID STORED SESSION FROM PERSISTENT COOKIE");
        }
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
