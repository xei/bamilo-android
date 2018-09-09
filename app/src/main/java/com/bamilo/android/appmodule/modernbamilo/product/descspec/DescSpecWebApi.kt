package com.bamilo.android.appmodule.modernbamilo.product.descspec

import com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo.GetDescriptionResponse
import com.bamilo.android.appmodule.modernbamilo.product.descspec.spec.pojo.GetSpecificationResponse
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DescSpecWebApi {

    /**
     * Get a list of description rows.
     */
    @GET("catalog/description/sku/{productId}")
    fun getDescription(@Path("productId") userId: String): Call<ResponseWrapper<GetDescriptionResponse>>

    /**
     * Get a list of description rows.
     */
    @GET("catalog/specification/sku/{productId}")
    fun getSpecification(@Path("productId") userId: String): Call<ResponseWrapper<GetSpecificationResponse>>

}