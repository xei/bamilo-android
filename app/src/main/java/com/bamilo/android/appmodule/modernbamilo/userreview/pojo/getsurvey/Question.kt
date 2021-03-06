package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName
data class Question(
    @SerializedName("id") val id: Int, //0
    @SerializedName("title") val title: String, //string
    @SerializedName("type") val type: String, //string
    @SerializedName("required") val required: Boolean, //true
    @SerializedName("hidden") val hidden: Boolean, //true
    @SerializedName("options") val options: List<Option>,
    @SerializedName("showRules") val showRules: List<ShowRule>?,
    var userInputText: String
): BaseModel()