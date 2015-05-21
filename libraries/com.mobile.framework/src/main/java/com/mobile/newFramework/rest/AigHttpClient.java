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

public class AigHttpClient {

    private static OkHttpClient sOkHttpClient;

    public static OkHttpClient getOkHttpClient(Context context) {
        return sOkHttpClient == null ? sOkHttpClient = newOkHttpClient(context) : sOkHttpClient;
    }

    private static OkHttpClient newOkHttpClient(Context context) {
        // HTTP CLIENT
        OkHttpClient okHttpClient = new OkHttpClient();
        // AUTHENTICATION
        okHttpClient.setAuthenticator(new AigAuthenticator());

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
        // INTERCEPTORS
        okHttpClient.interceptors().add(new RequestInterceptor());
        okHttpClient.interceptors().add(new ResponseInterceptor());
        // Return client
        return okHttpClient;
    }

    private static class RequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            System.out.println("############ OK HTTP: REQUEST INTERCEPTOR ############");
            Request request = chain.request();
            System.out.println("Headers:      \n" + request.headers());
            System.out.println("Url:            " + request.url());
            System.out.println("Https:          " + request.isHttps());
            System.out.println("Method:         " + request.method());
            System.out.println("Body:           " + request.body());
            System.out.println("Cache:          " + request.cacheControl());
            System.out.println("####################################################\n");
            return chain.proceed(request);
        }
    }

    private static class ResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            System.out.println("############ OK HTTP: RESPONSE INTERCEPTOR ############");
            Response response = chain.proceed(chain.request());
            System.out.println("Headers:          \n" + response.headers());
            System.out.println("Message:            " + response.message());
            System.out.println("Redirect:           " + response.isRedirect());
            System.out.println("Cache response:     " + response.cacheResponse());
            System.out.println("Network response:   " + response.networkResponse());
            System.out.println("> Request:          " + response.request().toString());
            System.out.println("######################################################\n");
            return response;
        }
    }

}
