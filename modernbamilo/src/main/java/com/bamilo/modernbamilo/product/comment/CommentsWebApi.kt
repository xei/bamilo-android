package com.bamilo.modernbamilo.product.comment

import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentsWebApi {

    /**
     * Get comments page.
     */
    @GET("catalog/reviews/sku/{productId}/page/{page}")
    fun getComment(@Path("productId") productId: String, @Path("page") page: Int) : Call<ResponseWrapper<GetCommentsResponse>>

}