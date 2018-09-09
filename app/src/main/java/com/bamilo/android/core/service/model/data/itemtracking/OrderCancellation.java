package com.bamilo.android.core.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mohsen on 1/27/18.
 */

public class OrderCancellation {
    @SerializedName("isCancellationServiceEnabled")
    @Expose
    private boolean cancellationServiceEnabled;
    @SerializedName("reasons")
    @Expose
    private List<CancellationReason> reasons;
    @SerializedName("noticeMessage")
    @Expose
    private String noticeMessage;
    @SerializedName("refundMessage")
    @Expose
    private String refundMessage;

    public boolean isCancellationServiceEnabled() {
        return cancellationServiceEnabled;
    }

    public void setCancellationServiceEnabled(boolean cancellationServiceEnabled) {
        this.cancellationServiceEnabled = cancellationServiceEnabled;
    }

    public List<CancellationReason> getReasons() {
        return reasons;
    }

    public void setReasons(List<CancellationReason> reasons) {
        this.reasons = reasons;
    }

    public String getNoticeMessage() {
        return noticeMessage;
    }

    public void setNoticeMessage(String noticeMessage) {
        this.noticeMessage = noticeMessage;
    }

    public String getRefundMessage() {
        return refundMessage;
    }

    public void setRefundMessage(String refundMessage) {
        this.refundMessage = refundMessage;
    }
}
