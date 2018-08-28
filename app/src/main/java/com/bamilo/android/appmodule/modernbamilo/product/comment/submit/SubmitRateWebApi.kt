package com.bamilo.android.appmodule.modernbamilo.product.comment.submit

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.*

interface SubmitRateWebApi {

    /**
     * Submit a review for a product.
     */
    @POST("rating/addproductreview")
    @FormUrlEncoded
    fun submit(@Field(value="sku", encoded = false) productId: String,
               @Field(value="rate", encoded = false) rating:Int,
               @Field(value="title", encoded = false) title: String,
               @Field(value="comment", encoded = false) content: String
    ) : Call<ResponseWrapper<Boolean>>

}