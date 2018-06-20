package com.bamilo.modernbamilo.userreview.pojo.getsurveylist

import com.google.gson.annotations.SerializedName
data class Product(
    @SerializedName("sku") val sku: String, //string
    @SerializedName("title") val title: String, //string
    @SerializedName("image") val image: String //string
)