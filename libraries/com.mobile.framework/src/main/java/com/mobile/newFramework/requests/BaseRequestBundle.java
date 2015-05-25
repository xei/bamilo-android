package com.mobile.newFramework.requests;

import java.util.Map;

public class BaseRequestBundle {

    private String url;

    private Integer cache;

    private Map<String, String> data;

    private boolean priority;

    /*
     * GETTERS
     */

    public String getUrl() {
        return url;
    }

    public Map<String, String> getData() {
        return data;
    }

    public boolean isPriority() {
        return priority;
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

        boolean priority;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setPriority(boolean priority) {
            this.priority = priority;
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

        public BaseRequestBundle build() {
            BaseRequestBundle requestBundle = new BaseRequestBundle();
            requestBundle.url = url;
            requestBundle.cache = cache;
            requestBundle.data = data;
            requestBundle.priority = priority;
            return requestBundle;
        }

    }

}
