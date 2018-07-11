package com.bamilo.modernbamilo.product.comment.submit

import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface SubmitRateWebApi {

    /**
     * Submit a review for a product.
     */
    @POST("product/rating/{productId}/{rating}/{title}/{content}?device=mobile_app")
    fun submit(@Path("productId") userId: String,
               @Path("rating") rating:Float,
               @Path("title") title: String,
               @Path("content") content: String
    ) : Call<ResponseWrapper<Boolean>>

}