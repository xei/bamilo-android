package com.bamilo.modernbamilo.product.sellerslist

import com.bamilo.modernbamilo.product.sellerslist.pojo.SellerViewModel
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SellersListWebApi {

    /**
     * Get a list of sellers which is available for a specific product.
     */
    @GET("seller/{productId}?device=mobile_app")
    fun getSellers(@Path("productId") userId: String) : Call<ResponseWrapper<ArrayList<SellerViewModel>>>

}