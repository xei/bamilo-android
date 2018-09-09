package com.bamilo.android.appmodule.bamiloapp.view.productdetail.network

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.ProductDetail
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Farshid
 * since 7/14/2018.
 * contact farshidabazari@gmail.com
 */
interface ProductWebApi {
    @GET("catalog/product/sku/{sku}")
    fun getProductDetail(@Path("sku") sku: String): Call<ResponseWrapper<ProductDetail>>

    @FormUrlEncoded
    @POST("wishlist/addproduct")
    fun addToWishList(@Field("sku") sku: String): Call<ResponseWrapper<Any>>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "wishlist/removeproduct", hasBody = true)
    fun removeFromWishList(@Field("sku") sku: String): Call<ResponseWrapper<Any>>
}