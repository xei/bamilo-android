package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurveylist

import com.google.gson.annotations.SerializedName
data class Data(
    @SerializedName("userId") val userId: Int, //0
    @SerializedName("orderNumber") val orderNumber: Int, //0
    @SerializedName("surveys") val surveys: List<com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.Survey>
)