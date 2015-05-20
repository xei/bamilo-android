package com.mobile.newFramework.rest;

import android.content.Context;

import retrofit.RestAdapter;

/**
 * Created by spereira on 5/19/15.
 */
public class AigRestAdapter {

    /**
     * Prepares everything needed to perform the request regarding Retrofit configurations
     *
     * @return BaseRequest
     */
    public static RestAdapter getRestAdapter(Context context, String url){
        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(AigHttpClient.getOkHttpClient(context))
                .setEndpoint(url)
                //.setConverter(JovagoApplication.sGsonConverter)
                //.setRequestInterceptor(new RequestInterceptor())
                //.setErrorHandler(new JovagoErrorHandler())
                .build();
    }

}
