package com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Price : Serializable {
    var price: String = ""
    @SerializedName("old_price")
    var oldPrice: String? = null
    var currency: String = ""
    var discount_benefit: String? = null
    var discount_percentage: String? = null
}