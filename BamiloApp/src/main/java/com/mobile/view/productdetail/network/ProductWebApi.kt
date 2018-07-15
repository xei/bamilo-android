package com.mobile.view.productdetail.network

import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.mobile.view.productdetail.network.response.ProductDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Farshid
 * since 7/14/2018.
 * contact farshidabazari@gmail.com
 */
interface ProductWebApi {
    @GET("catalog/product/sku/{sku}")
    fun getProductDetail(@Path("sku") sku: String): Call<ResponseWrapper<ProductDetailResponse>>
}