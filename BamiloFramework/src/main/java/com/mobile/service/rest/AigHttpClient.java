package com.mobile.service.rest;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.mobile.service.rest.configs.AigConfigurations;
import com.mobile.service.rest.configs.HeaderConstants;
import com.mobile.service.rest.cookies.AigCookieManager;
import com.mobile.service.rest.cookies.ISessionCookie;
import com.mobile.service.rest.errors.NoConnectivityException;
import com.mobile.service.utils.NetworkConnectivity;
import com.mobile.service.utils.output.Print;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import retrofit.client.OkClient;

/**
 * Class used to represent an http client.
 *
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
        if (sAigHttpClient == null) {
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
     *
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
        if (mContext != null && !NetworkConnectivity.isConnected(mContext)) {
            throw new NoConnectivityException();
        }
        return super.execute(request);
    }

    /*
     * ################ COOKIES ################
     */

    /**
     * Get the current cookie
     *
     * @return AigCookieManager that implements ISessionCookie
     */
    public ISessionCookie getCurrentCookie() {
        return (AigCookieManager) mOkHttpClient.getCookieHandler();
    }

    /**
     * Get a list cookies from CookieManager for WebViews
     *
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
        //CERTIFICATE
        setCertificates(okHttpClient);
        // REDIRECTS
        okHttpClient.setFollowSslRedirects(false);
        okHttpClient.setFollowRedirects(true);
        // TIMEOUTS
        okHttpClient.setReadTimeout(AigConfigurations.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setConnectTimeout(AigConfigurations.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        // INTERCEPTORS
        okHttpClient.interceptors().add(new RedirectResponseInterceptor());
        // Return client
        return okHttpClient;
    }

    private static void setCertificates(OkHttpClient okHttpClient) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                String certString = "-----BEGIN CERTIFICATE-----\n" +
                        "MIIEYjCCA0qgAwIBAgIKIQak23Abuwj69DANBgkqhkiG9w0BAQsFADBLMQswCQYD\n" +
                        "VQQGEwJOTzEdMBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxHTAbBgNVBAMM\n" +
                        "FEJ1eXBhc3MgQ2xhc3MgMiBDQSAyMB4XDTE3MTEyODE0MDUyMloXDTE4MTEyODIy\n" +
                        "NTkwMFowFTETMBEGA1UEAwwKYmFtaWxvLmNvbTCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                        "ggEPADCCAQoCggEBANnJ77A/bgpINMEuz4f9LpjdckYnB5N4t8DzFlIvrcqV3DpA\n" +
                        "0n0e4UpI1DO+Vx+8PEDsbzo8cdbVORxpaBR8kLcc8t3KP14F0J6jTBBzTX9/hVWl\n" +
                        "uNxOJaIjAe/1d3mMa8ZrKNYc3Ut7cReLU+QNGiPTsOoXkQ3i5hQlmkAIdx8ChDbM\n" +
                        "VU9UerjOJC+unXBWRKVyht2FwJG/bmWRb2UcuQLCHWTeMiP/T2Ino3KGo1C6IGLh\n" +
                        "gC8mYQtSocZU6xDnGlLua44qdKAVb0kUGnY06a7/+fZ5OUNoUVadoIvtlCZNpCPm\n" +
                        "ZwHTWYGh+KcDtEbDMLfBkewPru2bfLaEAKaBv4ECAwEAAaOCAXwwggF4MAkGA1Ud\n" +
                        "EwQCMAAwHwYDVR0jBBgwFoAUkq1libIAD8tRDcEj7JROj8EEP3cwHQYDVR0OBBYE\n" +
                        "FGWPQzy3EOK868sbyas29TmC1K+KMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAU\n" +
                        "BggrBgEFBQcDAQYIKwYBBQUHAwIwHwYDVR0gBBgwFjAKBghghEIBGgECBDAIBgZn\n" +
                        "gQwBAgEwOgYDVR0fBDMwMTAvoC2gK4YpaHR0cDovL2NybC5idXlwYXNzLm5vL2Ny\n" +
                        "bC9CUENsYXNzMkNBMi5jcmwwMwYDVR0RBCwwKoIKYmFtaWxvLmNvbYIMKi5iYW1p\n" +
                        "bG8uY29tgg53d3cuYmFtaWxvLmNvbTBqBggrBgEFBQcBAQReMFwwIwYIKwYBBQUH\n" +
                        "MAGGF2h0dHA6Ly9vY3NwLmJ1eXBhc3MuY29tMDUGCCsGAQUFBzAChilodHRwOi8v\n" +
                        "Y3J0LmJ1eXBhc3Mubm8vY3J0L0JQQ2xhc3MyQ0EyLmNlcjANBgkqhkiG9w0BAQsF\n" +
                        "AAOCAQEAS3xvoo4kSQNYLWb3C2UAvG+kM2SHcju4juFOEaGZ9jomEaEYZr2FTwPq\n" +
                        "RHPEnbBjtnCXezYxGR8vYr9ESivJgF69AnkOrzBeR9/wBfCYuen4eUvn1QtMhBWv\n" +
                        "Ge5fawrapB8bDgjX+pbMk+USCIh/lctLNur5e3ZwjQwhF0SnfQvFDjNNejAhsTw/\n" +
                        "hW8pKiCjbgb0rLv9Wtf6biDcc+io6scFO/gIg5QZ/daBGnU8b2njfMOWVZjr/dyW\n" +
                        "X1HvB8iQrRWiwWhddsOty8qgzcSpfCpO9aZDNbGXxFtDVHXl4Vo+kI/P+Dn8NgSC\n" +
                        "eKcCU2WVVLQP/4wB2Wftk6tZoxWL0g==\n" +
                        "-----END CERTIFICATE-----\n" +
                        "-----BEGIN CERTIFICATE-----\n" +
                        "MIIFADCCAuigAwIBAgIBGjANBgkqhkiG9w0BAQsFADBOMQswCQYDVQQGEwJOTzEd\n" +
                        "MBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxIDAeBgNVBAMMF0J1eXBhc3Mg\n" +
                        "Q2xhc3MgMiBSb290IENBMB4XDTE2MTIwMjEwMDAwMFoXDTMwMTAyNjA5MTYxN1ow\n" +
                        "SzELMAkGA1UEBhMCTk8xHTAbBgNVBAoMFEJ1eXBhc3MgQVMtOTgzMTYzMzI3MR0w\n" +
                        "GwYDVQQDDBRCdXlwYXNzIENsYXNzIDIgQ0EgMjCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                        "ggEPADCCAQoCggEBAJyrZ8aWSw0PkdLsyswzK/Ny/A5/uU6EqQ99c6omDMpI+yNo\n" +
                        "HjUO42ryrATs4YHla+xj+MieWyvz9HYaCnrGL0CE4oX8M7WzD+g8h6tUCS0AakJx\n" +
                        "dC5PBocUkjQGZ5ZAoF92ms6C99qfQXhHx7lBP/AZT8sCWP0chOf9/cNxCplspYVJ\n" +
                        "HkQjKN3VGa+JISavCcBqf33ihbPZ+RaLjOTxoaRaWTvlkFxHqsaZ3AsW71qSJwaE\n" +
                        "55l9/qH45vn5mPrHQJ8h5LjgQcN5KBmxUMoA2iT/VSLThgcgl+Iklbcv9rs6aaMC\n" +
                        "JH+zKbub+RyRijmyzD9YBr+ZTaowHvJs9G59uZMCAwEAAaOB6zCB6DAPBgNVHRMB\n" +
                        "Af8EBTADAQH/MB8GA1UdIwQYMBaAFMmAd+BikoL1RpzzuvdMw964o605MB0GA1Ud\n" +
                        "DgQWBBSSrWWJsgAPy1ENwSPslE6PwQQ/dzAOBgNVHQ8BAf8EBAMCAQYwEQYDVR0g\n" +
                        "BAowCDAGBgRVHSAAMD0GA1UdHwQ2MDQwMqAwoC6GLGh0dHA6Ly9jcmwuYnV5cGFz\n" +
                        "cy5uby9jcmwvQlBDbGFzczJSb290Q0EuY3JsMDMGCCsGAQUFBwEBBCcwJTAjBggr\n" +
                        "BgEFBQcwAYYXaHR0cDovL29jc3AuYnV5cGFzcy5jb20wDQYJKoZIhvcNAQELBQAD\n" +
                        "ggIBAESxBqAQmbrsyDeWL7r3QspDhkZxX+VtqHkI6A9OxR1HgahHDW4jJgGypwP4\n" +
                        "jjWkvqZ7lG4DHNv4tfAiR9bEg8wrRC9HLzF+Jm6vtUJsa/sMmkLDlmL8OKKFEZwI\n" +
                        "oBwEuwbCpDByTkD4m7ckPLI0XMzCxSXanKGgtxzFQdmnUHP3NpK9SdULGvz6E3I6\n" +
                        "QomcWJXRqOo8QOrnOLEkI5OM4Bq9lx/GVHubIOz2GZfiX3x91pcb6IxayTSIQDlL\n" +
                        "mcinv/AHInVqrVH/7hWv90yA6qG9LZc10DvnNw5/MyHEZuYsNqo8Gh14SNPqU696\n" +
                        "TSwDzEBHo4LgQsVdQlGCmmr0tF6Lu7kJlukYXFBTDFYD45pG2mPbtHgOQfBLOVjx\n" +
                        "/iNlxhu31vzcII0USKDnCYPaCOyXihnLbblHiJFTXzhNbZEstoSBH6PfChkUxORc\n" +
                        "YQ6w69TtOXs75fvfP4aVcBc6tIALQKYews3+xxqDOmXxJmL5d+B/sxzlmHruNELu\n" +
                        "Nqc5I66yJGEBrhIhNrRBUXIIz+W5w+uw2zg1T9Fo4Nq4EmEL4o1o6DyvI50D0EWZ\n" +
                        "0fkjkYNhL7htM6ktLyBoqMZaMDFsfD9S3e6ZjyNbpjHMFEvhr0vHVHWBMScYs6q2\n" +
                        "owfuqc6rT+Pco5CyOfYF33ke+z/hFe+6n6Y3oiGMDqvaTAfp\n" +
                        "-----END CERTIFICATE-----\n";
                InputStream cert = new ByteArrayInputStream(certString.getBytes());
                Certificate ca;
                try {
                    ca = cf.generateCertificate(cert);
                } finally {
                    cert.close();
                }

                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // creating a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // creating an SSLSocketFactory that uses our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

                okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (context != null) {
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
     * Add debug network interceptor (DebugTools).
     */
    @SuppressWarnings("unused")
    public void addDebugNetworkInterceptors(Interceptor interceptor) {
        mOkHttpClient.networkInterceptors().add(interceptor);
    }

    /**
     * Interceptor used to validate 301 redirects.<br>
     * - If the server returns an 301 error code after an https request,
     * we should try to perform the same request with http,
     * we need to use the returned Location and keep the body info.
     */
    private static class RedirectResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (response.networkResponse().code() == HttpURLConnection.HTTP_MOVED_PERM) {
                Request request = chain.request();
                int tryCount = 0;
                while (!response.isSuccessful() && tryCount < 1) {
                    tryCount++;
                    Request recoveryRequest = request.newBuilder().url(response.headers().get(HeaderConstants.LOCATION)).build();
                    // retry the request
                    response = chain.proceed(recoveryRequest);
                }
                Print.w(TAG, "############ OK HTTP: REDIRECT RESPONSE INTERCEPTOR ############");
                Print.w(TAG, "Network response:   " + response.networkResponse());
                Print.w(TAG, "> Request:          " + response.request());
                Print.w(TAG, "> Method:           " + chain.request().method());
                Print.w(TAG, "######################################################\n");
            }
            return response;
        }
    }

    public static void clearCache(Context context) throws IOException {
        Print.d(TAG, "Clearing cache");
        if (context != null) {
            Cache cache = new Cache(getCache(context), AigConfigurations.CACHE_MAX_SIZE);
            cache.delete();
        }

    }

    private static File getCache(@NonNull Context context) {
        return new File(context.getFilesDir() + "/retrofitCache");
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
