package com.mobile.view.productdetail.model

class Seller {
    var score = Score()
    val reviews = Reviews()
    var presenceDuration = PresenceDuration()

    val isGlobal: Boolean = false
    var isNew: Boolean = false

    var name: String = ""
    val warranty: String = ""
    val deliveryTime: String = ""
    val target: String = ""

    val id: Int = 0
    val minDeliveryTime: Int = 0
    val maxDeliveryTime: Int = 0
}