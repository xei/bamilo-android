/*
 * Copyright 2018 Bamilo, Inc.
 *
 * This file contains the contracts of sellers list web API.
 */

package com.bamilo.android.appmodule.modernbamilo.launch.model.webservice

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface LaunchWebApi {

    /**
     * Get application startup configs.
     */
    @POST("configurations")
    fun getStartupConfigs(@Query("platform") platform: String,
                          @Query("versionCode") versionCode: Int
    ) : Call<ResponseWrapper<GetStartupConfigsResponse>>

}