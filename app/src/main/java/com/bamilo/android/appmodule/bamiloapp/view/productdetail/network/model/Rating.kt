package com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model

import java.io.Serializable

class Rating : Serializable {
    var average: Float = 0.0f
    var max: Int = 0
    var total: Int = 0
    var stars = ArrayList<Star>()
}