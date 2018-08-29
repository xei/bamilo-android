package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName
data class Survey(
    @SerializedName("id") val id: Int, //0
    @SerializedName("alias") val alias: String, //string
    @SerializedName("product") val product: Product?,
    @SerializedName("title") val title: String, //string
    @SerializedName("pages") val pages: List<Page>
): BaseModel()