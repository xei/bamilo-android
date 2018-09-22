/*
 * Copyright 2018 Bamilo, Inc.
 *
 * This file contains the contracts of sellers list web API.
 */

package com.bamilo.android.appmodule.modernbamilo.launch.model.webservice

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.*

interface LaunchWebApi {

    /**
     * Get application startup configs.
     */
    @POST("configurations/")
    @FormUrlEncoded
    fun getStartupConfigs(@Field(value="platform", encoded = false) platform: String,
               @Field(value="versionCode", encoded = false) versionCode: Int
    ) : Call<ResponseWrapper<GetStartupConfigsResponse>>

}