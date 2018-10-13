package com.bamilo.android.framework.service.rest.cookies;

import android.content.Context;
import android.content.SharedPreferences;

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.AigPersistentHttpCookie;
import com.bamilo.android.framework.service.rest.configs.AigRestContract;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.security.Base64;

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

/**
 * Class used to manage cookies.
 *
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

    /*
     * #### STORE COOKIE INTO PREFERENCES  ####
     */

    /**
     * Override to save session cookie.<br>
     * Method used to validate cookies and save the session cookie into shared preferences
     */
    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        super.put(uri, responseHeaders);
        // Get shop domain
        String shop = AigRestContract.getShopDomain();
        // Get cookies
        List<HttpCookie> cookies = getCookieStore().getCookies();
        // Validate cookies
        for (HttpCookie cookie : cookies) {
            if (!cookie.hasExpired() && cookie.getDomain().contains(shop) && cookie.getName().contains(PHP_SESSION_ID_TAG)) {
                store(cookie);
                return;
            }
        }
    }

    /**
     * Persist the session cookie.
     */
    private void store(HttpCookie cookie) {
        //Print.i(TAG, "IS A CANDIDATE COOKIE TO STORE...");
        // Validate the cookie and the current
        if (mCurrentCookie == null || !mCurrentCookie.getValue().equals(cookie.getValue())) {
            mCurrentCookie = cookie;
            SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
            String str = encodeCookie(new AigPersistentHttpCookie(cookie));
            prefsWriter.putString(getCookieKey(), str);
            prefsWriter.apply();
        }
    }

    /*
     * #### LOAD COOKIE FROM PREFERENCES  ####
     */

    /**
     * Load the session cookie from shared preferences.
     */
    private void loadSessionCookie(Context context) {
        // Get preferences
        mCookiePrefs = context.getSharedPreferences(PERSISTENT_COOKIES_FILE, Context.MODE_PRIVATE);
        // Get stored encoded cookie
        String encodedCookie = mCookiePrefs.getString(getCookieKey(), null);
        // Decode
        HttpCookie cookie = decodeCookie(encodedCookie);
        // Save
        addSessionCookie(cookie);
    }

    /**
     * Method used to add a stored cookie
     * @param cookie  - The stored cookie, from CookiePrefs or SessionPrefs
     */
    private boolean addSessionCookie(HttpCookie cookie) {
        // Get shop domain
        String shop = AigRestContract.getShopDomain();
        // Save
        if (cookie != null && !cookie.hasExpired() && cookie.getDomain().contains(shop) && cookie.getName().contains(PHP_SESSION_ID_TAG)) {
            getCookieStore().add(URI.create(AigRestContract.getShopUri()), cookie);
            mCurrentCookie = cookie;
            return true;
        }
        return false;
    }

    /*
     * ####### ENCODE/DECODE METHODS #######
     */

    /**
     * Encode a serialize cookie using Base64 into a String.
     *
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
            return null;
        }
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Create a cookie from cookie string Base64.
     *
     * @return Cookie or null
     * @author spereira
     */
    private HttpCookie decodeCookie(String encodedCookieString) {
        HttpCookie cookie = null;
        if (!TextUtils.isEmpty(encodedCookieString)) {
            byte[] bytes = Base64.decode(encodedCookieString, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                cookie = ((AigPersistentHttpCookie) objectInputStream.readObject()).getCookie();
            } catch (IOException | ClassNotFoundException e) {
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

//    /**
//     * Method used to print a cookie
//     */
//    private void print(HttpCookie cookie) {
//        Print.i(TAG, "PRINT COOKIE: " +
//                cookie.getDomain() + " " +
//                cookie.getName() + " " +
//                cookie.getValue() + " " +
//                cookie.hasExpired() + " " +
//                cookie.getPath() + " " +
//                cookie.getDiscard() + " " +
//                cookie.getMaxAge());
//    }

    /*
     * ####### ICookieManager #######
     */

    /**
     * Add an encoded session cookie
     */
    @Override
    public void addEncodedSessionCookie(String encodedCookie) throws NullPointerException, URISyntaxException {
        HttpCookie cookie = decodeCookie(encodedCookie);
        if (!addSessionCookie(cookie)) {
            throw new NullPointerException("INVALID STORED SESSION FROM PERSISTENT COOKIE");
        }
    }

    /**
     * Get the current session cookie
     *
     * @return Encoded session cookie or null
     */
    @Override
    public String getEncodedSessionCookie() {
        return mCurrentCookie != null ? encodeCookie(new AigPersistentHttpCookie(mCurrentCookie)) : null;
    }
}
