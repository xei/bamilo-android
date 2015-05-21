package com.mobile.newFramework.requests;

import java.util.Map;

public class BaseRequestBundle {

    private String url;

    private Map<String, String> data;

    private boolean priority;


    public String getUrl() {
        return url;
    }

    public Map<String, String> getData() {
        return data;
    }

    public boolean isPriority() {
        return priority;
    }

    public static class Builder {

        String url;

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

        public BaseRequestBundle build() {
            BaseRequestBundle requestBundle = new BaseRequestBundle();
            requestBundle.url = url;
            requestBundle.data = data;
            requestBundle.priority = priority;
            return requestBundle;
        }

    }

}
