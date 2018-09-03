package com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Farshid
 * since 7/21/2018.
 * contact farshidabazari@gmail.com
 */

data class SimpleProduct(var price: Price = Price(),
                         var rating: Rating = Rating(),
                         var title: String? = null,
                         var sku: String? = null,
                         var simple_Sku: String? = null,
                         var image: String? = null,
                         var brand: String? = null,
                         var has_stock: Boolean = true,
                         @SerializedName("is_wishlist")
                         var isWishList: Boolean = false,
                         var isSelected: Boolean = false) : Serializable