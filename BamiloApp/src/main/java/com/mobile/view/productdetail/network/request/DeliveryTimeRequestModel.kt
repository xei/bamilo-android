package com.mobile.view.productdetail.network.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Farshid
 * since 7/11/2018.
 * contact farshidabazari@gmail.com
 */
data class DeliveryTimeRequestModel(
        @SerializedName("city-id")
        var cityId: Int = 0,
        @SerializedName("skus[]")
        var sku: String = "")