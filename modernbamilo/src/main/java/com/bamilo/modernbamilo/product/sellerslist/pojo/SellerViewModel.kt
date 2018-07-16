package com.bamilo.modernbamilo.product.sellerslist.pojo

data class SellerViewModel(val sellerId: String, val title: String, val deliveryTime: Long, val rate: Float, val baseAmount: Long, val payableAmount: Long, val discount: Int)