package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName
data class Product(
    @SerializedName("sku") val sku: String, //string
    @SerializedName("title") val title: String, //string
    @SerializedName("image") val image: String //string
): BaseModel()