package com.bamilo.apicore.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohsen on 1/27/18.
 */

public class Refund {
    public static final String STATUS_PENDING = "pending", STATUS_SUCCESS = "success";
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("cardNumber")
    @Expose
    private String cardNumber;
    @SerializedName("cancellation_reason")
    @Expose
    private String cancellationReason;
    @SerializedName("date")
    @Expose
    private String date;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
