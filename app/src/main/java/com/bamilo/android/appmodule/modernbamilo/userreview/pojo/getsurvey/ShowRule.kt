package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName
data class ShowRule(
    @SerializedName("questionId") val questionId: Int, //0
    @SerializedName("answerIds") val answerIds: List<String>
): BaseModel()