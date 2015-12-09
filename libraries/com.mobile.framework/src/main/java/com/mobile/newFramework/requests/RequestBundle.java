package com.mobile.newFramework.requests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.utils.CollectionUtils;

import java.util.Map;

/**
 * Class used to save all content used to perform a request.
 */
public class RequestBundle implements AigRestAdapter.Request {

    private String endpoint;
    private String path;
    private Map<String, String> data;
    private Integer cache;
    private boolean discard;

    /*
     * ##### BUILDER #####
     */

    public RequestBundle(@NonNull  String endpoint) {
        this.endpoint = endpoint;
    }

    public RequestBundle setCache(@Nullable Integer cache) {
        this.cache = cache;
        return this;
    }

    public RequestBundle addQueryPath(@NonNull String path) {
        this.path = path;
        return this;
    }

    public RequestBundle addQueryData(@NonNull Map<String, String> data) {
        this.data = data;
        return this;
    }

    /*
     * ##### GETTERS #####
     */

    @NonNull
    public String getPath() {
        return path;
    }

    @NonNull
    public Map<String, String> getData() {
        return data;
    }

    public boolean hasData() {
        return CollectionUtils.isNotEmpty(data);
    }

    /*
     * ##### AigRestAdapter.Request #####
     */

    @NonNull
    @Override
    public String getEndPoint() {
        return endpoint;
    }

    @Nullable
    @Override
    public Integer getCache() {
        return cache;
    }

    @Override
    public Boolean discardResponse() {
        return discard;
    }

    /*
     * ##### OTHER BUILDER #####
     */

    @Deprecated
    @SuppressWarnings("unused")
    public static class Builder {

        private String endpoint;
        private String path;
        private Map<String, String> data;
        private Integer cache;
        private boolean discard;

        public Builder setEndPoint(String endPoint) {
            this.endpoint = endPoint;
            return this;
        }

        public Builder discardResponse() {
            this.discard = true;
            return this;
        }

        public Builder setCache(Integer cache) {
            this.cache = cache;
            return this;
        }

        public Builder addQueryPath(String path) {
            this.path = path;
            return this;
        }

        public Builder addQueryData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public RequestBundle build() {
            RequestBundle requestBundle = new RequestBundle(endpoint);
            requestBundle.path = path;
            requestBundle.data = data;
            requestBundle.discard = discard;
            requestBundle.cache = cache;
            return requestBundle;
        }

    }

}
