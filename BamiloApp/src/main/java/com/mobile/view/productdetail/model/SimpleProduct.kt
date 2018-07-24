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
    var title: String? = null
    var sku: String? = null
    var simple_Sku: String? = null
    var image: String? = null
    var brand: String? = null

    var has_stock: Boolean = true
    @SerializedName("is_wishlist")
    var isWishList: Boolean = false
    var isSelected: Boolean = false
}