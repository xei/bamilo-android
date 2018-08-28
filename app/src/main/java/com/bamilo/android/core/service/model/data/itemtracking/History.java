
package com.bamilo.android.core.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {
    public static final String STATUS_ACTIVE = "active",
            STATUS_INACTIVE = "inactive", STATUS_SUCCESS = "success",
            STATUS_FAILED = "failed";
    public static final String NAME_NEW = "new", NAME_APPROVED = "exportable",
            NAME_RECEIVED = "item_received", NAME_SHIPPED = "shipped", NAME_DELIVERED = "delivered";

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("progress")
    @Expose
    private Integer progress;
    @SerializedName("widthMultiplier")
    @Expose
    private Integer widthMultiplier;
    @SerializedName("name_fa")
    @Expose
    private String nameFa;
    @SerializedName("date")
    @Expose
    private String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getWidthMultiplier() {
        return widthMultiplier;
    }

    public void setWidthMultiplier(Integer widthMultiplier) {
        this.widthMultiplier = widthMultiplier;
    }

    public String getNameFa() {
        return nameFa;
    }

    public void setNameFa(String nameFa) {
        this.nameFa = nameFa;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
