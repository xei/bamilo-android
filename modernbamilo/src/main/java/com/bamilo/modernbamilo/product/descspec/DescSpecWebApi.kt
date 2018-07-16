package com.bamilo.modernbamilo.product.descspec

import com.bamilo.modernbamilo.product.descspec.desc.pojo.DescriptionRow
import com.bamilo.modernbamilo.product.descspec.spec.pojo.SpecificationRow
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DescSpecWebApi {

    /**
     * Get a list of description rows.
     */
    @GET("product/description/{productId}?device=mobile_app")
    fun getDescription(@Path("productId") userId: String) : Call<ResponseWrapper<ArrayList<DescriptionRow>>>

    /**
     * Get a list of description rows.
     */
    @GET("product/specification/{productId}?device=mobile_app")
    fun getSpecification(@Path("productId") userId: String) : Call<ResponseWrapper<ArrayList<SpecificationRow>>>

}