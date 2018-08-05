package com.mobile.view.productdetail.model

import java.io.Serializable

/**
 * Created by Farshid
 * since 6/20/2018.
 * contact farshidabazari@gmail.com
 */
class PrimaryInfoModel : Serializable {
    var priceModel = Price()
    var rating = Rating()
    var title:String? = null
    var brand:String? = null
    var hasStock = true
}