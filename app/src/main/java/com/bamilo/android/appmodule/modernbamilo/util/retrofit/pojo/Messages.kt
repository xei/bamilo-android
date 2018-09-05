package com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class Messages(
        @SerializedName("success") val success: MessageItem?,
        @SerializedName("error") val error: MessageItem?,
        @SerializedName("validate") val validate: MessageItem?
)