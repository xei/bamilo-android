package com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurveylist

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

data class GetSurveyListResponse(
    @SerializedName("data") val data: Data
): BaseModel()