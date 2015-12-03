package com.mobile.newFramework.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobile.newFramework.rest.configs.AigConfigurations;
import com.mobile.newFramework.rest.cookies.AigCookieManager;
import com.mobile.newFramework.rest.cookies.ISessionCookie;
import com.mobile.newFramework.rest.errors.NoConnectivityException;
import com.mobile.newFramework.utils.NetworkConnectivity;
import com.mobile.newFramework.utils.output.Print;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.client.OkClient;

/**
 * Class used to represent an http client.
 * @author sergiopereira
 */
public class AigHttpClient extends OkClient {

    public static final String TAG = AigHttpClient.class.getSimpleName();

    private static AigHttpClient sAigHttpClient;

    private final OkHttpClient mOkHttpClient;

    private Context mContext;

    /*
     * ###### TEST CONSTRUCTOR ######
     */

    /**
     * Initialize rest client in test mode
     */
    public static void initializeTestingMode() {
        sAigHttpClient = new AigHttpClient(newOkHttpClient(null));
    }

    /**
     * Create rest client with specific OkHttpClient in test mode
     */
    private AigHttpClient(OkHttpClient okHttpClient) {
        super(okHttpClient);
        this.mOkHttpClient = okHttpClient;
    }


    /*
     * ###### NORMAL CONSTRUCTOR ######
     */


    /**
     * Initialize rest client
     */
    public static AigHttpClient getInstance(Context context) {
        if(sAigHttpClient == null) {
            init(context);
        }
        return sAigHttpClient;
    }

    /**
     * Initialize rest client
     */
    private static void init(Context context) {
        sAigHttpClient = new AigHttpClient(context, newOkHttpClient(context));
    }

    /**
     * Create rest client with specific OkHttpClient
     */
    private AigHttpClient(Context context, OkHttpClient okHttpClient) {
        super(okHttpClient);
        this.mOkHttpClient = okHttpClient;
        this.mContext = context;
    }

    /*
     * ###### GET SINGLETON INSTANCE ######
     */

    /**
     * Get the current instance of http client
     * @return AigHttpClient
     */
    public static AigHttpClient getInstance() {
        return sAigHttpClient;
    }

    /**
     * Intercept request execution to validate the network connectivity.
     */
    @Override
    public retrofit.client.Response execute(retrofit.client.Request request) throws IOException {
        if(mContext != null && !NetworkConnectivity.isConnected(mContext)) {
            throw new NoConnectivityException();
        }
        return super.execute(request);
    }

    /*
     * ################ COOKIES ################
     */

    /**
     * Get the current cookie
     * @return AigCookieManager that implements ISessionCookie
     */
    public ISessionCookie getCurrentCookie() {
        return (AigCookieManager) mOkHttpClient.getCookieHandler();
    }

    /**
     * Get a list cookies from CookieManager for WebViews
     * @return List<HttpCookie>
     */
    public List<HttpCookie> getCookies() {
        return ((AigCookieManager) mOkHttpClient.getCookieHandler()).getCookies();
    }

    /**
     * Remove all cookies from Cookie Manager
     */
    public void clearCookieStore() {
        ((AigCookieManager) mOkHttpClient.getCookieHandler()).removeAll();
    }

    /*
     * ################ OK HTTP CLIENT ################
     */

    /**
     * Create a specific OkHttpClient
     */
    private static OkHttpClient newOkHttpClient(Context context) {
        // HTTP CLIENT
        OkHttpClient okHttpClient = new OkHttpClient();
        // COOKIES
        setCookies(okHttpClient, context);
        // CACHE
        setCache(okHttpClient, context);
        // REDIRECTS
        okHttpClient.setFollowSslRedirects(false);
        okHttpClient.setFollowRedirects(true);
        // TIMEOUTS
        okHttpClient.setReadTimeout(AigConfigurations.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setConnectTimeout(AigConfigurations.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        // INTERCEPTORS
        addInterceptors(okHttpClient);
        // Return client
        return okHttpClient;
    }

    /**
     * Set cache case not in test mode.
     */
    private static void setCache(OkHttpClient okHttpClient, Context context) {
        if (context != null) {
            // CACHE
            Cache cache = new Cache(getCache(context), AigConfigurations.CACHE_MAX_SIZE);
            okHttpClient.setCache(cache);
        }
    }

    /**
     * Set the cookie manager.
     */
    private static void setCookies(OkHttpClient okHttpClient, Context context) {
        // Case not in test mode
        if(context != null) {
            Print.i(TAG, "ENABLED AIG COOKIE MANAGER RELEASE MODE");
            AigCookieManager cookieManager = new AigCookieManager(context);
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);
        }
        // Case in test mode
        else {
            Print.w(TAG, "WARNING: ENABLED THE COOKIE MANAGER TEST MODE");
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);
        }
        okHttpClient.setCookieHandler(CookieHandler.getDefault());
    }

    /**
     * Add some http interceptors to debug.
     */
    @SuppressWarnings("unused")
    private static void addInterceptors(OkHttpClient okHttpClient) {
        okHttpClient.interceptors().add(new RequestInterceptor());
        okHttpClient.interceptors().add(new ResponseInterceptor());
    }

    private static class RequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Print.d(TAG, "############ OK HTTP: REQUEST INTERCEPTOR ############");
            Request request = chain.request();
            Print.d(TAG, "Headers:      \n" + request.headers());
            Print.d(TAG, "Url:            " + request.url());
            Print.d(TAG, "UrI:            " + request.uri());
            Print.d(TAG, "Https:          " + request.isHttps());
            Print.d(TAG, "Method:         " + request.method());
            Print.d(TAG, "Body:           " + request.body());
            Print.d(TAG, "Cache:          " + request.cacheControl());
            Print.d(TAG, "####################################################\n");
            return chain.proceed(request);
        }
    }

    private static class ResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Print.d(TAG, "############ OK HTTP: RESPONSE INTERCEPTOR ############");
            Response response = chain.proceed(chain.request());
            Print.d(TAG, "Headers:          \n" + response.headers());
            Print.d(TAG, "Message:            " + response.message());
            Print.d(TAG, "Redirect:           " + response.isRedirect());
            Print.d(TAG, "Cache response:     " + response.cacheResponse());
            if(response.networkResponse().code() == HttpURLConnection.HTTP_MOVED_PERM){
                Print.d(TAG, "code1Network response code:   " + response.networkResponse().code());
                Request request = chain.request();
                int tryCount = 0;
                while (!response.isSuccessful() && tryCount < 1) {

                    Print.d(TAG, "code1interceptRequest is not successful - " + tryCount + " scheme "+request.uri().getScheme());
                    Print.d(TAG, "code1interceptRequest is not successful - " + tryCount + " Location "+ response.headers().get("Location"));
                    tryCount++;
                    Request recoveryRequest = request.newBuilder().url(response.headers().get("Location").toString()).build();
//                    request.newBuilder().url(request.headers().get("Location").toString());
                    Print.d(TAG, "code1interceptRequest is not successful - " + tryCount + " scheme "+request.uri().getScheme());
                    // retry the request
                    response = chain.proceed(recoveryRequest);
                }
            }
            Print.d(TAG, "Network response:   " + response.networkResponse());
            Print.d(TAG, "> Request:          " + response.request());
            Print.d(TAG, "> Method ###:          " + chain.request().method());
            Print.d(TAG, "> Body ###:          " + chain.request().body());
            Print.d(TAG, "######################################################\n");
            return response;
        }
    }

    public static void clearCache(Context context) throws IOException {
        Print.d(TAG, "Clearing cache");
        if(context != null) {
            Cache cache = new Cache(getCache(context), AigConfigurations.CACHE_MAX_SIZE);
            cache.delete();
        }

    }

    private static File getCache(@NonNull Context context){
        return new File(context.getFilesDir() + "/retrofitCache");
    }

}
