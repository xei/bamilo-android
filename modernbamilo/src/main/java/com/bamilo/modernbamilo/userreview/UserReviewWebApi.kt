package com.bamilo.modernbamilo.userreview

import com.bamilo.modernbamilo.userreview.pojo.getsurvey.GetSurveyResponse
import com.bamilo.modernbamilo.userreview.pojo.getsurveylist.GetSurveyListResponse
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserReviewWebApi {

    /**
     * Get a list of surveys which is available for a specific user.
     */
    @GET("survey/user/{userId}?device=mobile_app")
    fun getSurveysList(@Path("userId") userId: String) : Call<ResponseWrapper<GetSurveyListResponse>>

    /**
     * Gets a single survey by its alias (alias=journey_survey) after the purchase.
     */
    @GET("survey/alias/journey_survey?device=mobile_app")
    fun getSurvey(): Call<ResponseWrapper<GetSurveyResponse>>

    /**
     * Completely save a survey response.
     */
    @POST("survey/alias/{surveyAlias}")
    fun submitSurvey(@Path("surveyAlias") surveyAlias: Int): Call<ResponseWrapper<SubmitSurveyResponse>>

}