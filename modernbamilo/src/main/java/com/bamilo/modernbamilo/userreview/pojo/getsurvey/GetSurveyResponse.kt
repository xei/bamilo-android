package com.bamilo.modernbamilo.userreview.pojo.getsurvey

import com.google.gson.annotations.SerializedName

data class GetSurveyResponse(
        @SerializedName("survey") val survey: Survey
)