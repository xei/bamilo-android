package com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.response

import com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.City
import com.google.gson.annotations.SerializedName

/**
 * Created by Farshid
 * since 7/10/2018.
 * contact farshidabazari@gmail.com
 */
data class GetCityListResponse(
        @SerializedName("data") val data: ArrayList<City>
)