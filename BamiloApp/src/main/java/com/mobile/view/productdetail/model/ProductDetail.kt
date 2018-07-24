package com.mobile.view.productdetail.model

import com.google.gson.annotations.SerializedName

class ProductDetail {
    @SerializedName("is_wishlist")
    var isWishList: Boolean = false

    var title: String = ""
    var summery: String = ""
    var share_url: String = ""
    var image: String = ""
    var brand: String = ""
    var sku: String = ""
    var simple_sku: String = ""

    var other_seller_count: Int = 0
    var bundle = ProductBundle()
    var has_stock = true

    var image_list = ArrayList<Image>()
    var price = Price()
    var variations = ArrayList<Variation>()
    var return_policy = ReturnPolicy()
    var seller = Seller()
    var rating = Rating()
    var reviews = Reviews()
    var breadcrumbs = ArrayList<Breadcrumbs>()
}