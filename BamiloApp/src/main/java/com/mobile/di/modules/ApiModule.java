package com.mobile.di.modules;

import android.content.Context;

import com.mobile.view.R;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;

/**
 * Created on 12/24/2017.
 */
@Module
public class ApiModule {


    @Provides
    @Singleton
    @Named("apiBaseUrl")
    public HttpUrl provideApiBaseUrl(Context context) {
        String baseUrl = String.format("http://%s/%s/", context.getString(R.string.single_shop_country_url),
                context.getString(R.string.global_api_version));
        return HttpUrl.parse(baseUrl);
    }
}
