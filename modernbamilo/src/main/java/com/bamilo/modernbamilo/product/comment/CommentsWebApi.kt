package com.bamilo.modernbamilo.product.comment

import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentsWebApi {

    /**
     * Get comments page.
     */
    @POST("product/comment/{productId}/{page}?device=mobile_app")
    fun getComment(@Path("productId") productId: String, @Path("page") page: Int) : Call<ResponseWrapper<ArrayList<CommentViewModel>>>

}