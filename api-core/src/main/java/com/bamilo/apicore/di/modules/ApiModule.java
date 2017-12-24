package com.bamilo.apicore.di.modules;

import com.bamilo.apicore.service.BamiloApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {

    private static final String API_BASE_URL = "http://www.bamilo.com/mobapi/";
    private static final String API_VERSION = "v2.6/";

    @Provides
    @Singleton
    public BamiloApiService provideApiService(@Named("retrofit") Retrofit retrofit) {
        return retrofit.create(BamiloApiService.class);
    }


    @Provides
    @Singleton
    @Named("retrofit")
    public Retrofit provideRetrofit(@Named("apiBaseUrl") HttpUrl baseUrl, OkHttpClient client, Converter.Factory converterFactory,
                                    CallAdapter.Factory callAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    public Converter.Factory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor) {
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Provides
    @Singleton
    @Named("apiBaseUrl")
    public HttpUrl provideApiBaseUrl(@Named("apiVersion") String apiVersion) {
        return HttpUrl.parse(API_BASE_URL + apiVersion);
    }

    @Provides
    @Singleton
    @Named("apiVersion")
    public String provideApiVersion() {
        return API_VERSION;
    }

    @Provides
    @Singleton
    public CallAdapter.Factory provideRxJavaCallAdapterFactory() {
        return RxJavaCallAdapterFactory.create();
    }
}
