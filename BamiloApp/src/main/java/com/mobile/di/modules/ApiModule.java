package com.mobile.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;

import com.bamilo.apicore.service.model.JsonConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.security.Base64;
import com.mobile.view.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpCookie;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created on 12/24/2017.
 */
@Module
public class ApiModule {


    private static final int API_PAGINATED_ITEMS_COUNT = 24;
    private static final String PERSISTENT_COOKIES_FILE = "persistent_cookies";
    private static final String COOKIE_TAG = "cookie..bamilo.com";

    @Provides
    @Singleton
    @Named("apiBaseUrl")
    public HttpUrl provideApiBaseUrl(Context context) {
        String baseUrl = String.format("https://%s/%s/", context.getString(R.string.single_shop_country_url),
                context.getString(R.string.global_api_version));
        return HttpUrl.parse(baseUrl);
    }

    @Provides
    @Named("apiPaginatedItemsCount")
    public int providePaginatedItemsCount() {
        return API_PAGINATED_ITEMS_COUNT;
    }

    @Provides
    @Singleton
    public HttpCookie provideHttpCookie(Context context) {
        SharedPreferences mCookiePrefs = context.getSharedPreferences(PERSISTENT_COOKIES_FILE, Context.MODE_PRIVATE);
        // Get stored encoded cookie
        String encodedCookie = mCookiePrefs.getString(COOKIE_TAG, null);
        // Decode
        HttpCookie cookie = decodeCookie(encodedCookie);
        return cookie;
    }

    @Provides
    @Named("cookieKey")
    public String provideCookieKey(HttpCookie cookie) {
        return cookie.getName();
    }

    @Provides
    @Named("cookieValue")
    public String provideCookieValue(HttpCookie cookie) {
        return cookie.getValue();
    }

    @Provides
    @Singleton
    @Named("cookieSharedPrefs")
    public SharedPreferences provideCookieSharedPreferences(Context context) {
        return context.getSharedPreferences(PERSISTENT_COOKIES_FILE, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor, JsonManipulatorInterceptor jsonManipulatorInterceptor,
                                            @Named("cookieSharedPrefs") final SharedPreferences cookieSharedPrefs) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(jsonManipulatorInterceptor)
                .addInterceptor(interceptor)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {

                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        // Get stored encoded cookie
                        String encodedCookie = cookieSharedPrefs.getString(COOKIE_TAG, null);
                        // Decode
                        HttpCookie sharedCookie = decodeCookie(encodedCookie);
                        Cookie cookie = new Cookie.Builder()
                                .name(sharedCookie.getName())
                                .value(sharedCookie.getValue())
                                .httpOnly()
                                .domain(httpUrl.topPrivateDomain())
                                .path(httpUrl.encodedPath())
                                .build();
                        List<Cookie> cookies = new ArrayList<>();
                        cookies.add(cookie);
                        return cookies;
                    }
                });

        setCertificates(builder);
        return builder.build();
    }

    private void setCertificates(OkHttpClient.Builder okHttpClient) {
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

                okHttpClient.sslSocketFactory(sslContext.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Provides
    @Singleton
    public JsonManipulatorInterceptor provideJsonManipulatorInterceptor(Gson gson) {
        return new JsonManipulatorInterceptor(gson);
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
                cookie = ((com.bamilo.modernbamilo.util.retrofit.AigPersistentHttpCookie) objectInputStream.readObject()).getCookie();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return cookie;
    }

    /**
     * To manipulate type inconsistency
     */
    public static class JsonManipulatorInterceptor implements Interceptor {
        private Gson gson;

        private JsonManipulatorInterceptor(Gson gson) {
            this.gson = gson;
        }

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            JsonObject bodyJson = gson.fromJson(response.body().string(), JsonObject.class);
            JsonElement messages = bodyJson.get(JsonConstants.RestConstants.MESSAGES);
            if (messages instanceof JsonArray) {
                bodyJson.add(JsonConstants.RestConstants.MESSAGES, null);
            }
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), gson.toJson(bodyJson))).build();
        }
    }
}
