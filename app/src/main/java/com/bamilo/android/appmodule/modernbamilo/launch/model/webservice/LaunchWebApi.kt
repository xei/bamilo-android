/*
 * Copyright 2018 Bamilo, Inc.
 *
 * This file contains the contracts of sellers list web API.
 */

package com.bamilo.android.appmodule.modernbamilo.launch.model.webservice

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LaunchWebApi {

    /**
     * Get application startup configs.
     */
    @POST("configurations/")
    @FormUrlEncoded
    fun getStartupConfigs(@Field("platform") platform: String,
                          @Field("versionCode") versionCode: Int
    ) : Call<ResponseWrapper<GetStartupConfigsResponse>>

}