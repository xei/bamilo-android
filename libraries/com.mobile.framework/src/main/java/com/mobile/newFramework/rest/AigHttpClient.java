package com.mobile.newFramework.rest;

import android.content.Context;
import android.os.Build;

import com.mobile.framework.utils.NetworkConnectivity;
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
import java.util.concurrent.TimeUnit;

import retrofit.client.OkClient;

public class AigHttpClient extends OkClient {

    private static AigHttpClient sAigHttpClient;

    private Context mContext;

    public static AigHttpClient getHttpClient(Context context) {
        return sAigHttpClient == null ? sAigHttpClient = new AigHttpClient(context, newOkHttpClient(context)) : sAigHttpClient;
    }

    public AigHttpClient(Context context, OkHttpClient okHttpClient) {
        super(okHttpClient);
        this.mContext = context;
    }

    @Override
    public retrofit.client.Response execute(retrofit.client.Request request) throws IOException {
        if(mContext != null && !NetworkConnectivity.isConnected(mContext)) {
            throw new NoConnectivityException("No network connectivity!");
        }
        return super.execute(request);
    }

    /**
     * Exception
     */
    public class NoConnectivityException extends IOException {
        public NoConnectivityException(String detailMessage) {
            super(detailMessage);
        }
    }


    /*
     * ################ OK HTTP CLIENT ################
     */

    private static OkHttpClient newOkHttpClient(Context context) {
        // HTTP CLIENT
        OkHttpClient okHttpClient = new OkHttpClient();
        // AUTHENTICATION
        okHttpClient.setAuthenticator(new AigAuthenticator());
        // COOKIES
        setCookies(okHttpClient, context);
        // CACHE
        setCache(okHttpClient, context);
        // REDIRECTS
        okHttpClient.setFollowSslRedirects(true);
        okHttpClient.setFollowRedirects(true);
        // TIMEOUTS
        okHttpClient.setReadTimeout(AigConfigurations.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setConnectTimeout(AigConfigurations.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        // INTERCEPTORS
        //addInterceptors(okHttpClient);
        // Return client
        return okHttpClient;
    }

    private static void setCache(OkHttpClient okHttpClient, Context context) {
        if (context != null) {
            // CACHE
            File cacheDir = context.getCacheDir();
            Cache cache = new Cache(cacheDir, AigConfigurations.CACHE_MAX_SIZE);
            okHttpClient.setCache(cache);
        }
    }

    private static void setCookies(OkHttpClient okHttpClient, Context context) {
//        if(context != null) {
//            // TODO: COOKIES
//            //PersistentCookieStore cookieStore = new PersistentCookieStore(context);
//            //CookieManager cookieManager = new CookieManager((CookieStore) cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
//            //okHttpClient.setCookieHandler(cookieManager);
//            AigPersistentCookieStore cookieManager = new AigPersistentCookieStore();
//            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//            CookieHandler.setDefault(cookieManager);
//        }
//        else
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);
        }
        //
        okHttpClient.setCookieHandler(CookieHandler.getDefault());
    }

    private static void addInterceptors(OkHttpClient okHttpClient) {
        //okHttpClient.interceptors().add(new FullUrlRequestInterceptor());
        //okHttpClient.networkInterceptors().add(new FullUrlRequestInterceptor());
        okHttpClient.interceptors().add(new RequestInterceptor());
        okHttpClient.interceptors().add(new ResponseInterceptor());
    }

    private static class FullUrlRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            // Remove the "/" from request because the interface requires an end point as "/{end_point}"
            Request request = chain.request();
            String url = request.urlString();
            if(url != null && url.charAt(url.length() -1) == '/') {
                String newUrl =  url.substring(0, url.length() -1);
                request = request.newBuilder().url(newUrl).build();
            }
            System.out.println("############ OK HTTP: REQUEST INTERCEPTOR ############");
            System.out.println("Headers:      \n" + request.headers());
            System.out.println("Url:            " + request.url());
            System.out.println("UrI:            " + request.uri());
            System.out.println("Https:          " + request.isHttps());
            System.out.println("Method:         " + request.method());
            System.out.println("Body:           " + request.body());
            System.out.println("Cache:          " + request.cacheControl());
            System.out.println("####################################################\n");

            Response response = chain.proceed(request);
            System.out.println("############ OK HTTP: RESPONSE INTERCEPTOR ############");
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

    private static class RequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            System.out.println("############ OK HTTP: REQUEST INTERCEPTOR ############");
            Request request = chain.request();
            System.out.println("Headers:      \n" + request.headers());
            System.out.println("Url:            " + request.url());
            System.out.println("UrI:            " + request.uri());
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
