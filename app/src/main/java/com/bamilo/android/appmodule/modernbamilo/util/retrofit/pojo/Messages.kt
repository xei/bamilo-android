package com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class Messages(
        @SerializedName("success") val success: ArrayList<MessageItem>?,
        @SerializedName("error") val error: ArrayList<MessageItem>?,
        @SerializedName("validate") val validate: ArrayList<Map<String, String>>?
)