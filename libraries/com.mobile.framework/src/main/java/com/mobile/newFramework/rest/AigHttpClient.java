package com.mobile.newFramework.rest;

import android.content.Context;

import com.mobile.framework.rest.PersistentCookieStore;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.concurrent.TimeUnit;

import retrofit.client.OkClient;

/**
 * Created by spereira on 5/19/15.
 */
public class AigHttpClient {

    private static OkClient sOkHttpClient;

    public static OkClient getOkHttpClient(Context context) {
        return sOkHttpClient == null ? sOkHttpClient = newOkHttpClient(context) : sOkHttpClient;
    }

    private static OkClient newOkHttpClient(Context context) {
        // HTTP CLIENT
        OkHttpClient okHttpClient = new OkHttpClient();
        // AUTHENTICATION
        okHttpClient.setAuthenticator(new AigAuthenticator());
        // HEADERS
        // TODO: UA, GZIP
        if (context != null) {
            // COOKIES
            PersistentCookieStore cookieStore = new PersistentCookieStore(context);
            CookieManager cookieManager = new CookieManager((CookieStore) cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
            okHttpClient.setCookieHandler(cookieManager);
            // CACHE
            File cacheDir = context.getCacheDir();
            Cache cache = new Cache(cacheDir, AigConfigurations.CACHE_MAX_SIZE);
            okHttpClient.setCache(cache);
        }
        // REDIRECTS
        okHttpClient.setFollowSslRedirects(true);
        okHttpClient.setFollowRedirects(true);
        // TIMEOUTS
        okHttpClient.setReadTimeout(AigConfigurations.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setConnectTimeout(AigConfigurations.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        // RETRIES

        okHttpClient.interceptors().add(new LoggingInterceptor());


        return new OkClient(okHttpClient);
    }

    private static class LoggingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            System.out.println("############ OK HTTP CLIENT ############");

            long t1 = System.nanoTime();
            System.out.println(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            System.out.println(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            System.out.println("#########################################\n");

            return response;
        }
    }

//    /**
//     * Method used to set the user agent
//     * @author sergiopereira
//     */
//    private void setHttpUserAgent(){
//        // CASE Default user agent
//        String defaultUserAgent = System.getProperty("http.agent");
//        Log.i(TAG, "DEFAULT USER AGENT: " + defaultUserAgent);
//        if(!TextUtils.isEmpty(defaultUserAgent)) {
//            mHttpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, defaultUserAgent);
//        }
//    }

}
