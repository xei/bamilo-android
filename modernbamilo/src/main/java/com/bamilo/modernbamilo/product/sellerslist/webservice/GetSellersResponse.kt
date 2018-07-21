package com.bamilo.modernbamilo.product.sellerslist.webservice

import com.google.gson.annotations.SerializedName

data class GetSellersResponse (
        @SerializedName("items") val sellers: ArrayList<SellerProduct>
)

data class SellerProduct (
        @SerializedName("seller") val sellerInfo: SellerInfo,
        @SerializedName("product_offer") val productInfo: ProductInfo

)

data class SellerInfo (
        @SerializedName("name") val title: String,
        @SerializedName("score") val rating: SellerRating,
        @SerializedName("delivery_time") val deliveryTime: DeliveryTime
)

data class SellerRating (
        @SerializedName("overall") val rating: Float,
        @SerializedName("isEnable") val isValid: Boolean
)

data class DeliveryTime (
        @SerializedName("time_stamp") val epoch: Long,
        @SerializedName("message") val str: String
)

data class ProductInfo (
        @SerializedName("simple_sku") val sku: String,
        @SerializedName("price") val amount: Amount
)

data class Amount (
        @SerializedName("old_price") val baseAmount: Long,
        @SerializedName("price") val payableAmount: Long,
        @SerializedName("discount_percentage") val discountPercentage: Float,
        @SerializedName("currency") val currencySuffix: String
)