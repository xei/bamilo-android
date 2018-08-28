package com.bamilo.android.appmodule.bamiloapp.view.productdetail.model

import com.bamilo.android.appmodule.bamiloapp.view.productdetail.model.Star
import java.io.Serializable

class Rating : Serializable {
    var average: Float = 0.0f
    var max: Int = 0
    var total: Int = 0
    var stars = ArrayList<Star>()
}