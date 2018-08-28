package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey

import com.google.gson.annotations.SerializedName
data class Page(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("hidden") val hidden: Boolean,
    @SerializedName("questions") val questions: List<Question>
)