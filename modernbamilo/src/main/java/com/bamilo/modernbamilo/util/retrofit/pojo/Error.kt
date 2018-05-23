package com.bamilo.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class Error(
    @SerializedName("reason") val reason: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int
)