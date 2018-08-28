package com.bamilo.android.appmodule.bamiloapp.view.productdetail.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProductDetail : Serializable {
    @SerializedName("is_wishlist")
    var isWishList: Boolean = false

    var title: String = ""
    var summery: String? = null
    var share_url: String? = null
    var image: String? = null
    var brand: String? = null
    var sku: String = ""
    var simple_sku: String? = null

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