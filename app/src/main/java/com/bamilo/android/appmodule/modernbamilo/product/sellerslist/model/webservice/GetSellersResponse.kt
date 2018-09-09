/*
 * Copyright 2018 Bamilo, Inc.
 *
 * This file contains the declaration of data transfer objects while interacting by sellers list web API.
 */

package com.bamilo.android.appmodule.modernbamilo.product.sellerslist.model.webservice

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

data class GetSellersResponse (
        @SerializedName("items") val sellers: ArrayList<SellerProduct>
): BaseModel()

data class SellerProduct (
        @SerializedName("seller") val sellerInfo: SellerInfo,
        @SerializedName("product_offer") val productInfo: ProductInfo
): BaseModel()

data class SellerInfo (
        @SerializedName("name") val title: String,
        @SerializedName("score") val rating: SellerRating,
        @SerializedName("delivery_time") val deliveryTime: DeliveryTime
): BaseModel()

data class SellerRating (
        @SerializedName("overall") val rating: Float,
        @SerializedName("isEnable") val isValid: Boolean
): BaseModel()

data class DeliveryTime (
        @SerializedName("time_stamp") val epoch: Long,
        @SerializedName("message") val str: String
): BaseModel()

data class ProductInfo (
        @SerializedName("simple_sku") val sku: String,
        @SerializedName("price") val amount: Amount
): BaseModel()

data class Amount (
        @SerializedName("old_price") val baseAmount: Long,
        @SerializedName("price") val payableAmount: Long,
        @SerializedName("discount_percentage") val discountPercentage: Int,
        @SerializedName("currency") val currencySuffix: String
): BaseModel()