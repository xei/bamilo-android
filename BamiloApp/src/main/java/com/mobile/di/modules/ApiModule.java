package com.mobile.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mobile.service.rest.cookies.AigPersistentHttpCookie;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.security.Base64;
import com.mobile.view.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
        String baseUrl = String.format("http://%s/%s/", context.getString(R.string.single_shop_country_url),
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
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor,
                                            @Named("cookieSharedPrefs") final SharedPreferences cookieSharedPrefs) {

        return new OkHttpClient
                .Builder()
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
                })
                .addInterceptor(interceptor)
                .build();
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
                e.printStackTrace();
            }
        }
        return cookie;
    }
}
