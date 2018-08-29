package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName
data class Option(
    @SerializedName("id") val id: Int, //0
    @SerializedName("title") val title: String, //string
    @SerializedName("color") val color: String, //string
    @SerializedName("value") val value: String, //string
    @SerializedName("image") val image: String, //string
    @SerializedName("other") val other: Boolean, //true
    var isSelected: Boolean
): BaseModel()