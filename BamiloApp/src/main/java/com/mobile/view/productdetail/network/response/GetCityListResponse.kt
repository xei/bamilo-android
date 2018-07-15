package com.mobile.view.productdetail.network.response

import com.google.gson.annotations.SerializedName
import com.mobile.view.productdetail.network.model.City

/**
 * Created by Farshid
 * since 7/10/2018.
 * contact farshidabazari@gmail.com
 */
data class GetCityListResponse(
        @SerializedName("data") val data: ArrayList<City>
)