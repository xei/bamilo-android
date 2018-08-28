package com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class Session(
    @SerializedName("id") val id: String,
    @SerializedName("expire") val expire: Long?,
    @SerializedName("YII_CSRF_TOKEN") val yIICSRFTOKEN: String
)