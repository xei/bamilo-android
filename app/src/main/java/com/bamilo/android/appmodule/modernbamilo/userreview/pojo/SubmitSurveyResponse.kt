package com.bamilo.android.appmodule.modernbamilo.userreview.pojo

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

data class SubmitSurveyResponse(@SerializedName("success") val succeed: Boolean): BaseModel()