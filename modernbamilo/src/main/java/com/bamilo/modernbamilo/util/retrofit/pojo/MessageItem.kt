package com.bamilo.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class MessageItem(
    @SerializedName("reason") val reason: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int
)