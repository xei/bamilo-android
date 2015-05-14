package com.mobile.framework.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Locale;

import ch.boye.httpclientandroidlib.androidextra.Base64;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import de.akquinet.android.androlog.Log;

/**
 * This class is an extend of the class BasicCookieStore, used to manage the session cookie using shared preferences.
 * @see https://github.com/loopj/android-async-http/blob/master/library/src/main/java/com/loopj/android/http/PersistentCookieStore.java
 * @author spereira
 */
public class PersistentCookieStore extends BasicCookieStore implements ICurrentCookie{

    private static final String TAG = PersistentCookieStore.class.getSimpleName();

    private static final String PERSISTENT_COOKIES_FILE = "persistent_cookies";
    
    private static final String COOKIE_PREFS_TAG = "cookie_jumia";
    
    private static final String PHPSESSID_TAG = "PHPSESSID";

    private static final long serialVersionUID = 1L;

    private SharedPreferences mCookiePrefs;

    private String domain;
    
    /**
     * Construct a persistent cookie store.
     * @param context
     * @author spereira
     */
    public PersistentCookieStore(Context context) {
        Log.i(TAG, "ON CONSTRUCTOR");
        // Load the session cookie
        loadSessionCookie(context);
    }

    /*
     * (non-Javadoc)
     * @see ch.boye.httpclientandroidlib.impl.client.BasicCookieStore#clear()
     */
    @Override
    public void clear() {
        Log.i(TAG, "ON CLEAR COOKIE");
        // Clear cookies from local store
        super.clear();
        // Clear cookies from persistent store
        SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
    }
    
    /*
     * (non-Javadoc)
     * @see ch.boye.httpclientandroidlib.impl.client.BasicCookieStore#addCookie(ch.boye.httpclientandroidlib.cookie.Cookie)
     */
    @Override
    public synchronized void addCookie(Cookie cookie) {
        if(cookie != null) {
            super.addCookie(cookie);
            if (cookie.getName().contains(PHPSESSID_TAG)) {
                this.domain = cookie.getDomain();
            }
        }
    }

    /**
     * Add the encoded string to the store.
     *
     * @param encodedCookie Cookie in string format.
     */
    public void addCookie(String encodedCookie){
        addCookie(decodeCookie(encodedCookie));
    }

    /**
     * Save the session cookie into shared preferences.
     * @author spereira
     */
    public synchronized void saveSessionCookie() {
        // Save cookie into persistent store
        List<Cookie> cookies =  getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().contains(PHPSESSID_TAG) && cookie.getDomain().equals(domain)) {
                Log.i(TAG, "ON PERSIST COOKIE SESSION");
                SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
                String str = encodeCookie(new PersistentCookie(cookie));
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
        Log.i(TAG, "ON LOAD SESSION COOKIE");
        // Get preferences
        mCookiePrefs = context.getSharedPreferences(PERSISTENT_COOKIES_FILE, Context.MODE_PRIVATE);
        // Get stored encoded cookie
        String encodedCookie = mCookiePrefs.getString(COOKIE_PREFS_TAG, null);
        // Create cookie
        if(!TextUtils.isEmpty(encodedCookie)){
            // Get 
            Cookie cookie = decodeCookie(encodedCookie);
            // Save
            if(cookie != null){
                addCookie(cookie);
            }
        }
    }    

    /**
     * Encode a serialize cookie using Base64 into a String.
     * @param cookie
     * @return String or null
     */
    private String encodeCookie(PersistentCookie cookie) {
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
    private Cookie decodeCookie(String cookieString) {
        Log.i(TAG, "ON DECODE COOKIE: " + cookieString);
        byte[] bytes = Base64.decode(cookieString, Base64.DEFAULT);
        Log.i(TAG, "ON DECODE COOKIE: " + new String(bytes));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((PersistentCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            Log.d(TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "ClassNotFoundException in decodeCookie", e);
        }
        return cookie;
    }
    
    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't
     * have to rely on any large Base64 libraries. Can be overridden if you
     * like!
     * 
     * @param bytes
     *            byte array to be converted
     * @return string containing hex values
     */
    @Deprecated
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * Converts hex values from strings to byte arra
     * 
     * @param hexString
     *            string of hex-encoded values
     * @return decoded byte array
     */
    @Deprecated
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Return the cookie that is being used.
     *
     * @return The current cookie.
     */
    public String getCurrentCookie(){
        List<Cookie> cookies =  getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().contains(PHPSESSID_TAG) && cookie.getDomain().equals(domain)) {
                return encodeCookie(new PersistentCookie(cookie));
            }
        }
        return null;
    }

}
