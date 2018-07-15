package com.mobile.view.productdetail.network.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Farshid
 * since 7/11/2018.
 * contact farshidabazari@gmail.com
 */
class DeliveryTimeData {
    var sku: String = ""
    var delivery_time: Long = 0
    var delivery_message: String = ""
    var shipmentType: String = ""
    @SerializedName("delivery_zone_one") var tehranDeliveryTime: String = ""
    @SerializedName("delivery_zone_two") var otherCitiesDeliveryTime: String = ""
}