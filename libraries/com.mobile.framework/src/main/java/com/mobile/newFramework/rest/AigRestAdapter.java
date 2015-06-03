package com.mobile.newFramework.rest;

import android.content.Context;

import com.mobile.framework.rest.RestContract;

import org.apache.http.protocol.HTTP;

import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class AigRestAdapter {

    public static class RestAdapterInit{
        public String url;
        public Integer cache;
        public boolean discardResponse;
    }
    /**
     * Prepares everything needed to perform the request regarding Retrofit configurations
     *
     * @return BaseRequest
     */
    public static RestAdapter getRestAdapter(Context context, RestAdapterInit restAdapterInit) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(AigHttpClient.getHttpClient(context))
                .setEndpoint(restAdapterInit.url)
                .setRequestInterceptor(new HttpHeaderRequestInterceptor(restAdapterInit.cache));

        if(!restAdapterInit.discardResponse) {
            builder.setConverter(new ResponseConverter())
                .setErrorHandler(new AigErrorHandler());
        }

        return builder.build();
    }

    /**
     * TODO HEADERS
     */
    private static class HttpHeaderRequestInterceptor implements RequestInterceptor {

        Integer cache = RestContract.NO_CACHE;

        String agent;

        public HttpHeaderRequestInterceptor(Integer cache) {
            this.cache = cache;
            this.agent = System.getProperty("http.agent");
        }

        @Override
        public void intercept(RequestFacade request) {
            System.out.println("############# RETROFIT REQUEST INTERCEPTOR #############");
            // MOBAPI VERSION
            // MOBAPI LANG
            // MOBAPI YII_CSRF_TOKEN
            // DEVICE
            // CACHE
            if (cache == RestContract.NO_CACHE) {
                request.addHeader(HeaderConstants.CACHE_CONTROL, HeaderConstants.CACHE_CONTROL_NO_CACHE);
            } else {
                String value = HeaderConstants.CACHE_CONTROL_MAX_AGE + "=" + cache + "; " + HeaderConstants.CACHE_CONTROL_MUST_REVALIDATE;
                request.addHeader(HeaderConstants.CACHE_CONTROL, value);
            }
            // AGENT
            request.addHeader(HTTP.USER_AGENT, agent);
            System.out.println("##########################################################");
        }
    }

}
