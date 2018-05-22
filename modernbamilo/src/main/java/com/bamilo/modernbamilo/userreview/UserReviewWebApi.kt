package com.bamilo.modernbamilo.userreview

import com.bamilo.modernbamilo.userreview.pojo.getsurvey.getSurveyResponse
import com.bamilo.modernbamilo.userreview.pojo.getsurveylist.getSurveyListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserReviewWebApi {

    /**
     * Get a list of surveys which is available for a specific user.
     */
    @GET("survey/user/{userId}")
    fun getSurveysList(@Path("userId") userId: String) : Call<getSurveyListResponse>

    /**
     * Gets a single survey by its alias.
     */
    @GET("survey/alias/{surveyAlias}")
    fun getSurvey(@Path("surveyAlias") alias: String): Call<getSurveyResponse>

}