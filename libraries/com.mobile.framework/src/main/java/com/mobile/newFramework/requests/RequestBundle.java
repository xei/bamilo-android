package com.mobile.newFramework.requests;

import com.mobile.newFramework.rest.AigRestAdapter;

import java.util.Map;

public class RequestBundle {

    private String url;

    private Integer cache;

    private Map<String, String> data;

    private boolean discard;

    private String pathParameter;

    /*
     * GETTERS
     */

    public String getUrl() {
        return url;
    }

    public String getPathParameter() {
        return pathParameter;
    }

    public Map<String, String> getData() {
        return data;
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean isDiscardedResponse() {
        return discard;
    }

    public Integer getCache() {
        return cache;
    }

    /*
     * BUILDER
     */

    public static class Builder {

        String url;

        Integer cache;

        Map<String, String> data;

        boolean discard;

        String pathParameter;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder discardResponse() {
            this.discard = true;
            return this;
        }

        public Builder setData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public Builder setCache(Integer cache) {
            this.cache = cache;
            return this;
        }

        public Builder setPathParameter(String pathParameter) {
            this.pathParameter = pathParameter;
            return this;
        }

        public RequestBundle build() {
            RequestBundle requestBundle = new RequestBundle();
            requestBundle.url = url;
            requestBundle.cache = cache;
            requestBundle.data = data;
            requestBundle.discard = discard;
            requestBundle.pathParameter = pathParameter;
            return requestBundle;
        }

    }

    public AigRestAdapter.RestAdapterInit toRestAdapterInit(){
        AigRestAdapter.RestAdapterInit restAdapterInit = new AigRestAdapter.RestAdapterInit();
        restAdapterInit.url = url;
        restAdapterInit.cache = cache;
        restAdapterInit.discardResponse = discard;
        return restAdapterInit;
    }

    public void setUrl(String url){
        this.url = url;
    }

}
