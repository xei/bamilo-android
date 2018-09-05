package com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model

import java.io.Serializable

class Reviews : Serializable {
    var total: Int = 0
    var items = arrayListOf<Review>()
    var average: Float = 0f
}