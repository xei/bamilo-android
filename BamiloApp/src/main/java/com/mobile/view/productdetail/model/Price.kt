package com.mobile.view.productdetail.model

import com.google.gson.annotations.SerializedName

class Price {
    var price: String = ""
    @SerializedName("old_price") var oldPrice: String = ""
    var currency: String = ""
    var discount_benefit: String = ""
    var discount_percentage: String = ""
}