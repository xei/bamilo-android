package com.mobile.view.productdetail.model

import java.io.Serializable

class Reviews : Serializable {
    var total: Int = 0
    var items = arrayListOf<Review>()
    var average: Float = 0f
}