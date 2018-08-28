package com.bamilo.android.core.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohsen on 1/27/18.
 */

public class Cancellation {

    public static final String REASON_TYPE_IS_SHIPPED = "IS_SHIPPED",
            REASON_TYPE_HAS_CANCELLATION_REQUEST = "HAS_CANCELLATION_REQUEST",
            REASON_TYPE_IS_CANCELED = "IS_CANCELED";

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
