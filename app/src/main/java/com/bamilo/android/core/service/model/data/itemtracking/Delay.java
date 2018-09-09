package com.bamilo.android.core.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohsen on 1/27/18.
 */

public class Delay {
    @SerializedName("has_delay")
    @Expose
    private boolean hasDelay;
    @SerializedName("reason")
    @Expose
    private String reason;

    public boolean hasDelay() {
        return hasDelay;
    }

    public void setHasDelay(boolean hasDelay) {
        this.hasDelay = hasDelay;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
