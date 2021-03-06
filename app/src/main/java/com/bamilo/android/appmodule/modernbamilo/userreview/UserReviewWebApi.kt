package com.bamilo.android.appmodule.modernbamilo.userreview

import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.SubmitSurveyResponse
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.GetSurveyResponse
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurveylist.GetSurveyListResponse
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper

import retrofit2.Call
import retrofit2.http.*

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
     * Partially save a survey response.
     */
    @POST("survey/alias/journey_survey?__method=PATCH")
    fun submitSurveyPage(): Call<ResponseWrapper<SubmitSurveyResponse>>

    /**
     * Completely save a survey response.
     */
    @POST("survey/alias/journey_survey")
    @FormUrlEncoded
    fun submitSurvey(@Field (value="device", encoded = false) device: String,
                     @Field (value="orderNumber", encoded = false) orderNumber: String?,
                     @Field (value="userId", encoded = false) userId: String?,
                     @FieldMap responses: Map<String, String>
    ): Call<ResponseWrapper<SubmitSurveyResponse>>

    /**
     * Ignore the survey
     */
    @POST("survey/user/{userId}")
    @FormUrlEncoded
    fun cancelSurvey(@Path("userId") userId: String,
                     @Field (value="device", encoded = false) device: String = "mobile_app",
                     @Field (value="status", encoded = false) status: String? = "ignore",
                     @Field (value="__method", encoded = false) method: String? = "PATCH"
    ): Call<ResponseWrapper<Any>>

}