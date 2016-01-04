package com.mobile.newFramework.utils.debug;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Class used to initialize debug tools for debug version.
 */
public class DebugTools {

    public static boolean IS_DEBUGGABLE = false;

    private static final String TAG = DebugTools.class.getSimpleName();

    /**
     * Install and initialize debug tools only for debug version
     */
    public static void initialize(Application application) {
        // Get flag from application
        IS_DEBUGGABLE = DeviceInfoHelper.isDebuggable(application);
        // Validate and initialize
        if (IS_DEBUGGABLE) {
            // Logs
            Print.initializeAndroidMode(application);
            // #LEAK
            LeakCanary.install(application);
            // #STETHO
            Stetho.initializeWithDefaults(application);
            // Warning
            Print.w(TAG, "WARNING: APPLICATION IN DEBUG MODE");
        }
    }

    /**
     * Add network interceptors only for debug version
     */
    public static void addNetWorkInterceptors(OkHttpClient okHttpClient) {
        if (IS_DEBUGGABLE) {
            // #STETHO :: Enable Network Inspection
            okHttpClient.networkInterceptors().add(new StethoInterceptor());
            //okHttpClient.interceptors().add(new RequestDebuggerInterceptor());
            //okHttpClient.interceptors().add(new ResponseDebuggerInterceptor());
        }
    }

    @SuppressWarnings("unused")
    private static class RequestDebuggerInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Print.d(AigHttpClient.TAG, "############ OK HTTP: REQUEST INTERCEPTOR ############");
            Request request = chain.request();
            Print.d(AigHttpClient.TAG, "Headers:      \n" + request.headers());
            Print.d(AigHttpClient.TAG, "Url:            " + request.url());
            Print.d(AigHttpClient.TAG, "UrI:            " + request.uri());
            Print.d(AigHttpClient.TAG, "Https:          " + request.isHttps());
            Print.d(AigHttpClient.TAG, "Method:         " + request.method());
            Print.d(AigHttpClient.TAG, "Body:           " + request.body());
            Print.d(AigHttpClient.TAG, "Cache:          " + request.cacheControl());
            Print.d(AigHttpClient.TAG, "####################################################\n");
            return chain.proceed(request);
        }
    }

    @SuppressWarnings("unused")
    private static class ResponseDebuggerInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Print.d(AigHttpClient.TAG, "############ OK HTTP: RESPONSE INTERCEPTOR ############");
            Response response = chain.proceed(chain.request());
            Print.d(AigHttpClient.TAG, "Headers:          \n" + response.headers());
            Print.d(AigHttpClient.TAG, "Message:            " + response.message());
            Print.d(AigHttpClient.TAG, "Redirect:           " + response.isRedirect());
            Print.d(AigHttpClient.TAG, "Cache response:     " + response.cacheResponse());
            Print.d(AigHttpClient.TAG, "Network response:   " + response.networkResponse());
            Print.d(AigHttpClient.TAG, "> Request:          " + response.request());
            Print.d(AigHttpClient.TAG, "> Method:           " + chain.request().method());
            Print.d(AigHttpClient.TAG, "######################################################\n");
            return response;
        }
    }

}
