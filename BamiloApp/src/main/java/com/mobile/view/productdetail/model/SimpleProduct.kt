package com.mobile.view.productdetail.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Farshid
 * since 7/21/2018.
 * contact farshidabazari@gmail.com
 */
class SimpleProduct {
    var price = Price()
    var rating = Rating()
    var title: String = ""
    var sku: String = ""
    var simple_Sku: String = ""
    var image: String = ""
    var brand: String = ""

    var has_stock: Boolean = true
    @SerializedName("is_wishlist")
    var isWishList: Boolean = false
    var isSelected: Boolean = false
}