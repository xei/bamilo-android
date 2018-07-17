package com.mobile.view.productdetail.network

import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.mobile.view.productdetail.network.response.DeliveryTimeResponse
import com.mobile.view.productdetail.network.response.GetCityListResponse
import com.mobile.view.productdetail.network.response.GetRegionsListResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Farshid
 * since 7/10/2018.
 * contact farshidabazari@gmail.com
 */
interface RegionWebApi {
    @GET("customer/getaddressregions/")
    fun getRegionsList(): Call<ResponseWrapper<GetRegionsListResponse>>

    @GET("customer/getaddresscities/region/{regionId}")
    fun getCitiesList(@Path("regionId") region: Int): Call<ResponseWrapper<GetCityListResponse>>

    @FormUrlEncoded
    @POST("catalog/deliverytime/")
    fun getDeliveryTime(@Field("city-id") cityId: Int,
                        @Field("skus[]") simpleSku: String): Call<ResponseWrapper<DeliveryTimeResponse>>
}