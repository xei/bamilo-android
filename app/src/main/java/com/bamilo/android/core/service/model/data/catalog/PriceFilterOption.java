package com.bamilo.android.core.service.model.data.catalog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 12/26/2017.
 */

public class PriceFilterOption {
    @Expose
    @SerializedName("max")
    private long max;

    @Expose
    @SerializedName("min")
    private long min;

    @Expose
    @SerializedName("interval")
    private long interval;

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
