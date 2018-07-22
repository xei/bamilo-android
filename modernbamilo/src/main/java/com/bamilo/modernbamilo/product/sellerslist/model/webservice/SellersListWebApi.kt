/*
 * Copyright 2018 Bamilo, Inc.
 *
 * This file contains the contracts of sellers list web API.
 */

package com.bamilo.modernbamilo.product.sellerslist.model.webservice

import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SellersListWebApi {

    /**
     * Get a list of sellers which are available for an arbitrary product.
     */
    @GET("catalog/sellers/sku/{productId}")
    fun getSellers(@Path("productId") userId: String) : Call<ResponseWrapper<GetSellersResponse>>

}