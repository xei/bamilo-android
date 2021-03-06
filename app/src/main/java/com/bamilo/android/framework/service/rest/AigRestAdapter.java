package com.bamilo.android.framework.service.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bamilo.android.BuildConfig;
import com.bamilo.android.framework.service.rest.configs.AigRestContract;
import com.bamilo.android.framework.service.rest.configs.HeaderConstants;
import com.bamilo.android.framework.service.rest.errors.AigErrorHandler;
import com.bamilo.android.framework.service.utils.TextUtils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Class used to create a request entity with:<br/>
 *  - Logs<br/>
 *  - Http client<br/>
 *  - Api service endpoint<br/>
 *  - Header definition<br/>
 *  - Response Object converter<br/>
 *  - Error handler<br/>
 */
public class AigRestAdapter {

    /**
     * Interface to perform a request
     */
    public interface Request {
        @NonNull String getEndPoint();
        @Nullable Integer getCache();
        Boolean discardResponse();
    }

    /**
     * Prepares everything needed to perform the request regarding Retrofit configurations
     *
     * @return BaseRequest
     */
    public static RestAdapter getRestAdapter(Request request) {
        // Create a rest adapter
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setClient(AigHttpClient.getInstance())
                .setEndpoint(request.getEndPoint())
                .setRequestInterceptor(new HttpHeaderRequestInterceptor(request.getCache()));
        // Validate discard flag
        if(!request.discardResponse()) {
            builder.setConverter(new AigResponseConverter())
                .setErrorHandler(new AigErrorHandler());
        }
        return builder.build();
    }

    /**
     * Header interceptor to set request with:<br/>
     *     - Cache control<br/>
     *     - User agent<br/>
     */
    private static class HttpHeaderRequestInterceptor implements RequestInterceptor {

        private Integer cache = AigRestContract.NO_CACHE;

        private String agent;

        /**
         * Constructor
         */
        public HttpHeaderRequestInterceptor(Integer cache) {
            this.cache = cache;
            this.agent = System.getProperty("http.agent");
        }

        @Override
        public void intercept(RequestFacade request) {
            //Print.d("############# RETROFIT REQUEST INTERCEPTOR #############");
            // CACHE
            if (cache == AigRestContract.NO_CACHE) {
                request.addHeader(HeaderConstants.CACHE_CONTROL, HeaderConstants.CACHE_CONTROL_NO_CACHE);
            } else {
                String value = HeaderConstants.CACHE_CONTROL_MAX_AGE + "=" + cache + "; " + HeaderConstants.CACHE_CONTROL_MUST_REVALIDATE;
                request.addHeader(HeaderConstants.CACHE_CONTROL, value);
            }
            // AGENT
            request.addHeader(HeaderConstants.USER_AGENT, agent + " " + AigRestContract.AUTHENTICATION_USER_AGENT);

            if (TextUtils.isNotEmpty(AigRestContract.USER_LANGUAGE)) {
                request.addHeader(HeaderConstants.USER_LANGUAGE, AigRestContract.USER_LANGUAGE);
            }
            //Print.d("##########################################################");
        }
    }

}
