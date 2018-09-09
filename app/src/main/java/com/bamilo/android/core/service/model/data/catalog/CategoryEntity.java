
package com.bamilo.android.core.service.model.data.catalog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryEntity {

    @SerializedName("url_key")
    @Expose
    private String urlKey;
    @SerializedName("name")
    @Expose
    private String name;

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
