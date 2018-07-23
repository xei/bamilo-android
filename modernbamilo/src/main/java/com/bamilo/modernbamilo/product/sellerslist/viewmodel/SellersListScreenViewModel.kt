/*
 * Copyright 2018 Bamilo, Inc.
 *
 * This file contains the declaration of view models used in sellers list screen.
 */

package com.bamilo.modernbamilo.product.sellerslist.viewmodel

/**
 * This is the main view model of the sellers list screen and contains some fixed data and
 * a reference to a list of item view models.
 */
class SellersListScreenViewModel(
        val productId: String,
        val productTitle: String,
        val productThumbnailUrl: String,
        val sellersViewModel: ArrayList<SellersListItemViewModel> = ArrayList()
)

/**
 * This view model contains the presentation data of any row in sellers list screen.
 */
class SellersListItemViewModel (
    val sellerId: String,
    val title: String,
    val deliveryTime: String,
    val rate: Float,
    val isRateValid: Boolean = true,
    val baseAmount: Long,
    val payableAmount: Long,
    val discount: Int,
    val currency: String
)