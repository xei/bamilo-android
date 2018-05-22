package com.bamilo.modernbamilo.userreview.pojo.getsurvey

import com.google.gson.annotations.SerializedName
data class Page(
    @SerializedName("id") val id: Int, //0
    @SerializedName("title") val title: String, //string
    @SerializedName("questions") val questions: List<Question>
)