package com.bamilo.modernbamilo.product.rate

import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface SubmitRateWebApi {

    /**
     * Submit a review for a product.
     */
    @POST("product/rating/{productId}?device=mobile_app")
    fun getSellers(@Path("productId") userId: String) : Call<ResponseWrapper<Boolean>>

}