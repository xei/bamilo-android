package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurveylist

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName
data class Page(
    @SerializedName("id") val id: Int, //0
    @SerializedName("title") val title: String, //string
    @SerializedName("questions") val questions: List<Question>
): BaseModel()