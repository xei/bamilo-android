package com.mobile.newFramework.rest;

import com.mobile.newFramework.rest.configs.AigRestContract;
import com.mobile.newFramework.rest.configs.HeaderConstants;
import com.mobile.newFramework.rest.errors.AigErrorHandler;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

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
     * Constructor
     */
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
    public static RestAdapter getRestAdapter(RestAdapterInit restAdapterInit) {
        // Create a rest adapter
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(getLogLevel())
                .setClient(AigHttpClient.getInstance())
                .setEndpoint(restAdapterInit.url)
                .setRequestInterceptor(new HttpHeaderRequestInterceptor(restAdapterInit.cache));
        // Validate discard flag
        if(!restAdapterInit.discardResponse) {
            builder.setConverter(new AigResponseConverter())
                .setErrorHandler(new AigErrorHandler());
        }
        return builder.build();
    }

    /**
     * Get log level based in Androlog
     */
    private static RestAdapter.LogLevel getLogLevel() {
        return Print.isLoggable() ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
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

            if(TextUtils.isNotEmpty(AigRestContract.USER_LANGUAGE)) {
                request.addHeader(HeaderConstants.USER_LANGUAGE, AigRestContract.USER_LANGUAGE);
            }
            //Print.d("##########################################################");
        }
    }

}
