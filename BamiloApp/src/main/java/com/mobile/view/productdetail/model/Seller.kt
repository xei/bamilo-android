package com.mobile.view.productdetail.model

import com.google.gson.annotations.SerializedName

class Seller {
    val delivery_time: String? = null
    var isNew: Boolean = false
    @SerializedName("precenceDuration")
    var presenceDuration = PresenceDuration()
    var score = Score()

    var name: String? = null
    val warranty: String? = null
    val id: Long = 0
    val target: String? = null

    val minDeliveryTime: Int = 0
    val maxDeliveryTime: Int = 0
}