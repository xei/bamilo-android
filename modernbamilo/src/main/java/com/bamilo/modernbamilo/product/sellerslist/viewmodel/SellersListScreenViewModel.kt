package com.bamilo.modernbamilo.product.sellerslist.viewmodel

class SellersListScreenViewModel(
        val productTitle: String,
        val productThumbnailUrl: String,
        val sellersViewModel: ArrayList<SellersListItemViewModel> = ArrayList()
)

class SellersListItemViewModel (
    val sellerId: String,
    val title: String,
    val deliveryTime: String,
    val rate: Float,
    val isRateValid: Boolean = true,
    val baseAmount: Long,
    val payableAmount: Long,
    val discount: Float,
    val currency: String
)