package com.bamilo.apicore.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohsen on 1/27/18.
 */

public class Cancellation {

    @SerializedName("isCancelable")
    @Expose
    private boolean cancelable;

    @SerializedName("notCancelableReason")
    @Expose
    private String notCancelableReason;

    @SerializedName("notCancelableReasonType")
    @Expose
    private String notCancelableReasonType;

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public String getNotCancelableReason() {
        return notCancelableReason;
    }

    public void setNotCancelableReason(String notCancelableReason) {
        this.notCancelableReason = notCancelableReason;
    }

    public String getNotCancelableReasonType() {
        return notCancelableReasonType;
    }

    public void setNotCancelableReasonType(String notCancelableReasonType) {
        this.notCancelableReasonType = notCancelableReasonType;
    }
}
