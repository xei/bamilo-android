package com.bamilo.apicore.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mohsen on 1/27/18.
 */

public class CancellationReason {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("text")
    @Expose
    private String text;
}
